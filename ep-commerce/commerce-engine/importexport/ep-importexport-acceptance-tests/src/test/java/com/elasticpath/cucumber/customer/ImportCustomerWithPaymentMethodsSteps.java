/*
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.cucumber.customer;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.common.dto.customer.CustomerDTO;
import com.elasticpath.common.dto.customer.PaymentMethodDto;
import com.elasticpath.common.dto.customer.PaymentTokenDto;
import com.elasticpath.common.dto.customer.builder.CustomerDTOBuilder;
import com.elasticpath.common.dto.customer.transformer.PaymentTokenDTOTransformer;
import com.elasticpath.commons.util.TestDomainMarshaller;
import com.elasticpath.domain.builder.customer.CustomerBuilder;
import com.elasticpath.domain.builder.customer.CustomerGroupBuilder;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerGroup;
import com.elasticpath.importexport.common.configuration.PackagerConfiguration;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.factory.TestPaymentDTOBuilderFactory;
import com.elasticpath.importexport.common.factory.TestPaymentMethodBuilderFactory;
import com.elasticpath.importexport.common.summary.Summary;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.common.types.PackageType;
import com.elasticpath.importexport.common.types.TransportType;
import com.elasticpath.importexport.common.util.Message;
import com.elasticpath.importexport.importer.configuration.DependentElementConfiguration;
import com.elasticpath.importexport.importer.configuration.ImportConfiguration;
import com.elasticpath.importexport.importer.configuration.ImporterConfiguration;
import com.elasticpath.importexport.importer.configuration.RetrievalConfiguration;
import com.elasticpath.importexport.importer.controller.ImportController;
import com.elasticpath.importexport.importer.types.CollectionStrategyType;
import com.elasticpath.importexport.importer.types.DependentElementType;
import com.elasticpath.importexport.importer.types.ImportStrategyType;
import com.elasticpath.plugin.payment.dto.PaymentMethod;
import com.elasticpath.service.customer.CustomerGroupService;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.service.store.StoreService;

/**
 * Steps for the import customer with payment methods feature.
 */

@SuppressWarnings("PMD.TooManyFields")
public class ImportCustomerWithPaymentMethodsSteps {
	private static final String TEST_PASSWORD = "testPassword";
	private static final String TEST_SALT = "testSalt";
	private static final String TEST_DISPLAY_VALUE = "testDisplayValue";
	private static final String TEST_DISPLAY_VALUE_2 = "testDisplayValue2";
	private static final String TEST_USER_ID = "testUser@email.com";
	private static final String TEST_EMAIL = TEST_USER_ID;
	private static final String TEST_GUID = "testGuid";
	private static final String TEST_FIRST_NAME = "testFirstName";
	private static final String TEST_LAST_NAME = "testLastName";

	@Autowired
	private CustomerGroupService customerGroupService;

	@Autowired
	private ImportController importController;

	@Autowired
	private CustomerBuilder customerBuilder;

	@Autowired
	private CustomerGroupBuilder customerGroupBuilder;

	@Autowired
	private TestDomainMarshaller testDomainMarshaller;

	@Autowired
	private PaymentTokenDTOTransformer paymentTokenDTOTransformer;

	@Autowired
	private TestPaymentMethodBuilderFactory testPaymentMethodBuilderFactory;

	@Autowired
	private TestPaymentDTOBuilderFactory paymentDTOBuilderFactory;

	@Autowired
	private StoreService storeService;

	@Autowired
	private CustomerService customerService;

	private ImportConfiguration importConfiguration;

	private PaymentMethodDto testPaymentMethodA;
	private PaymentMethodDto testPaymentMethodB;
	private PaymentMethodDto testPaymentMethodC;

	private Summary summary;

	private String customersFilePath;

	/**
	 * Create test payment methods.
	 */
	@Before
	public void setupImportTestingComponents() {
		testPaymentMethodA = paymentDTOBuilderFactory.createPaymentTokenWithValue("A").build();
		testPaymentMethodB = paymentDTOBuilderFactory.createPaymentTokenWithValue("B").build();
		testPaymentMethodC = paymentDTOBuilderFactory.createPaymentTokenWithValue("C").build();

		if (customerGroupService.findByGroupName(CustomerGroup.DEFAULT_GROUP_NAME) == null) {
			final CustomerGroup defaultCustomerGroup = customerGroupBuilder.newInstance()
					.withGuid(String.format("guid_%s", CustomerGroup.DEFAULT_GROUP_NAME))
					.withName(CustomerGroup.DEFAULT_GROUP_NAME)
					.withDescription(String.format("Description for %s", CustomerGroup.DEFAULT_GROUP_NAME))
					.withEnabled(true)
					.build();
			customerGroupService.add(defaultCustomerGroup);
		}

		URL exportConfiguration = getClass().getClassLoader().getResource("customers");
		assert exportConfiguration != null : "Failed to load products directory";
		customersFilePath = exportConfiguration.getPath();
	}

