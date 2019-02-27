/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemdefinitions.components.link.impl;

import com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionEntity;
import com.elasticpath.rest.definition.itemdefinitions.ItemdefinitionsMediaTypes;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.SelfFactory;

/**
 * The test of {@link AddComponentsLinkToItemDefinitionStrategy}.
 */
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public final class AddComponentsLinkToItemDefinitionStrategyTest
		extends AbstractAddComponentsLinkToItemDefinitionStrategyTest<ItemDefinitionEntity> {

	@Override
	public void setUpStrategy() {
		strategy = new AddComponentsLinkToItemDefinitionStrategy(addComponentsLinkToItemDefinitionCommons);
	}

	@Override
	ResourceState<ItemDefinitionEntity> createRepresentationWithSelf() {
		return ResourceState.Builder
				.create(ItemDefinitionEntity.builder()
						.withItemId(ITEM_ID)
						.build())
				.withSelf(SelfFactory.createSelf(SELF_URI, ItemdefinitionsMediaTypes.ITEM_DEFINITION.id()))
				.withScope(SCOPE)
				.build();
	}
}
