/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.repositories;

import io.reactivex.Observable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.carts.CartIdentifier;
import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.id.Identifier;
import com.elasticpath.rest.id.transform.IdentifierTransformer;
import com.elasticpath.rest.id.transform.IdentifierTransformerProvider;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;

/**
 * Carts for item membership repository.
 *
 * @param <I>  the identifier type
 * @param <LI> the linked identifier type
 */
@Component
public class CartsForItemRepository<I extends ItemIdentifier, LI extends CartIdentifier> 
		implements LinksRepository<ItemIdentifier, CartIdentifier> {

	private ShoppingCartRepository shoppingCartRepository;

	private ItemRepository itemRepository;

	private IdentifierTransformerProvider identifierTransformerProvider;

	@Override
	public Observable<CartIdentifier> getElements(final ItemIdentifier itemIdentifier) {
		//Deep down item repository expects an encoded id
		final IdentifierTransformer<Identifier> identifierIdentifierTransformer = identifierTransformerProvider.forUriPart(ItemIdentifier.ITEM_ID);
		String encodedItemId = identifierIdentifierTransformer.identifierToUri(itemIdentifier.getItemId());
		return shoppingCartRepository.getDefaultShoppingCart()
				.flatMapObservable(cart -> itemRepository.getSkuForItemIdAsSingle(encodedItemId)
						.flatMapObservable(productSku -> getCartContainingProductSku(cart, productSku)));
	}

	/**
	 * Find cart containing product sku.
	 *
	 * @param cart       cart
	 * @param productSku productSku
	 * @return CartIdentifier
	 */
	protected Observable<CartIdentifier> getCartContainingProductSku(final ShoppingCart cart, final ProductSku productSku) {
		if (cart == null || productSku == null || cart.getCartItems(productSku.getSkuCode()).isEmpty()) {
			return Observable.empty();
		} else {
			return Observable.just(CartIdentifier.builder()
					.withScope(StringIdentifier.of(cart.getStore().getCode()))
					.withCartId(StringIdentifier.of(cart.getGuid()))
					.build());
		}
	}

	@Reference
	public void setShoppingCartRepository(final ShoppingCartRepository shoppingCartRepository) {
		this.shoppingCartRepository = shoppingCartRepository;
	}

	@Reference
	public void setItemRepository(final ItemRepository itemRepository) {
		this.itemRepository = itemRepository;
	}

	@Reference
	public void setIdentifierTransformerProvider(final IdentifierTransformerProvider identifierTransformerProvider) {
		this.identifierTransformerProvider = identifierTransformerProvider;
	}

}
