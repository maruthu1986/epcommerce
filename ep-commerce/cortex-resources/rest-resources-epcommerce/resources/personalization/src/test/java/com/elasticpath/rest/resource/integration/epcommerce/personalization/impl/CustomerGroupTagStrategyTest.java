/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.personalization.impl;

import static com.elasticpath.rest.resource.integration.epcommerce.personalization.impl.CustomerGroupTagStrategy.CUSTOMER_GROUP;
import static com.elasticpath.rest.resource.integration.epcommerce.personalization.impl.CustomerGroupTagStrategy.NAME_SEPARATOR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerGroup;
import com.elasticpath.tags.Tag;
import com.elasticpath.tags.service.TagFactory;

/**
 * Test class for {@link CustomerGroupTagStrategy}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CustomerGroupTagStrategyTest {

	private static final String CUSTOMER_GROUP_NAME_1 = "Group1";
	private static final String CUSTOMER_GROUP_NAME_2 = "Group2";
	private static final String CUSTOMER_GROUP_NAME_3 = "Group3";

	@Mock
	private TagFactory tagFactory;
	@Mock
	private Customer customer;

	@InjectMocks
	private CustomerGroupTagStrategy classUnderTest;

	@Test
	public void testTagName() {
		String tagName = classUnderTest.tagName();

		assertEquals(tagName, CUSTOMER_GROUP);
	}

	@Test
	public void testGetCustomerGroupTag() {
		arrangeSuccessFlowMocks();

		Optional<Tag> result = classUnderTest.createTag(customer);

		assertTrue("Expected tag to be present", result.isPresent());
		Tag actual = result.get();
		String expectedValue = CUSTOMER_GROUP_NAME_1;
		assertEquals("Expected customerGroup does not match result.", expectedValue, actual.getValue());
	}

	@Test
	public void verifyMultipleCustomerGroupsHandledCorrectly() {
		List<CustomerGroup> customerGroups = arrangeMultipleCustomerGroups();
		arrangeSuccessFlowMocks(customerGroups);

		Optional<Tag> result = classUnderTest.createTag(customer);

		assertTrue("Expected tag to be present", result.isPresent());
		Tag actual = result.get();
		String expectedTagValue = Joiner.on(NAME_SEPARATOR)
				.join(CUSTOMER_GROUP_NAME_1, CUSTOMER_GROUP_NAME_2, CUSTOMER_GROUP_NAME_3);
		assertEquals("Expected customerGroup does not match result.", expectedTagValue, actual.getValue());
	}

	@Test
	public void verifyDisabledCustomerGroupsNotIncludedInSubjectAttributeValue() {
		List<CustomerGroup> customerGroups = arrangeMultipleCustomerGroups();
		CustomerGroup disabledGroup = customerGroups.get(1);
		when(disabledGroup.isEnabled()).thenReturn(Boolean.FALSE);
		arrangeSuccessFlowMocks(customerGroups);

		Optional<Tag> result = classUnderTest.createTag(customer);

		assertTrue("Expected tag to be present", result.isPresent());
		Tag actual = result.get();
		assertFalse("The resulting value should not contain the group name of the disabled Customer Group.",
				actual.getValue().toString().contains(disabledGroup.getName()));
	}

	private void arrangeSuccessFlowMocks() {
		List<CustomerGroup> customerGroups = Collections.singletonList(createMockCustomerGroup(CUSTOMER_GROUP_NAME_1));
		arrangeSuccessFlowMocks(customerGroups);
	}

	private List<CustomerGroup> arrangeMultipleCustomerGroups() {
		return ImmutableList.of(
			createMockCustomerGroup(CUSTOMER_GROUP_NAME_1),
			createMockCustomerGroup(CUSTOMER_GROUP_NAME_2),
			createMockCustomerGroup(CUSTOMER_GROUP_NAME_3));
	}

	private CustomerGroup createMockCustomerGroup(final String groupName) {
		CustomerGroup customerGroup = mock(CustomerGroup.class);
		when(customerGroup.getName()).thenReturn(groupName);
		when(customerGroup.isEnabled()).thenReturn(Boolean.TRUE);
		return customerGroup;
	}

	private void arrangeSuccessFlowMocks(final List<CustomerGroup> customerGroups) {
		when(customer.getCustomerGroups()).thenReturn(customerGroups);
		String value = customerGroups.stream()
				.filter(CustomerGroup::isEnabled)
				.map(CustomerGroup::getName)
				.collect(Collectors.joining(NAME_SEPARATOR));

		when(tagFactory.createTagFromTagName(CUSTOMER_GROUP, value))
			.thenReturn(new Tag(value));
	}
}
