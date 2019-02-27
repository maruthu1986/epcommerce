/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.core.converters;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.shell.core.Completion;
import org.springframework.shell.core.Converter;
import org.springframework.shell.core.MethodTarget;

import com.elasticpath.datapopulation.core.exceptions.DataPopulationActionException;
import com.elasticpath.datapopulation.core.utils.DpUtils;

/**
 * {@link Converter} implementation to convert between a string and a concrete {@link InputStream} object.
 */
public class InputStreamConverter implements Converter<InputStream> {
	/**
	 * Name to use to request {@link #convertFromText(String, Class, String)} to return {@link System#in}.
	 */
	public static final String STANDARD_IN = "STDIN";

	private Converter<Resource> resourceConverter;

	// Converter methods

	/**
	 * Parses the given String value looking to resolve it to an {@link InputStream} by delegating to {@link #getInputStream(String)}.
	 *
	 * @param value         the value to resolve to an {@link InputStream}.
	 * @param targetType    should be {@link InputStream}.class but isn't validated.
	 * @param optionContext the context that this Converter should resolve in. However this is currently not used.
	 * @return an {@link InputStream} resolved from the value passed in.
	 * @throws DataPopulationActionException if the {@link Resource} could not be resolved from the value given, or if
	 *                                       there was a problem obtaining its {@link InputStream}.
	 */
	@Override
	public InputStream convertFromText(final String value, final Class<?> targetType, final String optionContext)
			throws DataPopulationActionException {
		return getInputStream(value);
	}

	@Override
	public boolean supports(final Class<?> requiredType, final String optionContext) {
		return InputStream.class.isAssignableFrom(requiredType);
	}

	@Override
	public boolean getAllPossibleValues(final List<Completion> completions, final Class<?> targetType, final String existingData,
										final String optionContext, final MethodTarget target) {
		return false;
	}

	// Implementation method

	/**
	 * Parses the given String resource location looking to resolve it to an {@link InputStream}. It first calls
	 * {@link #matchStandardInputStream(String)} to see if it matches Standard In, if so that is returned, otherwise {@link #getResource(String)}
	 * is called to resolve the value to a {@link Resource} object, and if that resolves to a {@link Resource} then a its {@link InputStream} is
	 * returned, otherwise a {@link DataPopulationCliException} is thrown as it couldn't resolve the value passed in.
	 *
	 * @param resourceLocation the value to resolve to an {@link InputStream}.
	 * @return an {@link InputStream} resolved from the resource location passed in.
	 * @throws DataPopulationActionException if the {@link Resource} could not be resolved from the value given,
	 *                                       or if there was a problem obtaining its {@link InputStream}.
	 */
	public InputStream getInputStream(final String resourceLocation) {
		InputStream result = null;

		if (StringUtils.isNotBlank(resourceLocation)) {
			// First check if the value matches Standard In
			result = matchStandardInputStream(resourceLocation);

			// If not then use Spring to resolve the resource, and then get its InputStream
			if (result == null) {
				final Resource resource = getResource(resourceLocation);

				try {
					result = resource.getInputStream();
				} catch (final IOException e) {
					throw new DataPopulationActionException("Error: Unable to get InputStream for resource '" + resource
							+ "'. " + DpUtils.getNestedExceptionMessage(e), e);
				}
			}
		}

		return result;
	}

	// Helper methods

	/**
	 * Resolves the String value to a {@link Resource} by delegating to the {@link Resource} converter obtained from {@link #getResourceConverter()}.
	 * If no resource could be found a {@link DataPopulationCliException} is thrown.
	 *
	 * @param value the value to resolve to a {@link Resource}.
	 * @return the resolved {@link Resource}, never null.
	 * @throws DataPopulationActionException if the {@link Resource} could not be resolved.
	 */
	protected Resource getResource(final String value) {
		final Resource result = getResourceConverter().convertFromText(value, Resource.class, null);
		if (result == null) {
			throw new DataPopulationActionException("Error: Unable to find resource: " + value);
		}
		return result;
	}

	/**
	 * Returns {@link System#in} if the given String equals, case-insensitively, {@link #STANDARD_IN}, otherwise it returns null.
	 *
	 * @param inputStreamName the name of the {@link InputStream} to resolve.
	 * @return {@link System#in} if the given String equals, case-insensitively, {@link #STANDARD_IN}, otherwise it returns null.
	 */
	protected InputStream matchStandardInputStream(final String inputStreamName) {
		InputStream result = null;

		if (StringUtils.equalsIgnoreCase(inputStreamName, STANDARD_IN)) {
			result = System.in;
		}

		return result;
	}

	// Getters and Setters

	protected Converter<Resource> getResourceConverter() {
		return this.resourceConverter;
	}

	public void setResourceConverter(final Converter<Resource> resourceConverter) {
		this.resourceConverter = resourceConverter;
	}
}
