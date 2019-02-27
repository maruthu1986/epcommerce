/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.search.index.pipeline;

/**
 * The Factory for creating {@code EntityLoadingTask}s for the {@code IndexingPipelineImpl}. See the Javadoc in the {@code IndexingPipelineImpl}
 * for what the {@code EntityLoadingTask} does.
 * 
 * @param <OUT> see {@code EntityLoadingTask}
 */
public interface EntityLoadingTaskFactory<OUT> extends IndexingTaskFactory<OUT> {
 
	@Override
	EntityLoadingTask<OUT> create();

}
