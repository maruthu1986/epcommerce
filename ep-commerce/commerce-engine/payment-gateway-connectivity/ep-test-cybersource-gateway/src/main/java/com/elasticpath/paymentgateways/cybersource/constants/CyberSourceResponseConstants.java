/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.paymentgateways.cybersource.constants;

/**
 * Constant values for Cybersource decision fields.
 */
public final class CyberSourceResponseConstants {
	/** ACCEPT decision. */
	public static final String DECISION_ACCEPT = "ACCEPT";

	/** REJECT decision. */
	public static final String DECISION_REJECT = "REJECT";

	/** ERROR decision. */
	public static final String DECISION_ERROR = "ERROR";

	/** Invalid data response code. **/
	public static final String INVALID_DATA_RESPONSE_CODE = "102";
	
	/** Card credit exceeded response code. **/
	public static final String CARD_CREDIT_EXCEEDED_RESPONSE_CODE = "210";
	
	/** General decline response code. **/
	public static final String GENERAL_DECLINE_RESPONSE_CODE = "203";
	
	/** Insufficient funds response code. **/
	public static final String INSUFFICIENT_FUNDS_RESPONSE_CODE = "204";
	
	/** Card expired response code. **/
	public static final String CARD_EXPIRED_RESPONSE_CODE = "202";
	
	/** CV check failed response code. **/
	public static final String CV_CHECK_FAILED_RESPONSE_CODE = "230";
	
	/** Decision CyberSource field. */
	public static final String DECISION = "decision";

	/** Reason code CyberSource field. */
	public static final String REASON_CODE = "reasonCode";
	
	/** Invalid field CyberSource field. */
	public static final String INVALID_FIELD = "invalidField_";
	
	/** Cybersource subscription reply id. **/
	public static final String SUBSCRIPTION_REPLY_ID = "paySubscriptionCreateReply_subscriptionID";
	
	/** Cybersource reply request token. */
	public static final String REQUEST_TOKEN = "requestToken";
	
	/** Cybersource reply request id. */
	public static final String REQUEST_ID = "requestID";

	/** Response Code for auth amount exceeded. */
	public static final String AUTH_AMOUNT_EXCEEDED_RESPONSE_CODE = "235";

	/** Response code for auth already reversed. */
	public static final String AUTH_ALREADY_REVERSED_RESPONSE_CODE = "237";

	/** Response Code for success. */
	public static final String SUCCESSFUL_RESPONSE_CODE = "100";

	/** Response Code for system error. */
	public static final String SYS_ERROR_RESPONSE_CODE = "150";

	/** Auth reply amount. */
	public static final String CC_AUTH_REPLY_AMOUNT = "ccAuthReply_amount";

	/** Capture reply amount. */
	public static final String CC_CAPTURE_REPLY_AMOUNT = "ccCaptureReply_amount";

	/** Auth reversal reply amount. */
	public static final String CC_AUTH_REVERSAL_REPLY_AMOUNT = "ccAuthReversalReply_amount";

	private CyberSourceResponseConstants() {
		// Do not instantiate this class
	}
}
