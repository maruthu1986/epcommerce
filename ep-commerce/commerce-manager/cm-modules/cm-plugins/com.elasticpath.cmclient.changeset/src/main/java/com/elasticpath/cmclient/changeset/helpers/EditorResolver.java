/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.changeset.helpers;

import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;

/**
 * Resolves editors. 
 */
public interface EditorResolver {

	/**
	 * Resolve the editor for the given {@link BusinessObjectDescriptor}.
	 * 
	 * @param objectDescriptor The {@link BusinessObjectDescriptor} used to determine the correct editor ID.
	 * @return the editor ID 
	 */
	String resolveEditorId(BusinessObjectDescriptor objectDescriptor);

}
