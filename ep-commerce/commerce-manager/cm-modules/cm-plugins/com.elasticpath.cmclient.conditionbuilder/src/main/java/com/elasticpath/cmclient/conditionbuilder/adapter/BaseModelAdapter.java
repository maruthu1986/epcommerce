/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
/**
 * 
 */
package com.elasticpath.cmclient.conditionbuilder.adapter;

import java.beans.PropertyChangeListener;

/**
 * BaseModel is a base model for condition builder.
 * @param <M> model for adapter
 */
public interface BaseModelAdapter<M> {

	/**
	 * Get the model.
	 * @return M type object
	 */
	M getModel();
	
	/**
	 * Add property change listener.
	 * @param listener PropertyChangeListener
	 */
	void addPropertyChangeListener(PropertyChangeListener listener);

	/**
	 * Remove property change listener.
	 * @param listener PropertyChangeListener
	 */
	void removePropertyChangeListener(PropertyChangeListener listener);

	/**
	 * Remove all listeners.
	 */
	void removeAllPropertyChangeListeners();

}
