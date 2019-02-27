/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.cucumber.steps;


import static org.assertj.core.api.Assertions.assertThat;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import cucumber.api.DataTable;
import cucumber.api.java.en.Given;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.common.dto.datapolicy.CustomerConsentDTO;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.builder.datapolicy.CustomerConsentBuilder;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.datapolicy.ConsentAction;
import com.elasticpath.domain.datapolicy.CustomerConsent;
import com.elasticpath.domain.datapolicy.DataPolicy;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.service.datapolicy.CustomerConsentService;
import com.elasticpath.service.datapolicy.DataPolicyService;
import com.elasticpath.service.store.StoreService;

/**
 * Customer consents steps.
 */
public class CustomerConsentSteps {

	private static final String DATE_FORMAT = "YYYY-MM-DD hh:mm:ss";
	private static final String TEST_EMAIL = "test@elasticpath.com";

	@Autowired
	private CustomerConsentService customerConsentService;

	@Autowired
	private DataPolicyService dataPolicyService;

	@Autowired
	private CustomerConsentBuilder customerConsentBuilder;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private StoreService storeService;

	@Autowired
	private BeanFactory beanFactory;

	private Customer generatedCustomer;

	/**
	 * Setup the tests with customer.
	 *
	 * @param customerFirstName the customer first name
	 * @param customerLastName  the customer last name
	 */
	@Given("^the customer \\[(\\w+) (\\w+)\\] has been created for customer consent$")
	public void createCustomerForCustomerConsent(final String customerFirstName, final String customerLastName) {
		createPersistedAnonymousCustomer(customerFirstName, customerLastName);
	}

	/**
	 * Setup the tests with customer consents.
	 *
	 * @param dataTable customer consent info.
	 * @throws ParseException in case of date parsing error.
	 */
	@Given("^the existing customer consents of$")
	public void setUpCustomerConsents(final DataTable dataTable) throws ParseException {
		final List<Map<String, String>> customerConsentsMap = dataTable.asMaps(String.class, String.class);
		final List<CustomerConsent> customerConsents = parseCustomerConsentsFromDataTable(customerConsentsMap,
				dataPolicyService, customerConsentBuilder);

		for (CustomerConsent customerConsent : customerConsents) {
			customerConsentService.save(customerConsent);
		}

		assertThat(customerConsentService.list())
				.isNotNull()
				.hasSize(customerConsentsMap.size());
	}

	/**
	 * Create customer consent list from a data table.
	 *
	 * @param customerConsentsMap customer consent data table.
	 * @param dataPolicyService data policy service.
	 * @param customerConsentBuilder customer consent builder.
	 * @return list of customer consents.
	 * @throws ParseException in case of date parsing error.
	 */
	public static List<CustomerConsent> parseCustomerConsentsFromDataTable(final List<Map<String, String>> customerConsentsMap,
				final DataPolicyService dataPolicyService, final CustomerConsentBuilder customerConsentBuilder) throws ParseException {

		final List<CustomerConsent> customerConsents = new ArrayList<>();
		for (Map<String, String> properties : customerConsentsMap) {
			DataPolicy dataPolicy = dataPolicyService.findByGuid(properties.get("dataPolicyGuid"));

			final CustomerConsent customerConsent = customerConsentBuilder.newInstance()
					.withGuid(properties.get("guid"))
					.withDataPolicy(dataPolicy)
					.withAction(ConsentAction.valueOf(properties.get("action")))
					.withConsentDate(getDate(properties.get("consentDate")))
					.withCustomerGuid(
							CustomerSteps.generateCustomerGuidFromName(properties.get("customerFirstName"), properties.get("customerLastName")))
					.build();

			customerConsents.add(customerConsent);
		}

		return customerConsents;
	}

	private void createPersistedAnonymousCustomer(final String customerFirstName, final String customerLastName) {
		final Customer anonymousCustomer = beanFactory.getBean(ContextIdNames.CUSTOMER);
		anonymousCustomer.setGuid(CustomerSteps.generateCustomerGuidFromName(customerFirstName, customerLastName));
		anonymousCustomer.setEmail(TEST_EMAIL);
		anonymousCustomer.setStoreCode(storeService.findAllStores().get(0).getCode());
		generatedCustomer = customerService.add(anonymousCustomer);

		assertThat(generatedCustomer)
				.isNotNull();
	}

	/**
	 * Create customer consent DTO list from a data table.
	 *
	 * @param customerConsentsMap customer consent data table.
	 * @return list of customer consent DTOs.
	 * @throws ParseException in case of date parsing error.
	 */
	public static List<CustomerConsentDTO> getCustomerConsentsFromDataTable(final List<Map<String, String>> customerConsentsMap) throws
			ParseException {
		List<CustomerConsentDTO> customerConsentDTOs = new ArrayList<>();

		for (Map<String, String> properties : customerConsentsMap) {
			final CustomerConsentDTO customerConsent = new CustomerConsentDTO();

			customerConsent.setGuid(properties.get("guid"));
			customerConsent.setDataPolicyGuid(properties.get("dataPolicyGuid"));
			customerConsent.setAction(properties.get("action"));
			customerConsent.setConsentDate(CustomerConsentSteps.getDate(properties.get("consentDate")));
			customerConsent.setCustomerGuid(
					CustomerSteps.generateCustomerGuidFromName(properties.get("customerFirstName"), properties.get("customerLastName")));

			customerConsentDTOs.add(customerConsent);
		}

		return customerConsentDTOs;
	}

	/**
	 * Format the date for testing.
	 *
	 * @param dateString date to convert.
	 * @return converted date.
	 * @throws ParseException in case of date parsing error.
	 */
	public static Date getDate(final String dateString) throws ParseException {
		final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
		LocalDateTime localDateTime = LocalDateTime.parse(dateString, formatter);

		return Date.from(localDateTime.atZone(ZoneOffset.UTC).toInstant());
	}

}
