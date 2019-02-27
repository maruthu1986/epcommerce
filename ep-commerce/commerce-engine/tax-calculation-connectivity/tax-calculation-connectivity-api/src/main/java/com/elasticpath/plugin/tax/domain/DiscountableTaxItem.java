/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.domain;

import java.math.BigDecimal;

/**
 * Interface defining a {@link TaxableItem} with the addition of the discount amount that has been apportioned to the item.
 */
public interface DiscountableTaxItem extends TaxableItem, Discountable {

	/**
	 * Gets the item amount before the discount is apportioned.
	 * 
	 * @return the amount of this item before discount
	 */
	BigDecimal getPrice();

}
