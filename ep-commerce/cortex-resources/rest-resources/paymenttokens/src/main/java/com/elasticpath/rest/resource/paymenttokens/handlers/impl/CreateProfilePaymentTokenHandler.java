/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymenttokens.handlers.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.paymenttokens.PaymentTokenEntity;
import com.elasticpath.rest.definition.profiles.ProfileEntity;
import com.elasticpath.rest.definition.profiles.ProfilesMediaTypes;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.paymenttokens.handlers.CreatePaymentTokenHandler;
import com.elasticpath.rest.resource.paymenttokens.integration.PaymentTokenWriterStrategy;
import com.elasticpath.rest.resource.paymenttokens.integration.constants.PaymentTokenOwnerType;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.PaymentMethodUriBuilderFactory;

/**
 * Implementation of {@link CreatePaymentTokenHandler} for profiles.
 */
@Singleton
@Named("createProfilePaymentTokenHandler")
public class CreateProfilePaymentTokenHandler implements CreatePaymentTokenHandler<ProfileEntity> {
	private final PaymentTokenWriterStrategy paymentTokenWriterStrategy;
	private final PaymentMethodUriBuilderFactory paymentMethodUriBuilderFactory;

	/**
	 * Default constructor.
	 *
	 * @param paymentTokenWriterStrategy the {@link PaymentTokenWriterStrategy}
	 * @param paymentMethodUriBuilderFactory the {@link PaymentMethodUriBuilderFactory}
	 */
	@Inject
	CreateProfilePaymentTokenHandler(
			@Named("paymentTokenWriterStrategy")
			final PaymentTokenWriterStrategy paymentTokenWriterStrategy,
			@Named("paymentMethodUriBuilderFactory")
			final PaymentMethodUriBuilderFactory paymentMethodUriBuilderFactory) {
		this.paymentTokenWriterStrategy = paymentTokenWriterStrategy;
		this.paymentMethodUriBuilderFactory = paymentMethodUriBuilderFactory;
	}

	@Override
	public String handledOwnerRepresentationType() {
		return ProfilesMediaTypes.PROFILE.id();
	}

	@Override
	public ExecutionResult<PaymentTokenEntity> createPaymentToken(final PaymentTokenEntity paymentTokenEntity,
																	final ResourceState<ProfileEntity> owningRepresentation) {
		String scope = owningRepresentation.getScope();
		return paymentTokenWriterStrategy.createPaymentTokenForOwner(paymentTokenEntity,
				owningRepresentation.getEntity().getProfileId(), PaymentTokenOwnerType.PROFILE_TYPE, scope);
	}

	@Override
	public String createPaymentTokenUri(final PaymentTokenEntity paymentTokenEntity, final ResourceState<ProfileEntity> owningRepresentation) {
		return paymentMethodUriBuilderFactory.get()
				.setPaymentMethodId(Base32Util.encode(paymentTokenEntity.getPaymentMethodId()))
				.setScope(owningRepresentation.getScope())
				.build();
	}
}
