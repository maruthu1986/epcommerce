/*
 * Copyright (c) Elastic Path Software Inc., 2016
 */

package com.elasticpath.test.integration.cartitemmodifier;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.elasticpath.domain.cartmodifier.CartItemModifierField;
import com.elasticpath.domain.cartmodifier.CartItemModifierGroup;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.test.integration.DirtiesDatabase;

/**
 * Test that the cart item modifier is persisted correctly.
 */
public class CartItemModifierQueriesTest extends AbstractCartItemModifierTest {

	/**
	 * Tests all find by GUID methods.
	 */
	@DirtiesDatabase
	@Test
	public void testFindGroupByCode() {
		final ProductType productType = persistSimpleProductTypeWithCartItemModifierGroup();

		//Retrieve CartItemModifierGroup
		final Set<CartItemModifierGroup> cartItemModifierGroups = productType.getCartItemModifierGroups();
		final CartItemModifierGroup cartItemModifierGroup = cartItemModifierGroups.iterator().next();

		List<CartItemModifierGroup> result = getPersistenceEngine()
				.retrieveByNamedQuery("CART_ITEM_MODIFIER_GROUP_BY_CODE", cartItemModifierGroup.getGuid());
		assertNotNull("The query should not return a null", result);
		assertThat(result, contains(cartItemModifierGroup));
	}

	/**
	 * Tests all find by GUID methods.
	 */
	@DirtiesDatabase
	@Test
	public void testFindFieldByCode() {
		final ProductType productType = persistSimpleProductTypeWithCartItemModifierGroup();

		//Retrieve CartItemModifierGroup
		final Set<CartItemModifierGroup> cartItemModifierGroups = productType.getCartItemModifierGroups();
		final CartItemModifierGroup cartItemModifierGroup = cartItemModifierGroups.iterator().next();

		//Retrieve CartItemModifierField
		final CartItemModifierField cartItemModifierField = cartItemModifierGroup.getCartItemModifierFields().iterator().next();

		List<CartItemModifierField> result = getPersistenceEngine().retrieveByNamedQuery("CART_ITEM_MODIFIER_FIELD_BY_CODE",
				cartItemModifierField.getGuid());
		assertNotNull("The query should not return a null", result);
		assertThat(result, contains(cartItemModifierField));
	}

}
