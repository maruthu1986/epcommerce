/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.items.transform;

import static com.elasticpath.rest.schema.SelfFactory.createSelf;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.elasticpath.rest.ResourceInfo;
import com.elasticpath.rest.definition.items.ItemEntity;
import com.elasticpath.rest.resource.items.constant.ItemsResourceConstants;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.transform.TransformToResourceState;
import com.elasticpath.rest.schema.uri.ItemsUriBuilder;
import com.elasticpath.rest.schema.uri.ItemsUriBuilderFactory;

/**
 * The Item Transformer.
 */
@Singleton
@Named("itemTransformer")
public final class ItemTransformer implements TransformToResourceState<ItemEntity, ItemEntity> {

	private final Provider<ItemsUriBuilder> itemsUriBuilder;

	/**
	 * Default Constructor.
	 *
	 * @param itemsUriBuilder items URI Builder Provider.
	 */
	@Inject
	public ItemTransformer(
			@Named("itemsUriBuilderFactory")
			final ItemsUriBuilderFactory itemsUriBuilder) {

		this.itemsUriBuilder = itemsUriBuilder;
	}


	/**
	 * Transforms an {@link ItemEntity} to an {@link ResourceState}.
	 *
	 * @param scope the scope
	 * @param itemEntity the item entity
	 * @return the item definition representation
	 */
	public ResourceState<ItemEntity> transform(final String scope, final ItemEntity itemEntity) {

		String itemId = itemEntity.getItemId();
		String selfUri = itemsUriBuilder.get()
				.setItemId(itemId)
				.setScope(scope)
				.build();
		Self itemSelf = createSelf(selfUri);
		ItemEntity encodedItemEntity = ItemEntity.builder()
				.withItemId(itemId)
				.build();
		return ResourceState.Builder.create(encodedItemEntity)
				.withResourceInfo(
					ResourceInfo.builder()
						.withMaxAge(ItemsResourceConstants.DEFAULT_MAX_AGE)
						.build())
				.withScope(scope)
				.withSelf(itemSelf)
				.build();
	}
}
