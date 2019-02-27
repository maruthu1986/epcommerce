/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.shipmentdetails;

/**
 * Constants for Shipment Details.
 */
public final class ShipmentDetailsConstants {

	private ShipmentDetailsConstants() {
		
	}

	/**
	 * Destination selector name.
	 */
	public static final String DESTINATION_SELECTOR_NAME = "destination-selector";

	/**
	 * Destination Info name.
	 */
	public static final String DESTINATION_INFO_NAME = "destination-info";

	/**
	 * Physical Delivery Type.
	 */
	public static final String SHIPMENT_TYPE = "SHIPMENT";

	/**
	 * Order id key.
	 */
	public static final String ORDER_ID = "order-id";

	/**
	 * Shipment id key.
	 */
	public static final String DELIVERY_ID = "shipment-id";

	/**
	 * Selection rule for shipment details.
	 */
	public static final String SELECTION_RULE = "1";

	/**
	 * The shipping option info name.
	 */
	public static final String SHIPPING_OPTION_INFO_NAME = "shipping-option-info";

	/**
	 * Selector display name.
	 */
	public static final String SHIPPING_OPTION_SELECTOR_NAME = "shipping-option-selector";

	/**
	 * Advisor message for shipment details.
	 */
	public static final String MESSAGE_NEED_SHIPMENT_DETAILS = "Shipment details must be provided before you can complete the purchase.";
}
