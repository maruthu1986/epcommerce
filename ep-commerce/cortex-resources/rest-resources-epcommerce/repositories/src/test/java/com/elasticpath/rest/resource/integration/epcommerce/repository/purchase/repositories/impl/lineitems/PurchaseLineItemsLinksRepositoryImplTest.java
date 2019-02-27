/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.purchase.repositories.impl.lineitems;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.ImmutableList;
import io.reactivex.Single;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.rest.definition.purchases.PurchaseIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemsIdentifier;
import com.elasticpath.rest.definition.purchases.PurchasesIdentifier;
import com.elasticpath.rest.id.type.PathIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.order.OrderRepository;

/**
 * Test for {@link PurchaseLineItemsLinksRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class PurchaseLineItemsLinksRepositoryImplTest {

	private static final String PURCHASE_ID = "purchaseId";
	private static final String SCOPE = "scope";
	private static final String SKU_1 = "sku1";
	private static final String SKU_2 = "sku2";
	@Mock
	private OrderRepository orderRepository;

	@InjectMocks
	private PurchaseLineItemsLinksRepositoryImpl<PurchaseLineItemsIdentifier, PurchaseLineItemIdentifier> repository;
	private PurchaseLineItemsIdentifier identifier;

	@Mock
	private Order order;
	@Mock
	private OrderSku item1;
	@Mock
	private OrderSku item2;

	@Before
	public void setUp() {
		setUpPurchaseLineItemsIdentifier();
	}

	@Test
	public void testTwoLinksPresent() {
		when(orderRepository.findByGuidAsSingle(SCOPE, PURCHASE_ID)).thenReturn(Single.just(order));
		doReturn(ImmutableList.of(item1, item2)).when(order).getRootShoppingItems();

		when(item1.getGuid()).thenReturn(SKU_1);
		when(item2.getGuid()).thenReturn(SKU_2);

		List<PurchaseLineItemIdentifier> result = ImmutableList.of(
				buildPurchaseLineItemIdentifier(SKU_1),
				buildPurchaseLineItemIdentifier(SKU_2)
		);

		repository.getElements(identifier)
				.test()
				.assertValueSequence(result);
	}

	@Test
	public void testOneLinkPresent() {
		when(orderRepository.findByGuidAsSingle(SCOPE, PURCHASE_ID)).thenReturn(Single.just(order));
		doReturn(ImmutableList.of(item1)).when(order).getRootShoppingItems();

		when(item1.getGuid()).thenReturn(SKU_1);

		List<PurchaseLineItemIdentifier> result = ImmutableList.of(
				buildPurchaseLineItemIdentifier(SKU_1)
		);

		repository.getElements(identifier)
				.test()
				.assertValueSequence(result);
	}

	@Test
	public void testNoLinkPresent() {
		when(orderRepository.findByGuidAsSingle(SCOPE, PURCHASE_ID)).thenReturn(Single.just(order));
		doReturn(Collections.emptyList()).when(order).getRootShoppingItems();

		repository.getElements(identifier)
				.test()
				.assertNoValues();
	}

	private void setUpPurchaseLineItemsIdentifier() {
		PurchasesIdentifier purchases = PurchasesIdentifier.builder()
				.withScope(StringIdentifier.of(SCOPE))
				.build();
		PurchaseIdentifier purchase = PurchaseIdentifier.builder()
				.withPurchases(purchases)
				.withPurchaseId(StringIdentifier.of(PURCHASE_ID))
				.build();
		identifier = PurchaseLineItemsIdentifier.builder()
				.withPurchase(purchase)
				.build();
	}

	private PurchaseLineItemIdentifier buildPurchaseLineItemIdentifier(final String guid) {
		return PurchaseLineItemIdentifier.builder()
				.withLineItemId(PathIdentifier.of(guid))
				.withPurchaseLineItems(PurchaseLineItemsIdentifier.builder()
						.withPurchase(identifier.getPurchase())
						.build())
				.build();
	}

}
