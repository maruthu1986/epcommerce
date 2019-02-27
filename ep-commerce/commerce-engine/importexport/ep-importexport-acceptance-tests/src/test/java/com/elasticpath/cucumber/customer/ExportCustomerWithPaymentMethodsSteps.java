/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.cucumber.customer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.common.dto.customer.CustomerDTO;
import com.elasticpath.common.dto.customer.PaymentMethodDto;
import com.elasticpath.common.dto.customer.transformer.PaymentTokenDTOTransformer;
import com.elasticpath.domain.builder.customer.CustomerBuilder;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.PaymentToken;
import com.elasticpath.importexport.builder.ExportConfigurationBuilder;
import com.elasticpath.importexport.common.dto.customer.CustomersDTO;
import com.elasticpath.importexport.common.exception.ConfigurationException;
import com.elasticpath.importexport.common.factory.TestPaymentMethodBuilderFactory;
import com.elasticpath.importexport.common.marshalling.XMLUnmarshaller;
import com.elasticpath.importexport.common.types.RequiredJobType;
import com.elasticpath.importexport.exporter.configuration.ExportConfiguration;
import com.elasticpath.importexport.exporter.configuration.search.SearchConfiguration;
import com.elasticpath.importexport.exporter.controller.ExportController;
import com.elasticpath.plugin.payment.dto.PaymentMethod;
import com.elasticpath.ql.parser.EPQueryType;
import com.elasticpath.search.searchengine.EpQLSearchEngine;
import com.elasticpath.search.searchengine.SolrIndexSearchResult;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.service.store.StoreService;

/**
 * Steps for the export customer with payment methods feature.
 */
public class ExportCustomerWithPaymentMethodsSteps {
	private static final String EXISTING_CUSTOMER_EPQL_QUERY = "FIND Customer WHERE GUID = ";
	private static final String TEST_GUID = "testGuid";
	private static final String TEST_TOKEN_IDENTITY1 = "testTokenIdentity1";
	private static final String TEST_TOKEN_IDENTITY2 = "testTokenIdentity2";
	private static final String EXPORT_DIRECTORY_PATH = "target/test-classes/export/ExportCustomerWithPaymentMethodsSteps";
	private static final String TEST_USER_ID = "testUser@email.com";
	private static final String TEST_EMAIL = TEST_USER_ID;
	private static final String TEST_FIRST_NAME = "testFirstName";
	private static final String TEST_LAST_NAME = "testLastName";

	@Autowired
	private ExportController exportController;

	@Autowired
	private EpQLSearchEngine epQLSearchEngine;

	@Autowired
	private CustomerBuilder customerBuilder;

	@Autowired
	private StoreService storeService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private TestPaymentMethodBuilderFactory testPaymentMethodBuilderFactory;

	@Autowired
	private PaymentTokenDTOTransformer paymentTokenDTOTransformer;

	private final XMLUnmarshaller xmlUnmarshaller = new XMLUnmarshaller(CustomersDTO.class);

	private Customer existingCustomer;
	private PaymentMethod paymentMethodA;
	private PaymentMethod paymentMethodB;

	/**
	 * Create a customer with 3 payment methods.
	 */
	@Given("^a customer exists with payment methods A and B and A is the default$")
	public void createACustomerWithPaymentMethods() {
		paymentMethodA = testPaymentMethodBuilderFactory.createPaymentTokenBuilderWithIdentity(TEST_TOKEN_IDENTITY1).build();
		paymentMethodB = testPaymentMethodBuilderFactory.createPaymentTokenBuilderWithIdentity(TEST_TOKEN_IDENTITY2).build();

		existingCustomer = createPersistedCustomerWithPaymentMethods(paymentMethodA, paymentMethodB);
		existingCustomer.getPaymentMethods().setDefault(paymentMethodA);
	}

	/**
	 * Create a customer without payment methods.
	 */
	@Given("^a customer exists with no payment methods$")
	public void createACustomerWithNoPaymentMethods() {
		existingCustomer = createPersistedCustomerWithPaymentMethods();
	}

	/**
	 * Export a customer using a card number filter.
	 *
	 * @param cardNumberFilterValue the filter to use
	 * @throws Exception in case of error
	 */
	@When("^I export the customer with CARD_NUMBER_FILTER set to (\\w+)$")
	public void executeExportWithCardNumberFilterValue(final String cardNumberFilterValue) throws Exception {
		configureAndExportCustomerWithCardNumberFilterValue(cardNumberFilterValue);
	}

	/**
	 * Export a customer.
	 *
	 * @throws Exception in case of error
	 */
	@When("^I export the customer$")
	public void executeExport() throws Exception {
		configureAndExportCustomerWithCardNumberFilterValue("STATIC");
	}

