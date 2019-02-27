/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.service.shoppingcart.impl;

import java.util.Collection;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.sellingchannel.director.CartDirector;

/**
 * Test case for {@link ShoppingCartMergerImpl}.
 */
@SuppressWarnings("PMD.NonStaticInitializer")
public class ShoppingCartMergerImplTest {

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private final ShoppingCartMergerImpl shoppingCartMerger = new ShoppingCartMergerImpl();

	private CartDirector cartDirector;

	/** Test initialization. */
	@Before
	public void setUp() {
		cartDirector = context.mock(CartDirector.class);
		shoppingCartMerger.setCartDirector(cartDirector);
	}

	/** When merging carts, the cmclient user ID and ip address should also be merged. */
	@Test
	@SuppressWarnings({"unchecked", "PMD.AvoidUsingHardCodedIP"})
	public void testMergeCmUserIdAndIpAddress() {
		final ShoppingCart donor = context.mock(ShoppingCart.class, "donorCart");
		final ShoppingCart recipient = context.mock(ShoppingCart.class, "recipientCart");

		final Long cmuserid = 4414L;

		context.checking(new Expectations() {
			{
				allowing(donor).getRootShoppingItems();
				allowing(donor).getNumItems();
				allowing(donor).getPromotionCodes();
				allowing(donor).getAppliedGiftCertificates();
				allowing(recipient).getRootShoppingItems();
				allowing(recipient).getNumItems();

				allowing(donor).getCmUserUID();
				will(returnValue(cmuserid));
				oneOf(recipient).setCmUserUID(cmuserid);

				allowing(recipient).applyPromotionCodes(with(any(Collection.class)));
			}
		});

		shoppingCartMerger.merge(recipient, donor);
	}

}
