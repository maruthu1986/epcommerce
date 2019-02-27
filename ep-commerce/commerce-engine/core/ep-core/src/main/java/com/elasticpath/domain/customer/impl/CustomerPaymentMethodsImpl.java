/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.domain.customer.impl;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.PredicateUtils;

import com.elasticpath.domain.customer.CustomerPaymentMethods;
import com.elasticpath.plugin.payment.dto.PaymentMethod;

/**
 * Default implementation of {@link com.elasticpath.domain.customer.CustomerPaymentMethods}. Delegates persistence of the payment method collection
 * and default to {@link CustomerImpl}. Ideally this could be represented as an {@link javax.persistence.Embeddable}, but due to a limitation of
 * OpenJPA this does not work correctly. Specifically, OpenJPA does not correctly delete orphaned elements from a {@link javax.persistence.OneToMany}
 * relationship defined on an <code>@Embeddable</code>, even when {@link org.apache.openjpa.persistence.ElementDependent} is specified.
 */
public class CustomerPaymentMethodsImpl implements CustomerPaymentMethods, Serializable {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000001L;
	private final CustomerImpl customer;

	/**
	 * Constructor.
	 *
	 * @param customer the customer to bind to
	 */
	CustomerPaymentMethodsImpl(final CustomerImpl customer) {
		this.customer = customer;
	}

	@Override
	public PaymentMethod getDefault() {
		return customer.getDefaultPaymentMethod();
	}

	@Override
	public void setDefault(final PaymentMethod defaultPaymentMethod) {
		if (defaultPaymentMethod == null) {
			throw new IllegalArgumentException("Default payment method can not be set to null");
		}

		if (!contains(defaultPaymentMethod)) {
			customer.getPaymentMethodsInternal().add(defaultPaymentMethod);
		}

		PaymentMethod resolvedDefault = resolveInternal(defaultPaymentMethod);

		customer.setDefaultPaymentMethod(resolvedDefault);
	}

	@Override
	public PaymentMethod getByUidPk(final long uidPk) {
		return Iterables.tryFind(all(), new Predicate<PaymentMethod>() {
			@Override
			public boolean apply(final PaymentMethod paymentMethod) {
				return ((AbstractPaymentMethodImpl) paymentMethod).getUidPk() == uidPk;
			}
		}).orNull();
	}

	@Override
	public Collection<PaymentMethod> all() {
		return Collections.unmodifiableCollection(customer.getPaymentMethodsInternal());
	}

	@Override
	public boolean contains(final PaymentMethod paymentMethod) {
		return all().contains(paymentMethod);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean addAll(final Collection<PaymentMethod> paymentMethods) {
		Collection<PaymentMethod> internalPaymentMethods = customer.getPaymentMethodsInternal();
		boolean addAllResult = internalPaymentMethods.addAll(CollectionUtils.subtract(paymentMethods, internalPaymentMethods));

		if (addAllResult && getDefault() == null) {
			setNextDefaultAvailable();
		}

		return addAllResult;
	}

	@Override
	public boolean add(final PaymentMethod paymentMethod) {
		return addAll(Arrays.asList(paymentMethod));
	}

	@Override
	public boolean remove(final PaymentMethod paymentMethod) {
		return removeAll(Arrays.asList(paymentMethod));
	}

	@Override
	public boolean removeAll(final Collection<PaymentMethod> paymentMethods) {
		boolean removeAllResult = customer.getPaymentMethodsInternal().removeAll(paymentMethods);
		if (removeAllResult && paymentMethods.contains(getDefault())) {
			customer.setDefaultPaymentMethod(null);
			setNextDefaultAvailable();
		}

		return removeAllResult;
	}

	@Override
	public void clear() {
		customer.setDefaultPaymentMethod(null);
		customer.getPaymentMethodsInternal().clear();
	}

	@Override
	public PaymentMethod resolve(final PaymentMethod paymentMethod) {
		if (paymentMethod == null) {
			throw new IllegalArgumentException("Payment method must not be null");
		}

		return resolveInternal(paymentMethod);
	}

	private PaymentMethod resolveInternal(final PaymentMethod paymentMethod) {
		return (PaymentMethod) CollectionUtils.find(customer.getPaymentMethodsInternal(), PredicateUtils.equalPredicate(paymentMethod));
	}

	private void setNextDefaultAvailable() {
		if (!all().isEmpty()) {
			setDefault(all().iterator().next());
		}
	}

	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof CustomerPaymentMethodsImpl)) {
			return false;
		}

		CustomerPaymentMethodsImpl rhs = (CustomerPaymentMethodsImpl) other;
		return Objects.equals(customer.getPaymentMethodsInternal(), rhs.customer.getPaymentMethodsInternal())
			&& Objects.equals(customer.getDefaultPaymentMethod(), rhs.customer.getDefaultPaymentMethod());
	}

	@Override
	public int hashCode() {
		return Objects.hash(customer.getPaymentMethodsInternal(), customer.getDefaultPaymentMethod());
	}

}
