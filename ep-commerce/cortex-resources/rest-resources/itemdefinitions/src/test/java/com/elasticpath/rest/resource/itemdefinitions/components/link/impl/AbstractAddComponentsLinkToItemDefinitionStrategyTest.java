/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemdefinitions.components.link.impl;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.hamcrest.Matchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Components;
import com.elasticpath.rest.resource.itemdefinitions.components.ItemDefinitionComponentLookup;
import com.elasticpath.rest.resource.itemdefinitions.rel.ItemDefinitionResourceRels;
import com.elasticpath.rest.schema.ResourceEntity;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.uri.URIUtil;


/**
 * The test of {@link AddComponentsLinkToItemDefinitionCommons}.
 */
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings({"unchecked", "rawtypes"})
public abstract class AbstractAddComponentsLinkToItemDefinitionStrategyTest <T extends ResourceEntity> {

	static final String SELF_URI = "/mock/self/uri";
	static final String ITEM_ID = "itemId";
	static final String SCOPE = "mock";

	@Mock
	ItemDefinitionComponentLookup mockComponentLookup;
	@InjectMocks
	AddComponentsLinkToItemDefinitionCommons addComponentsLinkToItemDefinitionCommons;
	ResourceStateLinkHandler<?> strategy;

	@Before
	public abstract void setUpStrategy();

	abstract ResourceState<? extends ResourceEntity> createRepresentationWithSelf();

	/**
	 * Test {@link ResourceStateLinkHandler#getLinks(ResourceState)} creates a link
	 * when the Item Definition has components.
	 */
	@Test
	public void testCreateLinksWithComponents() {

		when(mockComponentLookup.hasComponents(SCOPE, ITEM_ID))
				.thenReturn(ExecutionResultFactory.createReadOK(true));

		ResourceState representation = createRepresentationWithSelf();
		Iterable<ResourceLink> createdLinks = strategy.getLinks(representation);

		ResourceLink expectedLink = ResourceLinkFactory.create(
				URIUtil.format(SELF_URI, Components.URI_PART),
				CollectionsMediaTypes.LINKS.id(),
				ItemDefinitionResourceRels.COMPONENTS_REL,
				ItemDefinitionResourceRels.DEFINITION_REV);

		assertThat(createdLinks, Matchers.contains(expectedLink));
	}

	/**
	 * Test {@link ResourceStateLinkHandler#getLinks(ResourceState)} creates no links
	 * when the Item Definition does not have components.
	 */
	@Test
	public void testCreateLinksWithoutComponents() {
		when(mockComponentLookup.hasComponents(SCOPE, ITEM_ID))
				.thenReturn(ExecutionResultFactory.createReadOK(false));

		ResourceState representation = createRepresentationWithSelf();
		Iterable<ResourceLink> createdLinks = strategy.getLinks(representation);

		assertThat(createdLinks, Matchers.emptyIterable());
	}

	/**
	 * Test {@link ResourceStateLinkHandler#getLinks(ResourceState)} creates no links
	 * when the lookup fails.
	 */
	@Test
	public void testCreateLinksWithLookupFailure() {
		when(mockComponentLookup.hasComponents(SCOPE, ITEM_ID))
				.thenReturn(ExecutionResultFactory.<Boolean>createNotFound("Failure for test"));

		ResourceState representation = createRepresentationWithSelf();
		Iterable<ResourceLink> createdLinks = strategy.getLinks(representation);

		assertThat(createdLinks, Matchers.emptyIterable());
	}

}
