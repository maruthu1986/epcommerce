/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shoppingcart.validation.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.Collection;
import java.util.Collections;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.ConstituentItem;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.service.shoppingcart.validation.ShoppingItemDtoValidationContext;

@RunWith(MockitoJUnitRunner.class)
public class BundleStructureShoppingItemDtoValidatorTest {

	private static final String SKU_CODE = "skuCode";

	@InjectMocks
	private BundleStructureShoppingItemDtoValidatorImpl validator;

	@Mock
	private ShoppingItemDtoValidationContext context;

	@Mock
	private ProductSku productSku;

	@Mock
	private ShoppingItemDto shoppingItemDto;

	@Mock
	private ShoppingItemDto shoppingItemDto1;

	@Mock
	private ProductBundle productBundle;

	@Mock
	private BundleConstituent bundleConstituent;

	@Mock
	private ConstituentItem constituentItem;

	@Before
	public void setUp() {
		given(context.getProductSku()).willReturn(productSku);
		given(productSku.getProduct()).willReturn(productBundle);
		given(productSku.getSkuCode()).willReturn(SKU_CODE);
		given(productBundle.getConstituents()).willReturn(Collections.singletonList(bundleConstituent));
		given(bundleConstituent.getConstituent()).willReturn(constituentItem);
		given(constituentItem.isProductSku()).willReturn(true);
		given(context.getShoppingItemDto()).willReturn(shoppingItemDto);
		given(shoppingItemDto.getConstituents()).willReturn(Collections.singletonList(shoppingItemDto1));
		given(shoppingItemDto1.getSkuCode()).willReturn(SKU_CODE);
	}

	@Test
	public void testBundleStructureCorrect() {

		// Given
		given(constituentItem.getCode()).willReturn(SKU_CODE);

		// When
		Collection<StructuredErrorMessage> messageCollections = validator.validate(context);

		// Then
		assertThat(messageCollections).isEmpty();
	}

	@Test
	public void testBundleStructureIncorrect() {
		StructuredErrorMessage structuredErrorMessage = new StructuredErrorMessage("item.invalid.bundle.structure",
				"Requested item configuration does not have a valid bundle structure.",
				ImmutableMap.of("item-code", SKU_CODE));

		// Given
		given(constituentItem.getCode()).willReturn("skuCode1");

		// When
		Collection<StructuredErrorMessage> messageCollections = validator.validate(context);

		// Then
		assertThat(messageCollections).containsOnly(structuredErrorMessage);
	}
}
