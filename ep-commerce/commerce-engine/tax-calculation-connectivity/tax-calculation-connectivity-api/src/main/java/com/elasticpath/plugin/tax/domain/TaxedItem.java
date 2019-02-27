/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.domain;

import java.math.BigDecimal;
import java.util.List;

/**
 * Interface for an item that has tax calculation results after being processed by a {@link com.elasticpath.plugin.tax.calculator.TaxCalculator}.
 */
public interface TaxedItem extends DiscountableTaxItem {

	/**
     * Gets the total tax amount for the item.
	 *
	 * @return total tax amount
	 */
	BigDecimal getTotalTax();
	
	/**
     * Gets tax details for this item.
	 *
	 * @return a collection of the tax records for this item
	 */
	List<TaxRecord> getTaxRecords();
	
	/**
	 * Gets the price before taxes for the item. For tax exclusive calculations this is the item price. For tax
     * inclusive calculations this is the item price minus taxes.
	 * 
	 * @return the price before taxes
	 */
	BigDecimal getPriceBeforeTax();
	
	/**
     * Gets the amount of tax included in the price.
	 *
	 * @return the tax amount included in the price
	 */
	BigDecimal getTaxInPrice();
	
	/**
	 * Gets the associated taxable item.
	 *
	 * @return the taxable item
	 */
	TaxableItem getTaxableItem();
	
}
