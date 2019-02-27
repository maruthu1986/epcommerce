/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.customer.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.elasticpath.commons.exception.EpBindException;
import com.elasticpath.commons.util.impl.LocaleUtils;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.attribute.AttributeValue;
import com.elasticpath.domain.attribute.CustomerProfileValue;
import com.elasticpath.domain.attribute.impl.CustomerProfileValueImpl;
import com.elasticpath.domain.customer.CustomerProfile;
import com.elasticpath.domain.impl.AbstractLegacyPersistenceImpl;

/**
 * This is a default implementation of <code>CustomerProfile</code>.
 */
public class CustomerProfileImpl extends AbstractLegacyPersistenceImpl implements CustomerProfile {

	private static final long serialVersionUID = 5000000001L;

	private static final char SEPARATOR = '_';

	private Map<String, CustomerProfileValue> profileValueMap = new HashMap<>();
	private Map<String, Attribute> customerProfileAttributes;

	private long uidPk;

	@Override
	public boolean isProfileValueRequired(final String attributeKey) {
		AttributeValue attributeValue = getAttributeValue(attributeKey, null);

		if (attributeValue == null) {
			return false;
		}

		return attributeValue.getAttribute().isRequired();
	}

	@Override
	public String getStringProfileValue(final String attributeKey) {
		// customer profile attribute is not locale dependent, set locale to null
		return getStringAttributeValue(attributeKey, null);
	}

	private String getStringAttributeValue(final String attributeKey, final Locale locale) {
		AttributeValue attributeValue = getAttributeValue(attributeKey, locale);

		if (attributeValue == null) {
			return null;
		}

		return attributeValue.getStringValue();
	}

	@Override
	public Object getProfileValue(final String attributeKey) {
		AttributeValue attributeValue = getAttributeValue(attributeKey, null);
		if (attributeValue == null) {
			return null;
		}
		return attributeValue.getValue();
	}

	/**
	 * Get the value of an attribute with the specified key in the given locale.<br>
	 * If the attribute with the specified key does not exist in the given locale then this will attempt to find the attribute value without using a
	 * locale.<br>
	 * May return null if no attribute value is ultimately found.<br>
	 * This implementation delegates to getAttributeValueWithoutFallback(key, locale).
	 *
	 * @param attributeKey the key of the attribute to be retrieved
	 * @param locale the locale for which the attribute is requested
	 * @return the <code>AttributeValue</code>, or null if it cannot be found
	 */
	private AttributeValue getAttributeValue(final String attributeKey, final Locale locale) {
		return getAttributeValueWithoutFallBack(attributeKey, locale);
	}

	@Override
	public void setStringProfileValue(final String attributeKey, final String stringValue, final Date creationDate) throws EpBindException {
		setStringAttributeValue(getCustomerProfileAttributeMap().get(attributeKey), null, stringValue, creationDate);
	}

	@Override
	public void setStringProfileValue(final String attributeKey, final String stringValue) {
		setStringProfileValue(attributeKey, stringValue, null);
	}

	private void setStringAttributeValue(final Attribute attribute, final Locale locale,
										 final String stringValue, final Date creationDate) throws EpBindException {
		CustomerProfileValue attributeValue = getAttributeValueWithoutFallBack(attribute.getKey(), locale);
		if (attributeValue != null) {
			String attributeStringValue = attributeValue.getStringValue();
			if (!Objects.equals(stringValue, attributeStringValue)) {
				attributeValue.setStringValue(stringValue);
			}
			return;
		}

		attributeValue = createAttributeValue(attribute);
		attributeValue.setStringValue(stringValue);
		attributeValue.setCreationDate(creationDate);
		final String localizedAttributeKey = getLocalizedAttributeKey(attribute.getKey(), locale);
		attributeValue.setLocalizedAttributeKey(localizedAttributeKey);
		getProfileValueMap().put(localizedAttributeKey, attributeValue);
	}

	@Override
	public void setProfileValue(final String attributeKey, final Object value) {
		setAttributeValue(getCustomerProfileAttributeMap().get(attributeKey), null, value);
	}

	/**
	 * Returns the Attributes supported by this CustomerProfile object.
	 * @return an unmodifiable list of attributes
	 */
	@Override
	public Collection<Attribute> getProfileAttributes() {
		if (getCustomerProfileAttributeMap() != null) {
			return Collections.unmodifiableCollection(getCustomerProfileAttributeMap().values());
		}

		return Collections.emptyList();
	}

