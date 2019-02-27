/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.test.integration.order;

import org.junit.Test;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.impl.ShoppingCartImpl;
import com.elasticpath.test.db.DbTestCase;
import com.elasticpath.test.integration.DirtiesDatabase;

/**
 * Integration test for shopping cart.
 */
public class ComplexBundlePurchaseAndViewLineItemsTest extends DbTestCase {

	private ShoppingCartImpl shoppingCart;

	/**
	 * Tests that the shopping cart can be serialized even when services are
	 * linked during a get.
	 *
	 * @throws Exception if a failure is detected
	 */
	@DirtiesDatabase
	@Test
	public void testSerialization() throws Exception {
		shoppingCart = getBeanFactory().getBean(ContextIdNames.SHOPPING_CART);
	}

}