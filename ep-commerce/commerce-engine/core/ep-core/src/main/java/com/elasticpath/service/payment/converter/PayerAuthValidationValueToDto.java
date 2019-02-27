/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.service.payment.converter;

import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.misc.PayerAuthValidationValue;
import com.elasticpath.plugin.payment.dto.PayerAuthValidationValueDto;

/**
 * Converter from PayerAuthValidationValueDto to PayerAuthValidationValue.
 */
public class PayerAuthValidationValueToDto implements Converter<PayerAuthValidationValue, PayerAuthValidationValueDto> {
	private BeanFactory beanFactory;

	@Override
	public PayerAuthValidationValueDto convert(final PayerAuthValidationValue source) {
		PayerAuthValidationValueDto target = beanFactory.getBean(ContextIdNames.PAYER_AUTH_VALIDATION_VALUE_DTO);
		BeanUtils.copyProperties(source, target);
		return target;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
}
