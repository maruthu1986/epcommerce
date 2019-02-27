/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymenttokens.handlers.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.definition.orders.OrdersMediaTypes;
import com.elasticpath.rest.definition.paymenttokens.PaymentTokenEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.resource.paymenttokens.handlers.CreatePaymentTokenHandler;
import com.elasticpath.rest.resource.paymenttokens.integration.PaymentTokenWriterStrategy;
import com.elasticpath.rest.resource.paymenttokens.integration.constants.PaymentTokenOwnerType;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.uri.OrderPaymentMethodUriBuilderFactory;

/**
 * Implementation of {@link CreatePaymentTokenHandler} for orders.
 */
@Singleton
@Named("createOrderPaymentTokenHandler")
public class CreateOrderPaymentTokenHandler implements CreatePaymentTokenHandler<OrderEntity> {
	private final PaymentTokenWriterStrategy paymentTokenWriterStrategy;
	private final OrderPaymentMethodUriBuilderFactory orderPaymentMethodUriBuilderFactory;

	/**
	 * Default constructor.
	 *
	 * @param paymentTokenWriterStrategy          the {@link PaymentTokenWriterStrategy}
	 * @param orderPaymentMethodUriBuilderFactory the {@link OrderPaymentMethodUriBuilderFactory}
	 */
	@Inject
	CreateOrderPaymentTokenHandler(
			@Named("paymentTokenWriterStrategy")
			final PaymentTokenWriterStrategy paymentTokenWriterStrategy,
			@Named("orderPaymentMethodUriBuilderFactory")
			final OrderPaymentMethodUriBuilderFactory orderPaymentMethodUriBuilderFactory) {
		this.paymentTokenWriterStrategy = paymentTokenWriterStrategy;
		this.orderPaymentMethodUriBuilderFactory = orderPaymentMethodUriBuilderFactory;
	}

	@Override
	public String handledOwnerRepresentationType() {
		return OrdersMediaTypes.ORDER.id();
	}

	@Override
	public ExecutionResult<PaymentTokenEntity> createPaymentToken(final PaymentTokenEntity paymentTokenEntity,
																	final ResourceState<OrderEntity> owningRepresentation) {

		String scope = owningRepresentation.getScope();
		return paymentTokenWriterStrategy.createPaymentTokenForOwner(paymentTokenEntity,
				owningRepresentation.getEntity().getOrderId(), PaymentTokenOwnerType.ORDER_TYPE, scope);
	}

	@Override
	public String createPaymentTokenUri(final PaymentTokenEntity paymentTokenEntity, final ResourceState<OrderEntity> owningRepresentation) {
		return orderPaymentMethodUriBuilderFactory.get()
				.setScope(owningRepresentation.getScope())
				.setOrderId(Base32Util.encode(owningRepresentation.getEntity().getOrderId()))
				.build();
	}
}
