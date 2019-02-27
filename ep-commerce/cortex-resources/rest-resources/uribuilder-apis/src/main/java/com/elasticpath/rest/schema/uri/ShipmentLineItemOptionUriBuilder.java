/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.schema.uri;

/**
 * URI Builder for discounts resource.
 */
public interface ShipmentLineItemOptionUriBuilder extends ReadFromOtherUriBuilder<ShipmentLineItemOptionUriBuilder> {

	/**
	 * Sets the line item id.
	 *
	 * @param optionId the option id
	 * @return this builder
	 */
	ShipmentLineItemOptionUriBuilder setOptionId(String optionId);
	
}