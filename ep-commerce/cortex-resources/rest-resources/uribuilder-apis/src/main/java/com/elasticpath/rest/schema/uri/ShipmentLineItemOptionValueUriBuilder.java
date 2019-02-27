/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.schema.uri;

/**
 * URI Builder for discounts resource.
 */
public interface ShipmentLineItemOptionValueUriBuilder extends ReadFromOtherUriBuilder<ShipmentLineItemOptionValueUriBuilder> {

	/**
	 * Sets the line item id.
	 *
	 * @param optionValueId the option id
	 * @return this builder
	 */
	ShipmentLineItemOptionValueUriBuilder setOptionValueId(String optionValueId);
	
}