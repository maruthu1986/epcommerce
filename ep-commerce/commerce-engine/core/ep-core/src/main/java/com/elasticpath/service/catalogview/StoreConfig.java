/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.service.catalogview;

import com.elasticpath.domain.store.Store;
import com.elasticpath.settings.domain.SettingValue;
import com.elasticpath.settings.provider.SettingValueProvider;

/**
 * Encapsulates a store configuration.
 */
public interface StoreConfig {

	/**
	 * Returns the store object associated with this configuration.
	 * 
	 * @return the store associated with this store configuration.
	 * @throws com.elasticpath.base.exception.EpServiceException if the Store has not been set
	 */
	Store getStore();
	
	/**
	 * Returns the code for the Store associated with this configuration.
	 * 
	 * @return the code for the store associated with this store configuration
	 */
	String getStoreCode();

	/**
	 * Get the setting identified by the given path for the store associated with this configuration.
	 * @param path the unique identifier to the setting definition
	 * @return the requested setting value
	 * @deprecated use {@link #getSettingValue(SettingValueProvider)}
	 */
	@Deprecated
	SettingValue getSetting(String path);

	/**
	 * Returns the setting value corresponding to the store associated with this configuration.
	 *
	 * @param settingValueProvider the setting value provider representing the setting
	 * @param <T> the expected type of value to be returned
	 * @return the requested setting value
	 */
	default <T> T getSettingValue(SettingValueProvider<T> settingValueProvider) {
		return settingValueProvider.get(getStoreCode());
	}

}
