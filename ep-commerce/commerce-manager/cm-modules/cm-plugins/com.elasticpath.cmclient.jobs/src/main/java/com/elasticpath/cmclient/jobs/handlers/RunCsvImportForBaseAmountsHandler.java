/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.jobs.handlers;

import com.elasticpath.cmclient.core.ServiceLocator;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.jobs.helpers.ImportJobDataValidator;
import com.elasticpath.cmclient.jobs.helpers.ImportJobDataValidatorProcForBaseAmount;
import com.elasticpath.cmclient.jobs.views.AbstractJobList;
import com.elasticpath.cmclient.jobs.wizards.AbstractImportJobValidationPage;
import com.elasticpath.cmclient.jobs.wizards.CsvValidationPage;
import com.elasticpath.cmclient.jobs.wizards.DirectCsvImportBaseAmountsWizard;
import com.elasticpath.cmclient.jobs.wizards.DirectCsvImportBaseAmountsWizardDialog;
import com.elasticpath.cmclient.jobs.wizards.DirectCsvImportConfigurePriceListImportPage;
import com.elasticpath.cmclient.jobs.wizards.HeaderValidationPage;
import com.elasticpath.cmclient.jobs.wizards.ImportJobPreviewPage;
import com.elasticpath.cmclient.jobs.wizards.MappingsValidationPage;
import com.elasticpath.cmclient.jobs.wizards.PageChangeListener;
import com.elasticpath.cmclient.jobs.wizards.SuccessPage;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareHandler;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.dataimport.ImportJob;
import com.elasticpath.domain.dataimport.ImportJobRequest;
import com.elasticpath.service.dataimport.ImportService;

/**
 * Handler for running a CSV import of base amounts.
 */
public class RunCsvImportForBaseAmountsHandler extends AbstractPolicyAwareHandler {

	private final ImportService importService = ServiceLocator.getService(ContextIdNames.IMPORT_SERVICE);
	
	private static final String DEFAULT_IMPORT_JOB_FOR_BASE_AMOUNT = "DEFAULT_IMPORT_JOB_FOR_BASE_AMOUNT"; //$NON-NLS-1$

	private static final String DEFAULT_IMPORT_JOB_FOR_BASE_AMOUNT_NAME = "Base Amount Import"; //$NON-NLS-1$

	private final PolicyActionContainer handlerContainer = addPolicyActionContainer("runCsvImport"); //$NON-NLS-1$

	/**
	 *  Executes the event.
	 * @param event the event.
	 * @return the resulting window manager for the dialog.
	 * @throws ExecutionException the exception.
	 */
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		ImportJob selectedJob = this.importService.findImportJob(DEFAULT_IMPORT_JOB_FOR_BASE_AMOUNT);
		selectedJob.initialize();
		selectedJob.setCsvFileName(null);
		selectedJob.setName(DEFAULT_IMPORT_JOB_FOR_BASE_AMOUNT_NAME);
		
		DirectCsvImportBaseAmountsWizard wizard = new DirectCsvImportBaseAmountsWizard(
				selectedJob, AbstractJobList.PRICE_LIST_IMPORT_JOBS_TYPE);
		
		ImportJobRequest importJobRequest = wizard.getModel();
		
		// Create the wizard dialog
		DirectCsvImportBaseAmountsWizardDialog dialog = new DirectCsvImportBaseAmountsWizardDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
		PageChangeListener listener = new BaseAmountPageChangeListener(wizard, importJobRequest);
		dialog.addPageChangeListener(listener);
		
		// Open the wizard dialog
		dialog.open();
		dialog.removePageChangeListener(listener);
		return dialog.getWindowManager();
	}

	/**
	 * A next page change listener.
	 */
	private class BaseAmountPageChangeListener implements PageChangeListener {
		private final Wizard wizard;
		private final ImportJobRequest importJobRequest;
		/**
		 * @param wizard a wizard
		 * @param importJobRequest a model object
		 */
		BaseAmountPageChangeListener(final Wizard wizard, final ImportJobRequest importJobRequest) {
			this.wizard = wizard;
			this.importJobRequest = importJobRequest;
		}

		@Override
		public void pageChanged(final IWizardPage page) {
			if (this.wizard.getStartingPage() == page) {
				DirectCsvImportConfigurePriceListImportPage firstPage = (DirectCsvImportConfigurePriceListImportPage) page;
				
				IWizardPage nextPage = validateAndSetNextPages(firstPage);
				if (nextPage == null) {
					nextPage = null;
				}
				firstPage.setNextPage(nextPage);
			}
		}

		private IWizardPage validateAndSetNextPages(final DirectCsvImportConfigurePriceListImportPage firstPage) {

			this.importJobRequest.setImportSource(this.importJobRequest.getImportJob().getCsvFileName());

			ImportJobDataValidatorProcForBaseAmount validatorProc = 
				new ImportJobDataValidatorProcForBaseAmount("Validating file...", importJobRequest); //$NON-NLS-1$
			
			ImportJobDataValidator validator = new ImportJobDataValidator(validatorProc); 

			if (!validator.doValidate(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getShell())) {
				return null;
			}

			if (validator.isCsvValidationFault()) {
				AbstractImportJobValidationPage csvPage = 
					(AbstractImportJobValidationPage) wizard.getPage(CsvValidationPage.PAGE_NAME);
				csvPage.setValidationFaults(validator.csvValidationFaults());
				return csvPage;
			}
			
			if (!validatorProc.isHeaderValid()) {
				HeaderValidationPage headerValidationPage = (HeaderValidationPage) wizard.getPage(HeaderValidationPage.PAGE_NAME);
				headerValidationPage.setValidationMessage(validatorProc.getValidationMessage());
				return headerValidationPage;
			}

			if (validator.isMappingValidationFault()) {
				AbstractImportJobValidationPage mappingPage = 
					(AbstractImportJobValidationPage) wizard.getPage(MappingsValidationPage.PAGE_NAME);
				mappingPage.setValidationFaults(validator.mappingValidationFaults());

				if (firstPage.isPreviewModeActivated()) {
					return updateNextPageForPreviewPage(mappingPage);
				}
				return mappingPage;
			}

			if (firstPage.isPreviewModeActivated()) {
				return updateNextPageForPreviewPage(wizard.getPage(SuccessPage.PAGE_NAME));
			}
			return wizard.getPage(SuccessPage.PAGE_NAME);
		}

		private ImportJobPreviewPage updateNextPageForPreviewPage(final IWizardPage nextPage) {

			ImportJobPreviewPage previewPage = 
				(ImportJobPreviewPage) wizard.getPage(ImportJobPreviewPage.PAGE_NAME);
			if (previewPage != null) {
				previewPage.setNextPage(nextPage);
			}
			return previewPage;
		}
	}

	@Override
	public boolean isEnabled() {
		boolean enabled = true;
		if (getStatePolicy() != null) {
			enabled = (EpState.EDITABLE == getStatePolicy().determineState(handlerContainer));
		}
		return enabled;		
	}

	/**
	 *Gets the target identifier.
	 * @return
	 */
	@Override
	public String getTargetIdentifier() {
		return "csvImportPricelistHandler"; //$NON-NLS-1$
	}

}
