/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.integrations.payment.tokens.handler.impl;


import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.plugin.payment.dto.PaymentMethod;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.integrations.payment.tokens.handler.CartOrdersDefaultPaymentMethodPopulator;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;

/**
 * Update cart orders default payment methods.
 */
@Singleton
@Named("cartOrdersDefaultPaymentMethodPopulator")
public class CartOrdersDefaultPaymentMethodPopulatorImpl implements CartOrdersDefaultPaymentMethodPopulator {

	private static final Logger LOG = LoggerFactory.getLogger(CartOrdersDefaultPaymentMethodPopulatorImpl.class);

	private final CartOrderRepository cartOrderRepository;

	/**
	 * Constructor.
	 * 
	 * @param cartOrderRepository cart order repository.
	 */
	@Inject
	public CartOrdersDefaultPaymentMethodPopulatorImpl(
			@Named("cartOrderRepository")
			final CartOrderRepository cartOrderRepository) {
		this.cartOrderRepository = cartOrderRepository;
	}

	@Override
	public void updateAllCartOrdersPaymentMethods(final Customer customer, final PaymentMethod paymentMethod, final String storeCode) {

		ExecutionResult<Collection<String>> customerCartOrderGuidsResult = cartOrderRepository.findCartOrderGuidsByCustomer(storeCode,
				customer.getGuid());
		if (customerCartOrderGuidsResult.isFailure()) {
			LOG.warn("Customer has no cart orders to default addresses with.");
			return;
		}
		Collection<String> cartOrderGuids = customerCartOrderGuidsResult.getData();
		for (String cartOrderGuid : cartOrderGuids) {
			updateCartOrderPaymentMethod(paymentMethod, storeCode, cartOrderGuid);
		}
	}

	private void updateCartOrderPaymentMethod(final PaymentMethod paymentMethod, final String storeCode, final String cartOrderGuid) {
		ExecutionResult<CartOrder> cartOrderResult = cartOrderRepository.findByGuid(storeCode, cartOrderGuid);
		if (cartOrderResult.isFailure()) {
			LOG.warn("Cart order was not found for guid.");
			return;
		}

		CartOrder cartOrder = cartOrderResult.getData();
		if (cartOrder.getPaymentMethod() == null) {
			cartOrder.usePaymentMethod(paymentMethod);
			cartOrderRepository.saveCartOrder(cartOrder);
		}
	}
}
