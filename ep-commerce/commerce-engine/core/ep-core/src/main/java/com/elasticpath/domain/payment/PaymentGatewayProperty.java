/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.domain.payment;

import com.elasticpath.persistence.api.Persistable;

/**
 * Represents a Properties string to string properties map.
 */
public interface PaymentGatewayProperty extends Persistable {
	/**
	 * Gets the key of this property.
	 *
	 * @return the key of this property
	 */
	String getKey();

	/**
	 * Sets the key of this property.
	 *
	 * @param key the key of this property
	 */
	void setKey(String key);

	/**
	 * Gets the value of this property.
	 *
	 * @return the value of this property
	 */
	String getValue();

	/**
	 * Sets the value of this property.
	 *
	 * @param value the value of this property
	 */
	void setValue(String value);
}
