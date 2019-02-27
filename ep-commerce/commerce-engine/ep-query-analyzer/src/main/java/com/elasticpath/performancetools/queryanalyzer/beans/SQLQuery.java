/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.performancetools.queryanalyzer.beans;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.elasticpath.performancetools.queryanalyzer.utils.Utils;

/**
 * SQL query representation.
 */
public final class SQLQuery implements Serializable {
	/**
	 * Serial version.
	 */
	public static final long serialVersionUID = 1L;

	private static final String JOIN_KEYWORD = "JOIN";

	private final String query;
	private final int numberOfJoins;
	private long exeTimeMs;

	/**
	 * Default constructor.
	 */
	public SQLQuery() {
		this.query = "";
		this.numberOfJoins = 0;
	}

	/**
	 * Custom constructor.
	 *
	 * @param query     SQL query.
	 * @param exeTimeMs execution time in ms.
	 */
	public SQLQuery(final String query, final long exeTimeMs) {
		this.query = Utils.removeTabAndCRChars(query);
		this.exeTimeMs = exeTimeMs;
		this.numberOfJoins = getNumberOfJoins(this.query);
	}

	public String getQuery() {
		return query;
	}

	private int getNumberOfJoins(final String query) {
		return StringUtils.countMatches(query, JOIN_KEYWORD);
	}

	public int getNumberOfJoins() {
		return numberOfJoins;
	}

	public long getExeTimeMs() {
		return exeTimeMs;
	}

	public void setExeTimeMs(final long exeTimeMs) {
		this.exeTimeMs = exeTimeMs;
	}

	@Override
	public boolean equals(final Object otherSQLQuery) {
		return EqualsBuilder.reflectionEquals(this, otherSQLQuery, false);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this, false);
	}
}
