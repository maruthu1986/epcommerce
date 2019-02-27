/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
/**
 * 
 */
package com.elasticpath.cmclient.jobs.wizards;

import org.eclipse.osgi.util.NLS;

import com.elasticpath.cmclient.jobs.JobsMessages;
import com.elasticpath.domain.dataimport.ImportJob;
import com.elasticpath.domain.dataimport.ImportJobRequest;

/**
 * A wizard for the CSV Import for BaseAmounts.
 *
 */
public class DirectCsvImportBaseAmountsWizard extends RunImportJobWizard {

	/**
	 * Default constructor for wizard.
	 * @param importJob import job object
	 * @param jobType job type
	 */
	public DirectCsvImportBaseAmountsWizard(final ImportJob importJob, final int jobType) {
		super(importJob, jobType);
		this.setWindowTitle(JobsMessages.get().DirectCsvImportBaseAmountsWizard_Title);
	}

	@Override
	public void addPages() {

		ImportJobRequest request = this.getModel();

		this.addPage(new DirectCsvImportConfigurePriceListImportPage(
				JobsMessages.get().DirectCsvImportConfigurePriceListImportPage_Title,
				JobsMessages.get().DirectCsvImportConfigurePriceListImportPage_Description, request));

		String details =
			NLS.bind(JobsMessages.get().RunWizard_JobPreviewPageDetails,
			ImportJobPreviewPage.getPreviewRowsLimit());
		this.addPage(new ImportJobPreviewPage(
				JobsMessages.get().RunWizard_JobPreviewPageTitle, details, null, this.getModel(), null));

		this.addPage(new CsvValidationPage(JobsMessages.get().RunWizard_CsvValidationPageTitle,
				JobsMessages.get().RunWizard_CsvValidationPageDetails, null, request));
		this.addPage(new MappingsValidationPage(JobsMessages.get().RunWizard_MappingsValidationPageTitle,
				JobsMessages.get().RunWizard_MappingsValidationPageDetails, null, request));
		this.addPage(new SuccessPage(JobsMessages.get().RunWizard_MappingsValidationPageTitle,
				JobsMessages.get().RunWizard_MappingsValidationPageDetails, null));
		this.addPage(new HeaderValidationPage());
	}

}
