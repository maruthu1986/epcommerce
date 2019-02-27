/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.core.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import javax.inject.Provider;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.elasticpath.datapopulation.core.exceptions.FilterFileActionException;
import com.elasticpath.datapopulation.core.service.filtering.DirectoryFilterer;
import com.elasticpath.datapopulation.core.service.filtering.FileFilterer;
import com.elasticpath.datapopulation.core.utils.DpUtils;

/**
 * Service to filter property placeholders in the input file or directory and then output that to a desired destination.
 * Using the service requires an input file or directory of files, a proposed output directory and the filtering properties.
 * <p>
 * This service uses {@link org.springframework.util.PropertyPlaceholderHelper.PlaceholderResolver} to filter (see
 * {@link FileFilterer} for more details).
 */
public class FilterService {

	/**
	 * Default value for the '--recurse' option in the
	 * specifying if sub-directories should also be filtered.
	 */
	public static final String DEFAULT_RECURSE_OPTION_STRING = "true";

	/**
	 * Boolean representation of the {@link #DEFAULT_RECURSE_OPTION_STRING} used
	 * programmatically when no recurse option is specified.
	 */
	public static final boolean DEFAULT_RECURSE_OPTION = Boolean
			.parseBoolean(DEFAULT_RECURSE_OPTION_STRING);

	/**
	 * Used in the Service method definitions to describe what system properties
	 * mode values are permissible.
	 */
	protected static final String SYSTEM_PROPERTIES_MODE_HELP_STRING = "The system properties mode, can be one of: "
			+ FileFilterer.SYSTEM_PROPERTIES_MODE_NEVER
			+ ", "
			+ FileFilterer.SYSTEM_PROPERTIES_MODE_FALLBACK
			+ ", "
			+ " "
			+ FileFilterer.SYSTEM_PROPERTIES_MODE_OVERRIDE;

	/**
	 * Used in the Service method definitions to describe what the placeholder
	 * prefix option is used for.
	 */
	protected static final String PLACEHOLDER_PREFIX_HELP_STRING = "The placeholder prefix to use";

	/**
	 * Used in the Service method definitions to describe what the placeholder
	 * suffix option is used for.
	 */
	protected static final String PLACEHOLDER_SUFFIX_HELP_STRING = "The placeholder suffix to use";

	private FileFilterer defaultFileFilterer;
	private DirectoryFilterer defaultDirectoryFilterer;

	/**
	 * Filters the given {@link Resource}, with the given filter
	 * {@link Properties} objects, replacing property placeholders using the
	 * prefix and suffix given, and writing the filtered result to the
	 * {@link OutputStream} returned by the given {@link Provider}. Resolution
	 * of the property placeholders will also look up properties defined as
	 * system properties, depending on the mode specified.
	 * <p>
	 * Note: This method supports filtering nested property placeholders, so
	 * that inner property placeholders are first resolved before that resolved
	 * value is used when attempting to resolve the outer property
	 * placeholder(s).
	 * </p>
	 *
	 * @param input                the {@link Resource} to filter.
	 * @param outputProvider       a {@link Provider} which provides an {@link OutputStream} to
	 *                             write to. By using a {@link Provider} rather than passing an
	 *                             {@link OutputStream} directly, the same resource can be
	 *                             filtered in-situ since it is read in its entirety before the
	 *                             {@link Provider} is asked for an {@link OutputStream}.
	 * @param placeholderPrefix    the prefix used in the input file to indicate the start of a
	 *                             property placeholder.
	 * @param placeholderSuffix    the suffix used in the input file to indicate the end of a
	 *                             property placeholder.
	 * @param systemPropertiesMode the mode indicating how system properties should be used (if
	 *                             at all). See this class' static constants for more
	 *                             information.
	 * @param filterProperties     one or more {@link Properties} objects to filter the input
	 *                             file with. They are searched in order for each property
	 *                             placeholder, so the order they are passed to this method is
	 *                             significant if a property is defined in multiple filter
	 *                             {@link Properties} objects.
	 * @param <OS>                 the type of {@link OutputStream} that the {@link Provider}
	 *                             returns; this method doesn't care.
	 * @throws FilterFileActionException if there was a problem filtering the input or writing to the
	 *                                   output.
	 */
	public <OS extends OutputStream> void filter(final Resource input, final Provider<OS> outputProvider,
												 final String placeholderPrefix, final String placeholderSuffix,
												 final String systemPropertiesMode,
												 final Properties... filterProperties) throws FilterFileActionException {
		final FileFilterer filterer = createFileFilterer(placeholderPrefix,
				placeholderSuffix, systemPropertiesMode);

		try {
			filterer.filter(input, outputProvider, filterProperties);
		} catch (final IOException e) {
			throw new FilterFileActionException("Unable to filter. "
					+ DpUtils.getNestedExceptionMessage(e), e);
		}
	}