	private void configureAndExportCustomerWithCardNumberFilterValue(final String cardNumberFilterValue) throws IOException, ConfigurationException {
		final String epQLSearchQuery = EXISTING_CUSTOMER_EPQL_QUERY + existingCustomer.getGuid();

		List<Long> customerUids = Collections.singletonList(existingCustomer.getUidPk());
		SolrIndexSearchResult<Long> customerQuery = new SolrIndexSearchResult<>();
		customerQuery.setEpQueryType(EPQueryType.CUSTOMER);
		customerQuery.setResultUids(customerUids);

		when(epQLSearchEngine.<Long>search(epQLSearchQuery)).thenReturn(customerQuery);

		ExportConfiguration exportConfiguration =
				ExportConfigurationBuilder.newInstance()
						.setDeliveryTarget(EXPORT_DIRECTORY_PATH)
						.setExporterTypes(Collections.singletonList(RequiredJobType.CUSTOMER))
						.addExporterOption(RequiredJobType.CUSTOMER, "CARD_NUMBER_FILTER", cardNumberFilterValue)
						.build();

		SearchConfiguration searchConfiguration = new SearchConfiguration();
		searchConfiguration.setEpQLQuery(epQLSearchQuery);

		exportController.loadConfiguration(exportConfiguration, searchConfiguration);
		exportController.executeExport();
	}

	/**
	 * Ensure the exported customer has the expected payment methods and default.
	 *
	 * @throws Exception in case of error
	 */
	@Then("^the exported customer will have payment methods A and B and A is the default$")
	public void ensureExportedCustomerDTOHasPaymentMethodsWithCorrectDefaultSelected() throws Exception {
		CustomersDTO customerDtosMarshalled = getCustomerDtosMarshalled();

		assertThat(customerDtosMarshalled.getCustomers())
				.as("At least 1 customer should have been marshalled")
				.isNotEmpty();

		CustomerDTO customerDTO = customerDtosMarshalled.getCustomers().get(0);

		assertThat(customerDTO.getDefaultPaymentMethod().getPaymentMethod())
				.isEqualTo(transformToPaymentMethodDto(paymentMethodA));

		assertThat(customerDTO.getPaymentMethods())
				.containsExactlyInAnyOrder(transformToPaymentMethodDto(paymentMethodA), transformToPaymentMethodDto(paymentMethodB));
	}

	/**
	 * Ensure the exported customer record doesn't include real credit card numbers.
	 *
	 * @throws Exception in case of error
	 */
	@Then("^the exported customer will contain credit cards A and B without the real credit card numbers$")
	public void ensureRealCreditCardNumbersNotExported() throws Exception {
		CustomersDTO customerDtosMarshalled = getCustomerDtosMarshalled();

		assertThat(customerDtosMarshalled.getCustomers())
				.as("At least 1 customer should have been marshalled")
				.isNotEmpty();

		CustomerDTO customerDTO = customerDtosMarshalled.getCustomers().get(0);

		assertThat(customerDTO.getDefaultPaymentMethod().getPaymentMethod())
				.isEqualTo(transformToPaymentMethodDto(paymentMethodA));

		assertThat(customerDTO.getPaymentMethods())
				.containsExactlyInAnyOrder(transformToPaymentMethodDto(paymentMethodA), transformToPaymentMethodDto(paymentMethodB));
	}

	/**
	 * Ensure that the exported customer record has no payment methods.
	 *
	 * @throws Exception in case of error
	 */
	@Then("^the exported customer will have no payment methods$")
	public void ensureExportedCustomerDTOHasNoPaymentMethods() throws Exception {
		CustomersDTO customerDtosMarshalled = getCustomerDtosMarshalled();

		assertThat(customerDtosMarshalled.getCustomers())
				.as("At least 1 customer should have been marshalled")
				.isNotEmpty();

		assertThat(customerDtosMarshalled.getCustomers().get(0).getPaymentMethods())
				.isEmpty();
	}

	private PaymentMethodDto transformToPaymentMethodDto(final PaymentMethod paymentMethod) {
		if (paymentMethod instanceof PaymentToken) {
			return paymentTokenDTOTransformer.transformToDto((PaymentToken) paymentMethod);
		} else {
			return null;
		}
	}

	private CustomersDTO getCustomerDtosMarshalled() throws Exception {
		FileInputStream exportedCustomersFile = new FileInputStream(EXPORT_DIRECTORY_PATH + "/customers.xml");
		return xmlUnmarshaller.unmarshall(exportedCustomersFile);
	}

	private Customer createPersistedCustomerWithPaymentMethods(final PaymentMethod... paymentMethods) {
		final Customer customer = customerBuilder.newInstance()
				.withGuid(TEST_GUID)
				.withFirstName(TEST_FIRST_NAME)
				.withLastName(TEST_LAST_NAME)
				.withEmail(TEST_EMAIL)
				.withStoreCode(storeService.findAllStores().get(0).getCode())
				.withPaymentMethods(paymentMethods)
				.withDefaultToken(paymentMethods != null && paymentMethods.length > 0 ? Arrays.asList(paymentMethods).get(0) : null)
				.build();

		return customerService.add(customer);
	}
}
