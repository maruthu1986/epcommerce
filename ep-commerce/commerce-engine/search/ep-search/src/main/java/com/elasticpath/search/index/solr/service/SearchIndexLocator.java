/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.search.index.solr.service;

import java.io.File;

import com.elasticpath.service.search.IndexType;

/**
 * Provides the location of search indices.
 */
public interface SearchIndexLocator {

	/**
	 * Returns the file system location of the search index corresponding to the given index type.
	 *
	 * @param indexType the index to locate
	 * @return the file system location of the search index
	 */
	File getSearchIndexLocation(IndexType indexType);

}
