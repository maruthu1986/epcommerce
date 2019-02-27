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
public class ShippingAddressShoppingCartValidatorTest {
	private static final String SHOPPING_CART_GUID = "shoppingCartGuid";

	@InjectMocks
	private ShippingAddressShoppingCartValidatorImpl validator;

	@Mock
	private ShoppingCart shoppingCart;

	@Mock
	private ShoppingCartValidationContext context;

	@Mock
	private Address shippingAddress;

	@Before
	public void setUp() throws Exception {
		given(context.getShoppingCart()).willReturn(shoppingCart);
		given(shoppingCart.requiresShipping()).willReturn(true);
	}

	@Test
	public void testShippingNoAddressSpecifiedNotShippableItem() {
		// Given
		given(shoppingCart.requiresShipping()).willReturn(false);

		// When
		Collection<StructuredErrorMessage> messageCollections = validator.validate(context);

		// Then
		assertThat(messageCollections).isEmpty();
	}

	@Test
	public void testShippingAddressSpecified() {
		// Given
		given(shoppingCart.getShippingAddress()).willReturn(shippingAddress);

		// When
		Collection<StructuredErrorMessage> messageCollections = validator.validate(context);

		// Then
		assertThat(messageCollections).isEmpty();
	}

	@Test
	public void testShippingAddressNotSpecified() {
		// Given
		given(shoppingCart.getShippingAddress()).willReturn(null);

		// When
		Collection<StructuredErrorMessage> messageCollections = validator.validate(context);

		// Then
		StructuredErrorMessage errorMessage = new StructuredErrorMessage(StructuredErrorMessageType.NEEDINFO, "need.shipping.address",
				"Shipping address must be specified.", Collections.emptyMap(),
				new StructuredErrorResolution(ShoppingCart.class, SHOPPING_CART_GUID));
		assertThat(messageCollections).containsOnly(errorMessage);
	}
}