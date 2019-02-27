/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.shipping;

import java.util.Date;
import java.util.Locale;
import java.util.Map;

import com.elasticpath.domain.misc.LocalizedProperties;
import com.elasticpath.domain.misc.LocalizedPropertyValue;
import com.elasticpath.domain.store.Store;
import com.elasticpath.persistence.api.Entity;
import com.elasticpath.shipping.connectivity.dto.ShippingAddress;

/**
 * A ShippingServiceLevel object represents a shipping option associated with a shipping region.
 */
public interface ShippingServiceLevel extends Entity {

	/**
	 * The name of localized property -- name.
	 */
	String LOCALIZED_PROPERTY_NAME = "shippingServiceLevelDisplayName";

	/**
	 * Get the shipping region associated with this <code>ShippingServiceLevel</code>.
	 *
	 * @return the shippingRegion.
	 */
	ShippingRegion getShippingRegion();

	/**
	 * Set the shipping region associated with this <code>ShippingServiceLevel</code>.
	 *
	 * @param shippingRegion the shipping region to be associated with this shippingServiceLevel.
	 */
	void setShippingRegion(ShippingRegion shippingRegion);

	/**
	 * Get the shipping cost calculation method associated with this <code>ShippingServiceLevel</code>.
	 *
	 * @return shippingCostCalculationMethod.
	 */
	ShippingCostCalculationMethod getShippingCostCalculationMethod();

	/**
	 * Set the shipping cost calculation method associated with this <code>ShippingServiceLevel</code>.
	 *
	 * @param shippingCostCalculationMethod the shipping cost calculation method to be associated with this shippingServiceLevel.
	 */
	void setShippingCostCalculationMethod(ShippingCostCalculationMethod shippingCostCalculationMethod);

	/**
	 * Returns the <code>LocalizedProperties</code>.
	 *
	 * @return the <code>LocalizedProperties</code>
	 */
	LocalizedProperties getLocalizedProperties();

	/**
	 * Set the localized properties map.
	 *
	 * @param localizedPropertiesMap the map
	 */
	void setLocalizedPropertiesMap(Map<String, LocalizedPropertyValue> localizedPropertiesMap);

	/**
	 * @return Returns the carrier.
	 */
	String getCarrier();

	/**
	 * @param carrier The carrier to set.
	 */
	void setCarrier(String carrier);

	/**
	 * Returns the shipping service level code.
	 *
	 * @return the shipping service level code
	 */
	String getCode();

	/**
	 * Sets the shipping service level code.
	 *
	 * @param code the shipping service level code
	 */
	void setCode(String code);

	/**
	 * Return the <code>ShippingServiceLevel</code> name for the given locale.
	 * Falls back to the Store's default locale if not found for the given locale.
	 *
	 * @param locale the locale for which to retrieve the name
	 * @return The name of the ShippingServiceLevel
	 * @deprecated call getDisplayName(Locale, boolean) instead
	 */
	@Deprecated
	String getName(Locale locale);

	/**
	 * @return Returns the store.
	 */
	Store getStore();

	/**
	 * @param store The store to set.
	 */
	void setStore(Store store);

	/**
	 * Get the DisplayName for this ShippingServiceLevel in the
	 * given Locale, falling back to the Store's default locale
	 * if the ShippingServicelevel has no DisplayName in the given locale.
	 *
	 * @param locale the locale for which the DisplayName should be returned
	 * @param fallback whether the display name should be returned for the Store's
	 * default locale if the name for the given locale is not found.
	 * @return the display name for the given locale, or the fallback locale if requested,
	 * or null if none can be found.
	 */
	String getDisplayName(Locale locale, boolean fallback);

	/**
	 * Checks if the service level is enabled.
	 *
	 * @return true if the service level is active
	 */
	boolean isEnabled();

	/**
	 * Sets the service level enabled/disabled.
	 *
	 * @param enabled the boolean value
	 */
	void setEnabled(boolean enabled);

	/**
	 * @return the date when this shipping service level was last modified
	 */
	Date getLastModifiedDate();

	/**
	 * @param lastModifiedDate the date when this shipping service level was last modified
	 */
	void setLastModifiedDate(Date lastModifiedDate);

	/**
	 * This is applicable only if it is enabled, matches the given store code, and the given shipping address is in this region.
	 * @param storeCode the store code
	 * @param shippingAddress the shipping address
	 * @return true iff it is applicable
	 */
	boolean isApplicable(String storeCode, ShippingAddress shippingAddress);

}
