/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shoppingcart.validation.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.Arrays;
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
import com.elasticpath.base.common.dto.StructuredErrorMessageType;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.SelectionRule;
import com.elasticpath.service.shoppingcart.validation.ShoppingItemDtoValidationContext;

@RunWith(MockitoJUnitRunner.class)
public class BundleMaxSelectionRulesShoppingItemDtoValidatorTest {
	private static final String SKU_CODE = "skuCode";

	@InjectMocks
	private BundleMaxSelectionRulesShoppingItemDtoValidatorImpl validator;

	@Mock
	private ShoppingItemDtoValidationContext context;

	@Mock
	private ProductSku productSku;

	@Mock
	private ShoppingItemDto bundleShoppingItem;

	@Mock
	private ShoppingItemDto constituentShoppingItem1;

	@Mock
	private ShoppingItemDto constituentShoppingItem2;

	@Mock
	private ProductBundle productBundle;

	@Mock
	private SelectionRule selectionRule;

	@Before
	public void setUp() {
		given(context.getProductSku()).willReturn(productSku);
		given(productSku.getProduct()).willReturn(productBundle);
		given(productSku.getSkuCode()).willReturn(SKU_CODE);
		given(productBundle.getSelectionRule()).willReturn(selectionRule);
		given(selectionRule.getParameter()).willReturn(1);
		given(context.getShoppingItemDto()).willReturn(bundleShoppingItem);
	}

	@Test
	public void testBundleConstituentsCorrect() {
		// Given
		given(constituentShoppingItem1.isSelected()).willReturn(true);
		given(constituentShoppingItem2.isSelected()).willReturn(false);
		given(bundleShoppingItem.getConstituents()).willReturn(Arrays.asList(constituentShoppingItem1, constituentShoppingItem2));

		// When
		Collection<StructuredErrorMessage> messageCollections = validator.validate(context);

		// Then
		assertThat(messageCollections).isEmpty();
	}

	@Test
	public void testBundleConstituentsMoreThanMax() {
		StructuredErrorMessage structuredErrorMessage = new StructuredErrorMessage(StructuredErrorMessageType.NEEDINFO,
				"bundle.exceeds.max.constituents",
				"Bundle contains more than the maximum number of allowed bundle constituents.",
				ImmutableMap.of("item-code", SKU_CODE,
						"max-quantity", "1",
						"current-quantity", "2"
				));

		// Given
		given(constituentShoppingItem1.isSelected()).willReturn(true);
		given(constituentShoppingItem2.isSelected()).willReturn(true);
		given(bundleShoppingItem.getConstituents()).willReturn(Arrays.asList(constituentShoppingItem1, constituentShoppingItem2));

		// When
		Collection<StructuredErrorMessage> messageCollections = validator.validate(context);

		// Then
		assertThat(messageCollections).containsOnly(structuredErrorMessage);
	}

}