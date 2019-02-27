/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.cucumber.steps;

import static org.assertj.core.api.Assertions.assertThat;

import static com.elasticpath.commons.util.TestDomainMarshaller.marshalObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import cucumber.api.DataTable;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.common.dto.datapolicy.DataPointDTO;
import com.elasticpath.common.dto.datapolicy.DataPolicyDTO;
import com.elasticpath.importexport.builder.ImportConfigurationBuilder;
import com.elasticpath.importexport.builder.ImportExportTestDirectoryBuilder;
import com.elasticpath.importexport.common.dto.datapolicy.DataPoliciesDTO;
import com.elasticpath.importexport.common.manifest.Manifest;
import com.elasticpath.importexport.common.summary.Summary;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.common.util.Message;
import com.elasticpath.importexport.importer.configuration.ImportConfiguration;
import com.elasticpath.importexport.importer.controller.ImportController;
import com.elasticpath.importexport.importer.types.ImportStrategyType;

/**
 * Import data policy steps.
 */
public class ImportDataPolicySteps {

	private static final String DATA_POLICIES_IMPORT_FILE = "data_policies.xml";

	private static final String MANIFEST_EXPORT_FILE = "manifest.xml";

	@Autowired
	private ImportController importController;

	private DataPoliciesDTO dataPolicyDtoForImport;

	private File importDirectory;

	private Summary summary;

	private static int runNumber = 1;


	/**
	 * Clear import data.
	 */
	@Given("^the data policy import data has been emptied out$")
	public void clearDataPolicyImportData() {
		dataPolicyDtoForImport = new DataPoliciesDTO();
	}

	/**
	 * Setup the tests with data polices.
	 *
	 * @param dataTable data policy info.
	 */
	@Given("^the data policies to import of$")
	public void setUpDataPolicies(final DataTable dataTable) {
		final List<DataPolicyDTO> dataPolicies = getDataPolicyDTOsFromDataTable(dataTable.asMaps(String.class, String.class));

		dataPolicyDtoForImport.getDataPolicies().addAll(dataPolicies);
	}

	private List<DataPolicyDTO> getDataPolicyDTOsFromDataTable(final List<Map<String, String>> dataPolicesMap) {
		final List<DataPolicyDTO> dataPolicies = new ArrayList<>();
		for (Map<String, String> properties : dataPolicesMap) {
			final DataPolicyDTO dataPolicyDTO = new DataPolicyDTO();
			dataPolicyDTO.setGuid(properties.get("guid"));
			dataPolicyDTO.setPolicyName(properties.get("dataPolicyName"));
			dataPolicyDTO.setDescription(properties.get("description"));
			dataPolicyDTO.setReferenceKey(properties.get("referenceKey"));
			dataPolicyDTO.setRetentionPeriodInDays(Integer.valueOf(properties.get("retentionPeriod")));
			dataPolicyDTO.setState(properties.get("policyState"));
			dataPolicyDTO.setStartDate(new Date());
			dataPolicyDTO.setEndDate(new Date());
			dataPolicyDTO.setRetentionType(properties.get("retentionType"));
			dataPolicyDTO.setSegments(new HashSet<>(Arrays.asList(properties.get("segments").split(","))));
			dataPolicyDTO.setActivities(new HashSet<>(Arrays.asList(properties.get("activities"))));

			List<String> dataPointStringsList = Arrays.asList(properties.get("dataPoints").split(","));
			final List<DataPointDTO> dataPoints = new ArrayList<>();
			for (String dataPointStrings : dataPointStringsList) {
				String[] dataPointArray = dataPointStrings.trim().split(";");
				if (dataPointArray.length == DataPolicySteps.NUMBER_OF_DATA_POINT_VALUES) {
					DataPointDTO dataPoint = new DataPointDTO();
					dataPoint.setGuid(dataPointArray[DataPolicySteps.GUID_INDEX]);
					dataPoint.setName(dataPointArray[DataPolicySteps.NAME_INDEX]);
					dataPoint.setDescriptionKey(dataPointArray[DataPolicySteps.DESCRIPTION_INDEX]);
					dataPoint.setDataKey(dataPointArray[DataPolicySteps.KEY_INDEX]);
					dataPoint.setDataLocation(dataPointArray[DataPolicySteps.LOCATION_INDEX]);
					dataPoint.setRemovable(Boolean.parseBoolean(dataPointArray[DataPolicySteps.REMOVEABLE_INDEX]));

					dataPoints.add(dataPoint);
				}
			}
			dataPolicyDTO.getDataPoints().addAll(dataPoints);
			dataPolicies.add(dataPolicyDTO);
		}
		return dataPolicies;
	}

	/**
	 * Import data policy with importexport tool.
	 *
	 * @throws Exception the exception
	 */
	@When("^importing data policies with the importexport tool$")
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
		manifest.addResource(DATA_POLICIES_IMPORT_FILE);

		marshalObject(Manifest.class, manifest, new File(importDirectory, MANIFEST_EXPORT_FILE));
		marshalObject(DataPoliciesDTO.class, dataPolicyDtoForImport,
				new File(importDirectory, DATA_POLICIES_IMPORT_FILE));

		final ImportConfiguration importConfiguration =
				ImportConfigurationBuilder.newInstance()
						.setRetrievalSource(importDirectory.getPath())
						.addImporterConfiguration(JobType.DATA_POLICY, ImportStrategyType.INSERT)
						.build();

		importController.loadConfiguration(importConfiguration);
		summary = importController.executeImport();
	}

	/**
	 * Ensure an unsupported operation throws an exception.
	 *
	 * @param dataTable message info.
	 */
	@Then("^In the summary are the unsupported data policy operation warning messages of$")
	public void ensureWarningMessagesAreInSummary(final DataTable dataTable) {
		List<Message> warnings = summary.getWarnings();

		assertThat(warnings)
				.as("The import should have warnings.")
				.isNotEmpty();

		verifySummaryMessages(dataTable.asMaps(String.class, String.class), warnings);
	}

	/**
	 * Ensure an unsupported operation throws an exception.
	 *
	 * @param dataTable message info.
	 */
	@Then("^In the summary are the unsupported data policy operation error messages of$")
	public void ensureWarningErrorAreInSummary(final DataTable dataTable) {
		List<Message> failures = summary.getFailures();

		assertThat(failures)
				.as("The import should have failures.")
				.isNotEmpty();

		verifySummaryMessages(dataTable.asMaps(String.class, String.class), failures);
	}

	private void verifySummaryMessages(final List<Map<String, String>> messagesMap, final List<Message> importedMessages) {
		assertThat(importedMessages)
				.hasSize(messagesMap.size());

		for (Map<String, String> message : messagesMap) {
			final int index = messagesMap.indexOf(message);
			String[] details = message.get("details").split(",");

			assertThat(importedMessages.get(index).getCode())
					.isEqualTo(message.get("code"));
			assertThat(importedMessages.get(index).getParams())
					.isEqualTo(details);
		}
	}

}
