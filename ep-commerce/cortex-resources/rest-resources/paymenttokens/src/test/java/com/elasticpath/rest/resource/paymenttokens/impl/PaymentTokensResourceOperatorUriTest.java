/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymenttokens.impl;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
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
import com.elasticpath.rest.resource.dispatch.operator.annotation.Form;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Tests {@link PaymentTokensResourceOperatorImpl} resource operation uris.
 */
@SuppressWarnings("PMD.TestClassWithoutTestCases")
@RunWith(MockitoJUnitRunner.class)
public class PaymentTokensResourceOperatorUriTest extends AbstractResourceOperatorUriTest {
	private static final String PAYMENT_TOKENS_RESOURCE_NAME = "paymenttokens";
	public static final String SCOPE = "scope";

	@Spy
	private final PaymentTokensResourceOperatorImpl resourceOperator = new PaymentTokensResourceOperatorImpl(null, null, null, null, null, null);

	@Mock
	private OperationResult mockOperationResult;


	@Test
	public void ensureProcessCreatePaymentTokenForProfileMethodIsMatchedCorrectly() {
		String uri = URIUtil.format(PAYMENT_TOKENS_RESOURCE_NAME, SCOPE);
		ResourceOperation createOperation = TestResourceOperationFactory.createCreate(uri, null);
		doReturn(mockOperationResult)
				.when(resourceOperator)
				.processCreatePaymentTokenForProfile(SCOPE, createOperation);

		dispatchMethod(createOperation, resourceOperator);

		verify(resourceOperator, times(1)).processCreatePaymentTokenForProfile(SCOPE, createOperation);
	}

	@Test
	public void ensureProcessReadPaymentTokenFormForProfileIsMatchedCorrectly() {
		String uri = URIUtil.format(PAYMENT_TOKENS_RESOURCE_NAME, SCOPE, Form.URI_PART);
		ResourceOperation readOperation = TestResourceOperationFactory.createRead(uri);
		doReturn(mockOperationResult)
				.when(resourceOperator)
				.processReadPaymentTokenFormForProfile(SCOPE, readOperation);

		dispatchMethod(readOperation, resourceOperator);

		verify(resourceOperator, times(1)).processReadPaymentTokenFormForProfile(SCOPE, readOperation);
	}
}
