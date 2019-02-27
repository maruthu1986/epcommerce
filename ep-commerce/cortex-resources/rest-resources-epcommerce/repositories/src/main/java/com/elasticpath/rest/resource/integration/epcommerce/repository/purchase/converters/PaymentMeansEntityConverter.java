/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.purchase.converters;

import javax.inject.Named;
import javax.inject.Singleton;

import org.springframework.core.convert.converter.Converter;

import com.elasticpath.commons.util.Pair;
import com.elasticpath.domain.order.OrderAddress;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.rest.definition.purchases.PaymentMeansEntity;
import com.elasticpath.rest.definition.purchases.PaymentMeansPaymentTokenEntity;

/**
 * Converter for PaymentMeansEntity.
 */
@Singleton
@Named
public class PaymentMeansEntityConverter implements Converter<Pair<OrderPayment, OrderAddress>, PaymentMeansEntity> {


	@Override
	public PaymentMeansEntity convert(final Pair<OrderPayment, OrderAddress> orderPaymentOrderAddressPair) {
		OrderPayment orderPayment = orderPaymentOrderAddressPair.getFirst();


			return PaymentMeansPaymentTokenEntity.builder()
					.withPaymentMeansId(String.valueOf(orderPayment.getUidPk()))
					.withDisplayName(orderPayment.getDisplayValue())
					.build();
	}
}
