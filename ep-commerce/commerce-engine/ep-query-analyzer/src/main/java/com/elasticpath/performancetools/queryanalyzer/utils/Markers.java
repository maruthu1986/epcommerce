/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.performancetools.queryanalyzer.utils;

/**
 * Holder for various String markers used for searching within log file.
 */
public final class Markers {

	/**
	 * Marker for finding Cortex resource URIs.
	 */
	public static final String RELOS_RESOURCE_URI_MARKER = "RelOS Resource URI:";
	/**
	 * URI marker.
	 */
	public static final String URI_MARKER = "URI:";
	/**
	 * Marker for finding jpa queries.
	 */
	public static final String JPA_QUERY_MARKER = "Executing query:";
	/**
	 * Marker for finding jpa trace queries.
	 */
	public static final String JPA_TRACE_QUERY_MARKER = "TRACE org.apache.openjpa";
	/**
	 * Marker for finding sql queries.
	 */
	public static final String SQL_QUERY_MARKER = "executing prepstmnt ";
	/**
	 * Marker for finding jpa eager relations.
	 */
	public static final String EAGER_RELATIONS_MARKER = "Eager relations: ";

	private Markers() {
		//constants class
	}
}
