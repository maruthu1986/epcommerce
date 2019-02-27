/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemdefinitions.components.transform;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.ResourceInfo;
import com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionComponentEntity;
import com.elasticpath.rest.definition.items.ItemsMediaTypes;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Components;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Options;
import com.elasticpath.rest.resource.itemdefinitions.constant.ItemDefinitionResourceConstants;
import com.elasticpath.rest.resource.itemdefinitions.rel.ItemDefinitionResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.schema.uri.ItemsUriBuilderFactory;
import com.elasticpath.rest.schema.util.ElementListFactory;
import com.elasticpath.rest.uri.URIUtil;

/**
 * The Class ItemDefinitionComponentTransformer.
 */
@Singleton
@Named("itemDefinitionComponentTransformer")
public class ItemDefinitionComponentTransformer {

	private final ItemsUriBuilderFactory itemsUriBuilderFactory;


	/**
	 * Constructor.
	 *
	 * @param itemsUriBuilderFactory the items uri builder factory
	 */
	@Inject
	public ItemDefinitionComponentTransformer(
			@Named("itemsUriBuilderFactory")
			final ItemsUriBuilderFactory itemsUriBuilderFactory) {

		this.itemsUriBuilderFactory = itemsUriBuilderFactory;
	}


	/**
	 * Transform to representation.
	 *
	 * @param scope the scope
	 * @param parentUri the parent Uri.
	 * @param itemDefinitionId the item definition id
	 * @param componentId the component id
	 * @param hasOptions the component has options
	 * @param itemDefinitionComponentEntity the item definition component dto
	 * @return the item definition component representation
	 */
	public ResourceState<ItemDefinitionComponentEntity> transformToRepresentation(final String scope, final String parentUri,
			final String itemDefinitionId, final String componentId, final boolean hasOptions,
			final ItemDefinitionComponentEntity itemDefinitionComponentEntity) {

		String selfUri = URIUtil.format(parentUri, Components.URI_PART, componentId);
		String standaloneItemId = itemDefinitionComponentEntity.getStandaloneItemId();

		Collection<ResourceLink> links = new ArrayList<>();

		ItemDefinitionComponentEntity updatedEntity = ItemDefinitionComponentEntity.builderFrom(itemDefinitionComponentEntity)
				.withComponentId(componentId)
				.withStandaloneItemId(standaloneItemId)
				.withItemId(itemDefinitionId)
				.build();

		links.add(createListLink(parentUri));
		links.add(createStandaloneItemLink(scope, standaloneItemId));

		if (hasOptions) {
			links.add(processItemOptionsLink(selfUri));
		}
		return ResourceState.Builder.create(updatedEntity)
				.withSelf(SelfFactory.createSelf(selfUri))
				.withResourceInfo(
					ResourceInfo.builder()
						.withMaxAge(ItemDefinitionResourceConstants.MAX_AGE)
						.build())
				.withScope(scope)
				.addingLinks(links)
				.build();
	}

	private ResourceLink processItemOptionsLink(final String selfUri) {
		String optionsUri = URIUtil.format(selfUri, Options.URI_PART);
		return ResourceLinkFactory.create(optionsUri, CollectionsMediaTypes.LINKS.id(),
				ItemDefinitionResourceRels.OPTIONS_REL, ItemDefinitionResourceRels.DEFINITION_REV);

	}

	private ResourceLink createListLink(final String parentUri) {

		String listLinkUri = URIUtil.format(parentUri, Components.URI_PART);
		return ElementListFactory.createListWithoutElement(listLinkUri, CollectionsMediaTypes.LINKS.id());
	}

	private ResourceLink createStandaloneItemLink(final String scope, final String itemId) {
		String itemUri = itemsUriBuilderFactory.get()
				.setItemId(itemId)
				.setScope(scope)
				.build();

		return ResourceLinkFactory.createNoRev(itemUri, ItemsMediaTypes.ITEM.id(), ItemDefinitionResourceRels.STAND_ALONE_ITEM_REL);
	}
}
