/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.personalization.impl;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerRepository;
import com.elasticpath.tags.Tag;
import com.elasticpath.tags.service.TagFactory;

/**
 * Create customer first time buyer tag.
 */
@RunWith(MockitoJUnitRunner.class)
public class CustomerFirstTimeBuyerTagStrategyTest {

	private static final String ATTR_PRESENT_MSG = "First time buyer attribute should be present";

	@Mock
	private TagFactory tagFactory;
	@Mock
	private Customer customer;
	@Mock
	private CustomerRepository customerRepository;

	@InjectMocks
	private CustomerFirstTimeBuyerTagStrategy classUnderTest;


	@Test
	public void testTagName() {
		String actualName = classUnderTest.tagName();

		assertEquals(actualName, CustomerFirstTimeBuyerTagStrategy.FIRST_TIME_BUYER);
	}

	@Test
	public void testFirstTimeBuyerTagIsTrueWhenCustomerIsAFirstTimeBuyer() {
		when(customerRepository.isFirstTimeBuyer(customer))
			.thenReturn(true);
		when(tagFactory.createTagFromTagName(CustomerFirstTimeBuyerTagStrategy.FIRST_TIME_BUYER, "true"))
			.thenReturn(new Tag(true));

		Optional<Tag> result = classUnderTest.createTag(customer);

		assertTrue(ATTR_PRESENT_MSG, result.isPresent());
		Tag actual = result.get();
		assertTrue("First time buyer tag should be true", (boolean) actual.getValue());
	}

	@Test
	public void testFirstTimeBuyerTagIsFalseWhenCustomerIsNotAFirstTimeBuyer() {
		when(customerRepository.isFirstTimeBuyer(customer))
			.thenReturn(false);
		when(tagFactory.createTagFromTagName(CustomerFirstTimeBuyerTagStrategy.FIRST_TIME_BUYER, "false"))
				.thenReturn(new Tag(false));

		Optional<Tag> result = classUnderTest.createTag(customer);

		assertTrue(ATTR_PRESENT_MSG, result.isPresent());
		Tag actual = result.get();
		assertFalse("First time buyer tag should be false", (boolean) actual.getValue());
	}
}
