/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.repositories;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Single;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.carts.LineItemIdentifier;
import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.definition.items.ItemsIdentifier;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;

/**
 * Item for line item repository.
 *
 * @param <I>  the identifier type
 * @param <LI> the linked identifier type
 */
@Component
public class ItemIdentifierRepositoryImpl<I extends LineItemIdentifier, LI extends ItemIdentifier>
		implements LinksRepository<LineItemIdentifier, ItemIdentifier> {

	private ShoppingCartRepository shoppingCartRepository;

	private ItemRepository itemRepository;

	@Override
	public Observable<ItemIdentifier> getElements(final LineItemIdentifier identifier) {
		String lineItemId = identifier.getLineItemId().getValue();
		String scope = identifier.getLineItems().getCart().getScope().getValue();

		return shoppingCartRepository.getProductSku(identifier.getLineItems().getCart().getCartId().getValue(), lineItemId)
				.map(productSku -> itemRepository.getItemIdForProductSku(productSku))
				.flatMapObservable(itemId -> buildItemIdentifier(scope, itemId).toObservable());
	}

	/**
	 * Build item identifier.
	 *
	 * @param scope  scope
	 * @param itemId itemId
	 * @return item identifier
	 */
	protected Single<ItemIdentifier> buildItemIdentifier(final String scope, final IdentifierPart<Map<String, String>> itemId) {
		return Single.just(ItemIdentifier.builder()
				.withItemId(itemId)
				.withItems(ItemsIdentifier.builder()
						.withScope(StringIdentifier.of(scope))
						.build())
				.build());
	}

	@Reference
	public void setShoppingCartRepository(final ShoppingCartRepository shoppingCartRepository) {
		this.shoppingCartRepository = shoppingCartRepository;
	}

	@Reference
	public void setItemRepository(final ItemRepository itemRepository) {
		this.itemRepository = itemRepository;
	}

}


