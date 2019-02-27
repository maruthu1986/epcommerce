/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.store.promotions;

import java.util.Collection;
import java.util.Collections;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.promotions.CouponValidator;
import com.elasticpath.cmclient.core.promotions.ValidationState;
import com.elasticpath.common.dto.CouponModelDto;
import com.elasticpath.common.dto.CouponUsageModelDto;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.service.rules.CouponService;

/**
 * Validator for Coupon Editor.
 */
public class CouponEditorValidatorImpl implements CouponValidator {
	private CouponPageModel model;
	private String ruleCode;

	/**
	 * @param model the model to set.
	 */
	public void setModel(final CouponPageModel model) {
		this.model = model;
	}

	/**
	 * @param ruleCode for this promotion.
	 */
	public void setRuleCode(final String ruleCode) {
		this.ruleCode = ruleCode;
	}

	@Override
	public ValidationState isValid(final String couponCode, final String email) {
		// if it's an exact local match then it's not valid But if only the couponCode matches then its ok.
		for (CouponUsageModelDto dto : model.getAddedCouponUsageItems()) {
			if (StringUtils.equals(couponCode, dto.getCouponCode()) && StringUtils.equals(email, dto.getEmailAddress())) {
				return new ValidationState(false, ValidationState.CouponErrorState.SAME_PROMO_DUPLICATE);
			}
		}

		for (CouponUsageModelDto dto : model.getUpdatedCouponUsageItems()) {
			if (StringUtils.equals(couponCode, dto.getCouponCode()) && StringUtils.equals(email, dto.getEmailAddress())) {
				return new ValidationState(false, ValidationState.CouponErrorState.SAME_PROMO_DUPLICATE);
			}
		}

		//could still be a double match to items that have not been edited or added but are still associated with this promo
		if (!validForDatabase(couponCode, email, ruleCode)) {
			return new ValidationState(false, ValidationState.CouponErrorState.SAME_PROMO_DUPLICATE);
		}

		//could also still be a single match to items that are not associated with this promo.		
		if (!validForDatabase(couponCode, ruleCode)) {
			return new ValidationState(false, ValidationState.CouponErrorState.OTHER_PROMO_DUPLICATE);
		}

		return new ValidationState(true, ValidationState.CouponErrorState.NO_ERROR);
	}

	@Override
	public ValidationState isValid(final String couponCode) {
		for (CouponModelDto dto : model.getAddedCouponItems()) {
			if (StringUtils.equals(couponCode, dto.getCouponCode())) {
				return new ValidationState(false, ValidationState.CouponErrorState.SAME_PROMO_DUPLICATE);
			}
		}

		for (CouponModelDto dto : model.getUpdatedCouponItems()) {
			if (StringUtils.equals(couponCode, dto.getCouponCode())) {
				return new ValidationState(false, ValidationState.CouponErrorState.SAME_PROMO_DUPLICATE);
			}
		}

		//could also still be a match to items that not in the cache but in db.
		if (!validForDatabase(couponCode)) {
			return new ValidationState(false, ValidationState.CouponErrorState.DUPLICATE);
		}

		return new ValidationState(true, ValidationState.CouponErrorState.NO_ERROR);
	}

	@Override
	public ValidationState isBatchValid(final Collection<String> couponCodes) {
		// anything local has been checked.. db only.
		Collection<String> duplicates = validForDatabase(couponCodes, ruleCode);
		if (!duplicates.isEmpty()) {
			return new ValidationState(false, ValidationState.CouponErrorState.DUPLICATE, duplicates);
		}
		return new ValidationState(true, ValidationState.CouponErrorState.NO_ERROR, Collections.emptyList());
	}

	/*
	 *  Return true if cannot find the couponCode | email pair in the db FOR this ruleCode.
	 */
	private boolean validForDatabase(final String couponCode, final String email, final String ruleCode) {
		CouponService couponService = ServiceLocator.getService(ContextIdNames.COUPON_SERVICE);
		return !couponService.doesCouponCodeEmailPairExistForThisRuleCode(couponCode, email, ruleCode);
	}

	/*
	 *  Return true if cannot find the couponCode for any ruleCode Other than this one.
	 */
	private boolean validForDatabase(final String couponCode, final String ruleCode) {
		CouponService couponService = ServiceLocator.getService(ContextIdNames.COUPON_SERVICE);
		return couponService.doesCouponCodeOnlyExistForThisRuleCode(couponCode, ruleCode);
	}

	/*
	 *  Return true if not in db.
	 */
	private boolean validForDatabase(final String couponCode) {
		CouponService couponService = ServiceLocator.getService(ContextIdNames.COUPON_SERVICE);
		return (couponService.findByCouponCode(couponCode) == null);
	}

	/*
	 * Return collection of codes that were found in the db for ruleCodes Other than this one. 
	 */
	private Collection<String> validForDatabase(final Collection<String> couponCodes, final String ruleCode) {
		CouponService couponService = ServiceLocator.getService(ContextIdNames.COUPON_SERVICE);
		return couponService.findExistingCouponCodes(couponCodes, ruleCode);
	}

}
