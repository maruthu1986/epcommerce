/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.cucumber.steps;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.common.dto.customer.AttributeValueDTO;
import com.elasticpath.common.dto.customer.CustomerDTO;
import com.elasticpath.commons.util.TestDomainMarshaller;
import com.elasticpath.domain.attribute.AttributeType;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerGroup;
import com.elasticpath.domain.customer.impl.CustomerImpl;
import com.elasticpath.importexport.builder.ImportConfigurationBuilder;
import com.elasticpath.importexport.builder.ImportExportTestDirectoryBuilder;
import com.elasticpath.importexport.common.dto.customer.CustomersDTO;
import com.elasticpath.importexport.common.manifest.Manifest;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.importer.configuration.ImportConfiguration;
import com.elasticpath.importexport.importer.controller.ImportController;
import com.elasticpath.importexport.importer.types.ImportStrategyType;
import com.elasticpath.service.customer.CustomerGroupService;
import com.elasticpath.service.store.StoreService;

/**
 * Import customer steps.
 */
public class ImportCustomerSteps {

	private static final String CUSTOMERS_IMPORT_FILE = "customers.xml";

	private static final String MANIFEST_EXPORT_FILE = "manifest.xml";

	@Autowired
	private CustomerGroupService customerGroupService;

	@Autowired
	private StoreService storeService;

	@Autowired
	private ImportController importController;

	private CustomersDTO customersDtoForImport;

	private File importDirectory;

	private static int runNumber = 1;


	/**
	 * Clear import data.
	 */
	@Given("^the customer import data has been emptied out$")
	public void clearCustomerImportData() {
		customersDtoForImport = new CustomersDTO();
	}

	/**
	 * Include customer in import data.
	 *
	 * @param customerFirstName the customer first name
	 * @param customerLastName the customer last name
	 * @param unused the unused
	 */
	@Given("^the customer \\[(\\w+) (\\w+)\\] is included in the import data( with no .*)?$")
	public void includeCustomerInImportData(final String customerFirstName, final String customerLastName, final String unused) {
		includeCustomerInImportData(customerFirstName, customerLastName, Collections.<String>emptyList());
	}

	/**
	 * Include customer with groups in import data.
	 *
	 * @param customerFirstName the customer first name
	 * @param customerLastName the customer last name
	 * @param customerGroupNames the customer group names
	 */
	@Given("^the customer \\[(\\w+) (\\w+)\\] is included in the import data with assigned customer segments? \\[([A-Z0-9_,]+)\\]$")
	public void includeCustomerWithGroupsInImportData(
			final String customerFirstName, final String customerLastName, final String customerGroupNames) {
		includeCustomerInImportData(customerFirstName, customerLastName, Arrays.asList(customerGroupNames.split(",")));
	}

	private void includeCustomerInImportData(
			final String customerFirstName, final String customerLastName, final List<String> customerGroupNames) {

		final CustomerDTO customerDTO = new CustomerDTO();
		customerDTO.setGuid(CustomerSteps.generateCustomerGuidFromName(customerFirstName, customerLastName));
		customerDTO.setUserId(String.format("%s.%s@elasticpath.com", customerFirstName, customerLastName));
		customerDTO.setStoreCode(storeService.findAllStores().get(0).getCode());
		customerDTO.setStatus(Customer.STATUS_ACTIVE);
		customerDTO.setCreationDate(new Date());
		customerDTO.setLastEditDate(new Date());
		customerDTO.setPassword("password123");

		final Set<AttributeValueDTO> profileValueDTOs = new HashSet<>();
		profileValueDTOs.add(createProfileValueDTO(AttributeType.SHORT_TEXT, CustomerImpl.ATT_KEY_CP_FIRST_NAME, customerFirstName));
		profileValueDTOs.add(createProfileValueDTO(AttributeType.SHORT_TEXT, CustomerImpl.ATT_KEY_CP_LAST_NAME, customerLastName));
		profileValueDTOs.add(createProfileValueDTO(AttributeType.SHORT_TEXT, CustomerImpl.ATT_KEY_CP_EMAIL,
				String.format("%s.%s@elasticpath.com", customerFirstName, customerLastName)));
		profileValueDTOs.add(createProfileValueDTO(AttributeType.BOOLEAN, CustomerImpl.ATT_KEY_CP_ANONYMOUS_CUST, Boolean.toString(false)));
		customerDTO.setProfileValues(profileValueDTOs);

		final List<String> customerGroupGuids = new ArrayList<>();
		for (String customerGroupName : customerGroupNames) {
			final CustomerGroup customerGroup = customerGroupService.findByGroupName(customerGroupName);
			customerGroupGuids.add(customerGroup.getGuid());
		}
		customerDTO.setGroups(customerGroupGuids);

		customersDtoForImport.getCustomers().add(customerDTO);
	}

	private AttributeValueDTO createProfileValueDTO(final AttributeType attributeType, final String key, final String stringValue) {
		final AttributeValueDTO profileValueDTO = new AttributeValueDTO();
		profileValueDTO.setType(attributeType.getNameMessageKey());
		profileValueDTO.setKey(key);
		profileValueDTO.setValue(stringValue);

		return profileValueDTO;
	}

	/**
	 * Import customers with importexport tool.
	 *
	 * @throws Exception the exception
	 */
	@When("^importing customers with the importexport tool$")
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
		manifest.addResource(CUSTOMERS_IMPORT_FILE);

		TestDomainMarshaller.marshalObject(Manifest.class, manifest, new File(importDirectory, MANIFEST_EXPORT_FILE));
		TestDomainMarshaller.marshalObject(CustomersDTO.class, customersDtoForImport, new File(importDirectory, CUSTOMERS_IMPORT_FILE));

		final ImportConfiguration importConfiguration =
				ImportConfigurationBuilder.newInstance()
					.setRetrievalSource(importDirectory.getPath())
					.addImporterConfiguration(JobType.CUSTOMER, ImportStrategyType.INSERT_OR_UPDATE)
					.build();

		importController.loadConfiguration(importConfiguration);
		importController.executeImport();

	}

}
