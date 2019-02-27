/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.performancetools.queryanalyzer.utils;

import java.io.BufferedReader;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;

import org.apache.commons.lang3.StringUtils;

import com.elasticpath.performancetools.queryanalyzer.beans.JPAQuery;
import com.elasticpath.performancetools.queryanalyzer.beans.Operation;

/**
 * Util class for performing various operations.
 */
public final class Utils {

	/**
	 * Log timestamp format.
	 */
	public static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss,S";
	private static final String DEFAULT_OUTPUT_JSON_FILE_PATH = "/ep/db_statistics.json";

	private Utils() {
		//Utility class
	}

	/**
	 * Return the reference to an output JSON file, if not overridden by
	 * -Dprint.json.to.console.only system property.
	 *
	 * @return null if JSON output is redirected to console;
	 * otherwise reference to JSON output file.
	 */
	public static File getOutputJSONFileIfEnabled() {
		if (StringUtils.isNotBlank(System.getProperty(SystemProperties.PRINT_JSON_TO_CONSOLE_ONLY_SYSTEM_PROPERTY))) {
			return null;
		}

		String outputJSONFilePath = System.getProperty(SystemProperties.OUTPUT_JSON_FILE_PATH_SYSTEM_PROPERTY);
		if (StringUtils.isBlank(outputJSONFilePath)) {
			outputJSONFilePath = System.getProperty("user.home") + DEFAULT_OUTPUT_JSON_FILE_PATH;
		}

		final File outputJSONFile = new File(outputJSONFilePath);
		final File parent = outputJSONFile.getParentFile();

		if (!parent.exists() && parent.isDirectory() && !parent.mkdirs()) {
			throw new IllegalStateException("Folder [" + parent.getAbsolutePath() + "] couldn't be created");
		}

		return outputJSONFile;
	}

	/**
	 * Set operation thread name.
	 *
	 * @param threadNameMatcher the thread name matcher.
	 * @param operation         the operation to set thread name to.
	 */
	public static void setOperationThreadName(final Matcher threadNameMatcher, final Operation operation) {
		if (threadNameMatcher.find()) {
			final String threadName = threadNameMatcher.group(1);
			operation.setThread(threadName);
		}
	}

	/**
	 * Set operation timestamp.
	 *
	 * @param timestampMatcher    the timestamp matcher.
	 * @param operation           the operation to set timestamp to.
	 * @param isPreviousOperation flag indicating whether given operation is new or old.
	 * @throws Exception the exception.
	 */
	public static void setOperationTimestamp(final Matcher timestampMatcher, final Operation operation,
											 final boolean isPreviousOperation) throws Exception {
		if (timestampMatcher.find()) {
			final String timestamp = timestampMatcher.group(1);
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN, Locale.getDefault());
			final Date date = simpleDateFormat.parse(timestamp);
			if (isPreviousOperation) {
				operation.setFinishedAt(date);
			} else {
				operation.setStartedAt(date);
			}
		}
	}

	/**
	 * Set operation start/stop timestamps.
	 *
	 * @param timestampMatcher the timestamp matcher
	 * @param operation        the operation
	 * @param isStop           a flag indicating whether start or stop time should be set
	 * @throws Exception the exception
	 */
	public static void setOperationJpaStartStopTimestamp(final Matcher timestampMatcher, final Operation operation,
														 final boolean isStop) throws Exception {
		if (timestampMatcher.find()) {
			final String timestamp = timestampMatcher.group(1);
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN, Locale.getDefault());
			final Date date = simpleDateFormat.parse(timestamp);
			if (isStop) {
				operation.setJpaFinishedOfAt(date);
			} else {
				operation.setJpaKickedInAt(date);
			}
		}
	}

	/**
	 * Set a starting timestamp of a JPA query being executed.
	 *
	 * @param timestampMatcher the matcher
	 * @param jpaQuery         the JPAQuery instance
	 * @throws Exception the exception
	 */
	public static void setJpaQueryStartTimestamp(final Matcher timestampMatcher, final JPAQuery jpaQuery) throws Exception {
		if (timestampMatcher.find()) {
			final String timestamp = timestampMatcher.group(1);
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN, Locale.getDefault());
			final Date date = simpleDateFormat.parse(timestamp);
			jpaQuery.setStartedAt(date);
		}
	}

	/**
	 * Set found eager relations to a JPA query instance.
	 *
	 * @param eagerRelationsMatcher the matcher for eager relations string.
	 * @param jpaQuery              the JPA query instance to set eager relations to.
	 */
	public static void setJPAQueryEagerRelations(final Matcher eagerRelationsMatcher, final JPAQuery jpaQuery) {
		if (eagerRelationsMatcher.find()) {
			final String eagerRelations = eagerRelationsMatcher.group(1);
			jpaQuery.addEagerRelations(eagerRelations);
		}
	}

	/**
	 * JPA and SQL queries are usually broken into several lines. This method aggregates all query lines
	 * into a single buffer, until next line, starting with timestamp, is found.
	 *
	 * @param reader      the reader to read from.
	 * @param queryBuffer the buffer for storing query lines.
	 * @return the last line starting with timestamp.
	 * @throws Exception the exception.
	 */
	public static String processMultiLineQueries(final BufferedReader reader, final StringBuilder queryBuffer) throws Exception {
		queryBuffer.append('\n');

		String line;
		while ((line = reader.readLine()) != null) {
			final Matcher timestampMatcher = Patterns.TIMESTAMP_PATTERN.matcher(line);
			if (timestampMatcher.find()) {
				queryBuffer.append(line);
				break;
			}
			queryBuffer.append(line).append('\n');
		}

		return line;
	}

	/**
	 * Removes TAB and replace CR chars with single space.
	 *
	 * @param input string to be processed.
	 * @return clean string.
	 */
	public static String removeTabAndCRChars(final String input) {
		return input.replaceAll("\\t", "").replaceAll("\\n", " ");
	}
}
