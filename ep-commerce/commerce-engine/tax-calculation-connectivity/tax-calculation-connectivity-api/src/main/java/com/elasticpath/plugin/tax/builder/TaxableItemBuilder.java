/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.builder;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Map;

import com.elasticpath.plugin.tax.domain.TaxableItem;
import com.elasticpath.plugin.tax.domain.impl.TaxableItemImpl;

/**
 * Builder for {@link TaxableItemImpl}.
 */
public class TaxableItemBuilder {
	
private final TaxableItemImpl taxableItem;
	
	/**
     * Constructor.
     */
	public TaxableItemBuilder() {
		taxableItem = new TaxableItemImpl();
	}
	
	/** Gets a new builder. 
	 * 
	 * @return a new builder.
	 */
	public static TaxableItemBuilder newBuilder() {
		return new TaxableItemBuilder();
	}
	
	/** Gets the instance built by the builder.
	 * 
	 * @return the built instance
	 */
	public TaxableItem build() {
		return taxableItem;
	}
	
	/** Sets the tax code.
	 * 
	 * @param taxCode the given tax code
	 * @return the builder
	 */
	public TaxableItemBuilder withTaxCode(final String taxCode) {
		taxableItem.setTaxCode(taxCode);
		return this;
	}
	
	/** Sets the item code.
	 * 
	 * @param itemCode the given item code
	 * @return the builder
	 */
	public TaxableItemBuilder withItemCode(final String itemCode) {
		taxableItem.setItemCode(itemCode);
		return this;
	}
	
	/** Sets the item guid.
	 * 
	 * @param itemGuid the given item guid
	 * @return the builder
	 */
	public TaxableItemBuilder withItemGuid(final String itemGuid) {
		taxableItem.setItemGuid(itemGuid);
		return this;
	}
	
	/** Sets the currency.
	 * 
	 * @param currency the given currency
	 * @return the builder
	 */
	public TaxableItemBuilder withCurrency(final Currency currency) {
		taxableItem.setCurrency(currency);
		return this;
	}
	
	/** Sets the item amount.
	 * 
	 * @param itemAmount the given item amount
	 * @return the builder
	 */
	public TaxableItemBuilder withItemAmount(final BigDecimal itemAmount) {
		taxableItem.setPrice(itemAmount);
		return this;
	}
	
	/**
	 * Adds a field to the item field map.
	 * 
	 * @param fieldName the field name
	 * @param fieldValue the field value
	 * @return the builder
	 */
	public TaxableItemBuilder withField(final String fieldName, final String fieldValue) {
		taxableItem.setFieldValue(fieldName, fieldValue);
		return this;
	}
	
	/**
	 * Sets the item field map.
	 * 
	 * @param fieldValues the map containing the fields and its value for the taxable item
	 * @return the builder
	 */
	public TaxableItemBuilder withFields(final Map<String, String> fieldValues) {
		taxableItem.setFields(fieldValues);
		return this;
	}
}
