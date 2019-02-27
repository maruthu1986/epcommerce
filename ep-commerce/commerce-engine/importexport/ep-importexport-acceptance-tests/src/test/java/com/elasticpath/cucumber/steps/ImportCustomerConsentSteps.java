/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.cucumber.steps;

import static org.assertj.core.api.Assertions.assertThat;

import static com.elasticpath.commons.util.TestDomainMarshaller.marshalObject;

import java.io.File;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import cucumber.api.DataTable;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.common.dto.datapolicy.CustomerConsentDTO;
import com.elasticpath.domain.builder.datapolicy.CustomerConsentBuilder;
import com.elasticpath.domain.datapolicy.CustomerConsent;
import com.elasticpath.importexport.builder.ImportConfigurationBuilder;
import com.elasticpath.importexport.builder.ImportExportTestDirectoryBuilder;
import com.elasticpath.importexport.common.dto.datapolicy.CustomerConsentsDTO;
import com.elasticpath.importexport.common.manifest.Manifest;
import com.elasticpath.importexport.common.summary.Summary;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.common.util.Message;
import com.elasticpath.importexport.importer.configuration.ImportConfiguration;
import com.elasticpath.importexport.importer.controller.ImportController;
import com.elasticpath.importexport.importer.types.ImportStrategyType;
import com.elasticpath.service.datapolicy.CustomerConsentService;
import com.elasticpath.service.datapolicy.DataPolicyService;

/**
 * Import customer consent steps.
 */
public class ImportCustomerConsentSteps {

	private static final String CUSTOMER_CONSENTS_IMPORT_FILE = "customer_consents.xml";

	private static final String MANIFEST_EXPORT_FILE = "manifest.xml";

	@Autowired
	private ImportController importController;

	@Autowired
	private CustomerConsentService customerConsentService;

	@Autowired
	private DataPolicyService dataPolicyService;

	@Autowired
	private CustomerConsentBuilder customerConsentBuilder;

	private CustomerConsentsDTO customerConsentDtoForImport;

	private File importDirectory;

	private Summary summary;

	private static int runNumber = 1;


	/**
	 * Clear import data.
	 */
	@Given("^the customer consent import data has been emptied out$")
	public void clearCustomerConsentImportData() {
		customerConsentDtoForImport = new CustomerConsentsDTO();
	}

	/**
	 * Setup the tests with customer consents.
	 *
	 * @param dataTable customer consent info.
	 * @throws ParseException in case of date parsing error.
	 */
	@Given("^the customer consents to import of$")
	public void setUpCustomerConsents(final DataTable dataTable) throws ParseException {
		final List<CustomerConsentDTO> customerConsents =
				CustomerConsentSteps.getCustomerConsentsFromDataTable(dataTable.asMaps(String.class, String.class));

		customerConsentDtoForImport.getCustomerConsents().addAll(customerConsents);
	}

	/**
	 * Import customer consent with importexport tool.
	 *
	 * @throws Exception the exception
	 */
	@When("^importing customer consents with the importexport tool$")
	public void executeImport() throws Exception {
		importDirectory =
				ImportExportTestDirectoryBuilder.newInstance()
						.withTestName(this.getClass().getSimpleName())
						.withRunNumber(runNumber++)
						.build();

		if (!importDirectory.exists()) {
			importDirectory.mkdirs();
		}

		final Manifest manifest = new Manifest();
		manifest.addResource(CUSTOMER_CONSENTS_IMPORT_FILE);

		marshalObject(Manifest.class, manifest, new File(importDirectory, MANIFEST_EXPORT_FILE));
		marshalObject(CustomerConsentsDTO.class, customerConsentDtoForImport,
				new File(importDirectory, CUSTOMER_CONSENTS_IMPORT_FILE));

		final ImportConfiguration importConfiguration =
				ImportConfigurationBuilder.newInstance()
						.setRetrievalSource(importDirectory.getPath())
						.addImporterConfiguration(JobType.CUSTOMER_CONSENT, ImportStrategyType.INSERT)
						.build();

		importController.loadConfiguration(importConfiguration);
		summary = importController.executeImport();
	}

	/**
	 * Verify the tests with customer consents.
	 *
	 * @param dataTable customer consent info.
	 * @throws ParseException in case of date parsing error.
	 */
	@Then("^the imported customer consents records should equal$")
	public void verifyCustomerConsents(final DataTable dataTable) throws ParseException {
		final List<Map<String, String>> customerConsentsMap = dataTable.asMaps(String.class, String.class);
		final List<CustomerConsent> expectedCustomerConsents = CustomerConsentSteps.parseCustomerConsentsFromDataTable(customerConsentsMap,
				dataPolicyService, customerConsentBuilder);

		List<CustomerConsent> importedCustomerConsents = customerConsentService.list();

		assertThat(importedCustomerConsents)
				.as(String.format("imported customer consents are incorrect"))
				.hasSize(expectedCustomerConsents.size());

		for (CustomerConsent expectedCustomerConsent : expectedCustomerConsents) {
			CustomerConsent importedCustomerConsent = importedCustomerConsents.get(importedCustomerConsents.indexOf(expectedCustomerConsent));

			assertThat(importedCustomerConsent)
					.isEqualToComparingOnlyGivenFields(expectedCustomerConsent, "guid", "action", "customerGuid", "dataPolicy.guid");
		}
	}

	/**
	 * Ensure an unsupported operation throws an exception.
	 *
	 * @param dataTable message info.
	 */
	@Then("^In the summary are the unsupported customer consent operation warning messages of$")
	public void ensureWarningMessagesAreInSummary(final DataTable dataTable) {
		List<Message> warnings = summary.getWarnings();

		assertThat(!warnings.isEmpty())
				.as("The import should have warnings.")
				.isTrue();

		final List<Map<String, String>> messagesMap = dataTable.asMaps(String.class, String.class);

		assertThat(warnings)
				.hasSize(messagesMap.size());

		for (Map<String, String> message : messagesMap) {
			final int index = messagesMap.indexOf(message);
			String[] details = message.get("details").split(",");

			assertThat(warnings.get(index).getCode())
					.isEqualTo(message.get("code"));
			assertThat(warnings.get(index).getParams())
					.isEqualTo(details);
		}
	}

}
