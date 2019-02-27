/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.core.converters;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.shell.core.Completion;
import org.springframework.shell.core.Converter;
import org.springframework.shell.core.MethodTarget;

import com.elasticpath.datapopulation.core.utils.DpResourceUtils;

/**
 * {@link Converter} implementation to convert between a string and a concrete {@link Resource} object.
 */
public class ResourceConverter implements Converter<Resource>, ApplicationContextAware {
	private ApplicationContext applicationContext;

	@Override
	public Resource convertFromText(final String value, final Class<?> targetType, final String optionContext) {
		Resource result = null;

		if (StringUtils.isNotBlank(value)) {
			result = getApplicationContext().getResource(DpResourceUtils.getFileResourceUriByDefault(value, false));
		}

		return result;
	}

	@Override
	public boolean supports(final Class<?> requiredType, final String optionContext) {
		return Resource.class.isAssignableFrom(requiredType);
	}

	@Override
	public boolean getAllPossibleValues(final List<Completion> completions, final Class<?> targetType,
										final String existingData, final String optionContext, final MethodTarget target) {
		return false;
	}

	// Getters and Setters

	protected ApplicationContext getApplicationContext() {
		return this.applicationContext;
	}

	@Override
	public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
