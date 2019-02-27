/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
/**
 * 
 */
package com.elasticpath.cmclient.store.targetedselling.conditionalexpression.model;

/**
 * ComboViewerModelBuilder allow to get model for ComboViewer.
 * @param <T> object type for combobox
 */
public interface ComboViewerModelBuilder<T> {

	/**
	 * Get model.
	 * @return array of objects.
	 */
	T[] getModel();
}
