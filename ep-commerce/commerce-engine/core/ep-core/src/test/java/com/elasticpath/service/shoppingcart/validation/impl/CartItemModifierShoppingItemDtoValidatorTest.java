/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shoppingcart.validation.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.domain.cartmodifier.CartItemModifierField;
import com.elasticpath.domain.cartmodifier.CartItemModifierGroup;
import com.elasticpath.domain.cartmodifier.CartItemModifierType;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.service.shoppingcart.validation.ShoppingItemDtoValidationContext;
import com.elasticpath.validation.impl.ConstraintViolationTransformerImpl;
import com.elasticpath.validation.service.impl.CartItemModifierFieldValidationServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class CartItemModifierShoppingItemDtoValidatorTest {

	private static final int MEDIUM_MAX_SIZE = 10;

	@InjectMocks
	private CartItemModifierShoppingItemDtoValidatorImpl validator;

	@Mock
	private ShoppingItemDtoValidationContext context;

	private final CartItemModifierFieldValidationServiceImpl validationService = new CartItemModifierFieldValidationServiceImpl();

	private final ConstraintViolationTransformerImpl constraintViolationTransformer = new ConstraintViolationTransformerImpl();

	@Mock
	private ProductSku productSku;

	@Mock
	private Product product;

	@Mock
	private ProductType productType;

	@Mock
	private CartItemModifierGroup modifierGroup;

	@Mock
	private ShoppingItemDto shoppingItemDto;

	@Mock
	private CartItemModifierField referentField;

	@Before
	public void setUp() {

		given(context.getProductSku()).willReturn(productSku);
		given(productSku.getProduct()).willReturn(product);
		given(product.getProductType()).willReturn(productType);
		given(productType.getCartItemModifierGroups()).willReturn(ImmutableSet.of(modifierGroup));
		given(context.getShoppingItemDto()).willReturn(shoppingItemDto);

		validationService.setConstraintViolationTransformer(constraintViolationTransformer);

		validator.setCartItemModifierFieldValidationService(validationService);

	}

	@Test
	public void testItemModifierIncorrect() {

		// Given
		Map<String, String> idMap = new HashMap<>();
		idMap.put("{field.invalid.decimal.format}", "field.invalid.decimal.format");

		constraintViolationTransformer.setIdMap(idMap);

		String decimalFieldName = "amount";
		String fieldValue = "123abc";

		Map<String, String> itemsToValidate = new HashMap<>();
		itemsToValidate.put(decimalFieldName, fieldValue);

		Set<CartItemModifierField> referentFields = new HashSet<>();

		given(referentField.getCode()).willReturn(decimalFieldName);
		given(referentField.getFieldType()).willReturn(CartItemModifierType.DECIMAL);
		given(referentField.isRequired()).willReturn(true);
		given(referentField.getMaxSize()).willReturn(MEDIUM_MAX_SIZE);

		referentFields.add(referentField);


		given(modifierGroup.getCartItemModifierFields()).willReturn(referentFields);

		given(shoppingItemDto.getItemFields()).willReturn(itemsToValidate);


		// When
		Collection<StructuredErrorMessage> actualStructuredErrorMessages = validator.validate(context);


		// Then
		assertThat(actualStructuredErrorMessages).size().isEqualTo(1);

		StructuredErrorMessage actualStructuredErrorMessage = actualStructuredErrorMessages.iterator().next();

		String expectedDebugMessage = String.format("'%s' value '%s' must be a decimal.", decimalFieldName, fieldValue);

		assertThat(actualStructuredErrorMessage.getData().get("field-name")).isEqualTo(decimalFieldName);
		assertThat(actualStructuredErrorMessage.getDebugMessage()).isEqualTo(expectedDebugMessage);
		assertThat(actualStructuredErrorMessage.getMessageId()).isEqualTo("field.invalid.decimal.format");
	}

	@Test
	public void testItemModifierCorrect() {
		// Given
		Map<String, String> idMap = new HashMap<>();
		idMap.put("{field.invalid.decimal.format}", "field.invalid.decimal.format");

		constraintViolationTransformer.setIdMap(idMap);

		String decimalFieldName = "amount";
		String fieldValue = "123";

		Map<String, String> itemsToValidate = new HashMap<>();
		itemsToValidate.put(decimalFieldName, fieldValue);

		Set<CartItemModifierField> referentFields = new HashSet<>();

		given(referentField.getCode()).willReturn(decimalFieldName);
		given(referentField.getFieldType()).willReturn(CartItemModifierType.DECIMAL);
		given(referentField.isRequired()).willReturn(true);
		given(referentField.getMaxSize()).willReturn(MEDIUM_MAX_SIZE);

		referentFields.add(referentField);


		given(modifierGroup.getCartItemModifierFields()).willReturn(referentFields);

		given(shoppingItemDto.getItemFields()).willReturn(itemsToValidate);


		// When
		Collection<StructuredErrorMessage> actualStructuredErrorMessages = validator.validate(context);


		// Then
		assertThat(actualStructuredErrorMessages).isEmpty();

	}
}

