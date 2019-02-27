/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shipping.transformers.impl;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;

/**
 * Tests of {@link ShippableItemContainerTransformerImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShippableItemContainerTransformerImplTest {
	@Mock
	private ShoppingCart shoppingCart;

	@Mock
	private ShoppingItem shoppingItem1;

	@Mock
	private ShoppingItem shoppingItem2;

	@Mock
	private Predicate<ShoppingItem> shippableItemPredicate;

	@InjectMocks
	private ShippableItemContainerTransformerImpl objectUnderTest;

	@Test
	public void verifyFilterShoppingItemsFiltersApportionedLeafItemsCorrectly() {
		when(shoppingCart.getApportionedLeafItems()).thenReturn(asList(shoppingItem1, shoppingItem2));

		// Set the first ShoppingItem to not be shippable
		when(shippableItemPredicate.test(shoppingItem1)).thenReturn(false);
		when(shippableItemPredicate.test(shoppingItem2)).thenReturn(true);

		final Stream<ShoppingItem> actualFilteredShoppingItems = objectUnderTest.filterShoppingItems(shoppingCart);

		// filterShoppingItems() should filter the apportioned leaf items
		verify(shoppingCart).getApportionedLeafItems();

		// However as the method returns a stream the filtering should be deferred until the Stream is read
		// So the shippable item predicate should not yet be invokved
		verifyZeroInteractions(shippableItemPredicate);

		// Once Stream is read the Predicate should have been invoked
		final Collection<ShoppingItem> actualFilteredShoppingItemsCollection = actualFilteredShoppingItems.collect(Collectors.toList());

		verify(shippableItemPredicate).test(shoppingItem1);
		verify(shippableItemPredicate).test(shoppingItem2);

		// And verify the result only contains the ShoppingItem passing the Predicate
		assertThat(actualFilteredShoppingItemsCollection).containsExactly(shoppingItem2);
	}

	@Test
	public void verifyFilterShoppingItemsThrowsNPEWithNullCart() {
		assertThatExceptionOfType(NullPointerException.class)
				.isThrownBy(() -> objectUnderTest.filterShoppingItems(null))
				.withMessage("Shopping Cart is required.");
	}
}
