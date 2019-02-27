/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.performancetools.queryanalyzer.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.regex.Matcher;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Test class for {@link Patterns}.
 */
@RunWith(MockitoJUnitRunner.class)
public class PatternsTest {

	@Test
	public void shouldMatchJPAQueryPattern() {
		final Matcher matcher = Patterns.JPA_QUERY_PATTERN.matcher("2016-06-14 09:49:32,919 [http-bio-9080-exec-11] "
				+ "TRACE org.apache.openjpa.lib.log.SLF4JLogFactory$LogAdapter - "
				+ "Executing query: [SELECT access"
				+ "\n2016-06-14 09:49:32,930 [http-bio-9080-exec-10]");

		assertThat(matcher.find())
				.isTrue();

		assertThat(matcher.group(1).trim())
				.isEqualTo("[SELECT access");
	}

	@Test
	public void shouldMatchSQLQueryPattern() {
		final Matcher matcher = Patterns.SQL_PATTERN.matcher("2016-06-14 09:49:32,918 [http-bio-9080-exec-12] "
				+ "TRACE org.apache.openjpa.lib.log.SLF4JLogFactory$LogAdapter - "
				+ "executing prepstmnt 1202989754 SELECT t0.GUID"
				+ "\n2016-06-14 09:49:32,930 [http-bio-9080-exec-10]");

		assertThat(matcher.find())
				.isTrue();

		assertThat(matcher.group(1).trim())
				.isEqualTo("SELECT t0.GUID");
	}

	@Test
	public void shouldMatchThreadNamePattern() {
		final Matcher matcher = Patterns.THREAD_PATTERN.matcher("2016-06-14 09:49:32,819 [http-bio-9080-exec-13] ");

		assertThat(matcher.find())
				.isTrue();

		assertThat(matcher.group(1).trim())
				.isEqualTo("http-bio-9080-exec-13");
	}

	@Test
	public void shouldMatchTimestampPattern() {
		final Matcher matcher = Patterns.TIMESTAMP_PATTERN.matcher("2016-06-14 08:49:32,919 [http-bio-9080-exec-14] ");

		assertThat(matcher.find())
				.isTrue();

		assertThat(matcher.group(1).trim())
				.isEqualTo("2016-06-14 08:49:32,919");


	}

	@Test
	public void shouldMatchEagerRelationsPattern() {
		final Matcher matcher = Patterns.EAGER_RELATIONS_PATTERN.matcher("2016-06-14 07:49:32,919 [http-bio-9080-exec-17] "
				+ "TRACE org.apache.openjpa.lib.log.SLF4JLogFactory$LogAdapter - "
				+ "Eager relations: [com.pkg.EntityImpl.eagerRelation]");

		assertThat(matcher.find())
				.isTrue();

		assertThat(matcher.group(1).trim())
				.isEqualTo("com.pkg.EntityImpl.eagerRelation");
	}

	@Test
	public void shouldMatchTablePattern() {
		final Matcher matcher = Patterns.TABLE_PATTERN.matcher("FROM TCARTORDER t0 CROSS JOIN TSHOPPINGCART t1");

		assertThat(matcher.find())
				.isTrue();

		assertThat(matcher.group(1).trim())
				.isEqualTo("TCARTORDER");
	}

	@Test
	public void shouldMatchJPAEntityPattern() {
		final Matcher matcher = Patterns.JPA_ENTITY_PATTERN.matcher("FROM CartOrderImpl AS co");

		assertThat(matcher.find())
				.isTrue();

		assertThat(matcher.group(1).trim())
				.isEqualTo("CartOrderImpl");
	}

	@Test
	public void shouldMatchSQLQueryExeTimePattern() {
		final Matcher matcher = Patterns.SQL_QUERY_EXE_TIME_PATTERN.matcher("<t 2101959319, conn 1296868722> [1 ms] spent");

		assertThat(matcher.find())
				.isTrue();

		assertThat(matcher.group(1).trim())
				.isEqualTo("1 ms");
	}
}
