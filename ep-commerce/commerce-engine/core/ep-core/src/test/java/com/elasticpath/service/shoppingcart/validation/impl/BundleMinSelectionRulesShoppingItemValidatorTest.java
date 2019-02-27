/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shoppingcart.validation.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.Arrays;
import java.util.Collection;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.base.common.dto.StructuredErrorMessageType;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.SelectionRule;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.shoppingcart.validation.ShoppingItemValidationContext;

@RunWith(MockitoJUnitRunner.class)
public class BundleMinSelectionRulesShoppingItemValidatorTest {

	private static final String SKU_CODE = "skuCode";

	@InjectMocks
	private BundleMinSelectionRulesShoppingItemValidatorImpl validator;

	@Mock
	private ProductSkuLookup productSkuLookup;

	@Mock
	private ShoppingItemValidationContext context;

	@Mock
	private ProductSku productSku;

	@Mock
	private ShoppingItem bundleShoppingItem;

	@Mock
	private ShoppingItem constituentShoppingItem1;

	@Mock
	private ShoppingItem constituentShoppingItem2;

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
		given(selectionRule.getParameter()).willReturn(2);
		given(context.getShoppingItem()).willReturn(bundleShoppingItem);
	}

	@Test
	public void testBundleConstituentsCorrect() {

		// Given
		given(bundleShoppingItem.getBundleItems(productSkuLookup)).willReturn(Arrays.asList(constituentShoppingItem1, constituentShoppingItem2));

		// When
		Collection<StructuredErrorMessage> messageCollections = validator.validate(context);

		// Then
		assertThat(messageCollections).isEmpty();
	}

	@Test
	public void testBundleConstituentsLessThanMin() {
		StructuredErrorMessage structuredErrorMessage = new StructuredErrorMessage(StructuredErrorMessageType.NEEDINFO,
				"bundle.does.not.contain.min.constituents",
				"Bundle does not contain the minimum number of required bundle constituents.",
				ImmutableMap.of("item-code", SKU_CODE,
						"min-quantity", "2",
						"current-quantity", "1"
				));

		// Given
		given(bundleShoppingItem.getBundleItems(productSkuLookup)).willReturn(Arrays.asList(constituentShoppingItem1));

		// When
		Collection<StructuredErrorMessage> messageCollections = validator.validate(context);

		// Then
		assertThat(messageCollections).containsOnly(structuredErrorMessage);
	}
}
