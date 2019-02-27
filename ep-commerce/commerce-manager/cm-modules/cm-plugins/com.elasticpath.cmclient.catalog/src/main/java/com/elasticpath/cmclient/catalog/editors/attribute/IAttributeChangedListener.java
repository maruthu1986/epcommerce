/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.editors.attribute;

import com.elasticpath.domain.attribute.AttributeValue;

/**
 * Used for unifying the retrieval of values from the edit dialogs.
 */
public interface IAttributeChangedListener {

	/**
	 * Perform any action that the attribute value changed.
	 * 
	 * @param attributeValue the attributeValue been changed
	 */
	void attributeValueChanged(AttributeValue attributeValue);
}
