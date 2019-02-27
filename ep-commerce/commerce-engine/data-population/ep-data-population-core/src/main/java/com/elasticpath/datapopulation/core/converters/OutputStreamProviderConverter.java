/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.core.converters;

import java.io.OutputStream;
import java.util.List;
import javax.inject.Provider;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.shell.core.Completion;
import org.springframework.shell.core.Converter;
import org.springframework.shell.core.MethodTarget;

import com.elasticpath.datapopulation.core.exceptions.DataPopulationActionException;
import com.elasticpath.datapopulation.core.utils.ResourceOutputStreamProvider;
import com.elasticpath.datapopulation.core.utils.SimpleProvider;

/**
 * {@link Converter} implementation to convert between a string and a concrete {@link Provider&lt;OutputStream&gt;} object.
 */
public class OutputStreamProviderConverter implements Converter<Provider<OutputStream>> {
	/**
	 * The context that this Converter uses to convert to {@link Provider&lt;OutputStream&gt;} objects.
	 * A context is required because {@link Provider} is a generic class and so there can be several {@link Converter&lt;Provider&gt;} instances
	 * registered, and at runtime there is no way of knowing what type of {@link Provider} each {@link Converter} converts. The context is therefore
	 * used to distinguish. This {@link Converter} uses this constant.
	 *
	 * @see {@link #supports(Class, String)} for where the context is used by Spring-Shell to decide which {@link Converter} to use.
	 */
	public static final String OUTPUT_STREAM_OPTION_CONTEXT = "OutputStream";

	/**
	 * Name to use to request {@link #convertFromText(String, Class, String)} to return {@link System#out}.
	 */
	public static final String STANDARD_OUT = "STDOUT";

	/**
	 * Name to use to request {@link #convertFromText(String, Class, String)} to return {@link System#err}.
	 */
	public static final String STANDARD_ERR = "STDERR";

	private Converter<Resource> resourceConverter;

	/**
	 * Parses the given String value looking to resolve it to an {@link OutputStream}. It first calls {@link #matchStandardOutputStreams(String)}
	 * to see if it matches Standard Out or Error, if so that is returned, otherwise {@link #getResource(String)} is called to resolve the value to a
	 * {@link Resource} object, and if that resolves to a {@link Resource} then a its {@link OutputStream} is returned, otherwise a
	 * {@link DataPopulationCliException} is thrown as it couldn't resolve the value passed in.
	 *
	 * @param value         the value to resolve to an {@link OutputStream}.
	 * @param targetType    should be {@link OutputStream}.class but isn't validated.
	 * @param optionContext the context that this Converter should resolve in. Should be {@link #OUTPUT_STREAM_OPTION_CONTEXT} but this isn't
	 *                      validated.
	 * @return an {@link OutputStream} resolved from the value passed in.
	 * @throws DataPopulationActionException if the {@link Resource} could not be resolved from the value given,
	 *                                       or if there was a problem obtaining its {@link OutputStream}.
	 */
	@Override
	public Provider<OutputStream> convertFromText(final String value, final Class<?> targetType, final String optionContext) {
		Provider<OutputStream> result = null;

		if (StringUtils.isNotBlank(value)) {
			// First check if the value matches Standard Out/Error
			result = matchStandardOutputStreams(value);

			// If not then use Spring to resolve the resource, and then get its OutputStream
			// This allows writable resources (other than the standard FileSystemResource) to be contributed if required

			if (result == null) {
				final Resource resource = getResource(value);

				result = createOutputStreamProvider(resource);
			}
		}

		return result;
	}

	/**
	 * Resolves the String value to a {@link Resource} by delegating to the {@link Resource} converter obtained from {@link #getResourceConverter()}.
	 * If no resource could be found a {@link DataPopulationCliException} is thrown.
	 *
	 * @param value the value to resolve to a {@link Resource}.
	 * @return the resolved {@link Resource}, never null.
	 * @throws DataPopulationActionException if the {@link Resource} could not be resolved.
	 */
	protected Resource getResource(final String value) {
		final Resource resource = getResourceConverter().convertFromText(value, Resource.class, null);
		if (resource == null) {
			throw new DataPopulationActionException("Error: Unable to find resource: " + value);
		}
		return resource;
	}

