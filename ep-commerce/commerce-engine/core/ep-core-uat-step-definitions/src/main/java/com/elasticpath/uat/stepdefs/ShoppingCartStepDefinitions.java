/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.uat.stepdefs;

import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.elasticpath.domain.builder.checkout.CheckoutTestCartBuilder;
import com.elasticpath.domain.builder.customer.CustomerBuilder;
import com.elasticpath.test.persister.testscenarios.SimpleStoreScenario;
import com.elasticpath.uat.ScenarioContextValueHolder;

/**
 * Step Definitions for Shopping Cart-based functionality.
 */
public class ShoppingCartStepDefinitions {

	@Autowired
	private CheckoutTestCartBuilder checkoutTestCartBuilder;

	@Autowired
	@Qualifier("checkoutTestCartBuilderHolder")
	private ScenarioContextValueHolder<CheckoutTestCartBuilder> checkoutTestCartBuilderHolder;

	@Autowired
	private ScenarioContextValueHolder<SimpleStoreScenario> storeScenarioHolder;

	@Autowired
	private ScenarioContextValueHolder<CustomerBuilder> customerBuilderHolder;

	@Before
	public void initialiseCheckoutTestCartBuilderHolder() {
		checkoutTestCartBuilderHolder.set(checkoutTestCartBuilder.withScenario(storeScenarioHolder.get()));
	}

	@Given("^I have an item in my shopping cart$")
	public void addNonZeroPhysicalItemToCart() throws Exception {
		checkoutTestCartBuilderHolder.set(checkoutTestCartBuilder.withPhysicalProduct());
	}

	@Given("^I have added a Gift Certificate for \"(.+) <([^>]*)>\" to my shopping cart$")
	public void addGiftCertificateProductToCart(final String recipientName, final String recipientEmailAddress) throws Exception {
		final String senderName = customerBuilderHolder.get()
				.build()
				.getFullName();

		checkoutTestCartBuilderHolder.set(checkoutTestCartBuilder.withGiftCertificateProduct(senderName, recipientName, recipientEmailAddress));
	}

}
