/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.cucumber.steps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.common.dto.customer.CustomerGroupDTO;
import com.elasticpath.common.dto.customer.CustomerRoleDTO;
import com.elasticpath.importexport.builder.ExportConfigurationBuilder;
import com.elasticpath.importexport.builder.ImportExportTestDirectoryBuilder;
import com.elasticpath.importexport.common.dto.customer.CustomerGroupsDTO;
import com.elasticpath.importexport.common.manifest.Manifest;
import com.elasticpath.importexport.common.marshalling.XMLUnmarshaller;
import com.elasticpath.importexport.common.types.RequiredJobType;
import com.elasticpath.importexport.exporter.configuration.ExportConfiguration;
import com.elasticpath.importexport.exporter.configuration.search.SearchConfiguration;
import com.elasticpath.importexport.exporter.controller.ExportController;

/**
 * Export customer group steps.
 */
public class ExportCustomerGroupSteps {

	private static final String CUSTOMER_GROUPS_EXPORT_FILE = "customer_groups.xml";

	private static final String MANIFEST_EXPORT_FILE = "manifest.xml";

	@Autowired
	private ExportController exportController;

	private CustomerGroupsDTO exportedCustomerGroupsDTO;

	private File exportDirectory;

	private static int runNumber = 1;

	/**
	 * Export customer groups with importexport tool.
	 *
	 * @throws Exception the exception
	 */
	@When("^exporting customer segments with the importexport tool")
	public void runExport() throws Exception {
		exportDirectory =
				ImportExportTestDirectoryBuilder.newInstance()
					.withTestName(this.getClass().getSimpleName())
					.withRunNumber(runNumber++)
					.build();

		final ExportConfiguration exportConfiguration =
				ExportConfigurationBuilder.newInstance()
					.setDeliveryTarget(exportDirectory.getPath())
					.setExporterTypes(Arrays.asList(RequiredJobType.CUSTOMER_GROUP))
					.build();

		final SearchConfiguration searchConfiguration = new SearchConfiguration();

		exportController.loadConfiguration(exportConfiguration, searchConfiguration);
		exportController.executeExport();
	}

	/**
	 * Parses the exported customer group data.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@When("^the exported customer segments data is parsed$")
	public void parseExportedCustomerGroupData() throws IOException {
		final File exportedCustomerGroupsFile = new File(exportDirectory, CUSTOMER_GROUPS_EXPORT_FILE);

		assertTrue(
				String.format("Exported customer segments file not found: %s", exportedCustomerGroupsFile.getAbsolutePath()),
				exportedCustomerGroupsFile.exists());

		final XMLUnmarshaller customerGroupUnmarshaller = new XMLUnmarshaller(CustomerGroupsDTO.class);
		final FileInputStream exportedCustomerGroupsFileStream = new FileInputStream(exportedCustomerGroupsFile);
		exportedCustomerGroupsDTO = customerGroupUnmarshaller.unmarshall(exportedCustomerGroupsFileStream);

		exportedCustomerGroupsFileStream.close();
	}

	/**
	 * Ensure the customer group is exported with enabled value.
	 *
	 * @param customerGroupName the customer group name
	 * @param enabledString the enabled string
	 */
	@Then("^the exported customer segment records should include \\[([A-Z0-9_]+)\\] with enabled value of \\[(TRUE|FALSE)\\]$")
	public void ensureCustomerGroupExportedWithEnabledValue(final String customerGroupName, final String enabledString) {
		final CustomerGroupDTO matchingCustomerGroupDTO =
				findCustomerGroupDTOByName(exportedCustomerGroupsDTO.getCustomerGroups(), customerGroupName);

		final boolean shouldBeEnabled = "TRUE".equals(enabledString);

		assertNotNull(
				String.format("Customer segment [%s] not found in exported customer segment records", customerGroupName),
				matchingCustomerGroupDTO);

		assertEquals(
				String.format("Customer segment [%s] is exported with incorrect enabled value", customerGroupName),
				shouldBeEnabled, matchingCustomerGroupDTO.isEnabled());
	}

	/**
	 * Ensure the customer group is exported with roles.
	 *
	 * @param customerGroupName the customer group name
	 * @param authoritiesString the authorities string
	 */
	@Then("^the exported customer segment records should include \\[([A-Z0-9_]+)\\] with roles? \\[([A-Z_,]+)\\]$")
	public void ensureCustomerGroupExportedWithRoles(final String customerGroupName, final String authoritiesString) {
		validateCustomerGroupExportedWithRoles(customerGroupName, Arrays.asList(authoritiesString.split(",")));
	}

	/**
	 * Ensure the customer group is exported with no role.
	 *
	 * @param customerGroupName the customer group name
	 */
	@Then("^the exported customer segment records should include \\[([A-Z0-9_]+)\\] with no role$")
	public void ensureCustomerGroupExportedWithNoRole(final String customerGroupName) {
		validateCustomerGroupExportedWithRoles(customerGroupName, Collections.<String>emptyList());
	}

	private void validateCustomerGroupExportedWithRoles(final String customerGroupName, final List<String> expectedAuthorities) {
		final CustomerGroupDTO matchingCustomerGroupDTO =
				findCustomerGroupDTOByName(exportedCustomerGroupsDTO.getCustomerGroups(), customerGroupName);

		assertNotNull(
				String.format("Customer segment [%s] not found in exported customer segment records", customerGroupName),
				matchingCustomerGroupDTO);

		final Collection<String> exportedGroupAuthorities =
				Collections2.transform(matchingCustomerGroupDTO.getCustomerRoles(), new Function<CustomerRoleDTO, String>() {
					@Override
					public String apply(final CustomerRoleDTO customerRoleDTO) {
						return customerRoleDTO.getAuthority();
					}
				});

		assertTrue(
				String.format("Customer segment [%s] is exported with incorrect roles", customerGroupName),
				CollectionUtils.isEqualCollection(expectedAuthorities, exportedGroupAuthorities));
	}

	private CustomerGroupDTO findCustomerGroupDTOByName(
			final List<CustomerGroupDTO> customerGroupDTOs, final String customerGroupName) {

		final Collection<CustomerGroupDTO> matchingCustomerGroupDTOs =
				Collections2.filter(customerGroupDTOs,
					new Predicate<CustomerGroupDTO>() {
						@Override
						public boolean apply(final CustomerGroupDTO customerGroupDTO) {
							return customerGroupName.equals(customerGroupDTO.getName());
						}
					});

		if (matchingCustomerGroupDTOs.isEmpty()) {
			return null;
		}

		return matchingCustomerGroupDTOs.iterator().next();
	}

	/**
	 * Ensure manifest file includes an entry for customer groups.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Then("^the exported manifest file should have an entry for customer segments$")
	public void ensureManifestIncludesCustomerGroups() throws IOException {
		final File exportedManifestFile = new File(exportDirectory, MANIFEST_EXPORT_FILE);

		assertTrue(
				String.format("Exported manifest file not found: %s", exportedManifestFile.getAbsolutePath()),
				exportedManifestFile.exists());

		final XMLUnmarshaller manifestUnmarshaller = new XMLUnmarshaller(Manifest.class);
		final FileInputStream exportedManifestFileStream = new FileInputStream(exportedManifestFile);
		final Manifest manifest = manifestUnmarshaller.unmarshall(exportedManifestFileStream);

		assertTrue(
				"Manifest file missing customer segments entry",
				manifest.getResources().contains(CUSTOMER_GROUPS_EXPORT_FILE));

		exportedManifestFileStream.close();
	}
}
