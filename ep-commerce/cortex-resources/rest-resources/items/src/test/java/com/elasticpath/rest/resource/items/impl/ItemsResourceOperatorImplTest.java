/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.items.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static com.elasticpath.rest.test.AssertOperationResult.assertOperationResult;
import static org.mockito.BDDMockito.given;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.items.ItemEntity;
import com.elasticpath.rest.resource.items.ItemLookup;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Tests the {@link ItemsResourceOperatorImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ItemsResourceOperatorImplTest {
	private static final String ITEM_ID = "itemId";
	private static final String SCOPE = "scope";
	@Mock
	private ItemLookup itemLookup;
	@InjectMocks
	private ItemsResourceOperatorImpl itemsResourceOperator;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private ResourceOperation operation;
	@Mock
	private ResourceState<ItemEntity> itemResource;

	@Test
	public void ensureItemCanBeReadSuccessfully() {
		given(itemLookup.getItem(SCOPE, ITEM_ID)).willReturn(ExecutionResultFactory.createReadOK(itemResource));

		OperationResult result = itemsResourceOperator.processRead(SCOPE, ITEM_ID, operation);

		assertOperationResult(result)
				.resourceState(itemResource)
				.resourceStatus(ResourceStatus.READ_OK);
	}

	@Test
	public void ensureNotFoundReturnedWhenItemNotFound() {
		given(itemLookup.getItem(SCOPE, ITEM_ID))
				.willReturn(ExecutionResultFactory.<ResourceState<ItemEntity>>createNotFound());

		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		itemsResourceOperator.processRead(SCOPE, ITEM_ID, operation);
	}
}
