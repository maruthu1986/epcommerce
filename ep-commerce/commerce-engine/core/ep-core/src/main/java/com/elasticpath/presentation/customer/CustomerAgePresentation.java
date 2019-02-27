/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.presentation.customer;

import java.util.Date;

/**
 * Represents Customer age.
 */
public interface CustomerAgePresentation {
	
	/**
	 * Get the age of a person in years as of today.
	 *
	 * @param dateOfBirth date of birth.
	 * @return age in years.
	 */
	int getAgeInYearsAsOfNow(Date dateOfBirth);

}
