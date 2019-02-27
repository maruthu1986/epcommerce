/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.shipmentdetails.destinationinfo;

import io.reactivex.Maybe;
import io.reactivex.Single;

/**
 * Destination info service that retrieves and validates the selected address.
 */
public interface DestinationInfoService {

	/**
	 * Retrieve selected address id.
	 *
	 * @param scope		scope
	 * @param orderId	id
	 * @return Address id.
	 */
	Maybe<String> getSelectedAddressGuidIfShippable(String scope, String orderId);

	/**
	 * Checks if order is shippable.
	 *
	 * @param scope		scope
	 * @param orderId	id
	 * @return	true if order is shippable
	 */
	Single<Boolean> validateOrderIsShippable(String scope, String orderId);
}
