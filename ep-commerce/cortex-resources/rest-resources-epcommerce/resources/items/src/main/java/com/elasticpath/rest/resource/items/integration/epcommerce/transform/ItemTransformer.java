/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.items.integration.epcommerce.transform;

import java.util.Locale;

import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.items.ItemEntity;
import com.elasticpath.rest.resource.transform.AbstractDomainTransformer;

/**
 * Transforms a item id into a {@link ItemEntity}, and vice versa.
 */
@Singleton
@Named("itemTransformer")
public class ItemTransformer extends AbstractDomainTransformer<String, ItemEntity> {

	@Override
	public String transformToDomain(final ItemEntity resourceEntity, final Locale locale) {
		throw new UnsupportedOperationException("This operation is not implemented.");
	}

	@Override
	public ItemEntity transformToEntity(final String itemId, final Locale locale) {
		return ItemEntity.builder()
				.withItemId(itemId)
				.build();
	}
}
