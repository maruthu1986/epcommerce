/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.purchase.repositories.impl.paymentmeans;

import io.reactivex.Observable;
import io.reactivex.Single;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.springframework.core.convert.ConversionService;

import com.elasticpath.commons.util.Pair;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderAddress;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.repository.Repository;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.definition.purchases.PaymentMeansEntity;
import com.elasticpath.rest.definition.purchases.PurchaseIdentifier;
import com.elasticpath.rest.definition.purchases.PurchasePaymentmeanIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.order.OrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.ReactiveAdapter;

/**
 * Payment Means Entity Repository.
 *
 * @param <E> the entity type
 * @param <I> the identifier type
 */
@Component
public class PaymentMeansEntityRepositoryImpl<E extends PaymentMeansEntity, I extends PurchasePaymentmeanIdentifier>
		implements Repository<PaymentMeansEntity, PurchasePaymentmeanIdentifier> {

	/**
	 * Error for billing address not found.
	 */
	static final String NO_BILLING_ADDRESS_FOR_PAYMENT_MEANS = "No billing address for PaymentMeans with GUID %s was found in store %s";
	/**
	 * Error for payment means not found.
	 */
	static final String NO_PAYMENT_MEANS_FOUND = "No PaymentMeans with GUID %s was found in store %s";
	/**
	 * Failure for getting payment means.
	 */
	static final String FAILED_TO_GET_PAYMENT_MEANS = "Failed to get payment means.";

	private OrderRepository orderRepository;

	private ConversionService conversionService;

	private ReactiveAdapter reactiveAdapter;

	@Override
	public Single<PaymentMeansEntity> findOne(final PurchasePaymentmeanIdentifier identifier) {
		PurchaseIdentifier purchaseIdentifier = identifier.getPurchasePaymentmeans().getPurchase();
		String scope = purchaseIdentifier.getPurchases().getScope().getValue();
		String purchaseId = purchaseIdentifier.getPurchaseId().getValue();
		String paymeantmeansId = identifier.getPaymentmeansId().getValue();

		return getOrderPaymentUid(paymeantmeansId)
				.flatMap(orderPaymentUidLong -> orderRepository.findByGuidAsSingle(scope, purchaseId)
						.flatMap(order -> getPurchaseBillingAddress(order, purchaseId, scope)
								.flatMap(orderAddress -> getOrderPayment(orderPaymentUidLong, order, purchaseId, scope))
								.map(orderPayment -> convertPurchasePaymentToPaymentMeansEntity(order, orderPayment))));
	}

	/**
	 * Get the order payment uid.
	 *
	 * @param paymeantmeansId paymeantmeansId
	 * @return the order payment uid
	 */
	protected Single<Long> getOrderPaymentUid(final String paymeantmeansId) {
		return reactiveAdapter.fromNullableAsSingle(() -> parseOrderPaymentUidToLong(paymeantmeansId), FAILED_TO_GET_PAYMENT_MEANS);
	}

	/**
	 * Get the purchase's billing address.
	 *
	 * @param order      order
	 * @param purchaseId purchaseId
	 * @param scope      scope
	 * @return the paymentmeans billing address
	 */
	protected Single<OrderAddress> getPurchaseBillingAddress(final Order order, final String purchaseId, final String scope) {
		return reactiveAdapter.fromNullableAsSingle(order::getBillingAddress,
				String.format(NO_BILLING_ADDRESS_FOR_PAYMENT_MEANS, purchaseId, scope));
	}

	/**
	 * Get the order payment with the given order payment uid.
	 *
	 * @param orderPaymentUidLong orderPaymentUidLong
	 * @param order               order
	 * @param purchaseId          purchaseId
	 * @param scope               scope
	 * @return the order payment
	 */
	protected Single<OrderPayment> getOrderPayment(final Long orderPaymentUidLong, final Order order, final String purchaseId, final String scope) {
		return Observable.fromIterable(order.getOrderPayments())
				.filter(orderPayment -> orderPayment.getUidPk() == orderPaymentUidLong)
				.firstOrError()
				.onErrorResumeNext(Single.error(ResourceOperationFailure.notFound(String.format(NO_PAYMENT_MEANS_FOUND, purchaseId, scope))));
	}

	/**
	 * Convert the Purchase Payment to PaymentMeansEntity.
	 *
	 * @param order        order
	 * @param orderPayment orderPayment
	 * @return PaymentMeansEntity
	 */
	protected PaymentMeansEntity convertPurchasePaymentToPaymentMeansEntity(final Order order, final OrderPayment orderPayment) {
		return conversionService.convert(new Pair<>(orderPayment, order.getBillingAddress()), PaymentMeansEntity.class);
	}

	/**
	 * Parse the order payment uid to long.
	 *
	 * @param orderPaymentUid orderPaymentUid
	 * @return order payment uid long
	 */
	protected Long parseOrderPaymentUidToLong(final String orderPaymentUid) {
		Long orderPaymentUidLong;
		try {
			orderPaymentUidLong = Long.valueOf(orderPaymentUid);
		} catch (NumberFormatException e) {
			orderPaymentUidLong = null;
		}
		return orderPaymentUidLong;
	}

	@Reference
	public void setOrderRepository(final OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}

	@Reference
	public void setConversionService(final ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	@Reference
	public void setReactiveAdapter(final ReactiveAdapter reactiveAdapter) {
		this.reactiveAdapter = reactiveAdapter;
	}
}
