/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemdefinitions.components.transform;

import static com.elasticpath.rest.test.AssertResourceState.assertResourceState;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;

import com.elasticpath.rest.definition.base.DetailsEntity;
import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionComponentEntity;
import com.elasticpath.rest.definition.items.ItemsMediaTypes;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Components;
import com.elasticpath.rest.resource.itemdefinitions.constant.ItemDefinitionResourceConstants;
import com.elasticpath.rest.resource.itemdefinitions.rel.ItemDefinitionResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.uri.ItemsUriBuilder;
import com.elasticpath.rest.schema.uri.ItemsUriBuilderFactory;
import com.elasticpath.rest.schema.util.ElementListFactory;
import com.elasticpath.rest.uri.URIUtil;


/**
 * Tests {@link ItemDefinitionComponentTransformer}.
 */
public final class ItemDefinitionComponentTransformerTest {

	private static final String PARENT_URI = "parentUri";
	private static final String ITEMS_RESOURCE_NAME = "items";
	private static final int QUANTITY = 4;
	private static final String DISPLAY_NAME = "display-name";
	private static final String SCOPE = "scope";
	private static final String ITEM_DEF_ID = "itemDefId";
	private static final String COMPONENT_ID = "componentId";
	private static final String STANDALONE_ITEM_ID = "standalone item id";

	private final ItemsUriBuilderFactory testItemsUriBuilderFactory = new TestItemsUriBuilderFactory();
	private final ItemDefinitionComponentTransformer transformer = new ItemDefinitionComponentTransformer(testItemsUriBuilderFactory);

	/**
	 * Tests the transform to representation.
	 */
	@Test
	public void testTransformToRepresentation() {

		Collection<DetailsEntity> details = new ArrayList<>();

		ItemDefinitionComponentEntity testDto = ItemDefinitionComponentEntity.builder()
				.withDisplayName(DISPLAY_NAME)
				.withQuantity(QUANTITY)
				.withStandaloneItemId(STANDALONE_ITEM_ID)
				.withDetails(details)
				.build();

		ResourceState<ItemDefinitionComponentEntity> representation =
				transformer.transformToRepresentation(SCOPE, PARENT_URI, ITEM_DEF_ID, COMPONENT_ID, true, testDto);

		assertEquals(ITEM_DEF_ID, representation.getEntity().getItemId());
		assertEquals(COMPONENT_ID, representation.getEntity().getComponentId());
		assertEquals(SCOPE, representation.getScope());
		assertEquals(STANDALONE_ITEM_ID, representation.getEntity().getStandaloneItemId());

		String expectedUri = URIUtil.format(PARENT_URI, Components.URI_PART, COMPONENT_ID);

		String expectedItemUri = URIUtil.format(ITEMS_RESOURCE_NAME, SCOPE, STANDALONE_ITEM_ID);
		ResourceLink expectedItemLink =
				ResourceLinkFactory.createNoRev(expectedItemUri, ItemsMediaTypes.ITEM.id(), ItemDefinitionResourceRels.STAND_ALONE_ITEM_REL);

		String expectedListUri = URIUtil.format(PARENT_URI, Components.URI_PART);
		ResourceLink expectedListLink = ElementListFactory.createListWithoutElement(expectedListUri, CollectionsMediaTypes.LINKS.id());

		assertResourceState(representation)
				.self(SelfFactory.createSelf(expectedUri))
				.resourceInfoMaxAge(ItemDefinitionResourceConstants.MAX_AGE)
				.containsLink(expectedItemLink)
				.containsLink(expectedListLink);
	}

	/**
	 * A factory for creating {@link ItemsUriBuilder} objects.
	 */
	public final class TestItemsUriBuilderFactory implements ItemsUriBuilderFactory, ItemsUriBuilder {

		private String scope;
		private String itemId;

		@Override
		public ItemsUriBuilder get() {
			return this;
		}

		@Override
		public ItemsUriBuilder setScope(final String scope) {
			this.scope = scope;
			return this;
		}

		@Override
		public ItemsUriBuilder setItemId(final String itemId) {
			this.itemId = itemId;
			return this;
		}

		@Override
		public String build() {
			return URIUtil.format(ITEMS_RESOURCE_NAME, scope, itemId);
		}
	}
}
