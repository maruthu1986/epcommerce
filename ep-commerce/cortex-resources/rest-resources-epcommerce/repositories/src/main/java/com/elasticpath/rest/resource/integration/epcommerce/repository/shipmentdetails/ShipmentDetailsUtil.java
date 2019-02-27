/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.shipmentdetails;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import com.elasticpath.domain.shipping.ShipmentType;
import com.elasticpath.domain.shoppingcart.ShoppingCart;

/**
 * Utility class for Shipment Details.
 */
public final class ShipmentDetailsUtil {

	private ShipmentDetailsUtil() {

	}

	/**
	 * Create shipment details id.
	 *
	 * @param orderId		order id
	 * @param deliveryId	delivery id
	 * @return a map containing order id and delivery id.
	 */
	public static Map<String, String> createShipmentDetailsId(final String orderId, final String deliveryId) {
		return ImmutableMap.<String, String>of(ShipmentDetailsConstants.ORDER_ID, orderId, ShipmentDetailsConstants.DELIVERY_ID, deliveryId);
	}

	/**
	 * Checks if shopping cart has something shippable.
	 *
	 * @param shoppingCart	shopping cart.
	 * @return true if there is a shippable item.
	 */
	public static boolean containsPhysicalShipment(final ShoppingCart shoppingCart) {
		return shoppingCart.getShipmentTypes().stream().anyMatch(shipmentType -> shipmentType.equals(ShipmentType.PHYSICAL));
	}

}
