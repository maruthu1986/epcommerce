/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.carts.lineitems.impl;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.CART_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.UUID;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.rest.definition.carts.CartIdentifier;
import com.elasticpath.rest.definition.carts.LineItemIdentifier;
import com.elasticpath.rest.definition.carts.LineItemsIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.carts.lineitems.LineItemIdentifierRepository;

/**
 * Test class for {@link DependentLineItemRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class DependentLineItemRepositoryImplTest {

	@Mock
	private LineItemIdentifier lineItemIdentifier;

	@Mock
	private ShoppingCart shoppingCart;

	@Mock
	private ShoppingCartRepository shoppingCartRepository;

	@Mock
	private LineItemIdentifierRepository lineItemIdentifierRepository;

	@InjectMocks
	private DependentLineItemRepositoryImpl repository;

	@Before
	public void setUp() {
		when(shoppingCart.getAllShoppingItems())
				.thenReturn(Lists.newArrayList());
	}

	@Test
	public void verifyEmptyObservableWhenNoDependentItemsFound() {
		final String lineItemId = UUID.randomUUID().toString();

		when(shoppingCartRepository.getShoppingCart(CART_ID)).thenReturn(Single.just(shoppingCart));

		givenLineItemIdentiferHasLineItemId(lineItemId);
		givenLineItemIdentifierHasCartId(CART_ID);

		givenAParentLineItem(shoppingCart, lineItemId);

		final Observable<LineItemIdentifier> dependentLineItemIdentifiersObservable = repository.getElements(lineItemIdentifier);

		assertThat(dependentLineItemIdentifiersObservable.blockingIterable())
				.isEmpty();
	}

	@Test
	public void verifyObservableContainsAllDependentLineItems() {
		final LineItemIdentifier expectedIdentifier = mock(LineItemIdentifier.class);

		final String lineItemId = UUID.randomUUID().toString();
		final String dependentLineItemId = UUID.randomUUID().toString();

		givenLineItemIdentiferHasLineItemId(lineItemId);
		givenLineItemIdentifierHasCartId(CART_ID);

		final ShoppingItem parentShoppingItem = givenAParentLineItem(shoppingCart, lineItemId);
		final ShoppingItem dependentShoppingItem = givenADependentLineItem(shoppingCart, parentShoppingItem, dependentLineItemId);

		when(shoppingCartRepository.getShoppingCart(CART_ID)).thenReturn(Single.just(shoppingCart));
		doReturn(expectedIdentifier)
				.when(lineItemIdentifierRepository).buildLineItemIdentifier(shoppingCart, dependentShoppingItem);

		final Observable<LineItemIdentifier> dependentLineItemIdentifiersObservable = repository.getElements(lineItemIdentifier);

		assertThat(dependentLineItemIdentifiersObservable.blockingIterable())
				.hasOnlyOneElementSatisfying(identifier ->
													 assertThat(identifier)
															 .isEqualTo(expectedIdentifier));
	}

	@Test
	public void verifyEmptyMaybeReturnedWhenLineItemIsNotDependent() {
		final String lineItemId = UUID.randomUUID().toString();

		givenLineItemIdentiferHasLineItemId(lineItemId);
		givenLineItemIdentifierHasCartId(CART_ID);

		givenAParentLineItem(shoppingCart, lineItemId);

		when(shoppingCartRepository.getShoppingCart(CART_ID)).thenReturn(Single.just(shoppingCart));

		final Maybe<LineItemIdentifier> parentLineItemIdentifierOrNull = repository.findParent(lineItemIdentifier);

		assertThat(parentLineItemIdentifierOrNull.blockingGet())
				.isNull();
	}

	@Test
	public void verifyParentLinkReturnedWhenLineItemIsDependent() {
		final LineItemIdentifier expectedIdentifier = mock(LineItemIdentifier.class);

		final String lineItemId = UUID.randomUUID().toString();
		final String dependentLineItemId = UUID.randomUUID().toString();

		givenLineItemIdentiferHasLineItemId(dependentLineItemId);
		givenLineItemIdentifierHasCartId(CART_ID);

		final ShoppingItem parentShoppingItem = givenAParentLineItem(shoppingCart, lineItemId);
		givenADependentLineItem(shoppingCart, parentShoppingItem, dependentLineItemId);

		when(shoppingCartRepository.getShoppingCart(CART_ID)).thenReturn(Single.just(shoppingCart));
		doReturn(expectedIdentifier)
				.when(lineItemIdentifierRepository).buildLineItemIdentifier(shoppingCart, parentShoppingItem);

		final Maybe<LineItemIdentifier> parentLineItemIdentifierOrNull = repository.findParent(lineItemIdentifier);

		assertThat(parentLineItemIdentifierOrNull.blockingGet())
				.isEqualTo(expectedIdentifier);
	}

	private void givenLineItemIdentiferHasLineItemId(final String lineItemId) {
		when(lineItemIdentifier.getLineItemId())
				.thenReturn(StringIdentifier.of(lineItemId));
	}

	private void givenLineItemIdentifierHasCartId(final String cartId) {
		final CartIdentifier cartIdentifier = CartIdentifier.builder()
				.withCartId(StringIdentifier.of(cartId))
				.withScope(StringIdentifier.of("scope"))
				.build();
		final LineItemsIdentifier lineItemsIdentifier = LineItemsIdentifier.builder()
				.withCart(cartIdentifier)
				.build();

		when(lineItemIdentifier.getLineItems())
				.thenReturn(lineItemsIdentifier);
	}

	private ShoppingItem givenAParentLineItem(final ShoppingCart shoppingCart, final String lineItemId) {
		final ShoppingItem shoppingItem = mock(ShoppingItem.class);

		when(shoppingItem.getGuid()).thenReturn(lineItemId);
		when(shoppingItem.getChildren()).thenReturn(Lists.newArrayList());

		when(shoppingCartRepository.getShoppingItem(lineItemId, shoppingCart))
				.thenReturn(Single.just(shoppingItem));

		shoppingCart.getAllShoppingItems().add(shoppingItem);

		return shoppingItem;
	}

	private ShoppingItem givenADependentLineItem(final ShoppingCart shoppingCart, final ShoppingItem parentShoppingItem, final String
			dependentLineItemId) {
		final ShoppingItem dependentShoppingItem = givenAParentLineItem(shoppingCart, dependentLineItemId);

		parentShoppingItem.getChildren().add(dependentShoppingItem);

		return dependentShoppingItem;
	}

}