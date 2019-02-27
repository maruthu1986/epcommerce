/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.jobs.actions;
import com.elasticpath.cmclient.core.ServiceLocator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;

import com.elasticpath.cmclient.jobs.JobsMessages;
import com.elasticpath.cmclient.jobs.views.AbstractJobList;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.dataimport.ImportJob;
import com.elasticpath.service.dataimport.ImportService;

/**
 * Action to delete a tax code.
 */
public class DeleteJobAction extends AbstractAuthorizedJobAction {

	private final AbstractJobList listView;

	/**
	 * Constructor.
	 *
	 * @param listView the tax code list view
	 * @param text the action's text
	 * @param imageDescriptor the action's image
	 * @param permission the permission
	 */
	public DeleteJobAction(final AbstractJobList listView, final String text, final ImageDescriptor imageDescriptor, final String permission) {
		super(text, imageDescriptor, permission);
		this.listView = listView;
	}

	@Override
	public void run() {
		final ImportJob importJob = listView.getSelectedJob();
		final boolean confirmed = MessageDialog.openConfirm(listView.getSite().getShell(), JobsMessages.get().DeleteJobTitle,
			NLS.bind(JobsMessages.get().DeleteJobText,
			importJob.getName()));
		if (confirmed) {
			ImportService importService = ServiceLocator.getService(ContextIdNames.IMPORT_SERVICE);
			importService.remove(importJob);
			listView.refreshViewerInput();
			listView.updateNavigationComponents();
		}
	}
	
}
