/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.performancetools.queryanalyzer.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Query statistics representation.
 */
@SuppressWarnings("PMD.UnusedPrivateField")
public final class QueryStatistics implements Serializable {
	/**Serial version.*/
	public static final long serialVersionUID = 1L;

	private static final int MILLIS_TOP_BOUNDARY = 999;
	private static final int ONE_SECOND_MILLIS = 1000;
	private static final int SECONDS_TOP_BOUNDARY = 59;
	private static final int ONE_MINUTE_SECONDS = 60;

	private Integer overallDBCalls;
	private Integer overallJPACalls;
	private Integer overallDbExeTimeMs;

	//not used, but required by JSON serializer
	private String totalExecutionTimeFormatted;
	private String totalExecutionTimeWithoutDbCallsFormatted;
	private String totalExecutionTimeOfOperationsWithJpa;
	private String totalExecutionTimeOfOperationsWithoutJpa;

	private Integer totalNumberOfOperations;
	private Integer totalNumberOfOperationsWithJPA;
	private Integer totalNumberOfOperationsWithoutJPA;

	private transient Long totalExeTime;

	private final Map<String, Integer> totalDBCallsPerTable = new LinkedHashMap<>();
	private final Map<String, Integer> totalJPACallsPerEntity = new LinkedHashMap<>();

	private final Map<String, Integer> totalDBCallsPerOperation = new LinkedHashMap<>();
	private final Map<String, Integer> totalJPACallsPerOperation = new LinkedHashMap<>();

	private final Map<String, Integer> totalDBCallExeTimePerOperationMs = new LinkedHashMap<>();
	private final Map<Long, Integer> totalOperationsPerDuration = new LinkedHashMap<>();

	//all captured operations with JPA and SQL queries
	private final List<Operation> operations = new ArrayList<>();
	private final List<Operation> operationsWithJPADesc = new ArrayList<>();
	private final List<Operation> operationsWithoutJPA = new ArrayList<>();

	@JsonIgnore
	public List<Operation> getOperations() {
		return operations;
	}

	/**
	 * Add new operation to the collection of operations.
	 *
	 * @param operation a new operation.
	 */
	public void addOperation(final Operation operation) {
		this.operations.add(operation);
	}

	public List<Operation> getOperationsWithJPADesc() {
		return operationsWithJPADesc;
	}

	public List<Operation> getOperationsWithoutJPA() {
		return operationsWithoutJPA;
	}

	/**
	 * Add operation without JPA to the collection of operations without JPA queries.
	 *
	 * @param operationWithoutJPA an operation without JPA queries.
	 */
	public void addOperationWithoutJPA(final Operation operationWithoutJPA) {
		this.operationsWithoutJPA.add(operationWithoutJPA);
	}

	/**
	 * Add operation with JPA to the collection of operations with JPA queries.
	 *
	 * @param operationWithJPA an operation with JPA queries.
	 */
	public void addOperationWithJPA(final Operation operationWithJPA) {
		this.operationsWithJPADesc.add(operationWithJPA);
	}

	public Map<String, Integer> getTotalDBCallsPerTable() {
		return totalDBCallsPerTable;
	}

	public Map<String, Integer> getTotalJPACallsPerEntity() {
		return totalJPACallsPerEntity;
	}

	public Map<String, Integer> getTotalDBCallsPerOperation() {
		return totalDBCallsPerOperation;
	}

	public Map<String, Integer> getTotalJPACallsPerOperation() {
		return totalJPACallsPerOperation;
	}

	public Map<String, Integer> getTotalDBCallExeTimePerOperationMs() {
		return totalDBCallExeTimePerOperationMs;
	}

	public Integer getOverallDBCalls() {
		return overallDBCalls;
	}

	public void setOverallDBCalls(final Integer overallDBCalls) {
		this.overallDBCalls = overallDBCalls;
	}

	public Integer getOverallJPACalls() {
		return overallJPACalls;
	}

