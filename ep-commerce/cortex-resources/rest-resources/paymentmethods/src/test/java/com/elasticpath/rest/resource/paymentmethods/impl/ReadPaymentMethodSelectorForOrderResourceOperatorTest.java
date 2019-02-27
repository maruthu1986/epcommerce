/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.impl;

import static com.elasticpath.rest.chain.ResourceStatusMatcher.containsResourceStatus;
import static com.elasticpath.rest.test.AssertResourceLink.assertResourceLink;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.inject.Provider;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.rest.OperationResult;
import com.elasticpath.rest.ResourceOperation;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.TestResourceOperationFactory;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.command.read.ReadResourceCommand;
import com.elasticpath.rest.command.read.ReadResourceCommandBuilderProvider;
import com.elasticpath.rest.common.selector.SelectorRepresentationRels;
import com.elasticpath.rest.common.selector.SelectorResourceStateBuilder;
import com.elasticpath.rest.common.selector.SingleSelectorResourceStateBuilder;
import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.definition.controls.ControlsMediaTypes;
import com.elasticpath.rest.definition.controls.SelectorEntity;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.definition.orders.OrdersMediaTypes;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.rel.ListElementRels;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Selector;
import com.elasticpath.rest.resource.paymentmethods.PaymentMethodLookup;
import com.elasticpath.rest.resource.paymentmethods.constant.PaymentMethodsConstants;
import com.elasticpath.rest.resource.paymentmethods.rel.PaymentMethodRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.uri.PaymentMethodInfoUriBuilder;
import com.elasticpath.rest.schema.uri.PaymentMethodInfoUriBuilderFactory;
import com.elasticpath.rest.schema.uri.PaymentMethodListUriBuilder;
import com.elasticpath.rest.schema.uri.PaymentMethodListUriBuilderFactory;
import com.elasticpath.rest.uri.URIUtil;


/**
 * Contains tests for ReadPaymentMethodSelectorForOrderStrategy non-trivial methods.
 */
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("PMD.TooManyMethods")
public final class ReadPaymentMethodSelectorForOrderResourceOperatorTest {

	private static final String PAYMENT_METHOD_ID_TOKEN = "PAYMENT_METHOD_ID_TOKEN";
	private static final String TYPE_TOKEN = "TYPE_TOKEN";
	private static final String TYPE_AMERICANEXPRESS = "elasticpath.americanexpress";
	private static final String TYPE_VISA = "elasticpath.visa";
	private static final String EXPECTED_DATA = "Should contain the expected data.";
	private static final String EXPECTED_RESOURCE_STATUS = "This should have the expected resource status.";
	private static final String SUCCESSFUL_OPERATION = "This operation should be successful.";
	private static final String RESOURCE_SERVER_NAME = "RESOURCE_SERVER_NAME";
	private static final String SCOPE = "SCOPE";
	private static final String PAYMENT_METHOD_SELECTOR = "PAYMENT_METHOD_SELECTOR";
	private static final String ORDER_URI = "/orderUri";
	private static final String ORDER_ID = "ORDER_ID";
	private static final String DECODED_PAYMENT_METHOD_ID_VISA = "PAYMENT_METHOD_ID_VISA";
	private static final String PAYMENT_METHOD_ID_VISA = Base32Util.encode(DECODED_PAYMENT_METHOD_ID_VISA);
	private static final String DECODED_PAYMENT_METHOD_ID_AMEX = "PAYMENT_METHOD_ID_AMEX";
	private static final String PAYMENT_METHOD_ID_AMEX = Base32Util.encode(DECODED_PAYMENT_METHOD_ID_AMEX);
	private static final String PAYMENT_METHOD_LIST_URI = "PAYMENT_METHOD_LIST_URI";
	private static final String USER_ID = "USER_ID";
	public static final String INFO_URI = "/uri";
	private static final ResourceOperation READ = TestResourceOperationFactory.createRead(ORDER_URI);

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private ReadResourceCommandBuilderProvider readResourceCommandBuilderProvider;

	@Mock
	private PaymentMethodListUriBuilderFactory paymentMethodListUriBuilderFactory;

	@Mock
	private SelectorResourceStateBuilder selectorResourceStateBuilder;

	@Mock
	private Provider<SelectorResourceStateBuilder> selectorProvider;

	@Mock
	private PaymentMethodLookup paymentMethodLookup;

	@Mock
	private ResourceOperationContext resourceOperationContext;

