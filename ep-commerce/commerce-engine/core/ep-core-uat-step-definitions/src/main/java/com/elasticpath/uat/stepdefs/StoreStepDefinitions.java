/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.uat.stepdefs;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.assertThat;

import java.util.Locale;

import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import org.apache.commons.lang.LocaleUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.domain.builder.customer.CustomerBuilder;
import com.elasticpath.domain.store.Store;
import com.elasticpath.test.persister.TestApplicationContext;
import com.elasticpath.test.persister.testscenarios.SimpleStoreScenario;
import com.elasticpath.uat.ScenarioContextValueHolder;

/**
 * Step Definitions for Store-based functionality.
 */
public class StoreStepDefinitions {

	@Autowired
	private ScenarioContextValueHolder<SimpleStoreScenario> storeScenarioHolder;

	@Autowired
	private TestApplicationContext tac;

	@Autowired
	private ScenarioContextValueHolder<CustomerBuilder> customerBuilderHolder;

	@Before(order = 1000)
	public void setUp() {
		storeScenarioHolder.set(tac.useScenario(SimpleStoreScenario.class));
	}

	@Given("^(?:the customer is|I am) (?:shopping in|a customer of|an administrator of) Store \"([^\"]*)\"$")
	public void assertUsingCorrectStore(final String storeCode) {
		final String actualStoreCode = storeScenarioHolder.get().getStore().getCode();

		// This is awful, but until SimpleStoreScenario#intitialize is reimplemented in a more flexible fashion (with builders, perhaps),
		// it's rather difficult to construct a test Store with a custom Store Code on demand.
		assertThat("Currently only the Store with code '" + actualStoreCode + "' is supported.  Sorry about that.",
				   actualStoreCode, startsWith(storeCode));

		customerBuilderHolder.set(customerBuilderHolder.get().withStoreCode(actualStoreCode));
	}

	@Given("^the Store supports the (.+) locale$")
	public void assertStoreSupportsLocale(final String localeStr) {
		final Locale locale = LocaleUtils.toLocale(localeStr);
		final Store store = storeScenarioHolder.get().getStore();
		assertThat("Locale " + localeStr + " not supported by store " + store.getName(),
				   store.getSupportedLocales(), contains(locale));
	}

	@And("^my email address (.+) is registered as the Store's Administrator email$")
	public void setStoreAdministratorEmailAddress(final String emailAddress) throws Throwable {
		storeScenarioHolder.get().getStore().setStoreAdminEmailAddress(emailAddress);
	}

}
