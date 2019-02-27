/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.helpers.store;


/**
 * Represents abstract setting model.
 */
public abstract class AbstractSettingModel implements SettingModel {
	
	private final StoreEditorModel storeModel;
	
	/**
	 * Constructs the abstract setting model.
	 * 
	 * @param editorModel the editor model
	 */
	public AbstractSettingModel(final StoreEditorModel editorModel) {
		this.storeModel = editorModel;
	}
	
	/**
	 * Gets the store model.
	 * 
	 * @return the store model
	 */
	public StoreEditorModel getStoreModel() {
		return storeModel;
	}
}
