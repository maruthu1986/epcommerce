/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shoppingcart.validation.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.ImmutableMap;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.service.shoppingcart.validation.ShoppingItemDtoValidationContext;

@RunWith(MockitoJUnitRunner.class)
public class QuantityShoppingItemDtoValidatorTest {

	private static final String SKU_CODE = "skuCode";

	@InjectMocks
	private QuantityShoppingItemDtoValidatorImpl validator;

	@Mock
	private ShoppingItemDtoValidationContext context;

	@Mock
	private ShoppingItemDto shoppingItemDto;

	@Mock
	private ProductSku productSku;

	@Before
	public void setUp() {
		given(context.getShoppingItemDto()).willReturn(shoppingItemDto);
		given(context.getProductSku()).willReturn(productSku);
		given(productSku.getSkuCode()).willReturn(SKU_CODE);
	}

	@Test
	public void testShoppingItemHasInvalidQuantity() {
		int quantity = 0;

		int minimumQuantity = 1;
		StructuredErrorMessage structuredErrorMessage = new StructuredErrorMessage("field.invalid.minimum.value",
				String.format("'quantity' value '%d' must be greater than or equal to '%d'.", quantity, minimumQuantity),
				ImmutableMap.of("item-code", SKU_CODE,
						"field-name", "quantity",
						"min-value", String.format("%d", minimumQuantity),
						"invalid-value", String.format("%d", quantity)));

		// Given
		given(shoppingItemDto.getQuantity()).willReturn(quantity);
		given(context.isUpdate()).willReturn(false);

		// When
		Collection<StructuredErrorMessage> messageCollections = validator.validate(context);

		// Then
		assertThat(messageCollections).containsOnly(structuredErrorMessage);
	}

	@Test
	public void testShoppingItemHasValidQuantity() {
		// Given
		given(shoppingItemDto.getQuantity()).willReturn(1);

		given(context.isUpdate()).willReturn(false);

		// When
		Collection<StructuredErrorMessage> messageCollections = validator.validate(context);

		// Then
		assertThat(messageCollections).isEmpty();
	}

	@Test
	public void testShoppingItemHasValidQuantityUpdate() {
		// Given
		given(shoppingItemDto.getQuantity()).willReturn(0);
		given(context.isUpdate()).willReturn(true);

		// When
		Collection<StructuredErrorMessage> messageCollections = validator.validate(context);

		// Then
		assertThat(messageCollections).isEmpty();
	}
}
