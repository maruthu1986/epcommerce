/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.presentation.customer.impl;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;

import org.junit.Test;

import com.elasticpath.presentation.customer.CustomerAgePresentation;

/**
 * Tests of {@link CustomerAgePresentationImpl}.
 */
public class CustomerAgePresentationTest {

	private final CustomerAgePresentation agePresentation =  new CustomerAgePresentationImpl();
	
	
	/**
	 * Test that if a customer was born 20 years ago tomorrow, their age is still 19.
	 */
	@Test
	public void testGetCustomerAgeIsCorrectIfTheirBirthdayHasNotPassedThisYear() {
		final int twenty = 20;
		final int customerAge = 19;
		Calendar now = Calendar.getInstance();
		Calendar twentyYearsAgoTomorrow = Calendar.getInstance();
		twentyYearsAgoTomorrow.set(now.get(Calendar.YEAR) - twenty, now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH) + 1);
		assertEquals(customerAge, agePresentation.getAgeInYearsAsOfNow(twentyYearsAgoTomorrow.getTime()));
	}
	
	/**
	 * Test that if a customer was born 20 years ago today, their age is 20.
	 */
	@Test
	public void testGetCustomerAgeIsCorrectOnDateOfBirth() {
		final int twenty = 20;
		final int customerAge = 20;
		Calendar now = Calendar.getInstance();
		Calendar twentyYearsAgoToday = Calendar.getInstance();
		twentyYearsAgoToday.set(now.get(Calendar.YEAR) - twenty, now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
		assertEquals(customerAge, agePresentation.getAgeInYearsAsOfNow(twentyYearsAgoToday.getTime()));
	}
	
	/**
	 * Test that if a customer was born 20 years ago yesterday, their age is 20.
	 */
	@Test
	public void testGetCustomerAgeIsCorrectAfterBirthdayHasPassedThisYear() {
		final int twenty = 20;
		final int customerAge = 20;
		Calendar now = Calendar.getInstance();
		Calendar twentyYearsAgoYesterday = Calendar.getInstance();
		twentyYearsAgoYesterday.set(now.get(Calendar.YEAR) - twenty, now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH) - 1);
		assertEquals(customerAge, agePresentation.getAgeInYearsAsOfNow(twentyYearsAgoYesterday.getTime()));
	}

}
