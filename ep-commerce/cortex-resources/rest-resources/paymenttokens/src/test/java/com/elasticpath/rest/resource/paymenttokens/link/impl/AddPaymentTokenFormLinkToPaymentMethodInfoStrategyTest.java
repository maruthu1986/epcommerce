/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymenttokens.link.impl;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.rest.definition.controls.ControlsMediaTypes;
import com.elasticpath.rest.definition.controls.InfoEntity;
import com.elasticpath.rest.definition.orders.OrdersMediaTypes;
import com.elasticpath.rest.resource.commons.paymentmethod.rel.PaymentMethodCommonsConstants;
import com.elasticpath.rest.resource.paymenttokens.helper.PaymentTokenLinksFactory;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.SelfFactory;

/**
 * Test for {@link AddPaymentTokenFormLinkToPaymentMethodInfoStrategy}.
 */
@RunWith(MockitoJUnitRunner.class)
public class AddPaymentTokenFormLinkToPaymentMethodInfoStrategyTest {
	private static final String INCORRECT_NAME = "incorrectname";
	private static final String ORDER_URI = "/orders/scope/orderId";
	private static final String SCOPE = "scope";
	private static final String SELF_URI = "/self/scope/selfId";

	@Mock
	private PaymentTokenLinksFactory paymentTokenLinksFactory;
	@InjectMocks
	private AddPaymentTokenFormLinkToPaymentMethodInfoStrategy formLinkStrategy;

	private ResourceLink createPaymentTokenFormLink;

	private static final ResourceState<InfoEntity> INFO_STATE = ResourceState.Builder
			.create(InfoEntity.builder()
					.withName(PaymentMethodCommonsConstants.PAYMENT_METHOD_INFO_NAME)
			.build())
			.withScope(SCOPE)
			.withSelf(SelfFactory.createSelf(SELF_URI, ControlsMediaTypes.INFO.id()))
			.addingLinks(createOrderResourceLink())
			.build();


	@Before
	public void setUpObjectUnderTest() {
		createPaymentTokenFormLink = ResourceLink.builder().build();
		when(paymentTokenLinksFactory.createPaymentTokenFormLinkForOwner(ORDER_URI))
				.thenReturn(createPaymentTokenFormLink);
	}

	@Test
	public void verifyPaymentTokenFormLinkIsCreated() {
		formLinkStrategy.getLinks(INFO_STATE);
		verify(paymentTokenLinksFactory, times(1)).createPaymentTokenFormLinkForOwner(ORDER_URI);
	}

	@Test
	public void ensureNoLinkReturnedWhenInfoRepresentationMissingOrderLink() {
		ResourceState<InfoEntity> noLinks = ResourceState.builderFrom(INFO_STATE)
				.withLinks(Collections.<ResourceLink>emptySet())
				.build();

		Collection<ResourceLink> links = formLinkStrategy.getLinks(noLinks);

		assertThat("No links should be returned.", links, empty());
	}

	@Test
	public void ensureNoLinkReturnedWhenNotInfoRepresentation() {
		ResourceState<InfoEntity> wrongName = ResourceState.Builder.create(InfoEntity.builder()
				.withName(INCORRECT_NAME)
				.build())
				.build();

		Collection<ResourceLink> links = formLinkStrategy.getLinks(wrongName);

		assertThat("No links should be returned.", links, empty());
	}

	@Test
	public void ensureOnlyOneLinkReturned() {
		Collection<ResourceLink> links = formLinkStrategy.getLinks(INFO_STATE);
		assertThat("Only one link should be returned.", links, hasSize(1));
	}

	@Test
	public void ensureReturnedLinkIsCorrectlyFormed() {
		Collection<ResourceLink> links = formLinkStrategy.getLinks(INFO_STATE);
		assertThat(links, hasItem(createPaymentTokenFormLink));
	}

	private static ResourceLink createOrderResourceLink() {
		return ResourceLinkFactory.create(ORDER_URI,
				OrdersMediaTypes.ORDER.id(), PaymentMethodCommonsConstants.ORDER_REL, PaymentMethodCommonsConstants.PAYMENT_METHOD_INFO_NAME);
	}
}
