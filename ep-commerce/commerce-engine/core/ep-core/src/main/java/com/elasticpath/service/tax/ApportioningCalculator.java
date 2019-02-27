/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.tax;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Apportions a given amount among multiple items proportionally to the items'
 * values.
 *
 * Example: A price of a bundle is $149.99 It consists of a camera, whose
 * original price is $200 and a bag $50
 *
 * Let's call $149.99 - amountToApportion $200 and $50 - portions.
 *
 * The expected result is: camera $119.99 bag $30.00 Let's call these numbers
 * proportions.
 *
 */
public interface ApportioningCalculator {
	/**
	 * Calculates apportioned amounts.
	 *
	 * @param amountToApportion
	 *            amount to apportion.
	 * @param amounts
	 *            amounts.
	 * @return a map of apportioned amounts.
	 */
	Map<String, BigDecimal> calculateApportionedAmounts(
			BigDecimal amountToApportion,
			Map<String, BigDecimal> amounts);
}
