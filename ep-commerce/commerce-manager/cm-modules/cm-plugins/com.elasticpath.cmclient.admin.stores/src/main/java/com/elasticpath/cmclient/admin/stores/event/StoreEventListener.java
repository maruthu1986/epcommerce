/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.admin.stores.event;

import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.helpers.store.StoreEditorModel;

/**
 * This interface must be implemented by part that need to be notified on search result events.
 */
public interface StoreEventListener {

	/**
	 * Notifies for a changed store editor model.
	 * 
	 * @param event store editor model change event
	 */
	void storeChanged(ItemChangeEvent<StoreEditorModel> event);
}
