/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.core.registry;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.service.UISession;

import com.elasticpath.cmclient.core.CmSingletonUtil;


/**
 * A general object registry that provides observers with events on add/update/remove events.
 */
public final class ObjectRegistry {

	private final Collection<ObjectRegistryListener> objectRegistryListeners = new LinkedList<ObjectRegistryListener>();

	/**
	 * Constructor.
	 */
	protected ObjectRegistry() {
		super();
	}

	/**
	 * Gets the session instance of the registry.
	 *
	 * @return the registry instance
	 */
	public static ObjectRegistry getInstance() {
		return  CmSingletonUtil.getSessionInstance(ObjectRegistry.class);
	}

	private UISession getSession() {
		return RWT.getUISession();
	}

	/**
	 * Puts an object into the registry and fires events.
	 *
	 * @param key the object's key
	 * @param object the object
	 */
	public void putObject(final String key, final Serializable object) {
		if (key == null || object == null) {
			throw new IllegalArgumentException(String.format(
					"Cannot put null key: '%s' or object: '%s' into the registry: ", new Object[] { key, object})); //$NON-NLS-1$
		}

		if (getSession().getAttribute(key) == null) {
			getSession().setAttribute(key, object);
			fireObjectAdded(key, object);
		} else {
			final Serializable oldValue = (Serializable) getSession().getAttribute(key);
			getSession().setAttribute(key, object);
			fireObjectChanged(key, oldValue, object);
		}
	}
	
	/**
	 * Fires an update event with the given key. 
	 *
	 * @param key the key to use.
	 */
	public void fireEvent(final String key) {
		Object value = getSession().getAttribute(key);
		if (value != null) {
			fireObjectChanged(key, value, value);
		}
		
	}

	/**
	 * Gets an object with a key from the registry.
	 *
	 * @param key the key to use
	 * @return the found object or null if none
	 */
	public Object getObject(final String key) {
		return getSession().getAttribute(key);
	}

	/**
	 * Removes an object from the registry.
	 *
	 * @param key the key of the object in the registry
	 */
	public void removeObject(final String key) {
		final Object removedObject = getSession().getAttribute(key);
		getSession().removeAttribute(key);
		if (removedObject != null) {
			fireObjectRemoved(key, removedObject);
		}
	}

	/**
	 * Fires an 'object added' event.
	 */
	private void fireObjectAdded(final String key, final Object object) {
		for (final ObjectRegistryListener listener : objectRegistryListeners) {
			listener.objectAdded(key, object);
		}
	}

	/**
	 * Fires an 'object changed' event.
	 */
	private void fireObjectChanged(final String key, final Object oldValue, final Object newValue) {
		for (final ObjectRegistryListener listener : objectRegistryListeners) {
			listener.objectUpdated(key, oldValue, newValue);
		}
	}

	/**
	 * Fires an 'object removed' event.
	 */
	private void fireObjectRemoved(final String key, final Object removedObject) {
		for (final ObjectRegistryListener listener : objectRegistryListeners) {
			listener.objectRemoved(key, removedObject);
		}
	}

	/**
	 * Adds a new listener to the registry.
	 *
	 * @param listener the listener
	 */
	public void addObjectListener(final ObjectRegistryListener listener) {
		objectRegistryListeners.add(listener);
	}

	/**
	 * Adds a new listener to the registry.
	 *
	 * @param listener the listener
	 */
	public void removeObjectListener(final ObjectRegistryListener listener) {
		objectRegistryListeners.remove(listener);
	}


}
