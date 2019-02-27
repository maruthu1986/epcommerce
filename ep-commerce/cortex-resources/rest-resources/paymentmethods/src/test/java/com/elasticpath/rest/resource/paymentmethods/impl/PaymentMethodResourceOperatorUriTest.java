/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.impl;

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
import com.elasticpath.rest.resource.dispatch.operator.annotation.Default;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Selector;
import com.elasticpath.rest.resource.paymentmethods.alias.impl.DefaultPaymentMethodResourceOperatorImpl;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Tests Payment Methods Resource URIs.
 */
@RunWith(MockitoJUnitRunner.class)
public final class PaymentMethodResourceOperatorUriTest extends AbstractResourceOperatorUriTest {

	private static final String PAYMENTMETHODS_RESOURCE_NAME = "paymentmethods";
	private static final String ORDERS_RESOURCE_NAME = "orders";
	private static final String SCOPE = "mobee";
	private static final String ORDER_ID = "abcdefghijklmnopqrstuvwxy7=";
	private static final String PAYMENTMETHOD_ID = "typkj2qo4whj5fihed3mganc6dh7ufak=";

	@Spy
	private final PaymentMethodResourceOperatorImpl resourceOperator =
			new PaymentMethodResourceOperatorImpl(null, null, null, null, null);
	@Spy
	private final DefaultPaymentMethodResourceOperatorImpl aliasOperator = new DefaultPaymentMethodResourceOperatorImpl(null, null, null);
	@Mock
	private OperationResult mockOperationResult;

	/**
	 * Tests READ of default payment method.
	 */
	@Test
	public void testProcessReadDefault() {
		String uri = URIUtil.format(PAYMENTMETHODS_RESOURCE_NAME, SCOPE, Default.URI_PART);
		ResourceOperation operation = TestResourceOperationFactory.createRead(uri);
		doReturn(mockOperationResult)
				.when(aliasOperator)
				.processReadDefault(SCOPE, operation);

		dispatch(operation);

		verify(aliasOperator, times(1)).processReadDefault(SCOPE, operation);
	}

	/**
	 * Tests READ of specific payment method.
	 */
	@Test
	public void testProcessReadPaymentMethod() {
		String uri = URIUtil.format(PAYMENTMETHODS_RESOURCE_NAME, SCOPE, PAYMENTMETHOD_ID);
		ResourceOperation operation = TestResourceOperationFactory.createRead(uri);
		doReturn(mockOperationResult)
				.when(resourceOperator)
				.processReadPaymentMethod(SCOPE, PAYMENTMETHOD_ID, operation);

		dispatch(operation);

		verify(resourceOperator, times(1)).processReadPaymentMethod(SCOPE, PAYMENTMETHOD_ID, operation);
	}

	@Test
	public void ensureProcessDeletePaymentMethodIsMatchedCorrectly() {
		String uri = URIUtil.format(PAYMENTMETHODS_RESOURCE_NAME, SCOPE, PAYMENTMETHOD_ID);
		ResourceOperation operation = TestResourceOperationFactory.createDelete(uri);
		doReturn(mockOperationResult)
				.when(resourceOperator)
				.processDeletePaymentMethod(PAYMENTMETHOD_ID, operation);

		dispatch(operation);

		verify(resourceOperator, times(1)).processDeletePaymentMethod(PAYMENTMETHOD_ID, operation);
	}


	/**
	 * Tests READ on paymentmethods list URI.
	 */
	@Test
	public void testProcessReadPaymentMethodsList() {
		String uri = URIUtil.format(PAYMENTMETHODS_RESOURCE_NAME, SCOPE);
		ResourceOperation operation = TestResourceOperationFactory.createRead(uri);
		doReturn(mockOperationResult)
				.when(resourceOperator)
				.processReadPaymentMethodsList(SCOPE, operation);

		dispatch(operation);

		verify(resourceOperator, times(1)).processReadPaymentMethodsList(SCOPE, operation);
	}


	/**
	 * Tests READ to view payment method choice for an order.
	 */
	@Test
	public void testProcessReadDefaultPaymentMethodChoice() {
		String orderUriPart = URIUtil.formatRelative(SCOPE, ORDER_ID);
		String orderUri = URIUtil.formatRelative(ORDERS_RESOURCE_NAME, orderUriPart);
		String paymentMethodUri = URIUtil.formatRelative(SCOPE, Default.URI_PART);
		String uri = URIUtil.format(PAYMENTMETHODS_RESOURCE_NAME, paymentMethodUri, Selector.URI_PART, orderUri);
		ResourceOperation operation = TestResourceOperationFactory.createRead(uri, null);
		doReturn(mockOperationResult)
				.when(aliasOperator)
				.processReadDefaultPaymentMethodChoice(SCOPE, orderUri, operation);

		dispatch(operation);

		verify(aliasOperator, times(1)).processReadDefaultPaymentMethodChoice(SCOPE, orderUri, operation);
	}



	/**
	 * Dispatch a method to all operators under test for this resource.
	 *
	 * @param operation the operation to fire
	 */
	private void dispatch(final ResourceOperation operation) {
		dispatchMethod(operation, resourceOperator, aliasOperator);
	}
}
