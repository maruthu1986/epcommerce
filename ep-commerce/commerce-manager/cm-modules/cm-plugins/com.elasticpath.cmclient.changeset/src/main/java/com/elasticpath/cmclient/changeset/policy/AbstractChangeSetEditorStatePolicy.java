/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.changeset.policy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * An abstract implementation to provide common functionality to
 * the implementors.
 */
@SuppressWarnings("PMD.AbstractClassWithoutAbstractMethod")
public abstract class AbstractChangeSetEditorStatePolicy extends AbstractChangeSetExcludeDeterminationStatePolicy { 
	
	@Override
	protected Collection<String> getReadOnlyContainerNames() {
		Collection<String> names = new ArrayList<>(1);
		names.add("infoControls"); //$NON-NLS-1$

		return names;
	}

	@Override
	protected Collection<String> getEditableContainerNames() {
		return Arrays.asList(
				"emailControls", //$NON-NLS-1$
				"objectsDisplaySection", //$NON-NLS-1$
				"openSkuControls" //$NON-NLS-1$
				);
	}
	

 
}
