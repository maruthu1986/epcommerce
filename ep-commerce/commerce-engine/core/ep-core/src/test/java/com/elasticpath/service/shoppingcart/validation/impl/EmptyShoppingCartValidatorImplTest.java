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
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.shoppingcart.validation.ShoppingCartValidationContext;

@RunWith(MockitoJUnitRunner.class)
public class EmptyShoppingCartValidatorImplTest {

	@InjectMocks
	private EmptyShoppingCartValidatorImpl validator;

	@Mock
	private ShoppingCartValidationContext context;

	@Mock
	private ShoppingCart shoppingCart;

	@Before
	public void setUp() throws Exception {
		given(context.getShoppingCart()).willReturn(shoppingCart);
	}

	@Test
	public void testCartNotEmpty() {
		// Given
		given(shoppingCart.isEmpty()).willReturn(false);

		// When
		Collection<StructuredErrorMessage> messageCollections = validator.validate(context);

		// Then
		assertThat(messageCollections).isEmpty();
	}

	@Test
	public void testCartEmpty() {
		StructuredErrorMessage errorMessage = new StructuredErrorMessage(StructuredErrorMessageType.NEEDINFO, "cart.empty",
				"Shopping cart is empty.", Collections.emptyMap());

		// Given
		given(shoppingCart.isEmpty()).willReturn(true);

		// When
		Collection<StructuredErrorMessage> messageCollections = validator.validate(context);

		// Then
		assertThat(messageCollections).containsOnly(errorMessage);
	}

}