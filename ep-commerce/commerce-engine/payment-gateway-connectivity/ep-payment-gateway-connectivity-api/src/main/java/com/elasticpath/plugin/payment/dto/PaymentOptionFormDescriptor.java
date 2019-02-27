/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.plugin.payment.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elasticpath.plugin.payment.PaymentType;

/**
 * Represents the data needed to construct the html form that will post directly to the payment gateway.
 */
public class PaymentOptionFormDescriptor {

	private PaymentType paymentType;
	private String formId;
	private String messageKey;
	private final List<PaymentOptionFormRowDescriptor> visibleFormRows = new ArrayList<>();
	private final Map<String, String> hiddenFormFieldMap = new HashMap<>();
	private final Map<String, String> sessionValuesMap = new HashMap<>();
	private String postActionUrl;
	private String postActionMimeType;

	/**
	 * The type of payment this form descriptor enables.
	 * @return the payment type
	 */
	public PaymentType getPaymentType() {
		return paymentType;
	}

	/**
	 * The type of payment this form descriptor enables.
	 * @param paymentType the payment type
	 */
	void setPaymentType(final PaymentType paymentType) {
		this.paymentType = paymentType;
	}

	/**
	 * A unique identifier for the payment option.
	 * @return the form id
	 */
	public String getFormId() {
		return formId;
	}

	/**
	 * A unique identifier for the payment option.
	 * @param formId unique identifier
	 */
	void setFormId(final String formId) {
		this.formId = formId;
	}

	/**
	 * The key for the localized version of the message to display to the user for this payment option.
	 * @return the message key
	 */
	public String getMessageKey() {
		return messageKey;
	}

	/**
	 * The key for the localized version of the message to display to the user for this payment option.
	 * @param messageKey the message key
	 */
	void setMessageKey(final String messageKey) {
		this.messageKey = messageKey;
	}

	/**
	 * Returns a List of rows in the HTML form table that contain visible fields.
	 *
	 * @return the visible form rows List
	 */
	public List<PaymentOptionFormRowDescriptor> getVisibleFormRows() {
		return visibleFormRows;
	}

	/**
	 * Returns a Map that contains hidden form fields required by the payment gateway when the form is posted.
	 * The map key represents the hidden field id, and the map value represents the hidden field value.
	 *
	 * @return the hidden form field map
	 */
	public Map<String, String> getHiddenFormFieldMap() {
		return hiddenFormFieldMap;
	}

	/**
	 * A map of session values that will be returned as part of the handle response call.
	 *
	 * @return the session value map
	 */
	public Map<String, String> getSessionValuesMap() {
		return sessionValuesMap;
	}

	/**
	 * The external URL to post the request.
	 *
	 * @return the external url
	 */
	public String getPostActionUrl() {
		return postActionUrl;
	}

	/**
	 * Sets the external post action url.
	 *
	 * @param postActionUrl the url
	 */
	public void setPostActionUrl(final String postActionUrl) {
		this.postActionUrl = postActionUrl;
	}

	/**
	 * Mime type of the data to be posted to the post action url.
	 * Usually "application/x-www-form-urlencoded" or "application/json".
	 * @return mime type of the data to be posted
	 */
	public String getPostActionMimeType() {
		return postActionMimeType;
	}

	/**
	 * Mime type of the data to be posted to the post action url.
	 * Usually "application/x-www-form-urlencoded" or "application/json".
	 * @param postActionMimeType  mime type of the data to be posted
	 */
	public void setPostActionMimeType(final String postActionMimeType) {
		this.postActionMimeType = postActionMimeType;
	}
}
