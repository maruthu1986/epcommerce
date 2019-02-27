/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.domain.coupon.specifications;

import com.elasticpath.domain.rules.Coupon;

/**
 * A potential coupon usage.
 */
public class PotentialCouponUse {

	private Coupon coupon;

	private String storeCode;

	private String customerEmailAddress;

	/**
	 * Create potential coupon usage dto.
	 * 
	 * @param coupon coupon
	 * @param storeCode store code
	 * @param customerEmailAddress customer email address
	 */
	public PotentialCouponUse(final Coupon coupon, final String storeCode, final String customerEmailAddress) {
		setCoupon(coupon);
		setCustomerEmailAddress(customerEmailAddress);
		setStoreCode(storeCode);
	}

	/**
	 * @return coupon
	 */
	public Coupon getCoupon() {
		return coupon;
	}

	/**
	 * @param coupon set the coupon on the dto with coupon passed in.
	 */
	protected void setCoupon(final Coupon coupon) {
		this.coupon = coupon;
	}

	/**
	 * @return store code
	 */
	public String getStoreCode() {
		return storeCode;
	}

	/**
	 * @param storeCode set store code on dto
	 */
	protected void setStoreCode(final String storeCode) {
		this.storeCode = storeCode;
	}

	/**
	 * @return email address
	 */
	public String getCustomerEmailAddress() {
		return customerEmailAddress;
	}

	/**
	 * @param customerEmailAddress set customer email address on dto.
	 */
	protected void setCustomerEmailAddress(final String customerEmailAddress) {
		this.customerEmailAddress = customerEmailAddress;
	}

}
