/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.helpers;

import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.domain.attribute.Attribute;

/**
 * Listener interface used to manage attribute changes.
 */
public interface AttributeListener {

	/**
	 * Handle changed attribute event.
	 *
	 * @param event the event
	 */
	void attributeChange(ItemChangeEvent<Attribute> event);
}
