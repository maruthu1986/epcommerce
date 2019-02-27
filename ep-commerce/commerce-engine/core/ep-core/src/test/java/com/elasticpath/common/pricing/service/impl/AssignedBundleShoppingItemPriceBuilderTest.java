/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.common.pricing.service.impl;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.commons.exception.InvalidProductStructureException;
import com.elasticpath.domain.catalog.BundleConstituent;
import com.elasticpath.domain.catalog.ConstituentItem;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCartMessageIds;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.store.Store;
import com.elasticpath.sellingchannel.ProductUnavailableException;
import com.elasticpath.service.catalog.ProductSkuLookup;

/**
 * Test functionality of the {@link CalculatedBundleShoppingItemPriceBuilder}.
 */
@RunWith(MockitoJUnitRunner.class)
public class AssignedBundleShoppingItemPriceBuilderTest {
	private static final String ROOT_SHOPPING_ITEM_GUID = "ROOT_SHOPPING_ITEM_GUID";
	private static final String MISSING_CONSTITUENT_SHOPPING_ITEM_GUID = "MISSING_CONSTITUENT_SHOPPING_ITEM_GUID";
	private static final String MISSING_CONSTITUENT_SHOPPING_ITEM_SKU = "MISSING_CONSTITUENT_SHOPPING_ITEM_SKU";
	private static final String ERROR_MESSAGE_NO_MATCHING_CONSTITUENT = "Cart item has no matching bundle constituent";

	@InjectMocks
	private AssignedBundleShoppingItemPriceBuilder assignedBundleShoppingItemPriceBuilder;

	@Mock
	private ProductSkuLookup productSkuLookup;

	@Mock
	private Shopper shopper;

	@Mock
	private Store store;

	@Mock
	private ProductBundle rootProductBundle;

	@Mock
	private ShoppingItem rootItem;

	@Mock
	private ShoppingItem missingConstituentItem;

	@Mock
	private ProductSku rootItemSku;

	@Mock
	private ProductSku missingConstituentItemSku;

	@Before
	public void setUp() {
		when(missingConstituentItem.getSkuGuid()).thenReturn(MISSING_CONSTITUENT_SHOPPING_ITEM_GUID);
		when(rootItem.getSkuGuid()).thenReturn(ROOT_SHOPPING_ITEM_GUID);
		when(productSkuLookup.findByGuid(ROOT_SHOPPING_ITEM_GUID)).thenReturn(rootItemSku);
		when(productSkuLookup.findByGuid(MISSING_CONSTITUENT_SHOPPING_ITEM_GUID)).thenReturn(missingConstituentItemSku);
		when(rootItemSku.getProduct()).thenReturn(rootProductBundle);
		when(missingConstituentItemSku.getSkuCode()).thenReturn(MISSING_CONSTITUENT_SHOPPING_ITEM_SKU);
	}

	@Test
	public void verifyProductUnavailableExceptionThrownWhenAssignedBundleConstituentMissing() {
		when(rootItem.getChildren()).thenReturn(Collections.singletonList(missingConstituentItem));
		when(rootProductBundle.getConstituents()).thenReturn(Collections.emptyList());

		assertThatThrownBy(() -> assignedBundleShoppingItemPriceBuilder.build(rootItem, shopper, store))
				.isInstanceOf(ProductUnavailableException.class)
				.hasMessage(ERROR_MESSAGE_NO_MATCHING_CONSTITUENT)
				.hasFieldOrPropertyWithValue("structuredErrorMessages", getStructuredErrorMessages(missingConstituentItemSku));
	}

	@Test
	public void verifyProductUnavailableExceptionThrownWhenAssignedBundleNestedConstituentMissing() {
		final ProductBundle childProductBundle = mock(ProductBundle.class);
		final ShoppingItem childItem = mock(ShoppingItem.class);
		final BundleConstituent childConstituentItem = mock(BundleConstituent.class);

		final ConstituentItem missingProductConstituentItem = mock(ConstituentItem.class);

		when(rootItem.getChildren()).thenReturn(Collections.singletonList(childItem));
		when(childItem.getChildren()).thenReturn(Collections.singletonList(missingConstituentItem));

		when(rootProductBundle.getConstituents()).thenReturn(Collections.singletonList(childConstituentItem));
		when(childProductBundle.getConstituents()).thenReturn(Collections.emptyList());

		when(childConstituentItem.getConstituent()).thenReturn(missingProductConstituentItem);

		when(missingProductConstituentItem.isBundle()).thenReturn(true);
		when(missingProductConstituentItem.getProduct()).thenReturn(childProductBundle);

		assertThatThrownBy(() -> assignedBundleShoppingItemPriceBuilder.build(rootItem, shopper, store))
				.isInstanceOf(ProductUnavailableException.class)
				.hasMessage(ERROR_MESSAGE_NO_MATCHING_CONSTITUENT)
				.hasFieldOrPropertyWithValue("structuredErrorMessages", getStructuredErrorMessages(missingConstituentItemSku));
	}

	@Test
	public void verifyInvalidProductStructureExceptionThrownWhenAssignedBundleNestedConstituentIsNotABundle() {
		final ShoppingItem childItem = mock(ShoppingItem.class);
		final BundleConstituent childConstituentItem = mock(BundleConstituent.class);

		final ConstituentItem missingProductConstituentItem = mock(ConstituentItem.class);

		when(rootItem.getChildren()).thenReturn(Collections.singletonList(childItem));
		when(childItem.getChildren()).thenReturn(Collections.singletonList(missingConstituentItem));

		when(rootProductBundle.getConstituents()).thenReturn(Collections.singletonList(childConstituentItem));

		when(childConstituentItem.getConstituent()).thenReturn(missingProductConstituentItem);

		when(missingProductConstituentItem.isBundle()).thenReturn(false);

		assertThatThrownBy(() -> assignedBundleShoppingItemPriceBuilder.build(rootItem, shopper, store))
				.isInstanceOf(InvalidProductStructureException.class)
				.hasMessage("ShoppingItem structure invalid");
	}

	private List<StructuredErrorMessage> getStructuredErrorMessages(final ProductSku productSku) {
		List<StructuredErrorMessage> structuredErrorMessages = new ArrayList<>();
		structuredErrorMessages.add(new StructuredErrorMessage(
				ShoppingCartMessageIds.ITEM_NOT_AVAILABLE,
				ERROR_MESSAGE_NO_MATCHING_CONSTITUENT,
				ImmutableMap.of("item-code", productSku.getSkuCode())
		));
		return structuredErrorMessages;
	}

}
