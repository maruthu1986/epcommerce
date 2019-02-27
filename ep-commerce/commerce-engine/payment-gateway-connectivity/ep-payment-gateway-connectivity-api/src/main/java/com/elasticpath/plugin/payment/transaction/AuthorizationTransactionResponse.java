/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.plugin.payment.transaction;

import com.elasticpath.plugin.payment.dto.MoneyDto;

/**
 * Represents an authorization {@link PaymentTransactionResponse}. 
 */
public interface AuthorizationTransactionResponse extends PaymentTransactionResponse {

	/**
	 * Gets the money associated with the authorization transaction that took place.
	 *
	 * @return {@link com.elasticpath.plugin.payment.dto.MoneyDto} associated with transaction
	 */
	MoneyDto getMoney();
	
	/**
	 * Sets the money associated with the authorization transaction that took place.
	 *
	 * @param money the {@link com.elasticpath.plugin.payment.dto.MoneyDto}
	 */
	void setMoney(MoneyDto money);
}
