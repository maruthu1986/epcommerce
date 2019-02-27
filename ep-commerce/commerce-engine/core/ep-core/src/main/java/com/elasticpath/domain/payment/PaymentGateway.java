/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.payment;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.plugin.payment.PaymentGatewayType;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.plugin.payment.capabilities.PaymentGatewayCapability;
import com.elasticpath.plugin.payment.dto.PaymentOptionFormDescriptor;
import com.elasticpath.plugin.payment.transaction.TokenAcquireTransactionRequest;
import com.elasticpath.plugin.payment.transaction.TokenAcquireTransactionResponse;

/**
 * Represents a payment processing gateway such as Verisign or Cybersource.
 */
public interface PaymentGateway extends Persistable {

	/**
	 * Get the type of this payment gateway.
	 *
	 * @return the payment gateway type
	 */
	PaymentGatewayType getPaymentGatewayType();

	/**
	 * Get the type of this payment gateway - accessor for the discriminator value.
	 *
	 * @return the discriminator value of this gateway
	 */
	String getType();

	/**
	 * Sets the type.
	 *
	 * @param type the new type
	 */
	void setType(String type);

	/**
	 * Get the name of the payment gateway (e.g. CyberSource).
	 *
	 * @return the gateway name
	 */
	String getName();

	/**
	 * Set the name of the payment gateway (e.g. CyberSource).
	 *
	 * @param name the gateway name
	 */
	void setName(String name);

	/**
	 * Get the properties map of the payment gateway (e.g. merchantID, keysDirectory).
	 *
	 * @return the gateway properties map
	 */
	Map<String, PaymentGatewayProperty> getPropertiesMap();

	/**
	 * Set the properties map of the payment gateway (e.g. merchantID, keysDirectory).
	 *
	 * @param properties the gateway properties map
	 */
	void setPropertiesMap(Map<String, PaymentGatewayProperty> properties);

	/**
	 * Get the currencies supported by this payment gateway.
	 *
	 * @return a List of currency code strings (e.g. CAD)
	 */
	List<String> getSupportedCurrencies();

	/**
	 * Set the currencies supported by this payment gateway.
	 *
	 * @param currencies a List of currency code strings (e.g. CAD)
	 */
	void setSupportedCurrencies(List<String> currencies);

	/**
	 * Pre-authorize a payment.
	 *
	 * @param payment the payment to be preauthorized
	 * @param billingAddress the name and address of the person being billed
	 */
	void preAuthorize(OrderPayment payment, Address billingAddress);

	/**
	 * Captures a payment on a previously authorized card.
	 *
	 * @param payment the payment to be captured
	 */
	void capture(OrderPayment payment);

	/**
	 * Marks a transaction for immediate fund transfer without any pre-authorization. Note that
	 * Visa and Mastercard regulations prohibit capturing CC transaction funds until a product or
	 * service has been shipped to the buyer.
	 *
	 * @param payment the payment to be immediately processed
	 * @param billingAddress the name and address of the person being billed
	 */
	void sale(OrderPayment payment, Address billingAddress);

	/**
	 * Void a previous capture or credit. Can usually only be executed on the same day of the
	 * original transaction.
	 *
	 * @param payment the payment to be voided
	 */
	void voidCaptureOrCredit(OrderPayment payment);

	/**
	 * Reverse a previous pre-authorization. This can only be executed on Visas using the "Vital"
	 * processor and authorizations cannot be reversed using the test server and card info because
	 * the auth codes are not valid (Cybersource).
	 *
	 * @param payment the payment that was previously pre-authorized
	 */
	void reversePreAuthorization(OrderPayment payment);

	/**
	 * Refunds a previous capture or refunds to a stand-alone transaction.
	 *
	 * There are two type of refunds:
	 * - stand-alone - no previous capture is needed
	 * - follow-up - refunds towards a past capture
	 *
	 * @param payment the payment to be refunded
	 * @param billingAddress the billing address if the refund is of stand-alone type or null otherwise
	 */
	void refund(OrderPayment payment, Address billingAddress);

