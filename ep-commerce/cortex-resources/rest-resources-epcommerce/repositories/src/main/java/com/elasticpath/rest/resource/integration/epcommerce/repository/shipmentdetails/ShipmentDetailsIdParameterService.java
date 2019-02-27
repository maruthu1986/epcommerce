/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.shipmentdetails;

import java.util.Map;

import io.reactivex.Observable;

import com.elasticpath.rest.id.IdentifierPart;

/**
 * Service that returns shipment detail ids accessible by a user.
 */
public interface ShipmentDetailsIdParameterService {

	/**
	 * Retrieve shipment detail ids related to user.
	 *
	 * @param scope		scope
	 * @param userId	user id
	 * @return Shipment Detail Ids that the user has access to.
	 */
	Observable<IdentifierPart<Map<String, String>>> findShipmentDetailsIds(String scope, String userId);
}
