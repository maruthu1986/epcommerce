/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.core.converters;

import java.io.OutputStream;
import java.util.List;
import javax.inject.Provider;

import org.springframework.shell.core.Completion;
import org.springframework.shell.core.Converter;
import org.springframework.shell.core.MethodTarget;

/**
 * {@link Converter} implementation to convert between a string and a concrete {@link OutputStream} object.
 */
public class OutputStreamConverter implements Converter<OutputStream> {
	private Converter<Provider<OutputStream>> outputStreamProviderConverter;

	@Override
	public OutputStream convertFromText(final String value, final Class<?> targetType, final String optionContext) {
		OutputStream result = null;

		final Provider<OutputStream> provider = getOutputStreamProviderConverter().convertFromText(value, Provider.class, null);
		if (provider != null) {
			result = provider.get();
		}

		return result;
	}

	@Override
	public boolean supports(final Class<?> requiredType, final String optionContext) {
		return OutputStream.class.isAssignableFrom(requiredType);
	}

	@Override
	public boolean getAllPossibleValues(final List<Completion> completions, final Class<?> targetType, final String existingData,
										final String optionContext, final MethodTarget target) {
		return false;
	}

	// Getters and Setters

	protected Converter<Provider<OutputStream>> getOutputStreamProviderConverter() {
		return this.outputStreamProviderConverter;
	}

	public void setOutputStreamProviderConverter(final Converter<Provider<OutputStream>> outputStreamProviderConverter) {
		this.outputStreamProviderConverter = outputStreamProviderConverter;
	}
}
