/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.cucumber.steps;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import cucumber.api.DataTable;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.common.dto.datapolicy.CustomerConsentDTO;
import com.elasticpath.importexport.builder.ExportConfigurationBuilder;
import com.elasticpath.importexport.builder.ImportExportTestDirectoryBuilder;
import com.elasticpath.importexport.common.dto.datapolicy.CustomerConsentsDTO;
import com.elasticpath.importexport.common.manifest.Manifest;
import com.elasticpath.importexport.common.marshalling.XMLUnmarshaller;
import com.elasticpath.importexport.common.types.RequiredJobType;
import com.elasticpath.importexport.exporter.configuration.ExportConfiguration;
import com.elasticpath.importexport.exporter.configuration.search.SearchConfiguration;
import com.elasticpath.importexport.exporter.controller.ExportController;

/**
 * Export customer consents.
 */
public class ExportCustomerConsentSteps {

	private static final String CUSTOMER_CONSENT_EXPORT_FILE = "customer_consents.xml";

	private static final String MANIFEST_EXPORT_FILE = "manifest.xml";

	@Autowired
	private ExportController exportController;

	private CustomerConsentsDTO customerConsentsDTO;

	private File exportDirectory;

	private static int runNumber = 1;

	/**
	 * Export customer consents with importexport tool.
	 *
	 * @throws Exception the exception
	 */
	@When("^exporting customer consents with the importexport tool")
	public void runExport() throws Exception {
		exportDirectory =
				ImportExportTestDirectoryBuilder.newInstance()
						.withTestName(this.getClass().getSimpleName())
						.withRunNumber(runNumber++)
						.build();

		final ExportConfiguration exportConfiguration =
				ExportConfigurationBuilder.newInstance()
						.setDeliveryTarget(exportDirectory.getPath())
						.setExporterTypes(Arrays.asList(RequiredJobType.CUSTOMER_CONSENT))
						.build();

		final SearchConfiguration searchConfiguration = new SearchConfiguration();

		exportController.loadConfiguration(exportConfiguration, searchConfiguration);
		exportController.executeExport();
	}

	/**
	 * Parses the exported customer consents data.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@When("^the exported customer consents data is parsed$")
	public void parseExportedCustomerConsentData() throws IOException {
		final File exportedCustomerConsentsFile = new File(exportDirectory, CUSTOMER_CONSENT_EXPORT_FILE);

		assertThat(exportedCustomerConsentsFile)
				.as(String.format("Exported customer consents file not found: %s", exportedCustomerConsentsFile.getAbsolutePath()))
				.exists();

		try (final FileInputStream exportedCustomerConsentFileStream = new FileInputStream(exportedCustomerConsentsFile)) {
			final XMLUnmarshaller customerConsentUnmarshaller = new XMLUnmarshaller(CustomerConsentsDTO.class);
			customerConsentsDTO = customerConsentUnmarshaller.unmarshall(exportedCustomerConsentFileStream);
		}
	}

	/**
	 * Ensure the customer consents is exported.
	 *
	 * @param dataTable the exported customer consent info.
	 * @throws ParseException in case of date parsing error.
	 */
	@Then("^the exported customer consents records should equal$")
	public void verifyExportedCustomerConsent(final DataTable dataTable) throws ParseException {
		List<CustomerConsentDTO> expectedCustomerConsentDTO =
				CustomerConsentSteps.getCustomerConsentsFromDataTable(dataTable.asMaps(String.class, String.class));

		verifyListOfCustomerConsentDTOs(expectedCustomerConsentDTO, customerConsentsDTO.getCustomerConsents());
	}

	private void verifyListOfCustomerConsentDTOs(final List<CustomerConsentDTO> expectedCustomerConsents, final List<CustomerConsentDTO>
			exportedCustomerConsents) {
		for (CustomerConsentDTO expectedCustomerConsent : expectedCustomerConsents) {
			CustomerConsentDTO exportedCustomerConsent =
					exportedCustomerConsents.get(exportedCustomerConsents.indexOf(expectedCustomerConsent));

			assertThat(exportedCustomerConsent)
					.isEqualToComparingOnlyGivenFields(expectedCustomerConsent, "guid", "action", "customerGuid", "dataPolicyGuid");
		}
	}

	/**
	 * Ensure manifest file includes an entry for customer consents.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Then("^the exported manifest file should have an entry for customer consents$")
	public void ensureManifestIncludesCustomerConsents() throws IOException {
		final File exportedManifestFile = new File(exportDirectory, MANIFEST_EXPORT_FILE);

		assertThat(exportedManifestFile)
				.as(String.format("Exported manifest file not found: %s", exportedManifestFile.getAbsolutePath()))
				.exists();


		try (final FileInputStream exportedManifestFileStream = new FileInputStream(exportedManifestFile)) {
			final XMLUnmarshaller manifestUnmarshaller = new XMLUnmarshaller(Manifest.class);
			final Manifest manifest = manifestUnmarshaller.unmarshall(exportedManifestFileStream);

			assertThat(manifest.getResources())
					.as("Manifest file missing customer consents entry")
					.contains(CUSTOMER_CONSENT_EXPORT_FILE);
		}
	}
}
