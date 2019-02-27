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
import com.elasticpath.cmclient.pricelistassignments.views.PriceListAssigmentsSearchView;
import com.elasticpath.cmclient.pricelistassignments.wizard.PriceListAssignmentWizard;
import com.elasticpath.common.dto.pricing.PriceListAssignmentsDTO;

/**
 * 
 * Edit price list assignment.
 *
 */
public class EditPriceListAssigment extends AbstractPolicyAwareAction {

	private static final Logger LOG = Logger.getLogger(EditPriceListAssigment.class.getName());
	
	private final PriceListAssigmentsSearchView view;
	
	
	/**
	 * The constructor.
	 * 
	 * @param view the results from search list view.
	 * @param text the tool tip text.
	 * @param imageDescriptor the image is shown at the title.
	 */	
	public EditPriceListAssigment(final PriceListAssigmentsSearchView view,
			final String text, final ImageDescriptor imageDescriptor) {
		super(text, imageDescriptor);
		this.view = view;
		LOG.info("Created " + EditPriceListAssigment.class.getName());  //$NON-NLS-1$ 
		
	}
	
	@Override
	public void run() {
		LOG.debug("EditDynamicContent Action called."); //$NON-NLS-1$

		PriceListAssignmentsDTO priceListAssignmentDTO = view.getSelectedItem();
		final PriceListAssignmentWizard wizard = new PriceListAssignmentWizard();
		wizard.setObjectGuid(priceListAssignmentDTO.getGuid());		
		
		final WizardDialog dialog = new EpWizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
		dialog.setPageSize(PriceListAssignmentWizard.DEFAULT_WIDTH, PriceListAssignmentWizard.DEFAULT_HEIGHT);
		dialog.addPageChangingListener(wizard);
		
		dialog.open();
	}

	@Override
	public String getTargetIdentifier() {
		return "editPriceListAssignmentAction"; //$NON-NLS-1$
	}

}