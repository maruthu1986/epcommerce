/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.personalization.impl;

import static com.elasticpath.rest.resource.integration.epcommerce.personalization.impl.CustomerRegisteredTagStrategy.REGISTERED_CUSTOMER;
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
import com.elasticpath.tags.Tag;
import com.elasticpath.tags.service.TagFactory;

/**
 * Test for {@link CustomerRegisteredTagStrategy}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CustomerRegisteredTagStrategyTest {

	@Mock
	private TagFactory tagFactory;
	@Mock
	private Customer customer;

	@InjectMocks
	private CustomerRegisteredTagStrategy classUnderTest;


	@Test
	public void testTagName() {
		String actual = classUnderTest.tagName();

		assertEquals(REGISTERED_CUSTOMER, actual);
	}

	@Test
	public void testRegisteredCustomerWhenCustomerIsNotRegistered() {
		when(customer.isRegistered()).thenReturn(false);
		when(tagFactory.createTagFromTagName(REGISTERED_CUSTOMER, "false"))
			.thenReturn(new Tag(false));

		Optional<Tag> result = classUnderTest.createTag(customer);

		assertTrue(REGISTERED_CUSTOMER + " tag should be returned", result.isPresent());
		Tag actual = result.get();
		assertEquals(REGISTERED_CUSTOMER + " should be  false", Boolean.FALSE, actual.getValue());
	}

	@Test
	public void testRegisteredCustomerWhenCustomerExistsAndIsRegistered() {
		when(customer.isRegistered()).thenReturn(true);
		when(tagFactory.createTagFromTagName(REGISTERED_CUSTOMER, "true"))
			.thenReturn(new Tag(true));

		Optional<Tag> result = classUnderTest.createTag(customer);

		assertTrue(REGISTERED_CUSTOMER + " should be returned", result.isPresent());
		Tag actual = result.get();
		assertEquals(REGISTERED_CUSTOMER + " should be in true", Boolean.TRUE, actual.getValue());
	}
}
