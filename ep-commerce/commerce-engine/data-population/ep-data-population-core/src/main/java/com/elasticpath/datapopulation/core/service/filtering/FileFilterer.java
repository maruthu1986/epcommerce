/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.core.service.filtering;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.TreeMap;
import javax.inject.Provider;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.PreferencesPlaceholderConfigurer;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringValueResolver;

import com.elasticpath.datapopulation.core.exceptions.FilterFileActionException;
import com.elasticpath.datapopulation.core.service.filtering.helper.PropertiesStringValueResolver;
import com.elasticpath.datapopulation.core.service.filtering.helper.impl.PropertyPlaceholderStringValueResolver;
import com.elasticpath.datapopulation.core.utils.DpResourceUtils;
import com.elasticpath.datapopulation.core.utils.DpUtils;
import com.elasticpath.datapopulation.core.utils.ResourceOutputStreamProvider;

/**
 * A class which filters individual files based on the given filter information.
 */
public class FileFilterer {
	/**
	 * The String constant that can be passed as the '--systemPropertiesMode' option to indicate property resolution should look for system
	 * properties
	 * when filtering if properties are not defined in the input properties files.
	 */
	public static final String SYSTEM_PROPERTIES_MODE_FALLBACK = "SYSTEM_PROPERTIES_MODE_FALLBACK";
	/**
	 * The String constant that can be passed as the '--systemPropertiesMode' option to indicate property resolution should not look for system
	 * properties when filtering even if properties are not defined in the input properties files.
	 */
	public static final String SYSTEM_PROPERTIES_MODE_NEVER = "SYSTEM_PROPERTIES_MODE_NEVER";
	/**
	 * The String constant that can be passed as the '--systemPropertiesMode' option to indicate property resolution should first look for system
	 * properties defined when filtering, before looking for values defined in the input properties files.
	 */
	public static final String SYSTEM_PROPERTIES_MODE_OVERRIDE = "SYSTEM_PROPERTIES_MODE_OVERRIDE";
	/**
	 * Default property placeholder prefix.
	 */
	public static final String DEFAULT_PLACEHOLDER_PREFIX = "${";
	/**
	 * Default property placeholder suffix.
	 */
	public static final String DEFAULT_PLACEHOLDER_SUFFIX = "}";
	/**
	 * Default value for the '--systemPropertiesMode' option if none is specified; in this case we fall back to resolving property placeholders
	 * with system properties if they are not defined in the input properties files.
	 */
	public static final String DEFAULT_SYSTEM_PROPERTIES_MODE = SYSTEM_PROPERTIES_MODE_FALLBACK;
	/**
	 * Default file encoding to read and write the input and output files respectively.
	 */
	public static final String DEFAULT_FILE_ENCODING = "UTF-8";
	/**
	 * Number of lines to filter before flushing the {@link OutputStream} to avoid keeping too much in memory when filtering large files.
	 */
	protected static final int FLUSH_LINE_THRESHOLD = 200;
	/**
	 * A {@link Map} containing a valid system properties mode Strings mapped to their corresponding Integer code used by
	 * {@link com.elasticpath.datapopulation.core.service.filtering.helper.impl.PropertyPlaceholderStringValueResolver} internally.
	 */
	protected static final Map<String, Integer> SYSTEM_PROPERTIES_MODE_MAP = Collections.unmodifiableMap(createSystemPropertiesModeMap());
	private static final Logger LOG = Logger.getLogger(FileFilterer.class);
	private final String placeholderPrefix;
	private final String placeholderSuffix;

	private Integer systemPropertiesMode;
	private String fileEncoding;

	/**
	 * Default constructor calling {@link #FileFilterer(String)} with {@link #DEFAULT_SYSTEM_PROPERTIES_MODE}.
	 */
	public FileFilterer() {
		this(DEFAULT_SYSTEM_PROPERTIES_MODE);
	}

	/**
	 * Constructor to use the {@link #DEFAULT_PLACEHOLDER_PREFIX} and {@link #DEFAULT_PLACEHOLDER_SUFFIX} along with the system properties mode
	 * specified.
	 *
	 * @param systemPropertiesMode the system properties mode to use, must match one of the constants defined in this class.
	 */
	public FileFilterer(final String systemPropertiesMode) {
		this(DEFAULT_PLACEHOLDER_PREFIX, DEFAULT_PLACEHOLDER_SUFFIX, systemPropertiesMode);
	}

