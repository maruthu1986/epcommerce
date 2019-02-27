/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.test.integration.tax;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.domain.customer.impl.PaymentTokenImpl;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingCartTaxSnapshot;
import com.elasticpath.test.integration.DirtiesDatabase;

/**
 * Integration test for TaxOperationService.
 */
public class TaxOperationShoppingCartCheckoutTest extends AbstractBasicTaxOperationTest {

	/**
	 * Test order inventory.
	 */
	@DirtiesDatabase
	@Test
	public void testOrderTaxes() {
		// construct and save new shopping cart
		final Shopper shopper = customerSession.getShopper();
		ShoppingCart shoppingCart = persisterFactory.getOrderTestPersister().persistEmptyShoppingCart(address, address, customerSession,
				scenario.getShippingOption(), scenario.getStore());

		ShoppingItemDto physicalDto = new ShoppingItemDto(shippableProducts.get(0).getDefaultSku().getSkuCode(), 1);
		ShoppingItemDto electronicDto = new ShoppingItemDto(nonShippableProducts.get(0).getDefaultSku().getSkuCode(), 1);
		cartDirector.addItemToCart(shoppingCart, physicalDto);
		cartDirector.addItemToCart(shoppingCart, electronicDto);

		// make new order payment
		OrderPayment templateOrderPayment = persisterFactory.getOrderTestPersister().createOrderPayment(customer, new PaymentTokenImpl.TokenBuilder()
				.build());

		// checkout
		final ShoppingCartPricingSnapshot pricingSnapshot = pricingSnapshotService.getPricingSnapshotForCart(shoppingCart);
		final ShoppingCartTaxSnapshot taxSnapshot = taxSnapshotService.getTaxSnapshotForCart(shoppingCart, pricingSnapshot);

		checkoutService.checkout(shoppingCart, taxSnapshot, customerSession, templateOrderPayment, true);

		// only one order should have been created by the checkout service
		List<Order> ordersList = orderService.findOrderByCustomerGuid(shopper.getCustomer().getGuid(), true);
		assertEquals("There should be 1 item in the order", 1, ordersList.size());
		Order order = ordersList.iterator().next();
		
		// find the tax journal records for the order shipments
		for (OrderShipment orderShipment : order.getAllShipments()) {
			verifyTaxDocumentForOrderShipment(orderShipment, store);
		}
	}
}
