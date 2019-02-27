/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.cucumber.steps;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.common.dto.customer.CustomerGroupDTO;
import com.elasticpath.commons.util.TestDomainMarshaller;
import com.elasticpath.importexport.builder.ImportConfigurationBuilder;
import com.elasticpath.importexport.builder.ImportExportTestDirectoryBuilder;
import com.elasticpath.importexport.common.dto.customer.CustomerGroupsDTO;
import com.elasticpath.importexport.common.manifest.Manifest;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.importer.configuration.ImportConfiguration;
import com.elasticpath.importexport.importer.controller.ImportController;
import com.elasticpath.importexport.importer.types.ImportStrategyType;

/**
 * Import customer group steps.
 */
public class ImportCustomerGroupSteps {

	private static final String CUSTOMER_GROUPS_IMPORT_FILE = "customer_groups.xml";

	private static final String MANIFEST_EXPORT_FILE = "manifest.xml";

	@Autowired
	private ImportController importController;

	private CustomerGroupsDTO customerGroupsDtoForImport;

	private File importDirectory;

	private static int runNumber = 1;


	/**
	 * Clear import data.
	 */
	@Given("^the customer group import data has been emptied out$")
	public void clearCustomerGroupImportData() {
		customerGroupsDtoForImport = new CustomerGroupsDTO();
	}

	/**
	 * Include customer group in import data.
	 *
	 * @param customerGroupName the customer group name
	 * @param unused the unused
	 */
	@Given("^the customer segment \\[([A-Z0-9_]+)\\] is included in the import data( with no .*)?$")
	public void includeCustomerGroupInImportData(final String customerGroupName, final String unused) {
		includeCustomerGroup(customerGroupName, true, Collections.<String>emptyList());
	}

	/**
	 * Include customer group with enabled value in import data.
	 *
	 * @param customerGroupName the customer group name
	 * @param enabledString the enabled string
	 */
	@Given("^the customer segment \\[([A-Z0-9_]+)\\] is included in the import data with enabled value of \\[(TRUE|FALSE)\\]$")
	public void includeCustomerGroupWithEnabledValueInImportData(final String customerGroupName, final String enabledString) {
		includeCustomerGroup(customerGroupName, "TRUE".equals(enabledString), Collections.<String>emptyList());
	}

	/**
	 * Include customer group with roles in import data.
	 *
	 * @param customerGroupName the customer group name
	 * @param authoritiesString the authorities string
	 */
	@Given("^the customer segment \\[([A-Z0-9_]+)\\] is included in the import data with roles? \\[([A-Z_,]+)\\]$")
	public void includeCustomerGroupWithRolesInImportData(final String customerGroupName, final String authoritiesString) {
		includeCustomerGroup(customerGroupName, true, Arrays.asList(authoritiesString.split(",")));
	}

	private void includeCustomerGroup(final String customerGroupName, final boolean enabled, final List<String> authorities) {
		final CustomerGroupDTO customerGroupDTO = new CustomerGroupDTO();
		customerGroupDTO.setGuid(String.format("guid_%s", customerGroupName));
		customerGroupDTO.setName(customerGroupName);
		customerGroupDTO.setDescription(String.format("Description for %s", customerGroupName));
		customerGroupDTO.setEnabled(enabled);

		for (String authority : authorities) {
			customerGroupDTO.addCustomerRole(authority);
		}

		customerGroupsDtoForImport.getCustomerGroups().add(customerGroupDTO);
	}

	/**
	 * Import customer groups with importexport tool.
	 *
	 * @throws Exception the exception
	 */
	@When("^importing customer segments with the importexport tool$")
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
		manifest.addResource(CUSTOMER_GROUPS_IMPORT_FILE);

		TestDomainMarshaller.marshalObject(Manifest.class, manifest, new File(importDirectory, MANIFEST_EXPORT_FILE));
		TestDomainMarshaller.marshalObject(CustomerGroupsDTO.class, customerGroupsDtoForImport,
				new File(importDirectory, CUSTOMER_GROUPS_IMPORT_FILE));

		final ImportConfiguration importConfiguration =
				ImportConfigurationBuilder.newInstance()
					.setRetrievalSource(importDirectory.getPath())
					.addImporterConfiguration(JobType.CUSTOMER_GROUP, ImportStrategyType.INSERT_OR_UPDATE)
					.build();

		importController.loadConfiguration(importConfiguration);
		importController.executeImport();
	}

}
