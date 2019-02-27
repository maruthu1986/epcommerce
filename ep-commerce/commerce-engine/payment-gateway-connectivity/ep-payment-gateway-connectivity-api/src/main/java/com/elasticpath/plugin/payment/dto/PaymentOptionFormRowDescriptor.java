/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.plugin.payment.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the data needed to construct the html form table row containing visible fields.
 */
public class PaymentOptionFormRowDescriptor {

	private String messageKey;
	private final List<PaymentOptionFormFieldDescriptor> visibleFields = new ArrayList<>();

	/**
	 * The key for the localized version of the message to display to the user for this row.
	 *
	 * @return the message key
	 */
	public String getMessageKey() {
		return messageKey;
	}

	/**
	 * The key for the localized version of the message to display to the user for this row.
	 *
	 * @param messageKey the message key
	 */
	void setMessageKey(final String messageKey) {
		this.messageKey = messageKey;
	}

	/**
	 * The list of visible fields in this row.
	 *
	 * @return the fields in this row
	 */
	public List<PaymentOptionFormFieldDescriptor> getVisibleFields() {
		return visibleFields;
	}
}
