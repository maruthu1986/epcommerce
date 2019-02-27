/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.domain.shoppingcart;

import java.math.BigDecimal;

import com.elasticpath.money.Money;

/**
 * A consistent interface for retrieving prices from shopping carts and order skus.
 * 
 * @author gdenning
 *
 */
public interface PriceCalculator {
		
	/**
	 * Include any cart discounts when calculating the amount.
	 * @return PriceCalculator to satisfy fluency.
	 */
	PriceCalculator withCartDiscounts();

	/**
	 * The calculations should be done on the unit level.
	 * @return PriceCalculator to satisfy fluency
	 */
	PriceCalculator forUnitPrice();

	/**
	 * The amount calculated with any include/ignore considerations.
	 * @return calculated price
	 */
	BigDecimal getAmount();
	
	/**
	 * The Money amount calculated with any include/ignore considerations.
	 * @return calculated money
	 */
	Money getMoney();
}