	// Convenience methods for common requests

	/**
	 * Convenience method to filter the given {@link Resource} with the given
	 * filter {@link Properties} objects, using the default property placeholder
	 * values and system properties mode before writing the filtered result to
	 * the {@link OutputStream} returned by the given {@link Provider}.
	 *
	 * @param inputFile            the {@link Resource} to filter.
	 * @param outputStreamProvider a {@link Provider} which provides an {@link OutputStream} to
	 *                             write to. By using a {@link Provider} rather than passing an
	 *                             {@link OutputStream} directly, the same resource can be
	 *                             filtered in-situ since it is read in its entirety before the
	 *                             {@link Provider} is asked for an {@link OutputStream}.
	 * @param filterProperties     one or more {@link Properties} objects to filter the input
	 *                             file with. They are searched in order for each property
	 *                             placeholder, so the order they are passed to this method is
	 *                             significant if a property is defined in multiple filter
	 *                             {@link Properties} objects.
	 * @param <OS>                 the type of {@link OutputStream} that the {@link Provider}
	 *                             returns; this method doesn't care.
	 * @throws IOException if there was a problem filtering the input or writing to the
	 *                     output.
	 */
	public <OS extends OutputStream> void filter(final Resource inputFile,
												 final Provider<OS> outputStreamProvider,
												 final Properties... filterProperties) throws IOException {
		getDefaultFileFilterer().filter(inputFile, outputStreamProvider,
				filterProperties);
	}

	/**
	 * Convenience method to filter the given {@link InputStream} with the given
	 * filter {@link Properties} objects, using the default property placeholder
	 * values and system properties mode before writing the filtered result to
	 * the given output {@link File}.
	 *
	 * @param inputFile        the {@link InputStream} to filter.
	 * @param outputFile       the {@link File} to write to.
	 * @param filterProperties one or more {@link Properties} objects to filter the input
	 *                         file with. They are searched in order for each property
	 *                         placeholder, so the order they are passed to this method is
	 *                         significant if a property is defined in multiple filter
	 *                         {@link Properties} objects.
	 * @throws IOException if there was a problem filtering the input or writing to the
	 *                     output.
	 */
	public void filter(final InputStream inputFile, final File outputFile,
					   final Properties... filterProperties) throws IOException {
		getDefaultFileFilterer().filter(inputFile, new FileSystemResource(outputFile), filterProperties);
	}

	/**
	 * Convenience method to filter the given {@link File} with the given filter
	 * {@link Properties} objects, using the default property placeholder values
	 * and system properties mode before writing the filtered result to the
	 * given output {@link File}. Both the input and output files can be the
	 * same and it will correctly filter the file in-situ.
	 *
	 * @param inputFile        the {@link File} to filter.
	 * @param outputFile       the {@link File} to write to.
	 * @param filterProperties one or more {@link Properties} objects to filter the input
	 *                         file with. They are searched in order for each property
	 *                         placeholder, so the order they are passed to this method is
	 *                         significant if a property is defined in multiple filter
	 *                         {@link Properties} objects.
	 * @throws IOException if there was a problem filtering the input or writing to the
	 *                     output.
	 */
	public void filter(final File inputFile, final File outputFile, final Properties... filterProperties)
			throws IOException {
		getDefaultFileFilterer().filter(inputFile, outputFile, filterProperties);
	}

