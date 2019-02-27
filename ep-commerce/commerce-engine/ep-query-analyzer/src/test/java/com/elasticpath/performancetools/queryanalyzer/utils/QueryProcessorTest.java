/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.performancetools.queryanalyzer.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.elasticpath.performancetools.queryanalyzer.beans.JPAQuery;
import com.elasticpath.performancetools.queryanalyzer.beans.SQLQuery;

/**
 * Test class for {@link QueryProcessor}.
 */
@RunWith(MockitoJUnitRunner.class)
public class QueryProcessorTest {

	@Test
	public void shouldProcessMultipleSQLLines() throws Exception {
		final String firstSQLLine = "2016-06-14 09:49:32,919 [http-bio-9080-exec-10] "
				+ "TRACE org.apache.openjpa.lib.log.SLF4JLogFactory$LogAdapter - "
				+ "executing prepstmnt 1202989754 SELECT field";

		final String multiLineQuery = "FROM Table t\n"
				+ "WHERE t.field2=?\n"
				+ "[params=(String) some_string_value]\n"
				+ "2016-06-14 09:49:32,963 [http-bio-9080-exec-10] <t 2101959319, conn 1296868722> [1 ms] spent";

		final BufferedReader bufferedReader = new BufferedReader(new StringReader(multiLineQuery));

		final JPAQuery jpaQuery = new JPAQuery("jpa query");

		QueryProcessor.processSQLQueryLine(firstSQLLine, bufferedReader, jpaQuery);

		final List<SQLQuery> sqlQueries = jpaQuery.getSqlQueries();

		assertThat(sqlQueries)
				.hasSize(1);

		final SQLQuery sqlQuery = sqlQueries.iterator().next();

		assertThat(sqlQuery.getQuery().trim())
				.isEqualTo("SELECT field FROM Table t WHERE t.field2='some_string_value'");

		assertThat(sqlQuery.getExeTimeMs())
				.isEqualTo(1);

		bufferedReader.close();
	}

	@Test
	public void shouldReturnSingleLineSQLQueryWithInlinedParametersWhenFound() {
		final String multiLineQuery = "SELECT field FROM Table t\n"
				+ "WHERE t.field2=?\n"
				+ "[params=(String) some_string_value]\n";

		final String expected = "SELECT field FROM Table t WHERE t.field2='some_string_value'";
		final String actual = QueryProcessor.reformatSQLQuery(multiLineQuery);

		assertThat(actual.trim())
				.isEqualTo(expected);
	}

	@Test
	public void shouldReturnSingleLineSQLQueryWhenParametersAreNotFound() {
		final String multiLineQuery = "SELECT field FROM Table t\n"
				+ "INNER JOIN Table2 t2 ON t2.field=t.field\n";

		final String expected = "SELECT field FROM Table t INNER JOIN Table2 t2 ON t2.field=t.field";
		final String actual = QueryProcessor.reformatSQLQuery(multiLineQuery);

		assertThat(actual.trim())
				.isEqualTo(expected);
	}

}
