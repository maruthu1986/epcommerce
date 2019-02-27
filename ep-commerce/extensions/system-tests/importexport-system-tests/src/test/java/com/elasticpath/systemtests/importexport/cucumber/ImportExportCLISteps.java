package com.elasticpath.systemtests.importexport.cucumber;

import static java.nio.file.Files.exists;
import static java.nio.file.Files.isExecutable;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Cucumber step definitions IE CLI feature.
 */
public class ImportExportCLISteps {

	private static final int INTERRUPTED_EXCEPTION_CODE = 100;
	private String exportConfigurationPath;
	private String importConfigurationPath;
	private int status;

	//Gets this value from maven. Will not work from within IDE
	private final Path importExportWorkingDir = Paths.get(
			System.getProperty("importexport.cli.directory")
	);

	/**
	 * Provision customer export configuration file.
	 */
	@Given("^an export configuration$")
	public void provisionCustomerExportFile() {

		URL exportConfiguration = getClass().getClassLoader()
				.getResource("import-export-resources/exportconfiguration.xml");
		assert exportConfiguration != null : "Failed to load export configuration file";

		exportConfigurationPath = exportConfiguration.getFile();
	}

	/**
	 * Provision customer import configuration file.
	 */
	@Given("^an import configuration$")
	public void provisionCustomerImportFile() {

		URL importConfigurationFile = getClass().getClassLoader()
				.getResource("import-export-resources/importconfiguration.xml");
		assert importConfigurationFile != null : "Failed to load import configuration file";

		importConfigurationPath = importConfigurationFile.getFile();
	}

	/**
	 * Export customers from target system.
	 */
	@When("^I export the customers from the database using the IE CLI$")
	public void exportCustomersFromTargetSystem() {

		status = executeCLI(getScript().toString(), "-e", "searchconfiguration.xml", "-c", exportConfigurationPath);
	}

	/**
	 * import customers into target system.
	 */
	@When("^I import the customers into the database using the IE CLI$")
	public void importCustomersIntoTargetSystem() {

		status = executeCLI(getScript().toString(), "-i", "-c", importConfigurationPath);
	}

	/**
	 * Ensure the export runs successfully.
	 */
	@Then("^the CLI returns a successful response code$")
	public void ensureExportRunsSuccessfully() {

		assertThat("The CLI should return a successful response code", status,
				is(0));
	}

	private Path getScript() {

		return importExportWorkingDir.resolve("importexport." + getScriptExtension());
	}

	private int executeCLI(final String... commandLineArgs) {

		Path importExportCliScript = getScript();

		//Ensure our script is in a valid state
		if (!exists(importExportCliScript)) {
			throw new IllegalStateException(String.format("IE script does not exist %s", importExportCliScript));
		}

		if (!isExecutable(importExportCliScript)) {
			boolean isExecutable = importExportCliScript.toFile()
														.setExecutable(true);
			if (!isExecutable) {
				throw new IllegalStateException("Failed to set the IE script to be executable");
			}
		}

		//Load and run the script
		ProcessBuilder builder = new ProcessBuilder(commandLineArgs);
		try {
			builder.environment()
				   .put("IE_CLASSPATH", importExportWorkingDir.resolve("dependencies")
															  .toString() + File.separator + "*");

			return builder.inheritIO()
						  .directory(importExportWorkingDir.toFile())
						  .redirectErrorStream(true)
						  .start()
						  .waitFor();
		} catch (IOException e) {
			throw new IllegalStateException("An IO exception occurred: ", e);
		} catch (InterruptedException e) {
			Thread.currentThread()
				  .interrupt();
			return INTERRUPTED_EXCEPTION_CODE;
		}
	}

	private String getScriptExtension() {

		String extension;
		if (isWindows()) {
			extension = "bat";
		} else {
			extension = "sh";
		}
		return extension;
	}

	private boolean isWindows() {

		return getOperatingSystem()
				.startsWith("Windows");
	}

	private String getOperatingSystem() {

		return System.getProperty("os.name");
	}
}
