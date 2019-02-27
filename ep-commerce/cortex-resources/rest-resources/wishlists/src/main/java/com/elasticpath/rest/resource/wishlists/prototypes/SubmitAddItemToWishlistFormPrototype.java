/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.wishlists.prototypes;

import java.util.Map;
import javax.inject.Inject;

import io.reactivex.Single;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.definition.wishlists.AddItemToWishlistFormResource;
import com.elasticpath.rest.definition.wishlists.WishlistLineItemEntity;
import com.elasticpath.rest.definition.wishlists.WishlistLineItemIdentifier;
import com.elasticpath.rest.definition.wishlists.WishlistsIdentifier;
import com.elasticpath.rest.form.SubmitResult;
import com.elasticpath.rest.helix.data.annotation.ResourceRepository;
import com.elasticpath.rest.helix.data.annotation.UriPart;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Default;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;

/**
 * Submit add item to favorites form.
 */
public class SubmitAddItemToWishlistFormPrototype implements AddItemToWishlistFormResource.SubmitWithResult {


	private final String itemId;

	private final IdentifierPart<String> scope;

	private final Repository<WishlistLineItemEntity, WishlistLineItemIdentifier> repository;

	/**
	 * Constructor.
	 *
	 * @param itemId     itemId
	 * @param scope      scope
	 * @param repository repository
	 */
	@Inject
	public SubmitAddItemToWishlistFormPrototype(@UriPart(ItemIdentifier.ITEM_ID) final IdentifierPart<Map<String, String>> itemId,
												@UriPart(WishlistsIdentifier.SCOPE) final IdentifierPart<String> scope,
												@ResourceRepository final Repository<WishlistLineItemEntity, WishlistLineItemIdentifier>
														repository) {
		this.itemId = itemId.getValue().get(ItemRepository.SKU_CODE_KEY);
		this.scope = scope;
		this.repository = repository;
	}

	@Override
	public Single<SubmitResult<WishlistLineItemIdentifier>> onSubmitWithResult() {
		final WishlistLineItemEntity lineItemEntity = WishlistLineItemEntity.builder()
				.withItemId(itemId)
				.withWishlistId(Default.URI_PART)
				.build();

		return repository.submit(lineItemEntity, scope);
	}
}
