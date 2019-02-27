/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.core.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.Test;

public class DpResourceUtilsTest {

	@Test
	public void verifyGetFileResourceReturnsNullWhenResourceGivenIsBlank() {
		String resourceUri = DpResourceUtils.getFileResourceUriByDefault(" ", false);

		assertThat(resourceUri).isNull();
	}

	@Test
	public void verifyGetFileResourceReturnsPathWhenResourceGivenShouldNotExist() {
		String resourceUri
				= DpResourceUtils.getFileResourceUriByDefault("test-context.xml", false);

		assertThat(resourceUri)
				.endsWith("/commerce-engine/data-population/ep-data-population-core/test-context.xml");
	}

	@Test
	public void verifyGetFileResourceReturnsPathWhenResourceGivenShouldExist() {
		String resourceUri
				= DpResourceUtils.getFileResourceUriByDefault("src/test/resources/test-context.xml", true);

		assertThat(resourceUri)
				.endsWith("/commerce-engine/data-population/ep-data-population-core/src/test/resources/test-context.xml");
	}

	@Test
	public void verifyGetFileResourceThrowsExceptionWhenResourceGivenShouldExist() {
		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() ->
						DpResourceUtils
								.getFileResourceUriByDefault("test-context.xml", true))
				.withMessageStartingWith("The file specified is not referencing a file on the filesystem directly, this is required.");
	}

	@Test
	public void verifyBadURLThrowsExpectedException() {
		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() ->
						DpResourceUtils
								.getFileResourceUriByDefault("classpath:some-file.xml", true))
				.withMessageStartingWith("Unable to parse location or file does not exist. ");
	}
}