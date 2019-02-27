/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.promotions;

import java.util.Collection;

/**
 * Interface for describing validation for CouponCodes and Emails.
 */
public interface CouponValidator {
	
	/**
	 * Check Validity of a couponCode-email pair.
	 * Implementations should:
	 * 	1)Check in memory & db as required
	 * 	2)Maintain efficiency - eg. defer to Batch check for db if possible.
	 *
	 * @param couponCode the code to check.
	 * @param email the email to check.
	 * @return a validationState item.
	 */
	ValidationState isValid(String couponCode, String email);
	
	/**
	 * Check Validity of a couponCode without email.
	 * Implementations should:
	 * 	1) Check in memory & db as required.
	 *  2) Maintain efficiency - eg. defer to Batch check for db if possible.
	 *
	 * @param couponCode the code to check.
	 * @return a validationState item.
	 */
	ValidationState isValid(String couponCode);
	
	/**
	 * Check Validity of a set of couponCodes.
	 * Use where Batch calls can be made for efficiency.  
	 * When several coupon codes have been added and are tested against the db.
	 *
	 * @param couponCodes the code to check.
	 * @return a validationState item.
	 */
	ValidationState isBatchValid(Collection<String> couponCodes);

}
