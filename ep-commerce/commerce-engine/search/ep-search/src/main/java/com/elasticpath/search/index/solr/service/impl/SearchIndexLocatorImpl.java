/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.search.index.solr.service.impl;

import java.io.File;
import java.util.Map;

import com.google.common.collect.Maps;

import com.elasticpath.search.index.solr.service.SearchIndexLocator;
import com.elasticpath.service.search.IndexType;

/**
 * <p>Implementation of {@link SearchIndexLocator} that is backed by System Properties.</p>
 * <p>Solr configuration .xml files depend on these System Properties to satisfy placeholder elements used to specify the index locations. This
 * implementation uses the same properties in order to maintain consistency throughout the application.</p>
 */
public class SearchIndexLocatorImpl implements SearchIndexLocator {

	private Map<IndexType, String> indexTypeSystemPropertyKeyMap = Maps.newHashMap();

	@Override
	public File getSearchIndexLocation(final IndexType indexType) {
		final String systemPropertyKey = getIndexTypeSystemPropertyKeyMap().get(indexType);

		if (systemPropertyKey == null) {
			throw new IllegalArgumentException("Could not locate search index for type [" + indexType + "]: no configuration mapping exists for this"
					+ " index type. Ensure that the SearchIndexLocatorImpl configuration contains a mapping for this index type, and that it "
					+ "corresponds to the System Property key that stores the Solr index path.");
		}

		final String filePath = System.getProperty(systemPropertyKey);

		if (filePath == null) {
			throw new IllegalArgumentException("Could not locate search index for type [" + indexType + "]: System Property with key ["
					+ systemPropertyKey + "] does not exist. Ensure that the configuration SearchIndexLocatorImpl configuration mapping for this "
					+ "index type corresponds to the correct System Property key that stores the Solr index path. If the key appears to be correct, "
					+ "ensure that this property is being set, either via a -D JVM parameter, or via com.elasticpath.search"
					+ ".SolrConfigSystemPropertySetter.");
		}

		return new File(filePath);
	}

	public void setIndexTypeSystemPropertyKeyMap(final Map<IndexType, String> indexTypeSystemPropertyKeyMap) {
		this.indexTypeSystemPropertyKeyMap = indexTypeSystemPropertyKeyMap;
	}

	protected Map<IndexType, String> getIndexTypeSystemPropertyKeyMap() {
		return indexTypeSystemPropertyKeyMap;
	}

}
