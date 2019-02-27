/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.integrations.payment.tokens.transformer;

import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.customer.PaymentToken;
import com.elasticpath.domain.customer.impl.PaymentTokenImpl;
import com.elasticpath.rest.definition.paymenttokens.PaymentTokenEntity;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.paymentmethods.PaymentMethodIdentifierResolver;
import com.elasticpath.rest.resource.transform.AbstractDomainTransformer;

/**
 * The transformer to convert a {@link PaymentTokenEntity} to a {@link PaymentToken}.
 */
@Singleton
@Named("paymentTokenTransformer")
public class PaymentTokenTransformer extends AbstractDomainTransformer<PaymentToken, PaymentTokenEntity> {

	private final PaymentMethodIdentifierResolver paymentMethodIdentifierResolver;

	/**
	 * Default constructor.
	 *
	 * @param paymentMethodIdentifierResolver the payment method identifier resolver
	 */
	@Inject
	public PaymentTokenTransformer(
			@Named("paymentMethodIdentifierResolver")
			final PaymentMethodIdentifierResolver paymentMethodIdentifierResolver) {
		this.paymentMethodIdentifierResolver = paymentMethodIdentifierResolver;
	}

	/**
	 * Transform a {@link PaymentTokenEntity} to a {@link PaymentToken}.
	 *
	 * @param paymentTokenEntity the payment token
	 * @param locale the locale
	 * @return a {@link PaymentToken}
	 */
	@Override
	public PaymentToken transformToDomain(final PaymentTokenEntity paymentTokenEntity, final Locale locale) {
		PaymentTokenImpl.TokenBuilder tokenBuilder = new PaymentTokenImpl.TokenBuilder();

		return tokenBuilder
				.withDisplayValue(paymentTokenEntity.getDisplayName())
				.withValue(paymentTokenEntity.getToken())
				.build();
	}

	@Override
	public PaymentTokenEntity transformToEntity(final PaymentToken paymentToken, final Locale locale) {
		return PaymentTokenEntity.builder()
				.withDisplayName(paymentToken.getDisplayValue())
				.withToken(paymentToken.getValue())
				.withPaymentMethodId(paymentMethodIdentifierResolver.getIdentifierForPaymentMethod(paymentToken))
				.build();
	}
}