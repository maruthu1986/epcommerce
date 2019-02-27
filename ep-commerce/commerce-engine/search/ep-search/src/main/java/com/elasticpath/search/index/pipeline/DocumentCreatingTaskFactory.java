/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.search.index.pipeline;

import org.apache.solr.common.SolrInputDocument;

/**
 * This factory creates {@code DocumentCreatingTask}s for the {@code IndexingPipelineImpl}.
 * 
 * @param <IN> see {@code DocumentCreatingTask}
 */
public interface DocumentCreatingTaskFactory<IN> extends IndexingTaskFactory<SolrInputDocument> {

	@Override
	DocumentCreatingTask<IN> create();
}