	/**
	 * Convenience method to filter the given {@link Resource} with the given
	 * filter {@link Properties} objects, using the default property placeholder
	 * values and system properties mode before writing the filtered result to
	 * the given output {@link Resource}. Both the input and output resources
	 * can point to the same file and it will correctly filter the file in-situ.
	 *
	 * @param inputFile        the {@link Resource} to filter.
	 * @param outputFile       the {@link Resource} to write to, must be a writable resource,
	 *                         otherwise an {@link IOException} is thrown.
	 * @param filterProperties one or more {@link Properties} objects to filter the input
	 *                         file with. They are searched in order for each property
	 *                         placeholder, so the order they are passed to this method is
	 *                         significant if a property is defined in multiple filter
	 *                         {@link Properties} objects.
	 * @throws IOException if there was a problem filtering the input or writing to the
	 *                     output.
	 */
	public void filter(final Resource inputFile, final Resource outputFile,
					   final Properties... filterProperties) throws IOException {

		getDefaultFileFilterer()
				.filter(inputFile, outputFile, filterProperties);
	}

	/**
	 * Convenience method to filter all files in the given input directory with
	 * the given filter {@link Properties} objects, using the default property
	 * placeholder values and system properties mode before writing them to the
	 * given output directory. The input and output directories can be the same
	 * and the files will be filtered in-situ.
	 *
	 * @param inputDirectory   the input directory to filter all files in (recursing into
	 *                         sub-directories also).
	 * @param outputDirectory  the output directory to write filtered files to in the same
	 *                         directory structure as the output directory.
	 * @param filterProperties one or more {@link Properties} objects to filter the input
	 *                         file with. They are searched in order for each property
	 *                         placeholder, so the order they are passed to this method is
	 *                         significant if a property is defined in multiple filter
	 *                         {@link Properties} objects.
	 * @throws IOException if there was a problem filtering the input or writing to the
	 *                     output.
	 */
	public void filterDirectory(final File inputDirectory,
								final File outputDirectory, final Properties... filterProperties)
			throws IOException {
		getDefaultDirectoryFilterer().filterDirectory(inputDirectory,
				outputDirectory, DEFAULT_RECURSE_OPTION, filterProperties);
	}

	// Factory methods

	/**
	 * Factory method to create the default {@link FileFilterer} to use if
	 * explicit property placeholder and system properties mode values are not
	 * supplied, and a default {@link FileFilterer} has not been supplied by
	 * {@link #setDefaultFileFilterer(FileFilterer)}.
	 *
	 * @return the default {@link FileFilterer} to use if one has not been set
	 * by {@link #setDefaultFileFilterer(FileFilterer)}.
	 */
	protected FileFilterer createDefaultFileFilterer() {
		return new FileFilterer();
	}

	/**
	 * Factory method to create the default {@link DirectoryFilterer} to use if
	 * explicit property placeholder and system properties mode values are not
	 * supplied, and a default {@link DirectoryFilterer} has not been supplied
	 * by {@link #setDefaultDirectoryFilterer(DirectoryFilterer)}.
	 *
	 * @return the default {@link DirectoryFilterer} to use if one has not been
	 * set by {@link #setDefaultDirectoryFilterer(DirectoryFilterer)}.
	 */
	protected DirectoryFilterer createDefaultDirectoryFilterer() {
		return new DirectoryFilterer(getDefaultFileFilterer());
	}

