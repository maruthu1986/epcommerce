/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.plugin.payment.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.elasticpath.plugin.payment.PaymentType;

/**
 * Builder for PaymentOptionFormDescriptor. 
 */
public class PaymentOptionFormDescriptorBuilder {
	private final PaymentType paymentType;
	private final String formId;
	private final String messageKey;
	private final List<PaymentOptionFormRowDescriptor> visibleFormRows = new ArrayList<>();
	private final Map<String, String> hiddenFormFieldMap = new HashMap<>();
	private final Map<String, String> sessionValuesMap = new HashMap<>();
	private String postActionUrl;
	private String postActionMimeType;

	/**
	 * Construct PaymentOptionFormDescriptorBuilder for a new payment option.
	 * @param messageKey for the form description
	 * @param paymentType that the form descriptor enables
	 */
	public PaymentOptionFormDescriptorBuilder(final String messageKey, final PaymentType paymentType) {
		Random random = new Random();
		this.messageKey = messageKey;
		this.paymentType = paymentType;
		this.formId = paymentType.getName() + "-" + random.nextInt();
	}

	/**
	 * The action to perform when the user checks out (using x-www-form-urlencoded mime type).
	 * @param postActionUrl url to post the form to
	 * @return builder
	 */
	public PaymentOptionFormDescriptorBuilder withPostAction(final String postActionUrl) {
		this.postActionUrl = postActionUrl;
		this.postActionMimeType = "application/x-www-form-urlencoded";
		return this;
	}

	/**
	 * The action to perform when the user checks out.
	 * @param postActionUrl url to post the form to
	 * @param postActionMimeType mime type to use when formatting the post data
	 * @return builder
	 */
	public PaymentOptionFormDescriptorBuilder withPostAction(final String postActionUrl, final String postActionMimeType) {
		this.postActionUrl = postActionUrl;
		this.postActionMimeType = postActionMimeType;
		return this;
	}

	/**
	 * Add a row to the form table.
	 * @param paymentOptionFormRowDescriptor descriptor for the form row
	 * @return builder
	 */
	public PaymentOptionFormDescriptorBuilder withVisibleRow(final PaymentOptionFormRowDescriptor paymentOptionFormRowDescriptor) {
		this.visibleFormRows.add(paymentOptionFormRowDescriptor);
		return this;
	}

	/**
	 * Add a hidden field to the form.
	 * @param fieldId id for the field
	 * @param value value of the hidden field
	 * @return builder
	 */
	public PaymentOptionFormDescriptorBuilder withHiddenField(final String fieldId, final String value) {
		this.hiddenFormFieldMap.put(fieldId, value);
		return this;
	}

	/**
	 * Add a session value that will be saved to the session when the user posts.
	 * @param sessionKey key for the session value
	 * @param value value of the session value
	 * @return builder
	 */
	public PaymentOptionFormDescriptorBuilder withSessionValue(final String sessionKey, final String value) {
		this.sessionValuesMap.put(sessionKey, value);
		return this;
	}

	/**
	 * Build the PaymentOptionFormDescriptor.
	 * @return PaymentOptionFormDescriptor
	 */
	public PaymentOptionFormDescriptor build() {
		PaymentOptionFormDescriptor result = instantiatePaymentOptionFormDescriptor();
		result.setPaymentType(paymentType);
		result.setFormId(formId);
		result.setMessageKey(messageKey);
		result.setPostActionUrl(postActionUrl);
		result.setPostActionMimeType(postActionMimeType);
		result.getVisibleFormRows().addAll(visibleFormRows);
		result.getHiddenFormFieldMap().putAll(hiddenFormFieldMap);
		result.getSessionValuesMap().putAll(sessionValuesMap);
		return result;
	}

	/**
	 * Instantiate a new PaymentOptionFormDescriptor.
	 * @return new PaymentOptionFormDescriptor
	 */
	protected PaymentOptionFormDescriptor instantiatePaymentOptionFormDescriptor() {
		return new PaymentOptionFormDescriptor();
	}
}
