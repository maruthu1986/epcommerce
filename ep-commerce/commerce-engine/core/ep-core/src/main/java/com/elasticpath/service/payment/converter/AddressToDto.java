/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.service.payment.converter;

import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.plugin.payment.dto.AddressDto;

/**
 * Converter from Address to AddressDto.
 */
public class AddressToDto implements Converter<Address, AddressDto> {
	private BeanFactory beanFactory;

	@Override
	public AddressDto convert(final Address source) {
		AddressDto target = beanFactory.getBean(ContextIdNames.ADDRESS_DTO);
		BeanUtils.copyProperties(source, target);
		return target;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
}
