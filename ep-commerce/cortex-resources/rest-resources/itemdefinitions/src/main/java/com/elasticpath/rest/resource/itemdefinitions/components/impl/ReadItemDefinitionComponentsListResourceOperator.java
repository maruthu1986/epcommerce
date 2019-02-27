/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemdefinitions.components.impl;

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
import com.elasticpath.rest.resource.dispatch.operator.annotation.Components;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.itemdefinitions.components.ItemDefinitionComponentLookup;
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
 * Command for reading list of components for an item definition.
 */
@Singleton
@Named("readItemDefinitionComponentsListResourceOperator")
@Path({AnyResourceUri.PATH_PART, Components.PATH_PART})
public final class ReadItemDefinitionComponentsListResourceOperator implements ResourceOperator {

	private final ItemDefinitionComponentLookup itemDefinitionComponentLookup;

	/**
	 * Default constructor.
	 *
	 * @param itemDefinitionComponentLookup the item definition component lookup
	 */
	@Inject
	public ReadItemDefinitionComponentsListResourceOperator(
			@Named("itemDefinitionComponentLookup")
			final ItemDefinitionComponentLookup itemDefinitionComponentLookup) {

		this.itemDefinitionComponentLookup = itemDefinitionComponentLookup;
	}


	/**
	 * Process read bundle components list.
	 *
	 * @param itemDefinition the item definition
	 * @param operation the operation
	 * @return the operation result
	 */
	@Path
	@OperationType(Operation.READ)
	public OperationResult processReadComponentsListForItemDefinition(
			@AnyResourceUri
			final ResourceState<ItemDefinitionEntity> itemDefinition,
			final ResourceOperation operation) {

		String itemId = itemDefinition.getEntity().getItemId();
		String scope = itemDefinition.getScope();
		Self definitionSelf = itemDefinition.getSelf();

		Collection<String> components = Assign.ifSuccessful(itemDefinitionComponentLookup.findComponentIds(scope, itemId));
		ResourceState<LinksEntity> links = createLinksRepresentation(definitionSelf, components);

		return OperationResultFactory.createReadOK(links, operation);
	}


	/**
	 * Process read bundle components list.
	 *
	 * @param itemDefinitionComponent the item definition
	 * @param operation the operation
	 * @return the operation result
	 */
	@Path
	@OperationType(Operation.READ)
	public OperationResult processReadComponentsListForComponent(
			@AnyResourceUri
			final ResourceState<ItemDefinitionComponentEntity> itemDefinitionComponent,
			final ResourceOperation operation) {

		String itemId = itemDefinitionComponent.getEntity().getStandaloneItemId();
		String scope = itemDefinitionComponent.getScope();
		Self definitionSelf = itemDefinitionComponent.getSelf();

		Collection<String> components = Assign.ifSuccessful(itemDefinitionComponentLookup.findComponentIds(scope, itemId));
		ResourceState<LinksEntity> links = createLinksRepresentation(definitionSelf, components);

		return OperationResultFactory.createReadOK(links, operation);
	}

	private ResourceState<LinksEntity> createLinksRepresentation(final Self definitionSelf, final Collection<String> components) {

		Collection<ResourceLink> links = new ArrayList<>();
		String definitionUri = definitionSelf.getUri();
		String componentsUri = URIUtil.format(definitionUri, Components.URI_PART);

		ResourceLink definitionLink = ResourceLinkFactory.createFromSelf(definitionSelf,
				ItemDefinitionResourceRels.DEFINITION_REL, ItemDefinitionResourceRels.COMPONENTS_REL);
		links.add(definitionLink);

		Self self = SelfFactory.createSelf(componentsUri);

		for (String componentId : components) {
			String componentElementUri = URIUtil.format(componentsUri, componentId);
			ResourceLink componentElementLink =
					ElementListFactory.createElementOfList(componentElementUri, ItemdefinitionsMediaTypes.ITEM_DEFINITION_COMPONENT.id());
			links.add(componentElementLink);
		}

		return ResourceState.Builder.create(LinksEntity.builder().build())
				.withSelf(self)
				.withResourceInfo(
					ResourceInfo.builder()
						.withMaxAge(ItemDefinitionResourceConstants.MAX_AGE)
						.build())
				.addingLinks(links)
				.build();
	}
}
