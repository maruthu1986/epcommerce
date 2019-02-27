/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.search.index.pipeline;

/**
 * This factory creates {@code UidFilteringTask}s for the {@code IndexingPipelineImpl}. See the Javadoc in {@code IndexingPipelineImpl} for more
 * information.
 *
 * @param <IN> likely a List<Long> of uids
 * @param <OUT> a filtered List<Long> of uids
 */
public interface UidFilteringTaskFactory<IN, OUT> extends IndexingTaskFactory<OUT> {

	@Override
	UidFilteringTask<IN, OUT> create();
}
