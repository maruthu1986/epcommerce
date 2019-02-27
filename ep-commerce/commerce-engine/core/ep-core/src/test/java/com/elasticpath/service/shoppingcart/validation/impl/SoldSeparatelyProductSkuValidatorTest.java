/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shoppingcart.validation.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.Collection;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.service.shoppingcart.validation.ProductSkuValidationContext;

@RunWith(MockitoJUnitRunner.class)
public class SoldSeparatelyProductSkuValidatorTest {

	private static final String SKU_CODE = "sku_code";

	@InjectMocks
	private SoldSeparatelyProductSkuValidatorImpl validator;

	@Mock
	private ProductSkuValidationContext context;

	@Mock
	private Product product;

	@Mock
	private ProductSku productSku;

	@Before
	public void setUp() {
		given(productSku.getProduct()).willReturn(product);
		given(productSku.getSkuCode()).willReturn(SKU_CODE);
		given(context.getProductSku()).willReturn(productSku);
	}

	@Test
	public void testProductIsNotSoldSeparately() {
		StructuredErrorMessage structuredErrorMessage = new StructuredErrorMessage("item.not.sold.separately",
				String.format("Item '%s' is not sold separately.", SKU_CODE),
				ImmutableMap.of("item-code", SKU_CODE));

		// Given
		given(product.isNotSoldSeparately()).willReturn(true);

		// When
		Collection<StructuredErrorMessage> messageCollections = validator.validate(context);

		// Then
		assertThat(messageCollections).containsOnly(structuredErrorMessage);
	}

	@Test
	public void testProductIsSoldSeparately() {

		// Given
		given(product.isNotSoldSeparately()).willReturn(false);

		// When
		Collection<StructuredErrorMessage> messageCollections = validator.validate(context);

		// Then
		assertThat(messageCollections).isEmpty();
	}

}
