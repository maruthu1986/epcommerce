/**
 * Copyright (c) Elastic Path Software Inc., 2010
 */
package com.elasticpath.domain.rules.impl;

import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.apache.openjpa.persistence.DataCache;
import org.apache.openjpa.persistence.Externalizer;
import org.apache.openjpa.persistence.Factory;
import org.apache.openjpa.persistence.Persistent;

import com.elasticpath.domain.rules.CouponConfig;
import com.elasticpath.domain.rules.CouponUsageType;
import com.elasticpath.persistence.api.AbstractEntityImpl;

/**
 * Coupon Code implementation to represents the coupon codes that can be used with
 * a coupon rule element.
 */
@Entity
@Table(name = CouponConfigImpl.TABLE_NAME)
@DataCache(enabled = false)
public class CouponConfigImpl extends AbstractEntityImpl implements CouponConfig {

	private static final long serialVersionUID = 3221226272580116976L;

	private long uidPk;
	private String ruleCode;
	private int usageLimit;
	private CouponUsageType couponUsageType;
	private String guid;
	private int durationDays;
	private boolean limitedDuration;
	private boolean multiUsePerOrder;

	/** Database Table. */
	public static final String TABLE_NAME = "TCOUPONCONFIG";
	
	@Override
	@Id
	@Column(name = "UIDPK")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = TABLE_NAME)
	@TableGenerator(name = TABLE_NAME, table = "JPA_GENERATED_KEYS", pkColumnName = "ID", valueColumnName = "LAST_VALUE", pkColumnValue = TABLE_NAME)
	public long getUidPk() {
		return uidPk;
	}

	@Override
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}

	@Override
	public void setRuleCode(final String ruleCode) {
		this.ruleCode = ruleCode;
	}

	@Override
	@Basic
	@Column(name = "RULECODE", nullable = false)
	public String getRuleCode() {
		return ruleCode;
	}

	@Override
	public void setUsageLimit(final int usageLimit) {
		this.usageLimit = usageLimit;
	}

	@Override
	@Transient
	public boolean isUnlimited() {
		return getUsageLimit() == Integer.MAX_VALUE;
	}
	
	@Override
	@Transient
	public void setUnlimited() {
		setUsageLimit(Integer.MAX_VALUE);
	}
	
	@Override
	@Basic
	@Column(name = "USAGE_LIMIT")
	public int getUsageLimit() {
		return usageLimit;
	}

	@Override
	public void setUsageType(final CouponUsageType couponUsageType) {
		this.couponUsageType = couponUsageType;
	}

	@Override
	@Persistent
	@Externalizer("getName")
	@Factory("CouponUsageType.getEnum")
	@Column(name = "USAGE_TYPE")
	public CouponUsageType getUsageType() {
		return couponUsageType;
	}

	/**
	 * @return the GUID.
	 */
	@Override
	@Basic
	@Column(name = "GUID")
	public String getGuid() {
		return guid;
	}

	/**
	 * @param guid the GUID to set.
	 */
	@Override
	public void setGuid(final String guid) {
		this.guid = guid;
	}

	/**
 	 * Determines whether the given object is equal to this CouponConfig.
	 * Two CouponConfigs are considered equal if their guids are equal.
	 * 
	 * @param other the object to check against
	 * @return true if the other object is equal to this one
	 */
	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		
		if (!(other instanceof CouponConfigImpl)) {
			return false;
		}
		
		return Objects.equals(guid, ((CouponConfigImpl) other).guid);
	}

	/**
	 * Hashcode based on guid.
	 * 
	 * @return the hashcode
	 */
	@Override
	public int hashCode() {
		return Objects.hash(guid);
	}

	@Override
	@Basic
	@Column(name = "DURATION_DAYS")
	public int getDurationDays() {
		return durationDays;
	}

	@Override
	@Basic
	@Column(name = "LIMITED_DURATION")
	public boolean isLimitedDuration() {
		return limitedDuration;
	}

	@Override
	public void setDurationDays(final int durationDays) {
		this.durationDays = durationDays;
	}

	@Override
	public void setLimitedDuration(final boolean limitedDuration) {
		this.limitedDuration = limitedDuration;
	}

	/**
	 *
	 * @return use once per order flag.
	 */
	@Override
	@Basic
	@Column(name = "MULTI_USE_PER_ORDER")
	public boolean isMultiUsePerOrder() {
		return multiUsePerOrder;
	}

	/**
	 *
	 * @param multiUsePerOrder True if this coupon should only be used once for any order.
	 */
	@Override
	public void setMultiUsePerOrder(final boolean multiUsePerOrder) {
		this.multiUsePerOrder = multiUsePerOrder;
	}
}
