/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.presentation.customer.impl;

import java.util.Date;

import org.joda.time.LocalDate;
import org.joda.time.Years;

import com.elasticpath.presentation.customer.CustomerAgePresentation;

/**
 * Represent customer age.
 */
public class CustomerAgePresentationImpl implements CustomerAgePresentation {

	@Override
	public int getAgeInYearsAsOfNow(final Date dateOfBirth) {
		LocalDate birthdate = LocalDate.fromDateFields(dateOfBirth);
		LocalDate now = new LocalDate();
		Years age = Years.yearsBetween(birthdate, now);
		
		return age.getYears();
	}

}
