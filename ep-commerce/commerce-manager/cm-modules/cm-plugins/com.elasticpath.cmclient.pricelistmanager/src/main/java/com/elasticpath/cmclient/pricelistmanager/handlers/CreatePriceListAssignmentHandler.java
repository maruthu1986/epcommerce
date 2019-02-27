/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.pricelistmanager.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.wizard.EpWizardDialog;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareHandler;
import com.elasticpath.cmclient.pricelistassignments.wizard.PriceListAssignmentWizard;

/**
 * Menu handler for PLA creation. 
 */
public class CreatePriceListAssignmentHandler extends AbstractPolicyAwareHandler {

	private final PolicyActionContainer handlerContainer = addPolicyActionContainer("createPlaHandler"); //$NON-NLS-1$
	
	@Override
	public boolean isEnabled() {
		boolean enabled = true;
		if (getStatePolicy() != null) {
			enabled = (EpState.EDITABLE == getStatePolicy().determineState(handlerContainer));
		}
		return enabled;		
	}

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final PriceListAssignmentWizard wizard = new PriceListAssignmentWizard();
		wizard.setObjectGuid(null);
		
		final WizardDialog dialog = new EpWizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
		dialog.setPageSize(PriceListAssignmentWizard.DEFAULT_WIDTH, PriceListAssignmentWizard.DEFAULT_HEIGHT);
		dialog.addPageChangingListener(wizard);

		dialog.open();
		return dialog.getWindowManager();
	}

	@Override
	public String getTargetIdentifier() {
		return "createPlaHandler"; //$NON-NLS-1$
	}

}
