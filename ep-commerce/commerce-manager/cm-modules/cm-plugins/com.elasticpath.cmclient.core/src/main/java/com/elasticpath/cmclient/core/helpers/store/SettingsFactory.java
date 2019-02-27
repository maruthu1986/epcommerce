/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.helpers.store;

/**
 * This interface provides methods for settings creation.
 */
public interface SettingsFactory {
	
	/**
	 * Creates setting model by given path.
	 * 
	 * @param path the path of setting
	 * @param storeCode the store code
	 * @return the new setting model
	 */
	SettingModel createSetting(String path, String storeCode);

}
