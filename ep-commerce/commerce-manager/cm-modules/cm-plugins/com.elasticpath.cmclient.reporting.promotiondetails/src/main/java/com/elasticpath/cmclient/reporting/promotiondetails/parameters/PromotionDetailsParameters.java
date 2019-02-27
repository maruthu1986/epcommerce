/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
/**
 * 
 */
package com.elasticpath.cmclient.reporting.promotiondetails.parameters;

import java.util.Date;

/**
 * A parameters container.
 */
public class PromotionDetailsParameters {

	/**
	 * Parameters Id.
	 */
	public static final String DETAILS_PARAMETERS = 
		"com.elasticpath.cmclient.reporting.promotiondetails.parameters.PromotionDetailsParameters"; //$NON-NLS-1$

	private long storeUidPk;

	private Date startDate;

	private Date endDate;

	private String currencyCode;

	private String promotionCode;

	private String couponCode;

	/**
	 * @return store Uid Pk
	 */
	public long getStoreUidPk() {
		return storeUidPk; // NOPMD
	}

	/**
	 * @param storeUidPk store Uid Pk
	 */
	public void setStoreUidPk(final long storeUidPk) { // NOPMD
		this.storeUidPk = storeUidPk;
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
	 * @return getter for currency code.
	 */
	public String getCurrenyCode() {
		return currencyCode;
	}

	/**
	 * @param currencyCode setter for currency code.
	 */
	public void setCurrencyCode(final String currencyCode) {
		this.currencyCode = currencyCode;
	}

	/**
	 * @param promotionCode the promotion rule code
	 */
	public void setPromotionCode(final String promotionCode) {
		this.promotionCode = promotionCode;
	}

	/**
	 * @return promotion rule code
	 */
	public String getPromotionCode() {
		return promotionCode;
	}

	/**
	 * @param couponCode the coupon code. can be null.
	 */
	public void setCouponCode(final String couponCode) {
		this.couponCode = couponCode;
	}

	/**
	 * @return coupon code
	 */
	public String getCouponCode() {
		return couponCode;
	}

}
