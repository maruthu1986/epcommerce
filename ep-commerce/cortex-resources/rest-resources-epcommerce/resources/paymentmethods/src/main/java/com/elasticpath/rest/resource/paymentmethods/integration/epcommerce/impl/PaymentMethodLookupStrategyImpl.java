/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.paymentmethods.integration.epcommerce.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.customer.CustomerPaymentMethods;
import com.elasticpath.money.Money;
import com.elasticpath.plugin.payment.dto.PaymentMethod;
import com.elasticpath.rest.chain.Assign;
import com.elasticpath.rest.chain.BrokenChainException;
import com.elasticpath.rest.chain.OnFailure;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.definition.paymentmethods.PaymentMethodEntity;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.calc.TotalsCalculator;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.customer.paymentmethods.CustomerPaymentMethodsRepository;
import com.elasticpath.rest.resource.paymentmethods.integration.PaymentMethodLookupStrategy;
import com.elasticpath.rest.resource.paymentmethods.integration.epcommerce.transform.PaymentMethodTransformer;
import com.elasticpath.rest.util.math.NumberUtil;

/**
 * Implementation of {@link PaymentMethodLookupStrategy} specific to DCE.
 */
@Singleton
@Named("paymentMethodLookupStrategy")
public class PaymentMethodLookupStrategyImpl implements PaymentMethodLookupStrategy {

	private final CustomerPaymentMethodsRepository customerPaymentMethodsRepository;
	private final PaymentMethodTransformer paymentMethodTransformer;
	private final CartOrderRepository cartOrderRepository;
	private final ResourceOperationContext resourceOperationContext;
	private final TotalsCalculator totalsCalculator;

	/**
	 * Default Constructor.
	 *
	 * @param customerPaymentMethodsRepository the customer credit card repository
	 * @param paymentMethodTransformer the payment method transformer
	 * @param cartOrderRepository the cart order repository
	 * @param resourceOperationContext the resource operation context
	 * @param totalsCalculator totalsCalculator
	 */
	@Inject
	public PaymentMethodLookupStrategyImpl(
			@Named("customerPaymentMethodsRepository")
			final CustomerPaymentMethodsRepository customerPaymentMethodsRepository,
			@Named("paymentMethodTransformer")
			final PaymentMethodTransformer paymentMethodTransformer,
			@Named("cartOrderRepository")
			final CartOrderRepository cartOrderRepository,
			@Named("resourceOperationContext")
			final ResourceOperationContext resourceOperationContext,
			@Named("totalsCalculator")
			final TotalsCalculator totalsCalculator) {

		this.customerPaymentMethodsRepository = customerPaymentMethodsRepository;
		this.paymentMethodTransformer = paymentMethodTransformer;
		this.cartOrderRepository = cartOrderRepository;
		this.resourceOperationContext = resourceOperationContext;
		this.totalsCalculator = totalsCalculator;
	}

	@Override
	public ExecutionResult<PaymentMethodEntity> getOrderPaymentMethod(final String storeCode, final String cartOrderGuid) {

		CartOrder cartOrder = Assign.ifSuccessful(cartOrderRepository.findByGuid(storeCode, cartOrderGuid));
		PaymentMethod paymentMethod = Assign.ifNotNull(cartOrder.getPaymentMethod(),
				OnFailure.returnNotFound("No payment method set on cart order with guid %s in store %s", cartOrderGuid, storeCode));
		return ExecutionResultFactory.createReadOK(paymentMethodTransformer.transformToEntity(paymentMethod));
	}

	@Override
	public ExecutionResult<Boolean> isPaymentRequired(final String storeCode, final String cartOrderGuid) {
		Money totalMoney = Assign.ifSuccessful(totalsCalculator.calculateSubTotalForCartOrder(storeCode, cartOrderGuid));
		return ExecutionResultFactory.createReadOK(isCartOrderTotalPositive(totalMoney));
	}

	@Override
	public ExecutionResult<PaymentMethodEntity> getPaymentMethod(final String storeCode, final String paymentMethodId) {

		String customerGuid = resourceOperationContext.getUserIdentifier();
		PaymentMethod paymentMethod = Assign.ifSuccessful(customerPaymentMethodsRepository
				.findPaymentMethodByCustomerGuidAndPaymentMethodId(customerGuid, paymentMethodId));
		PaymentMethodEntity paymentMethodEntity = paymentMethodTransformer.transformToEntity(paymentMethod);

		return ExecutionResultFactory.createReadOK(paymentMethodEntity);
	}

	@Override
	public ExecutionResult<Collection<String>> getPaymentMethodIds(final String storeCode, final String customerGuid) {

		Collection<PaymentMethodEntity> paymentMethodEntities;
		try {
			paymentMethodEntities = Assign.ifSuccessful(getPaymentMethodsForUser(storeCode, customerGuid));
		} catch (BrokenChainException bce) {
			paymentMethodEntities = Collections.emptyList();
		}
		Collection<String> paymentMethodIds = new ArrayList<>(paymentMethodEntities.size());

		for (PaymentMethodEntity paymentMethodEntity : paymentMethodEntities) {
			paymentMethodIds.add(paymentMethodEntity.getPaymentMethodId());
		}

		return ExecutionResultFactory.createReadOK(paymentMethodIds);
	}

	@Override
	public ExecutionResult<Collection<PaymentMethodEntity>> getPaymentMethodsForUser(final String storeCode, final String customerGuid) {

		CustomerPaymentMethods paymentMethods =
				Assign.ifSuccessful(customerPaymentMethodsRepository.findPaymentMethodsByCustomerGuid(customerGuid));
		Collection<PaymentMethodEntity> paymentMethodEntities = new ArrayList<>(paymentMethods.all().size());

		for (PaymentMethod paymentMethod : paymentMethods.all()) {
			paymentMethodEntities.add(paymentMethodTransformer.transformToEntity(paymentMethod));
		}

		return ExecutionResultFactory.createReadOK(paymentMethodEntities);
	}

	@Override
	public ExecutionResult<Boolean> isPaymentMethodSelectedForOrder(final String storeCode,
																	final String cartOrderGuid) {

		CartOrder cartOrder = Assign.ifSuccessful(cartOrderRepository.findByGuid(storeCode, cartOrderGuid));
		return ExecutionResultFactory.createReadOK(cartOrder.getPaymentMethod() != null);
	}

	@Override
	public ExecutionResult<PaymentMethodEntity> getSelectorChosenPaymentMethod(final String scope, final String userId, final String decodedOrderId) {

		CartOrder cartOrder = Assign.ifSuccessful(cartOrderRepository.findByGuid(scope, decodedOrderId));
		PaymentMethod methodFromOrder = Assign.ifNotNull(cartOrder.getPaymentMethod(), OnFailure.returnNotFound());
		CustomerPaymentMethods paymentMethods =
				Assign.ifSuccessful(customerPaymentMethodsRepository.findPaymentMethodsByCustomerGuid(userId));
		PaymentMethod resolved = Assign.ifNotNull(paymentMethods.resolve(methodFromOrder), OnFailure.returnNotFound());

		return ExecutionResultFactory.createReadOK(paymentMethodTransformer.transformToEntity(resolved));
	}

	private boolean isCartOrderTotalPositive(final Money totalMoney) {
		return NumberUtil.isPositive(totalMoney.getAmount());
	}
}
