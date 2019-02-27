/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemdefinitions.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.apache.commons.lang3.StringUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionEntity;
import com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionOptionEntity;
import com.elasticpath.rest.definition.itemdefinitions.ItemDefinitionOptionValueEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.itemdefinitions.integration.ItemDefinitionLookupStrategy;
import com.elasticpath.rest.resource.itemdefinitions.options.integration.ItemDefinitionOptionLookupStrategy;
import com.elasticpath.rest.resource.itemdefinitions.transform.ItemDefinitionOptionTransformer;
import com.elasticpath.rest.resource.itemdefinitions.transform.ItemDefinitionOptionValueTransformer;
import com.elasticpath.rest.resource.itemdefinitions.transform.ItemDefinitionTransformer;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.uri.URIUtil;
import com.elasticpath.rest.util.collection.CollectionUtil;


/**
 * Tests the {@link ItemDefinitionLookupImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class ItemDefinitionLookupImplTest {

	private static final String TEST_COMPONENT_ID = "test_component_id";
	private static final String ITEMDEFINITION_URI = "/itemdefinitionuri";
	private static final String DECODED_OPTION_ID = "option_id";
	private static final String OPTION_ID = Base32Util.encode(DECODED_OPTION_ID);
	private static final String DECODED_VALUE_ID = "value ID";
	private static final String VALUE_ID = Base32Util.encode(DECODED_VALUE_ID);
	private static final String RESOURCE_SERVER_NAME = "itemdefinitions";
	private static final String TEST_SCOPE = "testScope";
	private static final String TEST_ITEM_ID = "testItemId";
	private static final String TEST_OPTION_ID = "testOptionId";
	private static final String TEST_DECODED_OPTION_ID = Base32Util.decode(TEST_OPTION_ID);

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private ItemDefinitionTransformer mockItemDefinitionTransformer;
	@Mock
	private ItemDefinitionOptionTransformer mockItemDefinitionOptionTransformer;
	@Mock
	private ItemDefinitionOptionValueTransformer mockOptionValueTransformer;
	@Mock
	private ItemDefinitionLookupStrategy mockItemDefinitionLookupStrategy;
	@Mock
	private ItemDefinitionOptionLookupStrategy mockItemDefinitionOptionLookupStrategy;

	@InjectMocks
	private ItemDefinitionLookupImpl itemDefinitionsLookup;


	/**
	 * Test find by item id.
	 */
	@Test
	public void testFindByItemId() {
		ResourceState<ItemDefinitionEntity> testRepresentation = ResourceState.Builder
				.create(ItemDefinitionEntity.builder().build()).build();
		ItemDefinitionEntity itemDefinitionEntity = ItemDefinitionEntity.builder()
				.build();

		when(mockItemDefinitionLookupStrategy.find(TEST_SCOPE, TEST_ITEM_ID))
				.thenReturn(ExecutionResultFactory.createReadOK(itemDefinitionEntity));
		when(mockItemDefinitionOptionLookupStrategy.findOptionIds(TEST_SCOPE, TEST_ITEM_ID))
				.thenReturn(ExecutionResultFactory.<Collection<String>>createNotFound(StringUtils.EMPTY));
		when(mockItemDefinitionTransformer.transformToRepresentation(itemDefinitionEntity, TEST_SCOPE, false))
				.thenReturn(testRepresentation);

		ExecutionResult<ResourceState<ItemDefinitionEntity>> result = itemDefinitionsLookup.findByItemId(TEST_SCOPE, TEST_ITEM_ID);

		assertTrue(result.isSuccessful());
		assertEquals(testRepresentation, result.getData());
	}

	/**
	 * Test find by item id with unsuccessful result.
	 */
	@Test
	public void testFindByItemIdWithUnsuccessfulResult() {
		when(mockItemDefinitionLookupStrategy.find(TEST_SCOPE, TEST_ITEM_ID))
				.thenReturn(ExecutionResultFactory.<ItemDefinitionEntity>createNotFound(null));

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		itemDefinitionsLookup.findByItemId(TEST_SCOPE, TEST_ITEM_ID);
	}

	/**
	 * Test find option ids for item definition.
	 */
	@Test
	public void testFindOptionIdsForItemDefinition() {
		Collection<String> optionIds = Collections.singleton(DECODED_OPTION_ID);

		when(mockItemDefinitionOptionLookupStrategy.findOptionIds(TEST_SCOPE, TEST_ITEM_ID))
				.thenReturn(ExecutionResultFactory.createReadOK(optionIds));

		ExecutionResult<Collection<String>> result = itemDefinitionsLookup.findOptionIdsForItem(TEST_SCOPE, TEST_ITEM_ID);

		String expectedOptionId = OPTION_ID;

		assertTrue(result.isSuccessful());
		assertTrue(CollectionUtil.containsOnly(Arrays.asList(expectedOptionId), result.getData()));
	}

	/**
	 * Test find option ids for item definition on failure.
	 */
	@Test
	public void testFindOptionIdsForItemDefinitionOnFailure() {

		when(mockItemDefinitionOptionLookupStrategy.findOptionIds(TEST_SCOPE, TEST_ITEM_ID))
				.thenReturn(ExecutionResultFactory.<Collection<String>>createNotFound("not found"));

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		itemDefinitionsLookup.findOptionIdsForItem(TEST_SCOPE, TEST_ITEM_ID);
	}

	/**
	 * Test find option for item definition.
	 */
	@Test
	public void testFindOptionForItemDefinition() {
		ItemDefinitionOptionEntity optionEntity = ItemDefinitionOptionEntity.builder().build();
		ResourceState<ItemDefinitionOptionEntity> itemDefinitionOption = ResourceState.Builder.create(optionEntity).build();

		when(mockItemDefinitionOptionLookupStrategy.findOption(TEST_SCOPE, TEST_ITEM_ID, TEST_DECODED_OPTION_ID))
				.thenReturn(ExecutionResultFactory.createReadOK(optionEntity));

		when(mockItemDefinitionOptionTransformer
				.transformToRepresentation(optionEntity, ITEMDEFINITION_URI, TEST_SCOPE, TEST_ITEM_ID, TEST_COMPONENT_ID))
				.thenReturn(itemDefinitionOption);

		ExecutionResult<ResourceState<ItemDefinitionOptionEntity>> result =
				itemDefinitionsLookup.findOption(TEST_SCOPE, ITEMDEFINITION_URI, TEST_ITEM_ID, TEST_COMPONENT_ID, TEST_OPTION_ID);

		assertTrue(result.isSuccessful());
		assertEquals(itemDefinitionOption, result.getData());
	}

	/**
	 * Test find option for item definition when option not found.
	 */
	@Test
	public void testFindOptionForItemDefinitionWhenOptionNotFound() {

		when(mockItemDefinitionOptionLookupStrategy.findOption(TEST_SCOPE, TEST_ITEM_ID, TEST_DECODED_OPTION_ID))
				.thenReturn(ExecutionResultFactory.<ItemDefinitionOptionEntity>createNotFound(StringUtils.EMPTY));

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		itemDefinitionsLookup.findOption(TEST_SCOPE, ITEMDEFINITION_URI, TEST_ITEM_ID, TEST_COMPONENT_ID, TEST_OPTION_ID);
	}


	/**
	 * Test find option value for item.
	 */
	@Test
	public void testFindOptionValue() {
		String itemDefinitionUri = URIUtil.format(RESOURCE_SERVER_NAME, TEST_SCOPE, TEST_ITEM_ID);
		String name = "value_name";
		String displayValue = "display value";
		ItemDefinitionOptionValueEntity optionValueEntity = ItemDefinitionOptionValueEntity.builder()
				.withName(name)
				.withDisplayName(displayValue)
				.build();
		ResourceState<ItemDefinitionOptionValueEntity> expectedRepresentation = ResourceState.Builder.create(optionValueEntity).build();
		when(mockItemDefinitionOptionLookupStrategy.findOptionValue(TEST_SCOPE, TEST_ITEM_ID, DECODED_OPTION_ID, DECODED_VALUE_ID))
				.thenReturn(ExecutionResultFactory.createReadOK(optionValueEntity));
		when(mockOptionValueTransformer.transformToRepresentation(optionValueEntity, itemDefinitionUri, OPTION_ID, VALUE_ID))
				.thenReturn(expectedRepresentation);

		ExecutionResult<ResourceState<ItemDefinitionOptionValueEntity>> result =
				itemDefinitionsLookup.findOptionValueForItem(TEST_SCOPE, itemDefinitionUri, TEST_ITEM_ID, OPTION_ID, VALUE_ID);

		assertTrue(result.isSuccessful());
		assertEquals(expectedRepresentation, result.getData());
	}

	/**
	 * Test find option value for item with a lookup failure.
	 */
	@Test
	public void testFindOptionValueWithLookupFailure() {

		when(mockItemDefinitionOptionLookupStrategy.findOptionValue(TEST_SCOPE, TEST_ITEM_ID, DECODED_OPTION_ID, DECODED_VALUE_ID))
				.thenReturn(ExecutionResultFactory.<ItemDefinitionOptionValueEntity>createNotFound("Test induced lookup failure."));

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		itemDefinitionsLookup.findOptionValueForItem(TEST_SCOPE, null, TEST_ITEM_ID, OPTION_ID, VALUE_ID);
	}
}
