/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.schema.uri;

/**
 * URI Builder for coupons resource.
 */
@Deprecated
public interface CouponsUriBuilder extends ReadFromOtherUriBuilder<CouponsUriBuilder>  {

	/**
	 * Set the coupon ID.
	 *
	 * @param couponId the coupon ID
	 * @return this builder
	 */
	CouponsUriBuilder setCouponId(String couponId);


	/**
	 * Set the info URI.
	 *
	 * @return this builder
	 */
	CouponsUriBuilder setInfoUri();


	/**
	 * Set the form URI.
	 *
	 * @return this builder.
	 */
	CouponsUriBuilder setFormUri();
}