	/**
	 * Creates a {@link FileFilterer} object that uses the given property
	 * placeholder prefix and suffix, as well as the system properties mode
	 * specified.
	 *
	 * @param placeholderPrefix    the prefix used in each input file to indicate the start of a
	 *                             property placeholder.
	 * @param placeholderSuffix    the suffix used in each input file to indicate the end of a
	 *                             property placeholder.
	 * @param systemPropertiesMode the mode indicating how system properties should be used (if
	 *                             at all). See this class' static constants for more
	 *                             information.
	 * @return a {@link FileFilterer} object that uses the given property
	 * placeholder prefix and suffix, as well as the system properties
	 * mode specified.
	 */
	protected FileFilterer createFileFilterer(final String placeholderPrefix,
											  final String placeholderSuffix, final String systemPropertiesMode) {
		return new FileFilterer(placeholderPrefix, placeholderSuffix,
				systemPropertiesMode);
	}

	/**
	 * Creates a {@link DirectoryFilterer} object that uses the given
	 * {@link FileFilterer} to filter the files inside the directories.
	 *
	 * @param fileFilterer the {@link FileFilterer} that should be used by the created
	 *                     {@link DirectoryFilterer}.
	 * @return a {@link DirectoryFilterer} object that uses the given
	 * {@link FileFilterer} to filter the files inside the directories.
	 */
	protected DirectoryFilterer createDirectoryFilterer(
			final FileFilterer fileFilterer) {
		return new DirectoryFilterer(fileFilterer);
	}

	// Getter and Setters

	/**
	 * Returns the default {@link FileFilterer} to use if explicit property
	 * placeholder and system properties mode values are not supplied when
	 * filtering. If one hasn't been set, {@link #createDefaultFileFilterer()}
	 * is called to set the default file filterer before returning it.
	 *
	 * @return default {@link FileFilterer} to use if explicit property
	 * placeholder and system properties mode values are not supplied;
	 * never null.
	 */
	protected FileFilterer getDefaultFileFilterer() {
		if (this.defaultFileFilterer == null) {
			this.defaultFileFilterer = createDefaultFileFilterer();
		}
		return this.defaultFileFilterer;
	}

	/**
	 * Sets the default {@link FileFilterer} to use if explicit property
	 * placeholder and system properties mode values are not supplied when
	 * filtering.
	 *
	 * @param defaultFileFilterer the default {@link FileFilterer} to use if explicit property
	 *                            placeholder and system properties mode values are not supplied
	 *                            when filtering.
	 */
	public void setDefaultFileFilterer(final FileFilterer defaultFileFilterer) {
		this.defaultFileFilterer = defaultFileFilterer;
	}

	/**
	 * Returns the default {@link DirectoryFilterer} to use if explicit property
	 * placeholder and system properties mode values are not supplied. If one
	 * hasn't been set, {@link #createDefaultDirectoryFilterer()} is called to
	 * set the default directory filterer before returning it.
	 *
	 * @return default {@link DirectoryFilterer} to use if explicit property
	 * placeholder and system properties mode values are not supplied;
	 * never null.
	 */
	protected DirectoryFilterer getDefaultDirectoryFilterer() {
		if (this.defaultDirectoryFilterer == null) {
			this.defaultDirectoryFilterer = createDefaultDirectoryFilterer();
		}
		return this.defaultDirectoryFilterer;
	}

	/**
	 * Sets the default {@link DirectoryFilterer} to use if explicit property
	 * placeholder and system properties mode values are not supplied when
	 * filtering.
	 *
	 * @param defaultDirectoryFilterer the default {@link DirectoryFilterer} to use if explicit
	 *                                 property placeholder and system properties mode values are not
	 *                                 supplied when filtering.
	 */
	public void setDefaultDirectoryFilterer(
			final DirectoryFilterer defaultDirectoryFilterer) {
		this.defaultDirectoryFilterer = defaultDirectoryFilterer;
	}
}
