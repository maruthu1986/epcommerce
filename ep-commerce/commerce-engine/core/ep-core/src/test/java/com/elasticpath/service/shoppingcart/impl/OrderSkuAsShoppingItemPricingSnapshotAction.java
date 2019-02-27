/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.service.shoppingcart.impl;

import org.jmock.api.Invocation;
import org.jmock.lib.action.CustomAction;

/**
 * <p>JMock Action that uses the same {@link com.elasticpath.domain.order.OrderSku OrderSku} passed into the
 * {@link com.elasticpath.service.shoppingcart.PricingSnapshotService PricingSnapshotService} as the return value
 * {@link com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot ShoppingItemPricingSnapshot}.</p>
 * <p>Recommended usage:</p>
 * <pre><code>
 * 		import static com.elasticpath.service.shoppingcart.impl.OrderSkuAsShoppingItemPricingSnapshotAction.returnTheSameOrderSkuAsPricingSnapshot;
 *
 * 		context.checking(new Expectations() {
 * 			{
 * 				allowing(pricingSnapshotService).getPricingSnapshotForOrderSku(with(any(OrderSku.class)));
 * 				will(returnTheSameOrderSkuAsPricingSnapshot());
 * 			}
 * 		});
 * </code></pre>
 * @see com.elasticpath.service.shoppingcart.PricingSnapshotService#getPricingSnapshotForOrderSku(com.elasticpath.domain.order.OrderSku)
 *		getPricingSnapshotForOrderSku
 */
public class OrderSkuAsShoppingItemPricingSnapshotAction extends CustomAction {

	/**
	 * Constructor.
	 */
	public OrderSkuAsShoppingItemPricingSnapshotAction() {
		super("Returns the same OrderSku as a ShoppingItemPricingSnapshot");
	}

	@Override
	public Object invoke(final Invocation invocation) {
		return invocation.getParameter(0);
	}

	/**
	 * Convenience method to create a new action while keeping a fluent syntax.
	 *
	 * @return a new action instance
	 */
	public static OrderSkuAsShoppingItemPricingSnapshotAction returnTheSameOrderSkuAsPricingSnapshot() {
		return new OrderSkuAsShoppingItemPricingSnapshotAction();
	}

}
