/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.carts.lineitems.impl;

import java.util.function.Predicate;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.rest.definition.carts.LineItemIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.carts.lineitems.DependentLineItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.carts.lineitems.LineItemIdentifierRepository;

/**
 * Default implementation of {@link DependentLineItemRepository}.
 */
@Component
public class DependentLineItemRepositoryImpl implements DependentLineItemRepository {

	private ShoppingCartRepository shoppingCartRepository;
	private LineItemIdentifierRepository lineItemIdentifierRepository;

	@Override
	public Maybe<LineItemIdentifier> findParent(final LineItemIdentifier childLineItemIdentifier) {
		final Single<ShoppingCart> shoppingCartSingle = getShoppingCart(childLineItemIdentifier);

		return shoppingCartSingle.flatMapMaybe(shoppingCart -> shoppingCart.getAllShoppingItems()
				.stream()
				.filter(getAnyChildMatchesIdentifierPredicate(childLineItemIdentifier))
				.findFirst()
				.map(shoppingItem -> lineItemIdentifierRepository.buildLineItemIdentifier(shoppingCart, shoppingItem))
				.map(Maybe::just)
				.orElseGet(Maybe::empty));

	}

	@Override
	public Observable<LineItemIdentifier> getElements(final LineItemIdentifier lineItemIdentifier) {
		final Single<ShoppingCart> shoppingCartSingle = getShoppingCart(lineItemIdentifier);

		return shoppingCartSingle.flatMapObservable(shoppingCart ->
				shoppingCartRepository.getShoppingItem(lineItemIdentifier.getLineItemId().getValue(), shoppingCart)
						.flatMapObservable(shoppingItem -> Observable.fromIterable(shoppingItem.getChildren()))
						.map(shoppingItem -> lineItemIdentifierRepository.buildLineItemIdentifier(shoppingCart, shoppingItem)));
	}


	/**
	 * Predicate that accepts {@link ShoppingItem} instances that contain any child item that matches the given identifier.
	 *
	 * @param childLineItemIdentifier the identifier to attempt to match
	 * @return a predicate that accepts {@link ShoppingItem} instances with a matching child item
	 */
	protected Predicate<ShoppingItem> getAnyChildMatchesIdentifierPredicate(final LineItemIdentifier childLineItemIdentifier) {
		return shoppingItem -> shoppingItem.getChildren().stream()
				.anyMatch(childShoppingItem -> childShoppingItem.getGuid().equals(childLineItemIdentifier.getLineItemId().getValue()));
	}

	/**
	 * Returns the corresponding Shopping Cart containing the line item.
	 *
	 * @param lineItemIdentifier the line item
	 * @return the shopping cart containing the line item
	 */
	protected Single<ShoppingCart> getShoppingCart(final LineItemIdentifier lineItemIdentifier) {
		return shoppingCartRepository.getShoppingCart(lineItemIdentifier.getLineItems().getCart().getCartId().getValue());
	}

	@Reference
	public void setShoppingCartRepository(final ShoppingCartRepository shoppingCartRepository) {
		this.shoppingCartRepository = shoppingCartRepository;
	}

	@Reference
	public void setLineItemIdentifierRepository(final LineItemIdentifierRepository lineItemIdentifierRepository) {
		this.lineItemIdentifierRepository = lineItemIdentifierRepository;
	}

}