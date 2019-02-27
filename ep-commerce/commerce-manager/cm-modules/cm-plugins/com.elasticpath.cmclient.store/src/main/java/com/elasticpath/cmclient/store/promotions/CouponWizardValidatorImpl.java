/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.store.promotions;

import java.util.Collection;
import java.util.Collections;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.promotions.CouponCollectionModel;
import com.elasticpath.cmclient.core.promotions.CouponValidator;
import com.elasticpath.cmclient.core.promotions.ValidationState;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.service.rules.CouponService;

/**
 * The CouponWizardValidator generally uses the in memory model to validate.
 */
public class CouponWizardValidatorImpl implements CouponValidator {
	private CouponCollectionModel model;

	/**
	 *
	 * @param model the model to set.
	 */
	public void setModel(final CouponCollectionModel model) {
		this.model = model;
	}

	@Override
	public ValidationState isValid(final String couponCode, final String email) {
		//All validation here is in memory
		if (model.isCouponCodeAndEmailAddressExist(couponCode, email)) {
			return new ValidationState(false, ValidationState.CouponErrorState.DUPLICATE);
		}
		return new ValidationState(true, ValidationState.CouponErrorState.NO_ERROR);
	}

	@Override
	public ValidationState isValid(final String couponCode) {
		if (model.isCouponCodeExist(couponCode)) {
			return new ValidationState(false, ValidationState.CouponErrorState.DUPLICATE);
		}
		return new ValidationState(true, ValidationState.CouponErrorState.NO_ERROR);
	}

	@Override
	public ValidationState isBatchValid(final Collection<String> couponCodes) {
		Collection<String> result = doCouponCodesExist(couponCodes);
		if (!result.isEmpty()) {
			return new ValidationState(false, ValidationState.CouponErrorState.DUPLICATE, result);
		}
		return new ValidationState(true, ValidationState.CouponErrorState.NO_ERROR, Collections.emptyList());
	}
	
	private Collection<String> doCouponCodesExist(final Collection<String> couponCodes) {
		CouponService couponService = ServiceLocator.getService(ContextIdNames.COUPON_SERVICE);
		return couponService.findExistingCouponCodes(couponCodes);
	}
}