	@Mock
	private PaymentMethodInfoUriBuilderFactory paymentMethodInfoUriBuilderFactory;

	@Mock
	private PaymentMethodInfoUriBuilder paymentMethodInfoUriBuilder;

	private ReadPaymentMethodSelectorForOrderResourceOperator readPaymentMethodSelectorForOrderResourceOperator;

	/**
	 * Set up the test environment.
	 */
	@Before
	public void setUp() {
		readPaymentMethodSelectorForOrderResourceOperator = new ReadPaymentMethodSelectorForOrderResourceOperator(RESOURCE_SERVER_NAME,
				readResourceCommandBuilderProvider,
				paymentMethodListUriBuilderFactory,
				paymentMethodInfoUriBuilderFactory,
				selectorProvider,
				paymentMethodLookup,
				resourceOperationContext);
		shouldReturnUserIdFromResourceOperationContext();
		when(paymentMethodInfoUriBuilderFactory.get()).thenReturn(paymentMethodInfoUriBuilder);
		when(paymentMethodInfoUriBuilder.setSourceUri(any(String.class))).thenReturn(paymentMethodInfoUriBuilder);
		when(paymentMethodInfoUriBuilder.build()).thenReturn(INFO_URI);
	}

	/**
	 * Test happy path where everything works nicely with a selected option.
	 */
	@Test
	public void testSuccessfulExecute() {
		ResourceState<OrderEntity> orderRepresentation = createOrderRepresentation();
		ResourceLink visaPaymentMethodLink = createPaymentMethodLink(TYPE_VISA, PAYMENT_METHOD_ID_VISA);
		ResourceLink amexPaymentMethodLink = createPaymentMethodLink(TYPE_AMERICANEXPRESS, PAYMENT_METHOD_ID_AMEX);
		ResourceState<LinksEntity> paymentMethods = createPaymentMethodsRepresentation(amexPaymentMethodLink, visaPaymentMethodLink);
		ResourceState<SelectorEntity> selectorRepresentation = createSelectorRepresentation(true);

		shouldBuildPaymentMethodListUri(PAYMENT_METHOD_LIST_URI);
		shouldBuildReadResourceCommandWithResult(ExecutionResultFactory.<ResourceState<?>>createReadOK(paymentMethods));
		shouldGetSelectorChosenPaymentMethodLinkWithResult(ExecutionResultFactory.createReadOK(visaPaymentMethodLink));
		shouldSetNameAndSelfUri(ORDER_URI);
		shouldBuildSelectorRepresentation(selectorRepresentation);

		OperationResult result = readPaymentMethodSelectorForOrderResourceOperator
				.processReadPaymentMethodSelector(RESOURCE_SERVER_NAME, orderRepresentation, READ);

		ResourceLink visaSelectorLink = createSelectorLink(PAYMENT_METHOD_ID_VISA);
		ResourceLink amexSelectorLink = createSelectorLink(PAYMENT_METHOD_ID_AMEX);
		List<ResourceLink> paymentMethodSelectors = Arrays.asList(amexSelectorLink, visaSelectorLink);
		ResourceLink paymentMethodLink = createPaymentMethodLink();
		verifyAddChoices(paymentMethodSelectors);
		verifySetSelection(visaSelectorLink);
		verifyAddLink(paymentMethodLink);

		assertTrue(SUCCESSFUL_OPERATION, result.isSuccessful());
		assertEquals(EXPECTED_RESOURCE_STATUS, ResourceStatus.READ_OK, result.getResourceStatus());
		assertEquals(EXPECTED_DATA, selectorRepresentation, result.getResourceState());
	}

	/**
	 * Expectations when no payment methods are found.
	 */
	@Test
	public void testNoPaymentMethodsFound() {
		ResourceState<OrderEntity> orderRepresentation = createOrderRepresentation();

		shouldBuildPaymentMethodListUri(PAYMENT_METHOD_LIST_URI);
		shouldBuildReadResourceCommandWithResult(ExecutionResultFactory.<ResourceState<?>>createNotFound("no payment methods found"));
		thrown.expect(containsResourceStatus(ResourceStatus.NOT_FOUND));

		readPaymentMethodSelectorForOrderResourceOperator
				.processReadPaymentMethodSelector(RESOURCE_SERVER_NAME, orderRepresentation, READ);	}

