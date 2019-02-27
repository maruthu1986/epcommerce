/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.shipmentdetails;

import java.util.Map;

import io.reactivex.Single;

/**
 * Service that retrieves shipment detail ids for an order.
 */
public interface ShipmentDetailsService {

	/**
	 * Retrieve shipment details ids for an order.
	 *
	 * @param scope the scope.
	 * @param orderId    cart order id
	 * @return	shipment details ids
	 */
	Single<Map<String, String>> getShipmentDetailsIdForOrder(String scope, String orderId);
}
