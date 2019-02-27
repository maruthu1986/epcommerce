/**
 * Copyright (c) Elastic Path Software Inc., 2013-2014
 */
package com.elasticpath.plugin.tax.domain;

import java.util.Currency;
import java.util.Map;

import com.elasticpath.plugin.tax.common.TaxItemObjectType;
import com.elasticpath.plugin.tax.common.TaxJournalType;
import com.elasticpath.plugin.tax.common.TaxTransactionType;

/**
 * Interface that defines the context required for tax operations.
 */
public interface TaxOperationContext {


	/**
	 * Gets the currency.
	 *
	 * @return the currency
	 */
	Currency getCurrency();

	/**
	 * Gets the customer code.
	 *
	 * @return the customer code
	 */
	String getCustomerCode();

	/**
	 * Gets the {@link TaxJournalType}.
	 *
	 * @return the journal type
	 */
	TaxJournalType getJournalType();

	/**
	 * Gets the tax document Id, such as an order shipment number.
	 *
	 * @return the document Id
	 */
	TaxDocumentId getDocumentId();

	/**
	 * Gets the order number.
	 *
	 * @return the order number
	 */
	String getOrderNumber();

	/**
	 * Gets the transaction type.
	 *
	 * @return the transaction type
	 */
	TaxTransactionType getTransactionType();

	/**
	 * Gets the {@link TaxItemObjectType} that identifies the EP domain object to which the tax item is related.
	 *
	 * @return the item object type
	 */
	TaxItemObjectType getItemObjectType();

	/**
	 * Gets the shipping item reference id, which is related to {@OrderShipment} or {@link OrderReturn}.
	 *
	 * @return the shipping item reference id
	 */
	String getShippingItemReferenceId();

	/**
	 * Gets the {@link TaxOverrideContext}.
	 *
	 * @return the taxOverrideContext
	 */
	TaxOverrideContext getTaxOverrideContext();

	/**
	 * Gets the {@link TaxExemption}.
	 *
	 * @return taxExemption tax exemption entity
	 */
	TaxExemption getTaxExemption();

	/**
	 * Gets the Customer's business number.
	 *
	 * @return the customer business number
	 */
	String getCustomerBusinessNumber();

	/**
	 * Gets a field by {@code name} and returns the current value. If the field has not been set then returns null.
	 * The field value provides a way to pass extra data of the taxable container to tax provider plugin
	 * if the extra data is needed for tax calculation on container level.
	 *
	 * @param name the name of the field
	 * @return the current value of the field or null
	 */
	String getFieldValue(String name);

	/**
	 * Assigns a value to a field to a field map. Any previous value is replaced.
	 *
	 * @param name the name of the field
	 * @param value the value to be assigned to the field
	 */
	void setFieldValue(String name, String value);

	/**
	 * Provides a container to hold extra data for the taxable container which may be needed by some tax provider plugin to calculate taxes
	 * based on the container level info.
	 *
	 * @return unmodifiable map of all key/value data field pairs
	 */
	Map<String, String> getFields();

	/**
	 * Assigns a map to the fields. The previous map is replaced.
	 *
	 * @param fieldValues The map of the fields to assign.
	 */
	void setFields(Map<String, String> fieldValues);
}
