/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.datapopulation.core.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.elasticpath.datapopulation.core.exceptions.DataPopulationActionException;

/**
 * Tests the {@link com.elasticpath.datapopulation.core.utils.ClasspathResourceResolverUtil} class.
 */
public class ClasspathResourceResolverUtilTest {

	private final ClasspathResourceResolverUtil classpathResolver = new ClasspathResourceResolverUtil();

	@Test
	public void testGetFileInputStream() throws IOException {
		InputStream ins = classpathResolver.getFileResourceStream("database-reset-sql/h2.sql");

		assertThat(ins)
				.as("The input stream for h2.sql should not be null")
				.isNotNull();
		String string = IOUtils.toString(ins);

		assertThat(string)
				.as("The input stream should be able to be read from")
				.isNotNull();
		assertThat(string)
				.as("The input file should contain ${data.population.schemaname}")
				.contains("${data.population.schemaname}");
	}

	@Test
	public void testGetUnfilteredSqlFileWithFallback() throws IOException {
		SqlInputStream ins = classpathResolver.getSqlInputStreamWithFallback("h2-h3-h4-h5-h6", "database-reset-sql/%s");

		assertThat(ins)
				.as("The input stream for h2.sql should not be null")
				.isNotNull();

		String string = IOUtils.toString(ins.getStream());

		assertThat(string)
				.as("The input stream should be able to be read from")
				.isNotNull();
		assertThat(string)
				.as("The input file should contain ${data.population.schemaname}")
				.contains("${data.population.schemaname}");

	}

	@Test
	public void testGetUnfilteredUrlFileWithFallback() throws IOException {
		InputStream ins = classpathResolver.getFileResourceStreamWithFallback("h2-h3-h4-h5-h6", "database-url/%s.properties");

		assertThat(ins)
				.as("The input stream for h2.properties should not be null")
				.isNotNull();

		String string = IOUtils.toString(ins);

		assertThat(string)
				.as("The input stream should be able to be read from")
				.isNotNull();
		assertThat(string)
				.as("The input file should contain data.population.url")
				.contains("data.population.url");
	}

	@Test(expected = DataPopulationActionException.class)
	public void testGetMultipleUnfilteredSqlFileThrowException() {
		classpathResolver.getSqlInputStreamWithFallback("multiple-test1-test2", "database-reset-sql/%s");
	}
}
