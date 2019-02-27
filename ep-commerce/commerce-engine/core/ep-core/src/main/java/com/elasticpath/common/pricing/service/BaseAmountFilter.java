/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.common.pricing.service;

import java.math.BigDecimal;

/**
 * Query criteria filter for searching BaseAmounts.
 * Encapsulates list of search criteria from the services API.
 */
public interface BaseAmountFilter {
	/**
	 *
	 * @param type the target object type.
	 */
	void setObjectType(String type);

	/**
	 * @param guid the target object guid.
	 */
	void setObjectGuid(String guid);

	/**
	 * @param sale the BaseAmount's sale value.
	 */
	void setSaleValue(BigDecimal sale);

	/**
	 * @param list the BaseAmount's list value.
	 */
	void setListValue(BigDecimal list);

	/**
	 * @param quantity the BaseAmount's quantity.
	 */
	void setQuantity(BigDecimal quantity);

	/**
	 * @param descriptorGuid the BaseAmount's Price List descriptor GUID.
	 */
	void setPriceListDescriptorGuid(String descriptorGuid);

	/**
	 * @return the target object type.
	 */
	String getObjectType();

	/**
	 * @return the target object guid.
	 */
	String getObjectGuid();

	/**
	 * @return the BaseAmount's list value.
	 */
	BigDecimal getListValue();

	/**
	 * @return the BaseAmount's sale value.
	 */
	BigDecimal getSaleValue();

	/**
	 * @return the BaseAmount's quantity.
	 */
	BigDecimal getQuantity();

	/**
	 * @return the BaseAmount's Price List descriptor GUID.
	 */
	String getPriceListDescriptorGuid();

}
