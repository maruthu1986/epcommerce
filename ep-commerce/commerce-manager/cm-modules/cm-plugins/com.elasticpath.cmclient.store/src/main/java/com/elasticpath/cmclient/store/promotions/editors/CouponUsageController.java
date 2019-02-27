/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.store.promotions.editors;

import java.util.Collection;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.common.dto.CouponUsageDtoMediator;
import com.elasticpath.common.dto.CouponUsageModelDto;
import com.elasticpath.commons.constants.ContextIdNames;
/**
 * Controller for managing CouponUsage adds and updates.
 */
public class CouponUsageController {
	
	/**
	 * Update coupon usages given a collection of DTOs.
	 * The CouponUsages must exist or an EpServiceException is thrown.
	 *
	 * @param couponUsageUpdates the Collection of CouponModelDto s.
	 * @throws EpServiceException Exception thrown if update fails.
	 */
	void update(final Collection<CouponUsageModelDto> couponUsageUpdates) throws EpServiceException {
		getCouponUsageDtoMediator().update(couponUsageUpdates);
	}
	
	/**
	 * Add coupon usages given a collection of DTOs.
	 * The CouponUsages must not exist or an EpServiceException is thrown.
	 *
	 * @param addedCouponUsages the Collection of CouponModelDto s.
	 * @param ruleCode the rule code
	 * @throws EpServiceException Exception thrown if update fails.
	 */
	void add(final Collection<CouponUsageModelDto> addedCouponUsages, final String ruleCode) throws EpServiceException {
		getCouponUsageDtoMediator().add(addedCouponUsages, ruleCode);
	}
	
	/**
	 * Get coupon usage mediator.
	 *  
	 * @return the coupon usage mediator 
	 */
	public CouponUsageDtoMediator getCouponUsageDtoMediator() {
		return ServiceLocator.getService(ContextIdNames.COUPON_USAGE_DTO_MEDIATOR);
	}

	
	/**
	 * Validate model dto.
	 * @param addedCouponUsageItems add usages
	 * @param code rule code
	 */
	public void validate(final Collection<CouponUsageModelDto> addedCouponUsageItems, final String code) {
		getCouponUsageDtoMediator().validate(addedCouponUsageItems, code);
	}
}
