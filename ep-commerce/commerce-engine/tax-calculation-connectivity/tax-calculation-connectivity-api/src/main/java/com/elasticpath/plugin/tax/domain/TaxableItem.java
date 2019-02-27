/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.domain;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Map;

/**
 * Interface defining an item for which tax calculations are to be performed.
 */
public interface TaxableItem {

	/**
	 * Gets the currency.
	 *
	 * @return the currency
	 */
	Currency getCurrency();

	/**
	 * Gets the taxable price the item (e.g. item price - apportioned discount amount).
	 *
	 * @return the taxable price of this item
	 */
	BigDecimal getTaxablePrice();

	/**
	 * Gets the tax code.
	 *
	 * @return tax code for this item
	 */
	String getTaxCode();

	/**
	 * Returns whether the tax code is active.
	 *
	 * @return true if the tax code is active
	 */
	boolean isTaxCodeActive();

	/**
	 * Returns the item's GUID.
	 *
	 * @return taxable item GUID
	 */
	String getItemGuid();

	/**
	 * @return the item code for this item
	 */
	String getItemCode();

	/**
	 * Sets the item's GUID.
	 *
	 * @param itemGuid the item's GUID
	 */
	void setItemGuid(String itemGuid);

	/**
	 * Gets the field value for a given {@code name}. If the field has not been set then null is returned.
	 * The field value provides a way to pass extra item-level data to tax provider plugins.
	 *
	 * @param name the name of the field
	 * @return the current value of the field or null
	 */
	String getFieldValue(String name);

	/**
	 * Sets a field value for the provided {@code name}. Any previous value is replaced.
	 *
	 * @param name the name of the field
	 * @param value the value to be assigned to the field
	 */
	void setFieldValue(String name, String value);

	/**
	 * Gets the field value for the quantity.
	 *
	 * @return the quantity
	 */
	int getQuantity();

	/**
	 * Sets the field value for the quantity.
	 *
	 * @param quantity the quantity of each item
	 */
	void setQuantity(int quantity);

	/**
	 * Gets the item description.
	 *
	 * @return the item description
	 */
	String getItemDescription();

	/**
	 * Sets the item description.
	 *
	 * @param itemDescription the item description
	 */
	void setItemDescription(String itemDescription);

	/**
	 * Provides a container to hold extra data for the taxable item which may be needed by some tax provider plugins to
	 * calculate taxes based on the item level info.
	 *
	 * @return immutable map of all key/value data field pairs
	 */
	Map<String, String> getFields();

	/**
	 * Sets a field map.  The previous map is replaced.
	 *
	 * @param fieldValues The field map to set
	 */
	void setFields(Map<String, String> fieldValues);

}
