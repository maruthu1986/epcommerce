/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shoppingcart.validation.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.base.common.dto.StructuredErrorMessageType;
import com.elasticpath.base.common.dto.StructuredErrorResolution;
import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.money.Money;
import com.elasticpath.plugin.payment.dto.PaymentMethod;
import com.elasticpath.service.shoppingcart.PricingSnapshotService;
import com.elasticpath.service.shoppingcart.validation.ShoppingCartValidationContext;

@RunWith(MockitoJUnitRunner.class)
public class PaymentMethodShoppingCartValidatorTest {

	private static final String GUID = "GUID";

	@InjectMocks
	private PaymentMethodShoppingCartValidatorImpl validator;

	@Mock
	private PaymentMethod paymentMethod;

	@Mock
	private ShoppingCart shoppingCart;

	@Mock
	private CartOrder cartOrder;

	@Mock
	private PricingSnapshotService pricingSnapshotService;

	@Mock
	private ShoppingCartValidationContext context;

	@Mock
	private ShoppingCartPricingSnapshot pricingSnapshot;

	@Before
	public void setUp() throws Exception {
		given(cartOrder.getGuid()).willReturn(GUID);
		given(pricingSnapshotService.getPricingSnapshotForCart(shoppingCart)).willReturn(pricingSnapshot);
		given(context.getCartOrder()).willReturn(cartOrder);
		given(context.getShoppingCart()).willReturn(shoppingCart);
	}

	@Test
	public void paymentMethodRequiredAndSpecified() {
		// Given
		given(pricingSnapshot.getSubtotal()).willReturn(BigDecimal.TEN);
		given(cartOrder.getPaymentMethod()).willReturn(paymentMethod);
		// When
		Collection<StructuredErrorMessage> messageCollections = validator.validate(context);

		// Then
		assertThat(messageCollections).isEmpty();
	}

	@Test
	public void paymentMethodRequiredButNotSpecified() {
		StructuredErrorMessage errorMessage = new StructuredErrorMessage(StructuredErrorMessageType.NEEDINFO, "need.payment.method",
				"Payment method must be specified.", Collections.emptyMap(),
				new StructuredErrorResolution(CartOrder.class, cartOrder.getGuid()));

		// Given
		given(pricingSnapshot.getSubtotal()).willReturn(BigDecimal.TEN);
		given(cartOrder.getPaymentMethod()).willReturn(null);

		// When
		Collection<StructuredErrorMessage> messageCollections = validator.validate(context);

		// Then
		assertThat(messageCollections).containsOnly(errorMessage);
	}

	@Test
	public void paymentMethodNotRequired() {
		// Given
		given(pricingSnapshot.getSubtotal()).willReturn(BigDecimal.ZERO);
		given(pricingSnapshot.getShippingCost()).willReturn(Money.valueOf(BigDecimal.ZERO, Currency.getInstance("USD")));

		// When
		Collection<StructuredErrorMessage> messageCollections = validator.validate(context);

		// Then
		assertThat(messageCollections).isEmpty();
	}
}