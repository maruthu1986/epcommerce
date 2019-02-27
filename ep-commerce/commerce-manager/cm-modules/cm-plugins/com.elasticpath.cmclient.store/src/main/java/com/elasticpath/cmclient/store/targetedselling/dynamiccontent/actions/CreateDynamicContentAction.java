/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.targetedselling.dynamiccontent.actions;

import org.apache.log4j.Logger;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.core.wizard.EpWizardDialog;
import com.elasticpath.cmclient.store.targetedselling.dynamiccontent.wizard.NewDynamicContentWizard;

/**
 * Create dynamic content level action.
 */
public class CreateDynamicContentAction extends AbstractBaseDynamicContentAction {

	/** The logger. */
	private static final Logger LOG = Logger.getLogger(CreateDynamicContentAction.class);

	/**
	 * The constructor.
	 * 
	 * @param text the tool tip text.
	 * @param imageDescriptor the image is shown at the title.
	 */
	public CreateDynamicContentAction(final String text, final ImageDescriptor imageDescriptor) {
		super(text, imageDescriptor);
	}

	@Override
	public void run() {
		LOG.debug("CreateDynamicContent Action called."); //$NON-NLS-1$

		final NewDynamicContentWizard wizard = new NewDynamicContentWizard();
		final WizardDialog dialog = new EpWizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
		dialog.addPageChangingListener(wizard);
		
		dialog.open();
	}

	@Override
	public String getTargetIdentifier() {
		return "createDynamicContentAction"; //$NON-NLS-1$
	}
}
