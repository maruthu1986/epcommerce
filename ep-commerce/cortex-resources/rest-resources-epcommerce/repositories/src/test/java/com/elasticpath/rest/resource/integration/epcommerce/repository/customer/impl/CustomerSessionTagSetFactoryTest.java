/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.customer.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.rest.identity.Subject;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.CustomerTagStrategy;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.UserTraitsToTagSetTransformer;
import com.elasticpath.tags.TagSet;

@RunWith(MockitoJUnitRunner.class)
public class CustomerSessionTagSetFactoryTest {

	@Mock
	ResourceOperationContext operationContext;
	@Mock
	CustomerTagStrategyRegistry tagStrategyRegistry;
	@Mock
	UserTraitsToTagSetTransformer transformer;

	@InjectMocks
	CustomerSessionTagSetFactory classUnderTest;

	@Mock
	Customer customer;
	@Mock
	CustomerTagStrategy tagStrategy;
	@Mock
	Subject subject;


	@Test
	public void testCreateTagSet() throws Exception {
		TagSet tagSet = new TagSet();
		when(operationContext.getSubject())
			.thenReturn(subject);
		when(transformer.transformUserTraitsToTagSet(subject))
			.thenReturn(tagSet);
		when(tagStrategyRegistry.getStrategies())
			.thenReturn(Arrays.asList(tagStrategy, tagStrategy));

		TagSet actual = classUnderTest.createTagSet(customer);

		assertNotNull(actual);
		assertSame(tagSet, actual);
		verify(tagStrategy, times(2)).populate(any(Customer.class), any(TagSet.class));
	}

	@Test
	public void testCreateTagSetWithNoSubject() throws Exception {
		when(tagStrategyRegistry.getStrategies())
			.thenReturn(Arrays.asList(tagStrategy, tagStrategy));

		TagSet actual = classUnderTest.createTagSet(customer);

		assertNotNull(actual);
		verifyZeroInteractions(transformer);
		verify(tagStrategy, times(2)).populate(any(Customer.class), any(TagSet.class));
	}

	@Test
	public void testCreateTagSetWithNoSubjectOrStrategies() throws Exception {
		when(tagStrategyRegistry.getStrategies())
			.thenReturn(Collections.emptyList());

		TagSet actual = classUnderTest.createTagSet(customer);

		assertNotNull(actual);
		verifyZeroInteractions(transformer);
		verifyZeroInteractions(tagStrategy);
	}
}