/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.core.utils;

import java.io.IOException;
import java.io.OutputStream;
import javax.inject.Provider;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.Resource;

/**
 * A {@link Provider} of a {@link OutputStream} to allow the {@link OutputStream} to only be created when required.
 * This is useful for {@link java.io.FileOutputStream}s in particular as it means the file won't be overwritten until it is requested.
 * This class therefore allows the same file to be read, transformed/filtered, and then written back.
 */
public class ResourceOutputStreamProvider implements Provider<OutputStream> {
	private final Resource resource;

	/**
	 * Constructor taking in the {@link Resource} to retrieve its {@link OutputStream} when requested.
	 *
	 * @param resource the {@link Resource} to retrieve its {@link OutputStream} when requested.
	 */
	public ResourceOutputStreamProvider(final Resource resource) {
		this.resource = resource;
	}

	@Override
	public OutputStream get() {
		OutputStream result = null;

		final Resource resource = getResource();
		if (resource != null) {
			try {
				result = getOutputStream(resource);
			} catch (final IOException e) {
				throw new IllegalArgumentException("Unable to create OutputStream for resource: " + resource + ". "
						+ DpUtils.getNestedExceptionMessage(e),
						e);
			}
		}

		return result;
	}

	/**
	 * Returns an {@link OutputStream} for the given {@link Resource}. Currently the module depends on Spring 3.0 and so we cannot test the
	 * {@link Resource} to see if it implements WritableResource since that was introduced in Spring 3.1. Instead
	 * {@link org.springframework.core.io.Resource#getFile()} is called, and a {@link java.io.FileOutputStream} is returned for that file.
	 * In future, when the Spring dependency in increased to &gt;= 3.1, this method should be refactored to use the WritableResource interface.
	 *
	 * @param resource the resource to retrieve its {@link OutputStream} if available.
	 * @return the resource's {@link OutputStream} if available.
	 * @throws IOException if there was a problem retrieving the {@link Resource}'s {@link OutputStream}.
	 */
	public OutputStream getOutputStream(final Resource resource) throws IOException {
		// Cannot currently use the WritableResource interface since that was introduced in Spring 3.1
		// Until our EP dependency's Spring version dependency is increased we'll only support Resource.getFile()

		// Use FileUtils.openOutputStream() to ensure the parent directory of the outputFile already exists even if the file itself doesn't
		return FileUtils.openOutputStream(resource.getFile());
	}

	// Getters and Setters

	public Resource getResource() {
		return this.resource;
	}
}
