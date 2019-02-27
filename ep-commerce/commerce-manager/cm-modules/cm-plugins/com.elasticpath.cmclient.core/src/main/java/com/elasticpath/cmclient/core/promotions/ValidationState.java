/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.promotions;

import java.util.Collection;
import java.util.Collections;

/**
 * Manage Validation State and reason.
 */
public class ValidationState {

	/**
	 * 
	 * State of coupon used to give reason for error or not if no error.
	 * 
	 */
	public enum CouponErrorState { NO_ERROR, /** no error. */ 
									DUPLICATE, /** duplicates exist somewhere in promotions. */
									SAME_PROMO_DUPLICATE, /** duplicates exist in this promotion. */
									OTHER_PROMO_DUPLICATE /** duplicates exist in some other promotion. */
	};

	private boolean valid;
	private CouponErrorState reason;
	private Collection<String> duplicates = Collections.emptyList();
	
	/**
	 * Constructor.
	 *
	 * @param isValid validity flag.
	 * @param reason reason for error if any.
	 */
	public ValidationState(final boolean isValid, final CouponErrorState reason) {
		this.valid = isValid;
		this.reason = reason;
	}

	/**
	 * Constructor.
	 *
	 * @param isValid validity flag.
	 * @param reason reason for error if any.
	 * @param duplicates if there are duplicate Codes to display.
	 */
	public ValidationState(final boolean isValid, final CouponErrorState reason, final Collection<String> duplicates) {
		this(isValid, reason);
		this.duplicates = duplicates;
	}
	
	/**
	 * validity check.
	 *
	 * @return true if valid.
	 */
	public boolean isValid() {
		return valid;
	}
	
	/**
	 * Get reason for error if any.
	 *
	 * @return reason for error.
	 */
	public CouponErrorState getReason() {
		return reason;
	}

	/**
	 * Get duplicate codes if any.
	 *
	 * @return duplicate codes.
	 */
	public Collection<String> getDuplicates() {
		return duplicates;
	}
}
