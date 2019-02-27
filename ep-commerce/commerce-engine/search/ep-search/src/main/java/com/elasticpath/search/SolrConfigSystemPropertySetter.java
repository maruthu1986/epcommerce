/*
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.search;

import java.nio.file.Paths;
import java.util.Map;

import com.google.common.collect.Maps;
import org.apache.log4j.Logger;

import com.elasticpath.service.search.IndexType;
import com.elasticpath.service.search.solr.SolrIndexConstants;

/**
 * Sets various System properties used when configuring Solr, particularly the Solr Home property, for properties that have not been set by the JVM.
 */
public class SolrConfigSystemPropertySetter {

	private static final Logger LOG = Logger.getLogger(SolrConfigSystemPropertySetter.class);

	private String configurationRootPath;

	private Map<IndexType, String> indexDataSubdirectories = Maps.newHashMap();
	private Map<IndexType, String> indexTypeSystemPropertyKeyMap = Maps.newHashMap();

	/**
	 * Sets the System properties used to configure Solr to default values, for all properties not already satisfied.
	 */
	public void setSolrConfigProperties() {
		if (!getIndexTypeSystemPropertyKeyMap().keySet().containsAll(getIndexDataSubdirectories().keySet())) {
			throw new IllegalStateException("Invalid configuration. All keys in the indexDataSubdirectories map must have a corresponding entry in "
					+ "indexTypeSystemPropertyKeyMap.");
		}

		setSystemPropertyIfAbsent(SolrIndexConstants.SOLR_HOME_PROPERTY, createDefaultEmbeddedSolrHomeDir());

		LOG.info("Solr home: " + System.getProperty(SolrIndexConstants.SOLR_HOME_PROPERTY));

		for (final Map.Entry<IndexType, String> indexTypeStringEntry : getIndexDataSubdirectories().entrySet()) {
			final String systemPropertyKey = getIndexTypeSystemPropertyKeyMap().get(indexTypeStringEntry.getKey());
			setSystemPropertyIfAbsent(systemPropertyKey, getIndexDirectoryPath(indexTypeStringEntry.getValue()));
		}
	}

	/**
	 * Constructs the fallback Solr home directory, used when no the System property at {@link SolrIndexConstants#SOLR_HOME_PROPERTY} is defined.
	 *
	 * @return a String representing the Solr home directory
	 */
	protected String createDefaultEmbeddedSolrHomeDir() {
		return configurationRootPath + SolrIndexConstants.SOLR_HOME_DIR;
	}

	/**
	 * Returns the full path of a given subdirectory within the Solr Home directory.
	 *
	 * @param indexSubdirectory the subdirectory of the Solr Home directory
	 * @return the full path of the subdirectory
	 */
	protected String getIndexDirectoryPath(final String indexSubdirectory) {
		if (indexSubdirectory == null) {
			return null;
		}

		return Paths.get(System.getProperty(SolrIndexConstants.SOLR_HOME_PROPERTY), indexSubdirectory).toString();
	}

	/**
	 * Sets {@code value} to system property {@code systemPropertyKey}, if no value already exists.
	 *
	 * @param systemPropertyKey the system property key
	 * @param value             the value to set
	 */
	protected void setSystemPropertyIfAbsent(final String systemPropertyKey, final String value) {
		if (System.getProperty(systemPropertyKey) == null && value != null) {
			System.setProperty(systemPropertyKey, value);
		}
	}

	protected String getConfigurationRootPath() {
		return configurationRootPath;
	}

	public void setConfigurationRootPath(final String configurationRootPath) {
		this.configurationRootPath = configurationRootPath;
	}

	protected Map<IndexType, String> getIndexDataSubdirectories() {
		return indexDataSubdirectories;
	}

	public void setIndexDataSubdirectories(final Map<IndexType, String> indexDataSubdirectories) {
		this.indexDataSubdirectories = indexDataSubdirectories;
	}

	public void setIndexTypeSystemPropertyKeyMap(final Map<IndexType, String> indexTypeSystemPropertyKeyMap) {
		this.indexTypeSystemPropertyKeyMap = indexTypeSystemPropertyKeyMap;
	}

	protected Map<IndexType, String> getIndexTypeSystemPropertyKeyMap() {
		return indexTypeSystemPropertyKeyMap;
	}

}
