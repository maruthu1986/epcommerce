/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.cucumber.steps;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.common.dto.datapolicy.DataPointDTO;
import com.elasticpath.common.dto.datapolicy.DataPolicyDTO;
import com.elasticpath.domain.datapolicy.DataPolicyState;
import com.elasticpath.importexport.builder.ExportConfigurationBuilder;
import com.elasticpath.importexport.builder.ImportExportTestDirectoryBuilder;
import com.elasticpath.importexport.common.dto.datapolicy.DataPoliciesDTO;
import com.elasticpath.importexport.common.manifest.Manifest;
import com.elasticpath.importexport.common.marshalling.XMLUnmarshaller;
import com.elasticpath.importexport.common.types.RequiredJobType;
import com.elasticpath.importexport.exporter.configuration.ExportConfiguration;
import com.elasticpath.importexport.exporter.configuration.search.SearchConfiguration;
import com.elasticpath.importexport.exporter.controller.ExportController;

/**
 * Export data policies.
 */
public class ExportDataPolicySteps {

	private static final String DATA_POLICY_EXPORT_FILE = "data_policies.xml";

	private static final String MANIFEST_EXPORT_FILE = "manifest.xml";

	@Autowired
	private ExportController exportController;

	private DataPoliciesDTO dataPoliciesDTO;

	private File exportDirectory;

	private static int runNumber = 1;

	/**
	 * Export data policies with importexport tool.
	 *
	 * @throws Exception the exception
	 */
	@When("^exporting data policies with the importexport tool")
	public void runExport() throws Exception {
		exportDirectory =
				ImportExportTestDirectoryBuilder.newInstance()
						.withTestName(this.getClass().getSimpleName())
						.withRunNumber(runNumber++)
						.build();

		final ExportConfiguration exportConfiguration =
				ExportConfigurationBuilder.newInstance()
						.setDeliveryTarget(exportDirectory.getPath())
						.setExporterTypes(Arrays.asList(RequiredJobType.DATA_POLICY))
						.build();

		final SearchConfiguration searchConfiguration = new SearchConfiguration();

		exportController.loadConfiguration(exportConfiguration, searchConfiguration);
		exportController.executeExport();
	}

	/**
	 * Parses the exported data policies data.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@When("^the exported data policies data is parsed$")
	public void parseExportedDataPolicyData() throws IOException {
		final File exportedDataPoliciesFile = new File(exportDirectory, DATA_POLICY_EXPORT_FILE);

		assertThat(exportedDataPoliciesFile)
				.as(String.format("Exported data policies file not found: %s", exportedDataPoliciesFile.getAbsolutePath()))
				.exists();

		try (final FileInputStream exportedDataPolicyFileStream = new FileInputStream(exportedDataPoliciesFile)) {
			final XMLUnmarshaller dataPolicyGroupUnmarshaller = new XMLUnmarshaller(DataPoliciesDTO.class);
			dataPoliciesDTO = dataPolicyGroupUnmarshaller.unmarshall(exportedDataPolicyFileStream);
		}
	}

	/**
	 * Ensure the data policies is exported.
	 *
	 * @param guid the data policy guid
	 */
	@Then("^the exported data policies records should include \\[([A-Z0-9_]+)\\]$")
	public void ensureDataPoliciesExported(final String guid) {
		final DataPolicyDTO matchingDataPolicyDTO =
				findDataPolicyDTOByGuid(dataPoliciesDTO.getDataPolicies(), guid);

		assertThat(matchingDataPolicyDTO)
				.as(String.format("Data policy [%s] not found in exported data policy records", guid))
				.isNotNull();
	}