	/**
	 * Test when no payment method selected.
	 */
	@Test
	public void testWhenNoPaymentMethodSelected() {
		ResourceState<OrderEntity> orderRepresentation = createOrderRepresentation();

		ResourceLink visaPaymentMethodLink = createPaymentMethodLink(TYPE_VISA, PAYMENT_METHOD_ID_VISA);
		ResourceLink amexPaymentMethodLink = createPaymentMethodLink(TYPE_AMERICANEXPRESS, PAYMENT_METHOD_ID_AMEX);
		ResourceState<LinksEntity> paymentMethods = createPaymentMethodsRepresentation(amexPaymentMethodLink, visaPaymentMethodLink);

		ResourceState<SelectorEntity> selectorRepresentation = createSelectorRepresentation(false);

		shouldBuildPaymentMethodListUri(PAYMENT_METHOD_LIST_URI);
		shouldBuildReadResourceCommandWithResult(ExecutionResultFactory.<ResourceState<?>>createReadOK(paymentMethods));
		shouldGetSelectorChosenPaymentMethodLinkWithResult(ExecutionResultFactory.<ResourceLink>createNotFound("no payment method selected"));
		shouldSetNameAndSelfUri(ORDER_URI);
		shouldBuildSelectorRepresentation(selectorRepresentation);

		OperationResult result = readPaymentMethodSelectorForOrderResourceOperator
				.processReadPaymentMethodSelector(RESOURCE_SERVER_NAME, orderRepresentation, READ);

		ResourceLink visaSelectorLink = createSelectorLink(PAYMENT_METHOD_ID_VISA);
		ResourceLink amexSelectorLink = createSelectorLink(PAYMENT_METHOD_ID_AMEX);
		List<ResourceLink> paymentMethodSelectors = Arrays.asList(amexSelectorLink, visaSelectorLink);
		ResourceLink paymentMethodLink = createPaymentMethodLink();
		verifyAddChoices(paymentMethodSelectors);
		verifyAddLink(paymentMethodLink);

		assertEquals(EXPECTED_RESOURCE_STATUS, ResourceStatus.READ_OK, result.getResourceStatus());
		assertEquals(EXPECTED_DATA, selectorRepresentation, result.getResourceState());
	}

	/**
	 * Test unexpected status from finding selected payment method.
	 */
	@Test
	@SuppressWarnings("rawtypes")
	public void testUnexpectedStatusFromFindingSelectedPaymentMethod() {
		ResourceState<OrderEntity> orderRepresentation = createOrderRepresentation();

		ResourceLink visaPaymentMethodLink = createPaymentMethodLink(TYPE_VISA, PAYMENT_METHOD_ID_VISA);
		ResourceLink amexPaymentMethodLink = createPaymentMethodLink(TYPE_AMERICANEXPRESS, PAYMENT_METHOD_ID_AMEX);
		ResourceState paymentMethods = createPaymentMethodsRepresentation(amexPaymentMethodLink, visaPaymentMethodLink);

		ResourceState<SelectorEntity> selectorRepresentation = createSelectorRepresentation(false);

		shouldBuildPaymentMethodListUri(PAYMENT_METHOD_LIST_URI);
		shouldBuildReadResourceCommandWithResult(ExecutionResultFactory.<ResourceState<?>>createReadOK(paymentMethods));
		shouldGetSelectorChosenPaymentMethodLinkWithResult(ExecutionResultFactory.<ResourceLink>createServerError("server error"));
		shouldSetNameAndSelfUri(ORDER_URI);
		shouldBuildSelectorRepresentation(selectorRepresentation);

		thrown.expect(containsResourceStatus(ResourceStatus.SERVER_ERROR));

		readPaymentMethodSelectorForOrderResourceOperator
				.processReadPaymentMethodSelector(RESOURCE_SERVER_NAME, orderRepresentation, READ);
		verifyNeverSetSelection();
	}

