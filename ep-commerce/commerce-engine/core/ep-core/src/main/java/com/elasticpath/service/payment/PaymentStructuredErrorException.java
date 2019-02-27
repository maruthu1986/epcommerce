/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.service.payment;

import static java.util.Arrays.asList;

import java.util.List;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.commons.exception.InvalidBusinessStateException;
import com.elasticpath.plugin.payment.exceptions.PaymentProcessingException;

/**
 * Structured error exception thrown when payment processing fails. Used to adapt exceptions from
 * payment-gateway-connectivity-api to structured errors.
 */
public class PaymentStructuredErrorException extends EpSystemException implements InvalidBusinessStateException {
	/**
	 * Serial version id.
	 */
	private static final long serialVersionUID = 5000000002L;

	private static final String DEBUG_MESSAGE = "Payment processing failed";

	private final List<StructuredErrorMessage> structuredErrorMessages;

	/**
	 * Creates a new <code>PaymentStructuredErrorException</code> object from a <code>PaymentProcessingException</code>.
	 *
	 * @param cause a <code>PaymentProcessingException</code> to be converted to a structured error.
	 */
	public PaymentStructuredErrorException(final PaymentProcessingException cause) {
		super(cause.getMessage(), cause);
		structuredErrorMessages = asList(new StructuredErrorMessage(cause.getStructuredMessageId(), DEBUG_MESSAGE, null));
	}

	@Override
	public List<StructuredErrorMessage> getStructuredErrorMessages() {
		return structuredErrorMessages;
	}

}
