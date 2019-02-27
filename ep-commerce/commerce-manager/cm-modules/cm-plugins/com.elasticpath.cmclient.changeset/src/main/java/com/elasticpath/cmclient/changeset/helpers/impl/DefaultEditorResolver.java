/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.changeset.helpers.impl;

import com.elasticpath.cmclient.changeset.ChangeSetSupportedEditors;
import com.elasticpath.cmclient.changeset.helpers.EditorResolver;
import com.elasticpath.cmclient.changeset.support.SupportedComponent;
import com.elasticpath.domain.objectgroup.BusinessObjectDescriptor;

/**
 *  Default implementation of the {@link EditorResolver} interface.
 */
public class DefaultEditorResolver implements EditorResolver {

	/**
	 * {@inheritDoc}.
	 */
	@Override
	public String resolveEditorId(final BusinessObjectDescriptor objectDescriptor) {
		SupportedComponent supportedComponent = ChangeSetSupportedEditors.getDefault().findSupportedComponent(objectDescriptor);
		
		return supportedComponent.getComponentId();
	}

}
