/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shoppingcart.validation.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.Collection;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.base.common.dto.StructuredErrorMessageType;
import com.elasticpath.base.common.dto.StructuredErrorResolution;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.shoppingcart.validation.ShoppingCartValidationContext;

@RunWith(MockitoJUnitRunner.class)
public class BillingAddressShoppingCartValidatorTest {

	private static final String GUID = "GUID";

	@InjectMocks
	private BillingAddressShoppingCartValidatorImpl validator;

	@Mock
	private Address address;

	@Mock
	private ShoppingCart shoppingCart;

	@Mock
	private ShoppingCartValidationContext context;

	@Before
	public void setUp() throws Exception {
		given(context.getShoppingCart()).willReturn(shoppingCart);
		given(shoppingCart.getGuid()).willReturn(GUID);
	}

	@Test
	public void billAddressSpecified() {
		// Given
		given(shoppingCart.getBillingAddress()).willReturn(address);

		// When
		Collection<StructuredErrorMessage> messageCollections = validator.validate(context);

		// Then
		assertThat(messageCollections).isEmpty();

	}

	@Test
	public void billingAddressNotSpecified() {
		// Given
		given(shoppingCart.getBillingAddress()).willReturn(null);

		// When
		Collection<StructuredErrorMessage> messageCollections = validator.validate(context);

		// Then
		StructuredErrorMessage errorMessage = new StructuredErrorMessage(StructuredErrorMessageType.NEEDINFO, "need.billing.address",
				"Billing address must be specified.", Collections.emptyMap(),
				new StructuredErrorResolution(ShoppingCart.class, shoppingCart.getGuid()));
		assertThat(messageCollections).containsOnly(errorMessage);
	}

}