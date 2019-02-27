/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.test.integration.shipping;

import java.util.Currency;
import java.util.Locale;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.domain.builder.checkout.CheckoutTestCartBuilder;
import com.elasticpath.domain.builder.customer.CustomerBuilder;
import com.elasticpath.domain.builder.shopper.ShoppingContext;
import com.elasticpath.domain.builder.shopper.ShoppingContextBuilder;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.persister.Persister;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.test.db.DbTestCase;

/**
 * Base class for shipping tests.
 */
public abstract class AbstractShippingTestCase extends DbTestCase {

	@Autowired
	protected CheckoutTestCartBuilder checkoutTestCartBuilder;

	@Autowired
	protected CustomerBuilder customerBuilder;

	@Autowired
	protected ShoppingContextBuilder shoppingContextBuilder;

	@Autowired
	protected CustomerService customerService;

	@Autowired
	protected Persister<ShoppingContext> shoppingContextPersister;

	protected ShoppingContext shoppingContext;

	protected String storeCode;

	protected Locale locale;

	protected Currency currency;

	@Before
	public void setUpContext() {
		storeCode = scenario.getStore().getCode();
		Customer testCustomer = customerBuilder.withStoreCode(storeCode).build();
		customerService.add(testCustomer);

		shoppingContext = shoppingContextBuilder.withCustomer(testCustomer)
				.withStoreCode(storeCode)
				.build();
		shoppingContextPersister.persist(shoppingContext);

		checkoutTestCartBuilder.withScenario(scenario)
				.withCustomerSession(shoppingContext.getCustomerSession())
				.withTestDoubleGateway();

		final CustomerSession customerSession = shoppingContext.getCustomerSession();
		locale = customerSession.getLocale();
		currency = customerSession.getCurrency();
	}
}
