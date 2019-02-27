/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.schema.uri;

/**
 * Builds shipping option info URIs.
 */
public interface ShippingOptionInfoUriBuilder extends ScopedUriBuilder<ShippingOptionInfoUriBuilder> {

	/**
	 * Sets the shipment details id.
	 *
	 * @param shipmentDetailsId the shipment details id
	 * @return this {@link ShippingOptionInfoUriBuilder}
	 */
	ShippingOptionInfoUriBuilder setShipmentDetailsId(String shipmentDetailsId);
}
