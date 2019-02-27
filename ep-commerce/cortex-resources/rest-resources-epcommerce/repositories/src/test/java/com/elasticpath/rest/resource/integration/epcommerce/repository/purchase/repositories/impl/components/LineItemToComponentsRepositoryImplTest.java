/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.purchase.repositories.impl.components;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.IdentifierTestFactory.buildPurchaseLineItemIdentifier;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.PURCHASE_ID;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.SCOPE;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import com.google.common.collect.ImmutableList;
import io.reactivex.Single;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalogview.StoreProduct;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemComponentsIdentifier;
import com.elasticpath.rest.definition.purchases.PurchaseLineItemIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.order.OrderRepository;

/**
 * Test for the  {@link LineItemToComponentsRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class LineItemToComponentsRepositoryImplTest {

	private final List<String> lineItemIds = ImmutableList.of("id", "id2");

	@Mock
	private ProductSku productSku;

	@InjectMocks
	private LineItemToComponentsRepositoryImpl<PurchaseLineItemIdentifier, PurchaseLineItemComponentsIdentifier> repository;

	@Mock
	private OrderRepository orderRepository;

	private final PurchaseLineItemIdentifier purchaseLineItemIdentifier = buildPurchaseLineItemIdentifier(SCOPE, PURCHASE_ID, lineItemIds);

	@Test
	public void testGetElementsWithComponentsReturnAnIdentifier() {
		Product product = mock(ProductBundle.class);

		mockDependencies(product);

		repository.getElements(purchaseLineItemIdentifier)
				.test()
				.assertNoErrors()
				.assertValueCount(1);
	}

	@Test
	public void testGetElementsWithoutComponentsReturnEmpty() {
		Product product = mock(StoreProduct.class);

		mockDependencies(product);

		repository.getElements(purchaseLineItemIdentifier)
				.test()
				.assertNoErrors()
				.assertValueCount(0);
	}

	private void mockDependencies(final Product product) {
		when(orderRepository.findProductSku(SCOPE, PURCHASE_ID, lineItemIds)).thenReturn(Single.just(productSku));
		when(productSku.getProduct()).thenReturn(product);
	}
}
