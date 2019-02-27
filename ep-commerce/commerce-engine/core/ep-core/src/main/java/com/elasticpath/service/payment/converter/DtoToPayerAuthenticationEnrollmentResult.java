/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.service.payment.converter;

import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.misc.PayerAuthenticationEnrollmentResult;
import com.elasticpath.plugin.payment.dto.PayerAuthenticationEnrollmentResultDto;

/**
 * Converter from PayerAuthenticationEnrollmentResultDto to PayerAuthenticationEnrollmentResult.
 */
public class DtoToPayerAuthenticationEnrollmentResult implements
		Converter<PayerAuthenticationEnrollmentResultDto, PayerAuthenticationEnrollmentResult> {
	private BeanFactory beanFactory;

	@Override
	public PayerAuthenticationEnrollmentResult convert(final PayerAuthenticationEnrollmentResultDto source) {
		PayerAuthenticationEnrollmentResult target = beanFactory.getBean(ContextIdNames.PAYER_AUTHENTICATION_ENROLLMENT_RESULT);
		BeanUtils.copyProperties(source, target);
		return target;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
}
