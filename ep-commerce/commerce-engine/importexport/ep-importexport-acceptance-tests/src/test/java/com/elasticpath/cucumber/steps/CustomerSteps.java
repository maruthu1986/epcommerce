/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.cucumber.steps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.elasticpath.cucumber.testexecutionlisteners.CucumberDatabaseTestExecutionListener;
import com.elasticpath.cucumber.testexecutionlisteners.CucumberJmsRegistrationTestExecutionListener;
import com.elasticpath.domain.builder.customer.CustomerBuilder;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerGroup;
import com.elasticpath.service.customer.CustomerGroupService;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.service.store.StoreService;

/**
 * Customer steps.
 */
//@ContextConfiguration("/integration-context-mocked-customer-service.xml")
@ContextConfiguration("/integration-context-with-customer-service.xml")
@TestExecutionListeners({
		CucumberJmsRegistrationTestExecutionListener.class,
		CucumberDatabaseTestExecutionListener.class,
		DependencyInjectionTestExecutionListener.class
})
public class CustomerSteps {

	@Autowired
	private CustomerService customerService;

	@Autowired
	private CustomerGroupService customerGroupService;

	@Autowired
	private StoreService storeService;

	@Autowired
	private CustomerBuilder customerBuilder;

	private static Map<String, Customer> persistedCustomers = new HashMap<>();


	/**
	 * Clear all created customers.
	 */
	@Given("^there is no existing customer in the system$")
	public void clearAllCreatedCustomers() {
		persistedCustomers.clear();
	}

	/**
	 * Creates a customer.
	 *
	 * @param customerFirstName the customer first name
	 * @param customerLastName  the customer last name
	 * @param unused            the unused
	 */
	@Given("^the customer \\[(\\w+) (\\w+)\\] has been created( with no .*)?$")
	public void createCustomer(final String customerFirstName, final String customerLastName, final String unused) {
		addCustomer(customerFirstName, customerLastName, Collections.<String>emptyList());
	}

	/**
	 * Creates a customer with customer groups.
	 *
	 * @param customerFirstName  the customer first name
	 * @param customerLastName   the customer last name
	 * @param customerGroupNames the customer group names
	 */
	@Given("^the customer \\[(\\w+) (\\w+)\\] has been created and assigned customer segments? \\[([A-Z0-9_,]+)\\]$")
	public void createCustomerWithCustomerGroups(
			final String customerFirstName, final String customerLastName, final String customerGroupNames) {
		addCustomer(customerFirstName, customerLastName, Arrays.asList(customerGroupNames.split(",")));
	}

	private void addCustomer(final String customerFirstName, final String customerLastName, final List<String> customerGroupNames) {
		final String customerGuid = generateCustomerGuidFromName(customerFirstName, customerLastName);
		final List<CustomerGroup> customerGroups = new ArrayList<>();
		for (String customerGroupName : customerGroupNames) {
			customerGroups.add(customerGroupService.findByGroupName(customerGroupName));
		}

		final Customer customer = customerBuilder.newInstance()
				.withGuid(customerGuid)
				.withFirstName(customerFirstName)
				.withLastName(customerLastName)
				.withEmail(customerFirstName + "." + customerLastName + "@email.com")
				.withStoreCode(storeService.findAllStores().get(0).getCode())
				.withCustomerGroups(customerGroups.toArray(new CustomerGroup[customerGroups.size()]))
				.build();

		Customer cust = customerService.add(customer);
		persistedCustomers.put(cust.getGuid(), cust);
	}

	/**
	 * Adding a customer.
	 * @param customer the customer.
	 * @param customerService the customerService.
	 */

	public static void addCustomer(final Customer customer, final CustomerService customerService) {
		Customer cust = customerService.add(customer);
		persistedCustomers.put(cust.getGuid(), cust);
	}

	/**
	 * Ensure customer not assigned to groups.
	 *
	 * @param customerFirstName the customer first name
	 * @param customerLastName  the customer last name
	 */
	@Then("the customer \\[(\\w+) (\\w+)\\] should not be assigned to any customer segment$")
	public void ensureCustomerNotAssignedToGroups(final String customerFirstName, final String customerLastName) {
		validateCustomerAssignedToGroups(customerFirstName, customerLastName, Collections.<String>emptyList());
	}

	/**
	 * Ensure customer assigned to groups.
	 *
	 * @param customerFirstName  the customer first name
	 * @param customerLastName   the customer last name
	 * @param customerGroupNames the customer group names
	 */
	@Then("the customer \\[(\\w+) (\\w+)\\] should be assigned to customer segments? \\[([A-Z0-9_,]+)\\]$")
	public void ensureCustomerAssignedToGroups(final String customerFirstName, final String customerLastName,
											   final String customerGroupNames) {
		validateCustomerAssignedToGroups(customerFirstName, customerLastName, Arrays.asList(customerGroupNames.split(",")));
	}

	private void validateCustomerAssignedToGroups(final String customerFirstName, final String customerLastName,
												  final List<String> customerGroupNames) {

		final String customerGuid = generateCustomerGuidFromName(customerFirstName, customerLastName);
		final Customer persistedCustomer = findPersistedCustomerByGuid(customerGuid, customerService);

		final Collection<String> persistedCustomerGroupNames =
				Collections2.transform(persistedCustomer.getCustomerGroups(), new Function<CustomerGroup, String>() {
					@Override
					public String apply(final CustomerGroup customerGroup) {
						return customerGroup.getName();
					}
				});

		Assert.assertTrue(
				String.format("Customer [%s %s] was not assigned the correct customer groups %s",
						customerFirstName, customerLastName, persistedCustomerGroupNames),
				CollectionUtils.isEqualCollection(customerGroupNames, persistedCustomerGroupNames));
	}

	/**
	 * Update an existing customer.
	 *
	 * @param customer the customer
	 * @param customerService the customerService
	 */
	public static void updateCustomer(final Customer customer, final CustomerService customerService) {
		Customer cust = customerService.update(customer);

		persistedCustomers.put(cust.getGuid(), cust);
	}

	/**
	 * Generate customer guid from name.
	 *
	 * @param firstName the first name
	 * @param lastName  the last name
	 * @return the string
	 */
	public static String generateCustomerGuidFromName(final String firstName, final String lastName) {
		return String.format("guid_%s_%s", firstName, lastName);
	}

	/**
	 * Find the persisted customer by guid.
	 *
	 * @param guid the guid
	 * @param customerService the customerService
	 * @return the customer
	 */
	public static Customer findPersistedCustomerByGuid(final String guid, final CustomerService customerService) {
		final Customer foundCustomer = customerService.findByGuid(guid);

		return foundCustomer;
	}

	/**
	 * Gets the persisted customers.
	 *
	 * @return the persisted customers
	 */
	public static List<Customer> getPersistedCustomers() {
		return new ArrayList<>(persistedCustomers.values());
	}
}
