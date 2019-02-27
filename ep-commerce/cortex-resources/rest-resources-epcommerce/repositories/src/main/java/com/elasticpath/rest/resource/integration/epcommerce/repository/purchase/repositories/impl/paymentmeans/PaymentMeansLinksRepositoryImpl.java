/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.purchase.repositories.impl.paymentmeans;

import java.util.Comparator;

import io.reactivex.Observable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.purchases.PurchaseIdentifier;
import com.elasticpath.rest.definition.purchases.PurchasePaymentmeanIdentifier;
import com.elasticpath.rest.definition.purchases.PurchasePaymentmeansIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.order.OrderRepository;

/**
 * Repository for Purchase Payment Means link.
 *
 * @param <I>  the identifier type
 * @param <LI> the linked identifier type
 */
@Component
public class PaymentMeansLinksRepositoryImpl<I extends PurchasePaymentmeansIdentifier, LI extends PurchasePaymentmeanIdentifier>
		implements LinksRepository<PurchasePaymentmeansIdentifier, PurchasePaymentmeanIdentifier> {

	private static final Comparator<OrderPayment> ORDER_PAYMENT_COMPARATOR = Comparator.comparing(OrderPayment::getLastModifiedDate);

	private OrderRepository orderRepository;

	@Override
	public Observable<PurchasePaymentmeanIdentifier> getElements(final PurchasePaymentmeansIdentifier identifier) {
		PurchaseIdentifier purchaseIdentifier = identifier.getPurchase();
		String scope = purchaseIdentifier.getPurchases().getScope().getValue();
		String purchaseId = purchaseIdentifier.getPurchaseId().getValue();

		return orderRepository.findByGuidAsSingle(scope, purchaseId)
				.flattenAsObservable(Order::getOrderPayments)
				.sorted(ORDER_PAYMENT_COMPARATOR)
				.filter(orderPayment -> OrderPayment.AUTHORIZATION_TRANSACTION.equals(orderPayment.getTransactionType()))
				.map(orderPayment -> buildPurchasePaymentmeanIdenitifer(identifier, orderPayment));
	}

	/**
	 * Build the PurchasePaymentmeanIdentifier.
	 *
	 * @param identifier   identifier
	 * @param orderPayment orderPayment
	 * @return the PurchasePaymentmeanIdentifier
	 */
	protected PurchasePaymentmeanIdentifier buildPurchasePaymentmeanIdenitifer(
			final PurchasePaymentmeansIdentifier identifier, final OrderPayment orderPayment) {
		return PurchasePaymentmeanIdentifier.builder()
				.withPaymentmeansId(StringIdentifier.of(String.valueOf(orderPayment.getUidPk())))
				.withPurchasePaymentmeans(identifier)
				.build();
	}

	@Reference
	public void setOrderRepository(final OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}
}
