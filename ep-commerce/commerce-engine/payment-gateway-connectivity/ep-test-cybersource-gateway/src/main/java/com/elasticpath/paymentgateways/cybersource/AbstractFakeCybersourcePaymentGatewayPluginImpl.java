/*
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.paymentgateways.cybersource;

import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.CC_AUTH_REVERSAL_SERVICE_AUTH_REQUEST_ID;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.CC_AUTH_REVERSAL_SERVICE_RUN;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.CC_CAPTURE_SERVICE_AUTH_REQUEST_ID;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.CC_CAPTURE_SERVICE_AUTH_REQUEST_TOKEN;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.CC_CAPTURE_SERVICE_RUN;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.CC_CREDIT_SERVICE_RUN;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.MERCHANT_ID;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.MERCHANT_REFERENCE_CODE;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.PAYER_AUTH_ENROLL_SERVICE_RUN;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.PAYER_AUTH_VALIDATE_SERVICE_RUN;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.PURCHASE_TOTALS_CURRENCY;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.PURCHASE_TOTALS_GRAND_TOTAL_AMOUNT;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.REQUEST_ID;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.REQUEST_TOKEN;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceRequestFields.VOID_SERVICE_RUN;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceResponseConstants.AUTH_AMOUNT_EXCEEDED_RESPONSE_CODE;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceResponseConstants.CARD_CREDIT_EXCEEDED_RESPONSE_CODE;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceResponseConstants.CARD_EXPIRED_RESPONSE_CODE;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceResponseConstants.CC_CAPTURE_REPLY_AMOUNT;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceResponseConstants.CV_CHECK_FAILED_RESPONSE_CODE;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceResponseConstants.DECISION;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceResponseConstants.DECISION_ACCEPT;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceResponseConstants.DECISION_ERROR;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceResponseConstants.DECISION_REJECT;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceResponseConstants.GENERAL_DECLINE_RESPONSE_CODE;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceResponseConstants.INSUFFICIENT_FUNDS_RESPONSE_CODE;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceResponseConstants.REASON_CODE;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceResponseConstants.SUCCESSFUL_RESPONSE_CODE;
import static com.elasticpath.paymentgateways.cybersource.constants.CyberSourceResponseConstants.SYS_ERROR_RESPONSE_CODE;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import com.elasticpath.plugin.payment.capabilities.CaptureCapability;
import com.elasticpath.plugin.payment.capabilities.RefundCapability;
import com.elasticpath.plugin.payment.capabilities.ReversePreAuthorizationCapability;
import com.elasticpath.plugin.payment.capabilities.VoidCaptureCapability;
import com.elasticpath.plugin.payment.dto.AddressDto;
import com.elasticpath.plugin.payment.dto.MoneyDto;
import com.elasticpath.plugin.payment.dto.OrderPaymentDto;
import com.elasticpath.plugin.payment.dto.PayerAuthenticationEnrollmentResultDto;
import com.elasticpath.plugin.payment.dto.PaymentMethod;
import com.elasticpath.plugin.payment.dto.ShoppingCartDto;
import com.elasticpath.plugin.payment.dto.impl.MoneyDtoImpl;
import com.elasticpath.plugin.payment.dto.impl.PayerAuthenticationEnrollmentResultDtoImpl;
import com.elasticpath.plugin.payment.exceptions.AuthorizedAmountExceededException;
import com.elasticpath.plugin.payment.exceptions.CardDeclinedException;
import com.elasticpath.plugin.payment.exceptions.CardErrorException;
import com.elasticpath.plugin.payment.exceptions.CardExpiredException;
import com.elasticpath.plugin.payment.exceptions.InsufficientFundException;
import com.elasticpath.plugin.payment.exceptions.PaymentGatewayException;
import com.elasticpath.plugin.payment.spi.AbstractCreditCardPaymentGatewayPluginSPI;
import com.elasticpath.plugin.payment.transaction.CaptureTransactionRequest;
import com.elasticpath.plugin.payment.transaction.CaptureTransactionResponse;

/**
 * Abstract Cybersource payment gateway implementation.
 */
