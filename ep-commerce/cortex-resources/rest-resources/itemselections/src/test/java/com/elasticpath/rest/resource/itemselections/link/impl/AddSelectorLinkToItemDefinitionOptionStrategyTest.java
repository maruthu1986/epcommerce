/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemselections.link.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Test;

import com.elasticpath.rest.definition.controls.ControlsMediaTypes;
import com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionOptionEntity;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Options;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Selector;
import com.elasticpath.rest.resource.itemselections.rel.ItemSelectionsResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.common.selector.SelectorRepresentationRels;
import com.elasticpath.rest.uri.URIUtil;
import com.elasticpath.rest.util.collection.CollectionUtil;


/**
 * Test for {@link com.elasticpath.rest.resource.itemselections.link.impl.AddSelectorLinkToItemDefinitionOptionStrategy}.
 */
public final class AddSelectorLinkToItemDefinitionOptionStrategyTest {

	private static final String COMPONENT_ID = "component_id";
	private static final String SCOPE = "scope";
	private static final String RESOURCE_SERVER_NAME = "itemselection";
	private static final String ITEM_ID = "a1234";
	private static final String OPTION_ID = "o042";

	private final AddSelectorLinkToItemDefinitionOptionStrategy strategy = new AddSelectorLinkToItemDefinitionOptionStrategy(RESOURCE_SERVER_NAME);


	/**
	 * Tests getLinks.
	 */
	@Test
	public void testCreateLinks() {
		ResourceState<ItemDefinitionOptionEntity> optionRepresentation = ResourceState.Builder
				.create(ItemDefinitionOptionEntity.builder()
						.withItemId(ITEM_ID)
						.withOptionId(OPTION_ID)
						.build())
				.withScope(SCOPE)
				.build();
		String expectedUri = URIUtil.format(RESOURCE_SERVER_NAME, SCOPE, ITEM_ID, Options.URI_PART, OPTION_ID, Selector.URI_PART);
		ResourceLink expectedLink = ResourceLinkFactory.create(expectedUri, ControlsMediaTypes.SELECTOR.id(),
				SelectorRepresentationRels.SELECTOR, ItemSelectionsResourceRels.OPTION_REV);

		Collection<ResourceLink> getLinks = strategy.getLinks(optionRepresentation);

		ResourceLink link = CollectionUtil.first(getLinks);
		assertNotNull(link);
		assertEquals(expectedLink, link);
	}

	/**
	 * Test create links when item is not a component.
	 */
	@Test
	public void testCreateLinksWhenItemIsNotAComponent() {
		ResourceState<ItemDefinitionOptionEntity> optionRepresentation = ResourceState.Builder
				.create(ItemDefinitionOptionEntity.builder()
						.withItemId(ITEM_ID)
						.withOptionId(OPTION_ID)
						.withComponentId(COMPONENT_ID)
						.build())
				.withScope(SCOPE)
				.build();

		Collection<ResourceLink> getLinks = strategy.getLinks(optionRepresentation);

		assertTrue(CollectionUtil.isEmpty(getLinks));
	}
}
