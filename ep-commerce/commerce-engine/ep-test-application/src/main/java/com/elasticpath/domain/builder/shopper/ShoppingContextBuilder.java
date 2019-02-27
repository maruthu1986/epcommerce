/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.domain.builder.shopper;

import java.util.Currency;
import java.util.Date;
import java.util.Locale;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.builder.DomainObjectBuilder;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.tags.Tag;
import com.elasticpath.tags.TagSet;
import com.elasticpath.test.util.Utils;

/**
 * A builder that builds {@link ShoppingContext}s for testing purposes.
 */
public class ShoppingContextBuilder implements DomainObjectBuilder<ShoppingContext> {

	private static final String SHOPPING_START_TIME_TAG = "SHOPPING_START_TIME";

	private final BeanFactory beanFactory;

	private Customer customer;
	private String storeCode;

	/**
	 * Constructor.
	 *
	 * @param beanFactory the bean factory
	 */
	public ShoppingContextBuilder(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * Sets the customer used within the Shopper.
	 *
	 * @param customer the customer
	 * @return the shopper builder
	 */
	public ShoppingContextBuilder withCustomer(final Customer customer) {
		this.customer = customer;
		return this;
	}

	/**
	 * Sets the store code for the Shopper.
	 *
	 * @param storeCode the store code
	 * @return the builder
	 */
	public ShoppingContextBuilder withStoreCode(final String storeCode) {
		this.storeCode = storeCode;
		return this;
	}

	@Override
	public ShoppingContext build() {
		final CustomerSession customerSession = buildCustomerSession();
		final Shopper shopper = buildShopper();

		shopper.updateTransientDataWith(customerSession);
		customerSession.setShopper(shopper);

		return new ShoppingContext(customerSession, shopper);
	}

	protected Shopper buildShopper() {
		final Shopper shopper = beanFactory.getBean(ContextIdNames.SHOPPER);
		shopper.setCustomer(customer);
		shopper.setStoreCode(storeCode);
		return shopper;
	}

	protected CustomerSession buildCustomerSession() {
		final CustomerSession customerSession = beanFactory.getBean(ContextIdNames.CUSTOMER_SESSION);

		customerSession.setCreationDate(new Date());
		customerSession.setCurrency(Currency.getInstance(Locale.US));
		customerSession.setLastAccessedDate(new Date());
		customerSession.setGuid(Utils.uniqueCode("session"));
		customerSession.setLocale(Locale.US);
		TagSet tagSet = new TagSet();
		tagSet.addTag(SHOPPING_START_TIME_TAG, new Tag(new Date().getTime()));
		customerSession.setCustomerTagSet(tagSet);

		return customerSession;
	}


}
