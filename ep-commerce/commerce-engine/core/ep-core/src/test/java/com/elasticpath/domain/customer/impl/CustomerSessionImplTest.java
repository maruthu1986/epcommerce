/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.customer.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Currency;
import java.util.Locale;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.customer.TagSetInvalidationDeterminer;
import com.elasticpath.domain.pricing.PriceListStack;
import com.elasticpath.tags.Tag;
import com.elasticpath.tags.TagSet;
import com.elasticpath.test.factory.TestCustomerSessionFactory;

/**
 * Test <code>CustomerSessionImpl</code>. So far, there are only getters and setters for <code>CustomerSessionImpl</code>, so no tests have
 * been written for it.
 */
public class CustomerSessionImplTest {

	private static final String VALID_MESSAGE = "Price list stack should be valid after being set";

	private static final String TAG = "tag";
	private static final String INSTORE_SEARCH_TERMS_KEY = "INSTORE_SEARCH_TERMS";
	private static final String CATEGORIES_VISITED_KEY = "CATEGORIES_VISITED";
	private static final String CART_SUBTOTAL_KEY = "CART_SUBTOTAL";

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private TagSetInvalidationDeterminer priceListStackInvalidationDeterminer;
	private TagSetInvalidationDeterminer promoInvalidationDeterminer;

	/**
	 * Prepare for test.
	 */
	@Before
	public void setUp() {
		priceListStackInvalidationDeterminer = context.mock(TagSetInvalidationDeterminer.class, "priceListMock");
		promoInvalidationDeterminer = context.mock(TagSetInvalidationDeterminer.class, "promoMock");

		context.checking(new Expectations() { {
			//priceListStackInvalidationDeterminer expectations

			allowing(priceListStackInvalidationDeterminer).needInvalidate(with(CATEGORIES_VISITED_KEY));
			will(returnValue(false));

			allowing(priceListStackInvalidationDeterminer).needInvalidate(with(CART_SUBTOTAL_KEY));
			will(returnValue(false));

			allowing(priceListStackInvalidationDeterminer).needInvalidate(with(TAG));
			will(returnValue(true));

			allowing(priceListStackInvalidationDeterminer).needInvalidate(with(INSTORE_SEARCH_TERMS_KEY));
			will(returnValue(false));

			//promoInvalidationDeterminer expectations
			allowing(promoInvalidationDeterminer).needInvalidate(with(CATEGORIES_VISITED_KEY));
			will(returnValue(false));

			allowing(promoInvalidationDeterminer).needInvalidate(with(CART_SUBTOTAL_KEY));
			will(returnValue(false));

			allowing(promoInvalidationDeterminer).needInvalidate(with(TAG));
			will(returnValue(false));

			allowing(promoInvalidationDeterminer).needInvalidate(with(INSTORE_SEARCH_TERMS_KEY));
			will(returnValue(true));
		} });
	}

	private CustomerSession createNewCustomerSession() {
		CustomerSessionImpl customerSession = (CustomerSessionImpl) TestCustomerSessionFactory.getInstance().createNewCustomerSession();
		customerSession.setPriceListStackInvalidationDeterminer(priceListStackInvalidationDeterminer);
		customerSession.setPromoInvalidationDeterminer(promoInvalidationDeterminer);

		return customerSession;
	}

	/**
	 * Test that manipulations on the customer session object trigger price list stack
	 * flag changes.
	 * Expectations:
	 * 1. Initially when object is created the flag should be set to false (i.e. invalid stack)
	 * 2. setCurrency should invalidate price list stack since it is currency dependent
	 * 3. setPriceListStack should set flag to valid
	 * 4. TagSet changes should trigger invalid flag.
	 */
	@Test
	public void testInvalidationFlagForPriceListStack() {

		final CustomerSession customerSession = createNewCustomerSession();
		assertFalse("Price list stack should be invalid initially", customerSession.isPriceListStackValid());

		final PriceListStack pls = context.mock(PriceListStack.class);

		customerSession.setPriceListStack(pls);
		assertTrue(VALID_MESSAGE, customerSession.isPriceListStackValid());

		customerSession.setCurrency(Currency.getInstance(Locale.UK));

		assertFalse("Price list stack should be invalid if the currency changes", customerSession.isPriceListStackValid());

		// reset
		customerSession.setPriceListStack(pls);
		assertTrue(VALID_MESSAGE, customerSession.isPriceListStackValid());

		final TagSet tagSet = new TagSet();
		customerSession.setCustomerTagSet(tagSet);
		assertFalse("Price list stack should be invalid if the tag set changes", customerSession.isPriceListStackValid());

		// reset
		customerSession.setPriceListStack(pls);
		assertTrue(VALID_MESSAGE, customerSession.isPriceListStackValid());

		tagSet.addTag(TAG, new Tag(TAG));
		assertFalse("Price list stack should be invalid if the tag set changes", customerSession.isPriceListStackValid());

	}


