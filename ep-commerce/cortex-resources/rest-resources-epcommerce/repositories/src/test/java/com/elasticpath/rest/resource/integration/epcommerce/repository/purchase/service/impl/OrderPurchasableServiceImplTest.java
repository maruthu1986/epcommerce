/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.integration.epcommerce.repository.purchase.service.impl;

import static org.mockito.BDDMockito.given;

import java.util.Collections;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import io.reactivex.Single;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.rest.advise.Message;
import com.elasticpath.rest.definition.orders.OrderIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.StructuredErrorMessageTransformer;
import com.elasticpath.service.shoppingcart.validation.PurchaseCartValidationService;
import com.elasticpath.service.shoppingcart.validation.ShoppingCartValidationContext;

/**
 * Unit tests for {@link OrderPurchasableServiceImpl}.
 */

@RunWith(MockitoJUnitRunner.class)
public class OrderPurchasableServiceImplTest {

	private static final String ORDER_ID = "ORDER_ID";

	private static final String SCOPE = "SCOPE";

	private static final String SHOPPING_CART_ID = "Shopping cat Id";

	private static final OrderIdentifier ORDER = OrderIdentifier.builder()
			.withOrderId(StringIdentifier.of(ORDER_ID))
			.withScope(StringIdentifier.of(SCOPE))
			.build();

	private static final String ERROR_ID = "error.id";

	private static final String ERROR_MESSAGE = "error.message";

	private static final Map<String, String> ERROR_DATA = Collections.emptyMap();

	private static final Message MESSAGE = Message.builder().withId(ERROR_ID).withDebugMessage(ERROR_MESSAGE).withData(ERROR_DATA).build();

	private static final StructuredErrorMessage STRUCTURED_ERROR_MESSAGE = new StructuredErrorMessage(ERROR_ID, ERROR_MESSAGE, ERROR_DATA);

	@InjectMocks
	private OrderPurchasableServiceImpl service;

	@Mock
	private CartOrderRepository cartOrderRepository;

	@Mock
	private PurchaseCartValidationService validationService;

	@Mock
	private StructuredErrorMessageTransformer messageConverter;

	@Mock
	private CartOrder cartOrder;

	@Mock
	private ShoppingCart shoppingCart;

	@Mock
	private ShoppingCartValidationContext context;


	@Before
	public void setup() {
		given(cartOrderRepository.findByGuidAsSingle(SCOPE, ORDER_ID))
				.willReturn(Single.just(cartOrder));
		given(cartOrderRepository.getEnrichedShoppingCartSingle(SCOPE, cartOrder))
				.willReturn(Single.just(shoppingCart));

		given((shoppingCart).getGuid()).willReturn(SHOPPING_CART_ID);
		given(validationService.buildContext(Matchers.anyObject(), Matchers.anyObject())).willReturn(context);
	}

	@Test
	public void testOrderIsPurchasable() {
		// Given
		given(validationService.validate(context))
				.willReturn(Collections.emptyList());

		// When
		service.validateOrderPurchasable(ORDER)

				// Then
				.test()
				.assertNoErrors()
				.assertValueCount(0);


	}

	@Test
	public void testOrderIsNotPurchasable() {
		// Given
		given(validationService.validate(context))
				.willReturn(ImmutableList.of(STRUCTURED_ERROR_MESSAGE));
		given(messageConverter.transform(ImmutableList.of(STRUCTURED_ERROR_MESSAGE), SHOPPING_CART_ID))
				.willReturn(ImmutableList.of(MESSAGE));


		// When
		service.validateOrderPurchasable(ORDER)

				//Then
				.test()
				.assertNoErrors()
				.assertValueCount(1)
				.assertValue(MESSAGE);


	}

}
