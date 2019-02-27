/*
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.commons.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderPaymentStatus;
import com.elasticpath.plugin.payment.PaymentType;

/**
 * The comparator factory which uses to create some comparators for Payments.
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public final class PaymentsComparatorFactory {

	private PaymentsComparatorFactory() {

	}

	/**
	 * Creates an OrderPayment comparator. Compares by created date in descending order,
	 * and in the case of a date match compares amounts.
	 *
	 * @return an OrderPayment comparator instance
	 */
	public static Comparator<OrderPayment> getOrderPaymentDateCompatator() {
		return (payment1, payment2) -> Comparator.comparing(OrderPayment::getCreatedDate)
			.thenComparing(OrderPayment::getAmount)
			.reversed()
			.compare(payment1, payment2);
	}

	/**
	 * Gets a {@link Comparator} which compares payment sources of two {@link OrderPayment}s. 
	 * 
	 * @return payment source comparator
	 */
	public static Comparator<OrderPayment> getPaymentSourceComparator() {
		return (payment1, payment2) -> {

			int initial = Comparator.nullsLast(Comparator.comparing(OrderPayment::getPaymentMethod))
				.compare(payment1, payment2);

			if (initial == 0) {
				if (PaymentType.GIFT_CERTIFICATE.equals(payment1.getPaymentMethod())) {
					return new GiftCertificateOrderPaymentComparator().compare(payment1, payment2);
				}

				if (PaymentType.PAYMENT_TOKEN.equals(payment1.getPaymentMethod())) {
					return new PaymentTokenOrderPaymentComparator().compare(payment1, payment2);
				}
			}

			return initial;
		};
	}

	/**
	 * Return list of all unique payment sources for the specified list of payments.
	 * 
	 * @param transactionType the transaction type of the payments that is desired 
	 * 		  or <code>null</code> in case no filtering should be applied by this criterion. 
	 * 		  For the available types see {@link OrderPayment}.
	 * @param allPayments list of all payments from which payment sources should be mined.
	 * @param exceptionalPaymentType the payment types which are not be include to result list
	 * @return list of unique payment sources without payments which have paymentType from exceptionalPaymentType array.
	 */
	public static List<OrderPayment> getListOfUniquePayments(
			final String transactionType,
			final Iterable<OrderPayment> allPayments,
			final PaymentType... exceptionalPaymentType) {

		TreeSet<OrderPayment> unique = new TreeSet<>(getPaymentSourceComparator());
		List<PaymentType> excludedPaymentTypes = Arrays.asList(exceptionalPaymentType);

		for (OrderPayment orderPayment : allPayments) {
			if (!excludedPaymentTypes.contains(orderPayment.getPaymentMethod())
					&& (transactionType == null || transactionType.equals(orderPayment.getTransactionType()))
					&& orderPayment.getStatus() != OrderPaymentStatus.FAILED) {
				unique.add(orderPayment);
			}
		}

		return new ArrayList<>(unique);
	}

}