	/**
	 * Builds a properties object from the properties map. One difference from this and the
	 * properties map is that this will be a direct <code>String</code> -> <code>String</code>
	 * relationship. Changes in this object will not be reflected within the original properties
	 * map.
	 *
	 * @return A clone of the properties map in <code>String</code> -> <code>String</code> format
	 */
	Properties buildProperties();

	/**
	 * Merges the given properties with the existing properties map by adding each property to the
	 * property map. Each key and value will be casted to String via their <code>toString()</code>
	 * method.
	 *
	 * @param properties a properties object
	 */
	void mergeProperties(Properties properties);

	/**
	 * Sets the properties map with the given properties by overwriting the existing properties
	 * map. Each key and value will be casted to a <code>String</code> via their
	 * <code>toString()</code> method.
	 *
	 * @param properties a properties object
	 */
	void setProperties(Properties properties);

	/**
	 * Gateways should implement this if they need to finalize a shipment
	 * once all payment process has been completed.  This may include, for
	 * example, sending confirmation emails from external checkouts.
	 *
	 * @param orderShipment <CODE>OrderShipment</CODE> to be finalized.
	 */
	void finalizeShipment(OrderShipment orderShipment);

	/**
	 * Determines if the payment gateway plugin is installed.
	 *
	 * @return Boolean value representing whether the plugin is installed.
	 */
	boolean isPaymentGatewayPluginInstalled();

	/**
	 * Formats and signs all information that must be sent to payment gateway to acquire a token.
	 * @param tokenAcquireTransactionRequest the token acquire request
	 * @param billingAddress billing address fields
	 * @param finishExternalAuthUrl the EP controller URL that the payment gateway should direct the user to after token acquire
	 * @param cancelExternalAuthUrl the EP controller URL that the payment gateway should direct the user to if token acquire is canceled
	 * @return the external token acquire request form descriptor
	 */
	PaymentOptionFormDescriptor buildExternalTokenAcquireRequest(TokenAcquireTransactionRequest tokenAcquireTransactionRequest,
			Address billingAddress, String finishExternalAuthUrl, String cancelExternalAuthUrl);

	/**
	 * Handles external token acquire response.
	 *
	 * @param responseMap the response map
	 * @return the token acquire response
	 */
	TokenAcquireTransactionResponse handleExternalTokenAcquireResponse(Map<String, String> responseMap);

	/**
	 * Formats and signs all information that must be sent to payment gateway except any credit card info.
	 *
	 * @param orderPayment payment details
	 * @param billingAddress billing address fields
	 * @param redirectExternalAuthUrl the url that checkout should post to immediately before redirecting to the hosted
	 *     payment gateway form
	 * @param finishExternalAuthUrl the url that the gateway should call if the direct post authentication is successful
	 * @param cancelExternalAuthUrl the url that the gateway should call if the direct post authentication fails
	 * @return the external auth request form descriptor
	 */
	PaymentOptionFormDescriptor buildExternalAuthRequest(OrderPayment orderPayment, Address billingAddress,
			String redirectExternalAuthUrl, String finishExternalAuthUrl, String cancelExternalAuthUrl);
	
	/**
	 * Handles direct post auth response.
	 *
	 * @param paymentType the payment type
	 * @param responseMap the response map
	 * @return an updated order payment DTO
	 */
	OrderPayment handleExternalAuthResponse(PaymentType paymentType, Map<String, String> responseMap);
	
	/**
	 * Prepares for redirecting to the real payment gateway hosted order page.
	 *
	 * @param paymentType the payment type
	 * @param responseMap the response map
	 * @return PaymentOptionFormDescriptor
	 */
	PaymentOptionFormDescriptor prepareForRedirect(PaymentType paymentType, Map<String, String> responseMap);

	/**
	 * Determine if the associated payment gateway plugin supports the passed capability.
	 * @param capabilityClass the capability to interrogate about
	 * @return true if payment gateway supports passed capability
	 */
	boolean supportsCapability(Class<? extends PaymentGatewayCapability> capabilityClass);
}
