/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.uat.email.dataimport.stepdefs;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import static com.elasticpath.uat.email.stepdefs.EmailStepDefinitions.getContents;
import static com.elasticpath.uat.email.stepdefs.EmailStepDefinitions.getEmailMessageBySubject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.mail.Message;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.builder.CmUserBuilder;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.dataimport.ImportDataType;
import com.elasticpath.domain.dataimport.ImportJob;
import com.elasticpath.domain.dataimport.ImportJobRequest;
import com.elasticpath.domain.dataimport.ImportJobStatus;
import com.elasticpath.domain.dataimport.ImportType;
import com.elasticpath.domain.dataimport.impl.AbstractImportTypeImpl;
import com.elasticpath.domain.dataimport.impl.ImportDataTypeCustomerImpl;
import com.elasticpath.domain.dataimport.impl.ImportJobRequestImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.cmuser.CmUserService;
import com.elasticpath.service.dataimport.ImportService;
import com.elasticpath.test.integration.importjobs.ImportJobProcessorLauncher;
import com.elasticpath.test.persister.testscenarios.SimpleStoreScenario;
import com.elasticpath.test.util.Utils;
import com.elasticpath.uat.ScenarioContextValueHolder;

/**
 * Step Definitions for verifying Data Import emails.
 */
public class DataImportStepDefinitions {

	/** CSV file text strings qualifier. */
	public static final char CSV_FILE_TEXT_QUALIFIER = '"';

	/** CSV file column delimiter. */
	public static final char CSV_FILE_COL_DELIMETER = '|';

	@Autowired
	private ImportService importService;

	@Autowired
	private BeanFactory beanFactory;

	@Autowired
	private CmUserService cmUserService;

	@Autowired
	private String assetDirectory;

	@Autowired
	private ScenarioContextValueHolder<SimpleStoreScenario> storeScenarioHolder;

	@Autowired
	private ScenarioContextValueHolder<CmUserBuilder> cmUserBuilderHolder;

	@Autowired
	private ScenarioContextValueHolder<Map<String, Message>> emailMessagesHolder;

	@Autowired
	private ScenarioContextValueHolder<Runnable> emailSendingCommandHolder;

	private ImportJob importJob;

	private static final String OPTIONAL_EMAIL_SUBJECT = "(?: \"(.+)\")?";

	@Given("a file named \"([^\"]*)\" exists in the import directory$")
	public void importFileExists(final String fileName) throws IOException {

		final String filePath = assetDirectory + File.separator + "import" + File.separator + fileName;
		final File importFile = new File(filePath);

		assertTrue(
				"The "
						+ fileName
						+ " file cannot be found; data for this test has not been set up correctly.  Please make sure the test data file has been placed in the ${runtime.assets.dir}/import directory.",
				importFile.exists());

	}

	@Given("^an import job is created for \"(.+)\"$")
	public void createImportJob(final String filename) throws Throwable {

		importJob = createInsertCustomerImportJob(filename);

	}

	@When("^the import job has completed$")
	public void importJobCompleted() throws Throwable {
		// Defer execution until we are ready to check for the email
		emailSendingCommandHolder.set(() -> {
			try {
				executeImportJob(importJob);
			} catch (InterruptedException e) {
				fail("Import job failed: " + e.getMessage());
			}
		});
	}

	@Then("^the" + OPTIONAL_EMAIL_SUBJECT
			+ " email should list (\\d+) total rows, (\\d+) successfully imported rows, (\\d+) failed rows, (\\d+) left rows$")
	public void verifyImportRowCounts(final String emailSubject, final int totalRows, final int importedRows, final int failedRows,
			final int leftRows) throws Throwable {
		final Message message = getEmailMessageBySubject(emailSubject, emailMessagesHolder.get());

		final String emailContents = getContents(message);

		final String totalRowString = String.format("Total Rows: %d", totalRows);
		assertThat("The email contents should contain the correct " + totalRowString, emailContents, containsString(totalRowString));

		final String importedRowsString = String.format("Successfully Imported Rows: %d", importedRows);
		assertThat("The email contents should contain the correct " + importedRowsString, emailContents, containsString(importedRowsString));

		final String failedRowsString = String.format("Failed Imported Rows: %d", failedRows);
		assertThat("The email contents should contain the correct " + failedRowsString, emailContents, containsString(failedRowsString));

		final String leftRowsString = String.format("Failed Imported Rows: %d", leftRows);
		assertThat("The email contents should contain the correct " + leftRowsString, emailContents, containsString(leftRowsString));
	}

