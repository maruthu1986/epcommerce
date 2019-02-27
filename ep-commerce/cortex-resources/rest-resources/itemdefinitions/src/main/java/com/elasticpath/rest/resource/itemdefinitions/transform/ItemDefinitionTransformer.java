/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemdefinitions.transform;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.ResourceInfo;
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
import com.elasticpath.rest.schema.uri.ItemsUriBuilderFactory;
import com.elasticpath.rest.uri.URIUtil;


/**
 * The Item Definitions Transformer.
 */
@Singleton
@Named("itemDefinitionTransformer")
public final class ItemDefinitionTransformer {

	private final String resourceServerName;
	private final ItemsUriBuilderFactory itemsUriBuilderFactory;


	/**
	 * Default Constructor.
	 *
	 * @param resourceServerName the resource server name
	 * @param itemsUriBuilderFactory the items uri builder factory
	 */
	@Inject
	public ItemDefinitionTransformer(
			@Named("resourceServerName")
			final String resourceServerName,
			@Named("itemsUriBuilderFactory")
			final ItemsUriBuilderFactory itemsUriBuilderFactory) {

		this.resourceServerName = resourceServerName;
		this.itemsUriBuilderFactory = itemsUriBuilderFactory;
	}


	/**
	 * Transforms an {@link ItemDefinitionEntity} to a {@link ResourceState}.
	 *
	 * @param itemDefinitionEntity the item definition dto
	 * @param scope the scope
	 * @param hasOptions the has options
	 * @return the item definition representation
	 */
	public ResourceState<ItemDefinitionEntity> transformToRepresentation(final ItemDefinitionEntity itemDefinitionEntity, final String scope,
			final boolean hasOptions) {

		String itemId = itemDefinitionEntity.getItemId();

		String selfUri = URIUtil.format(resourceServerName, scope, itemId);
		Self itemDefinitionSelf = SelfFactory.createSelf(selfUri);

		Collection<ResourceLink> links = new ArrayList<>();
		links.add(createItemLink(scope, itemId));
		if (hasOptions) {
			links.add(getItemOptionsLink(selfUri));
		}
		return ResourceState.Builder
				.create(ItemDefinitionEntity.builderFrom(itemDefinitionEntity)
						.withItemId(itemId)
						.build())
				.withScope(scope)
				.withSelf(itemDefinitionSelf)
				.withResourceInfo(
					ResourceInfo.builder()
						.withMaxAge(ItemDefinitionResourceConstants.MAX_AGE)
						.build())
				.addingLinks(links)
				.build();
	}

	private ResourceLink getItemOptionsLink(final String selfUri) {
		String optionsUri = URIUtil.format(selfUri, Options.URI_PART);
		return ResourceLinkFactory.create(optionsUri, CollectionsMediaTypes.LINKS.id(),
				ItemDefinitionResourceRels.OPTIONS_REL, ItemDefinitionResourceRels.DEFINITION_REV);
	}

	private ResourceLink createItemLink(final String scope, final String itemId) {
		String itemUri = itemsUriBuilderFactory.get()
				.setItemId(itemId)
				.setScope(scope)
				.build();
		return ResourceLinkFactory.create(itemUri, ItemsMediaTypes.ITEM.id(), ItemDefinitionResourceRels.ITEM_REL,
				ItemDefinitionResourceRels.DEFINITION_REV);
	}
}
