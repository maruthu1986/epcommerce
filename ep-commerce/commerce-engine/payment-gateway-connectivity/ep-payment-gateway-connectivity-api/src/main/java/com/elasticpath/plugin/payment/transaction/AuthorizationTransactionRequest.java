/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.plugin.payment.transaction;

import com.elasticpath.plugin.payment.dto.MoneyDto;
import com.elasticpath.plugin.payment.dto.PaymentMethod;

/**
 * Represents an authorization payment gateway transaction.
 */
public interface AuthorizationTransactionRequest extends PaymentTransactionRequest {
	/**
	 * Gets the {@link PaymentMethod} associated with this {@link AuthorizationTransactionRequest}.
	 *
	 * @return the {@link PaymentMethod} associated with this {@link AuthorizationTransactionRequest}
	 */
	PaymentMethod getPaymentMethod();
	
	/**
	 * Sets the {@link PaymentMethod} associated with this {@link AuthorizationTransactionRequest}.
	 *
	 * @param paymentMethod the {@link PaymentMethod} associated with this {@link AuthorizationTransactionRequest}
	 */
	void setPaymentMethod(PaymentMethod paymentMethod);
	
	/**
	 * Gets this {@link AuthorizationTransactionRequest}'s money. 
	 * The {@link com.elasticpath.plugin.payment.dto.MoneyDto} returned includes the authorization amount and currency.
	 *
	 * @return the {@link com.elasticpath.plugin.payment.dto.MoneyDto} associated with this {@link AuthorizationTransactionRequest}
	 */
	MoneyDto getMoney();
	
	/**
	 * Sets the {@link AuthorizationTransactionRequest}'s money.
	 *
	 * @param money the {@link com.elasticpath.plugin.payment.dto.MoneyDto} associated with this {@link AuthorizationTransactionRequest}
	 */
	void setMoney(MoneyDto money);
}
