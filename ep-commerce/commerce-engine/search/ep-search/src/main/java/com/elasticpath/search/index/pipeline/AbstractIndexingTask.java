/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.search.index.pipeline;

import com.elasticpath.search.index.pipeline.stats.PipelinePerformance;

/**
 * A simple class from which {@code IndexingTask}s may extend. Takes care of setting the nextStage and {@code PipelinePerformance} injection.
 *
 * @author idcmp
 * @param <OUT> see {@code IndexingTask}
 */
public abstract class AbstractIndexingTask<OUT> implements IndexingTask<OUT> {

	private IndexingStage<OUT, ?> nextStage;

	private PipelinePerformance performance;

	@Override
	public void setNextStage(final IndexingStage<OUT, ?> nextStage) {
		this.nextStage = nextStage;
	}

	@Override
	public void setPipelinePerformance(final PipelinePerformance performance) {
		this.performance = performance;
	}

	public PipelinePerformance getPipelinePerformance() {
		return this.performance;
	}

	public IndexingStage<OUT, ?> getNextStage() {
		return this.nextStage;
	}

}
