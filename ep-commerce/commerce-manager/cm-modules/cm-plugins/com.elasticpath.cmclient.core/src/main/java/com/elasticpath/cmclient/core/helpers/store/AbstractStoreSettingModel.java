/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.helpers.store;

import java.util.Map;

/**
 * Represents the sore setting model.
 */
public abstract class AbstractStoreSettingModel extends AbstractSettingModel {

	private final String path;

	private final String name;

	private final String type;

	private final String defaultValue;
	
	private final String description;

	/**
	 * Creates the sore setting model.
	 * 
	 * @param editorModel the editor model
	 * @param path the unique path for setting
	 * @param name the name
	 * @param type the type
	 * @param defaultValue the default value
	 */
	public AbstractStoreSettingModel(final StoreEditorModel editorModel, final String path, final String name, final String type,
			final String defaultValue) {
		super(editorModel);
		this.path = path;
		this.name = name;
		this.type = type;
		this.defaultValue = defaultValue;
		this.description = name; // if it is necessary, the unique description can be provided.
	}

	@Override
	public String getDefaultValue() {
		return defaultValue;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public String getPath() {
		return path;
	}
	
	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public SettingValidationState validateSetting() {
		if (!getStoreModel().getStoreState().isIncomplete()) {
			final String assignedValue = getAssignedValue();
			if (assignedValue == null || "".equals(assignedValue)) { //$NON-NLS-1$
				return SettingValidationState.FAILURE;
			}
		}
		return SettingValidationState.SUCCESS;
	}
	
	@Override
	public abstract String getAssignedValue();

	@Override
	public void updateSettings(final Map<String, String> settingValues) {
		//do nothing
	}
}
