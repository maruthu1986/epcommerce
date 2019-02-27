/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.pricing.impl;

import java.util.Currency;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.common.pricing.service.PriceListLookupService;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.pricing.PriceListStack;
import com.elasticpath.domain.pricing.impl.PriceListStackImpl;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.store.Store;
import com.elasticpath.tags.TagSet;

/**
 * Test.
 */
public class SessionPriceListLifecycleImplTest {

	private static final String CATALOG_CODE = "TEST_CATALOG";

	@Rule
	public JUnitRuleMockery context = new JUnitRuleMockery();

	@Mock
	private PriceListLookupService priceListLookupService;

	@Mock
	private Shopper shopper;

	@Mock
	private CustomerSession customerSession;

	@Mock
	private Store store;

	@Mock
	private Catalog catalog;

	private Currency currency;
	private TagSet tagSet;

	private SessionPriceListLifecycleImpl sessionPriceListLifecycle;

	@Before
	public void setUp() {
		sessionPriceListLifecycle = new SessionPriceListLifecycleImpl();
		sessionPriceListLifecycle.setPriceListLookupService(priceListLookupService);
		currency = Currency.getInstance("CAD");
		tagSet = new TagSet();
		context.checking(new Expectations() {
			{
				// Boiler plate setup for empty CE entities/parameters.
				allowing(store).getCatalog();
				will(returnValue(catalog));
				allowing(catalog).getCode();
				will(returnValue(CATALOG_CODE));
				allowing(customerSession).getShopper();
				will(returnValue(shopper));

				allowing(shopper).getCurrency();
				will(returnValue(currency));
				allowing(shopper).getTagSet();
				will(returnValue(tagSet));
			}
		});
	}

	@Test
	public void testRefreshPriceListStack() throws Exception {
		final PriceListStack priceListStack = new PriceListStackImpl();
		context.checking(new Expectations() {
			{
				allowing(shopper).isPriceListStackValid();
				will(returnValue(false));

				// Really all I want to check is that this collaborator
				// is called and that the price list stack is updated (see below).
				allowing(priceListLookupService).getPriceListStack(CATALOG_CODE, currency, tagSet);
				will(returnValue(priceListStack));

				// Then the price list on the session is updated.
				oneOf(shopper).setPriceListStack(priceListStack);
			}
		});

		// When the price list stack is refreshed.
		sessionPriceListLifecycle.refreshPriceListStack(customerSession, store);
		context.assertIsSatisfied();
	}

	@Test
	public void testNoRefreshWhenPriceListStackIsUpToDate() {
		final PriceListStack priceListStack = new PriceListStackImpl();
		context.checking(new Expectations() {
			{
				allowing(shopper).isPriceListStackValid();
				will(returnValue(true));

				never(priceListLookupService).getPriceListStack(
						with(any(String.class)), with(any(Currency.class)), with(any(TagSet.class)));
				never(shopper).setPriceListStack(priceListStack);
			}
		});
		sessionPriceListLifecycle.refreshPriceListStack(customerSession, store);
	}

	@Test
	public void testShopperRefreshPriceListStack() throws Exception {
		final PriceListStack priceListStack = new PriceListStackImpl();
		context.checking(new Expectations() {
			{
				allowing(shopper).isPriceListStackValid();
				will(returnValue(false));

				// Really all I want to check is that this collaborator
				// is called and that the price list stack is updated (see below).
				allowing(priceListLookupService).getPriceListStack(CATALOG_CODE, currency, tagSet);
				will(returnValue(priceListStack));

				// Then the price list on the session is updated.
				oneOf(shopper).setPriceListStack(priceListStack);
			}
		});

		// When the price list stack is refreshed.
		sessionPriceListLifecycle.refreshPriceListStack(shopper, CATALOG_CODE);
		context.assertIsSatisfied();
	}

	@Test
	public void testShopperNoRefreshWhenPriceListStackIsUpToDate() {
		final PriceListStack priceListStack = new PriceListStackImpl();
		context.checking(new Expectations() {
			{
				allowing(shopper).isPriceListStackValid();
				will(returnValue(true));

				never(priceListLookupService).getPriceListStack(
						with(any(String.class)), with(any(Currency.class)), with(any(TagSet.class)));
				never(shopper).setPriceListStack(priceListStack);
			}
		});
		sessionPriceListLifecycle.refreshPriceListStack(shopper, CATALOG_CODE);
	}
}
