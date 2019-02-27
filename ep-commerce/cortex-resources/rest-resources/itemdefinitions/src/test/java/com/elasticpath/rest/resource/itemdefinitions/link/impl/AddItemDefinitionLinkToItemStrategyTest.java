/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemdefinitions.link.impl;

import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Collections;

import org.junit.Test;

import com.elasticpath.rest.definition.itemdefinitions.ItemdefinitionsMediaTypes;
import com.elasticpath.rest.definition.items.ItemEntity;
import com.elasticpath.rest.definition.items.ItemsMediaTypes;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.uri.URIUtil;
import com.elasticpath.rest.util.collection.CollectionUtil;

/**
 * Test class for {@link AddItemDefinitionLinkToItemStrategy}.
 */
public final class AddItemDefinitionLinkToItemStrategyTest {
	private static final String ITEM = "item";
	private static final String DEFINITION = "definition";
	private static final String ITEMS = "items";
	private static final String ITEM_ID = "item_id";
	private static final String SCOPE = "scope";
	private static final String ITEM_DEFINITIONS = "itemdefinitions";
	private final AddItemDefinitionLinkToItemStrategy strategy = new AddItemDefinitionLinkToItemStrategy(ITEM_DEFINITIONS);

	/**
	 * Test create link to item.
	 */
	@Test
	public void testCreateLinkToItem() {
		String expectedUri = URIUtil.format(ITEM_DEFINITIONS, SCOPE, ITEM_ID);
		ResourceLink expectedLink = ResourceLinkFactory.create(expectedUri, ItemdefinitionsMediaTypes.ITEM_DEFINITION.id(), DEFINITION, ITEM);

		Collection<ResourceLink> resultLinks = strategy.getLinks(createItemRepresentation());
		assertTrue(CollectionUtil.containsOnly(resultLinks, Collections.singleton(expectedLink)));
	}

	private ResourceState<ItemEntity> createItemRepresentation() {
		Self itemSelf = SelfFactory.createSelf(URIUtil.format(ITEMS, SCOPE, ITEM_ID), ItemsMediaTypes.ITEM.id());
		return ResourceState.Builder
				.create(ItemEntity.builder()
						.withItemId(ITEM_ID)
						.build())
				.withSelf(itemSelf)
				.withScope(SCOPE)
				.build();
	}

}