	/**
	 * Create a existing customer.
	 */
	@Given("^a customer exists$")
	public void createACustomerWithNoPaymentMethods() {
		createPersistedCustomerWithPaymentMethods();
	}

	/**
	 * Create a existingCustomer.
	 */
	@Given("^a customer exists with payment methods$")
	public void createACustomerWithPaymentMethods() {
		PaymentMethod paymentMethod = testPaymentMethodBuilderFactory.createPaymentTokenBuilderWithIdentity(TEST_DISPLAY_VALUE).build();
		PaymentMethod paymentMethod2 = testPaymentMethodBuilderFactory.createPaymentTokenBuilderWithIdentity(TEST_DISPLAY_VALUE_2).build();

		createPersistedCustomerWithPaymentMethods(paymentMethod, paymentMethod2);
	}

	/**
	 * Create an XML representation including 3 payment methods (last one default).
	 *
	 * @throws Exception in case of error
	 */
	@And("^an import with payment methods A,B and C and C is the default$")
	public void setupImportFileForCustomerWithPaymentMethodsAndDefault() throws Exception {
		CustomerDTO customerDTO = createCustomerDTOWithPaymentMethodsAndDefault(Arrays.asList(testPaymentMethodA,
				testPaymentMethodB, testPaymentMethodC), testPaymentMethodC);
		createXmlRepresentationOfCustomerAndConfigureImport(customerDTO);
	}

	/**
	 * Create an XML representation with 3 payment methods but no default.
	 *
	 * @throws Exception in case of error
	 */
	@And("^an import with payment methods A,B and C and no default payment method$")
	public void setupImportFileForCustomerWithPaymentMethodsAndNoDefault() throws Exception {
		CustomerDTO customerDTO = createCustomerDTOWithPaymentMethodsAndDefault(Arrays.asList(testPaymentMethodA,
				testPaymentMethodB, testPaymentMethodC), null);

		createXmlRepresentationOfCustomerAndConfigureImport(customerDTO);
	}

	/**
	 * Create an import configuration that should retain collections.
	 *
	 * @throws Exception in case of error
	 */
	@And("^an import configured with a RETAIN_COLLECTION collection strategy for payment methods$")
	public void setupImportFileForCustomerWithRetainCollectionForPaymentMethods() throws Exception {
		CustomerDTO customerDTO = createCustomerDTOWithPaymentMethodsAndDefault(Arrays.asList(testPaymentMethodA,
				testPaymentMethodB, testPaymentMethodC), null);

		createXmlRepresentationOfCustomerAndConfigureImport(customerDTO);
		ImporterConfiguration importerConfiguration = importConfiguration.getImporterConfiguration(JobType.CUSTOMER);

		HashMap<DependentElementType, DependentElementConfiguration> dependentElementTypeMap =
				new HashMap<>();
		DependentElementConfiguration dependentElementConfiguration = new DependentElementConfiguration();
		dependentElementConfiguration.setDependentElementType(DependentElementType.PAYMENT_METHODS);
		dependentElementConfiguration.setCollectionStrategyType(CollectionStrategyType.RETAIN_COLLECTION);
		dependentElementTypeMap.put(DependentElementType.PAYMENT_METHODS, dependentElementConfiguration);

		importerConfiguration.setDependentElementMap(dependentElementTypeMap);
	}

	/**
	 * Setup an XML representation of a customer with no payment methods.
	 *
	 * @throws Exception in case of error
	 */
	@And("^an import with an empty collection of payment methods$")
	public void setupImportFilerForCustomerWithNoPaymentMethods() throws Exception {
		CustomerDTO customerDTO = createCustomerDTOWithPaymentMethodsAndDefault(Collections.emptyList(), null);

		createXmlRepresentationOfCustomerAndConfigureImport(customerDTO);
	}

	/**
	 * Execute the import.
	 *
	 * @throws ConfigurationException if an error occurs during export configuration
	 */
	@When("^I execute the import$")
	public void executeImport() throws ConfigurationException {
		importController.loadConfiguration(importConfiguration);
		summary = importController.executeImport();
	}

	/**
	 * Ensure the customer has the expected payment methods and default.
	 */
	@Then("^the customer will be updated with payment methods A,B and C and C is chosen as the default$")
	public void ensureImportedCustomerHasListOfPaymentMethodsWithDefaultSelected() {
		PaymentMethod paymentMethod = transformToPaymentMethod(testPaymentMethodA);
		PaymentMethod paymentMethod2 = transformToPaymentMethod(testPaymentMethodB);
		PaymentMethod paymentMethod3 = transformToPaymentMethod(testPaymentMethodC);

		Customer persistedCustomer = getCustomerUpdated();

		new CustomerPaymentMethodsValidator(persistedCustomer)
				.withPaymentMethods(paymentMethod, paymentMethod2, paymentMethod3)
				.withDefaultPaymentMethod(paymentMethod3)
				.validate();
	}

