/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.jobs.actions;

import java.util.HashMap;
import java.util.List;

import com.elasticpath.cmclient.core.ServiceLocator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;

import com.elasticpath.cmclient.core.wizard.EpWizardDialog;
import com.elasticpath.cmclient.jobs.views.AbstractJobList;
import com.elasticpath.cmclient.jobs.wizards.ColumnCharDelimiter;
import com.elasticpath.cmclient.jobs.wizards.ImportJobWizard;
import com.elasticpath.cmclient.jobs.wizards.TextCharDelimiter;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.dataimport.ImportDataType;
import com.elasticpath.domain.dataimport.ImportJob;
import com.elasticpath.service.dataimport.ImportService;

/**
 * Action to create a new tax code.
 */
public class CreateJobAction extends AbstractAuthorizedJobAction {

	private final AbstractJobList listView;

	private final ImportService importService;

	private final int type;
	/**
	 * Constructor.
	 *
	 * @param listView the tax code list view
	 * @param text the action's text
	 * @param type jobs type
	 * @param permission the permission
	 * @param imageDescriptor the action's image
	 */
	public CreateJobAction(final AbstractJobList listView, final String text,
			final ImageDescriptor imageDescriptor, final String permission, final int type) {
		super(text, imageDescriptor, permission);
		this.listView = listView;
		importService = ServiceLocator.getService(ContextIdNames.IMPORT_SERVICE);
		this.type = type;
		setEnabled(isAuthorized());
	}

	@Override
	public void run() {
		ImportJob importJob = ServiceLocator.getService(ContextIdNames.IMPORT_JOB);
		initImportJobDefaultValues(importJob);
		ImportJobWizard wizard = new ImportJobWizard(importJob, type);
		WizardDialog dialog = new EpWizardDialog(listView.getSite().getShell(), wizard);
		if (Window.OK == dialog.open()) {
			importService.saveOrUpdateImportJob(importJob);
			listView.refreshViewerInput();
			listView.updateNavigationComponents();
		}
	}

	private void initImportJobDefaultValues(final ImportJob importJob) {
		List<ImportDataType> dataTypes = importService.listImportDataTypes();
		importJob.setImportDataTypeName(dataTypes.get(0).getName());
		importJob.setCsvFileColDelimeter(ColumnCharDelimiter.COMMA.getDelimiter());
		importJob.setCsvFileTextQualifier(TextCharDelimiter.SINGLE_QUOTE.getDelimiter());
		importJob.setMappings(new HashMap<>());
	}
}