	/**
	 * Gets the customer profile attribute metadata.  This metadata is required to set profile attributes,
	 * like email, name, etc.  For new customer objects, the metadata is set by the bean factory (see prototypes.xml).
	 * For existing customers, the metadata is set by OpenJPA via the {@link com.elasticpath.service.customer.impl.CustomerPostLoadStrategy}.
	 *
	 * @return the attribute metadata
	 */
	protected Map<String, Attribute> getCustomerProfileAttributeMap() {
		return customerProfileAttributes;
	}

	/**
	 * Sets the customer profile attribute metadata.  This metadata is required to set profile attributes,
	 * like email, name, etc.  For new customer objects, the metadata is set by the bean factory (see prototypes.xml).
	 * For existing customers, the metadata is set by OpenJPA via the {@link com.elasticpath.service.customer.impl.CustomerPostLoadStrategy}.
	 *
	 * @param customerProfileAttributes the attribute metadata
	 */
	public void setCustomerProfileAttributeMap(final Map<String, Attribute> customerProfileAttributes) {
		this.customerProfileAttributes = customerProfileAttributes;
	}

	private void setAttributeValue(final Attribute attribute, final Locale locale, final Object value) {
		CustomerProfileValue attributeValue = getAttributeValueWithoutFallBack(attribute.getKey(), locale);
		if (attributeValue != null) {
			attributeValue.setValue(value);
			return;
		}

		attributeValue = createAttributeValue(attribute);
		attributeValue.setValue(value);
		final String localizedAttributeKey = getLocalizedAttributeKey(attribute.getKey(), locale);
		attributeValue.setLocalizedAttributeKey(localizedAttributeKey);
		getProfileValueMap().put(localizedAttributeKey, attributeValue);
	}

	private CustomerProfileValue getAttributeValueWithoutFallBack(final String attributeKey, final Locale locale) {
		if (locale == null) {
			return getProfileValueMap().get(getLocalizedAttributeKey(attributeKey, null));
		}
		Locale broadenedLocale = locale;

		CustomerProfileValue attributeValue = getProfileValueMap().get(getLocalizedAttributeKey(attributeKey, broadenedLocale));
		if (attributeValue == null) { // Remove the variant, if present
			broadenedLocale = LocaleUtils.broadenLocale(broadenedLocale);
			attributeValue = getProfileValueMap().get(getLocalizedAttributeKey(attributeKey, broadenedLocale));
		}
		if (attributeValue == null) { // Remove the country, if present
			broadenedLocale = LocaleUtils.broadenLocale(broadenedLocale);
			attributeValue = getProfileValueMap().get(getLocalizedAttributeKey(attributeKey, broadenedLocale));
		}
		return attributeValue;
	}

	/**
	 * Factory method for creating CustomerProfileValue objects for the given attribute.
	 * Override this method in a subclass if you need to change the persistent type.
	 *
	 * @param attribute the attribute
	 * @return the attribute value
	 */
	protected CustomerProfileValue createAttributeValue(final Attribute attribute) {
		CustomerProfileValue attributeValue = new CustomerProfileValueImpl();
		attributeValue.setAttribute(attribute);
		attributeValue.setAttributeType(attribute.getAttributeType());

		return attributeValue;
	}

	@Override
	public void setProfileValueMap(final Map<String, CustomerProfileValue> profileValueMap) {
		this.profileValueMap = profileValueMap;
	}

	@Override
	public Map<String, CustomerProfileValue> getProfileValueMap() {
		return profileValueMap;
	}

	@Override
	@Transient
	public long getUidPk() {
		return uidPk;
	}

	@Override
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}

	/**
	 * Creates a localized attribute key by combining the given key and the given locale.
	 *
	 * @param key the static portion of the AttributeKey
	 * @param locale the locale to use in creating the locale portion of the AttributeKey
	 * @return an aggregate key to look up a localized AttributeValue in the AttributeValueMap
	 */
	protected String getLocalizedAttributeKey(final String key, final Locale locale) {
		if (locale == null) {
			return key;
		}
		return key + SEPARATOR + locale;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
			.append("profileValueMap", getProfileValueMap())
			.toString();
	}
}
