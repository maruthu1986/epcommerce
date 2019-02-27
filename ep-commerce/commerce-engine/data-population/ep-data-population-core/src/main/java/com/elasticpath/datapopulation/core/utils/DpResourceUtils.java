/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.core.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.util.ResourceUtils;

/**
 * A simple utility class for Resource-based functionality (Spring Resource primarily).
 */
public final class DpResourceUtils {

	private static final Logger LOG = Logger.getLogger(DpResourceUtils.class);

	/**
	 * This class is intended as as a static utility class, so it is not intended to be instantiated.
	 */
	private DpResourceUtils() {
		super();
	}

	/**
	 * Returns a Spring {@link org.springframework.core.io.Resource} uri for the given resource location, if an explicit resource scheme has not
	 * been specified such as 'file:' or 'classpath:', then the path is converted explicitly to use a file scheme, so that Spring defaults to
	 * locating it on the filesystem, rather than using its default resource loader, which could be a classpath resource loader.
	 * If mustExistOnFileSystem is true then if there is an explicit resource loader (scheme) configured in the resource location passed in, and its
	 * not the 'file:' scheme, or if the file it points to does not exist, then an {@link IllegalArgumentException} is thrown.
	 *
	 * @param resourceLocationGiven the resource location to use
	 * @param mustExistOnFileSystem true if the resource location must refer to a file system resource (either explicitly or implicitly), and that
	 *                              file must exist on the file system; false otherwise.
	 * @return a Spring {@link org.springframework.core.io.Resource} uri for the given resource location, using the 'file:' scheme if one has not
	 * been explicitly specified.
	 * @throws IllegalArgumentException if the resource uri was unable to be parsed; or if mustExistOnFileSystem is true and the file does not exist;
	 *                                  or if mustExistOnFileSystem is true and an explicit scheme was specified that was not 'file:'.
	 */
	public static String getFileResourceUriByDefault(final String resourceLocationGiven, final boolean mustExistOnFileSystem) {

		if (StringUtils.isBlank(resourceLocationGiven)) {
			return null;
		}

		final String resourceLocation = normalizeLocation(resourceLocationGiven);

		URL resourceUrl = null;

		try {
			resourceUrl = ResourceUtils.getURL(resourceLocation);

			final boolean fileSchemeUsed = ResourceUtils.isFileURL(resourceUrl);
			File file = ResourceUtils.getFile(resourceLocation);
			file = getCanonicalFile(file);

			if (fileSchemeUsed && mustExistOnFileSystem) {
				if (!file.exists()) {
					throw new IllegalArgumentException("The file specified is not referencing a file on the filesystem directly, this is required.");
				}
				resourceUrl = ResourceUtils.getURL(file.getAbsolutePath());
			}
		} catch (final FileNotFoundException e) {
			throw new IllegalArgumentException("Unable to parse location or file does not exist. " + DpUtils.getNestedExceptionMessage(e), e);
		}

		String result = resourceUrl.toExternalForm();

		LOG.debug("Resource uri generated for '" + resourceLocationGiven + "': " + result);

		return result;
	}

	private static File getCanonicalFile(final File file) {
		File retFile;
		try {
			retFile = file.getCanonicalFile();
		} catch (final IOException e) {
			retFile = file;
			LOG.warn("Unable to canonicalize file or regenerate the resource url, so sticking with resource url", e);
		}
		return retFile;
	}

	/**
	 * Replace any double forward slashes that happen when appending relative path to a directory
	 * since directory paths are generated with a trailing / since it's a uri path.
	 *
	 * @param locationGiven location to normalize.
	 * @return normalized location.
	 */
	private static String normalizeLocation(final String locationGiven) {
		return StringUtils.replace(locationGiven, "//", "/");
	}

	/**
	 * Returns true if the {@link InputStream} given is not null or {@link System#in}; false otherwise.
	 *
	 * @param inputStream the {@link InputStream} to check
	 * @return true if the {@link InputStream} given is not null or {@link System#in}; false otherwise.
	 */
	public static boolean isNotStandardIn(final InputStream inputStream) {
		return (inputStream != null && inputStream != System.in);
	}

	/**
	 * Returns true if the {@link OutputStream} given is not null, {@link System#out} or {@link System#err}; false otherwise.
	 *
	 * @param outputStream the {@link OutputStream} to check
	 * @return true if the {@link OutputStream} given is not null, {@link System#out} or {@link System#err}; false otherwise.
	 */
	public static boolean isNotStandardOutOrErr(final OutputStream outputStream) {
		return (outputStream != null && outputStream != System.out && outputStream != System.err);
	}
}
