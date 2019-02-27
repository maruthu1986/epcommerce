/*
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.payment.impl;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.impl.AbstractEpDomainImpl;
import com.elasticpath.domain.payment.PaymentHandler;
import com.elasticpath.domain.payment.PaymentHandlerFactory;
import com.elasticpath.plugin.payment.PaymentType;


/**
 * Payment handler factory class for getting instance of {@link PaymentHandler}.
 */
public class PaymentHandlerFactoryImpl extends AbstractEpDomainImpl implements PaymentHandlerFactory {

	/** Serial version id. */
	private static final long serialVersionUID = 5000000001L;

	/**
	 *
	 * @param paymentType the payment type
	 * @return PaymentHandler instance
	 */
	@Override
	public PaymentHandler getPaymentHandler(final PaymentType paymentType) {
		if (paymentType == null) {
			return null;
		}
		PaymentHandler handler;
		switch (paymentType.getOrdinal()) {
			case PaymentType.CREDITCARD_DIRECT_POST_ORDINAL :
				handler = getBean(ContextIdNames.PAYMENT_HANDLER_CREDITCARD_DIRECT_POST);
				break;
			case PaymentType.PAYMENT_TOKEN_ORDINAL :
				handler = getBean(ContextIdNames.PAYMENT_HANDLER_TOKEN);
				break;
			case PaymentType.PAYPAL_EXPRESS_ORDINAL :
				handler = getBean(ContextIdNames.PAYMENT_HANDLER_PAYPAL);
				break;
			case PaymentType.RETURN_AND_EXCHANGE_ORDINAL :
				handler = getBean(ContextIdNames.PAYMENT_HANDLER_EXCHANGE);
				break;
			case PaymentType.GIFT_CERTIFICATE_ORDINAL :
				handler = getBean(ContextIdNames.PAYMENT_HANDLER_GIFTCERTIFICATE);
				break;
			case PaymentType.HOSTED_PAGE_ORDINAL:
				handler = getBean(ContextIdNames.PAYMENT_HANDLER_DIRECT_POST_PAYPAL);
				break;
			default: 
				throw new IllegalArgumentException("Payment handler id is not valid");
		}
		return handler;
	}

}
