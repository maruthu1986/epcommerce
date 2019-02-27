/*
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.test.integration.cartitemmodifier;

import org.junit.Test;

import com.elasticpath.test.integration.DirtiesDatabase;

/**
 * Test that the cart item modifier is persisted correctly.
 */
public class CartItemModifierTest extends AbstractCartItemModifierTest {

	/**
	 * Test ExtProductType tree saving operation.
	 */
	@DirtiesDatabase
	@Test
	public void testSaveProductTypeWithCartItemModifierTree() {
		persistSimpleProductTypeWithCartItemModifierGroup();
	}

}
