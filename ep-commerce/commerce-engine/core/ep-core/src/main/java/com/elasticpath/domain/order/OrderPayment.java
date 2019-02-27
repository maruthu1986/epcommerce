/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.domain.order;

import java.math.BigDecimal;
import java.util.Date;

import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.customer.PaymentToken;
import com.elasticpath.domain.misc.PayerAuthValidationValue;
import com.elasticpath.money.Money;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.plugin.payment.PaymentType;

/**
 * Represents customer payment information.
 */
public interface OrderPayment extends Persistable {

	/**
	 * The orderPayment transaction type of fund authorization.
	 */
	String AUTHORIZATION_TRANSACTION = "Authorization";

	/**
	 * The orderPayment transaction type of fund capture.
	 */
	String CAPTURE_TRANSACTION = "Capture";

	/**
	 * The orderPayment transaction type of fund verification.
	 * @deprecated This transaction type should be removed when the old Paypal payment gateway is removed.
	*/
	@Deprecated
	String ORDER_TRANSACTION = "Order";

	/**
	 * The orderPayment transaction type of refund.
	 */
	String CREDIT_TRANSACTION = "Credit";

	/**
	 * The orderPayment transaction type of reverse authorization.
	 */
	String REVERSE_AUTHORIZATION = "Authorization Reversal";

	/**
	 * Get the related order.
	 *
	 * @return the order
	 */
	Order getOrder();

	/**
	 * Set the order for the payment.
	 *
	 * @param order the order to set
	 */
	void setOrder(Order order);

	/**
	 * Get the related orderShipment. The orderShipment maybe null when the payment is on the order level and not related to a shipment.
	 *
	 * @return the orderShipment
	 */
	OrderShipment getOrderShipment();

	/**
	 * Set the orderShipment for the payment.
	 *
	 * @param orderShipment the orderShipment to set
	 */
	void setOrderShipment(OrderShipment orderShipment);

	/**
	 * Get the date that this order was created on.
	 *
	 * @return the created date
	 */
	Date getCreatedDate();

	/**
	 * Set the date that the order is created.
	 *
	 * @param createdDate the start date
	 */
	void setCreatedDate(Date createdDate);

	/**
	 * Get the date that this was last modified on.
	 *
	 * @return the last modified date
	 */
	Date getLastModifiedDate();

	/**
	 * Returns the display value for the payment method used to create this order payment, for example the display value
	 * of a token.
	 *
	 * @return the display value of the payment method
	 */
	String getDisplayValue();

	/**
	 * Sets the display value for the payment method used to create this order payment, for example the display value
	 * of a token.
	 *
	 * @param displayValue the display value of the payment method
	 */
	void setDisplayValue(String displayValue);

	/**
	 * Get the payment method. A payment method could be the name of the payment processor/gateway.
	 *
	 * @return the payment method
	 */
	PaymentType getPaymentMethod();

	/**
	 * Set the payment method.
	 *
	 * @param paymentMethod the payment method
	 */
	void setPaymentMethod(PaymentType paymentMethod);

	/**
	 * Get the amount of this payment.
	 *
	 * @return the amount as a BigDecimal
	 */
	BigDecimal getAmount();

	/**
	 * Set the amount of this payment.
	 *
	 * @param amount the amount as a BigDecimal
	 */
	void setAmount(BigDecimal amount);

	/**
	 * Get the reference id. The reference ID is basically a merchant reference code, and is usually set to the Order Number associated with this
	 * payment.
	 *
	 * @return the reference id.
	 */
	String getReferenceId();

	/**
	 * Set the reference id. The reference ID is basically a merchant reference code, and is usually set to the Order Number
	 * or shipment number associated with this payment.
	 *
	 * @param referenceId the reference id.
	 */
	void setReferenceId(String referenceId);

	/**
	 * Get the requestToken. The request token is a code returned by the payment processor for every request. It is used to associate any transaction
	 * with its associated follow-on transaction, such as a capture transaction with its preceding preauthorization, much like the requestId.
	 *
	 * @return the request token.
	 */
	String getRequestToken();

	/**
	 * Set the request token.
	 *
	 * @param requestToken the request token
	 */
	void setRequestToken(String requestToken);

