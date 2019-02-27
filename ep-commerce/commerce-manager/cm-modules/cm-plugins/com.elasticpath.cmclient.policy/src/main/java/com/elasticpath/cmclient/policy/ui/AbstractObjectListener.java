/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.policy.ui;

import com.elasticpath.cmclient.core.registry.ObjectRegistry;
import com.elasticpath.cmclient.core.registry.ObjectRegistryListener;

/**
 *	Abstract listener to allow Editor Page Sections to subscribe to the object registry.
 */
public abstract class AbstractObjectListener implements ObjectRegistryListener {

	 /**
	  *  Constructor.
	  */
	public AbstractObjectListener() {
		ObjectRegistry.getInstance().addObjectListener(this);
		
	}
	@Override
	public void objectAdded(final String key, final Object object) {
		eventFired(key);
	}
		
	@Override
	public void objectUpdated(final String key, final Object oldValue, final Object newValue) {
		eventFired(key);
	}
		
	@Override
	public void objectRemoved(final String key, final Object object) {
		eventFired(key);
	}

	
	/**
	 * Deregisters the listener with the object registry.
	 */
	public void deRegisterListener() {
		ObjectRegistry.getInstance().removeObjectListener(this);
	}
	/**
	 *  This gets fired when the object registry fires an event.
	 *
	 * @param key the key of the event.
	 */
	public abstract void eventFired(final String key);
	 
}
