/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.jobs.wizards;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import com.elasticpath.cmclient.core.wizard.AbstractEpWizard;
import com.elasticpath.cmclient.jobs.JobsImageRegistry;
import com.elasticpath.cmclient.jobs.JobsMessages;
import com.elasticpath.cmclient.jobs.views.AbstractJobList;
import com.elasticpath.domain.dataimport.ImportJob;

/**
 * The wizard for editing jobs.
 */
public class ImportJobWizard extends AbstractEpWizard<ImportJob> {

	private final ImportJob importJob;

	private final int type;

	/**
	 * Constructor.
	 * 
	 * @param importJob job
	 * @param type jobs type
	 */
	public ImportJobWizard(final ImportJob importJob, final int type) {
		super(null, null, null);
		this.importJob = importJob;
		if (importJob.isPersisted()) {
			this.setWindowTitle(JobsMessages.get().EditJobAction);
		} else {
			this.setWindowTitle(JobsMessages.get().CreateJobAction);
		}
		this.type = type;

	}

	@Override
	public void addPages() {
		if (this.type == AbstractJobList.CATALOG_IMPORT_JOBS_TYPE) {
			addPage(new ConfigureCatalogImportJobPage(importJob));
		} else if (this.type == AbstractJobList.CUSTOMER_IMPORT_JOBS_TYPE) {
			addPage(new ConfigureCustomerImportJobPage(importJob));
		} else if (type == AbstractJobList.WAREHOUSE_IMPORT_JOBS_TYPE) {
			addPage(new ConfigureWarehouseImportJobPage(importJob));
		} else if (type == AbstractJobList.PRICE_LIST_IMPORT_JOBS_TYPE) {
			addPage(new ConfigurePriceListImportJobPage(importJob));
		}

		addPage(new DataFieldMappingPage("DataFieldMappingPage", JobsMessages.get().ImportJobWizard_DataFieldMapping, //$NON-NLS-1$
				JobsMessages.get().ImportJobWizard_DataFieldMappingDescription, null, importJob));
	}

	@Override
	public boolean performFinish() {
		// make explicit call to set internal mappings in import job as this needs to be called explicitly
		importJob.setMappings(importJob.getMappings());
		
		return super.performFinish();
	}

	@Override
	protected ImportJob getModel() {
		return this.importJob;
	}

	@Override
	protected Image getWizardImage() {
		ImageDescriptor wizardImage;
		if (importJob.isPersisted()) {
			wizardImage = JobsImageRegistry.JOB_EDIT;
		} else {
			wizardImage = JobsImageRegistry.JOB_CREATE;
		}
		return JobsImageRegistry.getImage(wizardImage);
	}
}
