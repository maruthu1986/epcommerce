/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.store.promotions.editors;

import java.util.Collection;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.common.dto.CouponDtoMediator;
import com.elasticpath.common.dto.CouponModelDto;
import com.elasticpath.commons.constants.ContextIdNames;
/**
 * Controller For managing Coupon adds and updates.
 */
public class CouponController {
	
	/**
	 * Update coupons given a collection of DTOs.
	 * The Coupons must exist or an EpServiceException is thrown.
	 *
	 * @param couponUpdates the Collection of CouponModelDto s.
	 * @throws EpServiceException Exception thrown if update fails.
	 */
	void update(final Collection<CouponModelDto> couponUpdates) throws EpServiceException {
		getMediator().update(couponUpdates);
	}
	
	/**
	 * Add coupons given a collection of DTOs.
	 * The Coupons must not exist or an EpServiceException is thrown.
	 *
	 * @param addedCoupons the Collection of CouponModelDto s.
	 * @param ruleCode the rule code
	 * @throws EpServiceException Exception thrown if update fails.
	 */
	void add(final Collection<CouponModelDto> addedCoupons, final String ruleCode) throws EpServiceException {
		getMediator().add(addedCoupons, ruleCode);
	}

	/**
	 * Get the coupon dto mediator.
	 * 
	 * @return the mediator
	 */
	public CouponDtoMediator getMediator() {
		return ServiceLocator.getService(ContextIdNames.COUPON_DTO_MEDIATOR);
	}

	/**
	 * Validate model items.
	 *
	 * @param addedCouponItems coupons to add
	 * @param ruleCode rule code
	 */
	public void validate(final Collection<CouponModelDto> addedCouponItems, final String ruleCode) {
		getMediator().validate(addedCouponItems, ruleCode);
	}
}
