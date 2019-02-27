/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.admin.stores.views;

import com.elasticpath.cmclient.core.helpers.store.StoreEditorModel;

/**
 * Represents store selector interface.
 */
public interface StoreSelector {
	
	/**
	 * Gets the currently-selected store editor model.
	 * 
	 * @return the currently-selected StoreEditorModel
	 */
	StoreEditorModel getSelectedStoreEditorModel();
}
