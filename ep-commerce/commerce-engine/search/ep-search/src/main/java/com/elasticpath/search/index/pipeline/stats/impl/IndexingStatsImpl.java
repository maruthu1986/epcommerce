/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.search.index.pipeline.stats.impl;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.search.IndexBuildStatus;
import com.elasticpath.search.index.pipeline.stats.IndexingStatistics;
import com.elasticpath.search.index.pipeline.stats.PipelinePerformance;
import com.elasticpath.search.index.pipeline.stats.PipelineStatus;
import com.elasticpath.service.search.IndexType;

/**
 * The default implementation of {@code IndexingStatistics} which holds all the performance and status information for all the
 * {@code IndexingPipeline}s. This code must all be thread safe.
 */
@ManagedResource(objectName = "com.elasticpath.search:name=IndexingStats", description = "Stats from the indexing process", currencyTimeLimit = 1)
public class IndexingStatsImpl implements IndexingStatistics {

	private final ConcurrentHashMap<IndexType, PipelineStatus> pipelineStatuses = new ConcurrentHashMap<>();

	private final ConcurrentHashMap<IndexType, PipelinePerformance> performances = new ConcurrentHashMap<>();

	private final ConcurrentHashMap<IndexType, IndexBuildStatus> indexBuildStatuses = new ConcurrentHashMap<>();

	@Override
	public PipelineStatus getPipelineStatus(final IndexType indexType) {
		return pipelineStatuses.get(indexType);
	}

	@Override
	public PipelinePerformance getPerformance(final IndexType indexType) {
		return performances.get(indexType);
	}

	@Override
	public void attachPipelineStatus(final IndexType indexType, final PipelineStatus status) {
		pipelineStatuses.put(indexType, status);
	}

	@Override
	public void attachPipelinePerformance(final IndexType indexType, final PipelinePerformance performance) {
		performances.put(indexType, performance);
	}

	@Override
	@ManagedOperation(description = "Reset all collected statuses and statistics.")
	public void reset() {
		for (final IndexType key : pipelineStatuses.keySet()) {
			getPipelineStatus(key).reset();
		}

		for (final IndexType key : performances.keySet()) {
			getPerformance(key).reset();
		}
	}

	/**
	 * Exposed via JMX, returns a formatted table of the in/out count of items for each {@code IndexingPipeline} registered with this
	 * {@code IndexingStatistics}.
	 *
	 * @return A multi-line string, best displayed with a non-proportional font.
	 */
	@ManagedAttribute(description = "Statuses for all pipelines", currencyTimeLimit = 1)
	public String getPipelineStatuses() {
		StringBuilder message = new StringBuilder(String.format("%-30s %-15s %-15s%n", "IndexType", "Items In", "Items Out"));
		for (final IndexType key : pipelineStatuses.keySet()) {
			message.append(String.format("%-30s %-15s %-15s%n", key, getPipelineStatus(key).getIncomingCount(), getPipelineStatus(key)
					.getCompletedCount()));
		}
		return message.toString();
	}

	/**
	 * Exposed via JMX, returns a formatted table of all the {@code PipelinePerformance} objects for all the {@code IndexingPipeline}s.
	 *
	 * @return A multi-line string, best displayed with a non-proportional font.
	 */
	@ManagedAttribute(description = "Pipeline Performance Information", currencyTimeLimit = 1)
	public String getPipelinePerformance() {
		StringBuilder message = new StringBuilder();
		for (final IndexType type : performances.keySet()) {
			message.append("Index: ").append(type);
			message.append(String.format("%n%-30s %-10s %-10s %-10s%n", "Key", "Min", "Mean", "Max"));
			for (final String key : getPerformance(type).getDescriptiveStatisticsKeys()) {
				final DescriptiveStatistics stat = getPerformance(type).getDescriptiveStatistics(key);
				message.append(String.format("%-30s %10f %10f %10f%n", key, stat.getMin(), stat.getMean(), stat.getMax()));
			}
			message.append(String.format("%-30s %-10s%n", "Counter", "Value"));
			for (final String key : getPerformance(type).getCounterKeys()) {
				message.append(String.format("%-30s %10d%n", key, getPerformance(type).getCounter(key)));
			}
			message.append("\n\n");
		}
		return message.toString();
	}

	/**
	 * For JMX. Given the string name of an {@code IndexType}, return the String value of its current {@code IndexStatus}. This can be used to check
	 * progress occasionally.
	 *
	 * @param indexTypeName string value of {@code IndexType}
	 * @return string value of {@code IndexStatus}.
	 */
	@ManagedOperation(description = "Return the index status for the specified IndexType", currencyTimeLimit = 1)
	@ManagedOperationParameters({ @ManagedOperationParameter(name = "indexTypeName", description = "The string value of the IndexType.") })
	public String getIndexStatus(final String indexTypeName) {
		final IndexBuildStatus buildStatus = indexBuildStatuses.get(IndexType.findFromName(indexTypeName));

		if (buildStatus == null) {
			return "?";
		}
		return buildStatus.getIndexStatus().toString();
	}

	/**
	 * For JMX. Return the String representation of all the attached {@code IndexBuildStatus}es.
	 *
	 * @return see {@code IndexBuildStatus#toString()}.
	 */
	@ManagedAttribute(description = "Index Build Status for all registered indexes", currencyTimeLimit = 1)
	public String getIndexBuildStatuses() {
		StringBuilder message = new StringBuilder();
		for (final Map.Entry<IndexType, IndexBuildStatus> entry : indexBuildStatuses.entrySet()) {
			message.append(entry.getValue());
			message.append('\n');
		}
		return message.toString();
	}

	@Override
	public IndexBuildStatus getIndexBuildStatus(final IndexType indexType) {
		return indexBuildStatuses.get(indexType);
	}

	@Override
	public void attachIndexBuildStatus(final IndexType indexType, final IndexBuildStatus indexBuildStatus) {
		if (indexType == null || !Objects.equals(indexType, indexBuildStatus.getIndexType())) {
			throw new EpServiceException("index build status is not consistent with the index type. index type: " + indexType
					+ "build status: " + indexBuildStatus);
		} 
		
		indexBuildStatuses.put(indexType, indexBuildStatus);
	}
}
