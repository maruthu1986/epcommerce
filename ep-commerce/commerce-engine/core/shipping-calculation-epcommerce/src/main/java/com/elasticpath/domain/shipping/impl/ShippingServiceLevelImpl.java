/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.domain.shipping.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.log4j.Logger;
import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.Dependent;
import org.apache.openjpa.persistence.ElementDependent;
import org.apache.openjpa.persistence.FetchAttribute;
import org.apache.openjpa.persistence.FetchGroup;
import org.apache.openjpa.persistence.jdbc.EagerFetchMode;
import org.apache.openjpa.persistence.jdbc.ElementClassCriteria;
import org.apache.openjpa.persistence.jdbc.ElementJoinColumn;
import org.apache.openjpa.persistence.jdbc.FetchMode;
import org.apache.openjpa.persistence.jdbc.ForeignKey;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.EpShippingContextIdNames;
import com.elasticpath.domain.impl.AbstractLegacyEntityImpl;
import com.elasticpath.domain.misc.LocalizedProperties;
import com.elasticpath.domain.misc.LocalizedPropertyValue;
import com.elasticpath.domain.misc.impl.ShippingServiceLevelLocalizedPropertyValueImpl;
import com.elasticpath.domain.shipping.ShippingCostCalculationMethod;
import com.elasticpath.domain.shipping.ShippingRegion;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.persistence.support.FetchGroupConstants;
import com.elasticpath.shipping.connectivity.dto.ShippingAddress;

/**
 * A ShippingRegion represents a region that will be associated with one or more shipping services. For now, it is composed of country and a
 * subcountry, i.e. CA(country) and BC(subcountry).
 */
@Entity
@Table(name = ShippingServiceLevelImpl.TABLE_NAME)
@FetchGroup(name = FetchGroupConstants.ORDER_DEFAULT, attributes = {
			@FetchAttribute(name = "carrier"),
			@FetchAttribute(name = "localizedPropertiesMap") },
			fetchGroups = { FetchGroupConstants.DEFAULT	})
@DataCache(enabled = true)
@SuppressWarnings("PMD.GodClass")
public class ShippingServiceLevelImpl extends AbstractLegacyEntityImpl implements ShippingServiceLevel {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private static final Logger LOG = Logger.getLogger(ShippingServiceLevelImpl.class);

	/**
	 * The name of the table & generator to use for persistence.
	 */
	public static final String TABLE_NAME = "TSHIPPINGSERVICELEVEL";

	private ShippingRegion shippingRegion;

	private ShippingCostCalculationMethod shippingCostCalculationMethod;

	private LocalizedProperties localizedProperties;
	
	private Map<String, LocalizedPropertyValue> localizedPropertiesMap = new HashMap<>();

	private String carrier;
	
	private String code;

	private long uidPk;

	private Store store;

	private boolean enabled;

	private Date lastModifiedDate;

	private String guid;

	/**
	 * Get the shipping region associated with this <code>ShippingServiceLevel</code>.
	 *
	 * @return the shippingRegion.
	 */
	@Override
	@ManyToOne(targetEntity = ShippingRegionImpl.class)
	@JoinColumn(name = "SHIPPING_REGION_UID", nullable = false)
	public ShippingRegion getShippingRegion() {
		return this.shippingRegion;
	}

	/**
	 * Set the shipping region associated with this <code>ShippingServiceLevel</code>.
	 *
	 * @param shippingRegion the shipping region to be associated with this shippingServiceLevel.
	 */
	@Override
	public void setShippingRegion(final ShippingRegion shippingRegion) {
		this.shippingRegion = shippingRegion;
	}

	/**
	 * Get the shipping cost calculation method associated with this <code>ShippingServiceLevel</code>.
	 *
	 * @return shippingCostCalculationMethod.
	 */
	@Override
	@ManyToOne(targetEntity = AbstractShippingCostCalculationMethodImpl.class, cascade = { CascadeType.ALL })
	@JoinColumn(name = "SCCM_UID", nullable = false)
	@ForeignKey
	@Dependent
	public ShippingCostCalculationMethod getShippingCostCalculationMethod() {
		return this.shippingCostCalculationMethod;
	}

	/**
	 * Set the shipping cost calculation method associated with this <code>ShippingServiceLevel</code>.
	 *
	 * @param shippingCostCalculationMethod the shipping cost calculation method to be associated with this shippingServiceLevel.
	 */
	@Override
	public void setShippingCostCalculationMethod(final ShippingCostCalculationMethod shippingCostCalculationMethod) {
		this.shippingCostCalculationMethod = shippingCostCalculationMethod;
	}

	/**
	 * Returns the <code>LocalizedProperties</code>.
	 *
	 * @return the <code>LocalizedProperties</code>
	 */
	@Override
	@Transient
	public LocalizedProperties getLocalizedProperties() {
		if (localizedProperties == null) {
			this.localizedProperties = getBean(ContextIdNames.LOCALIZED_PROPERTIES);
			this.localizedProperties.setLocalizedPropertiesMap(getLocalizedPropertiesMap(), 
					EpShippingContextIdNames.SHIPPING_SERVICE_LEVEL_LOCALIZED_PROPERTY_VALUE);
		}
		return this.localizedProperties;
	}

	/**
	 * Sets the <code>LocalizedProperties</code>.
	 *
	 * @param localizedProperties the <code>LocalizedProperties</code>
	 */
	public void setLocalizedProperties(final LocalizedProperties localizedProperties) {
		this.localizedProperties = localizedProperties;
		if (localizedProperties != null) {
			setLocalizedPropertiesMap(localizedProperties.getLocalizedPropertiesMap());
		}
	}