	/**
	 * Test that two tag from PLA_SHOPPER dictionary can not invalidate the
	 * price list stack.
	 */
	@Test
	public void testSkipPriceListStackInvalidation() {

		final CustomerSession customerSession = createNewCustomerSession();
		assertFalse("Price list stack should be invalid initially", customerSession.isPriceListStackValid());

		final PriceListStack pls = context.mock(PriceListStack.class);

		customerSession.setPriceListStack(pls);
		assertTrue(VALID_MESSAGE, customerSession.isPriceListStackValid());

		customerSession.setCurrency(Currency.getInstance(Locale.UK));

		assertFalse("Price list stack should be invalid if the currency changes", customerSession.isPriceListStackValid());

		// reset
		customerSession.setPriceListStack(pls);
		assertTrue("Price list stack should be valid  after being set", customerSession.isPriceListStackValid());

		final TagSet tagSet = new TagSet();
		customerSession.setCustomerTagSet(tagSet);
		assertFalse("Price list stack should  be invalid if the tag set changes", customerSession.isPriceListStackValid());

		// reset
		customerSession.setPriceListStack(pls);
		assertTrue("Price  list stack should be valid after being set", customerSession.isPriceListStackValid());

		tagSet.addTag("CATEGORIES_VISITED", new Tag("1,2,3,4,57"));
		assertTrue(customerSession.isPriceListStackValid()); // Price list stack should be valid if the tag set changes

		tagSet.addTag("CART_SUBTOTAL", new Tag("120"));
		assertTrue(customerSession.isPriceListStackValid()); // Price list stack should be valid if the tag set changes


	}

	/**
	 * Test that validated that customer session as a listener to tag set is correctly
	 * add itself to and removes itself from the tag set.
	 */
	@Test
	public void testTagSetListeners() {

		final PriceListStack pls = context.mock(PriceListStack.class);
		final CustomerSession customerSession = createNewCustomerSession();

		assertFalse("Price list stack should be invalid on new session", customerSession.isPriceListStackValid());

		customerSession.setPriceListStack(pls);

		assertNotNull("Initially if tag set should be created automatically", customerSession.getCustomerTagSet());
		assertTrue("Price list stack should be valid if it was set", customerSession.isPriceListStackValid());
		customerSession.getCustomerTagSet().addTag(TAG, new Tag("change"));
		assertFalse("Price list stack is invalidated since the session is listening to tag set", customerSession.isPriceListStackValid());

		// reset
		customerSession.setPriceListStack(pls);
		assertTrue("Price list stack should be valid if it was set", customerSession.isPriceListStackValid());

		final TagSet initialTagSet = customerSession.getCustomerTagSet();
		final TagSet newTagSet = new TagSet();
		customerSession.setCustomerTagSet(newTagSet);
		assertFalse("Since tag set was reset the PLS should be invalid", customerSession.isPriceListStackValid());
		assertEquals("Tag set must be changed to set value", newTagSet, customerSession.getCustomerTagSet());
		// reset
		customerSession.setPriceListStack(pls);
		assertTrue("Price list stack should be valid if it was set", customerSession.isPriceListStackValid());
		initialTagSet.addTag("do change", new Tag("on initial tag set"));
		assertTrue("Price list stack should still be valid since we are not listening to initial tag set", customerSession.isPriceListStackValid());
		newTagSet.addTag(TAG, new Tag("on the new tag set assigned to session"));
		assertFalse("Price list stack should be invalidated whe tag set changes", customerSession.isPriceListStackValid());

	}

	@Test
	public void testSkipPriceListStackInvalidationWhenTagIsInstoreSearchTerm() {

		final CustomerSession customerSession = createNewCustomerSession();
		assertFalse("Price list stack should be invalid initially", customerSession.isPriceListStackValid());

		final PriceListStack pls = context.mock(PriceListStack.class);

		customerSession.setPriceListStack(pls);
		assertTrue(VALID_MESSAGE, customerSession.isPriceListStackValid());

		final TagSet tagSet = new TagSet();
		customerSession.setCustomerTagSet(tagSet);
		assertFalse("Price list stack should  be invalid if the tag set changes", customerSession.isPriceListStackValid());

		// reset
		customerSession.setPriceListStack(pls);
		assertTrue("Price  list stack should be valid after being set", customerSession.isPriceListStackValid());

		tagSet.addTag(INSTORE_SEARCH_TERMS_KEY, new Tag("ac/dc"));
		assertTrue("Price list stach should be valid for search tag", customerSession.isPriceListStackValid());

	}

}
