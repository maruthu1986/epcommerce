/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemdefinitions.link.impl;

import java.util.Collection;
import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.itemdefinitions.ItemdefinitionsMediaTypes;
import com.elasticpath.rest.definition.items.ItemEntity;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.itemdefinitions.rel.ItemDefinitionResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.uri.URIUtil;


/**
 * Link strategy to add definition link to item.
 */
@Singleton
@Named("addItemDefinitionLinkToItemStrategy")
public final class AddItemDefinitionLinkToItemStrategy implements ResourceStateLinkHandler<ItemEntity> {

	private final String resourceServerName;


	/**
	 * Default constructor.
	 *
	 * @param resourceServerName the resource server name
	 */
	@Inject
	public AddItemDefinitionLinkToItemStrategy(
			@Named("resourceServerName")
			final String resourceServerName) {

		this.resourceServerName = resourceServerName;
	}


	@Override
	public Collection<ResourceLink> getLinks(final ResourceState<ItemEntity> itemRepresentation) {

		String itemId = itemRepresentation.getEntity().getItemId();
		String itemDefinitionUri = URIUtil.format(resourceServerName, itemRepresentation.getScope(), itemId);
		ResourceLink itemDefinitionLink = ResourceLinkFactory.create(itemDefinitionUri, ItemdefinitionsMediaTypes.ITEM_DEFINITION.id(),
				ItemDefinitionResourceRels.DEFINITION_REL, ItemDefinitionResourceRels.ITEM_REV);

		return Collections.singleton(itemDefinitionLink);
	}
}
