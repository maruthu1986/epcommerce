/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.carts.prototypes;

import java.util.Map;
import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.rest.definition.carts.AddToDefaultCartFormResource;
import com.elasticpath.rest.definition.carts.LineItemEntity;
import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.helix.data.annotation.ResourceService;
import com.elasticpath.rest.helix.data.annotation.UriPart;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartItemModifiersRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;

/**
 * Add to default cart form.
 */
public class ReadAddToDefaultCartFormPrototype implements AddToDefaultCartFormResource.Read {

	private final IdentifierPart<Map<String, String>> itemId;

	private final CartItemModifiersRepository cartItemModifiersRepository;

	/**
	 * Constructor.
	 *
	 * @param itemId                      item id
	 * @param cartItemModifiersRepository cartItemModifiersRepository
	 */
	@Inject
	public ReadAddToDefaultCartFormPrototype(@UriPart(ItemIdentifier.ITEM_ID) final IdentifierPart<Map<String, String>> itemId,
											 @ResourceService final CartItemModifiersRepository cartItemModifiersRepository) {
		this.itemId = itemId;
		this.cartItemModifiersRepository = cartItemModifiersRepository;
	}

	@Override
	public Single<LineItemEntity> onRead() {
		return cartItemModifiersRepository.getConfiguration(this.itemId.getValue().get(ItemRepository.SKU_CODE_KEY))
				.map(configuration -> LineItemEntity.builder()
						.withQuantity(0)
						.withConfiguration(configuration)
						.build());

	}
}
