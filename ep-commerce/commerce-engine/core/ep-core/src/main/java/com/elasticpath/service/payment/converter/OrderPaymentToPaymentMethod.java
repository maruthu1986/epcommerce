/*
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.service.payment.converter;

import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.plugin.payment.dto.PaymentMethod;
import com.elasticpath.plugin.payment.dto.TokenPaymentMethod;
import com.elasticpath.service.payment.gateway.GiftCertificateOrderPaymentDto;

/**
 * Converter from OrderPayment to PaymentMethod.
 */
public class OrderPaymentToPaymentMethod implements Converter<OrderPayment, PaymentMethod> {
	private BeanFactory beanFactory;

	@Override
	public PaymentMethod convert(final OrderPayment source) {
		PaymentMethod target;
		if (PaymentType.GIFT_CERTIFICATE.equals(source.getPaymentMethod())) {
			target = beanFactory.getBean(ContextIdNames.GIFT_CERTIFICATE_ORDER_PAYMENT_DTO);
			((GiftCertificateOrderPaymentDto) target).setGiftCertificate(source.getGiftCertificate());
		} else {
			target = beanFactory.getBean(ContextIdNames.TOKEN_PAYMENT_METHOD);
			((TokenPaymentMethod) target).setValue(source.extractPaymentToken().getValue());
		}
		BeanUtils.copyProperties(source, target);
		return target;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
}
