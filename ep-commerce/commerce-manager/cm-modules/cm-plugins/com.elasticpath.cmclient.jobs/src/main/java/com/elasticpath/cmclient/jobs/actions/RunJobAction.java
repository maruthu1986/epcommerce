/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.jobs.actions;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.WizardDialog;

import com.elasticpath.cmclient.core.wizard.EpWizardDialog;
import com.elasticpath.cmclient.jobs.views.AbstractJobList;
import com.elasticpath.cmclient.jobs.wizards.RunImportJobWizard;
import com.elasticpath.cmclient.policy.StatePolicyTargetListener;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareAction;
import com.elasticpath.commons.util.Pair;
import com.elasticpath.domain.dataimport.ImportJob;

/**
 * Run job action.
 */
public class RunJobAction extends AbstractPolicyAwareAction {

	private final AbstractJobList listView;

	private final int type;
	
	private final String permission;
	
	/**
	 * Constructor.
	 * 
	 * @param listView the tax code list view
	 * @param text the action's text
	 * @param imageDescriptor the action's image
	 * @param permission the permission
	 * @param type jobs type
	 */
	public RunJobAction(final AbstractJobList listView, final String text, 
			final ImageDescriptor imageDescriptor, final String permission, final int type) {
		super(text);
		this.setImageDescriptor(imageDescriptor);
		this.listView = listView;
		this.type = type;
		this.permission = permission;
	}

	@Override
	public void run() {
		ImportJob selectedJob = listView.getSelectedJob();
		RunImportJobWizard wizard = new RunImportJobWizard(selectedJob, type);
		// Create the wizard dialog
		WizardDialog dialog = new EpWizardDialog(listView.getSite().getShell(), wizard);
		// Open the wizard dialog
		if (dialog.open() == 0) {
			listView.refreshViewerInput();
		}
	}
	
	/**
	 * Identify this editor as a policy target.
	 * 
	 * @return an identifier string
	 */
	@Override
	public String getTargetIdentifier() {
		return "runImportJobAction"; //$NON-NLS-1$
	}

	/**
	 * Callback when the selected import job changes.
	 * @param event The event.
	 * @param importJob The job that was selected.
	 */
	public void jobSelectionChanged(final SelectionChangedEvent event, final ImportJob importJob) {
		
		getPolicyActionContainers().get(getTargetIdentifier()).setPolicyDependent(importJob);
		
		for (Object listener : getListenerList().getListeners()) {
			((StatePolicyTargetListener) listener).statePolicyTargetActivated(this);
		}
	}
	
	@Override
	protected Object getDependentObject() {
		// Overriding this method means that the init method on the Policy is called
		// with this listView as the dependent object.
		return new Pair<>(listView, permission);
	}
}
