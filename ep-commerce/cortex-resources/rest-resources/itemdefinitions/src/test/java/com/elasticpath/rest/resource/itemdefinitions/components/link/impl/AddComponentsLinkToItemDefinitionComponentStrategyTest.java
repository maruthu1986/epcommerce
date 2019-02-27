/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemdefinitions.components.link.impl;

import com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionComponentEntity;
import com.elasticpath.rest.definition.itemdefinitions.ItemdefinitionsMediaTypes;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.SelfFactory;


/**
 * The test of {@link AddComponentsLinkToItemDefinitionComponentStrategy}.
 */
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public final class AddComponentsLinkToItemDefinitionComponentStrategyTest
		extends AbstractAddComponentsLinkToItemDefinitionStrategyTest<ItemDefinitionComponentEntity> {

	@Override
	public void setUpStrategy() {
		strategy = new AddComponentsLinkToItemDefinitionComponentStrategy(addComponentsLinkToItemDefinitionCommons);
	}

	@Override
	ResourceState<ItemDefinitionComponentEntity> createRepresentationWithSelf() {
		return ResourceState.Builder
				.create(ItemDefinitionComponentEntity.builder()
						.withStandaloneItemId(ITEM_ID)
						.build())
				.withSelf(SelfFactory.createSelf(SELF_URI, ItemdefinitionsMediaTypes.ITEM_DEFINITION_COMPONENT.id()))
				.withScope(SCOPE)
				.build();
	}
}
