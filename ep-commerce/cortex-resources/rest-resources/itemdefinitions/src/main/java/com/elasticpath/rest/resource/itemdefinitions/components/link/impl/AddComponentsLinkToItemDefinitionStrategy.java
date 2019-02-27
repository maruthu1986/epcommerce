/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemdefinitions.components.link.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionEntity;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;


/**
 * Strategy to add a components link to an item definition.
 */
@Singleton
@Named("addComponentsLinkToItemDefinitionStrategy")
public final class AddComponentsLinkToItemDefinitionStrategy implements ResourceStateLinkHandler<ItemDefinitionEntity> {

	private final AddComponentsLinkToItemDefinitionCommons addComponentsLinkToItemDefinitionCommons;

	/**
	 * Constructor.
	 *
	 * @param addComponentsLinkToItemDefinitionCommons the common linker
	 */
	@Inject
	AddComponentsLinkToItemDefinitionStrategy(
			@Named("addComponentsLinkToItemDefinitionCommons")
			final AddComponentsLinkToItemDefinitionCommons addComponentsLinkToItemDefinitionCommons) {

		this.addComponentsLinkToItemDefinitionCommons = addComponentsLinkToItemDefinitionCommons;
	}

	@Override
	public Iterable<ResourceLink> getLinks(final ResourceState<ItemDefinitionEntity> resourceState) {
		String itemId = resourceState.getEntity().getItemId();
		return addComponentsLinkToItemDefinitionCommons.getLinks(resourceState, itemId);
	}
}
