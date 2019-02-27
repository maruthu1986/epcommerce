/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.pricelistmanager.handlers;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.core.editors.GuidEditorInput;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareHandler;
import com.elasticpath.cmclient.pricelistmanager.editors.PriceListEditor;
import com.elasticpath.domain.pricing.PriceListDescriptor;

/**
 * Handler class for opening a new PriceListEditor. Invoked by UI elements such as toolbar or menu item.
 */
public class CreatePriceListHandler extends AbstractPolicyAwareHandler {
	
	private static final Logger LOG = Logger.getLogger(CreatePriceListHandler.class);

	private final PolicyActionContainer handlerContainer = addPolicyActionContainer("createVirtualCatalogHandler"); //$NON-NLS-1$	
	
	/**r
	 * Open up a new PriceListEditor with a new Price List.
	 * 
	 * @param arg0 not used
	 * @return null
	 * @throws ExecutionException on exception
	 */
	@Override
	public Object execute(final ExecutionEvent arg0) throws ExecutionException {
		final IEditorInput editorInput = 
			new GuidEditorInput(null, PriceListDescriptor.class);
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(editorInput, PriceListEditor.PART_ID);
		} catch (final PartInitException e) {
			LOG.error("Can not open PriceList editor", e); //$NON-NLS-1$
		}
		return null;
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
		return "createPriceListHandler"; //$NON-NLS-1$
	}

}
