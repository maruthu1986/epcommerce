/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.alias;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.rel.ListElementRels;
import com.elasticpath.rest.resource.dispatch.operator.annotation.Default;
import com.elasticpath.rest.resource.paymentmethods.rel.PaymentMethodRels;
import com.elasticpath.rest.schema.ResourceLink;
import com.elasticpath.rest.schema.ResourceLinkFactory;
import com.elasticpath.rest.uri.URIUtil;

/**
 * Creates links to the payment methods resource.
 */
@Singleton
@Named("paymentMethodsResourceLinkFactory")
public class PaymentMethodsResourceLinkFactory {

	private final String resourceServerName;

	/**
	 * Default constructor.
	 *
	 * @param resourceServerName the resource server name
	 */
	@Inject
	public PaymentMethodsResourceLinkFactory(
			@Named("resourceServerName")
			final String resourceServerName) {
		this.resourceServerName = resourceServerName;
	}

	/**
	 * Creates a default payment method element {@link ResourceLink}.
	 *
	 * @param scope the scope
	 * @param type the type of representation for the {@link ResourceLink}
	 * @return the created {@link ResourceLink}
	 */
	public ResourceLink createDefaultPaymentMethodElementLink(final String scope, final String type) {
		String defaultPaymentMethodUri = URIUtil.format(resourceServerName, scope, Default.URI_PART);
		return ResourceLinkFactory.create(defaultPaymentMethodUri, type, PaymentMethodRels.DEFAULT_REL, ListElementRels.LIST);
	}
}
