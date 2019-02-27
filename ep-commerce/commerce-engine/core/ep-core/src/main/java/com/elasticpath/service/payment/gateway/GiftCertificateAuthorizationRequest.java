/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.service.payment.gateway;

import com.elasticpath.plugin.payment.transaction.AuthorizationTransactionRequest;

/**
 * Gift certificate authorization transaction request.
 */
public interface GiftCertificateAuthorizationRequest extends AuthorizationTransactionRequest, GiftCertificateTransactionRequest {
	
}
