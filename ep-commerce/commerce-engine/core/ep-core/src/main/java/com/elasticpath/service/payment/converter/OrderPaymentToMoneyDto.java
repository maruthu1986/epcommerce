/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.service.payment.converter;

import org.springframework.core.convert.converter.Converter;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.plugin.payment.dto.MoneyDto;

/**
 * Converter from OrderPayment to MoneyDto.
 */
public class OrderPaymentToMoneyDto implements Converter<OrderPayment, MoneyDto> {
	private BeanFactory beanFactory;

	@Override
	public MoneyDto convert(final OrderPayment source) {
		MoneyDto money = beanFactory.getBean(ContextIdNames.MONEY_DTO);
		money.setAmount(source.getAmount());
		money.setCurrencyCode(source.getCurrencyCode());
		return money;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
}