	public void setOverallJPACalls(final Integer overallJPACalls) {
		this.overallJPACalls = overallJPACalls;
	}

	public Integer getOverallDbExeTimeMs() {
		return overallDbExeTimeMs;
	}

	public void setOverallDbExeTimeMs(final Integer overallDbExeTimeMs) {
		this.overallDbExeTimeMs = overallDbExeTimeMs;
	}

	/**
	 * Return formatted total execution time of all operations (db execution time included).
	 *
	 * @return formatted total execution time.
	 */
	public String getTotalExecutionTimeFormatted() {
		return getFormattedTime(totalExeTime);
	}

	public void setTotalExecutionTime(final Long totalExecutionTime) {
		totalExeTime = totalExecutionTime;
	}

	/**
	 * Return formatted total execution time spent in execution of operations,
	 * without counting db execution time.
	 *
	 * @return formatted time.
	 */
	public String getTotalExecutionTimeWithoutDbCallsFormatted() {
		final long diffWithoutDbCalls = totalExeTime - overallDbExeTimeMs;
		return getFormattedTime(diffWithoutDbCalls);
	}

	/**
	 * Sum all operation (those that make JPA calls) duration times.
	 *
	 * @return Total execution time for all JPA-based operations
	 */
	public String getTotalExecutionTimeOfOperationsWithJpa() {
		return getFormattedTime(operationsWithJPADesc.stream()
				.mapToLong(Operation::getTotalOpDurationMs)
				.sum());
	}

	/**
	 * Sum all operation (those that do not make JPA calls) duration times.
	 *
	 * @return Total execution time for all non-JPA-based operations
	 */
	public String getTotalExecutionTimeOfOperationsWithoutJpa() {
		return getFormattedTime(operationsWithoutJPA.stream()
				.mapToLong(Operation::getTotalOpDurationMs)
				.sum());
	}

	/**
	 * Format given millis into more human-readable format
	 * MM minutes SS seconds MM millis.
	 *
	 * @param exeTime execution time in milliseconds
	 * @return formatted time
	 */
	public String getFormattedTime(final Long exeTime) {
		long seconds = 0L;
		long millisReminder = exeTime;
		long minutes = 0L;

		if (exeTime > MILLIS_TOP_BOUNDARY) {
			seconds = exeTime / ONE_SECOND_MILLIS;
			millisReminder = exeTime % ONE_SECOND_MILLIS;

			if (seconds > SECONDS_TOP_BOUNDARY) {
				minutes = seconds / ONE_MINUTE_SECONDS;
				seconds = seconds % ONE_MINUTE_SECONDS;
			}
		}
		return String.format("%d minute(s) %d second(s) %d millis", minutes, seconds, millisReminder);
	}

	public Integer getTotalNumberOfOperations() {
		return totalNumberOfOperations;
	}

	public void setTotalNumberOfOperations(final Integer totalNumberOfOperations) {
		this.totalNumberOfOperations = totalNumberOfOperations;
	}

	public Integer getTotalNumberOfOperationsWithJPA() {
		return totalNumberOfOperationsWithJPA;
	}

	public void setTotalNumberOfOperationsWithJPA(final Integer totalNumberOfOperationsWithJPA) {
		this.totalNumberOfOperationsWithJPA = totalNumberOfOperationsWithJPA;
	}

	public Map<Long, Integer> getTotalOperationsPerDuration() {
		return totalOperationsPerDuration;
	}

	public Integer getTotalNumberOfOperationsWithoutJPA() {
		return totalNumberOfOperationsWithoutJPA;
	}

	public void setTotalNumberOfOperationsWithoutJPA(final Integer totalNumberOfOperationsWithoutJPA) {
		this.totalNumberOfOperationsWithoutJPA = totalNumberOfOperationsWithoutJPA;
	}

	@Override
	public boolean equals(final Object otherStatistics) {
		return EqualsBuilder.reflectionEquals(this, otherStatistics, false);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this, false);
	}
}
