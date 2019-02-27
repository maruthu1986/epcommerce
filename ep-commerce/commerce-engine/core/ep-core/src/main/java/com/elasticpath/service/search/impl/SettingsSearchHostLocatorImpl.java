/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.service.search.impl;

import com.elasticpath.service.search.SearchHostLocator;
import com.elasticpath.settings.provider.SettingValueProvider;

/**
 * Implementation of SearchHostLocator that retrieves the search host location from the settings service.
 * If deployed in a multiple search server/cluster configuration, a master search host URL should be
 * defined in the settings service with a context of 'master'. On the master search server, this class should
 * be configured so that 'requiresMaster' is true so it will use the master search host URL for index writing.
 */
public class SettingsSearchHostLocatorImpl implements SearchHostLocator {

	private boolean requiresMaster;

	private SettingValueProvider<String> defaultSearchHostLocationProvider;

	private SettingValueProvider<String> masterSearchHostLocationProvider;

	/**
	 * Obtain the URL of the search host stored in the setting service.
	 * If multiple search servers are deployed in a cluster, a setting value with the context of 'master'
	 * should be set to define a different server URL for the master search server that writes to the indexes.
	 *
	 * @return the URL string of the search host.
	 */
	@Override
	public String getSearchHostLocation() {
		if (getRequiresMaster()) {
			return getMasterSearchHostLocationProvider().get();
		}

		return getDefaultSearchHostLocationProvider().get();
	}

	/**
	 * Set whether the master search server is required.
	 * A master search server URL is available optionally depending on if
	 * search servers are deployed in a clustered configuration.
	 *
	 * @param requiresMaster true if getting master server responsible for indexing in a cluster.
	 */
	public void setRequiresMaster(final boolean requiresMaster) {
		this.requiresMaster = requiresMaster;
	}

	/**
	 * @return true if the master server is required
	 */
	@SuppressWarnings("PMD.BooleanGetMethodName")
	public boolean getRequiresMaster() {
		return requiresMaster;
	}

	public void setDefaultSearchHostLocationProvider(final SettingValueProvider<String> defaultSearchHostLocationProvider) {
		this.defaultSearchHostLocationProvider = defaultSearchHostLocationProvider;
	}

	protected SettingValueProvider<String> getDefaultSearchHostLocationProvider() {
		return defaultSearchHostLocationProvider;
	}

	public void setMasterSearchHostLocationProvider(final SettingValueProvider<String> masterSearchHostLocationProvider) {
		this.masterSearchHostLocationProvider = masterSearchHostLocationProvider;
	}

	protected SettingValueProvider<String> getMasterSearchHostLocationProvider() {
		return masterSearchHostLocationProvider;
	}

}
