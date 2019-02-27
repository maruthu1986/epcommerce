/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.jobs.actions;
import com.elasticpath.cmclient.core.ServiceLocator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardDialog;

import com.elasticpath.cmclient.core.wizard.EpWizardDialog;
import com.elasticpath.cmclient.jobs.views.AbstractJobList;
import com.elasticpath.cmclient.jobs.wizards.ImportJobWizard;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.dataimport.ImportJob;
import com.elasticpath.service.dataimport.ImportService;

/**
 * Action to edit a tax code.
 */
public class EditJobAction extends AbstractAuthorizedJobAction {

	private final AbstractJobList listView;

	private final int type;

	/**
	 * Constructor.
	 *
	 * @param listView the customer list view
	 * @param text the action's text
	 * @param type jobs type
	 * @param imageDescriptor the action's image
	 * @param permission the permission
	 */
	public EditJobAction(final AbstractJobList listView, final String text,
			final ImageDescriptor imageDescriptor, final String permission, final int type) {
		super(text, imageDescriptor, permission);
		this.listView = listView;
		this.type = type;
	}

	@Override
	public void run() {
		ImportJob selectedImportJob = listView.getSelectedJob();
		ImportService importService = ServiceLocator.getService(ContextIdNames.IMPORT_SERVICE);
		ImportJob importJobToEdit = importService.getImportJob(selectedImportJob.getUidPk());
		ImportJobWizard wizard = new ImportJobWizard(importJobToEdit, type);
		WizardDialog dialog = new EpWizardDialog(listView.getSite().getShell(), wizard);
		if (dialog.open() == 0) {
			importService.saveOrUpdateImportJob(importJobToEdit);
			listView.refreshViewerInput();
		}
	}
}