	/**
	 * Test selected payment method not in available choices.
	 */
	@Test
	public void testSelectedPaymentMethodNotInAvailableChoices() {
		ResourceState<OrderEntity> orderRepresentation = createOrderRepresentation();

		ResourceLink visaPaymentMethodLink = createPaymentMethodLink(TYPE_VISA, PAYMENT_METHOD_ID_VISA);
		ResourceLink amexPaymentMethodLink = createPaymentMethodLink(TYPE_AMERICANEXPRESS, PAYMENT_METHOD_ID_AMEX);
		ResourceLink chosenPaymentMethodLink = createPaymentMethodLink(TYPE_TOKEN, PAYMENT_METHOD_ID_TOKEN);
		ResourceState<LinksEntity> paymentMethods = createPaymentMethodsRepresentation(amexPaymentMethodLink, visaPaymentMethodLink);

		ResourceState<SelectorEntity> selectorRepresentation = createSelectorRepresentation(false);

		shouldBuildPaymentMethodListUri(PAYMENT_METHOD_LIST_URI);
		shouldBuildReadResourceCommandWithResult(ExecutionResultFactory.<ResourceState<?>>createReadOK(paymentMethods));
		shouldGetSelectorChosenPaymentMethodLinkWithResult(ExecutionResultFactory.createReadOK(chosenPaymentMethodLink));
		shouldSetNameAndSelfUri(ORDER_URI);
		shouldBuildSelectorRepresentation(selectorRepresentation);

		OperationResult result = readPaymentMethodSelectorForOrderResourceOperator
				.processReadPaymentMethodSelector(RESOURCE_SERVER_NAME, orderRepresentation, READ);

		ResourceLink visaSelectorLink = createSelectorLink(PAYMENT_METHOD_ID_VISA);
		ResourceLink amexSelectorLink = createSelectorLink(PAYMENT_METHOD_ID_AMEX);
		List<ResourceLink> paymentMethodSelectors = Arrays.asList(amexSelectorLink, visaSelectorLink);
		ResourceLink paymentMethodLink = createPaymentMethodLink();
		verifyAddChoices(paymentMethodSelectors);
		verifyNeverSetSelection();
		verifyAddLink(paymentMethodLink);

		assertEquals(EXPECTED_RESOURCE_STATUS, ResourceStatus.READ_OK, result.getResourceStatus());
		assertEquals(EXPECTED_DATA, selectorRepresentation, result.getResourceState());
	}

	@Test
	public void ensureCreatePaymentMethodInfoLinkInvokesPaymentMethodInfoUriBuilderFactory() {
		readPaymentMethodSelectorForOrderResourceOperator.createPaymentMethodInfoLink("");
		verify(paymentMethodInfoUriBuilderFactory, times(1)).get();
	}

	@Test
	public void ensureCreatePaymentMethodInfoLinkSetsUriOnPaymentMethodUriBuilder() {
		readPaymentMethodSelectorForOrderResourceOperator.createPaymentMethodInfoLink("order/uri");
		verify(paymentMethodInfoUriBuilder, times(1)).setSourceUri("order/uri");
	}

	@Test
	public void ensureCreatePaymentMethodInfoLinkBuildsUri() {
		readPaymentMethodSelectorForOrderResourceOperator.createPaymentMethodInfoLink("");
		verify(paymentMethodInfoUriBuilder, times(1)).build();
	}

	@Test
	public void ensureCreatePaymentMethodInfoLinkBuildsCorrectLink() {
		ResourceLink createdLink = readPaymentMethodSelectorForOrderResourceOperator.createPaymentMethodInfoLink("");
		assertResourceLink(createdLink)
				.uri(INFO_URI)
				.rel(PaymentMethodRels.PAYMENTMETHODINFO_REL)
				.rev(SelectorRepresentationRels.SELECTOR)
				.type(ControlsMediaTypes.INFO.id());
	}

	private void shouldSetNameAndSelfUri(final String orderUri) {
		when(selectorProvider.get()).thenReturn(selectorResourceStateBuilder);
		when(selectorResourceStateBuilder.setName(PaymentMethodsConstants.PAYMENT_METHOD_SELECTOR_NAME)).thenReturn(selectorResourceStateBuilder);
		when(selectorResourceStateBuilder.setSelfUri(URIUtil.format(RESOURCE_SERVER_NAME, Selector.URI_PART, orderUri)))
				.thenReturn(selectorResourceStateBuilder);
	}

	private void verifyAddChoices(final Collection<ResourceLink> choices) {
		for (ResourceLink choice : choices) {
			verify(selectorResourceStateBuilder).addChoice(choice);
		}
	}

	private void verifySetSelection(final ResourceLink selected) {
		verify(selectorResourceStateBuilder).setSelection(selected);
	}

	private void verifyNeverSetSelection() {
		verify(selectorResourceStateBuilder, never()).setSelection(any(ResourceLink.class));
	}

	private void verifyAddLink(final ResourceLink paymentMethodLink) {
		verify(selectorResourceStateBuilder).addLink(paymentMethodLink);
	}