	/**
	 * Constructor to use to specify the placeholder prefix, and suffix to use, along with the system properties mode specified.
	 *
	 * @param placeholderPrefix    the property placeholder prefix to use.
	 * @param placeholderSuffix    the property placeholder suffix to use.
	 * @param systemPropertiesMode the system properties mode to use, must match one of the constants defined in this class.
	 */
	@SuppressWarnings("PMD.ConstructorCallsOverridableMethod")
	public FileFilterer(final String placeholderPrefix, final String placeholderSuffix, final String systemPropertiesMode) {
		this.placeholderPrefix = placeholderPrefix;
		this.placeholderSuffix = placeholderSuffix;
		this.systemPropertiesMode = parseSystemPropertiesMode(systemPropertiesMode);
	}

	// API methods

	/**
	 * Creates a {@link Map} of valid system properties mode String values to their corresponding Integer values.
	 * These Integer values are defined by {@link PreferencesPlaceholderConfigurer} since that class is used in the underlying property resolution.
	 *
	 * @return a {@link Map} of valid system properties mode String values to their corresponding Integer values.
	 */
	protected static Map<String, Integer> createSystemPropertiesModeMap() {
		final Map<String, Integer> result = new TreeMap<>();

		result.put(SYSTEM_PROPERTIES_MODE_FALLBACK, PreferencesPlaceholderConfigurer.SYSTEM_PROPERTIES_MODE_FALLBACK);
		result.put(SYSTEM_PROPERTIES_MODE_NEVER, PreferencesPlaceholderConfigurer.SYSTEM_PROPERTIES_MODE_NEVER);
		result.put(SYSTEM_PROPERTIES_MODE_OVERRIDE, PreferencesPlaceholderConfigurer.SYSTEM_PROPERTIES_MODE_OVERRIDE);

		return result;
	}

	/**
	 * Filters the given {@link Resource}, with the given filter {@link Properties} objects, replacing property placeholders using the prefix and
	 * suffix configured, and writing the filtered result to the {@link OutputStream} returned by the given {@link Provider}.
	 * Resolution of the property placeholders will also look up properties defined as system properties, depending on the mode configured in this
	 * object.
	 * <p>
	 * Support for filtering nested property placeholders is provided. This means that inner property placeholders are first resolved before that
	 * resolved value is used when attempting to resolve the outer property placeholder(s).
	 * </p>
	 *
	 * @param inputFile            the input {@link Resource} to filter.
	 * @param outputStreamProvider a {@link Provider} which returns an {@link OutputStream} to write the filtered file to.
	 * @param filterProperties     one or more {@link Properties} objects to filter the input file with. They are searched in order for each property
	 *                             placeholder, so the order they are passed to this method is significant if a property is defined in multiple
	 *                             filter
	 *                             {@link Properties} objects.
	 * @param <OS>                 the type of {@link OutputStream} that the {@link Provider} returns; this method doesn't care.
	 * @throws IOException if there was a problem filtering the input or writing to the output.
	 */
	public <OS extends OutputStream> void filter(final Resource inputFile, final Provider<OS> outputStreamProvider,
												 final Properties... filterProperties) throws IOException {
		filter(inputFile.getInputStream(), outputStreamProvider, filterProperties);
	}

	/**
	 * Filters the given {@link Resource}, with the given filter {@link Properties} objects, replacing property placeholders using the prefix and
	 * suffix configured, and writing the filtered result to the given output {@link File}.
	 * Resolution of the property placeholders will also look up properties defined as system properties, depending on the mode configured in this
	 * object.
	 * <p>
	 * Support for filtering nested property placeholders is provided. This means that inner property placeholders are first resolved before that
	 * resolved value is used when attempting to resolve the outer property placeholder(s).
	 * </p>
	 *
	 * @param inputFile        the input {@link Resource} to filter.
	 * @param outputFile       the output file to write the filtered contents to.
	 * @param filterProperties one or more {@link Properties} objects to filter the input file with. They are searched in order for each property
	 *                         placeholder, so the order they are passed to this method is significant if a property is defined in multiple filter
	 *                         {@link Properties} objects.
	 * @throws IOException if there was a problem filtering the input or writing to the output.
	 */
	public void filter(final Resource inputFile, final File outputFile, final Properties... filterProperties) throws IOException {
		filter(inputFile, new FileSystemResource(outputFile), filterProperties);
	}

