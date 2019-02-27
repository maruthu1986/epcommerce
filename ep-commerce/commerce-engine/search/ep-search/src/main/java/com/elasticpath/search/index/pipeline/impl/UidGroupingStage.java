/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.search.index.pipeline.impl;

import java.util.Collection;
import java.util.Set;

import org.springframework.beans.factory.ObjectFactory;

import com.elasticpath.search.index.pipeline.IndexingTask;
import com.elasticpath.search.index.pipeline.UidGroupingTask;

/**
 * This stage is responsible for breaking a big list of uids into several smaller lists of uids.
 * <p>
 * This stage will call the next stage multiple times.
 * 
 */
public class UidGroupingStage extends AbstractIndexingStage<Collection<Long>, Set<Long>> {

	private ObjectFactory<UidGroupingTask<Collection<Long>, Set<Long>>> indexGroupingTaskFactory;

	@Override
	IndexingTask<Set<Long>> create(final Collection<Long> payload) {
		final UidGroupingTask<Collection<Long>, Set<Long>> groupingTask = getIndexGroupingTaskFactory().getObject();
		groupingTask.setUids(payload);
		return groupingTask;
	}

	/**
	 * @param indexGroupingTaskFactory the indexGroupingTaskFactory to set
	 */
	public void setIndexGroupingTaskFactory(final ObjectFactory<UidGroupingTask<Collection<Long>, Set<Long>>> indexGroupingTaskFactory) {
		this.indexGroupingTaskFactory = indexGroupingTaskFactory;
	}

	/**
	 * @return the indexGroupingTaskFactory
	 */
	public ObjectFactory<UidGroupingTask<Collection<Long>, Set<Long>>> getIndexGroupingTaskFactory() {
		return indexGroupingTaskFactory;
	}

}