/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.uat.stepdefs;

import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import org.apache.commons.lang.LocaleUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.domain.builder.customer.CustomerBuilder;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.service.customer.CustomerRegistrationService;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.uat.ScenarioContextValueHolder;

/**
 * Step Definitions for Customer-based functionality.
 */
public class CustomerStepDefinitions {

	@Autowired
	private ScenarioContextValueHolder<CustomerBuilder> customerBuilderHolder;

	@Autowired
	private CustomerBuilder customerBuilder;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private CustomerRegistrationService customerRegistrationService;

	@Autowired
	private ScenarioContextValueHolder<Runnable> emailSendingCommandHolder;

	@Before(order = 1100)
	public void initialiseCustomerBuilderHolder() {
		customerBuilderHolder.set(customerBuilder);
	}

	@Given("^(?:the customer's|my) shopping locale is (.+)$")
	public void setCustomerLocale(final String localeStr) {
		customerBuilderHolder.set(customerBuilderHolder.get().withPreferredLocale(LocaleUtils.toLocale(localeStr)));
	}

	@Given("^(?:the customer's|my) email address (.+) has been registered in (?:my|their) user account$")
	public void setCustomerEmailAddress(final String customerEmailAddress) {
		customerBuilderHolder.set(customerBuilderHolder.get().withEmail(customerEmailAddress));
	}

	@When("^(?:I|(?:a|the) customer) registers? as a new user with email address (.+)$")
	public void registerNewCustomer(final String emailAddress) throws Exception {
		// Defer execution until we are ready to check for the email
		emailSendingCommandHolder.set(() -> {
			setCustomerEmailAddress(emailAddress);
			final Customer customer = customerBuilderHolder.get().build();
			customerService.addByAuthenticate(customer, false);
		});
	}

	@When("^an anonymous customer registers as a new user with email address (.+)$")
	public void registerAnonymousCustomer(final String emailAddress) throws Exception {
		// Defer execution until we are ready to check for the email
		emailSendingCommandHolder.set(() -> {
			setCustomerEmailAddress(emailAddress);
			final Customer customer = customerBuilderHolder.get().build();
			customerRegistrationService.registerCustomer(customer);
		});
	}

	@When("^a CSR creates a new customer account with email address (.+)")
	public void registerCustomerFromCsr(final String emailAddress) throws Throwable {
		// Defer execution until we are ready to check for the email
		emailSendingCommandHolder.set(() -> {
			setCustomerEmailAddress(emailAddress);
			final Customer customer = customerBuilderHolder.get().build();
			customerRegistrationService.registerCustomerAndSendPassword(customer);
		});
	}

	@When("^(?:the customer|I) changes? (?:my|their) password$")
	public void changeCustomerPassword() throws Throwable {
		// Defer execution until we are ready to check for the email
		emailSendingCommandHolder.set(() -> {
			final Customer customer = customerBuilderHolder.get().build();
			customerService.changePasswordAndSendEmail(customer, "NEWPASSWORD");
		});
	}

	@When("^the customer's password is reset$")
	public void resetCustomerPassword() throws Throwable {
		// Defer execution until we are ready to check for the email
		emailSendingCommandHolder.set(() -> {
			final Customer customer = customerBuilderHolder.get().build();
			customerService.auditableResetPassword(customer);
		});
	}

}
