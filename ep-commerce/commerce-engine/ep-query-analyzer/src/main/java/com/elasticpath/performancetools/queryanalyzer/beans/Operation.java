/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.performancetools.queryanalyzer.beans;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Representation of a Cortex operation.
 */
@SuppressWarnings("PMD.UnusedPrivateField")
public final class Operation implements Serializable {
	/**
	 * Serial version.
	 */
	public static final long serialVersionUID = 1L;

	private static final String UNKNOWN_RESOURCE_OPERATION = "UNKNOWN";

	private final String type;
	private final String uri;
	private final List<JPAQuery> jpaQueries = new LinkedList<>();
	private final Map<String, Integer> totalDBCallsPerTable = new HashMap<>();
	private final Map<String, Integer> totalJPACallsPerEntity = new HashMap<>();
	private String thread;
	private transient Date startedAt;
	private transient Date finishedAt;
	private transient Date jpaKickedInAt;
	private transient Date jpaFinishedOfAt;
	private long jpaTotalExeTimeMs;
	private long totalOpDurationMs;

	//not used, but required by JSON serializer
	private long dbTotalTimeMs;
	private long jpaTotalExeTimeWithoutDbMs;
	private long durationWithoutJpaMs;

	/**
	 * Default constructor.
	 */
	public Operation() {
		this.type = UNKNOWN_RESOURCE_OPERATION;
		this.uri = "Non-Cortex-Operation";
	}

	/**
	 * Custom constructor.
	 *
	 * @param type operation type.
	 * @param uri  operation uri.
	 */
	public Operation(final String type, final String uri) {
		this.type = type;
		this.uri = uri;
	}

	public String getType() {
		return type;
	}

	public String getUri() {
		return uri;
	}

	public List<JPAQuery> getJpaQueries() {
		return jpaQueries;
	}

	public Map<String, Integer> getTotalDBCallsPerTable() {
		return totalDBCallsPerTable;
	}

	public Map<String, Integer> getTotalJPACallsPerEntity() {
		return totalJPACallsPerEntity;
	}

	public String getThread() {
		return thread;
	}

	public void setThread(final String thread) {
		this.thread = thread;
	}

	/**
	 * Add new JPA query to the collection of jpa queries.
	 *
	 * @param jpaQuery the jpa query.
	 */
	public void addJPAQuery(final JPAQuery jpaQuery) {
		this.jpaQueries.add(jpaQuery);
	}

	@JsonIgnore
	public Date getStartedAt() {
		return startedAt;
	}

	public void setStartedAt(final Date startedAt) {
		this.startedAt = startedAt;
	}

	@JsonIgnore
	public Date getFinishedAt() {
		return finishedAt;
	}

	/**
	 * Set the timestamp of the last found JPA Query line.
	 *
	 * @param finishedAt the timestamp
	 */
	@SuppressWarnings("PMD.ConfusingTernary")
	public void setFinishedAt(final Date finishedAt) {
		this.finishedAt = finishedAt;

		if (startedAt != null) {
			this.totalOpDurationMs = finishedAt.getTime() - startedAt.getTime();
		} else if (!jpaQueries.isEmpty()) {
			this.totalOpDurationMs = finishedAt.getTime() - ((LinkedList<JPAQuery>) jpaQueries).getFirst().getStartedAt().getTime();
		}
	}

	public Long getTotalOpDurationMs() {
		return totalOpDurationMs;
	}

	@JsonIgnore
	public Date getJpaKickedInAt() {
		return jpaKickedInAt;
	}

	public void setJpaKickedInAt(final Date jpaKickedInAt) {
		this.jpaKickedInAt = jpaKickedInAt;
	}

	public void setJpaFinishedOfAt(final Date jpaFinishedOfAt) {
		this.jpaFinishedOfAt = jpaFinishedOfAt;
	}

	/**
	 * Get total time spent in execution of JPA queries.
	 * NOTE: this time may not be totally reliable because OpenJPA framework writes a lot in the log file
	 *
	 * @return total time in milliseconds
	 */
	public long getJpaTotalExeTimeMs() {
		if (jpaFinishedOfAt == null || jpaKickedInAt == null) {
			this.jpaTotalExeTimeMs = 0L;
		} else {
			this.jpaTotalExeTimeMs = jpaFinishedOfAt.getTime() - jpaKickedInAt.getTime();
		}
		return jpaTotalExeTimeMs;
	}


	public long getJpaTotalExeTimeWithoutDbMs() {
		return getJpaTotalExeTimeMs() - getDbTotalTimeMs();
	}

	/**
	 * Get total time spent in db calls.
	 *
	 * @return total time in milliseconds
	 */
	public long getDbTotalTimeMs() {
		return jpaQueries.stream()
				.map(JPAQuery::getSqlQueries)
				.flatMap(Collection::stream)
				.mapToLong(SQLQuery::getExeTimeMs)
				.sum();
	}

	public long getDurationWithoutJpaMs() {
		return totalOpDurationMs - jpaTotalExeTimeMs;
	}

	@Override
	public boolean equals(final Object otherOperation) {
		return EqualsBuilder.reflectionEquals(this, otherOperation, false);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this, false);
	}
}
