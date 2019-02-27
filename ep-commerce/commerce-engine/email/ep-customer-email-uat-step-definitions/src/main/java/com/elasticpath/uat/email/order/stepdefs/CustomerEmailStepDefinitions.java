/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.uat.email.order.stepdefs;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

import static com.elasticpath.uat.email.stepdefs.EmailStepDefinitions.getContents;
import static com.elasticpath.uat.email.stepdefs.EmailStepDefinitions.getEmailMessageBySubject;

import java.util.Map;
import javax.mail.Message;

import cucumber.api.java.en.Then;
import org.apache.velocity.tools.generic.DateTool;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.domain.builder.customer.CustomerBuilder;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.uat.ScenarioContextValueHolder;

/**
 * Step Definitions for customer email functionality.
 */
public class CustomerEmailStepDefinitions {

	private static final String CREATION_DATE_FORMAT_STRING = "MMMM d, yyyy";

	@Autowired
	private ScenarioContextValueHolder<Map<String, Message>> emailMessagesHolder;

	@Autowired
	private ScenarioContextValueHolder<CustomerBuilder> customerBuilderHolder;

	@Then("^the(?: \"(.+)\")? email should contain the customer account user ID")
	public void verifyEmailContainsCustomerAccountNumber(final String emailSubject) throws Exception {
		final String emailContents = getContents(getEmailMessageBySubject(emailSubject, emailMessagesHolder.get()));
		assertThat("The email contents should include the customer ID",
				   emailContents, containsString(getCustomer().getUserId()));
	}

	@Then("^the(?: \"(.+)\")? email should contain the customer account creation date$")
	public void verifyEmailContainsCustomerAccountCreationDate(final String emailSubject) throws Exception {
		final String emailContents = getContents(getEmailMessageBySubject(emailSubject, emailMessagesHolder.get()));
		assertThat("The email contents should include the customer creation date",
				   emailContents,
				   containsString(new DateTool().get(CREATION_DATE_FORMAT_STRING)));
	}

	@Then("^the(?: \"(.+)\")? email should contain the customer account email address$")
	public void verifyEmailContainsCustomerEmailAddress(final String emailSubject) throws Exception {
		final String emailContents = getContents(getEmailMessageBySubject(emailSubject, emailMessagesHolder.get()));
		assertThat("The email contents should include the customer email address",
				   emailContents, containsString(getCustomer().getEmail()));
	}

	@Then("^the(?: \"(.+)\")? email should contain my name$")
	public void verifyEmailContainsCustomerName(final String emailSubject) throws Exception {
		final String emailContents = getContents(getEmailMessageBySubject(emailSubject, emailMessagesHolder.get()));
		assertThat("The email contents should include the customer's first name",
				   emailContents, containsString(getCustomer().getFirstName()));
		assertThat("The email contents should include the customer's last name",
				   emailContents, containsString(getCustomer().getLastName()));
	}

	@Then("^the(?: \"(.+)\")? email should contain the new password$")
	public void verifyEmailContainsCustomerPassword(final String emailSubject) throws Exception {
		final String emailContents = getContents(getEmailMessageBySubject(emailSubject, emailMessagesHolder.get()));
		assertThat("The email contents should include the customer password",
				   emailContents, containsString(getCustomer().getClearTextPassword()));
	}

	private Customer getCustomer() {
		return customerBuilderHolder.get().build();
	}

}
