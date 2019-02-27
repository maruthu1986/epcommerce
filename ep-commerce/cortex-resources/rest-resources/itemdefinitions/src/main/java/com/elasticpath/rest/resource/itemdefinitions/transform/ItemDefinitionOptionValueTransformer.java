/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemdefinitions.transform;

import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.ResourceInfo;
import com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionOptionValueEntity;
import com.elasticpath.rest.definition.itemdefinitions.ItemdefinitionsMediaTypes;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Options;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Values;
import com.elasticpath.rest.resource.itemdefinitions.constant.ItemDefinitionResourceConstants;
import com.elasticpath.rest.resource.itemdefinitions.rel.ItemDefinitionResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.uri.URIUtil;


/**
 * The Item Definitions Option Value Transformer.
 */
@Singleton
@Named("itemDefinitionOptionValueTransformer")
public final class ItemDefinitionOptionValueTransformer {

	/**
	 * Transforms an {@link ItemDefinitionOptionValueEntity} to a {@link com.elasticpath.rest.schema.ResourceState}.
	 *
	 * @param optionValueEntity the item definition option value dto
	 * @param itemDefinitionUri the item definition selfUri
	 * @param optionId the option ID
	 * @param valueId the value ID
	 * @return the item definition representation
	 */
	public ResourceState<ItemDefinitionOptionValueEntity> transformToRepresentation(final ItemDefinitionOptionValueEntity optionValueEntity,
			final String itemDefinitionUri, final String optionId, final String valueId) {

		String optionUri = URIUtil.format(itemDefinitionUri, Options.URI_PART, optionId);
		ResourceLink optionLink = ResourceLinkFactory.createNoRev(optionUri, ItemdefinitionsMediaTypes.ITEM_DEFINITION_OPTION.id(),
				ItemDefinitionResourceRels.OPTION_REL);

		String selfUri = URIUtil.format(optionUri, Values.URI_PART, valueId);
		Self self = SelfFactory.createSelf(selfUri);

		return ResourceState.Builder.create(optionValueEntity)
				.withSelf(self)
				.withResourceInfo(
					ResourceInfo.builder()
						.withMaxAge(ItemDefinitionResourceConstants.MAX_AGE)
						.build())
				.addingLinks(optionLink)
				.build();
	}
}