	/**
	 * Returns true if the given requiredType is a {@link Provider} instance and the optionContext given equals, case-insensitively,
	 * {@link #OUTPUT_STREAM_OPTION_CONTEXT}; false otherwise.
	 *
	 * @param requiredType  the type to validate; any class that isn't a {@link Provider} implementor will result in false being returned.
	 * @param optionContext the context requested; any value other than {@link #OUTPUT_STREAM_OPTION_CONTEXT} will result in false being returned.
	 * @return true if the given requiredType is a {@link Provider} instance and the optionContext given equals, case-insensitively,
	 * {@link #OUTPUT_STREAM_OPTION_CONTEXT}; false otherwise.
	 */
	@Override
	public boolean supports(final Class<?> requiredType, final String optionContext) {
		// As Provider is a generic class we use the context to make sure that the type of Provider expected is a Provider<OutputStream>
		// It means users need to set the CliOption's optionContext value to the constant below
		return Provider.class.isAssignableFrom(requiredType) && StringUtils.equalsIgnoreCase(optionContext, OUTPUT_STREAM_OPTION_CONTEXT);
	}

	@Override
	public boolean getAllPossibleValues(final List<Completion> completions, final Class<?> targetType, final String existingData,
										final String optionContext, final MethodTarget target) {
		return false;
	}

	/**
	 * Checks if the outputStreamName matches (case-insensitively) either {@link #STANDARD_OUT} or {@link #STANDARD_ERR}, and if so returns the
	 * appropriate {@link OutputStream}; otherwise returns null.
	 *
	 * @param outputStreamName the name of the {@link OutputStream} to resolve.
	 * @return {@link System#out} if the given String equals, case-insensitively, {@link #STANDARD_OUT}; returns {@link System#err} if the given
	 * String equals, case-insensitively, {@link #STANDARD_ERR}, otherwise it returns null.
	 */
	protected Provider<OutputStream> matchStandardOutputStreams(final String outputStreamName) {
		Provider<OutputStream> result = null;

		if (StringUtils.equalsIgnoreCase(outputStreamName, STANDARD_OUT)) {
			result = createOutputStreamProvider(System.out);
		} else if (StringUtils.equalsIgnoreCase(outputStreamName, STANDARD_ERR)) {
			result = createOutputStreamProvider(System.err);
		}

		return result;
	}

	// Factory methods

	/**
	 * For the given {@link Resource} this method creates a {@link Provider} of {@link OutputStream}.
	 * Instantiates the {@link ResourceOutputStreamProvider} class by default.
	 *
	 * @param resource the resource to create a {@link Provider&lt;OutputStream&gt;} for.
	 * @return a {@link Provider} of {@link OutputStream} for the given {@link Resource}.
	 */
	protected Provider<OutputStream> createOutputStreamProvider(final Resource resource) {
		return new ResourceOutputStreamProvider(resource);
	}

	/**
	 * For the given {@link OutputStream} this method creates a {@link Provider} for it.
	 * Instantiates the {@link SimpleProvider} class by default.
	 *
	 * @param outputStream the {@link OutputStream} object to wrap in a {@link Provider} object.
	 * @return a {@link Provider} which returns the given {@link OutputStream} when requested.
	 */
	protected Provider<OutputStream> createOutputStreamProvider(final OutputStream outputStream) {
		return new SimpleProvider<>(outputStream);
	}

	// Getters and Setters

	protected Converter<Resource> getResourceConverter() {
		return this.resourceConverter;
	}

	public void setResourceConverter(final Converter<Resource> resourceConverter) {
		this.resourceConverter = resourceConverter;
	}
}
