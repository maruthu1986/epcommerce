/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemdefinitions.transform;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

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
 * Tests the {@link ItemDefinitionOptionTransformer}.
 */
public final class ItemDefinitionOptionTransformerTest {

	private static final String COMPONENT_ID = "component_id";
	private static final String RESOURCE_NAME = "itemdefinitions";
	private static final String VALUE_CODE = "value_code";
	private static final String VALUE_ID = Base32Util.encode(VALUE_CODE);
	private static final String OPTION_CODE = "option_code";
	private static final String OPTION_ID = Base32Util.encode(OPTION_CODE);
	private static final String OPTION_DISPLAY_NAME = "option_display_name";
	private static final String OPTION_NAME = "option_name";
	private static final String SCOPE = "scope";
	private static final String ITEM_ID = "item_id";
	private static final String ITEMDEFINITION_URI = URIUtil.format(RESOURCE_NAME, SCOPE, ITEM_ID);

	private final ItemDefinitionOptionTransformer transformer = new ItemDefinitionOptionTransformer();

	/**
	 * Test transform to representation.
	 */
	@Test
	public void testTransformToRepresentation() {
		ItemDefinitionOptionEntity itemDefinitionOptionEntity = createItemDefinitionOptionEntity();
		ResourceState<ItemDefinitionOptionEntity> expectedItemDefinitionOption = createExpectedItemDefinitionOption(itemDefinitionOptionEntity);

		ResourceState<ItemDefinitionOptionEntity> itemDefinitionOption =
				transformer.transformToRepresentation(itemDefinitionOptionEntity, ITEMDEFINITION_URI, SCOPE, ITEM_ID, COMPONENT_ID);

		assertEquals(expectedItemDefinitionOption, itemDefinitionOption);
	}

	private ResourceState<ItemDefinitionOptionEntity> createExpectedItemDefinitionOption(final ItemDefinitionOptionEntity entity) {

		String optionsUri = URIUtil.format(ITEMDEFINITION_URI, Options.URI_PART);
		ResourceLink optionsLink = ElementListFactory.createListWithoutElement(optionsUri, CollectionsMediaTypes.LINKS.id());

		String optionUri = URIUtil.format(optionsUri, OPTION_ID);
		Self self = SelfFactory.createSelf(optionUri);

		String valueUri = URIUtil.format(optionUri, Values.URI_PART, VALUE_ID);
		ResourceLink valueLink = ResourceLinkFactory.create(valueUri, ItemdefinitionsMediaTypes.ITEM_DEFINITION_OPTION_VALUE.id(),
				ItemDefinitionResourceRels.VALUE_REL, ItemDefinitionResourceRels.OPTION_REV);

		return ResourceState.Builder
				.create(ItemDefinitionOptionEntity.builderFrom(entity)
						.withItemId(ITEM_ID)
						.withOptionId(OPTION_ID)
						.withComponentId(COMPONENT_ID)
						.build())
				.withSelf(self)
				.withResourceInfo(
					ResourceInfo.builder()
						.withMaxAge(ItemDefinitionResourceConstants.MAX_AGE)
						.build())
				.withScope(SCOPE)
				.addingLinks(valueLink, optionsLink)
				.build();
	}

	private ItemDefinitionOptionEntity createItemDefinitionOptionEntity() {

		return ItemDefinitionOptionEntity.builder()
				.withName(OPTION_NAME)
				.withDisplayName(OPTION_DISPLAY_NAME)
				.withOptionId(OPTION_CODE)
				.withOptionValueId(VALUE_CODE)
				.build();
	}
}
