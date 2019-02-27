/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.cucumber.steps;

import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
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
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.common.dto.customer.AttributeValueDTO;
import com.elasticpath.common.dto.customer.CustomerDTO;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.impl.CustomerImpl;
import com.elasticpath.importexport.builder.ExportConfigurationBuilder;
import com.elasticpath.importexport.builder.ImportExportTestDirectoryBuilder;
import com.elasticpath.importexport.common.dto.customer.CustomersDTO;
import com.elasticpath.importexport.common.manifest.Manifest;
import com.elasticpath.importexport.common.marshalling.XMLUnmarshaller;
import com.elasticpath.importexport.common.types.RequiredJobType;
import com.elasticpath.importexport.exporter.configuration.ExportConfiguration;
import com.elasticpath.importexport.exporter.configuration.search.SearchConfiguration;
import com.elasticpath.importexport.exporter.configuration.search.SearchQuery;
import com.elasticpath.importexport.exporter.controller.ExportController;
import com.elasticpath.ql.parser.EPQueryType;
import com.elasticpath.search.searchengine.EpQLSearchEngine;
import com.elasticpath.search.searchengine.SolrIndexSearchResult;
import com.elasticpath.service.customer.CustomerGroupService;

/**
 * Export customer steps.
 */
public class ExportCustomerSteps {

	private static final String CUSTOMERS_EXPORT_FILE = "customers.xml";

	private static final String MANIFEST_EXPORT_FILE = "manifest.xml";

	private static final String CUSTOMER_SEARCH_QUERY = "FIND Customer";

	@Autowired
	private ExportController exportController;

	@Autowired
	private CustomerGroupService customerGroupService;

	@Autowired
	private EpQLSearchEngine epQLSearchEngine;

	private CustomersDTO exportedCustomersDTO;

	private File exportDirectory;

	private static int runNumber = 1;

	/**
	 * Export customers with importexport tool.
	 *
	 * @throws Exception in case of any exception
	 */
	@When("^exporting customers with the importexport tool")
	public void runExport() throws Exception {
		exportDirectory =
				ImportExportTestDirectoryBuilder.newInstance()
						.withTestName(this.getClass().getSimpleName())
						.withRunNumber(runNumber++)
						.build();

		final ArrayList<Long> createdCustomerUids = new ArrayList<>(
				Collections2.transform(CustomerSteps.getPersistedCustomers(), new Function<Customer, Long>() {
					@Override
					public Long apply(final Customer customer) {
						return customer.getUidPk();
					}
				})
		);

		final SolrIndexSearchResult<Long> customerSearchResult = new SolrIndexSearchResult<>();
		customerSearchResult.setEpQueryType(EPQueryType.CUSTOMER);
		customerSearchResult.setResultUids(createdCustomerUids);

		when(epQLSearchEngine.<Long>search(CUSTOMER_SEARCH_QUERY)).thenReturn(customerSearchResult);

		final ExportConfiguration exportConfiguration =
				ExportConfigurationBuilder.newInstance()
						.setDeliveryTarget(exportDirectory.getPath())
						.setExporterTypes(Arrays.asList(RequiredJobType.CUSTOMER))
						.build();

		final SearchConfiguration searchConfiguration = new SearchConfiguration();
		searchConfiguration.getQueries().add(new SearchQuery(EPQueryType.CUSTOMER.getTypeName(), CUSTOMER_SEARCH_QUERY));

		exportController.loadConfiguration(exportConfiguration, searchConfiguration);
		exportController.executeExport();
	}

	/**
	 * Parses the exported customer data.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@When("^the exported customers data is parsed$")
	public void parseExportedCustomerData() throws IOException {
		final File exportedCustomersFile = new File(exportDirectory, CUSTOMERS_EXPORT_FILE);

		Assert.assertTrue(
				String.format("Exported customers file not found: %s", exportedCustomersFile.getAbsolutePath()),
				exportedCustomersFile.exists());

		final XMLUnmarshaller customersUnmarshaller = new XMLUnmarshaller(CustomersDTO.class);
		final FileInputStream exportedCustomersFileStream = new FileInputStream(exportedCustomersFile);
		exportedCustomersDTO = customersUnmarshaller.unmarshall(exportedCustomersFileStream);

		exportedCustomersFileStream.close();
	}

	/**
	 * Ensure the customer is exported with customer groups.
	 *
	 * @param customerFirstName  the customer first name
	 * @param customerLastName   the customer last name
	 * @param customerGroupNames the customer group names
	 */
	@Then("^the exported customer record \\[(\\w+) (\\w+)\\] should include associations? to customer segments? \\[([A-Z0-9_,]+)\\]$")
	public void ensureCustomerExportedWithCustomerGroups(
			final String customerFirstName, final String customerLastName, final String customerGroupNames) {
		validateCustomerExportedWithGroups(customerFirstName, customerLastName, Arrays.asList(customerGroupNames.split(",")));
	}

