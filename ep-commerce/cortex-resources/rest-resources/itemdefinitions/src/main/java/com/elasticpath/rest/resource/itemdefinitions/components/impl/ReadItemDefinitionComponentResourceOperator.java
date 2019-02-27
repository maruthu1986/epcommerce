/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemdefinitions.components.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.OperationResultFactory;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionComponentEntity;
import com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionEntity;
import com.elasticpath.rest.resource.dispatch.operator.ResourceOperator;
import com.elasticpath.rest.resource.dispatch.operator.annotation.AnyResourceUri;
import com.elasticpath.rest.resource.dispatch.operator.annotation.ComponentId;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Components;
import com.elasticpath.rest.resource.dispatch.operator.annotation.OperationType;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Path;
import com.elasticpath.rest.resource.itemdefinitions.components.ItemDefinitionComponentLookup;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.util.ResourceStateUtil;

/**
 * Read Item Definition Component Command.
 */
@Singleton
@Named("readItemDefinitionComponentResourceOperator")
@Path({AnyResourceUri.PATH_PART, Components.PATH_PART, ComponentId.PATH_PART})
public final class ReadItemDefinitionComponentResourceOperator implements ResourceOperator {

	private final ItemDefinitionComponentLookup itemDefinitionComponentLookup;

	/**
	 * Constructor.
	 *
	 * @param itemDefinitionComponentLookup the item definition component lookup
	 */
	@Inject
	public ReadItemDefinitionComponentResourceOperator(
			@Named("itemDefinitionComponentLookup")
			final ItemDefinitionComponentLookup itemDefinitionComponentLookup) {

		this.itemDefinitionComponentLookup = itemDefinitionComponentLookup;
	}


	/**
	 * Process read bundle component.
	 *
	 * @param itemDefinition the item definition
	 * @param componentId the component id
	 * @param operation the operation
	 * @return the operation result
	 */
	@Path
	@OperationType(Operation.READ)
	public OperationResult processReadBundleComponentForItemDefinition(
			@AnyResourceUri
			final ResourceState<ItemDefinitionEntity> itemDefinition,
			@ComponentId
			final String componentId,
			final ResourceOperation operation) {

		String scope = itemDefinition.getScope();
		String itemId = itemDefinition.getEntity().getItemId();
		String parentUri = ResourceStateUtil.getSelfUri(itemDefinition);

		ResourceState<ItemDefinitionComponentEntity> itemDefinitionComponent = Assign.ifSuccessful(
				itemDefinitionComponentLookup.getComponent(scope, parentUri, itemId, componentId));

		return OperationResultFactory.createReadOK(itemDefinitionComponent, operation);
	}

	/**
	 * Process read bundle component.
	 *
	 * @param itemDefinitionComponent the item definition component
	 * @param componentId the component id
	 * @param operation the operation
	 * @return the operation result
	 */
	@Path
	@OperationType(Operation.READ)
	public OperationResult processReadBundleComponentForItemDefintionComponent(
			@AnyResourceUri
			final ResourceState<ItemDefinitionComponentEntity> itemDefinitionComponent,
			@ComponentId
			final String componentId,
			final ResourceOperation operation) {

		String scope = itemDefinitionComponent.getScope();
		String itemId = itemDefinitionComponent.getEntity().getItemId();
		String parentUri = ResourceStateUtil.getSelfUri(itemDefinitionComponent);

		ResourceState<ItemDefinitionComponentEntity> itemDefinitionBundleComponent = Assign.ifSuccessful(
				itemDefinitionComponentLookup.getComponent(scope, parentUri, itemId, componentId));

		return OperationResultFactory.createReadOK(itemDefinitionBundleComponent, operation);
	}
}
