/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;

import com.google.common.collect.ImmutableList;
import io.reactivex.Single;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.advise.Message;
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.sku.ProductSkuRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.StructuredErrorMessageTransformer;
import com.elasticpath.service.shoppingcart.validation.ProductSkuValidationContext;
import com.elasticpath.service.shoppingcart.validation.impl.AddProductSkuToCartValidationServiceImpl;

/**
 * Tests {@link AddToCartAdvisorServiceImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class AddToCartAdvisorServiceImplTest {

	private static final String STORE_CODE = "store";
	private static final String SKU_CODE = "sku";
	private static final String SHOPPING_CART_GUID = "cart";

	@InjectMocks
	private AddToCartAdvisorServiceImpl advisorService;

	@Mock
	private ProductSkuRepository productSkuRepository;

	@Mock
	private AddProductSkuToCartValidationServiceImpl addToCartValidationService;

	@Mock
	private StructuredErrorMessageTransformer structuredErrorMessageTransformer;

	@Mock
	private ShoppingCartRepository shoppingCartRepository;

	@Mock
	private ShoppingCart shoppingCart;

	@Mock
	private ProductSku productSku;

	@Mock
	private ProductSkuValidationContext validationContext;

	@Before
	public void setup() {


		given(shoppingCartRepository.getDefaultShoppingCartGuid()).willReturn(Single.just(SHOPPING_CART_GUID));

		given(shoppingCartRepository.getShoppingCart(SHOPPING_CART_GUID)).willReturn(Single.just(shoppingCart));

		given(productSkuRepository.getProductSkuWithAttributesByCodeAsSingle(SKU_CODE)).willReturn(Single.just(productSku));

		given(addToCartValidationService.buildContext(any(), any(), any(), any())).willReturn(validationContext);
	}

	/**
	 * Test item is purchasable.
	 */
	@Test
	public void shouldBeTrueWhenItemIsPurchasable() {

		given(addToCartValidationService.validate(validationContext))
				.willReturn(Collections.emptyList());

		given(structuredErrorMessageTransformer.transform(Collections.emptyList(), SHOPPING_CART_GUID))
				.willReturn(Collections.emptyList());

		advisorService.validateItemPurchasable(STORE_CODE, SKU_CODE)
				.test()
				.assertNoErrors()
				.assertValueCount(0);
	}

	/**
	 * Test item is not purchasable.
	 */
	@Test
	public void shouldBeFalseWhenItemIsNotPurchasable() {

		ImmutableList<StructuredErrorMessage> errorList = ImmutableList.of(new StructuredErrorMessage("error", "message", Collections.emptyMap()));

		given(addToCartValidationService.validate(validationContext))
				.willReturn(errorList);

		given(structuredErrorMessageTransformer.transform(errorList, SHOPPING_CART_GUID))
				.willReturn(ImmutableList.of(Message.builder()
						.build()));

		advisorService.validateItemPurchasable(STORE_CODE, SKU_CODE)
				.test()
				.assertNoErrors()
				.assertValueCount(1);

	}

	@Test
	public void testValidateQuantitySuccess() {
		LineItemEntity lineItemEntity = mock(LineItemEntity.class);
		when(lineItemEntity.getQuantity()).thenReturn(1);

		advisorService.validateLineItemEntity(lineItemEntity)
				.test()
				.assertNoErrors()
				.assertValueCount(0);
	}

	@Test
	public void testValidateQuantityFailureWhenNull() {
		LineItemEntity lineItemEntity = mock(LineItemEntity.class);
		when(lineItemEntity.getQuantity()).thenReturn(null);

		advisorService.validateLineItemEntity(lineItemEntity)
				.test()
				.assertError(ResourceOperationFailure.class);
	}
}