/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.service.payment.converter;

import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.plugin.payment.dto.OrderSkuDto;

/**
 * Converter from OrderSku to OrderSkuDto.
 */
public class OrderSkuToDto implements Converter<OrderSku, OrderSkuDto> {
	private BeanFactory beanFactory;

	@Override
	public OrderSkuDto convert(final OrderSku source) {
		OrderSkuDto target = beanFactory.getBean(ContextIdNames.ORDER_SKU_DTO);
		BeanUtils.copyProperties(source, target);
		return target;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
}