	/**
	 * Ensure the customer has the expected payment methods and default.
	 */
	@Then("^the customer will be updated with payment methods A,B, and C and A will be chosen as the default$")
	public void ensureImportedCustomerHasListOfPaymentMethodsWithFirstPaymentMethodSelectedAsDefault() {
		PaymentMethod paymentMethod = transformToPaymentMethod(testPaymentMethodA);
		PaymentMethod paymentMethod2 = transformToPaymentMethod(testPaymentMethodB);
		PaymentMethod paymentMethod3 = transformToPaymentMethod(testPaymentMethodC);

		Customer persistedCustomer = getCustomerUpdated();

		new CustomerPaymentMethodsValidator(persistedCustomer)
				.withPaymentMethods(paymentMethod, paymentMethod2, paymentMethod3)
				.withDefaultPaymentMethod(paymentMethod)
				.validate();
	}

	/**
	 * Ensure the customer has no payment methods.
	 */
	@Then("^the customer is updated and has no payment methods$")
	public void ensureImportedCustomerHasNoPaymentMethods() {
		Customer persistedCustomer = getCustomerUpdated();

		new CustomerPaymentMethodsValidator(persistedCustomer)
				.withPaymentMethods()
				.withDefaultPaymentMethod(null)
				.validate();
	}

	/**
	 * Ensure an unsupported operation throws an exception.
	 */
	@Then("^an unsupported operation exception is thrown$")
	public void ensureUnsupportedOperationExceptionIsThrown() {
		assertThat(summary.failuresExist())
				.as("The import should have failed.")
				.isTrue();

		List<Message> failures = summary.getFailures();

		assertThat(failures.size())
				.as("There should only be two failure messages")
				.isEqualTo(2);

		assertThat(failures.get(0).getException().getMessage())
				.contains("Only CLEAR_COLLECTION is currently supported");
	}

	private Customer getCustomerUpdated() {
		return customerService.findByGuid(TEST_GUID);
	}

	private Customer createPersistedCustomerWithPaymentMethods(final PaymentMethod... paymentMethods) {
		final Customer customer = customerBuilder.newInstance()
				.withGuid(TEST_GUID)
				.withFirstName(TEST_FIRST_NAME)
				.withLastName(TEST_LAST_NAME)
				.withEmail(TEST_EMAIL)
				.withStoreCode(storeService.findAllStores().get(0).getCode())
				.withPaymentMethods(paymentMethods)
				.build();

		return customerService.add(customer);
	}

	private CustomerDTO createCustomerDTOWithPaymentMethodsAndDefault(final List<PaymentMethodDto> paymentMethodDtos,
																	  final PaymentMethodDto defaultPaymentMethod) {
		return new CustomerDTOBuilder().withGuid(TEST_GUID)
				.withCreationDate(new Date())
				.withLastEditDate(new Date())
				.withStoreCode(storeService.findAllStores().get(0).getCode())
				.withUserId(TEST_USER_ID)
				.withPaymentMethods(paymentMethodDtos.toArray(new PaymentMethodDto[paymentMethodDtos.size()]))
				.withDefaultPaymentMethod(defaultPaymentMethod)
				.withPassword(TEST_PASSWORD)
				.withSalt(TEST_SALT)
				.build();
	}

	private PaymentMethod transformToPaymentMethod(final PaymentMethodDto paymentMethodDto) {
		if (paymentMethodDto instanceof PaymentTokenDto) {
			return paymentTokenDTOTransformer.transformToDomain((PaymentTokenDto) paymentMethodDto);
		} else {
			return null;
		}
	}

	private void createXmlRepresentationOfCustomerAndConfigureImport(final CustomerDTO customerDTO) throws Exception {
		testDomainMarshaller.marshall(customerDTO, JobType.CUSTOMER, customersFilePath + "/customers.xml");

		importConfiguration = new ImportConfiguration();
		HashMap<JobType, ImporterConfiguration> importerConfigurationMap = new HashMap<>();
		ImporterConfiguration importerConfiguration = new ImporterConfiguration();
		importerConfiguration.setImportStrategyType(ImportStrategyType.INSERT_OR_UPDATE);
		importerConfiguration.setJobType(JobType.CUSTOMER);

		PackagerConfiguration packagerConfiguration = new PackagerConfiguration();
		packagerConfiguration.setType(PackageType.NONE);

		RetrievalConfiguration retrievalConfiguration = new RetrievalConfiguration();
		retrievalConfiguration.setMethod(TransportType.FILE);
		retrievalConfiguration.setSource(customersFilePath);

		importerConfigurationMap.put(JobType.CUSTOMER, importerConfiguration);

		importConfiguration.setImporterConfigurationMap(importerConfigurationMap);
		importConfiguration.setXmlValidation(true);
		importConfiguration.setPackagerConfiguration(packagerConfiguration);
		importConfiguration.setRetrievalConfiguration(retrievalConfiguration);
	}

}
