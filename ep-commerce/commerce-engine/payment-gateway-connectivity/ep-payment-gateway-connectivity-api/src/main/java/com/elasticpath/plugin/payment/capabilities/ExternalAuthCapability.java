/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.plugin.payment.capabilities;

import java.util.Map;

import com.elasticpath.plugin.payment.dto.AddressDto;
import com.elasticpath.plugin.payment.dto.OrderPaymentDto;
import com.elasticpath.plugin.payment.dto.OrderShipmentDto;
import com.elasticpath.plugin.payment.dto.PaymentOptionFormDescriptor;
import com.elasticpath.plugin.payment.transaction.AuthorizationTransactionRequest;

/**
 * A marker interface for capabilities related to external authentication, such as direct order post and hosted page authentication.
 */
public interface ExternalAuthCapability extends PaymentGatewayCapability {

	/**
	 * Formats and signs all information that must be sent to payment gateway except any credit card info.
	 * @param authorizationTransactionRequest the {@link AuthorizationTransactionRequest}
	 * @param billingAddress billing address fields
	 * @param shipment order shipment dto
	 * @param redirectExternalAuthUrl the url that checkout should post to immediately before redirecting to the hosted
	 *     payment gateway form
	 * @param finishExternalAuthUrl the EP controller URL that the payment gateway should direct the user to after successful payment processing
	 * @param cancelExternalAuthUrl the EP controller URL that the payment gateway should direct the user to if payment processing fails
	 * 
	 * @return the external auth request object
	 */
	PaymentOptionFormDescriptor buildExternalAuthRequest(AuthorizationTransactionRequest authorizationTransactionRequest, AddressDto billingAddress,
			OrderShipmentDto shipment, String redirectExternalAuthUrl, String finishExternalAuthUrl, String cancelExternalAuthUrl);

	/**
	 * Handles direct post auth response.
	 * 
	 * @param responseMap the response map
	 * @return an updated order payment DTO
	 */
	OrderPaymentDto handleExternalAuthResponse(Map<String, String> responseMap);

}
