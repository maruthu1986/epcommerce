/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
/**
 * 
 */
package com.elasticpath.cmclient.reporting.promotionusage.parameters;

import java.util.Date;

import com.elasticpath.cmclient.reporting.common.ReportParameters;

/**
 * A parameters container.
 *
 */
public class PromotionUsageParameters implements ReportParameters {

	private long storeUidPk;

	private String store;
	
	private Date startDate;
	
	private Date endDate;

	private Integer promotionType;
	
	private boolean onlyPromotionsWithCouponCodes;

	/**
	 * @return store Uid Pk
	 */
	public long getStoreUidPk() {
		return storeUidPk;
	}
	/**
	 * @param storeUidPk store Uid Pk
	 */
	public void setStoreUidPk(final long storeUidPk) {
		this.storeUidPk = storeUidPk;
	}

	/**
	 * Gets the store name.
	 *
	 * @return String the store name
	 */
	public String getStore() {
		return store;
	}

	/**
	 * Sets the store name.
	 *
	 * @param store the store name
	 */
	public void setStore(final String store) {
		this.store = store;
	}

	/**
	 * @return the purchased from date
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate the purchased from date
	 */
	public void setStartDate(final Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the purchased to date
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate the purchased to date
	 */
	public void setEndDate(final Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return promotion type (scenario type {@link com.elasticpath.domain.rules.RuleScenarios}) 
	 *         or null if no specific type is required
	 */
	public Integer getPromotionType() {
		if (promotionType != null && promotionType == 0) {
			return null;
		}
		return promotionType;
	}
	
	/**
	 * @param promotionType promotion type (scenario type {@link com.elasticpath.domain.rules.RuleScenarios}) 
	 *        or null if no specific type is required
	 */
	public void setPromotionType(final Integer promotionType) {
		this.promotionType = promotionType;
	}
	/**
	 * @return true if only promotions with coupon codes are required to be in report
	 */
	public boolean isOnlyPromotionsWithCouponCodes() {
		return onlyPromotionsWithCouponCodes;
	}
	/**
	 * @param onlyPromotionsWithCouponCodes true if only promotions with coupon codes are required to be in report
	 */
	public void setOnlyPromotionsWithCouponCodes(
			final boolean onlyPromotionsWithCouponCodes) {
		this.onlyPromotionsWithCouponCodes = onlyPromotionsWithCouponCodes;
	}
	
}