	private void shouldBuildSelectorRepresentation(final ResourceState<SelectorEntity> selectorRepresentation) {
		when(selectorResourceStateBuilder.build()).thenReturn(selectorRepresentation);
	}

	private void shouldBuildReadResourceCommandWithResult(final ExecutionResult<ResourceState<?>> result) {
		final ReadResourceCommand.Builder readResourceCommandBuilder = Mockito.mock(ReadResourceCommand.Builder.class);
		final ReadResourceCommand readResourceCommand = Mockito.mock(ReadResourceCommand.class);
		when(readResourceCommandBuilderProvider.get()).thenReturn(readResourceCommandBuilder);
		when(readResourceCommandBuilder.setResourceUri(PAYMENT_METHOD_LIST_URI)).thenReturn(readResourceCommandBuilder);
		when(readResourceCommandBuilder.setExpectedType(CollectionsMediaTypes.LINKS.id())).thenReturn(readResourceCommandBuilder);
		when(readResourceCommandBuilder.build()).thenReturn(readResourceCommand);
		when(readResourceCommand.execute()).thenReturn(result);
	}

	private void shouldBuildPaymentMethodListUri(final String result) {
		final PaymentMethodListUriBuilder paymentMethodListUriBuilder = Mockito.mock(PaymentMethodListUriBuilder.class);
		when(paymentMethodListUriBuilderFactory.get()).thenReturn(paymentMethodListUriBuilder);
		when(paymentMethodListUriBuilder.setScope(SCOPE)).thenReturn(paymentMethodListUriBuilder);
		when(paymentMethodListUriBuilder.build()).thenReturn(result);
	}

	private void shouldGetSelectorChosenPaymentMethodLinkWithResult(final ExecutionResult<ResourceLink> result) {
		when(paymentMethodLookup.getSelectorChosenPaymentMethodLink(SCOPE, USER_ID, ORDER_ID)).thenReturn(result);
	}

	private void shouldReturnUserIdFromResourceOperationContext() {
		when(resourceOperationContext.getUserIdentifier()).thenReturn(USER_ID);
	}

	private ResourceState<OrderEntity> createOrderRepresentation() {
		Self self = SelfFactory.createSelf(ORDER_URI, OrdersMediaTypes.ORDER.id());
		return ResourceState.Builder
				.create(OrderEntity.builder().withOrderId(ORDER_ID).build())
				.withSelf(self)
				.withScope(SCOPE)
				.build();
	}

	private ResourceState<LinksEntity> createPaymentMethodsRepresentation(final ResourceLink... links) {
		//Self self = SelfFactory.createSelf(ORDER_URI, OrdersMediaTypes.ORDER.id());
		return ResourceState.Builder.create(LinksEntity.builder().build())
				//.withSelf(self)
				.addingLinks(links)
				.build();
	}

	private ResourceLink createPaymentMethodLink(final String type, final String paymentMethodId) {
		return ResourceLinkFactory.create(URIUtil.format(RESOURCE_SERVER_NAME, SCOPE, paymentMethodId),
				type,
				ListElementRels.ELEMENT,
				"list");
	}

	private ResourceState<SelectorEntity> createSelectorRepresentation(final boolean chosenExists) {
		SingleSelectorResourceStateBuilder builder = new SingleSelectorResourceStateBuilder();
		builder.setName(PAYMENT_METHOD_SELECTOR)
				.setSelfUri(URIUtil.format(RESOURCE_SERVER_NAME, Selector.URI_PART, ORDER_URI))
				.addChoice(createSelectorLink(PAYMENT_METHOD_ID_AMEX))
				.addChoice(createSelectorLink(PAYMENT_METHOD_ID_VISA))
				.addLink(createPaymentMethodLink());

		if (chosenExists) {
			builder.setSelection(createSelectorLink(PAYMENT_METHOD_ID_VISA));
		}

		return builder.build();
	}

	private ResourceLink createSelectorLink(final String paymentMethodId) {
		return ResourceLinkFactory.createUriType(URIUtil.format(RESOURCE_SERVER_NAME, SCOPE, paymentMethodId, Selector.URI_PART, ORDER_URI),
				CollectionsMediaTypes.LINKS.id());
	}

	private ResourceLink createPaymentMethodLink() {
		return ResourceLinkFactory.create(INFO_URI,
				ControlsMediaTypes.INFO.id(),
				PaymentMethodRels.PAYMENTMETHODINFO_REL,
				SelectorRepresentationRels.SELECTOR);
	}
}