	/**
	 * @return Returns the carrier.
	 */
	@Override
	@Basic
	@Column(name = "CARRIER")
	public String getCarrier() {
		return this.carrier;
	}

	/**
	 * @param carrier The carrier to set.
	 */
	@Override
	public void setCarrier(final String carrier) {
		this.carrier = carrier;
	}
	
	/**
	 * Returns the shipping service level code.
	 *
	 * @return the shipping service level code
	 */
	@Override
	@Basic(optional = false)
	@Column(name = "CODE", unique = true, nullable = false)
	public String getCode() {
		return this.code;
	}

	/**
	 * Sets the shipping service level code.
	 *
	 * @param code the shipping service level code
	 */
	@Override
	public void setCode(final String code) {
		this.code = code;
	}

	/**
	 * Get the localized properties map.
	 * 
	 * @return the map
	 */
	@OneToMany(targetEntity = ShippingServiceLevelLocalizedPropertyValueImpl.class, fetch = FetchType.EAGER,
			cascade = { CascadeType.ALL }, orphanRemoval = true)
	@EagerFetchMode(FetchMode.PARALLEL)
	@MapKey(name = "localizedPropertyKey")
	@ElementJoinColumn(name = "OBJECT_UID", referencedColumnName = "UIDPK", nullable = false)
	@ElementClassCriteria
	@ElementDependent
	protected Map<String, LocalizedPropertyValue> getLocalizedPropertiesMap() {
		return this.localizedPropertiesMap;
	}

	/**
	 * Set the localized properties map.
	 * 
	 * @param localizedPropertiesMap the map
	 */
	@Override
	public void setLocalizedPropertiesMap(final Map<String, LocalizedPropertyValue> localizedPropertiesMap) {
		this.localizedPropertiesMap = localizedPropertiesMap;
	}

	/**
	 * @return the guid
	 */
	@Override
	@Basic
	@Column(name = "GUID", length = GUID_LENGTH, nullable = false)
	public String getGuid() {
		return guid;
	}

	/**
	 * @param guid the guid to set
	 */
	@Override
	public void setGuid(final String guid) {
		this.guid = guid;
	}

	/**
	 * Gets the unique identifier for this domain model object.
	 * 
	 * @return the unique identifier.
	 */
	@Override
	@Id
	@Column(name = "UIDPK")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS", pkColumnName = "ID", valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME)
	public long getUidPk() {
		return this.uidPk;
	}

	/**
	 * Sets the unique identifier for this domain model object.
	 * 
	 * @param uidPk the new unique identifier.
	 */
	@Override
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}

	@Override
	@ManyToOne(targetEntity = StoreImpl.class, cascade = { CascadeType.MERGE, CascadeType.REFRESH })
	@JoinColumn(name = "STORE_UID", nullable = false)
	public Store getStore() {
		return this.store;
	}

	@Override
	public void setStore(final Store store) {
		this.store = store;
	}

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
	@Override
	@Transient
	public String getDisplayName(final Locale locale, final boolean fallback) {
		String displayName = this.getLocalizedProperties().getValue(LOCALIZED_PROPERTY_NAME, locale);
		if (displayName == null) {
			if (fallback) {
				displayName = this.getLocalizedProperties().getValue(LOCALIZED_PROPERTY_NAME, this.getStoreDefaultLocale());
				if (LOG.isInfoEnabled()) {
					LOG.info("No localized property for displayName for ShippingServiceLevel.code=" + this.getCode()
						+ " and supplied locale: " + locale
						+ ". falling back to default store locale: " + this.getStoreDefaultLocale()
						+ " displayName = " + displayName);
				}
			} else {
				LOG.warn("No localized property display name for displayName for ShippingServiceLevel.code="
						+ this.getCode() + " and supplied locale: " 
						+ locale
						+ ". not falling back to default store locale. displayName will be null");
			}
		}

		return displayName;
	}

	/**
	 * Return the <code>ShippingServiceLevel</code> name for the given locale.
	 * Falls back to the Store's default locale if not found for the given locale.
	 *
	 * This implementation calls getDisplayName()
	 *
	 * @param locale the locale for which to retrieve the name
	 * @return The name of the ShippingServiceLevel
	 * @deprecated
	 */
	@Override
	@SuppressWarnings("deprecation")
	@Deprecated
	@Transient
	public String getName(final Locale locale) {
		return getDisplayName(locale, true);
	}
	
	/**
	 * Get the containing store's default locale.
	 * 
	 * @return the containing store's default locale
	 */
	@Transient
	protected Locale getStoreDefaultLocale() {
		return this.getStore().getDefaultLocale();
	}

	@Override
	@Basic
	@Column(name = "ENABLED", nullable = false)
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * @return the date when this shipping service level was last modified
	 */
	@Override
	@Basic(optional = true)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_MODIFIED_DATE", insertable = false, nullable = false)
	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	/**
	 * @param lastModifiedDate the date when this shipping service level was last modified
	 */
	@Override
	public void setLastModifiedDate(final Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	@Override
	public boolean isApplicable(final String storeCode, final ShippingAddress shippingAddress) {
		return isEnabled() && getStore().getCode().equalsIgnoreCase(storeCode) && getShippingRegion().isInShippingRegion(shippingAddress);
	}

}
