/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.rules.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.commons.exception.InvalidBusinessStateException;


/**
 * This exception is thrown when attempt is made to apply an invalid coupon.
 */
public class CouponNotValidException extends EpServiceException implements InvalidBusinessStateException {

	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;

	private final List<StructuredErrorMessage> structuredErrorMessages;

	/**
	 * Constructor.
	 *
	 * @param couponCode error message
	 */
	public CouponNotValidException(final String couponCode) {
		super(String.format("Coupon '%s' is not valid", couponCode));
		Map<String, String> data = new HashMap<>();
		data.put("coupon-code", couponCode);
		structuredErrorMessages = Collections.singletonList(new StructuredErrorMessage("coupon.not.valid",
				String.format("Coupon '%s' is not valid", couponCode), data));

	}

	@Override
	public List<StructuredErrorMessage> getStructuredErrorMessages() {
		return structuredErrorMessages;
	}

}
