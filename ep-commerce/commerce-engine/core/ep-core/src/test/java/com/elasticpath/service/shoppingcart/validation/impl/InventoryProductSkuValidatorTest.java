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
import com.elasticpath.common.dto.SkuInventoryDetails;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.store.Store;
import com.elasticpath.sellingchannel.inventory.ProductInventoryShoppingService;
import com.elasticpath.service.shoppingcart.validation.ProductSkuValidationContext;

@RunWith(MockitoJUnitRunner.class)
public class InventoryProductSkuValidatorTest {

	private static final String SKU_CODE = "sku_code";

	@InjectMocks
	private InventoryProductSkuValidatorImpl validator;

	@Mock
	private ProductInventoryShoppingService productInventoryShoppingService;

	@Mock
	private ProductSkuValidationContext context;

	@Mock
	private Store store;

	@Mock
	private SkuInventoryDetails skuInventoryDetails;

	@Mock
	private ProductSku productSku;

	@Before
	public void setUp() {
		given(productSku.getSkuCode()).willReturn(SKU_CODE);

		given(context.getProductSku()).willReturn(productSku);
		given(context.getStore()).willReturn(store);

		given(productInventoryShoppingService.getSkuInventoryDetails(productSku, store)).willReturn(skuInventoryDetails);
	}

	@Test
	public void testProductNotAvailable() {
		StructuredErrorMessage structuredErrorMessage = new StructuredErrorMessage("item.insufficient.inventory",
				String.format("Item '%s' does not have sufficient inventory.", SKU_CODE),
				ImmutableMap.of("item-code", SKU_CODE));

		// Given
		given(skuInventoryDetails.hasSufficientUnallocatedQty()).willReturn(false);

		// When
		Collection<StructuredErrorMessage> messageCollections = validator.validate(context);

		// Then
		assertThat(messageCollections).containsOnly(structuredErrorMessage);
	}

	@Test
	public void testProductAvailable() {
		// Given
		given(skuInventoryDetails.hasSufficientUnallocatedQty()).willReturn(true);

		// When
		Collection<StructuredErrorMessage> messageCollections = validator.validate(context);

		// Then
		assertThat(messageCollections).isEmpty();
	}
}
