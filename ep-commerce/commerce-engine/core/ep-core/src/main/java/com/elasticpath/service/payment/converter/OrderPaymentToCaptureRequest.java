/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.service.payment.converter;

import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.plugin.payment.dto.MoneyDto;
import com.elasticpath.plugin.payment.transaction.CaptureTransactionRequest;

/**
 * Converter from OrderPayment to CaptureTransactionRequest.
 */
public class OrderPaymentToCaptureRequest implements Converter<OrderPayment, CaptureTransactionRequest> {
	private BeanFactory beanFactory;

	@Override
	public CaptureTransactionRequest convert(final OrderPayment source) {
		ConversionService conversionService = beanFactory.getBean(ContextIdNames.CONVERSION_SERVICE);

		CaptureTransactionRequest target;
		if (PaymentType.GIFT_CERTIFICATE.equals(source.getPaymentMethod())) {
			target = beanFactory.getBean(ContextIdNames.GIFT_CERTIFICATE_CAPTURE_REQUEST);
		} else {
			target = beanFactory.getBean(ContextIdNames.CAPTURE_TRANSACTION_REQUEST);
		}
		MoneyDto money = conversionService.convert(source, MoneyDto.class);
		target.setMoney(money);

		BeanUtils.copyProperties(source, target);

		return target;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
}
