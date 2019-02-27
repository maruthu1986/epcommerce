/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.sync;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import de.pdark.decentxml.Document;
import de.pdark.decentxml.XMLIOSource;
import de.pdark.decentxml.XMLParser;

import org.apache.commons.lang3.SystemUtils;

/**
 * Sync step definitions.
 */
public class SyncStepDefinitions {

	private static final File MAVEN_BUILD_DIRECTORY = new File("target");
	private static final File MAVEN_OUTPUT_DIRECTORY = new File(MAVEN_BUILD_DIRECTORY, "classes");
	private static final File SOURCE_SYSTEM = file(MAVEN_BUILD_DIRECTORY, "source-system");
	private static final File TARGET_SYSTEM = file(MAVEN_BUILD_DIRECTORY, "target-system");
	private static final File IMPORT_EXPORT_CLI_DIRECTORY = filenameStartsWith(MAVEN_BUILD_DIRECTORY, "ext-importexport-cli-");
	private static final File SYNC_TOOL_CLI_DIRECTORY = filenameStartsWith(MAVEN_BUILD_DIRECTORY, "ext-sync-cli-");

	/**
	 * Creates the source database.
	 */
	@Given("^a source system$")
	public void createSourceDatabase() {
		// database created in maven with maven-dependency-plugin, id unpack-source-database
		File sourceDatabase = file(SOURCE_SYSTEM, "SMOKETESTDB.h2.db");
		assert sourceDatabase.exists();
	}

	/**
	 * Creates the target database.
	 */
	@And("^a target system$")
	public void createTargetDatabase() {
		// database created in maven with maven-dependency-plugin, id unpack-target-database
		File targetDatabase = file(TARGET_SYSTEM, "SMOKETESTDB.h2.db");
		assert targetDatabase.exists();
	}

	/**
	 * Creates change set and import changes into it.
	 * @throws IOException when import fails
	 * @throws InterruptedException when import fails
	 */
	@And("^a change set on the source system$")
	public void createChangeSetAndImportChanges() throws IOException, InterruptedException {
		// creation of change set implemented with liquibase-maven-plugin, id import-create-change-set

		File importConfiguration = new File(MAVEN_OUTPUT_DIRECTORY, "source-import-configuration");
		Process importexport = new ProcessBuilder(
				executable(IMPORT_EXPORT_CLI_DIRECTORY, "importexport"),
				"-i",
				"-p",
				file(importConfiguration, "import.config").toString(),
				"-c",
				file(importConfiguration, "import-configuration.xml").toString(),
				"-g",
				"01234567-890A-BCDE-F012-34567890ABCD")
				.directory(filenameStartsWith(MAVEN_BUILD_DIRECTORY, "ext-importexport-cli-"))
				.inheritIO()
				.start();
		int exitCode = importexport.waitFor();
		assertEquals(0, exitCode);
	}

	/**
	 * Executes the sync tool and transfer change set from source to target systems.
	 * @throws IOException when sync tool fails
	 * @throws InterruptedException when sync tool fails
	 */
	@When("^the sync tool transfers the change set$")
	public void executeSyncTool() throws IOException, InterruptedException {
		File syncConfiguration = new File(MAVEN_OUTPUT_DIRECTORY, "synctool-configuration");
		Process synctool = new ProcessBuilder(
				executable(SYNC_TOOL_CLI_DIRECTORY, "synctool"),
				"-f",
				"-p",
				"01234567-890A-BCDE-F012-34567890ABCD",
				"-s",
				file(syncConfiguration, "sourceconfig.xml").toString(),
				"-t",
				file(syncConfiguration, "targetconfig.xml").toString())
				.directory(SYNC_TOOL_CLI_DIRECTORY)
				.inheritIO()
				.start();
		int exitCode = synctool.waitFor();
		assertEquals(0, exitCode);
	}

	/**
	 * Exports the changes from target system and verifies the changes in the change set made it over.
	 * @throws IOException when export or reading of export xml fails
	 * @throws InterruptedException when export fails
	 */
	@Then("^the changes are found on the target system$")
	public void exportChangesAndVerify() throws IOException, InterruptedException {
		File exportConfiguration = new File(MAVEN_OUTPUT_DIRECTORY, "target-export-configuration");
		Process importexport = new ProcessBuilder(
				executable(IMPORT_EXPORT_CLI_DIRECTORY, "importexport"),
				"-e",
				file(IMPORT_EXPORT_CLI_DIRECTORY, "searchconfiguration.xml").toString(),
				"-p",
				file(exportConfiguration, "export.config").toString(),
				"-c",
				file(exportConfiguration, "export-configuration.xml").toString())
				.directory(IMPORT_EXPORT_CLI_DIRECTORY)
				.inheritIO()
				.start();
		int exitCode = importexport.waitFor();
		assertEquals(0, exitCode);

		XMLParser parser = new XMLParser();
		File priceListsFile = file(TARGET_SYSTEM, "exported", "price_lists.xml");
		Document priceLists = parser.parse(new XMLIOSource(priceListsFile));
		assertEquals("newpricelist", priceLists.getRootElement().getChild("price_list").getChild("guid").getText());
	}

	private static String executable(final File directory, final String name) {
		if (SystemUtils.IS_OS_WINDOWS) {
			return String.format("%s\\%s.bat", directory, name);
		}
		// otherwise assume sh like
		return String.format("./%s.sh", name);
	}

	private static File file(final File baseDirectory, final String... paths) {
		File result = new File(baseDirectory, paths[0]);
		for (int i = 1; i < paths.length; i++) {
			result = new File(result, paths[i]);
		}
		return result.getAbsoluteFile();
	}

	private static File filenameStartsWith(final File directory, final String filenamePrefix) {
		File[] filesStartWith = directory.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(final File dir, final String name) {
				return name.startsWith(filenamePrefix);
			}
		});
		assert filesStartWith.length == 1 : String.format("could not find a file that starts with %s in %s", filenamePrefix, directory);
		return filesStartWith[0].getAbsoluteFile();
	}
}
