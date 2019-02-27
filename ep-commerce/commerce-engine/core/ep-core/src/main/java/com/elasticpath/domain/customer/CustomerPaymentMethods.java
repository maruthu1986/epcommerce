/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.domain.customer;

import java.util.Collection;

import com.elasticpath.plugin.payment.dto.PaymentMethod;

/**
 * Represents an association between a customer and a collection of {@link PaymentMethod}s.
 */
public interface CustomerPaymentMethods {
	/**
	 * Gets a customer's default payment method.
	 * @return the default payment method
	 */
	PaymentMethod getDefault();

	/**
	 * Sets the default payment method. If a payment method {@link Object#equals(Object)} to the default is already in the collection of payment
	 * methods, that instance will be used. Otherwise it will be added to the collection.
	 * @param defaultPaymentMethod the method to set as default
	 */
	void setDefault(PaymentMethod defaultPaymentMethod);

	/**
	 * Gets the payment method by UidPk.
	 *
	 * @param uidPk the payment method UidPk
	 * @return the {@link PaymentMethod} with the specified UidPk
	 */
	PaymentMethod getByUidPk(long uidPk);

	/**
	 * Returns a list of payment methods.
	 * @return the list of payment methods
	 */
	Collection<PaymentMethod> all();

	/**
	 * Returns true if this customer payment methods contains at least one element that is equal to the given element.
	 * @param paymentMethod element whose presence is to be tested
	 * @return true if this customer payment methods contains the specified element.
	 */
	boolean contains(PaymentMethod paymentMethod);

	/**
	 * Adds all elements of the specified collection to this customer payment methods.
	 * @param paymentMethods the collection of payment methods to add
	 * @return true if this customer payment methods changed as a result of this operation
	 */
	boolean addAll(Collection<PaymentMethod> paymentMethods);

	/**
	 * Adds the given payment method to this customer payment methods.
	 * @param paymentMethod the payment method to add
	 * @return true if this customer payment methods changed as a result of this operation
	 */
	boolean add(PaymentMethod paymentMethod);

	/**
	 * Removes the given payment method from this customer payment methods.
	 * @param paymentMethod the payment method to remove
	 * @return true if this customer payment methods changed as a result of this operation
	 */
	boolean remove(PaymentMethod paymentMethod);

	/**
	 * Removes the given payment methods from this customer payment methods.
	 * @param paymentMethods the collection of payment methods to remove
	 * @return true if this customer payment methods changed as a result of this operation
	 */
	boolean removeAll(Collection<PaymentMethod> paymentMethods);

	/**
	 * Removes all payment methods from this customer.
	 */
	void clear();

	/**
	 * Resolves the customer payment method instance that equals the one provided. If no such payment method exists this method returns null.
	 *
	 * @param paymentMethod the payment method to resolve
	 * @return the resolved payment method, null if no such payment method exists
	 */
	PaymentMethod resolve(PaymentMethod paymentMethod);
}