	/**
	 * Filters the given input {@link File}, with the given filter {@link Properties} objects, replacing property placeholders using the prefix and
	 * suffix configured, and writing the filtered result to the given output {@link File}.
	 * Resolution of the property placeholders will also look up properties defined as system properties, depending on the mode configured in this
	 * object.
	 * <p>
	 * Support for filtering nested property placeholders is provided. This means that inner property placeholders are first resolved before that
	 * resolved value is used when attempting to resolve the outer property placeholder(s).
	 * </p>
	 *
	 * @param inputFile        the input file to filter.
	 * @param outputFile       the output file to write the filtered contents to.
	 * @param filterProperties one or more {@link Properties} objects to filter the input file with. They are searched in order for each property
	 *                         placeholder, so the order they are passed to this method is significant if a property is defined in multiple filter
	 *                         {@link Properties} objects.
	 * @throws IOException if there was a problem filtering the input or writing to the output.
	 */
	public void filter(final File inputFile, final File outputFile, final Properties... filterProperties)
			throws IOException {
		final InputStream input = new FileInputStream(inputFile);
		final Provider<OutputStream> outputProvider = createOutputStreamProvider(outputFile);

		filter(input, outputProvider, filterProperties);
	}

	/**
	 * Filters the given {@link Resource}, with the given filter {@link Properties} objects, replacing property placeholders using the prefix and
	 * suffix configured, and writing the filtered result to the given output {@link Resource} (as long as it's writable).
	 * Resolution of the property placeholders will also look up properties defined as system properties, depending on the mode configured in this
	 * object.
	 * <p>
	 * Support for filtering nested property placeholders is provided. This means that inner property placeholders are first resolved before that
	 * resolved value is used when attempting to resolve the outer property placeholder(s).
	 * </p>
	 *
	 * @param inputFile        the input {@link Resource} to filter.
	 * @param outputFile       the output {@link Resource} to filter; must be writable otherwise an {@link IOException} is thrown when it is
	 *                         attempted.
	 * @param filterProperties one or more {@link Properties} objects to filter the input file with. They are searched in order for each property
	 *                         placeholder, so the order they are passed to this method is significant if a property is defined in multiple filter
	 *                         {@link Properties} objects.
	 * @throws IOException if there was a problem filtering the input or writing to the output.
	 */
	public void filter(final Resource inputFile, final Resource outputFile, final Properties... filterProperties) throws IOException {
		final InputStream input = inputFile.getInputStream();
		final Provider<OutputStream> outputProvider = createOutputStreamProvider(outputFile);

		filter(input, outputProvider, filterProperties);
	}

	// Implementation methods

	/**
	 * Filters the given {@link InputStream}, with the given filter {@link Properties} objects, replacing property placeholders using the prefix and
	 * suffix configured, and writing the filtered result to the given output {@link Resource} (as long as it's writable).
	 * Resolution of the property placeholders will also look up properties defined as system properties, depending on the mode configured in this
	 * object.
	 * <p>
	 * Support for filtering nested property placeholders is provided. This means that inner property placeholders are first resolved before that
	 * resolved value is used when attempting to resolve the outer property placeholder(s).
	 * </p>
	 *
	 * @param inputFile        the input {@link InputStream} to filter.
	 * @param outputFile       the output {@link Resource} to filter; must be writable otherwise an {@link IOException} is thrown when it is
	 *                         attempted.
	 * @param filterProperties one or more {@link Properties} objects to filter the input file with. They are searched in order for each property
	 *                         placeholder, so the order they are passed to this method is significant if a property is defined in multiple filter
	 *                         {@link Properties} objects.
	 * @throws IOException if there was a problem filtering the input or writing to the output.
	 */
	public void filter(final InputStream inputFile, final Resource outputFile, final Properties... filterProperties) throws IOException {
		final Provider<OutputStream> outputProvider = createOutputStreamProvider(outputFile);
		filter(inputFile, outputProvider, filterProperties);
	}

