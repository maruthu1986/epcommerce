/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.performancetools.queryanalyzer.utils;

import java.util.regex.Pattern;

/**
 * Holder for all required patterns.
 */
public final class Patterns {

	/**
	 * Pattern for extracting thread names.
	 */
	public static final Pattern THREAD_PATTERN = Pattern.compile(".*?\\s\\[(.*?)\\].*?");
	/**
	 * Pattern for extracting timestamp.
	 */
	public static final Pattern TIMESTAMP_PATTERN = Pattern.compile("^(.*)? \\[");
	/**
	 * Pattern for extracting jpa eager relations.
	 */
	public static final Pattern EAGER_RELATIONS_PATTERN = Pattern.compile(Markers.EAGER_RELATIONS_MARKER + "\\[(.*?)\\]$");
	/**
	 * Pattern for extracting db table names.
	 */
	public static final Pattern TABLE_PATTERN = Pattern.compile(".*?FROM (\\bT.+?\\b).*?");
	/**
	 * Pattern for extracting JPA entity names.
	 */
	public static final Pattern JPA_ENTITY_PATTERN = Pattern.compile("FROM (.*?)\\s");
	/**
	 * Pattern for extracting SQL exe times.
	 */
	public static final Pattern SQL_QUERY_EXE_TIME_PATTERN = Pattern.compile(">.*?\\[(.+?)\\] spent");
	private static final String DATE_PATTERN_STRING = "(\\d{4}-\\d{2}-\\d{2})";
	private static final String JPA_QUERY_PATTERN_BASE_STRING = "(?s)" + Markers.JPA_QUERY_MARKER + " (.*)" + DATE_PATTERN_STRING;
	/**
	 * Pattern for extracting JPA queries when log file contains more lines after JPA query line.
	 */
	public static final Pattern JPA_QUERY_PATTERN = Pattern.compile(JPA_QUERY_PATTERN_BASE_STRING);
	/**
	 * Pattern for extracting JPA queries when JPA query line is the last one.
	 */
	public static final Pattern JPA_QUERY_PATTERN_DATE_OPTIONAL = Pattern.compile(JPA_QUERY_PATTERN_BASE_STRING + "?");
	private static final String SQL_PATTERN_BASE_STRING = "(?s)" + Markers.SQL_QUERY_MARKER + "\\d+\\s*(.*)?" + DATE_PATTERN_STRING;
	/**
	 * Pattern for extracting SQL queries when log file contains more lines after SQL query line..
	 */
	public static final Pattern SQL_PATTERN = Pattern.compile(SQL_PATTERN_BASE_STRING);
	/**
	 * Pattern for extracting SQL queries when SQL query line is the last one.
	 */
	public static final Pattern SQL_PATTERN_DATE_OPTIONAL = Pattern.compile(SQL_PATTERN_BASE_STRING + "?");

	private Patterns() {
		//constant holder
	}

}
