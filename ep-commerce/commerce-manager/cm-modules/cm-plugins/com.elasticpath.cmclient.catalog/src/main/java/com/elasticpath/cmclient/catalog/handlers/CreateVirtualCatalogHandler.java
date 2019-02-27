/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.elasticpath.cmclient.catalog.dialogs.catalog.VirtualCatalogDialog;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareHandler;

/**
 * Create Virtual Catalog handler.
 */
public class CreateVirtualCatalogHandler extends AbstractPolicyAwareHandler {

	private static final String TARGET_IDENTIFIER = "createVirtualCatalogHandler"; //$NON-NLS-1$
	private final PolicyActionContainer handlerContainer = addPolicyActionContainer(TARGET_IDENTIFIER);

	@Override
	public Object execute(final ExecutionEvent executionEvent) throws ExecutionException {

		final VirtualCatalogDialog virtualCatalogDialog = new VirtualCatalogDialog();
		virtualCatalogDialog.setObjectGuid(null);

		virtualCatalogDialog.open();

		return virtualCatalogDialog.getWindowManager();
	}
	
	@Override
	public boolean isEnabled() {
		boolean enabled = true;
		if (getStatePolicy() != null) {
			enabled = (EpState.EDITABLE == getStatePolicy().determineState(handlerContainer));
		}
		return enabled;		
	}

	@Override
	public String getTargetIdentifier() {
		return TARGET_IDENTIFIER;
	}
}