	/**
	 * Ensure the customer is exported with no customer group.
	 *
	 * @param customerFirstName the customer first name
	 * @param customerLastName  the customer last name
	 */
	@Then("^the exported customer record \\[(\\w+) (\\w+)\\] should have no association to any customer segment$")
	public void ensureCustomerExportedWithNoCustomerGroup(final String customerFirstName, final String customerLastName) {
		validateCustomerExportedWithGroups(customerFirstName, customerLastName, Collections.<String>emptyList());
	}

	private void validateCustomerExportedWithGroups(
			final String customerFirstName, final String customerLastName, final List<String> expectedCustomerGroupNames) {

		final CustomerDTO matchingCustomerDTO =
				findCustomerDTOByName(exportedCustomersDTO.getCustomers(), customerFirstName, customerLastName);

		Assert.assertNotNull(
				String.format("Customer [%s %s] not found in exported customer records", customerFirstName, customerLastName),
				matchingCustomerDTO);

		final List<String> dtoGroupGuids = matchingCustomerDTO.getGroups();
		final Collection<String> dtoGroupNames =
				Collections2.transform(dtoGroupGuids, new Function<String, String>() {
					@Override
					public String apply(final String groupGuid) {
						return customerGroupService.findByGuid(groupGuid).getName();
					}
				});

		Assert.assertTrue(
				String.format("Customer [%s %s] is exported with incorrect association to customer segments",
						customerFirstName, customerLastName),
				CollectionUtils.isEqualCollection(expectedCustomerGroupNames, dtoGroupNames));
	}

	private CustomerDTO findCustomerDTOByName(final List<CustomerDTO> customerDTOs,
											  final String customerFirstName, final String customerLastName) {

		final Collection<CustomerDTO> matchingCustomerDTOs =
				Collections2.filter(customerDTOs,
						new Predicate<CustomerDTO>() {
							@Override
							public boolean apply(final CustomerDTO customerDTO) {
								final String dtoFirstName = getProfileValueString(customerDTO, CustomerImpl.ATT_KEY_CP_FIRST_NAME);
								final String dtoLastName = getProfileValueString(customerDTO, CustomerImpl.ATT_KEY_CP_LAST_NAME);
								return customerFirstName.equals(dtoFirstName) && customerLastName.equals(dtoLastName);
							}
						});

		if (matchingCustomerDTOs.isEmpty()) {
			return null;
		}

		return matchingCustomerDTOs.iterator().next();
	}

	private String getProfileValueString(final CustomerDTO customerDTO, final String profileKey) {
		for (AttributeValueDTO attributeValueDTO : customerDTO.getProfileValues()) {
			if (profileKey.equals(attributeValueDTO.getKey())) {
				return attributeValueDTO.getValue();
			}
		}
		return null;
	}

	/**
	 * Ensure manifest file includes an entry for customers.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Then("^the exported manifest file should have an entry for customers$")
	public void ensureManifestIncludesCustomers() throws IOException {
		final File exportedManifestFile = new File(exportDirectory, MANIFEST_EXPORT_FILE);

		Assert.assertTrue(
				String.format("Exported manifest file not found: %s", exportedManifestFile.getAbsolutePath()),
				exportedManifestFile.exists());

		final XMLUnmarshaller manifestUnmarshaller = new XMLUnmarshaller(Manifest.class);
		final FileInputStream exportedManifestFileStream = new FileInputStream(exportedManifestFile);
		final Manifest manifest = manifestUnmarshaller.unmarshall(exportedManifestFileStream);

		Assert.assertTrue(
				"Manifest file missing customers entry",
				manifest.getResources().contains(CUSTOMERS_EXPORT_FILE));

		exportedManifestFileStream.close();
	}
}
