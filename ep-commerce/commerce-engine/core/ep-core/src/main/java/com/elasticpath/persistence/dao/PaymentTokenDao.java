/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.persistence.dao;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.customer.PaymentToken;

/**
 * Represents a DAO for {@link PaymentToken}s.
 */
public interface PaymentTokenDao {
	/**
	 * Adds the given {@link PaymentToken}.
	 *
	 * @param paymentToken the paymentToken to add
	 * @return the persisted instance of the paymentToken
	 * @throws EpServiceException - in case of any errors
	 */
	PaymentToken add(PaymentToken paymentToken) throws EpServiceException;

	/**
	 * Find a {@link PaymentToken} by uidpk.
	 *
	 * @param uidpk the uidpk
	 * @return the payment token if found, otherwise null
	 * @throws EpServiceException - in case of any errors
	 */
	PaymentToken get(long uidpk) throws EpServiceException;
}
