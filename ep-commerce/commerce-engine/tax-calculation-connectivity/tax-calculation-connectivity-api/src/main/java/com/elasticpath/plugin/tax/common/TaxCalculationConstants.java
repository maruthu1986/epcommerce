/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.common;

import java.math.RoundingMode;

/**
 * Constants used for tax calculation.
 */
public final class TaxCalculationConstants {

	/** Scale to be used for tax value decimal fields. */
	public static final int DEFAULT_DECIMAL_SCALE = 2;
	
	/** Scale to be used for tax value decimal dividing. */
	public static final int DEFAULT_DIVIDE_SCALE = 10;

	/** Scale to be used for tax rate decimal field. */
	public static final int TAX_RATE_DECIMAL_SCALE = 6;
	
	/** Rounding mode to be used for tax calculation fields. */
	public static final RoundingMode DEFAULT_ROUNDING_MODE = RoundingMode.HALF_UP;

	private TaxCalculationConstants() {
		// Do not instantiate this class
	}
}