	/**
	 * Reads the content from the given {@link InputStream} and filters it with the given filter {@link Properties} objects, replacing property
	 * placeholders using the prefix and suffix configured, and writing the filtered result to the given output {@link Resource} (as long as it's
	 * writable). Resolution of the property placeholders will also look up properties defined as system properties, depending on the mode configured
	 * in this object.
	 * <p>
	 * Support for filtering nested property placeholders is provided. This means that inner property placeholders are first resolved before that
	 * resolved value is used when attempting to resolve the outer property placeholder(s).
	 * </p>
	 *
	 * @param input                the {@link InputStream} to read from.
	 * @param outputStreamProvider a {@link Provider} which returns an {@link OutputStream} to write the filtered file to.
	 * @param filterProperties     one or more {@link Properties} objects to filter the input file with. They are searched in order for each property
	 *                             placeholder, so the order they are passed to this method is significant if a property is defined in multiple
	 *                             filter
	 *                             {@link Properties} objects.
	 * @param <OS>                 the type of {@link OutputStream} that the {@link Provider} returns; this method doesn't care.
	 * @throws IOException if there was a problem filtering the input or writing to the output.
	 */
	public <OS extends OutputStream> void filter(final InputStream input, final Provider<OS> outputStreamProvider,
												 final Properties... filterProperties) throws IOException {
		final int systemPropertiesModeInteger = getSystemPropertiesMode();
		final PropertiesStringValueResolver lineFilterer = createStringValueResolver(getPlaceholderPrefix(), getPlaceholderSuffix(),
				systemPropertiesModeInteger);

		Arrays.stream(filterProperties)
				.filter(Objects::nonNull)
				.forEach(lineFilterer::addProperties);

		filter(input, outputStreamProvider, lineFilterer);
	}

	/**
	 * Reads the {@link InputStream} and writes the filtered content to the {@link OutputStream} provided by the given {@link Provider}.
	 * In order to support writing to the same location as the input, the {@link OutputStream} is not retrieved from the {@link Provider} until after
	 * the {@link InputStream} has been read in its entirety. After the {@link InputStream} has been read it is closed if it's not {@link System#in}.
	 * The OutputStream is then retrieved and written to. The {@link OutputStream} is then closed if it's not {@link System#out} or
	 * {@link System#err}. Therefore the caller does not need to close either the {@link InputStream} or {@link OutputStream} after this call
	 * completes.
	 *
	 * @param input          the {@link InputStream} to read from.
	 * @param outputProvider the {@link Provider} of the {@link OutputStream} to write to.
	 * @param lineFilterer   the filterer to use to filter each individual line.
	 * @param <OS>           the type of {@link OutputStream} that the {@link Provider} returns; this method doesn't care.
	 * @throws IOException if there is a problem reading or writing.
	 */
	protected <OS extends OutputStream> void filter(final InputStream input, final Provider<OS> outputProvider,
													final StringValueResolver lineFilterer)
			throws IOException {
		// Read the whole input in first before writing the filtered output since we could be overwriting the input
		// depending on if the same file is specified as the output file for example
		final List<String> inputLines = readInput(input, DpResourceUtils.isNotStandardIn(input));

		// Now write the filtered output, we delay resolving the OutputStream from the provider until after we've read the input
		// in case it's outputting to the same location
		filter(inputLines, outputProvider.get(), lineFilterer);
	}

	/**
	 * Reads the {@link InputStream}'s contents into a {@link List} of {@link String}s. If closeInputStream is true, the method closes the
	 * {@link InputStream} before returning.
	 *
	 * @param input            the {@link InputStream} to read.
	 * @param closeInputStream true if the {@link InputStream} should be closed before returning; false otherwise.
	 * @return the line contents of the {@link InputStream}.
	 * @throws IOException if any problem occurred reading the {@link InputStream}
	 */
	@SuppressWarnings("unchecked")
	protected List<String> readInput(final InputStream input, final boolean closeInputStream) throws IOException {
		final List<String> inputLines;

		try {
			// Enforce the file encoding so it's not platform dependent
			inputLines = IOUtils.readLines(input, getFileEncoding());
		} finally {
			if (input != null) {
				try {
					if (closeInputStream) {
						input.close();
					}
				} catch (final IOException e) {
					LOG.warn("Unable to close InputStream: + " + input, e);
				}
			}
		}

		return inputLines;
	}

	// Helper methods

