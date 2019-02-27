/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shoppingcart.validation.impl;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.base.common.dto.StructuredErrorMessageType;
import com.elasticpath.base.common.dto.StructuredErrorResolution;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.shoppingcart.validation.ShoppingCartValidationContext;
import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collection;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class EmailAddressShoppingCartValidatorImplTest {

	private static final String RESERVED_EMAIL = "reserved@reserved.com";

	private static final String GUID = "GUID";

	@InjectMocks
	private EmailAddressShoppingCartValidatorImpl validator;

	@Mock
	private ShoppingCartValidationContext context;

	@Mock
	private Shopper shopper;

	@Mock
	private Customer customer;

	@Mock
	private ShoppingCart shoppingCart;

	@Before
	public void setUp() throws Exception {

		validator.setReservedEmails(ImmutableSet.of(RESERVED_EMAIL));

		given(context.getShoppingCart()).willReturn(shoppingCart);
		given(shoppingCart.getShopper()).willReturn(shopper);
		given(shopper.getCustomer()).willReturn(customer);
		given(customer.getGuid()).willReturn(GUID);
	}

	@Test
	public void testEmailSet() {
		// Given
		given(customer.getEmail()).willReturn("info@some.com");

		// When
		Collection<StructuredErrorMessage> messageCollections = validator.validate(context);

		// Then
		assertThat(messageCollections).isEmpty();
	}

	@Test
	public void testEmailNotSet() {
		StructuredErrorMessage errorMessage = new StructuredErrorMessage(StructuredErrorMessageType.NEEDINFO, "need.email",
				"Customer email address must be specified.", Collections.emptyMap(),
				new StructuredErrorResolution(Customer.class, GUID));

		// Given
		given(customer.getEmail()).willReturn(null);

		// When
		Collection<StructuredErrorMessage> messageCollections = validator.validate(context);

		// Then
		assertThat(messageCollections).containsOnly(errorMessage);
	}

	@Test
	public void testEmailSetButIncorrect() {

		StructuredErrorMessage errorMessage = new StructuredErrorMessage(StructuredErrorMessageType.NEEDINFO, "need.email",
				"Customer email address must be specified.", Collections.emptyMap(),
				new StructuredErrorResolution(Customer.class, GUID));

		// Given
		given(customer.getEmail()).willReturn("k.boom.info");

		// When
		Collection<StructuredErrorMessage> messageCollections = validator.validate(context);

		// Then
		assertThat(messageCollections).containsOnly(errorMessage);
	}

	@Test
	public void testEmailSetButReserved() {

		StructuredErrorMessage errorMessage = new StructuredErrorMessage(StructuredErrorMessageType.NEEDINFO, "need.email",
				"Customer email address must be specified.", Collections.emptyMap(),
				new StructuredErrorResolution(Customer.class, GUID));

		// Given
		given(customer.getEmail()).willReturn(RESERVED_EMAIL);

		// When
		Collection<StructuredErrorMessage> messageCollections = validator.validate(context);

		// Then
		assertThat(messageCollections).containsOnly(errorMessage);
	}
}

