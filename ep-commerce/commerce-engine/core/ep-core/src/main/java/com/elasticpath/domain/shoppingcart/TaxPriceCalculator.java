/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.domain.shoppingcart;

/**
 * A consistent interface for retrieving prices from shopping carts and order skus.
 * For tax exclusive stores this will include tax.
 * For tax inclusive stores this will omit the tax.
 * For any other result, use the standard {@link PriceCalculator}
 */
public interface TaxPriceCalculator extends PriceCalculator {

}
