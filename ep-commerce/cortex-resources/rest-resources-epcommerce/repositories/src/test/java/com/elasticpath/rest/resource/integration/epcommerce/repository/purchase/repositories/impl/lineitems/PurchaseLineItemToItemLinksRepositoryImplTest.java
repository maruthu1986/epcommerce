/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.purchase.repositories.impl.lineitems;

import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.reactivex.Single;

import com.google.common.collect.ImmutableList;

import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemIdentifier;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.order.OrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.sku.ProductSkuRepository;

@RunWith(MockitoJUnitRunner.class)
public class PurchaseLineItemToItemLinksRepositoryImplTest {

	private static final String PURCHASE_ID = "testPurchaseId";
	private static final String SCOPE = "testScope";
	private static final List<String> GUID_PATH_FROM_LINE_ITEM = ImmutableList
			.of("91e37a1a-1bea-40b6-816b-5e000e8dd022", "b2cd9e7d-8c46-4df8-91af-12a596a18408");
	private static final String SKU_GUID = "testSkuGuid";
	private static final String LINE_ITEM_NOT_FOUND = "Line item not found";

	@InjectMocks
	private PurchaseLineItemToItemLinksRepositoryImpl<PurchaseLineItemIdentifier, ItemIdentifier> target;

	@Mock
	private OrderRepository orderRepository;

	@Mock
	private ItemRepository itemRepository;

	@Mock
	private ProductSkuRepository productSkuRepository;

	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private PurchaseLineItemIdentifier purchaseLineItemIdentifier;

	@Mock
	private OrderSku orderSku;

	@Mock
	private ProductSku productSku;

	@Mock
	private IdentifierPart<Map<String, String>> itemId;

	@Before
	public void setUp() {

		when(purchaseLineItemIdentifier.getPurchaseLineItems().getPurchase().getPurchases().getScope().getValue()).thenReturn(SCOPE);
		when(purchaseLineItemIdentifier.getPurchaseLineItems().getPurchase().getPurchaseId().getValue()).thenReturn(PURCHASE_ID);
		when(purchaseLineItemIdentifier.getLineItemId().getValue()).thenReturn(GUID_PATH_FROM_LINE_ITEM);

		when(orderRepository.findOrderSku(SCOPE, PURCHASE_ID, GUID_PATH_FROM_LINE_ITEM)).thenReturn(Single.just(orderSku));
		when(orderSku.getSkuGuid()).thenReturn(SKU_GUID);
		when(productSkuRepository.getProductSkuWithAttributesByGuidAsSingle(SKU_GUID)).thenReturn(Single.just(productSku));
		when(itemRepository.getItemIdForProductSku(productSku)).thenReturn(itemId);

	}

	@Test
	public void testGetElements() {

		target.getElements(purchaseLineItemIdentifier)
				.test()
				.assertNoErrors()
				.assertValueCount(1)
				.assertValue(submitResult -> submitResult.getItemId().equals(itemId))
				.assertValue(submitResult -> submitResult.getItems().getScope().getValue().equals(SCOPE));

	}

	@Test
	public void testGetElementsWithEmptyOrderSku() {

		when(orderRepository.findOrderSku(SCOPE, PURCHASE_ID, GUID_PATH_FROM_LINE_ITEM))
				.thenReturn(Single.error(ResourceOperationFailure.notFound(LINE_ITEM_NOT_FOUND)));

		target.getElements(purchaseLineItemIdentifier)
				.test()
				.assertError(ResourceOperationFailure.class)
				.assertErrorMessage("Line item not found");

	}

}
