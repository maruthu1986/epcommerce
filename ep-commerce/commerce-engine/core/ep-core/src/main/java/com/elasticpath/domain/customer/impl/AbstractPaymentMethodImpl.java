/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.domain.customer.impl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.TableGenerator;

import com.elasticpath.persistence.api.AbstractPersistableImpl;
import com.elasticpath.plugin.payment.dto.PaymentMethod;

/**
 * Abstract base class that {@link PaymentMethod}s should extend in order to be persistable using JPA. This is
 * required because OpenJPA 1 doesn't support polymorphic persistence using interfaces only.
 *
 * Subclasses can test the correctness of their implementation by writing a unit test that extends
 * {@link com.elasticpath.domain.customer.impl.AbstractPaymentMethodImplTest}.
 *
 * @param <T> the type extending {@link PaymentMethod}.
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class AbstractPaymentMethodImpl<T extends AbstractPaymentMethodImpl<T>> extends AbstractPersistableImpl implements PaymentMethod {

	private static final long serialVersionUID = 50000000001L;

	private static final String GENERATOR_NAME = "PAYMENTMETHOD";

	private long uidPk;

	@Override
	@Id
	@Column(name = "UIDPK")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = GENERATOR_NAME)
	@TableGenerator(name = GENERATOR_NAME, table = "JPA_GENERATED_KEYS", pkColumnName = "ID",
			valueColumnName = "LAST_VALUE", pkColumnValue = GENERATOR_NAME, allocationSize = HIGH_CONCURRENCY_ALLOCATION_SIZE)
	public long getUidPk() {
		return uidPk;
	}

	@Override
	public void setUidPk(final long uidPk) {
		this.uidPk = uidPk;
	}

	/**
	 * Creates a new instance of the payment method subclassing {@link AbstractPaymentMethodImpl}.
	 * Copies all fields with the exception of UIDPK.
	 * @return a new copy of the payment method.
	 */
	public abstract T copy();
}
