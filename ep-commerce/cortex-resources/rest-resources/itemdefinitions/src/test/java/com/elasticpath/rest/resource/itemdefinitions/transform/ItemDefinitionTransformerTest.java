/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemdefinitions.transform;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;

import com.elasticpath.rest.ResourceInfo;
import com.elasticpath.rest.definition.base.DetailsEntity;
import com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionEntity;
import com.elasticpath.rest.definition.items.ItemsMediaTypes;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Options;
import com.elasticpath.rest.resource.itemdefinitions.constant.ItemDefinitionResourceConstants;
import com.elasticpath.rest.resource.itemdefinitions.rel.ItemDefinitionResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.schema.uri.ItemsUriBuilder;
import com.elasticpath.rest.schema.uri.ItemsUriBuilderFactory;
import com.elasticpath.rest.uri.URIUtil;


/**
 * Tests the {@link ItemDefinitionTransformer}.
 */
public final class ItemDefinitionTransformerTest {

	private static final String TEST_RESOURCE_SERVER_NAME = "testResource";
	private static final String TEST_DISPLAY_NAME = "testDisplayName";
	private static final String TEST_ITEM_ID = "testConfigurationCode";
	private static final String TEST_SCOPE = "testScope";
	private static final String MOCK_ITEM_URI = "/mock/item/uri";

	private final TestItemsUriBuilderFactory testItemsUriBuilderFactory = new TestItemsUriBuilderFactory();
	private final ItemDefinitionTransformer itemDefinitionsTransformer = new ItemDefinitionTransformer(TEST_RESOURCE_SERVER_NAME,
			testItemsUriBuilderFactory);

	@Test
	public void testTransformToRepresentation() {

		Collection<DetailsEntity> attributes = new ArrayList<>(1);
		DetailsEntity attribute = DetailsEntity.builder()
				.withName("key")
				.withDisplayName("description")
				.withDisplayValue("displayValue")
				.withValue("value")
				.build();

		attributes.add(attribute);

		ItemDefinitionEntity itemDefinitionEntity = ItemDefinitionEntity.builder()
				.withDisplayName(TEST_DISPLAY_NAME)
				.withItemId(TEST_ITEM_ID)
				.withDetails(attributes)
				.build();

		ResourceState<ItemDefinitionEntity> representation =
				itemDefinitionsTransformer.transformToRepresentation(itemDefinitionEntity, TEST_SCOPE, true);

		ResourceLink itemLink = createItemLink();

		String optionsUri = URIUtil.format(TEST_RESOURCE_SERVER_NAME, TEST_SCOPE, TEST_ITEM_ID, Options.URI_PART);
		ResourceLink optionsLink = ResourceLinkFactory.create(optionsUri, CollectionsMediaTypes.LINKS.id(),
				ItemDefinitionResourceRels.OPTIONS_REL, ItemDefinitionResourceRels.DEFINITION_REV);
		ResourceState<ItemDefinitionEntity> expectedRepresentation =
				createExpectedRepresentation(itemDefinitionEntity, Arrays.asList(itemLink, optionsLink));

		assertEquals(expectedRepresentation, representation);
	}

	@Test
	public void testTransformToRepresentationWithNoOptions() {
		ItemDefinitionEntity itemDefinitionEntity = ItemDefinitionEntity.builder()
				.withDisplayName(TEST_DISPLAY_NAME)
				.withItemId(TEST_ITEM_ID)
				.build();

		ResourceState<ItemDefinitionEntity> representation =
				itemDefinitionsTransformer.transformToRepresentation(itemDefinitionEntity, TEST_SCOPE, false);

		ResourceLink itemLink = createItemLink();

		ResourceState<ItemDefinitionEntity> expectedRepresentation = createExpectedRepresentation(itemDefinitionEntity, Arrays.asList(itemLink));

		assertEquals(expectedRepresentation, representation);
	}

	private ResourceState<ItemDefinitionEntity> createExpectedRepresentation(final ItemDefinitionEntity itemDefinitionEntity,
			final Collection<ResourceLink> linksToAdd) {
		String expectedSelfUri = URIUtil.format(TEST_RESOURCE_SERVER_NAME, TEST_SCOPE, TEST_ITEM_ID);

		Self self = SelfFactory.createSelf(expectedSelfUri);

		return ResourceState.Builder
				.create(ItemDefinitionEntity.builderFrom(itemDefinitionEntity)
						.withItemId(TEST_ITEM_ID)
						.build())
				.withScope(TEST_SCOPE)
				.withSelf(self)
				.withResourceInfo(
					ResourceInfo.builder()
						.withMaxAge(ItemDefinitionResourceConstants.MAX_AGE)
						.build())
				.addingLinks(linksToAdd)
				.build();
	}

	private ResourceLink createItemLink() {
		return ResourceLinkFactory.create(MOCK_ITEM_URI, ItemsMediaTypes.ITEM.id(), ItemDefinitionResourceRels.ITEM_REL,
				ItemDefinitionResourceRels.DEFINITION_REV);
	}

	/**
	 * Test item uri builder factory.
	 */
	private class TestItemsUriBuilderFactory implements ItemsUriBuilderFactory, ItemsUriBuilder {

		@Override
		public ItemsUriBuilder get() {
			return this;
		}

		@Override
		public ItemsUriBuilder setScope(final String scope) {
			return this;
		}

		@Override
		public ItemsUriBuilder setItemId(final String itemId) {
			return this;
		}

		@Override
		public String build() {
			return MOCK_ITEM_URI;
		}
	}
}
