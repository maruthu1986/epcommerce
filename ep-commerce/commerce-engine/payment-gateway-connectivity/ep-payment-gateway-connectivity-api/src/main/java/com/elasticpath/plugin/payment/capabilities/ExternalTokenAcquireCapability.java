/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.plugin.payment.capabilities;

import java.util.Map;

import com.elasticpath.plugin.payment.dto.AddressDto;
import com.elasticpath.plugin.payment.dto.PaymentOptionFormDescriptor;
import com.elasticpath.plugin.payment.transaction.TokenAcquireTransactionRequest;
import com.elasticpath.plugin.payment.transaction.TokenAcquireTransactionResponse;

/**
 * This interface indicates that the payment gateway supports acquisition of payment tokens through client-side calls.
 */
public interface ExternalTokenAcquireCapability extends PaymentGatewayCapability {
	/**
	 * Formats and signs all information that must be sent to payment gateway to acquire a token.
	 * @param tokenAcquireTransactionRequest the token acquire request
	 * @param billingAddress billing address fields
	 * @param finishExternalTokenAcquireUrl the EP controller URL that the payment gateway should direct the user to after token acquire
	 * @param cancelExternalTokenAcquireUrl the EP controller URL that the payment gateway should direct the user to if token acquire is canceled
	 *
	 * @return the external auth request object
	 */
	PaymentOptionFormDescriptor buildExternalTokenAcquireRequest(TokenAcquireTransactionRequest tokenAcquireTransactionRequest,
		AddressDto billingAddress, String finishExternalTokenAcquireUrl, String cancelExternalTokenAcquireUrl);

	/**
	 * Handles external token acquire response.
	 *
	 * @param responseMap the response map
	 * @return the token acquire response
	 */
	TokenAcquireTransactionResponse handleExternalTokenAcquireResponse(Map<String, String> responseMap);

}
