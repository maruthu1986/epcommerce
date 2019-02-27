/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.purchase.repositories.impl.options;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedMap;
import io.reactivex.Single;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.purchases.PurchaseIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemOptionsIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemsIdentifier;
import com.elasticpath.rest.definition.purchases.PurchasesIdentifier;
import com.elasticpath.rest.id.type.PathIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.id.util.CompositeIdUtil;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.order.OrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.impl.ReactiveAdapterImpl;

/**
 * Test for the  {@link LineItemToOptionsRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class LineItemToOptionsRepositoryImplTest {

	private static final String ITEM_ID = "itemId";
	private static final String PURCHASE_ID = "purchaseId";
	private static final String SCOPE = "scope";
	private static final String SKU_CODE = "skuCode";

	@Mock
	private ItemRepository itemRepository;
	@InjectMocks
	private ReactiveAdapterImpl reactiveAdapter;
	@Mock
	private OrderRepository orderRepository;
	@InjectMocks
	private LineItemToOptionsRepositoryImpl<PurchaseLineItemIdentifier, PurchaseLineItemOptionsIdentifier> repository;

	@Mock
	private ProductSku productSku;
	private PurchaseLineItemIdentifier purchaseLineItemIdentifier;

	@Before
	public void setUp() {
		repository.setReactiveAdapter(reactiveAdapter);
	}

	@Test
	public void testNoOptionsForTheLineItem() {

		createPurchaseLineItemIdentifier();
		setUpSkuRepoAndOptionLookupWithOptions(Collections.emptySet());

		repository.getElements(purchaseLineItemIdentifier)
				.test()
				.assertNoValues();
	}

	@Test
	public void testSeveralOptionsForTheLineItem() {

		Set<SkuOption> options = ImmutableSet.of(mock(SkuOption.class), mock(SkuOption.class));
		createPurchaseLineItemIdentifier();
		setUpSkuRepoAndOptionLookupWithOptions(options);

		repository.getElements(purchaseLineItemIdentifier)
				.test()
				.assertValue(PurchaseLineItemOptionsIdentifier.builder()
						.withPurchaseLineItem(purchaseLineItemIdentifier)
						.build());

	}

	private void setUpSkuRepoAndOptionLookupWithOptions(final Set<SkuOption> options) {
		when(orderRepository.findProductSku(any(), any(), any())).thenReturn(Single.just(productSku));
		when(productSku.getSkuCode()).thenReturn(SKU_CODE);
		String encodedSkuCode = getEncodedItemId(SKU_CODE); //This step will be eventually removed
		when(itemRepository.getSkuOptionsForItemId(encodedSkuCode)).thenReturn(ExecutionResultFactory.createReadOK(options));
	}

	private void createPurchaseLineItemIdentifier() {
		PurchasesIdentifier purchases = PurchasesIdentifier.builder()
				.withScope(StringIdentifier.of(SCOPE))
				.build();
		PurchaseIdentifier purchase = PurchaseIdentifier.builder()
				.withPurchases(purchases)
				.withPurchaseId(StringIdentifier.of(PURCHASE_ID))
				.build();
		PurchaseLineItemsIdentifier purchaseLineItems = PurchaseLineItemsIdentifier.builder()
				.withPurchase(purchase)
				.build();
		purchaseLineItemIdentifier = PurchaseLineItemIdentifier.builder()
				.withPurchaseLineItems(purchaseLineItems)
				.withLineItemId(PathIdentifier.of(ITEM_ID))
				.build();
	}

	private String getEncodedItemId(final String skuCode) {
		return CompositeIdUtil.encodeCompositeId(
				ImmutableSortedMap.of(ItemRepository.SKU_CODE_KEY, skuCode)
		);
	}

}
