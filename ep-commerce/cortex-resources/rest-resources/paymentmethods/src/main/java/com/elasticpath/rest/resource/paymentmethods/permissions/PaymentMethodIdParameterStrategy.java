/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.permissions;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.identity.util.PrincipalsUtil;
import com.elasticpath.rest.resource.paymentmethods.PaymentMethodLookup;
import com.elasticpath.rest.authorization.parameter.AbstractCollectionValueStrategy;
import org.apache.shiro.subject.PrincipalCollection;


/**
 * Strategy for resolving the payment method ID parameter.
 */
@Singleton
@Named("paymentMethodIdParameterStrategy")
public final class PaymentMethodIdParameterStrategy extends AbstractCollectionValueStrategy {

	private final PaymentMethodLookup paymentMethodLookup;


	/**
	 * Constructor.
	 *
	 * @param paymentMethodLookup payment method lookup
	 */
	@Inject
	PaymentMethodIdParameterStrategy(
			@Named("paymentMethodLookup")
			final PaymentMethodLookup paymentMethodLookup) {

		this.paymentMethodLookup = paymentMethodLookup;
	}

	@Override
	protected Collection<String> getParameterValues(final PrincipalCollection principals) {
		String scope = PrincipalsUtil.getScope(principals);
		String userId = PrincipalsUtil.getUserIdentifier(principals);
		return paymentMethodLookup.getPaymentMethodIds(scope, userId).getData();
	}
}
