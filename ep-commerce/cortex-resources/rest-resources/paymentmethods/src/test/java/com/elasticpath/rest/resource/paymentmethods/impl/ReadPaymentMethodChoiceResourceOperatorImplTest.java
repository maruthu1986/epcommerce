/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.impl;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.commons.lang3.StringUtils;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.rest.Operation;
import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.TestResourceOperationContextFactory;
import com.elasticpath.rest.TestResourceOperationFactory;
import com.elasticpath.rest.chain.BrokenChainException;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.command.read.ReadResourceCommand;
import com.elasticpath.rest.command.read.ReadResourceCommandBuilderProvider;
import com.elasticpath.rest.common.selector.SelectorRepresentationRels;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.controls.ControlsMediaTypes;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.definition.paymentmethods.PaymentMethodEntity;
import com.elasticpath.rest.definition.paymentmethods.PaymentmethodsMediaTypes;
import com.elasticpath.rest.identity.Subject;
import com.elasticpath.rest.identity.TestSubjectFactory;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Selector;
import com.elasticpath.rest.resource.paymentmethods.PaymentMethodLookup;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Test class for {@link com.elasticpath.rest.resource.paymentmethods.impl.ReadPaymentMethodChoiceResourceOperator}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class ReadPaymentMethodChoiceResourceOperatorImplTest {

	private static final String SCOPE = "SCOPE";
	private static final String USER_ID = "userId";
	private static final String ORDER_URI = "/orderuri";
	private static final String RESOURCE_SERVER_NAME = "RESOURCE_SERVER_NAME";
	private static final String ORDER_ID = "ORDER_ID";
	private static final String PAYMENT_METHOD_ID = "PAYMENT_METHOD_ID";
	private static final String PAYMENT_METHOD_URI = URIUtil.format(RESOURCE_SERVER_NAME, SCOPE, PAYMENT_METHOD_ID);;
	private static final String DIFFERENT_PAYMENT_METHOD_ID = "DIFFERENT_PAYMENT_METHOD_ID";
	private static final String RESOURCE_OPERATION_URI = URIUtil.format(PAYMENT_METHOD_URI, Selector.PATH_PART, ORDER_URI);
	private static final ResourceState<OrderEntity> ORDER = ResourceState.Builder
			.create(OrderEntity.builder()
					.withOrderId(ORDER_ID)
					.build())
			.withSelf(SelfFactory.createSelf(ORDER_URI))
			.build();
	private static final ResourceOperation READ = TestResourceOperationFactory.createRead(ORDER_URI);

	private static final Subject SUBJECT = TestSubjectFactory.createWithScopeAndUserId(SCOPE, USER_ID);
	private static final ResourceOperationContext OPERATION_CONTEXT = TestResourceOperationContextFactory.create(Operation.READ,
			RESOURCE_OPERATION_URI,
			null, SUBJECT);

	@Mock
	private ReadResourceCommand readResourceCommand;
	@Mock
	private PaymentMethodLookup paymentMethodLookup;
	@Mock
	private ReadResourceCommand.Builder readResourceCommandBuilder;
	@Mock
	private ReadResourceCommandBuilderProvider readResourceCommandBuilderProvider;
	private ReadPaymentMethodChoiceResourceOperator readPaymentMethodChoiceResourceOperator;

	@Before
	public void setUp() {
		when(readResourceCommandBuilderProvider.get()).thenReturn(readResourceCommandBuilder);
		readPaymentMethodChoiceResourceOperator = new ReadPaymentMethodChoiceResourceOperator(RESOURCE_SERVER_NAME, OPERATION_CONTEXT,
				paymentMethodLookup, readResourceCommandBuilderProvider);
	}

	/**
	 * Test read payment method choice.
	 */
	@Test
	public void testReadPaymentMethodChoice() {
		when(readResourceCommand.execute())
				.thenReturn(ExecutionResultFactory.<ResourceState<?>>createReadOK(createPaymentMethodRepresentation()));

		shouldHandlePaymentMethodRepresentation();
		shouldFindSelectedPaymentForOrderWithResult(ExecutionResultFactory.createReadOK(DIFFERENT_PAYMENT_METHOD_ID));

		OperationResult result = readPaymentMethodChoiceResourceOperator
				.processReadPaymentMethodChoice(RESOURCE_SERVER_NAME, SCOPE, PAYMENT_METHOD_ID, ORDER, READ);

		assertTrue(result.isSuccessful());
		assertEquals(createExpectedChoiceRepresentation(false), result.getResourceState());
	}

	/**
	 * Test read payment method choice when order not found.
	 */
	@Test(expected = BrokenChainException.class)
	public void testReadPaymentMethodChoiceWhenOrderNotFound() {
		shouldHandlePaymentMethodRepresentation();
		shouldExecuteReadResourceCommandWithResult(ExecutionResultFactory.<ResourceState<?>>createNotFound(StringUtils.EMPTY));

		readPaymentMethodChoiceResourceOperator
				.processReadPaymentMethodChoice(RESOURCE_SERVER_NAME, SCOPE, PAYMENT_METHOD_ID, ORDER, READ);
	}

	/**
	 * Test read payment method choice with different selected payment ID.
	 */
	@Test
	public void testReadPaymentMethodChoiceWithSameSelectedPaymentId() {
		when(readResourceCommand.execute())
				.thenReturn(ExecutionResultFactory.<ResourceState<?>>createReadOK(createPaymentMethodRepresentation()));

		shouldHandlePaymentMethodRepresentation();
		shouldFindSelectedPaymentForOrderWithResult(ExecutionResultFactory.createReadOK(PAYMENT_METHOD_ID));

		OperationResult result = readPaymentMethodChoiceResourceOperator
				.processReadPaymentMethodChoice(RESOURCE_SERVER_NAME, SCOPE, PAYMENT_METHOD_ID, ORDER, READ);

		assertTrue(result.isSuccessful());
		assertEquals(createExpectedChoiceRepresentation(true), result.getResourceState());
		assertThat("The select action link should not be generated.",
				result.getResourceState().getLinks(),
				not(hasItem(createSelectActionLink())));
	}

	/**
	 * Test read payment method choice when selected payment failure.
	 */
	@Test
	public void testReadPaymentMethodChoiceWhenSelectedPaymentFailure() {
		when(readResourceCommand.execute())
				.thenReturn(ExecutionResultFactory.<ResourceState<?>>createReadOK(createPaymentMethodRepresentation()));

		shouldHandlePaymentMethodRepresentation();
		shouldFindSelectedPaymentForOrderWithResult(ExecutionResultFactory.<String>createNotFound(StringUtils.EMPTY));

		OperationResult result = readPaymentMethodChoiceResourceOperator
				.processReadPaymentMethodChoice(RESOURCE_SERVER_NAME, SCOPE, PAYMENT_METHOD_ID, ORDER, READ);

		assertTrue(result.isSuccessful());
		assertEquals(createExpectedChoiceRepresentation(false), result.getResourceState());
	}


	/**
	 * Test read payment method choice on unhandled payment method error.
	 */
	@Test(expected = BrokenChainException.class)
	public void testReadPaymentMethodChoiceOnUnhandledPaymentMethodError() {
		when(readResourceCommand.execute())
				.thenReturn(ExecutionResultFactory.<ResourceState<?>>createReadOK(createPaymentMethodRepresentation()));

		shouldHandlePaymentMethodRepresentation();
		shouldFindSelectedPaymentForOrderWithResult(ExecutionResultFactory.<String>createServerError("server error"));

		readPaymentMethodChoiceResourceOperator
				.processReadPaymentMethodChoice(RESOURCE_SERVER_NAME, SCOPE, PAYMENT_METHOD_ID, ORDER, READ);
	}

	private ResourceLink createSelectActionLink() {
		return ResourceLinkFactory.createUriRel(RESOURCE_OPERATION_URI, SelectorRepresentationRels.SELECT_ACTION);
	}

	private void shouldHandlePaymentMethodRepresentation() {
		when(readResourceCommandBuilder.setReadLinks(false)).thenReturn(readResourceCommandBuilder);
		when(readResourceCommandBuilder.setResourceUri(PAYMENT_METHOD_URI)).thenReturn(readResourceCommandBuilder);
		when(readResourceCommandBuilder.build()).thenReturn(readResourceCommand);
	}

	private void shouldExecuteReadResourceCommandWithResult(final ExecutionResult<ResourceState<?>> result) {
		when(readResourceCommand.execute()).thenReturn(result);
	}

	private void shouldFindSelectedPaymentForOrderWithResult(final ExecutionResult<String> selectedPaymentResult) {
		when(paymentMethodLookup.findChosenPaymentMethodIdForOrder(SCOPE, USER_ID, ORDER_ID)).thenReturn(selectedPaymentResult);
	}


	private ResourceState<LinksEntity> createExpectedChoiceRepresentation(final boolean isPaymentMethodSelected) {
		Collection<ResourceLink> links = new ArrayList<>();
		ResourceLink descriptionLink = ResourceLinkFactory.createNoRev(PAYMENT_METHOD_URI,
				PaymentmethodsMediaTypes.PAYMENT_TOKEN.id(),
				SelectorRepresentationRels.DESCRIPTION);
		links.add(descriptionLink);

		String selectorUri = URIUtil.format(RESOURCE_SERVER_NAME, Selector.URI_PART, ORDER_URI);
		ResourceLink selectorLink = ResourceLinkFactory.createNoRev(selectorUri,
				ControlsMediaTypes.SELECTOR.id(),
				SelectorRepresentationRels.SELECTOR);
		links.add(selectorLink);

		String selectActionUri = URIUtil.format(URIUtil.format(PAYMENT_METHOD_URI, Selector.URI_PART, ORDER_URI));

		if (!isPaymentMethodSelected) {
			ResourceLink selectActionLink = ResourceLinkFactory.createUriRel(selectActionUri,
					SelectorRepresentationRels.SELECT_ACTION);
			links.add(selectActionLink);
		}

		Self self = SelfFactory.createSelf(selectActionUri);

		return ResourceState.Builder.create(LinksEntity.builder().build())
				.withSelf(self)
				.withLinks(links)
				.build();
	}

	private ResourceState<?> createPaymentMethodRepresentation() {
		Self self = SelfFactory.createSelf(PAYMENT_METHOD_URI, PaymentmethodsMediaTypes.PAYMENT_TOKEN.id());
		return ResourceState.Builder
				.create(PaymentMethodEntity.builder().withPaymentMethodId(PAYMENT_METHOD_ID).build())
				.withSelf(self)
				.build();
	}
}