@SuppressWarnings({"PMD.GodClass", "PMD.ExcessiveImports"})
public abstract class AbstractFakeCybersourcePaymentGatewayPluginImpl extends AbstractCreditCardPaymentGatewayPluginSPI
		implements CaptureCapability, RefundCapability, ReversePreAuthorizationCapability, VoidCaptureCapability {

	/** Determines the number of digits in the random portion of the reference code. */
	private static final int RANDOM_NUMBER_MULTIPLIER = 1000;

	/** Serial version id. */
	public static final long serialVersionUID = 5000000001L;

	private static final String CYBERSOURCE_REASON_CODE = "Cybersource Reason Code: ";

	private static final Logger LOG = Logger.getLogger(AbstractFakeCybersourcePaymentGatewayPluginImpl.class);

	private static final String TRUE = "true";

	private static final String KEYS_DIRECTORY = "keysDirectory";

	private static final String PAYER_AUTH_ENROLL = "475";

	private static final String PAYER_AUTH_ACS_URL = "payerAuthEnrollReply_acsURL";

	private static final String PAYER_AUTH_PAREQ = "payerAuthEnrollReply_paReq";

	private Map<String, String> supportedCardTypes;

	/**
	 * Sets the reference code for the given <code>OrderPayment</code>.
	 *
	 * @param request the map specifying the parameters of the Cybersource request
	 */
	protected void setRequestReferenceCode(final Map<String, String> request) {
		request.put(MERCHANT_REFERENCE_CODE, this.generateReferenceCode());
	}

	/**
	 * Reverse a previous pre-authorization. This can only be executed on Visas using the "Vital"
	 * processor and authorizations cannot be reversed using the test server and card info because
	 * the auth codes are not valid (Cybersource).
	 *
	 * @param payment the payment that was previously pre-authorized
	 * @throws CardExpiredException if the card has expired
	 * @throws CardErrorException if there was an error processing the given information
	 * @throws PaymentGatewayException if the payment processing fails
	 */
	@Override
	public void reversePreAuthorization(final OrderPaymentDto payment) {
		final HashMap<String, String> request = new HashMap<>();

		request.put(CC_AUTH_REVERSAL_SERVICE_RUN, TRUE);

		request.put(CC_AUTH_REVERSAL_SERVICE_AUTH_REQUEST_ID, payment.getAuthorizationCode());
		request.put(PURCHASE_TOTALS_CURRENCY, payment.getCurrencyCode());
		request.put(PURCHASE_TOTALS_GRAND_TOTAL_AMOUNT, convertToString(payment.getAmount()));
		request.put(MERCHANT_ID, getConfigurationValues().get(MERCHANT_ID));
		request.put(REQUEST_ID, payment.getAuthorizationCode());
		request.put(REQUEST_TOKEN, payment.getRequestToken());

		setRequestReferenceCode(request);

		Map<String, String> transactionReply = runTransaction(payment, request);

		updateOrderPaymentWithFollowOnRequestFields(payment, transactionReply);
	}


	@Override
	public void voidCaptureOrCredit(final OrderPaymentDto payment) {
		// Do not attempt void on sandbox because it will always fail with error code 246.
		// See http://stackoverflow.com/questions/12949395/void-transaction-attempt-for-cybersource-gives-error.
		if ("true".equalsIgnoreCase(getConfigurationValues().get("sendToProduction"))) {
			final HashMap<String, String> request = new HashMap<>();

			request.put(VOID_SERVICE_RUN, TRUE);

			request.put("voidService_voidRequestID", payment.getAuthorizationCode());
			request.put("voidService_voidRequestToken", payment.getRequestToken());
			request.put(MERCHANT_ID, getConfigurationValues().get(MERCHANT_ID));

			request.put(REQUEST_ID, payment.getAuthorizationCode());
			request.put(REQUEST_TOKEN, payment.getRequestToken());

			Map<String, String> transactionReply = runTransaction(payment, request);

			updateOrderPaymentWithFollowOnRequestFields(payment, transactionReply);
		}
	}

	@Override
	public CaptureTransactionResponse capture(final CaptureTransactionRequest captureTransactionRequest) {
		final HashMap<String, String> request = new HashMap<>();

		request.put(CC_CAPTURE_SERVICE_RUN, TRUE);
		request.put(CC_CAPTURE_SERVICE_AUTH_REQUEST_ID, captureTransactionRequest.getAuthorizationCode());
		request.put(CC_CAPTURE_SERVICE_AUTH_REQUEST_TOKEN, captureTransactionRequest.getRequestToken());

		MoneyDto money = captureTransactionRequest.getMoney();
		request.put(PURCHASE_TOTALS_CURRENCY, money.getCurrencyCode());
		request.put(PURCHASE_TOTALS_GRAND_TOTAL_AMOUNT, convertToString(money.getAmount()));
		request.put(MERCHANT_ID, getConfigurationValues().get(MERCHANT_ID));
		request.put(MERCHANT_REFERENCE_CODE, captureTransactionRequest.getReferenceId());
		request.put(REQUEST_ID, captureTransactionRequest.getAuthorizationCode());
		request.put(REQUEST_TOKEN, captureTransactionRequest.getRequestToken());

		Map<String, String> captureReply = runTransaction(null, request);
		String captureAmount = captureReply.get(CC_CAPTURE_REPLY_AMOUNT);
		String captureCurrency = captureReply.get(PURCHASE_TOTALS_CURRENCY);
		String requestToken = captureReply.get(REQUEST_TOKEN);
		String authorizationCode = captureReply.get(REQUEST_ID);

		return createCaptureResponse(new MoneyDtoImpl(new BigDecimal(captureAmount), captureCurrency), requestToken, authorizationCode,
				captureTransactionRequest.getReferenceId());
	}


	private void updateOrderPaymentWithFollowOnRequestFields(final OrderPaymentDto payment, final Map<String, String> transactionReply) {
		payment.setAuthorizationCode(transactionReply.get(REQUEST_ID));
		payment.setRequestToken(transactionReply.get(REQUEST_TOKEN));
	}

	/**
	 * Executes the transaction.
	 *
	 * @param payment the payment to be authorized, captured, etc.
	 * @param request the request parameters
	 * @return PaymentTransactionResponse the response from the gateway
	 */
	protected Map<String, String> runTransaction(final PaymentMethod payment, final Map<String, String> request) {
		displayMap("PAYMENT GATEWAY REQUEST:", request);

		// run transaction
		Map<String, String> reply = runTransaction(request);

		displayMap("PAYMENT GATEWAY REPLY:", reply);

		final String decision = reply.get(DECISION);

		if (!decision.equalsIgnoreCase(DECISION_ACCEPT)) {
			processRejection(reply);
		}

		return reply;
	}

	/**
	 * Process a rejected transaction's reply map.
	 *
	 * @param reply the reply
	 * @throws CardDeclinedException if the card is declined
	 * @throws CardExpiredException if the card has expired
	 * @throws CardErrorException if there was an error processing the given information
	 */
	private void processRejection(final Map<String, String> reply) throws CardErrorException {
		/*
		 * When a card is rejected, the Payment Processor supplies its own reason for the
		 * rejection, in the ccAuthReply_processorResponse string. In addition, there is a reason
		 * code, which is likely supplied by CyberSource. The two numbers are likely to be
		 * different, and the payment processor's response string may not always be translated
		 * into the equivalent CyberSource reason code. However, we use the CyberSource reason
		 * code.
		 */
		final String decision = reply.get(DECISION);
		final String reason = reply.get(REASON_CODE);

		if (decision.equalsIgnoreCase(DECISION_REJECT)) {
			final String exceptionMsg = "CYBERSOURCE_REASON_CODE:" + CYBERSOURCE_REASON_CODE + " reason: " + reason;
			if (reason.equalsIgnoreCase(CARD_EXPIRED_RESPONSE_CODE)) {
				LOG.info("CardExpiredException: " + exceptionMsg);
				throw new CardExpiredException(CYBERSOURCE_REASON_CODE + reason);
			} else if (reason.equalsIgnoreCase(GENERAL_DECLINE_RESPONSE_CODE) || reason.equalsIgnoreCase(CV_CHECK_FAILED_RESPONSE_CODE)) {
				LOG.info("CardDeclinedException: " + exceptionMsg);
				throw new CardDeclinedException(CYBERSOURCE_REASON_CODE + reason);
			} else if (reason.equalsIgnoreCase(INSUFFICIENT_FUNDS_RESPONSE_CODE) || reason.equalsIgnoreCase(CARD_CREDIT_EXCEEDED_RESPONSE_CODE)) {
				LOG.info("InsufficientFundException: " + exceptionMsg);
				throw new InsufficientFundException(CYBERSOURCE_REASON_CODE + reason);
			} else if (reason.equalsIgnoreCase(AUTH_AMOUNT_EXCEEDED_RESPONSE_CODE)) {
				LOG.info("Authorized amount exceeded: " + exceptionMsg);
				throw new AuthorizedAmountExceededException(AUTH_AMOUNT_EXCEEDED_RESPONSE_CODE + reason);
			}

			LOG.info("CardErrorException: CYBERSOURCE_REASON_CODE:" + CYBERSOURCE_REASON_CODE + " reason: " + reason);
			throw new CardErrorException(CYBERSOURCE_REASON_CODE + reason);

		} else if (decision.equalsIgnoreCase(DECISION_ERROR)) {

			LOG.error("PaymentGatewayException: CyberSource Processor Error");
			throw new PaymentGatewayException("CyberSource Processor Error");
		}
	}

	/**
	 * Runs the transaction on the Cybersource client. This method is used as a hook for JUnit
	 * testing.
	 *
	 * @param request a Map of request key value pairs
	 * @return a result Map of key value pairs
	 */
	@SuppressWarnings("unchecked")
	Map<String, String> runTransaction(final Map<String, String> request) {
		final Properties props = buildProperties();
		final String fullPath = FilenameUtils.concat(getCertificatePathPrefix(), props.getProperty(KEYS_DIRECTORY));
		props.setProperty(KEYS_DIRECTORY, fullPath);
		if (LOG.isDebugEnabled()) {
			LOG.debug("Using certificate key directory: " + fullPath);
		}
		return TestCybersourceClient.runTransaction(request, props);
	}

	private Properties buildProperties() {
		final Properties prop = new Properties();
		for (final Entry<String, String> entry : this.getConfigurationValues().entrySet()) {
			String value = entry.getValue();
			if (value == null) {
				value = "";
			}
			prop.setProperty(entry.getKey(), value);
		}
		return prop;
	}

	/**
	 * Displays the contents of a request or response map for debugging.
	 *
	 * @param header Header text describing the transaction.
	 * @param parameterMap Map object to display containing request or response parameters.
	 */
	private void displayMap(final String header, final Map<String, String> parameterMap) {

		LOG.warn(header);

		final StringBuilder outputBuffer = new StringBuilder();

		if (parameterMap != null && !parameterMap.isEmpty()) {
			for (final Entry<String, String> element : parameterMap.entrySet()) {
				final String key = element.getKey();
				final String value = element.getValue();
				outputBuffer.append(key).append('=').append(value).append('\n');
			}
		}

		LOG.warn(outputBuffer.toString());
	}

	/**
	 * Returns a unique reference code for a payment transaction. The reference code is composed
	 * of a three-digit random number, followed by the current date/time in milliseconds. The
	 * three digit random number minimizes the possibility of duplicate reference codes if two are
	 * processed during the same millisecond.
	 *
	 * @return the unique reference code
	 */
	private String generateReferenceCode() {
		return String.valueOf((Math.random() * RANDOM_NUMBER_MULTIPLIER) + (new Date()).getTime());
	}

	/**
	 * Get the card types supported by this payment gateway.
	 *
	 * @return a List of card type strings (e.g. VISA) supported by this payment gateway.
	 */
	@Override
	public List<String> getSupportedCardTypes() {
		if (supportedCardTypes == null) {
			supportedCardTypes = this.getDefaultSupportedCardTypes();
		}
		return getDefaultSupportedCardTypesValues(supportedCardTypes);
	}

	/**
	 * Get the List of card type strings in the given supportedCardTypesMap Map.
	 *
	 * @param supportedCardTypesMap a Map of all code-card combinations supported by CyberSource.
	 * @return a List of card type strings supported by CyberSource.
	 */
	private List<String> getDefaultSupportedCardTypesValues(final Map<String, String> supportedCardTypesMap) {
		final List<String> supportedCardTypesList = new ArrayList<>();
		supportedCardTypesList.addAll(supportedCardTypesMap.keySet());
		return supportedCardTypesList;
	}

	/**
	 * Get the card types supported by this payment gateway.
	 *
	 * @return a Map of all code-card combinations supported by CyberSource
	 */
	protected Map<String, String> getDefaultSupportedCardTypes() {
		final Map<String, String> cardTypesMap = new HashMap<>();
		cardTypesMap.put("Visa", "001");
		cardTypesMap.put("MasterCard", "002");
		cardTypesMap.put("Eurocard", "002");
		cardTypesMap.put("American Express", "003");
		cardTypesMap.put("Discover", "004");
		cardTypesMap.put("Diners Club", "005");
		cardTypesMap.put("Carte Blanche", "006");
		cardTypesMap.put("JCB", "007");
		cardTypesMap.put("EnRoute", "014");
		cardTypesMap.put("JAL", "021");
		//cardTypesMap.put("Maestro", "024");
		//cardTypesMap.put("Solo", "024");
		cardTypesMap.put("Delta", "031");
		cardTypesMap.put("Visa Electron", "033");
		cardTypesMap.put("Dankort", "034");
		cardTypesMap.put("Laser", "035");
		cardTypesMap.put("Carte Bleue", "036");
		cardTypesMap.put("Carta Si", "037");
		//cardTypesMap.put("Maestro", "042");

		return cardTypesMap;
	}

	/**
	 * Check the card account enrollment.
	 * @param shoppingCart the shoppingCart
	 * @param payment orderPayment.
	 * @return result of enrollment checking.
	 */
	@Override
	public PayerAuthenticationEnrollmentResultDto checkEnrollment(final ShoppingCartDto shoppingCart, final OrderPaymentDto payment) {
		final PayerAuthenticationEnrollmentResultDto payerAuthEnrollmentValue = new PayerAuthenticationEnrollmentResultDtoImpl();

		final HashMap<String, String> request = new HashMap<>();
		request.put(PAYER_AUTH_ENROLL_SERVICE_RUN, TRUE);
		request.put(MERCHANT_ID, getConfigurationValues().get(MERCHANT_ID));

		request.put(PURCHASE_TOTALS_CURRENCY, shoppingCart.getCurrencyCode());

		displayMap("CREDIT CARD 3-D SECURE ENROLLMENT REQUEST:", request);

		// run transaction
		final Map<String, String> reply = runTransaction(request);

		displayMap("CREDIT CARD 3-D SECURE ENROLLMENT REPLY:", reply);

		final String decision = reply.get(DECISION);
		final String reason = reply.get(REASON_CODE);

		if (decision.equals(DECISION_REJECT) && reason.equals(PAYER_AUTH_ENROLL)) {
			payerAuthEnrollmentValue.setEnrolled(true);
			final String acsURL = reply.get(PAYER_AUTH_ACS_URL);
			payerAuthEnrollmentValue.setAcsURL(acsURL);
			final String pareq = reply.get(PAYER_AUTH_PAREQ);
			payerAuthEnrollmentValue.setPaREQ(pareq);
		}
		return payerAuthEnrollmentValue;
	}

	/**
	 * Validate the authentication.
	 * @param payment orderPayment.
	 * @param paRes from issuing bank.
	 * @return boolean successful value for validation.
	 */
	@Override
	public boolean validateAuthentication(final OrderPaymentDto payment, final String paRes) {

		final HashMap<String, String> request = new HashMap<>();
		request.put(PAYER_AUTH_VALIDATE_SERVICE_RUN, TRUE);
		request.put(MERCHANT_ID, getConfigurationValues().get(MERCHANT_ID));


		request.put(PURCHASE_TOTALS_CURRENCY, payment.getCurrencyCode());

		request.put("payerAuthValidateService_signedPARes", paRes);

		displayMap("CREDIT CARD 3-D SECURE AUTHENTICATION REQUEST:", request);

		// run transaction
		final Map<String, String> reply = runTransaction(request);

		displayMap("CREDIT CARD 3-D SECURE AUTHENTICATION REPLY:", reply);

		final String reason = reply.get(REASON_CODE);

		if (!reason.equals(SUCCESSFUL_RESPONSE_CODE) && reason.equals(SYS_ERROR_RESPONSE_CODE)) {
			throw new PaymentGatewayException("CyberSource Processor Error");
		}
		return true;
	}

	/**
	 * Refunds a certain amount to a credit card.
	 *
	 * @param payment the order payment
	 * @param billingAddress the billing address
	 */
	@Override
	public void refund(final OrderPaymentDto payment, final AddressDto billingAddress) {
		final HashMap<String, String> request = new HashMap<>();

		if (payment.getAuthorizationCode() == null) { // refund to a new credit card (stand-alone credit)
			if (billingAddress == null) {
				throw new PaymentGatewayException("Can not refund a stand-alone payment without billing address defined");
			}
			// Billing Address
			request.put("billTo_firstName", billingAddress.getFirstName());
			request.put("billTo_lastName", billingAddress.getLastName());
			request.put("billTo_street1", billingAddress.getStreet1());
			request.put("billTo_city", billingAddress.getCity());

			if (billingAddress.getSubCountry() != null) {
				request.put("billTo_state", billingAddress.getSubCountry());
			}

			request.put("billTo_postalCode", billingAddress.getZipOrPostalCode());
			request.put("billTo_country", billingAddress.getCountry());
			request.put("billTo_email", payment.getEmail());

		} else { // refund to an already existing capture payment (follow-up credit)
			request.put(CC_CAPTURE_SERVICE_AUTH_REQUEST_ID, payment.getAuthorizationCode());
			request.put(CC_CAPTURE_SERVICE_AUTH_REQUEST_TOKEN, payment.getRequestToken());
		}

		request.put(CC_CREDIT_SERVICE_RUN, TRUE);
		request.put(PURCHASE_TOTALS_CURRENCY, payment.getCurrencyCode());
		request.put(PURCHASE_TOTALS_GRAND_TOTAL_AMOUNT, convertToString(payment.getAmount()));
		request.put(MERCHANT_ID, getConfigurationValues().get(MERCHANT_ID));
		request.put(REQUEST_ID, payment.getAuthorizationCode());
		request.put(REQUEST_TOKEN, payment.getRequestToken());

		Map<String, String> transactionReply = runTransaction(payment, request);

		updateOrderPaymentWithFollowOnRequestFields(payment, transactionReply);
	}

	@Override
	public Collection<String> getConfigurationParameters() {
		final Collection<String> parameters = new ArrayList<>();
		parameters.add(MERCHANT_ID);
		parameters.add(KEYS_DIRECTORY);
		parameters.add("targetAPIVersion");
		parameters.add("sendToProduction");
		parameters.add("logMaximumSize");
		parameters.add("enableLog");
		parameters.add("logDirectory");
		return parameters;
	}

}
