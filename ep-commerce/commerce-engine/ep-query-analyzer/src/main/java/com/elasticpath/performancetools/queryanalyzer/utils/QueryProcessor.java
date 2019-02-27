/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.performancetools.queryanalyzer.utils;

import static com.elasticpath.performancetools.queryanalyzer.utils.Utils.processMultiLineQueries;

import java.io.BufferedReader;
import java.util.regex.Matcher;

import org.apache.commons.lang3.StringUtils;

import com.elasticpath.performancetools.queryanalyzer.beans.JPAQuery;
import com.elasticpath.performancetools.queryanalyzer.beans.SQLQuery;

/**
 * JPA/SQL query processor.
 */
public final class QueryProcessor {

	private static final int ONE_SECOND_MILLIS = 1000;

	private QueryProcessor() {
		//util class
	}

	/**
	 * Parses SQL query lines.
	 *
	 * @param line     First line with SQL query.
	 * @param reader   the reader to read from.
	 * @param jpaQuery JPA query parent.
	 * @return the next log line.
	 * @throws Exception exception.
	 */
	public static String processSQLQueryLine(final String line, final BufferedReader reader, final JPAQuery jpaQuery) throws Exception {
		final StringBuilder queryBuffer = new StringBuilder(line);
		final String processedLine = processMultiLineQueries(reader, queryBuffer);

		Matcher matcher = getSQLPatternMatcher(processedLine, queryBuffer);

		if (matcher.find()) {

			final String sqlQuery = reformatSQLQuery(matcher.group(1));

			long sqlQueryExeTimeMs = getSQLQueryExeTimeMillis(processedLine);

			//JPA query always precedes SQL one and it's never null
			jpaQuery.addSQLQuery(new SQLQuery(sqlQuery, sqlQueryExeTimeMs));
		}
		return processedLine;
	}

	private static long getSQLQueryExeTimeMillis(final String processedLine) {
		if (processedLine == null) {
			return 0L;
		}

		final Matcher matcher = Patterns.SQL_QUERY_EXE_TIME_PATTERN.matcher(processedLine);

		if (!matcher.find()) {
			return 0L;
		}

		final String sqlQueryExeTimeStr = matcher.group(1);
		final String[] exeTimeTokens = sqlQueryExeTimeStr.split(" ");

		if ("ms".equalsIgnoreCase(exeTimeTokens[1])) {
			return Long.parseLong(exeTimeTokens[0]);
		}
		//to simplify things, it is assumed that the time unit is in seconds (everything else would be horrible)

		return Long.parseLong(exeTimeTokens[0]) * ONE_SECOND_MILLIS;
	}

	private static Matcher getSQLPatternMatcher(final String processedLine, final StringBuilder queryBuffer) {
		if (processedLine == null) {
			return Patterns.SQL_PATTERN_DATE_OPTIONAL.matcher(queryBuffer.toString());
		}

		return Patterns.SQL_PATTERN.matcher(queryBuffer.toString());
	}

	/**
	 * Removes CRLF and TAB chars from SQL query and embeds parameters into
	 * query.
	 *
	 * @param sqlQuery SQL query to be reformatted.
	 * @return reformatted SQL query.
	 */
	public static String reformatSQLQuery(final String sqlQuery) {
		final String[] queryParts = sqlQuery.split("\n");
		final StringBuilder buffer = new StringBuilder();

		String params = "";
		for (String queryPart : queryParts) {
			if (queryPart.contains("params")) {
				//remove []
				params = queryPart.replaceAll("[\\[\\]]", "").trim();
			} else {
				buffer.append(queryPart.trim()).append(' ');
			}
		}

		/*
			params=(String) 80E0B57C-EEF0-646D-105E-056DD9C1D25D, (String) MOBEE

			 first split gives
			 params
			 (String) 80E0B57C-EEF0-646D-105E-056DD9C1D25D, (String) MOBEE

			 second split on second token gives
			 (String) 80E0B57C-EEF0-646D-105E-056DD9C1D25D
			 (String) MOBEE

		 */

		if (StringUtils.isBlank(params)) {
			return buffer.toString();
		}

		String[] paramsTokens = params.split("=")[1].split(", ");
		String[] tokens;
		for (String paramToken : paramsTokens) {
			tokens = paramToken.split(" "); //Param_Type - Param_Value
			//(String)
			// MOBEE
			paramToken = tokens.length == 2 ? tokens[1] : ""; //actual param value
			if (tokens[0].contains("String")) {
				paramToken = "\'" + paramToken + "\'";
			}

			//replace ? with actual parameter value
			int questionMarkPos = buffer.indexOf("?");
			buffer.replace(questionMarkPos, questionMarkPos + 1, paramToken);
		}

		return buffer.toString();
	}
}
