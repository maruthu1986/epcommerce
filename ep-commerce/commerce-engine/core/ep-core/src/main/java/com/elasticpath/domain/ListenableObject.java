/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain;

import java.beans.PropertyChangeListener;

/**
 * Represents an object with property listener support.
 */
public interface ListenableObject {

	/**
	 * Add a listener to this bean.
	 *
	 * @param listener the property change listener to add
	 */
	void addPropertyChangeListener(PropertyChangeListener listener);

	/**
	 * Add a listener to this bean.
	 *
	 * @param listener the property change listener to add
	 * @param replace indicates whether to replace existing instance of same listener
	 */
	void addPropertyChangeListener(PropertyChangeListener listener, boolean replace);

	/**
	 * Add a listener to this bean for a specific property.
	 *
	 * @param propertyName the name of the property to listen for changes on
	 * @param listener the property change listener to add
	 */
	void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);

	/**
	 * Add a listener to this bean for a specific property.
	 *
	 * @param propertyName the name of the property to listen for changes on
	 * @param listener the property change listener to add
	 * @param replace indicates whether to replace existing instance of same listener
	 */
	void addPropertyChangeListener(String propertyName, PropertyChangeListener listener, boolean replace);

	/**
	 * Remove a listener from this bean.
	 *
	 * @param listener the property change listener to remove
	 */
	void removePropertyChangeListener(PropertyChangeListener listener);

	/**
	 * Remove a listener from this bean for a specific property.
	 *
	 * @param propertyName the name of the property to stop listening on
	 * @param listener the property change listener to remove
	 */
	void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);

}