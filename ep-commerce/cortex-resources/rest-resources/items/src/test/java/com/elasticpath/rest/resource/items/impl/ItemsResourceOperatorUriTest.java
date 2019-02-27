/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.items.impl;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.TestResourceOperationFactory;
import com.elasticpath.rest.resource.dispatch.operator.AbstractResourceOperatorUriTest;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Tests URI-related annotations on {@link ItemsResourceOperatorImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class ItemsResourceOperatorUriTest extends AbstractResourceOperatorUriTest {

	private static final String ITEMS = "items";
	private static final String SCOPE = "rockjam";
	private static final String ITEM_ID = "itemid=";

	@Spy
	private final ItemsResourceOperatorImpl resourceOperator = new ItemsResourceOperatorImpl(null);
	@Mock
	private OperationResult mockOperationResult;

	/**
	 * Tests [{@link ItemsResourceOperatorImpl#processRead(String, String, ResourceOperation)}.
	 */
	@Test
	public void testProcessRead() {
		ResourceOperation operation = TestResourceOperationFactory.createRead(URIUtil.format(ITEMS, SCOPE, ITEM_ID));
		doReturn(mockOperationResult)
				.when(resourceOperator)
				.processRead(SCOPE, ITEM_ID, operation);

		dispatchMethod(operation, resourceOperator);

		verify(resourceOperator).processRead(SCOPE, ITEM_ID, operation);
	}
}
