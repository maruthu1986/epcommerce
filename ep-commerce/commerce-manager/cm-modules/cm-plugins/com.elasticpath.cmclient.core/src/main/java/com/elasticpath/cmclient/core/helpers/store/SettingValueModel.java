/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.helpers.store;

import java.util.Map;

import com.elasticpath.settings.domain.SettingDefinition;
import com.elasticpath.settings.domain.SettingMetadata;
import com.elasticpath.settings.domain.SettingValue;

/**
 * Represents setting model for marketing and system tabs.
 */
public class SettingValueModel extends AbstractSettingModel {

	private static final String KEY_NAME = "displayName"; //$NON-NLS-1$

	private final SettingValue settingValue;

	private final SettingDefinition settingDefinition;

	/**
	 * Constructs the setting model.
	 * 
	 * @param editorModel the editor model
	 * @param settingValue the setting value
	 * @param settingDefinition the setting definition
	 */
	public SettingValueModel(final StoreEditorModel editorModel, final SettingValue settingValue, final SettingDefinition settingDefinition) {
		super(editorModel);
		this.settingValue = settingValue;
		this.settingDefinition = settingDefinition;

		if (settingValue == null || settingDefinition == null) {
			throw new IllegalArgumentException("setting value or setting definition was not set"); //$NON-NLS-1$
		}
	}

	@Override
	public String getName() {
		final SettingMetadata settingMetadata = settingDefinition.getMetadata().get(KEY_NAME);
		String name = settingDefinition.getPath();
		if (settingMetadata != null) {
			name = settingMetadata.getValue();
		}
		return name;
	}

	@Override
	public String getType() {
		return settingDefinition.getValueType();
	}

	@Override
	public String getDefaultValue() {
		return settingDefinition.getDefaultValue();
	}

	@Override
	public String getAssignedValue() {
		return settingValue.getValue();
	}

	@Override
	public void setAssignedValue(final String assignedValue) {
		settingValue.setValue(assignedValue);
	}

	@Override
	public String getPath() {
		return settingDefinition.getPath();
	}

	@Override
	public String getDescription() {
		return settingDefinition.getDescription();
	}

	@Override
	public void updateSettings(final Map<String, String> settingValues) {
		settingValues.put(getPath(), getAssignedValue());
	}
	
	@Override
	public SettingValidationState validateSetting() {
		return SettingValidationState.SUCCESS;
	}
}
