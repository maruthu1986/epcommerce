/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.search.index.pipeline.impl;

import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.ObjectFactory;

import com.elasticpath.search.index.pipeline.DocumentCreatingTask;
import com.elasticpath.search.index.pipeline.IndexingTask;

/**
 * This {@code IndexingStage} delegates its work to a {@code DocumentCreatingTask} created 
 * from the {@code ObjectFactoryCreatingFactoryBean} and handed into a {@code TaskExecutor}.
 * 
 * @param <IN> the input of this stage is almost always an entity (like a {@code Product})
 */
public class DocumentCreatingStage<IN> extends AbstractIndexingStage<IN, SolrInputDocument> {

	private ObjectFactory<DocumentCreatingTask<IN>> documentCreatorFactory;
	
	@Override
	public IndexingTask<SolrInputDocument> create(final IN entity) {
		final DocumentCreatingTask<IN> creator = getDocumentCreatorFactory().getObject();
		creator.setEntity(entity);
		return creator;
	}

	/**
	 * @param documentCreatorFactory the documentCreatorFactory to set
	 */
	public void setDocumentCreatorFactory(final ObjectFactory<DocumentCreatingTask<IN>> documentCreatorFactory) {
		this.documentCreatorFactory = documentCreatorFactory;
	}

	/**
	 * @return the documentCreatorFactory
	 */
	public ObjectFactory<DocumentCreatingTask<IN>> getDocumentCreatorFactory() {
		return documentCreatorFactory;
	}

}