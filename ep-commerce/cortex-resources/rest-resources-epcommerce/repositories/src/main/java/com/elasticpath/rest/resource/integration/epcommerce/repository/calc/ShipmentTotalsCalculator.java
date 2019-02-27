/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.calc;

import io.reactivex.Single;

import com.elasticpath.money.Money;

/**
 * Performs totals calculations for the Shipment.
 */
public interface ShipmentTotalsCalculator {

	/**
	 * Calculates the total for the given Shipment.
	 *
	 * @param orderGuid the orderGuid of the Order
	 * @param shipmentGuid the shipmentGuid of the Shipment to total
	 * @return the total or an error
	 */
	Single<Money> calculateTotalForShipment(String orderGuid, String shipmentGuid);

	/**
	 * Calculates the total for the given Shipment Line Item.
	 *
	 * @param orderGuid    the orderGuid of the Order
	 * @param shipmentGuid the shipmentGuid of the Shipment to total
	 * @param shipmentItemGuid the shipmentItemGuid of the Shipment Line Item to total
	 * @return the total or an error
	 */
	Single<Money> calculateTotalForLineItem(String orderGuid, String shipmentGuid, String shipmentItemGuid);
}
