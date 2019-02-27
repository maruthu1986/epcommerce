/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.changeset.helpers;

import com.elasticpath.cmclient.core.registry.ObjectRegistryListener;
import com.elasticpath.cmclient.core.ui.ICompositeBlockSupport;

/**
 * Adds dynamically a composite block with the information of the change set the dialog object belongs to.
 */
public class DialogSupport implements ObjectRegistryListener {

	/**
	 * Object added.
	 *
	 * @param key the key.
	 * @param object the object.
	 */
	public void objectAdded(final String key, final Object object) {
		addChangeSetInfoBlock(key, object);
	}

	private void addChangeSetInfoBlock(final String key, final Object object) {
		if ("activeEditor".equals(key)) { //$NON-NLS-1$
			ICompositeBlockSupport blockSupport = (ICompositeBlockSupport) object;
			blockSupport.addCompositeBlock(new ChangeSetInfoBlock());
		}
	}

	/**
	 * Object removed.
	 *
	 * @param key the key
	 * @param object the object
	 */
	public void objectRemoved(final String key, final Object object) {
		// not interested in that event
	}

	/**
	 * Object updated.
	 *
	 * @param key the key
	 * @param oldValue the old value
	 * @param newValue the new value
	 */
	public void objectUpdated(final String key, final Object oldValue, final Object newValue) {
		if ("activeEditor".equals(key)) { //$NON-NLS-1$
			ICompositeBlockSupport blockSupport = (ICompositeBlockSupport) newValue;
			blockSupport.addCompositeBlock(new ChangeSetInfoBlock());
		}
	}

}
