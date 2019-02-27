/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.changeset.event;

import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.domain.changeset.ChangeSet;

/**
 * This interface must be implemented by part that needs to be notified on change set changes.
 */
public interface ChangeSetEventListener {
	
	/**
	 * Notifies for a modified ChangeSet.
	 * 
	 * @param event ChangeSet change event.
	 */
	void changeSetModified(ItemChangeEvent<ChangeSet> event);	
}
