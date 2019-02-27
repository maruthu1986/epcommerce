/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */

package com.elasticpath.rest.resource.integration.epcommerce.repository.customer.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.convert.converter.Converter;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.rest.definition.profiles.ProfileEntity;

/**
 * Test that the Profile Entity Converter behaves as expected.
 */
@RunWith(MockitoJUnitRunner.class)
public class ProfileEntityConverterTest {

	@Mock
	private Customer customer;

	private final Converter<Customer, ProfileEntity> converter = new ProfileEntityConverter();

	@Test
	public void testForValidConversion() {
		when(customer.getGuid()).thenReturn(UUID.randomUUID().toString());
		when(customer.getFirstName()).thenReturn(StringUtils.EMPTY);
		when(customer.getLastName()).thenReturn(StringUtils.EMPTY);

		ProfileEntity result = converter.convert(customer);
		assertThat(result.getGivenName()).isEmpty();
		assertThat(result.getFamilyName()).isEmpty();
	}
}