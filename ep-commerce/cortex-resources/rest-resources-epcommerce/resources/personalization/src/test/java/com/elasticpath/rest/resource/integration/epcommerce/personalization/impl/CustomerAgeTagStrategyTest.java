/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.personalization.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.tags.Tag;
import com.elasticpath.tags.service.TagFactory;

/**
 * Look up for customer age tag.
 */
@RunWith(MockitoJUnitRunner.class)
public class CustomerAgeTagStrategyTest {

	private static final int AGE_IN_YEARS_INT = 20;

	private static final String AGE_IN_YEARS = String.valueOf(AGE_IN_YEARS_INT);

	@Mock
	private TagFactory tagFactory;
	@Mock
	private Customer customer;

	@InjectMocks
	CustomerAgeTagStrategy classUnderTest;

	@Test
	public void testTagName() {
		String traitName = classUnderTest.tagName();

		assertEquals(traitName, CustomerAgeTagStrategy.CUSTOMER_AGE_YEARS);
	}

	@Test
	public void testNoCustomerAgeTagWhenNoDateOfBirthForCustomer() {
		when(customer.getDateOfBirth())
			.thenReturn(null);

		Optional<Tag> result = classUnderTest.createTag(customer);

		assertFalse("Result should be absent.", result.isPresent());
	}

	@Test
	public void testCustomerAgeTagIsPresentWhenCustomerHasDateOfBirth() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.YEAR, -AGE_IN_YEARS_INT);
		Date a20yearsAgoTime = calendar.getTime();
		when(customer.getDateOfBirth()).thenReturn(a20yearsAgoTime);
		when(tagFactory.createTagFromTagName(CustomerAgeTagStrategy.CUSTOMER_AGE_YEARS, AGE_IN_YEARS))
			.thenReturn(new Tag(AGE_IN_YEARS_INT));

		Optional<Tag> result = classUnderTest.createTag(customer);

		assertTrue("Customer age should have tag.", result.isPresent());
		Tag actual = result.get();
		assertEquals("Tag should have value of customer age", AGE_IN_YEARS_INT, actual.getValue());
	}
}
