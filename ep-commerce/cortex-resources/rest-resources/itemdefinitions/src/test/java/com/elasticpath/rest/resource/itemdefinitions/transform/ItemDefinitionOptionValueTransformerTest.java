/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemdefinitions.transform;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.elasticpath.rest.ResourceInfo;
import com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionOptionValueEntity;
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
import com.elasticpath.rest.uri.URIUtil;


/**
 * Tests the {@link ItemDefinitionTransformer}.
 */
public final class ItemDefinitionOptionValueTransformerTest {

	private static final String RESOURCE_SERVER_NAME = "testResource";
	private static final String SCOPE = "testScope";
	private static final String DISPLAY_NAME = "Display Name";
	private static final String OPTION_VALUE_NAME = "OPTION_KEY";
	private static final String CONFIGURATION_CODE = "testConfigurationCode";
	private static final String ENCODED_ITEM_ID = Base32Util.encode(CONFIGURATION_CODE);
	private static final String OPTION_CODE = "option_code";
	private static final String VALUE_CODE = "value_code";
	private static final String ENCODED_OPTION_ID = Base32Util.encode(OPTION_CODE);
	private static final String ENCODED_VALUE_ID = Base32Util.encode(VALUE_CODE);

	private final ItemDefinitionOptionValueTransformer transformer = new ItemDefinitionOptionValueTransformer();

	/**
	 * Test transform to representation.
	 */
	@Test
	public void testTransformToRepresentation() {
		ItemDefinitionOptionValueEntity optionValueEntity = ItemDefinitionOptionValueEntity.builder()
				.withDisplayName(DISPLAY_NAME)
				.withName(OPTION_VALUE_NAME)
				.build();
		String itemDefinitionUri = URIUtil.format(RESOURCE_SERVER_NAME, SCOPE, ENCODED_ITEM_ID);
		ResourceState<ItemDefinitionOptionValueEntity> expectedRepresentation = createExpectedRepresentation(itemDefinitionUri);

		ResourceState<ItemDefinitionOptionValueEntity> transformedRepresentation =
				transformer.transformToRepresentation(optionValueEntity, itemDefinitionUri, ENCODED_OPTION_ID, ENCODED_VALUE_ID);

		assertEquals(expectedRepresentation, transformedRepresentation);
	}

	private ResourceState<ItemDefinitionOptionValueEntity> createExpectedRepresentation(final String itemDefinitionUri) {
		String optionUri = URIUtil.format(itemDefinitionUri, Options.URI_PART, ENCODED_OPTION_ID);
		ResourceLink expectedOptionLink =
				ResourceLinkFactory.createNoRev(optionUri, ItemdefinitionsMediaTypes.ITEM_DEFINITION_OPTION.id(),
						ItemDefinitionResourceRels.OPTION_REL);

		String selfUri = URIUtil.format(optionUri, Values.URI_PART, ENCODED_VALUE_ID);
		Self expectedSelf = SelfFactory.createSelf(selfUri);

		return ResourceState.Builder
				.create(ItemDefinitionOptionValueEntity.builder()
						.withDisplayName(DISPLAY_NAME)
						.withName(OPTION_VALUE_NAME)
						.build())
				.withSelf(expectedSelf)
				.withResourceInfo(
					ResourceInfo.builder()
						.withMaxAge(ItemDefinitionResourceConstants.MAX_AGE)
						.build())
				.addingLinks(expectedOptionLink)
				.build();
	}
}
