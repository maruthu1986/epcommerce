/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.event;

import com.elasticpath.cmclient.core.helpers.store.SettingModel;

/**
 * Represent change listener for settings model.
 */
public interface SettingChangeListener {
	
	/**
	 * This method calls when setting was changed.
	 * 
	 * @param model the changed setting model
	 */
	void settingChanged(SettingModel model);

}
