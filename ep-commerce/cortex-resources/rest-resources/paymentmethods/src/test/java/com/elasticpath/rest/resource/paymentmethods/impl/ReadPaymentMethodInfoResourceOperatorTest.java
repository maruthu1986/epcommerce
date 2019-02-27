/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.TestResourceOperationFactory;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.common.selector.SelectorRepresentationRels;
import com.elasticpath.rest.definition.controls.ControlsMediaTypes;
import com.elasticpath.rest.definition.controls.InfoEntity;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.definition.orders.OrdersMediaTypes;
import com.elasticpath.rest.resource.commons.paymentmethod.rel.PaymentMethodCommonsConstants;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Selector;
import com.elasticpath.rest.resource.paymentmethods.PaymentMethodLookup;
import com.elasticpath.rest.resource.paymentmethods.constant.PaymentMethodsConstants;
import com.elasticpath.rest.resource.paymentmethods.rel.PaymentMethodRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.uri.PaymentMethodInfoUriBuilder;
import com.elasticpath.rest.schema.uri.PaymentMethodInfoUriBuilderFactory;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Tests {@link ReadPaymentMethodInfoResourceOperator}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class ReadPaymentMethodInfoResourceOperatorTest {

	private static final String RESOURCE_SERVER_NAME = "RESOURCE_SERVER_NAME";
	private static final String SCOPE = "scope";
	private static final String ORDER_URI = "/orderUri";
	private static final String ORDER_ID = "orderId";
	private static final String PAYMENTMETHODS = "paymentmethods";
	private static final String PAYMENT_METHOD_SELECTOR_URI = URIUtil.format(PAYMENTMETHODS, Selector.URI_PART, ORDER_URI);
	private static final String PAYMENT_METHOD_INFO_URI = "/uri";
	private static final String SELECTED_PAYMENT_METHOD_URI = "/selectedPaymentMethodUri";
	private static final String TEST_REPRESENTATION_TYPE = "testRepresentationType";
	private static final ResourceState<OrderEntity> ORDER = ResourceState.Builder
			.create(OrderEntity.builder()
					.withOrderId(ORDER_ID)
					.build())
			.withSelf(SelfFactory.createSelf(ORDER_URI, OrdersMediaTypes.ORDER.id()))
			.withScope(SCOPE)
			.build();
	private static final ResourceOperation READ = TestResourceOperationFactory.createRead(ORDER_URI);
	@Mock
	private PaymentMethodLookup paymentMethodLookup;

	@Mock
	private PaymentMethodInfoUriBuilderFactory paymentMethodInfoUriBuilderFactory;

	@Mock
	private PaymentMethodInfoUriBuilder paymentMethodInfoUriBuilder;

	private ReadPaymentMethodInfoResourceOperator readPaymentMethodInfoResourceOperator;

	/**
	 * Set up the test environment.
	 */
	@Before
	public void setUp() {
		readPaymentMethodInfoResourceOperator =
				new ReadPaymentMethodInfoResourceOperator(PAYMENTMETHODS, paymentMethodLookup, paymentMethodInfoUriBuilderFactory);
		when(paymentMethodInfoUriBuilderFactory.get()).thenReturn(paymentMethodInfoUriBuilder);
		when(paymentMethodInfoUriBuilder.setSourceUri(any(String.class))).thenReturn(paymentMethodInfoUriBuilder);
		when(paymentMethodInfoUriBuilder.build()).thenReturn(PAYMENT_METHOD_INFO_URI);
	}

	/**
	 * Test read payment method info representation.
	 */
	@Test
	public void testReadPaymentMethodInfoRep() {
		ResourceLink selectedPaymentMethodLink = getTestSelectedPaymentMethodLink();
		shouldFindSelectedPaymentMethodLinkForOrderWithResult(ExecutionResultFactory.createReadOK(selectedPaymentMethodLink));

		OperationResult result = readPaymentMethodInfoResourceOperator
				.processReadPaymentMethodInfoForOrder(RESOURCE_SERVER_NAME, ORDER, READ);

		assertTrue(result.isSuccessful());
		assertEquals(createInfoRepresentation(selectedPaymentMethodLink), result.getResourceState());
	}

	/**
	 * Test read payment method info representation with no payment method selected.
	 */
	@Test
	public void testReadPaymentMethodInfoRepresentationWithNoPaymentMethodSelected() {
		shouldFindSelectedPaymentMethodLinkForOrderWithResult(ExecutionResultFactory.<ResourceLink>createNotFound("notFound"));

		OperationResult result = readPaymentMethodInfoResourceOperator
				.processReadPaymentMethodInfoForOrder(RESOURCE_SERVER_NAME, ORDER, READ);

		assertTrue(result.isSuccessful());
		assertEquals(createInfoRepresentation(null), result.getResourceState());
	}

	/**
	 * Test read order delivery method links rep with an unhandled resource status error.
	 */
	@Test
	public void testReadOrderDeliveryMethodLinksRepWithUnhandledResourceStatusError() {
		shouldFindSelectedPaymentMethodLinkForOrderWithResult(ExecutionResultFactory.<ResourceLink>createServerError("server error"));

		OperationResult result = readPaymentMethodInfoResourceOperator
				.processReadPaymentMethodInfoForOrder(RESOURCE_SERVER_NAME, ORDER, READ);

		assertTrue(result.isSuccessful());
		assertEquals(createInfoRepresentation(null), result.getResourceState());
	}

	@Test
	public void ensurePaymentMethodInfoUriBuilderFactoryGetIsInvoked() {
		readPaymentMethodInfoResourceOperator.createPaymentMethodInfoUri("");
		verify(paymentMethodInfoUriBuilderFactory, times(1)).get();
	}

	@Test
	public void ensurePaymentMethodInfoUriBuilderSetUriIsInvoked() {
		readPaymentMethodInfoResourceOperator.createPaymentMethodInfoUri("order/uri");
		verify(paymentMethodInfoUriBuilder, times(1)).setSourceUri("order/uri");
	}

	@Test
	public void ensurePaymentMethodInfoUriBuilderBuildIsInvoked() {
		readPaymentMethodInfoResourceOperator.createPaymentMethodInfoUri("");
		verify(paymentMethodInfoUriBuilder, times(1)).build();
	}

	@Test
	public void ensureUriReturnedFromUriBuilderBuildIsReturnedFromCreatePaymentMethodUri() {
		String paymentMethodInfoUri = readPaymentMethodInfoResourceOperator.createPaymentMethodInfoUri("");
		assertEquals("payment method info URI", "/uri", paymentMethodInfoUri);
	}


	private void shouldFindSelectedPaymentMethodLinkForOrderWithResult(final ExecutionResult<ResourceLink> result) {
		when(paymentMethodLookup.getSelectedPaymentMethodLinkForOrder(SCOPE, ORDER_ID)).thenReturn(result);
	}

	private ResourceState<InfoEntity> createInfoRepresentation(final ResourceLink selectedPaymentMethodLink) {

		Collection<ResourceLink> links = new ArrayList<>();

		if (selectedPaymentMethodLink != null) {
			links.add(selectedPaymentMethodLink);
		}

		ResourceLink selectorLink = ResourceLinkFactory.create(PAYMENT_METHOD_SELECTOR_URI,
				ControlsMediaTypes.SELECTOR.id(),
				SelectorRepresentationRels.SELECTOR,
				PaymentMethodRels.PAYMENTMETHODINFO_REV);
		links.add(selectorLink);

		ResourceLink orderLink = ResourceLinkFactory.create(ORDER_URI,
				OrdersMediaTypes.ORDER.id(),
				PaymentMethodCommonsConstants.ORDER_REL,
				PaymentMethodRels.PAYMENTMETHODINFO_REV);
		links.add(orderLink);

		return ResourceState.Builder
				.create(InfoEntity.builder()
						.withName(PaymentMethodsConstants.PAYMENT_METHOD_INFO_NAME)
						.withInfoId(ORDER_ID)
						.build())
				.withScope(SCOPE)
				.addingLinks(links)
				.withSelf(SelfFactory.createSelf(PAYMENT_METHOD_INFO_URI))
				.build();
	}


	private ResourceLink getTestSelectedPaymentMethodLink() {
		return ResourceLinkFactory.createNoRev(SELECTED_PAYMENT_METHOD_URI, TEST_REPRESENTATION_TYPE, PaymentMethodRels.PAYMENTMETHOD_REL);
	}
}
