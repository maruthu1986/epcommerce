/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.email.producer.spi.composer.util.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.junit.Test;

/**
 * Test class for {@link TemplatePathResolverImpl}.
 */
public class TemplatePathResolverImplTest {

	@Test
	public void verifyTemplatePathIsConcatenationOfBaseDirectoryPlusGivenFilename() throws Exception {
		final String filename = "myfile.html";
		final String expectedOutput = "email" + File.separator + "myfile.html.vm";

		assertThat(new TemplatePathResolverImpl().apply(filename))
				.isEqualTo(expectedOutput);
	}

}