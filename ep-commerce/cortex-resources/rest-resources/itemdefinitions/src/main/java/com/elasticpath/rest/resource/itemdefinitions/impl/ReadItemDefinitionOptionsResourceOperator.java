/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemdefinitions.impl;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.OperationResultFactory;
import com.elasticpath.rest.ResourceInfo;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionComponentEntity;
import com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionEntity;
import com.elasticpath.rest.definition.itemdefinitions.ItemdefinitionsMediaTypes;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.AnyResourceUri;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Options;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.itemdefinitions.ItemDefinitionLookup;
import com.elasticpath.rest.resource.itemdefinitions.constant.ItemDefinitionResourceConstants;
import com.elasticpath.rest.resource.itemdefinitions.rel.ItemDefinitionResourceRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.util.ElementListFactory;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Command for read the options of an item definition.
 */
@Singleton
@Named("readItemDefinitionOptionsResourceOperator")
@Path({AnyResourceUri.PATH_PART, Options.PATH_PART})
public final class ReadItemDefinitionOptionsResourceOperator implements ResourceOperator {

	private final ItemDefinitionLookup itemDefinitionLookup;


	/**
	 * Default constructor.
	 *
	 * @param itemDefinitionLookup the item definition lookup
	 */
	@Inject
	public ReadItemDefinitionOptionsResourceOperator(
			@Named("itemDefinitionLookup")
			final ItemDefinitionLookup itemDefinitionLookup) {

		this.itemDefinitionLookup = itemDefinitionLookup;
	}


	/**
	 * Process read of item definition options.
	 *
	 * @param itemDefinition the item definition
	 * @param operation the operation
	 * @return the operation result
	 */
	@Path
	@OperationType(Operation.READ)
	public OperationResult processReadOptionsForItemDefinition(
			@AnyResourceUri
			final ResourceState<ItemDefinitionEntity> itemDefinition,
			final ResourceOperation operation) {

		String itemId = itemDefinition.getEntity().getItemId();
		String scope = itemDefinition.getScope();
		Self definitionSelf = itemDefinition.getSelf();

		Collection<String> optionIds = Assign.ifSuccessful(itemDefinitionLookup.findOptionIdsForItem(scope, itemId));
		ResourceState<LinksEntity> links = createLinksRepresentation(definitionSelf, optionIds);

		return OperationResultFactory.createReadOK(links, operation);
	}

	/**
	 * Process read of item definition component options.
	 *
	 * @param itemDefinitionComponent the item definition component
	 * @param operation the operation
	 * @return the operation result
	 */
	@Path
	@OperationType(Operation.READ)
	public OperationResult processReadOptionsForItemDefinitionComponent(
			@AnyResourceUri
			final ResourceState<ItemDefinitionComponentEntity> itemDefinitionComponent,
			final ResourceOperation operation) {

		String itemId = itemDefinitionComponent.getEntity().getStandaloneItemId();
		String scope = itemDefinitionComponent.getScope();
		Self definitionSelf = itemDefinitionComponent.getSelf();

		Collection<String> optionIds = Assign.ifSuccessful(itemDefinitionLookup.findOptionIdsForItem(scope, itemId));
		ResourceState<LinksEntity> links = createLinksRepresentation(definitionSelf, optionIds);

		return OperationResultFactory.createReadOK(links, operation);
	}

	private ResourceState<LinksEntity> createLinksRepresentation(final Self definitionSelf, final Collection<String> optionIds) {
		Collection<ResourceLink> links = new ArrayList<>();

		ResourceLink definitionLink = ResourceLinkFactory.createFromSelf(definitionSelf,
				ItemDefinitionResourceRels.DEFINITION_REL, ItemDefinitionResourceRels.OPTIONS_REV);
		links.add(definitionLink);

		String optionsUri = URIUtil.format(definitionSelf.getUri(), Options.URI_PART);
		Self optionsSelf = SelfFactory.createSelf(optionsUri);

		for (String optionId : optionIds) {
			String optionUri = URIUtil.format(optionsUri, optionId);
			ResourceLink optionLink = ElementListFactory.createElementOfList(optionUri, ItemdefinitionsMediaTypes.ITEM_DEFINITION_OPTION.id());
			links.add(optionLink);
		}
		return ResourceState.Builder.create(LinksEntity.builder().build())
				.addingLinks(links)
				.withSelf(optionsSelf)
				.withResourceInfo(
					ResourceInfo.builder()
						.withMaxAge(ItemDefinitionResourceConstants.MAX_AGE)
						.build())
				.build();
	}

}
