/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.transformer;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.rest.definition.orders.OrderEntity;
import com.elasticpath.rest.definition.paymentmethods.PaymentMethodEntity;
import com.elasticpath.rest.id.util.Base32Util;
import com.elasticpath.rest.schema.ResourceState;
import com.elasticpath.rest.schema.Self;
import com.elasticpath.rest.schema.SelfFactory;
import com.elasticpath.rest.schema.uri.OrderPaymentMethodUriBuilderFactory;

/**
 * The transformer to convert a {@link PaymentMethodEntity} to a {@link com.elasticpath.rest.schema.ResourceState}.
 */
@Singleton
@Named("orderPaymentMethodTransformer")
public class OrderPaymentMethodTransformer {
	private final OrderPaymentMethodUriBuilderFactory orderPaymentMethodUriBuilderFactory;

	/**
	 * Default constructor.
	 *
	 * @param orderPaymentMethodUriBuilderFactory the {@link com.elasticpath.rest.schema.uri.OrderPaymentMethodUriBuilderFactory}
	 */
	@Inject
	public OrderPaymentMethodTransformer(
			@Named("orderPaymentMethodUriBuilderFactory")
			final OrderPaymentMethodUriBuilderFactory orderPaymentMethodUriBuilderFactory) {
		this.orderPaymentMethodUriBuilderFactory = orderPaymentMethodUriBuilderFactory;
	}

	/**
	 * Transforms a {@link PaymentMethodEntity} to a {@link com.elasticpath.rest.schema.ResourceState}.
	 *
	 * @param order the order the payment method is selected for
	 * @param paymentMethodEntity the {@link PaymentMethodEntity}
	 * @return the representation
	 */
	public ResourceState<PaymentMethodEntity> transformToRepresentation(final ResourceState<OrderEntity> order,
																final PaymentMethodEntity paymentMethodEntity) {

		String scope = order.getScope();
		String encodedOrderId = Base32Util.encode(order.getEntity().getOrderId());
		String selfUri = orderPaymentMethodUriBuilderFactory.get()
				.setOrderId(encodedOrderId)
				.setScope(scope)
				.build();

		Self self = SelfFactory.createSelf(selfUri);

		return ResourceState.Builder.create(paymentMethodEntity)
				.withScope(scope)
				.withSelf(self)
				.build();
	}
}
