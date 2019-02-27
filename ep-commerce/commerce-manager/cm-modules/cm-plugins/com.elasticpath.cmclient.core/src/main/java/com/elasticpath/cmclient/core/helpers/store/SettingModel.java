/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.helpers.store;

import java.util.Map;

/**
 * Represents interface for setting model.
 */
public interface SettingModel {
	
	/**
	 * Gets the unique path for setting.
	 * 
	 * @return the unique path
	 */
	String getPath();

	/**
	 * The setting name.
	 * 
	 * @return the name
	 */
	String getName();

	/**
	 * The setting type.
	 * 
	 * @return the type
	 */
	String getType();

	/**
	 * The setting default value.
	 * 
	 * @return the defaultValue
	 */
	String getDefaultValue();

	/**
	 * The setting assigned value.
	 * 
	 * @return the assignedValue
	 */
	String getAssignedValue();
	
	/**
	 * The setting description.
	 *
	 * @return the description
	 */
	String getDescription();

	/**
	 * Sets setting assigned value.
	 * 
	 * @param assignedValue the assigned value
	 */
	void setAssignedValue(String assignedValue);
	
	/**
	 * Updates the setting values map.
	 * 
	 * @param settingValues the values that will be updated
	 */
	void updateSettings(Map<String, String> settingValues);
	
	/**
	 * Validates setting value.
	 * 
	 * @return validation status
	 */
	SettingValidationState validateSetting();
}