	/**
	 * Get the authorization code, returned with every transaction. It is used to associate any transaction with its associated follow-on
	 * transaction, such as a capture transaction with its preceding preauthorization, much like the requestToken.
	 *
	 * @return the authorization code
	 */
	String getAuthorizationCode();

	/**
	 * Set the authorization code, returned with every transaction.
	 *
	 * @param authorizationCode the authorization code
	 */
	void setAuthorizationCode(String authorizationCode);

	/**
	 * Get the currency code (e.g. CAD or USD).
	 *
	 * @return the currency code
	 */
	String getCurrencyCode();

	/**
	 * Set the currency code.
	 *
	 * @param currencyCode the currency code code
	 */
	void setCurrencyCode(String currencyCode);

	/**
	 * Get the customer's email address (Required for card processing).
	 *
	 * @return the customer email address
	 */
	String getEmail();

	/**
	 * Set the customer's email address (Required for card processing).
	 *
	 * @param email the customer's email address
	 */
	void setEmail(String email);

	/**
	 * Get the payment transaction type, i.e. "Authorization", "Sale" or "Credit".
	 *
	 * @return the payment transaction type
	 */
	String getTransactionType();

	/**
	 * Get the payment transaction type, i.e. "Authorization", "Sale" or "Credit".
	 *
	 * @param transactionType the payment transaction type
	 */
	void setTransactionType(String transactionType);

	/**
	 * Get the status of the order payment.
	 *
	 * @return the order payment status
	 */
	OrderPaymentStatus getStatus();

	/**
	 * Set the status of the order payment.
	 *
	 * @param status the status of the order payment
	 */
	void setStatus(OrderPaymentStatus status);

	/**
	 * Store the temporary token needed by some payment gateway, i.e. PayPal to complete the process.
	 *
	 * @param token payment gateway token
	 */
	void setGatewayToken(String token);

	/**
	 * Return the payment gateway token.
	 *
	 * @return the temproary payment gateway token.
	 */
	String getGatewayToken();

	/**
	 * Get the ipAddress of the user from the Order Payment.
	 *
	 * @return the ipAddress
	 */
	String getIpAddress();

	/**
	 * Set the users ip Address into the Order Payment.
	 *
	 * @param ipAddress the ipAddress of the user.
	 */
	void setIpAddress(String ipAddress);

	/**
	 * Get the gift certificate for the payment.
	 *
	 * @return the giftCertificate
	 */
	GiftCertificate getGiftCertificate();

	/**
	 * Set the gift certificate for the payment.
	 *
	 * @param giftCertificate the giftCertificate to set
	 */
	void setGiftCertificate(GiftCertificate giftCertificate);

	/**
	 * Copy follow on transaction information from orderPayment to <code>this</code> Order Payment.
	 *
	 * @param orderPayment the source of Gateway info.
	 */
	void copyTransactionFollowOnInfo(OrderPayment orderPayment);

	/**
	 * Get the total amount money.
	 *
	 * @return a <code>Money</code> object representing the total amount
	 */
	Money getAmountMoney();

	/**
	 * Get Payer Authentication Validate value for transaction.
	 *
	 * @return the payerAuthValidationValue.
	 */
	PayerAuthValidationValue getPayerAuthValidationValue();

	/**
	 * Set the payerAuthValidationValue for order payment.
	 *
	 * @param payerAuthValidationValue the payerAuthValidationValue
	 */
	void setPayerAuthValidationValue(PayerAuthValidationValue payerAuthValidationValue);

	/**
	 * Copies the date that this was last modified.
	 *
	 * @param authorizationPayment the payment to copy from
	 */
	void copyLastModifiedDate(OrderPayment authorizationPayment);

	/**
	 * Returns true if this payment is done in the subscriptions context. See SUBS-113.
	 * @return payment for subscriptions
	 * */
	boolean isPaymentForSubscriptions();

	/**
	 * Sets true if the payment if for subscriptions.
	 * @param paymentForSubscriptions the payment for subscriptions
	 * */
	void setPaymentForSubscriptions(boolean paymentForSubscriptions);

	/**
	 * Copies the relevant information across from the provided payment token into this object.
	 * @param paymentToken the payment token to use as a source
	 */
	void usePaymentToken(PaymentToken paymentToken);

	/**
	 * Copies relevant fields of this object into a {@link PaymentToken}.
	 * @return a newly created and populated {@link PaymentToken}
	 */
	PaymentToken extractPaymentToken();

}
