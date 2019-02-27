/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemselections.transform;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.common.selector.SelectorResourceStateBuilder;
import com.elasticpath.rest.common.selector.SingleSelectorResourceStateBuilder;
import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.definition.controls.SelectorEntity;
import com.elasticpath.rest.definition.itemdefinitions.ItemdefinitionsMediaTypes;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Options;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Selector;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Values;
import com.elasticpath.rest.resource.itemselections.integration.ItemSelectionOptionValuesDto;
import com.elasticpath.rest.resource.itemselections.rel.ItemSelectionsResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.common.selector.SelectorRepresentationRels;
import com.elasticpath.rest.schema.uri.ItemDefinitionsOptionUriBuilderFactory;
import com.elasticpath.rest.uri.URIUtil;


/**
 * Transforms {@link ItemSelectionOptionValuesDto} into a SelectorRepresentation.
 */
@Singleton
@Named("itemSelectionOptionValuesTransformer")
public final class ItemSelectionOptionValuesTransformer {

	private final String resourceServerName;
	private final ItemDefinitionsOptionUriBuilderFactory itemDefinitionsOptionUriBuilderFactory;


	/**
	 * Default constructor.
	 *
	 * @param resourceServerName the resource server name
	 * @param itemDefinitionsOptionUriBuilderFactory the item definitions option uri builder factory
	 */
	@Inject
	public ItemSelectionOptionValuesTransformer(
			@Named("resourceServerName")
			final String resourceServerName,
			@Named("itemDefinitionsOptionUriBuilderFactory")
			final ItemDefinitionsOptionUriBuilderFactory itemDefinitionsOptionUriBuilderFactory) {

		this.resourceServerName = resourceServerName;
		this.itemDefinitionsOptionUriBuilderFactory = itemDefinitionsOptionUriBuilderFactory;
	}


	/**
	 * Transform ItemSelectionOptionValuesDto into a selector representation.
	 *
	 * @param dto the dto
	 * @param scope the scope
	 * @param itemId the item id
	 * @param optionId the option id
	 * @return the selector representation
	 */
	public ResourceState<SelectorEntity> transformToRepresentation(final ItemSelectionOptionValuesDto dto, final String scope,
			final String itemId, final String optionId) {
		SelectorResourceStateBuilder selectorBuilder = new SingleSelectorResourceStateBuilder();

		String optionUri = URIUtil.format(resourceServerName, scope, itemId, Options.URI_PART, optionId);
		String selfUri = URIUtil.format(optionUri, Selector.URI_PART);
		selectorBuilder.setSelfUri(selfUri)
				.setName(ItemSelectionsResourceRels.SELECTOR_NAME);

		for (String optionValueCorrelationId : dto.getSelectableOptionValueCorrelationIds()) {
			String optionValueId = Base32Util.encode(optionValueCorrelationId);
			String choiceUri = URIUtil.format(optionUri, Values.URI_PART, optionValueId, Selector.URI_PART);
			ResourceLink choiceLink = ResourceLinkFactory.createUriType(choiceUri, CollectionsMediaTypes.LINKS.id());
			selectorBuilder.addChoice(choiceLink);
		}

		String selectedValueId = Base32Util.encode(dto.getChosenOptionValueCorrelationId());
		String chosenUri = URIUtil.format(optionUri, Values.URI_PART, selectedValueId, Selector.URI_PART);
		ResourceLink chosenLink = ResourceLinkFactory.createUriType(chosenUri, CollectionsMediaTypes.LINKS.id());
		selectorBuilder.setSelection(chosenLink);

		String itemDefinitionsOptionUri = itemDefinitionsOptionUriBuilderFactory.get()
				.setScope(scope)
				.setItemId(itemId)
				.setOptionId(optionId)
				.build();

		ResourceLink itemDefinitionsOptionLink = ResourceLinkFactory.create(itemDefinitionsOptionUri,
				ItemdefinitionsMediaTypes.ITEM_DEFINITION_OPTION.id(), ItemSelectionsResourceRels.OPTION_REL, SelectorRepresentationRels.SELECTOR);

		selectorBuilder.addLink(itemDefinitionsOptionLink);

		return selectorBuilder.build();
	}
}
