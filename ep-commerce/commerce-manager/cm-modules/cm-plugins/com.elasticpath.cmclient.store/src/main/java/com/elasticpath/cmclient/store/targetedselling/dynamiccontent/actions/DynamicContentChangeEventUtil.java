/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
/**
 * 
 */
package com.elasticpath.cmclient.store.targetedselling.dynamiccontent.actions;

import com.elasticpath.cmclient.core.event.EventType;
import com.elasticpath.cmclient.core.event.UIEvent;
import com.elasticpath.cmclient.store.StorePlugin;
import com.elasticpath.cmclient.store.targetedselling.dynamiccontent.model.DynamicContentSearchTabModel;
import com.elasticpath.cmclient.store.targetedselling.dynamiccontent.model.impl.DynamicContentSearchTabModelImpl;
import com.elasticpath.domain.contentspace.DynamicContent;


/**
 * A utility class for price list assignment actions.
 */
public class DynamicContentChangeEventUtil {

	/**
	 * Fire event for any success operation.
	 * @param eventType event type for fire
	 * @param dynamicContent object for event
	 */
	public void fireEvent(final EventType eventType, final DynamicContent dynamicContent) {

		UIEvent<DynamicContentSearchTabModel> eventForList = new UIEvent<>(
				new DynamicContentSearchTabModelImpl(),
				eventType,
				false
		);

		UIEvent<DynamicContent> event = new UIEvent<>(
				dynamicContent,
				eventType,
				false
		);

		StorePlugin.getDefault().getDynamicContentListController().onEvent(eventForList);
		StorePlugin.getDefault().getDynamicContentsController().onEvent(event);
	}
}
