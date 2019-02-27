/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.domain;

import java.math.BigDecimal;

/**
 * Interface to apply and retrieve the discount associated with a {@link TaxableItem}.
 */
public interface Discountable {

	/**
     * Gets the applied discount amount.
	 *
	 * @return the applied discount amount
	 */
	BigDecimal getDiscount();
	
	/**
	 * Applies a discount amount.
	 * 
	 * @param discount the discount amount to apply
	 */
	void applyDiscount(BigDecimal discount);

}
