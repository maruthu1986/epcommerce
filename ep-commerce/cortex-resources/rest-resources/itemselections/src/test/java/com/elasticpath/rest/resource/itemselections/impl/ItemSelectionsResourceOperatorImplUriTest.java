/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.itemselections.impl;

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
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.dispatch.operator.AbstractResourceOperatorUriTest;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Options;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Selector;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Values;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Tests URI-related annotations on {@link ItemSelectionsResourceOperatorImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class ItemSelectionsResourceOperatorImplUriTest extends AbstractResourceOperatorUriTest {

	private static final String RESOURCE_SERVER_NAME = "itemselections";
	private static final String SCOPE = "scope";
	private static final String ITEM_ID = Base32Util.encode("itemId");
	private static final String OPTION_ID = Base32Util.encode("optionId");
	private static final String VALUE_ID = Base32Util.encode("valueId");

	@Spy
	private final ItemSelectionsResourceOperatorImpl resourceOperator =
			new ItemSelectionsResourceOperatorImpl(null, null, null);

	@Mock
	private OperationResult mockOperationResult;

	/**
	 * Tests {@link ItemSelectionsResourceOperatorImpl#processReadOptionValueChoice(String, String, String, String, ResourceOperation)}.
	 */
	@Test
	public void testProcessReadOptionValueChoice() {
		String uri = URIUtil.format(RESOURCE_SERVER_NAME, SCOPE, ITEM_ID, Options.URI_PART, OPTION_ID, Values.URI_PART, VALUE_ID, Selector.URI_PART);
		ResourceOperation operation = TestResourceOperationFactory.createRead(uri);
		doReturn(mockOperationResult)
				.when(resourceOperator)
				.processReadOptionValueChoice(SCOPE, ITEM_ID, OPTION_ID, VALUE_ID, operation);

		dispatch(operation);

		verify(resourceOperator).processReadOptionValueChoice(SCOPE, ITEM_ID, OPTION_ID, VALUE_ID, operation);
	}

	/**
	 * Tests {@link ItemSelectionsResourceOperatorImpl#processReadOptionValueSelector(String, String, String, ResourceOperation)}.
	 */
	@Test
	public void testProcessReadOptionValueSelector() {
		String uri = URIUtil.format(RESOURCE_SERVER_NAME, SCOPE, ITEM_ID, Options.URI_PART, OPTION_ID, Selector.URI_PART);
		ResourceOperation operation = TestResourceOperationFactory.createRead(uri);
		doReturn(mockOperationResult)
				.when(resourceOperator)
				.processReadOptionValueSelector(SCOPE, ITEM_ID, OPTION_ID, operation);

		dispatch(operation);

		verify(resourceOperator).processReadOptionValueSelector(SCOPE, ITEM_ID, OPTION_ID, operation);
	}

	/**
	 * Tests {@link ItemSelectionsResourceOperatorImpl#processSelectChoice(String, String, String, String, ResourceOperation).
	 */
	@Test
	public void testProcessPostSelectChoice() {
		String uri = URIUtil.format(RESOURCE_SERVER_NAME, SCOPE, ITEM_ID, Options.URI_PART, OPTION_ID, Values.URI_PART, VALUE_ID, Selector.URI_PART);
		ResourceOperation operation = TestResourceOperationFactory.createCreate(uri, null);
		doReturn(mockOperationResult)
				.when(resourceOperator)
				.processSelectChoice(SCOPE, ITEM_ID, OPTION_ID, VALUE_ID, operation);

		dispatch(operation);

		verify(resourceOperator).processSelectChoice(SCOPE, ITEM_ID, OPTION_ID, VALUE_ID, operation);
	}

	private void dispatch(final ResourceOperation operation) {
		dispatchMethod(operation, resourceOperator);
	}
}
