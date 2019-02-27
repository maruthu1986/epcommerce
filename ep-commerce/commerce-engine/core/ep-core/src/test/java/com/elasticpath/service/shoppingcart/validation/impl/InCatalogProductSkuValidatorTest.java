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
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.shoppingcart.validation.ProductSkuValidationContext;

@RunWith(MockitoJUnitRunner.class)
public class InCatalogProductSkuValidatorTest {

	@InjectMocks
	private InCatalogProductSkuValidatorImpl validator;

	@Mock
	private ProductSkuValidationContext context;

	@Mock
	private Product product;

	@Mock
	private ProductSku productSku;

	@Mock
	private Store store;

	@Mock
	private Catalog catalog;

	private static final String SKU_CODE = "sku_code";

	@Before
	public void setUp() {
		given(productSku.getProduct()).willReturn(product);
		given(productSku.getSkuCode()).willReturn(SKU_CODE);
		given(store.getCatalog()).willReturn(catalog);
		given(context.getStore()).willReturn(store);

		given(context.getProductSku()).willReturn(productSku);

	}

	@Test
	public void testProductIsNotInCatalog() {
		StructuredErrorMessage structuredErrorMessage = new StructuredErrorMessage("item.not.in.store.catalog",
				String.format("Item '%s' is not part of the current store's catalog.", SKU_CODE),
				ImmutableMap.of("item-code", SKU_CODE));

		// Given
		given(product.isInCatalog(Mockito.anyObject(), Mockito.anyBoolean())).willReturn(false);

		// When
		Collection<StructuredErrorMessage> messageCollections = validator.validate(context);

		// Then
		assertThat(messageCollections).containsOnly(structuredErrorMessage);
	}

	@Test
	public void testProductIsInCatalog() {
		// Given
		given(product.isInCatalog(Mockito.anyObject(), Mockito.anyBoolean())).willReturn(true);

		// When
		Collection<StructuredErrorMessage> messageCollections = validator.validate(context);

		// Then
		assertThat(messageCollections).isEmpty();
	}

}
