/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.link.impl;

import static com.elasticpath.rest.test.AssertResourceLink.assertResourceLink;
import static org.junit.Assert.assertThat;

import java.util.Collection;

import org.junit.Test;

import org.hamcrest.Matchers;

import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.definition.profiles.ProfileEntity;
import com.elasticpath.rest.resource.paymentmethods.constants.PaymentMethodTestConstants;
import com.elasticpath.rest.resource.paymentmethods.rel.PaymentMethodRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.uri.URIUtil;
import com.elasticpath.rest.util.collection.CollectionUtil;

/**
 * Test for {@link com.elasticpath.rest.resource.paymentmethods.link.impl.AddPaymentMethodLinkToProfileStrategy}.
 */
public final class AddPaymentMethodLinkToProfileStrategyTest {

	private static final String SCOPE = "scope";

	private final AddPaymentMethodLinkToProfileStrategy addPaymentMethodLinkToProfileStrategy = new AddPaymentMethodLinkToProfileStrategy(
			PaymentMethodTestConstants.PAYMENTMETHODS_PATH);


	/**
	 * Tests creation of the link to payment methods for profile.
	 */
	@Test
	public void testCreateLinkToPaymentMethodsForProfile() {
		ResourceState<ProfileEntity> profile = ResourceState.Builder
				.create(ProfileEntity.builder().build())
				.withScope(SCOPE)
				.build();

		Collection<ResourceLink> createdLinks =	addPaymentMethodLinkToProfileStrategy.getLinks(profile);

		assertThat("There should only be one link created.", createdLinks, Matchers.hasSize(1));
		ResourceLink createdLink = CollectionUtil.first(createdLinks);
		String expectedLinkUri = URIUtil.format(PaymentMethodTestConstants.PAYMENTMETHODS_PATH, SCOPE);
		assertResourceLink(createdLink)
				.rel(PaymentMethodRels.PAYMENTMETHODS_REL)
				.rev(PaymentMethodRels.PROFILE_REV)
				.type(CollectionsMediaTypes.LINKS.id())
				.uri(expectedLinkUri);
	}
}
