/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.personalization.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

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
 * Test for {@link CustomerGenderTagStrategy}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CustomerGenderTagStrategyTest {

	private static final char NON_EXISTENT_GENDER_CODE = (char) 0;

	private static final Character FEMALE = 'F';

	@Mock
	private TagFactory tagFactory;

	@InjectMocks
	private CustomerGenderTagStrategy classUnderTest;

	@Mock
	private Customer customer;


	@Test
	public void testNoCustomerGenderTagIsReturnedWhenItNonExistent() {
		when(customer.getGender()).thenReturn(NON_EXISTENT_GENDER_CODE);

		Optional<Tag> result = classUnderTest.createTag(customer);

		assertFalse("Resulting tag should be empty.", result.isPresent());
	}

	@Test
	public void testCustomerGenderTagIsReturnedWhenItExists() {
		when(customer.getGender()).thenReturn(FEMALE);
		when(tagFactory.createTagFromTagName(CustomerGenderTagStrategy.CUSTOMER_GENDER, FEMALE.toString()))
			.thenReturn(new Tag(FEMALE));

		Optional<Tag> result = classUnderTest.createTag(customer);

		assertTrue(CustomerGenderTagStrategy.CUSTOMER_GENDER + " should be returned", result.isPresent());
		Tag actual = result.get();
		assertEquals(CustomerGenderTagStrategy.CUSTOMER_GENDER + " should be female", FEMALE, actual.getValue());
	}
}
