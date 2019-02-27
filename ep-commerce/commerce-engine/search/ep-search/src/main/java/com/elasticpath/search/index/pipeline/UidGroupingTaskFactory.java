/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.search.index.pipeline;

/**
 * This factory creates {@code UidGroupingTask}s for the {@code IndexingPipelineImpl}. See the Javadoc in {@code IndexingPipelineImpl} for more
 * information.
 * 
 * @param <IN> likely a List<Long> of uids
 * @param <OUT> multiple smaller List<Long> of uids
 */
public interface UidGroupingTaskFactory<IN, OUT> extends IndexingTaskFactory<OUT> {

	@Override
	UidGroupingTask<IN, OUT> create();
}
