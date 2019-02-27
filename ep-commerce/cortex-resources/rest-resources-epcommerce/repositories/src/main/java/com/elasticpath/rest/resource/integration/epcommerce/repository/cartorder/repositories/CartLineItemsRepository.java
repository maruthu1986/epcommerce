/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.repositories;

import io.reactivex.Completable;
import io.reactivex.Observable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.carts.CartIdentifier;
import com.elasticpath.rest.definition.carts.LineItemIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.carts.lineitems.LineItemIdentifierRepository;

/**
 * Repository for line items in a cart.
 *
 * @param <I> the identifier type
 * @param <LI> the linked identifier type
 */
@Component
public class CartLineItemsRepository<I extends CartIdentifier, LI extends LineItemIdentifier>
		implements LinksRepository<CartIdentifier, LineItemIdentifier> {

	private ShoppingCartRepository shoppingCartRepository;
	private LineItemIdentifierRepository lineItemIdentifierRepository;

	@Override
	public Observable<LineItemIdentifier> getElements(final CartIdentifier cartIdentifier) {
		return shoppingCartRepository.getShoppingCart(cartIdentifier.getCartId().getValue())
				.flatMapObservable(shoppingCart -> Observable.fromIterable(shoppingCart.getRootShoppingItems()))
				.map(shoppingItem -> lineItemIdentifierRepository.buildLineItemIdentifier(cartIdentifier, shoppingItem));
	}

	@Override
	public Completable deleteAll(final CartIdentifier cartIdentifier) {
		return shoppingCartRepository.getShoppingCart(cartIdentifier.getCartId().getValue())
				.flatMapCompletable(shoppingCart -> shoppingCartRepository.removeAllItemsFromCart(shoppingCart));

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
