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

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.service.catalog.impl.BundleIdentifierImpl;
import com.elasticpath.service.shoppingcart.validation.ProductSkuValidationContext;

@RunWith(MockitoJUnitRunner.class)
public class PriceExistsProductSkuValidatorTest {

	private static final String SKU_CODE = "sku_code";

	@InjectMocks
	private PriceExistsProductSkuValidatorImpl validator;

	@Mock
	private ProductSkuValidationContext context;

	@Mock
	private Product product;

	@Mock
	private ProductBundle calculatedProductBundle;

	@Mock
	private ProductSku productSku;

	@Mock
	private ProductSku parentProductSku;

	@Mock
	private Price price;

	@Before
	public void setUp() {
		given(calculatedProductBundle.isCalculated()).willReturn(true);
		given(productSku.getSkuCode()).willReturn(SKU_CODE);
		given(context.getProductSku()).willReturn(productSku);

		validator.setBundleIdentifier(new BundleIdentifierImpl());
	}

	@Test
	public void testProductPriceExist() {
		// Given
		given(productSku.getProduct()).willReturn(product);
		given(context.getPromotedPrice()).willReturn(price);

		// When
		Collection<StructuredErrorMessage> messageCollections = validator.validate(context);

		// Than
		assertThat(messageCollections).isEmpty();
	}

	@Test
	public void testProductPriceDoesNotExist() {
		StructuredErrorMessage structuredErrorMessage = new StructuredErrorMessage("item.missing.price",
				String.format("Item '%s' does not have a price.", SKU_CODE),
				ImmutableMap.of("item-code", SKU_CODE));
		// Given
		given(productSku.getProduct()).willReturn(product);

		// When
		Collection<StructuredErrorMessage> messageCollections = validator.validate(context);

		// Then
		assertThat(messageCollections).containsOnly(structuredErrorMessage);
	}

	@Test
	public void testProductPriceDoesNotExistForCalculatedBundle() {
		// Given
		given(productSku.getProduct()).willReturn(calculatedProductBundle);

		// When
		Collection<StructuredErrorMessage> messageCollections = validator.validate(context);

		// Then
		assertThat(messageCollections).isEmpty();
	}

	@Test
	public void testProductPriceDoesNotExistForBundleWithParentAssignedBundle() {
		// Given
		given(context.getParentProductSku()).willReturn(parentProductSku);
		given(productSku.getProduct()).willReturn(calculatedProductBundle);

		// When
		Collection<StructuredErrorMessage> messageCollections = validator.validate(context);

		// Then
		assertThat(messageCollections).isEmpty();
	}
}