	/**
	 * Uses the given {@link StringValueResolver} to iterate through the input and write the filtered lines to the {@link OutputStream} given.
	 * <p>
	 * Note: This method closes the {@link OutputStream} once complete, if closeOutputStream is true, as long as the {@link OutputStream} is not
	 * {@link System#out} or {@link System#err} this is done.
	 * </p>
	 *
	 * @param inputLines   the input to iterate through.
	 * @param output       the {@link OutputStream} to write to.
	 * @param lineFilterer the line filterer to use to filter each line before writing it out.
	 *                     {@link System#err}.
	 */
	private void filter(final List<String> inputLines, final OutputStream output, final StringValueResolver lineFilterer) {
		// Use a Writer so we can enforce the file encoding so it's not platform dependent
		final PrintWriter writer = createPrintWriter(output);

		try {
			final Iterator<String> iter = inputLines.iterator();

			for (int i = 0; iter.hasNext(); i++) {
				final String inputLine = iter.next();
				writer.println(lineFilterer.resolveStringValue(inputLine));

				// Flush each time we reach the threshold
				if (i % FLUSH_LINE_THRESHOLD == 0) {
					writer.flush();
				}
			}
		} finally {
			writer.flush();

			// Only close the Writer (and Stream) if requested and the underlying OutputStream is not STDOUT/STDERR
			if (DpResourceUtils.isNotStandardOutOrErr(output)) {
				writer.close();

				if (writer.checkError()) {
					LOG.warn("Unable to close PrintWriter: " + writer);
				}
			}
		}
	}

	/**
	 * Return the default file encoding to read and write the files in, if one isn't specified explicitly.
	 *
	 * @return the default file encoding to use, UTF-8.
	 */
	protected String getDefaultFileEncoding() {
		return DEFAULT_FILE_ENCODING;
	}

	/**
	 * Return the default system properties mode Integer, this is the one associated with {@link #DEFAULT_SYSTEM_PROPERTIES_MODE} though sub-classes
	 * can override to provide a different default if desired.
	 *
	 * @return Integer corresponding to {@link #DEFAULT_SYSTEM_PROPERTIES_MODE}.
	 */
	protected Integer getDefaultSystemPropertiesMode() {
		return parseSystemPropertiesMode(DEFAULT_SYSTEM_PROPERTIES_MODE);
	}

	// Factory Methods

	/**
	 * Parses the Integer corresponding to the system properties mode given. If an unrecognized value is provided a {@link IllegalArgumentException}
	 * is thrown.
	 *
	 * @param systemPropertiesMode the system properties String constant value to parse. See this class' constants for available values.
	 * @return the Integer corresponding to the system properties mode given, never null.
	 */
	protected final Integer parseSystemPropertiesMode(final String systemPropertiesMode) {
		if (StringUtils.isBlank(systemPropertiesMode)) {
			throw new IllegalArgumentException("No system properties mode given, if the default is supposed to be used, pass in the "
					+ "DEFAULT_SYSTEM_PROPERTIES_MODE class constant value.");
		}

		final Integer result = SYSTEM_PROPERTIES_MODE_MAP.get(systemPropertiesMode);
		if (result == null) {
			throw new IllegalArgumentException("Unknown system properties mode: " + systemPropertiesMode);
		}

		return result;
	}

	/**
	 * Creates a {@link PrintWriter} for the given {@link OutputStream} using the configured file encoding to use (see {@link #getFileEncoding()}.
	 *
	 * @param output the {@link OutputStream} to create a {@link PrintWriter} for.
	 * @return a {@link PrintWriter} for the given {@link OutputStream} using the configured file encoding to use (see {@link #getFileEncoding()}.
	 */
	protected PrintWriter createPrintWriter(final OutputStream output) {
		return createPrintWriter(output, getFileEncoding());
	}

	/**
	 * Creates a {@link PrintWriter} for the given {@link OutputStream} using the explicit file encoding given.
	 * Throws a {@link FilterFileActionException} if the file encoding given is invalid.
	 *
	 * @param output       the {@link OutputStream} to create a {@link PrintWriter} for.
	 * @param fileEncoding the file encoding to use.
	 * @return a {@link PrintWriter} for the given {@link OutputStream} and file encoding.
	 * @throws FilterFileActionException if the file encoding given is invalid.
	 */
	protected PrintWriter createPrintWriter(final OutputStream output, final String fileEncoding) throws FilterFileActionException {
		final PrintWriter result;

		try {
			result = new PrintWriter(new OutputStreamWriter(output, fileEncoding));
		} catch (final UnsupportedEncodingException e) {
			throw new FilterFileActionException("Error: Unable to output file in encoding '" + fileEncoding
					+ "." + DpUtils.getNestedExceptionMessage(e), e);
		}

		return result;
	}

