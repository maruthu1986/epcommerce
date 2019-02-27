/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shoppingcart.validation.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import static com.elasticpath.domain.catalog.AvailabilityCriteria.ALWAYS_AVAILABLE;
import static com.elasticpath.domain.catalog.AvailabilityCriteria.AVAILABLE_FOR_BACK_ORDER;
import static com.elasticpath.domain.catalog.AvailabilityCriteria.AVAILABLE_FOR_PRE_ORDER;
import static com.elasticpath.domain.catalog.AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK;

import java.util.Collection;
import java.util.Collections;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.base.common.dto.StructuredErrorMessageType;
import com.elasticpath.common.dto.InventoryDetails;
import com.elasticpath.domain.catalog.InventoryCalculator;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.Warehouse;
import com.elasticpath.service.catalog.ProductInventoryManagementService;
import com.elasticpath.service.shoppingcart.validation.ShoppingItemValidationContext;

@RunWith(MockitoJUnitRunner.class)
public class InventoryShoppingItemValidatorImplTest {

	private static final String SKU_CODE = "sku_code";

	private static final String SKU_GUID = "sku_guid";

	private static final long WAREHOUSE_ID = 1232365;

	private static final int REQUESTED_QUANTITY = 10;

	private static final int ENOUGH_AVAILABLE = 15;

	private static final int NOT_ENOUGH_AVAILABLE = 5;

	private static final String MESSAGE_ID = "item.insufficient.inventory";

	@InjectMocks
	private InventoryShoppingItemValidatorImpl validator;

	@Mock
	private InventoryCalculator inventoryCalculator;

	@Mock
	private ShoppingItem shoppingItem;

	@Mock
	private ProductInventoryManagementService productInventoryManagementService;

	@Mock
	private Store store;

	@Mock
	private ProductSku productSku;

	@Mock
	private Product product;

	@Mock
	private Warehouse warehouse;

	@Mock
	private ShoppingCart shoppingCart;

	@Mock
	private InventoryDetails inventoryDetails;

	private ShoppingItemValidationContext context;

	@Before
	public void setUp() throws Exception {
		context = new ShoppingItemValidationContextImpl();

		context.setProductSku(productSku);
		context.setStore(store);
		context.setShoppingItem(shoppingItem);
		context.setShoppingCart(shoppingCart);

		given(shoppingCart.getCartItemsBySkuGuid(SKU_GUID)).willReturn(Collections.singletonList(shoppingItem));

		given(productSku.isShippable()).willReturn(true);
		given(productSku.getSkuCode()).willReturn(SKU_CODE);
		given(productSku.getGuid()).willReturn(SKU_GUID);

		given(shoppingItem.getQuantity()).willReturn(REQUESTED_QUANTITY);

		given(inventoryCalculator.getInventoryDetails(productInventoryManagementService, productSku, WAREHOUSE_ID)).willReturn(inventoryDetails);

		given(warehouse.getUidPk()).willReturn(WAREHOUSE_ID);

		given(store.getWarehouses()).willReturn(Collections.singletonList(warehouse));

		given(productSku.getProduct()).willReturn(product);

		given(product.getAvailabilityCriteria()).willReturn(AVAILABLE_WHEN_IN_STOCK);
	}

	@Test
	public void testHasEnoughInventory() {
		// Given
		given(inventoryDetails.getAvailableQuantityInStock()).willReturn(ENOUGH_AVAILABLE);

		// When
		Collection<StructuredErrorMessage> messageCollections = validator.validate(context);

		// Then
		assertThat(messageCollections).isEmpty();
	}

	@Test
	public void testNotEnoughInventory() {
		StructuredErrorMessage errorMessage =
				new StructuredErrorMessage(StructuredErrorMessageType.ERROR, MESSAGE_ID,
						String.format("Item '%s' only has %d available but %d were requested.",
								productSku.getSkuCode(), NOT_ENOUGH_AVAILABLE, REQUESTED_QUANTITY),
						ImmutableMap.of("item-code", productSku.getSkuCode(),
								"quantity-requested", String.format("%d", REQUESTED_QUANTITY),
								"inventory-available", String.format("%d", NOT_ENOUGH_AVAILABLE)));

		// Given
		given(inventoryDetails.getAvailableQuantityInStock()).willReturn(NOT_ENOUGH_AVAILABLE);

		// When
		Collection<StructuredErrorMessage> messageCollections = validator.validate(context);

		// Then
		assertThat(messageCollections).containsOnly(errorMessage);
	}

	@Test
	public void testAlwaysAvailable() {

		// Given
		given(product.getAvailabilityCriteria()).willReturn(ALWAYS_AVAILABLE);

		// When
		Collection<StructuredErrorMessage> messageCollections = validator.validate(context);

		// Then
		assertThat(messageCollections).isEmpty();
	}

	@Test
	public void testAvailableForPreOrder() {

		// Given
		given(product.getAvailabilityCriteria()).willReturn(AVAILABLE_FOR_PRE_ORDER);

		// When
		Collection<StructuredErrorMessage> messageCollections = validator.validate(context);

		// Then
		assertThat(messageCollections).isEmpty();
	}

	@Test
	public void testAvailableForBackOrder() {

		// Given
		given(product.getAvailabilityCriteria()).willReturn(AVAILABLE_FOR_BACK_ORDER);

		// When
		Collection<StructuredErrorMessage> messageCollections = validator.validate(context);

		// Then
		assertThat(messageCollections).isEmpty();
	}

}