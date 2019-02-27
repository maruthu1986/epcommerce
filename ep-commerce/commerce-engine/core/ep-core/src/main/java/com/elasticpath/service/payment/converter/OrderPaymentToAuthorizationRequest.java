/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.service.payment.converter;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.plugin.payment.dto.MoneyDto;
import com.elasticpath.plugin.payment.dto.PaymentMethod;
import com.elasticpath.plugin.payment.transaction.AuthorizationTransactionRequest;
import com.elasticpath.service.payment.gateway.GiftCertificateAuthorizationRequest;

/**
 * Converter from OrderPayment to AuthorizationTransactionRequest.
 */
public class OrderPaymentToAuthorizationRequest implements Converter<OrderPayment, AuthorizationTransactionRequest> {
	private BeanFactory beanFactory;

	@Override
	public AuthorizationTransactionRequest convert(final OrderPayment source) {
		ConversionService conversionService = beanFactory.getBean(ContextIdNames.CONVERSION_SERVICE);

		AuthorizationTransactionRequest target;
		if (PaymentType.GIFT_CERTIFICATE.equals(source.getPaymentMethod())) {
			target = beanFactory.getBean(ContextIdNames.GIFT_CERTIFICATE_AUTHORIZATION_REQUEST);
			((GiftCertificateAuthorizationRequest) target).setGiftCertificate(source.getGiftCertificate());
		} else {
			target = beanFactory.getBean(ContextIdNames.AUTHORIZATION_TRANSACTION_REQUEST);
		}
		target.setMoney(conversionService.convert(source, MoneyDto.class));
		target.setPaymentMethod(conversionService.convert(source, PaymentMethod.class));
		target.setReferenceId(source.getReferenceId());

		return target;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
}