	/**
	 * Ensure the data policies is exported with policy state value.
	 *
	 * @param guid              the data policy guid
	 * @param policyStateString the policy state string
	 */
	@Then("^the exported data policies records should include \\[([A-Z0-9_]+)\\] with policy state of \\[(DRAFT|ACTIVE|DISABLED)\\]$")
	public void ensureDataPoliciesExportedWithState(final String guid, final String policyStateString) {
		final DataPolicyDTO matchingDataPolicyDTO =
				findDataPolicyDTOByGuid(dataPoliciesDTO.getDataPolicies(), guid);
		DataPolicyState state = DataPolicyState.valueOf(policyStateString);

		assertThat(matchingDataPolicyDTO)
				.as(String.format("Data policy [%s] not found in exported data policy records", guid))
				.isNotNull();

		assertThat(state.getName())
				.as(String.format("Data policy [%s] is exported with incorrect policy state value", guid))
				.isEqualTo(matchingDataPolicyDTO.getState());
	}

	/**
	 * Ensure the data policy is exported with data points.
	 *
	 * @param guid       the data policy guid
	 * @param dataPoints the data point string
	 */
	@Then("^the exported data policy records should include \\[([A-Z0-9_]+)\\] with data points? \\[([A-Z0-9_,]+)\\]$")
	public void ensureDataPolicyExportedWithDataPoints(final String guid, final String dataPoints) {
		validateDataPolicyExportedWithDataPoints(guid, Arrays.asList(dataPoints.split(",")));
	}

	/**
	 * Ensure the data policy is exported with no data points.
	 *
	 * @param guid he data policy name
	 */
	@Then("^the exported data policy records should include \\[([A-Z0-9_]+)\\] with no data points$")
	public void ensureataPolicyExportedWithNoDataPoints(final String guid) {
		validateDataPolicyExportedWithDataPoints(guid, Collections.<String>emptyList());
	}

	private void validateDataPolicyExportedWithDataPoints(final String guid, final List<String> expectedDataPoints) {
		final DataPolicyDTO matchingDataPolicyDTO =
				findDataPolicyDTOByGuid(dataPoliciesDTO.getDataPolicies(), guid);

		assertThat(matchingDataPolicyDTO)
				.as(String.format("Data policy [%s] not found in exported data policy records", guid))
				.isNotNull();

		final List<String> exportedDataPoints = matchingDataPolicyDTO.getDataPoints()
				.stream()
				.map(DataPointDTO::getGuid)
				.collect(Collectors.toList());

		assertThat(expectedDataPoints)
				.as(String.format("Data policy [%s] is exported with incorrect data points", guid))
				.isEqualTo(exportedDataPoints);
	}

	private DataPolicyDTO findDataPolicyDTOByGuid(
			final List<DataPolicyDTO> dataPolicyDTOs, final String guid) {

		final List<DataPolicyDTO> matchingDataPolicyDTOs = dataPolicyDTOs
				.stream()
				.filter(dataPolicy -> guid.equals(dataPolicy.getGuid()))
				.collect(Collectors.toList());

		if (matchingDataPolicyDTOs.isEmpty()) {
			return null;
		}

		return matchingDataPolicyDTOs.iterator().next();
	}

	/**
	 * Ensure manifest file includes an entry for data policies.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Then("^the exported manifest file should have an entry for data policies$")
	public void ensureManifestIncludesDataPolicies() throws IOException {
		final File exportedManifestFile = new File(exportDirectory, MANIFEST_EXPORT_FILE);

		assertThat(exportedManifestFile)
				.as(String.format("Exported manifest file not found: %s", exportedManifestFile.getAbsolutePath()))
				.exists();


		try (final FileInputStream exportedManifestFileStream = new FileInputStream(exportedManifestFile)) {
			final XMLUnmarshaller manifestUnmarshaller = new XMLUnmarshaller(Manifest.class);
			final Manifest manifest = manifestUnmarshaller.unmarshall(exportedManifestFileStream);

			assertThat(manifest.getResources())
					.as("Manifest file missing data policies entry")
					.contains(DATA_POLICY_EXPORT_FILE);
		}
	}
}
