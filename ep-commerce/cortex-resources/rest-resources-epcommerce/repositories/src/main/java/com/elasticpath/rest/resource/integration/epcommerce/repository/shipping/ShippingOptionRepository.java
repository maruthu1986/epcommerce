/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.shipping;

import java.util.Map;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

import com.elasticpath.shipping.connectivity.dto.ShippingOption;

/**
 * The facade for operations with shipping options.
 */
public interface ShippingOptionRepository {

	/**
	 * Find a Shipping option by code.
	 * @param storeCode the store code
	 * @param shipmentDetailsId the shipment details id
	 * @param shippingOptionCode the shipping option code
	 *
	 * @return Single with the shipping option
	 */
	Single<ShippingOption> findByCode(String storeCode, Map<String, String> shipmentDetailsId, String shippingOptionCode);

	/**
	 * Find Shipping option by given shipping option code.
	 *
	 * @param shippingOptionCode    shipping option code
	 * @param shippingOptions    observable shipping option
	 * @return Single with shipping option
	 */
	Single<ShippingOption> getShippingOption(String shippingOptionCode, Observable<ShippingOption> shippingOptions);

	/**
	 * Find available shipping option codes for the given shipment.
	 *
	 * @param storeCode the store code
	 * @param shipmentDetailsId the shipment details id
	 * @return ExecutionResult with the collection of valid shipping option codes
	 */
	Observable<String> findShippingOptionCodesForShipment(String storeCode, Map<String, String> shipmentDetailsId);

	/**
	 * Gets the selected shipping option code for the given shipment.
	 *
	 * @param storeCode the store code
	 * @param shipmentDetailsId the shipment details id
	 * @return Single with the selected shipping option code
	 */
	Maybe<String> getSelectedShippingOptionCodeForShipmentDetails(String storeCode, Map<String, String> shipmentDetailsId);
}
