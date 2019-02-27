/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.changeset.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchPartSite;

import com.elasticpath.cmclient.changeset.wizards.CreateChangeSetWizard;
import com.elasticpath.cmclient.core.wizard.EpWizardDialog;

/**
 * The action that creates new change sets.
 */
public class CreateChangeSetAction extends Action {

	private final IWorkbenchPartSite workbenchPartSite;

	/**
	 * Constructs a new action.
	 * 
	 * @param workbenchPartSite the change set view
	 * @param name the action's name
	 * @param imageDesc the action's descriptor
	 */
	public CreateChangeSetAction(final IWorkbenchPartSite workbenchPartSite, final String name, final ImageDescriptor imageDesc) {
		super(name, imageDesc);
		this.workbenchPartSite = workbenchPartSite;
	}

	@Override
	public void run() {
		
		CreateChangeSetWizard newWizard = new CreateChangeSetWizard();
		WizardDialog dialog = new EpWizardDialog(workbenchPartSite.getShell(), newWizard);
		dialog.addPageChangingListener(newWizard);
		// open the dialog
		dialog.open();
	}
	
}
