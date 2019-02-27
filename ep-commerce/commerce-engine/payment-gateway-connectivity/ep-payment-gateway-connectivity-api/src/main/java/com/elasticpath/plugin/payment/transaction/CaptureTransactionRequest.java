/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.plugin.payment.transaction;

import com.elasticpath.plugin.payment.dto.MoneyDto;

/**
 * Represents a {@link PaymentTransactionRequest} request for a capture payment gateway transaction.
 */
public interface CaptureTransactionRequest extends PaymentTransactionRequest {
	/**
	 * This field represents a unique identifier for the {@link CaptureTransactionRequest}
	 * and is generally used to ensure operations are idempotent. Given an authorization and a
	 * capture for example, a duplicate capture with the same request id should not succeed.
	 *
	 * @return the unique identifier associated with this {@link CaptureTransactionRequest}
	 */
	String getRequestToken();

	/**
	 * Sets the fields representing a unique identifier for the {@link CaptureTransactionRequest}
	 * and is generally used to ensure operations are idempotent. Given an authorization and a
	 * capture for example, a duplicate capture with the same request token should not succeed.
	 *
	 * @param requestToken the unique identifier associated with this {@link CaptureTransactionRequest}.
	 */
	void setRequestToken(String requestToken);

	/**
	 * Get the authorization code for this {@link CaptureTransactionRequest}.
	 * It is used to associate any transaction with its associated follow-on
	 * transaction, such as a capture transaction with its preceding preauthorization, much like the requestToken.
	 *
	 * @return the authorization code
	 */
	String getAuthorizationCode();

	/**
	 * Set the authorization code for this {@link CaptureTransactionRequest}.
	 * It is used to associate any transaction with its associated follow-on
	 * transaction, such as a capture transaction with its preceding preauthorization, much like the requestToken.
	 *
	 * @param authorizationCode the authorization code
	 */
	void setAuthorizationCode(String authorizationCode);

	/**
	 * Gets this {@link CaptureTransactionRequest}'s money.
	 * The {@link com.elasticpath.plugin.payment.dto.MoneyDto} returned includes the capture amount and currency.
	 *
	 * @return the {@link com.elasticpath.plugin.payment.dto.MoneyDto} associated with this {@link CaptureTransactionRequest}
	 */
	MoneyDto getMoney();

	/**
	 * Sets the {@link CaptureTransactionRequest}'s money.
	 *
	 * @param money the {@link com.elasticpath.plugin.payment.dto.MoneyDto} associated with this {@link CaptureTransactionRequest}
	 */
	void setMoney(MoneyDto money);
}
