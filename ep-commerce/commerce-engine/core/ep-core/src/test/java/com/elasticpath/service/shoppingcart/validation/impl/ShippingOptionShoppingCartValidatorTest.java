/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shoppingcart.validation.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.base.common.dto.StructuredErrorMessageType;
import com.elasticpath.base.common.dto.StructuredErrorResolution;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.shipping.ShippingOptionResult;
import com.elasticpath.service.shipping.ShippingOptionService;
import com.elasticpath.service.shoppingcart.validation.ShoppingCartValidationContext;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;

@RunWith(MockitoJUnitRunner.class)
public class ShippingOptionShoppingCartValidatorTest {

	private static final String SHOPPING_CART_GUID = "shoppingCartGuid";
	private static final String SHIPPING_OPTION_CODE = "shippingOptionCode";
	private static final String CARRIER_CODE = "carrierCode";
	private static final String SHIPPING_OPTION_FIELD = "shipping-option";
	private static final String OTHER_SHIPPING_OPTION_CODE = "otherShippingOptionCode";
	private static final String STORE_CODE = "testStoreCode";
	private static final Locale LOCALE_US = Locale.US;

	@InjectMocks
	private ShippingOptionShoppingCartValidatorImpl validator;

	@Mock
	private ShoppingCartValidationContext context;

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private ShoppingCart shoppingCart;

	@Mock
	private ShippingOptionService shippingOptionService;

	@Mock
	private ShippingOptionResult shippingOptionResult;

	@Mock
	private ShippingOption selectedShippingOption;

	@Mock
	private ShippingOption availableShippingOption;

	@Before
	public void setUp() throws Exception {
		when(context.getShoppingCart()).thenReturn(shoppingCart);
		when(shoppingCart.getGuid()).thenReturn(SHOPPING_CART_GUID);
		when(shoppingCart.requiresShipping()).thenReturn(true);
		when(shoppingCart.getStore().getCode()).thenReturn(STORE_CODE);
		when(shoppingCart.getShopper().getLocale()).thenReturn(LOCALE_US);
		when(shoppingCart.getSelectedShippingOption()).thenReturn(Optional.of(selectedShippingOption));
		when(selectedShippingOption.getCode()).thenReturn(SHIPPING_OPTION_CODE);
		when(selectedShippingOption.getCarrierCode()).thenReturn(Optional.of(CARRIER_CODE));
		when(availableShippingOption.getCode()).thenReturn(SHIPPING_OPTION_CODE);
		when(availableShippingOption.getCarrierCode()).thenReturn(Optional.of(CARRIER_CODE));
		when(shippingOptionService.getShippingOptions(Matchers.anyObject())).thenReturn(shippingOptionResult);
		when(shippingOptionResult.isSuccessful()).thenReturn(true);
		when(shippingOptionResult.getAvailableShippingOptions()).thenReturn(Collections.singletonList(availableShippingOption));
	}

	/**
	 * Validate the structured error message received when the shopping cart does not have a shipping option.
	 */
	@Test
	public void verifyStructuredErrorMessageWhenNoShippingOptionExists() {
		// Given
		when(shoppingCart.getSelectedShippingOption()).thenReturn(Optional.empty());

		// When
		Collection<StructuredErrorMessage> messageCollections = validator.validate(context);

		// Then
		StructuredErrorMessage errorMessage = new StructuredErrorMessage(StructuredErrorMessageType.NEEDINFO, "need.shipping.option",
				"Shipping option must be specified.", Collections.emptyMap(),
				new StructuredErrorResolution(ShoppingCart.class, SHOPPING_CART_GUID));
		assertThat(messageCollections).containsOnly(errorMessage);
	}

	/**
	 * Validate the structured error message received when the shopping cart has an invalid shipping option.
	 */
	@Test
	public void verifyStructuredErrorMessageWhenShippingOptionsUnavailable() {
		// Given
		when(shippingOptionResult.isSuccessful()).thenReturn(false);
		when(shippingOptionResult.getErrorDescription(false)).thenReturn("No error information provided.");

		// When
		Collection<StructuredErrorMessage> messageCollections = validator.validate(context);

		// Then
		StructuredErrorMessage errorMessage = new StructuredErrorMessage(StructuredErrorMessageType.ERROR, "shipping.options.unavailable",
				"There was a problem retrieving shipping options from the shipping service: No error information provided.", Collections.emptyMap(),
				new StructuredErrorResolution(ShoppingCart.class, SHOPPING_CART_GUID));
		assertThat(messageCollections).containsOnly(errorMessage);
	}

	/**
	 * Validate the structured error message received when the shopping cart has an invalid shipping option.
	 */
	@Test
	public void verifyStructuredErrorMessageWhenShippingOptionIsInvalid() {
		// Given
		when(availableShippingOption.getCode()).thenReturn(OTHER_SHIPPING_OPTION_CODE);

		// When
		Collection<StructuredErrorMessage> messageCollections = validator.validate(context);

		// Then
		StructuredErrorMessage errorMessage = new StructuredErrorMessage(StructuredErrorMessageType.ERROR, "invalid.shipping.option",
				"Selected shipping option is not valid.", ImmutableMap.of(SHIPPING_OPTION_FIELD, SHIPPING_OPTION_CODE),
				new StructuredErrorResolution(ShoppingCart.class, SHOPPING_CART_GUID));
		assertThat(messageCollections).containsOnly(errorMessage);
	}

	/**
	 * Validate cart with valid shipping information.
	 */
	@Test
	public void verifyNoStructuredErrorMessagesWhenCartMeetsCheckoutRequirements() {
		// When
		Collection<StructuredErrorMessage> messageCollections = validator.validate(context);

		// Then
		assertThat(messageCollections).isEmpty();
	}

}