	protected ImportJob createInsertCustomerImportJob(final String csvFileName) {
		final List<ImportDataType> importDataTypes = importService.getCustomerImportDataTypes();
		final String importDataTypeName = findByType(importDataTypes, ImportDataTypeCustomerImpl.class).getName();
		final Map<String, Integer> mappings = new HashMap<>();

		mappings.put("guid", 1);
		mappings.put("userId", 2);
		mappings.put("CP_FIRST_NAME", 3);
		mappings.put("CP_LAST_NAME", 4);
		mappings.put("CP_EMAIL", 5);
		mappings.put("CP_ANONYMOUS_CUST", 6);
		mappings.put("CP_HTML_EMAIL", 7);
		mappings.put("status", 8);
		mappings.put("creationDate", 9);
		mappings.put("CP_PHONE", 10);

		final ImportJob importJob = createSimpleImportJob(storeScenarioHolder.get().getStore(), Utils.uniqueCode("Insert Customers"), csvFileName,
				AbstractImportTypeImpl.INSERT_TYPE, importDataTypeName, mappings);
		return importJob;
	}

	/**
	 * Helper to create an import jobs for testing purposes.
	 * 
	 * @param associatedDomainObject the associate domain object (store, catalog or warehouse)
	 * @param name the name of the import job
	 * @param csvFileName the name of the csv file
	 * @param importType the import type
	 * @param importDataTypeName the import data type name. Should be only the name of the import file without any parent folders specified.
	 * @param mappings the map of import mappings
	 * @return the persisted importJob
	 */
	protected ImportJob createSimpleImportJob(final Persistable associatedDomainObject, final String name, final String csvFileName,
			final ImportType importType, final String importDataTypeName, final Map<String, Integer> mappings) {
		final ImportJob importJob = beanFactory.getBean(ContextIdNames.IMPORT_JOB);
		if (associatedDomainObject instanceof Store) {
			importJob.setStore((Store) associatedDomainObject);
		} else if (associatedDomainObject instanceof Catalog) {
			importJob.setCatalog((Catalog) associatedDomainObject);
		} else if (associatedDomainObject instanceof Warehouse) {
			importJob.setWarehouse((Warehouse) associatedDomainObject);
		} else {
			throw new IllegalArgumentException("Store, Catalog or Warehouse object expected for associatedDomainObject.");
		}

		importJob.setImportDataTypeName(importDataTypeName);
		importJob.setImportType(importType);
		importJob.setName(name);
		importJob.setCsvFileName(csvFileName);
		importJob.setCsvFileColDelimeter(CSV_FILE_COL_DELIMETER);
		importJob.setCsvFileTextQualifier(CSV_FILE_TEXT_QUALIFIER);
		importJob.setMappings(mappings);

		return importService.saveOrUpdateImportJob(importJob);
	}

	private void executeImportJob(final ImportJob importJob) throws InterruptedException {

		final CmUser initiator = getCmUser();
		final ImportJobRequest importJobProcessRequest = new ImportJobRequestImpl();
		importJobProcessRequest.setImportJob(importJob);
		importJobProcessRequest.setImportSource(importJob.getCsvFileName());
		importJobProcessRequest.setInitiator(initiator);
		importJobProcessRequest.setReportingLocale(Locale.getDefault());

		final ImportJobStatus status = importService.scheduleImport(importJobProcessRequest);

		final ImportJobProcessorLauncher launcher = new ImportJobProcessorLauncher(beanFactory);
		launcher.launchAndWaitToFinish(status);

	}

	private CmUser getCmUser() {
		final String cmUserEmail = cmUserBuilderHolder.get().build().getEmail();
		return cmUserService.findByEmail(cmUserEmail);
	}

	private ImportDataType findByType(final List<ImportDataType> importDataTypes, final Class<? extends ImportDataType> type) {

		for (final ImportDataType importDataType : importDataTypes) {
			if (type.isInstance(importDataType)) {
				return importDataType;
			}
		}
		return null;
	}
}
