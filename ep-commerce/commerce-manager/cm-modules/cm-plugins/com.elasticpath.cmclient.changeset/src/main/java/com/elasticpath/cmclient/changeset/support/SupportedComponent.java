/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.changeset.support;

import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;

/**
 * Supported editor wrapper class.
 */
public interface SupportedComponent {
	/**
	 * Gets the component ID.
	 * 
	 * @return the editorId
	 */
	String getComponentId();

	/**
	 * Opens the component.
	 * 
	 * @param objectDescriptor the object descriptor to be open
	 */
	void openComponent(BusinessObjectDescriptor objectDescriptor);
	
	/**
	 * Gets the object type of the supported component.
	 * 
	 * @return the object type
	 */
	String getObjectType();
}