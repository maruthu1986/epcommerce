/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.customer;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import javax.validation.Valid;

import com.elasticpath.commons.exception.EpBindException;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.CustomerProfileValue;
import com.elasticpath.validation.constraints.AttributeRequired;

/**
 * This interface represents a group of <code>AttributeValue</code> of Customer Profile type attribute.
 * It is aggregated into <code>Customer</code>.
 */
@AttributeRequired
public interface CustomerProfile {

	/**
	 * Get the value of a string profile with the specified key in default locale.
	 *
	 * @param attributeKey the key of the profile to be retrieved
	 * @return the <code>String</code> value of the attribute
	 */
	String getStringProfileValue(String attributeKey);

	/**
	 * Set the customer profile value based on the given string value and given creation date.
	 * Method is important for import functionality.
	 *
	 * @param attributeKey the attribute Key to set the value
	 * @param stringValue the string value
	 * @param creationDate the date attribute value has been created
	 * @throws EpBindException in case the given string value is invalid
	 */
	void setStringProfileValue(String attributeKey, String stringValue, Date creationDate) throws EpBindException;

	/**
	 * Set the customer profile value based on the given string value.
	 *
	 * @param attributeKey the attribute Key to set the value
	 * @param stringValue the string value
	 * @throws EpBindException in case the given string value is invalid
	 */
	void setStringProfileValue(String attributeKey, String stringValue) throws EpBindException;

	/**
	 * Set the customer profile value based on the given pair of key/object.
	 *
	 * @param attributeKey the attribute Key to set the value
	 * @param value the object to set the value
	 * @throws EpBindException in case the given value is invalid
	 */
	void setProfileValue(String attributeKey, Object value) throws EpBindException;

	/**
	 * Get the value of a profile with the specified key in default locale.
	 *
	 * @param attributeKey the key of the profile to be retrieved
	 * @return the value of the attribute
	 */
	Object getProfileValue(String attributeKey);

	/**
	 * Sets the attribute value map.
	 *
	 * @param attributeValueMap the attribute value map
	 */
	void setProfileValueMap(Map<String, CustomerProfileValue> attributeValueMap);

	/**
	 * Returns the attribute value map.
	 *
	 * @return the attribute value map
	 */
	@Valid
	Map<String, CustomerProfileValue> getProfileValueMap();

	/**
	 * Determines if profile value with the specified key is required or not.
	 * If an profile value is not found for the specified key, returns "false".
	 *
	 * @param attributeKey the key of the profile to be retrieved
	 * @return true if profile value if required, false otherwise or if profile value not found.
	 */
	boolean isProfileValueRequired(String attributeKey);

	/**
	 * Returns the Attributes supported by this CustomerProfile object.
	 * @return an unmodifiable list of attributes
	 */
	Collection<Attribute> getProfileAttributes();
}
