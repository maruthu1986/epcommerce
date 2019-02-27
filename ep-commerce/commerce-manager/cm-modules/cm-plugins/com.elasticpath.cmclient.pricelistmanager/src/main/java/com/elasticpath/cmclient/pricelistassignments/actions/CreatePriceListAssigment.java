/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.pricelistassignments.actions;

import org.apache.log4j.Logger;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.core.wizard.EpWizardDialog;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareAction;
import com.elasticpath.cmclient.pricelistassignments.wizard.PriceListAssignmentWizard;

/**
 * 
 * Create price list assignment action.
 *
 */
public class CreatePriceListAssigment extends AbstractPolicyAwareAction {

	private static final Logger LOG = Logger.getLogger(CreatePriceListAssigment.class.getName());
	
	/**
	 * The constructor.
	 * 
	 * @param text the tool tip text.
	 * @param imageDescriptor the image is shown at the title.
	 */	
	public CreatePriceListAssigment(final String text, final ImageDescriptor imageDescriptor) {
		super(text, imageDescriptor);
		LOG.info("Created " + CreatePriceListAssigment.class.getName());  //$NON-NLS-1$ 
		
	}
	
	@Override
	public void run() {
		LOG.debug("CreateDynamicContentAssignment Action called."); //$NON-NLS-1$

		final PriceListAssignmentWizard wizard = new PriceListAssignmentWizard();
		wizard.setObjectGuid(null);
		
		final WizardDialog dialog = new EpWizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
		dialog.setPageSize(PriceListAssignmentWizard.DEFAULT_WIDTH, PriceListAssignmentWizard.DEFAULT_HEIGHT);
		dialog.addPageChangingListener(wizard);

		dialog.open();
	}

	@Override
	public String getTargetIdentifier() {
		return "createPriceListAssignmentAction"; //$NON-NLS-1$
	}
}
