/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.link.impl;

import java.util.Collection;
import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.collections.CollectionsMediaTypes;
import com.elasticpath.rest.definition.profiles.ProfileEntity;
import com.elasticpath.rest.resource.dispatch.linker.ResourceStateLinkHandler;
import com.elasticpath.rest.resource.paymentmethods.rel.PaymentMethodRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.uri.URIUtil;


/**
 * Create a link to payment methods on the profile representation.
 */
@Singleton
@Named("addPaymentMethodLinkToProfileStrategy")
public final class AddPaymentMethodLinkToProfileStrategy implements ResourceStateLinkHandler<ProfileEntity> {

	private final String resourceServerName;


	/**
	 * Constructor.
	 *
	 * @param resourceServerName The Resource server name.
	 */
	@Inject
	AddPaymentMethodLinkToProfileStrategy(
			@Named("resourceServerName")
			final String resourceServerName) {

		this.resourceServerName = resourceServerName;
	}

	@Override
	public Collection<ResourceLink> getLinks(final ResourceState<ProfileEntity> profile) {

		String scope = profile.getScope();
		String paymentMethodsListUri = URIUtil.format(resourceServerName, scope);
		ResourceLink paymentInfoLink = ResourceLinkFactory.create(paymentMethodsListUri, CollectionsMediaTypes.LINKS.id(),
				PaymentMethodRels.PAYMENTMETHODS_REL, PaymentMethodRels.PROFILE_REV);

		return Collections.singleton(paymentInfoLink);
	}
}
