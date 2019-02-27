/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemdefinitions.impl;

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
 * Test class for {@link ItemDefinitionsResourceOperatorImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class ItemDefinitionsResourceOperatorImplTest extends AbstractResourceOperatorUriTest {

	private static final String RESOURCE_NAME = "itemdefinitions";
	private static final String SCOPE = "scope";
	private static final String ITEM_ID = "mnxw4ztjmaaaaa52xeylunfxw4sle=";

	@Spy
	private final ItemDefinitionsResourceOperatorImpl itemDefinitionsResourceOperator =
			new ItemDefinitionsResourceOperatorImpl(null);

	@Mock
	private OperationResult mockOperationResult;

	/**
	 * Test read item definition.
	 */
	@Test
	public void testReadItemDefinition() {
		String uri = URIUtil.format(RESOURCE_NAME, SCOPE, ITEM_ID);
		ResourceOperation operation = TestResourceOperationFactory.createRead(uri);
		doReturn(mockOperationResult)
				.when(itemDefinitionsResourceOperator)
				.processRead(SCOPE, ITEM_ID, operation);

		dispatch(operation);

		verify(itemDefinitionsResourceOperator).processRead(SCOPE, ITEM_ID, operation);
	}

	private void dispatch(final ResourceOperation operation) {
		dispatchMethod(operation, itemDefinitionsResourceOperator);
	}
}
