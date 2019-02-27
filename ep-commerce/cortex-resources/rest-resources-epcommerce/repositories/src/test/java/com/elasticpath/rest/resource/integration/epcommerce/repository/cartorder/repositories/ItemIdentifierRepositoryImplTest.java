/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.repositories;

import static org.mockito.Mockito.when;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.CART_ID;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.ITEM_IDENTIFIER_PART;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.LINE_ITEM_ID;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.SCOPE;

import io.reactivex.Single;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.rest.definition.carts.LineItemIdentifier;
import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.IdentifierTestFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;

/**
 * Test for {@link ItemIdentifierRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ItemIdentifierRepositoryImplTest {

	@InjectMocks
	private ItemIdentifierRepositoryImpl<LineItemIdentifier, ItemIdentifier> repository;
	@Mock
	private ShoppingCartRepository shoppingCartRepository;
	@Mock
	private ItemRepository itemRepository;
	@Mock
	private ProductSku productSku;

	@Test
	public void getElements() {
		LineItemIdentifier lineItemIdentifier = IdentifierTestFactory.buildLineItemIdentifier(SCOPE, CART_ID, LINE_ITEM_ID);

		when(shoppingCartRepository.getProductSku(CART_ID, LINE_ITEM_ID)).thenReturn(Single.just(productSku));
		when(itemRepository.getItemIdForProductSku(productSku)).thenReturn(ITEM_IDENTIFIER_PART);

		repository.getElements(lineItemIdentifier)
				.test()
				.assertNoErrors()
				.assertValueCount(1);
	}

}
