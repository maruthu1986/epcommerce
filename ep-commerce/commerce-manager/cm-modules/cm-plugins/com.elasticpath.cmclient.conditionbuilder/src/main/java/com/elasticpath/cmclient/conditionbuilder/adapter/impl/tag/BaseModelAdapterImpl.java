/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
/**
 * 
 */
package com.elasticpath.cmclient.conditionbuilder.adapter.impl.tag;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import com.elasticpath.cmclient.conditionbuilder.adapter.BaseModelAdapter;

/**
 * BaseModelAdapterImpl.
 * @param <M> model for adapter
 */
public class BaseModelAdapterImpl<M> implements BaseModelAdapter<M> {

	private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	private final M model;
	
	/**
	 * Default constructor.
	 * @param model model
	 */
	public BaseModelAdapterImpl(final M model) {
		super();
		this.model = model;
	}

	@Override
	public void addPropertyChangeListener(final PropertyChangeListener listener) {
		this.propertyChangeSupport.addPropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(final PropertyChangeListener listener) {
		this.propertyChangeSupport.removePropertyChangeListener(listener);
	}

	@Override
	public void removeAllPropertyChangeListeners() {
		PropertyChangeListener[] listeners = this.propertyChangeSupport.getPropertyChangeListeners();
		if (listeners != null) {
			for (int index = listeners.length - 1; index >= 0; index--) {
				this.propertyChangeSupport.removePropertyChangeListener(listeners[index]);
			}
		}
	}

	@Override
	public M getModel() {
		return this.model;
	}

	/**
	 * Get the PropertyChangeSupport object.
	 * @return the propertyChangeSupport property support
	 */
	protected PropertyChangeSupport getPropertyChangeSupport() {
		return propertyChangeSupport;
	}

}
