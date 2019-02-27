/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.admin.configuration.listener;

import com.elasticpath.settings.domain.SettingValue;

/**
 * Listener for when a settings value object is updated. 
 *
 */
public interface SettingValueUpdateListener {
	
	/**
	 * Notify that a setting value has been updated.
	 * @param event the value updated
	 */
	void settingValueUpdated(SettingValue event); 
	
}
