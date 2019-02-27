/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.transformer.handler.impl;

import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.paymenttokens.PaymentTokenEntity;
import com.elasticpath.rest.definition.paymenttokens.PaymenttokensMediaTypes;
import com.elasticpath.rest.resource.commons.handler.PaymentHandler;
import com.elasticpath.rest.schema.ResourceEntity;

/**
 * Implementation of {@link PaymentHandler} that handles {@link PaymentTokenEntity}s.
 */
@Singleton
@Named("paymentTokenPaymentMethodHandler")
public final class PaymentTokenPaymentMethodHandler implements PaymentHandler {
	@Override
	public Class<? extends ResourceEntity> handledType() {
		return PaymentTokenEntity.class;
	}

	@Override
	public String representationType() {
		return PaymenttokensMediaTypes.PAYMENT_TOKEN.id();
	}
}
