/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.items.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static com.elasticpath.rest.test.AssertExecutionResult.assertExecutionResult;
import static org.mockito.Mockito.when;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.items.ItemEntity;
import com.elasticpath.rest.resource.items.integration.ItemLookupStrategy;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.transform.TransformToResourceState;

/**
 * Test class for {@link ItemLookupImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class ItemLookupImplTest {

	private static final String SCOPE = "scope";
	private static final String ITEM_ID = "itemId";

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private ItemLookupStrategy mockItemLookupStrategy;

	@Mock
	private TransformToResourceState<ItemEntity, ItemEntity> mockItemTransformer;

	@InjectMocks
	private ItemLookupImpl itemLookup;
	@Mock
	private ResourceState<ItemEntity> itemRepresentation;
	@Mock
	private ItemEntity itemEntity;

	/**
	 * Tests that the representation produced by the transformer is returned when the lookup strategy is successful.
	 */
	@Test
	public void testStrategyIsSuccessful() {
		when(mockItemLookupStrategy.getItem(SCOPE, ITEM_ID))
				.thenReturn(ExecutionResultFactory.createReadOK(itemEntity));
		when(mockItemTransformer.transform(SCOPE, itemEntity))
				.thenReturn(itemRepresentation);

		ExecutionResult<ResourceState<ItemEntity>> result = itemLookup.getItem(SCOPE, ITEM_ID);

		assertExecutionResult(result).data(itemRepresentation);
	}

	/**
	 * Tests that a failure result is returned when the lookup strategy fails.
	 */
	@Test
	public void testStrategyFails() {
		when(mockItemLookupStrategy.getItem(SCOPE, ITEM_ID))
				.thenReturn(ExecutionResultFactory.<ItemEntity>createNotFound());

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		itemLookup.getItem(SCOPE, ITEM_ID);
	}
}
