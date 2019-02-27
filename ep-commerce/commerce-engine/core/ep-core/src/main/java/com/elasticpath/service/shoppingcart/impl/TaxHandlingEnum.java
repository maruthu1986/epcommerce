/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.service.shoppingcart.impl;

/**
 * Enum for determining how taxes should be handled.
 */
public enum TaxHandlingEnum {
	/** Get the tax inclusive/exclusive from the site.*/
	USE_SITE_DEFAULTS,
	/** included tax in the price. */
	INCLUDE,
	/** tax is not included in price.*/
	EXCLUDE
}
