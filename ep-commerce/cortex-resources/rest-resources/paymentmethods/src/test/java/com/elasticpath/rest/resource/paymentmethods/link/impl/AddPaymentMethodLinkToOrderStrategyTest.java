/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.link.impl;

import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.rest.definition.controls.ControlsMediaTypes;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.definition.orders.OrdersMediaTypes;
import com.elasticpath.rest.resource.paymentmethods.rel.PaymentMethodRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.uri.PaymentMethodInfoUriBuilder;
import com.elasticpath.rest.schema.uri.PaymentMethodInfoUriBuilderFactory;


/**
 * Test for {@link com.elasticpath.rest.resource.paymentmethods.link.impl.AddPaymentMethodLinkToOrderStrategy}.
 */
@RunWith(MockitoJUnitRunner.class)
public final class AddPaymentMethodLinkToOrderStrategyTest {

	private static final String ORDER_ID = "orderId";
	private static final String ORDER_SELF_URI = "/mockorderuri";
	private static final String SCOPE = "scope";
	private static final String PAYMENT_METHOD_INFO_URI = "/uri";
	public static final String THE_CREATED_LINKS_SHOULD_BE_THE_SAME_AS_EXPECTED = "The created links should be the same as expected";

	@Mock
	private PaymentMethodInfoUriBuilderFactory paymentMethodInfoUriBuilderFactory;

	@Mock
	private PaymentMethodInfoUriBuilder paymentMethodInfoUriBuilder;

	private AddPaymentMethodLinkToOrderStrategy addPaymentMethodLinkToOrderStrategy;

	@Before
	public void setUp() {
		addPaymentMethodLinkToOrderStrategy = new AddPaymentMethodLinkToOrderStrategy(paymentMethodInfoUriBuilderFactory);
		when(paymentMethodInfoUriBuilderFactory.get()).thenReturn(paymentMethodInfoUriBuilder);
		when(paymentMethodInfoUriBuilder.setSourceUri(any(String.class))).thenReturn(paymentMethodInfoUriBuilder);
		when(paymentMethodInfoUriBuilder.build()).thenReturn(PAYMENT_METHOD_INFO_URI);
	}

	/**
	 * Tests creation of the link to payment methods selector for order with payment required and paymentinfo needed.
	 */
	@Test
	public void testLinkToPaymentMethodsForOrderWithPaymentRequiredAndInfoNeeded() {
		ResourceState<OrderEntity> orderRepresentation = createOrderRepresentation();

		Collection<ResourceLink> createdLinks =
				addPaymentMethodLinkToOrderStrategy.getLinks(orderRepresentation);

		assertThat("There should be two links created.", createdLinks, Matchers.hasSize(1));
		assertThat(THE_CREATED_LINKS_SHOULD_BE_THE_SAME_AS_EXPECTED, createdLinks, hasItems(createTestPaymentMethodInfoLink()));
	}


	/**
	 * Tests creation of the link to payment methods selector for order with payment required and a default payment method selected.
	 */
	@Test
	public void testLinkToPaymentMethodsForOrderWithPaymentRequiredAndDefaultSelected() {
		ResourceState<OrderEntity> orderRepresentation = createOrderRepresentation();

		Collection<ResourceLink> createdLinks =
				addPaymentMethodLinkToOrderStrategy.getLinks(orderRepresentation);

		assertThat("There should be one link created.", createdLinks, Matchers.hasSize(1));
		assertThat(THE_CREATED_LINKS_SHOULD_BE_THE_SAME_AS_EXPECTED, createdLinks, hasItems(createTestPaymentMethodInfoLink()));
	}

	/**
	 * Tests that no link to the payment methods selector for order is created when payment is not required.
	 */
	@Test
	public void testLinkToPaymentMethodsForOrderWithPaymentNotRequired() {
		ResourceState<OrderEntity> orderRepresentation = createOrderRepresentation();

		Collection<ResourceLink> createdLinks = addPaymentMethodLinkToOrderStrategy.getLinks(orderRepresentation);

		assertThat("There should be one link created.", createdLinks, Matchers.hasSize(1));
		assertThat(THE_CREATED_LINKS_SHOULD_BE_THE_SAME_AS_EXPECTED, createdLinks, hasItems(createTestPaymentMethodInfoLink()));
	}

	/**
	 * Tests that no link to the payment methods selector for order is created when the payment required lookup fails.
	 */
	@Test
	public void testLinkToPaymentMethodsForOrderWithPaymentRequiredFailure() {
		ResourceState<OrderEntity> orderRepresentation = createOrderRepresentation();

		Collection<ResourceLink> createdLinks =
				addPaymentMethodLinkToOrderStrategy.getLinks(orderRepresentation);

		assertThat("There should be one link created.", createdLinks, Matchers.hasSize(1));
		assertThat(THE_CREATED_LINKS_SHOULD_BE_THE_SAME_AS_EXPECTED, createdLinks, hasItems(createTestPaymentMethodInfoLink()));
	}

	@Test
	public void ensurePaymentMethodInfoUriBuilderFactoryGetIsInvoked() {
		addPaymentMethodLinkToOrderStrategy.createPaymentMethodInfoUri("");
		verify(paymentMethodInfoUriBuilderFactory, times(1)).get();
	}

	@Test
	public void ensurePaymentMethodInfoUriBuilderSetUriIsInvoked() {
		addPaymentMethodLinkToOrderStrategy.createPaymentMethodInfoUri("order/uri");
		verify(paymentMethodInfoUriBuilder, times(1)).setSourceUri("order/uri");
	}

	@Test
	public void ensurePaymentMethodInfoUriBuilderBuildIsInvoked() {
		addPaymentMethodLinkToOrderStrategy.createPaymentMethodInfoUri("");
		verify(paymentMethodInfoUriBuilder, times(1)).build();
	}

	@Test
	public void ensureUriReturnedFromUriBuilderBuildIsReturnedFromCreatePaymentMethodUri() {
		String paymentMethodInfoUri = addPaymentMethodLinkToOrderStrategy.createPaymentMethodInfoUri("");
		assertEquals("payment method info URI", "/uri", paymentMethodInfoUri);
	}

	private ResourceState<OrderEntity> createOrderRepresentation() {
		Self orderSelf = SelfFactory.createSelf(ORDER_SELF_URI, OrdersMediaTypes.ORDER.id());
		return ResourceState.Builder
				.create(OrderEntity.builder().withOrderId(ORDER_ID).build())
				.withSelf(orderSelf)
				.withScope(SCOPE)
				.build();
	}

	private ResourceLink createTestPaymentMethodInfoLink() {
		return ResourceLinkFactory.create(PAYMENT_METHOD_INFO_URI, ControlsMediaTypes.INFO.id(),
				PaymentMethodRels.PAYMENTMETHODINFO_REL,
				PaymentMethodRels.ORDER_REV);
	}
}
