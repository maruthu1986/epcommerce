/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.admin.configuration.listener;

import com.elasticpath.settings.domain.SettingDefinition;

/**
 * Listener for when a settings definition object is updated. 
 *
 */
public interface SettingDefinitionUpdateListener {
	
	/**
	 * Notify that a setting definition has been updated.
	 * @param event the definition updated
	 */
	void settingDefinitionUpdated(SettingDefinition event); 
	
}
