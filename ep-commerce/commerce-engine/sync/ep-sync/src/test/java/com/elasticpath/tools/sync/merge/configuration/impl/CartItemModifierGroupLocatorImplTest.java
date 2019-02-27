/*
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.tools.sync.merge.configuration.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.cartmodifier.CartItemModifierGroup;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.cartitemmodifier.CartItemModifierService;

/**
 * Tests for {@link CartItemModifierGroupLocatorImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CartItemModifierGroupLocatorImplTest {

	public static final String CART_ITEM_MODIFIER_GROUP_GUID = "testGroupGuid";
	public static final String NON_EXIST_CART_ITEM_MODIFIER_GROUP_GUID = "testNonExistGroupGuid";

	@Mock
	private CartItemModifierGroup mockCartItemModifierGroup;

	@Mock
	private CartItemModifierService mockCartItemModifierService;

	private CartItemModifierGroupLocatorImpl target;

	@Before
	public void setUp() {

		target = new CartItemModifierGroupLocatorImpl();
		target.setCartItemModifierService(mockCartItemModifierService);

		given(mockCartItemModifierService.findCartItemModifierGroupByCode(CART_ITEM_MODIFIER_GROUP_GUID))
				.willReturn(mockCartItemModifierGroup);
		given(mockCartItemModifierService.findCartItemModifierGroupByCode(NON_EXIST_CART_ITEM_MODIFIER_GROUP_GUID)).willReturn(null);

	}

	@Test
	public void testLocatePersistence() {

		// when
		Persistable result = target.locatePersistence(CART_ITEM_MODIFIER_GROUP_GUID, CartItemModifierGroup.class);

		// verify
		assertEquals(mockCartItemModifierGroup, result);
	}

	@Test
	public void testLocatePersistenceWithNonExistGuid() {

		// when
		Persistable result = target.locatePersistence(NON_EXIST_CART_ITEM_MODIFIER_GROUP_GUID, CartItemModifierGroup.class);

		// verify
		assertNull(result);
	}

	@Test
	public void testIsResponsibleFor() {

		// when
		boolean result1 = target.isResponsibleFor(CartItemModifierGroup.class);
		boolean result2 = target.isResponsibleFor(Object.class);

		// verify
		assertTrue(result1);
		assertFalse(result2);

	}

}
