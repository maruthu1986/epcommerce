/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemdefinitions.transform;

import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.ResourceInfo;
import com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionOptionEntity;
import com.elasticpath.rest.definition.itemdefinitions.ItemdefinitionsMediaTypes;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Options;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Values;
import com.elasticpath.rest.resource.itemdefinitions.constant.ItemDefinitionResourceConstants;
import com.elasticpath.rest.resource.itemdefinitions.rel.ItemDefinitionResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.schema.util.ElementListFactory;
import com.elasticpath.rest.uri.URIUtil;


/**
 * Item Definition Option Transformer.
 */
@Singleton
@Named("itemDefinitionOptionTransformer")
public final class ItemDefinitionOptionTransformer {

	/**
	 * Transform to representation.
	 *
	 * @param itemDefinitionOptionEntity the item definition option dto
	 * @param itemDefinitionUri the item definition uri
	 * @param scope the scope
	 * @param itemId the item id
	 * @param componentId the component id
	 * @return the item definition option representation
	 */
	public ResourceState<ItemDefinitionOptionEntity> transformToRepresentation(final ItemDefinitionOptionEntity itemDefinitionOptionEntity,
			final String itemDefinitionUri, final String scope, final String itemId, final String componentId) {

		String optionId = Base32Util.encode(itemDefinitionOptionEntity.getOptionId());
		String selectedValueId = Base32Util.encode(itemDefinitionOptionEntity.getOptionValueId());

		String optionsUri = URIUtil.format(itemDefinitionUri, Options.URI_PART);
		ResourceLink optionsLink = ElementListFactory.createListWithoutElement(optionsUri, CollectionsMediaTypes.LINKS.id());

		String optionUri = URIUtil.format(optionsUri, optionId);
		Self self = SelfFactory.createSelf(optionUri);

		String selectedValueUri = URIUtil.format(optionUri, Values.URI_PART, selectedValueId);
		ResourceLink selectedValueLink = ResourceLinkFactory.create(selectedValueUri, ItemdefinitionsMediaTypes.ITEM_DEFINITION_OPTION_VALUE.id(),
				ItemDefinitionResourceRels.VALUE_REL, ItemDefinitionResourceRels.OPTION_REL);

		return ResourceState.Builder
				.create(ItemDefinitionOptionEntity
						.builderFrom(itemDefinitionOptionEntity)
						.withItemId(itemId)
						.withComponentId(componentId)
						.withOptionId(optionId)
						.build())
				.withScope(scope)
				.withSelf(self)
				.withResourceInfo(
					ResourceInfo.builder()
						.withMaxAge(ItemDefinitionResourceConstants.MAX_AGE)
						.build())
				.addingLinks(selectedValueLink, optionsLink)
				.build();
	}
}
