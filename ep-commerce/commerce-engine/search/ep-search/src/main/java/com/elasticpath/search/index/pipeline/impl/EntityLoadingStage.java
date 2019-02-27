/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.search.index.pipeline.impl;

import java.util.Set;

import org.springframework.beans.factory.ObjectFactory;

import com.elasticpath.search.index.pipeline.EntityLoadingTask;
import com.elasticpath.search.index.pipeline.IndexingTask;

/**
 * This stage is responsible for creating a {@code EntityLoadingTask} which will handle loading. See {@code IndexingPipelineImpl} for how this stage
 * fits into the overall {@code IndexingPipeline}.
 * 
 * @param <ENTITYTYPE> a particular domain entity.
 */
public class EntityLoadingStage<ENTITYTYPE> extends AbstractIndexingStage<Set<Long>, ENTITYTYPE> {

	private ObjectFactory<EntityLoadingTask<ENTITYTYPE>> loaderFactory;

	@Override
	public IndexingTask<ENTITYTYPE> create(final Set<Long> uids) {

		final EntityLoadingTask<ENTITYTYPE> loadingTask = getLoaderFactory().getObject();
		loadingTask.setBatch(uids);

		return loadingTask;
	}

	/**
	 * @param loaderFactory the loaderFactory to set
	 */
	public void setLoaderFactory(final ObjectFactory<EntityLoadingTask<ENTITYTYPE>> loaderFactory) {
		this.loaderFactory = loaderFactory;
	}

	/**
	 * @return the loaderFactory
	 */
	public ObjectFactory<EntityLoadingTask<ENTITYTYPE>> getLoaderFactory() {
		return loaderFactory;
	}
}