	/**
	 * Creates a {@link PropertiesStringValueResolver} implementation to resolve the given placeholder information.
	 *
	 * @param placeholderPrefix    the property placeholder prefix to use.
	 * @param placeholderSuffix    the property placeholder suffix to use.
	 * @param systemPropertiesMode the system properties mode to use, must match one of the constants defined in this class.
	 * @return a {@link PropertiesStringValueResolver} implementation that resolves the given placeholder information.
	 */
	protected PropertiesStringValueResolver createStringValueResolver(final String placeholderPrefix, final String placeholderSuffix,
																	  final int systemPropertiesMode) {
		return new PropertyPlaceholderStringValueResolver(placeholderPrefix, placeholderSuffix, systemPropertiesMode, true);
	}

	/**
	 * Creates a {@link Provider} object which provides an {@link OutputStream} for the given {@link File}.
	 *
	 * @param outputFile the {@link File} to provide an {@link OutputStream} for.
	 * @return a {@link Provider} object which provides an {@link OutputStream} for the given {@link File}.
	 */
	protected Provider<OutputStream> createOutputStreamProvider(final File outputFile) {
		return createOutputStreamProvider(new FileSystemResource(outputFile));
	}

	// Getters and Setters

	/**
	 * Creates a {@link Provider} object which provides an {@link OutputStream} for the given {@link Resource}.
	 *
	 * @param outputFile the {@link Resource} to provide an {@link OutputStream} for.
	 * @return a {@link Provider} object which provides an {@link OutputStream} for the given {@link Resource}.
	 */
	protected Provider<OutputStream> createOutputStreamProvider(final Resource outputFile) {
		return new ResourceOutputStreamProvider(outputFile);
	}

	/**
	 * Gets the placeholder prefix used by this object.
	 *
	 * @return the placeholder prefix in use.
	 */
	public String getPlaceholderPrefix() {
		return this.placeholderPrefix;
	}

	/**
	 * Gets the placeholder suffix used by this object.
	 *
	 * @return the placeholder suffix in use.
	 */
	public String getPlaceholderSuffix() {
		return this.placeholderSuffix;
	}

	/**
	 * Gets the system properties mode Integer, returning {@link #getDefaultSystemPropertiesMode()} if one hasn't been explicitly configured.
	 *
	 * @return the system properties mode Integer, never null.
	 */
	protected Integer getSystemPropertiesMode() {
		if (this.systemPropertiesMode == null) {
			this.systemPropertiesMode = getDefaultSystemPropertiesMode();
		}
		return this.systemPropertiesMode;
	}

	/**
	 * Sets the explicit system properties mode to use, by calling {@link #parseSystemPropertiesMode(String)} to parse the given String into its
	 * corresponding Integer value.
	 *
	 * @param systemPropertiesMode the explicit system properties mode to use.
	 */
	public void setSystemPropertiesMode(final String systemPropertiesMode) {
		setSystemPropertiesMode(parseSystemPropertiesMode(systemPropertiesMode));
	}

	/**
	 * Sets the explicit system properties mode Integer to use.
	 *
	 * @param systemPropertiesMode the explicit system properties mode Integer to use.
	 */
	public void setSystemPropertiesMode(final Integer systemPropertiesMode) {
		this.systemPropertiesMode = systemPropertiesMode;
	}

	/**
	 * Gets the file encoding in use by this object, returning {@link #getDefaultFileEncoding()} ()} if one hasn't been explicitly configured.
	 *
	 * @return the file encoding in use by this object, never null.
	 */
	public String getFileEncoding() {
		if (this.fileEncoding == null) {
			this.fileEncoding = getDefaultFileEncoding();
		}
		return this.fileEncoding;
	}

	// Static methods

	/**
	 * Sets the file encoding to use by this object.
	 *
	 * @param fileEncoding the file encoding to use.
	 */
	public void setFileEncoding(final String fileEncoding) {
		this.fileEncoding = fileEncoding;
	}
}
