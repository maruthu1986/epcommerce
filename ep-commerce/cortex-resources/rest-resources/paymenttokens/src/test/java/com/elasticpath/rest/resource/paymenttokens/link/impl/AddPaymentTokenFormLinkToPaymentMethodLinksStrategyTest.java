/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymenttokens.link.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.rest.definition.collections.LinksEntity;
import com.elasticpath.rest.resource.commons.paymentmethod.rel.PaymentMethodCommonsConstants;
import com.elasticpath.rest.resource.paymenttokens.helper.PaymentTokenLinksFactory;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;

/**
 * Tests the {@link AddPaymentTokenFormLinkToPaymentMethodLinksStrategy}.
 */
@RunWith(MockitoJUnitRunner.class)
public class AddPaymentTokenFormLinkToPaymentMethodLinksStrategyTest {
	public static final String TEST_SCOPE = "testScope";
	public static final String INCORRECT_NAME = "incorrectName";
	@Mock
	private PaymentTokenLinksFactory paymentTokenLinksFactory;
	@InjectMocks
	private AddPaymentTokenFormLinkToPaymentMethodLinksStrategy addPaymentTokenFormLinkToPaymentMethodLinksStrategy;

	private ResourceLink createPaymentTokenformLink;

	private static final ResourceState<LinksEntity> LINK_STATE = ResourceState.Builder
			.create(LinksEntity.builder()
					.withName(PaymentMethodCommonsConstants.PAYMENT_METHOD_LINKS_NAME)
					.build())
			.withScope(TEST_SCOPE)
			.build();


	@Before
	public void setUpHappyCollaboratorsAndCommonTestComponents() {
		createPaymentTokenformLink = ResourceLink.builder().build();
		when(paymentTokenLinksFactory.createPaymentTokenFormLinkForOwner(TEST_SCOPE)).thenReturn(createPaymentTokenformLink);
	}

	@Test
	public void verifyCreatePaymentTokenFormLinkIsCreated() {
		addPaymentTokenFormLinkToPaymentMethodLinksStrategy.getLinks(LINK_STATE);

		verify(paymentTokenLinksFactory, times(1)).createPaymentTokenFormLinkForOwner(TEST_SCOPE);
	}

	@Test
	public void ensureOnlyOneLinkReturned() {
		Collection<ResourceLink> links = addPaymentTokenFormLinkToPaymentMethodLinksStrategy.getLinks(LINK_STATE);

		assertSame("Only one link should be returned.", 1, links.size());
	}

	@Test
	public void ensureCorrectCreatePaymentTokenFormLinkIsReturned() {
		Collection<ResourceLink> createdLinks = addPaymentTokenFormLinkToPaymentMethodLinksStrategy.getLinks(LINK_STATE);

		assertThat(createdLinks, hasItem(createPaymentTokenformLink));
	}

	@Test
	public void ensureLinksRepresentationWithNullNameReturnsEmptyCollection() {
		ResourceState<LinksEntity> nullName = ResourceState.Builder
				.create(LinksEntity.builder()
						.build())
				.build();

		Collection<ResourceLink> createdLinks = addPaymentTokenFormLinkToPaymentMethodLinksStrategy.getLinks(nullName);

		assertThat(createdLinks, empty());
	}

	@Test
	public void ensureLinksRepresentationWithIncorrectNameReturnsEmptyCollection() {
		ResourceState<LinksEntity> wrongName = ResourceState.Builder
				.create(LinksEntity.builder()
						.withName(INCORRECT_NAME)
						.build())
				.build();

		Collection<ResourceLink> createdLinks = addPaymentTokenFormLinkToPaymentMethodLinksStrategy.getLinks(wrongName);

		assertThat(createdLinks, empty());
	}


}
