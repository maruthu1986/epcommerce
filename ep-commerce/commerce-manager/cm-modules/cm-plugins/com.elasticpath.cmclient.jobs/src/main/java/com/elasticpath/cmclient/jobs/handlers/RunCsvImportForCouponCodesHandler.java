/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.jobs.handlers;

import com.elasticpath.cmclient.core.ServiceLocator;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.core.promotions.CouponCollectionModel;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.jobs.JobsMessages;
import com.elasticpath.cmclient.jobs.helpers.ImportJobDataValidator;
import com.elasticpath.cmclient.jobs.helpers.ImportJobDataValidatorProcForPromoCoupons;
import com.elasticpath.cmclient.jobs.views.AbstractJobList;
import com.elasticpath.cmclient.jobs.wizards.AbstractImportJobValidationPage;
import com.elasticpath.cmclient.jobs.wizards.CsvValidationPage;
import com.elasticpath.cmclient.jobs.wizards.PageChangeListener;
import com.elasticpath.cmclient.jobs.wizards.SuccessPage;
import com.elasticpath.cmclient.jobs.wizards.couponcodes.CouponCodesCsvImportWizard;
import com.elasticpath.cmclient.jobs.wizards.couponcodes.CouponCodesCsvImportWizardDialog;
import com.elasticpath.cmclient.jobs.wizards.couponcodes.DirectCsvImportConfigureCouponsImportPage;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareHandler;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.dataimport.ImportJob;
import com.elasticpath.domain.dataimport.ImportJobRequest;
import com.elasticpath.domain.rules.CouponUsageType;
import com.elasticpath.service.dataimport.ImportService;

/**
 * Handler for running a CSV import of base amounts.
 */
public class RunCsvImportForCouponCodesHandler extends AbstractPolicyAwareHandler {

	private final ImportService importService = ServiceLocator.getService(ContextIdNames.IMPORT_SERVICE);
	
	private static final String IMPORT_JOB_FOR_COUPON_CODES = "Coupon Codes Import"; //$NON-NLS-1$
	
	private static final String IMPORT_JOB_FOR_COUPON_CODES_EMAIL = "Coupon Codes and Addresses Import"; //$NON-NLS-1$

	private final PolicyActionContainer handlerContainer = addPolicyActionContainer("runCsvImport"); //$NON-NLS-1$

	private final CouponCollectionModel couponUsageCollectionModel;

	private final JobChangeAdapter importJobDoneListener;
	
	
	/**
	 * Default constructor.
	 * 
	 * @param model the model to load coupons to.
	 * @param importJobDoneListener the importJobDoneListener
	 */
	public RunCsvImportForCouponCodesHandler(final CouponCollectionModel model, final JobChangeAdapter importJobDoneListener) {
		super();
		this.couponUsageCollectionModel = model;
		this.importJobDoneListener = importJobDoneListener;
	}



	/**
	 *  Executes the event.
	 * @param event the event.
	 * @return the resulting window manager for the dialog.
	 * @throws ExecutionException the exception.
	 */
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		ImportJob selectedJob;
		
		if (CouponUsageType.LIMIT_PER_SPECIFIED_USER.equals(couponUsageCollectionModel.getCouponConfig().getUsageType())) {
			selectedJob = this.importService.findImportJob(IMPORT_JOB_FOR_COUPON_CODES_EMAIL);
		} else {
			selectedJob = this.importService.findImportJob(IMPORT_JOB_FOR_COUPON_CODES);
		}
		
		
		selectedJob.initialize();
		selectedJob.setCsvFileName(null);
		
		CouponCodesCsvImportWizard wizard = new CouponCodesCsvImportWizard(
				selectedJob, AbstractJobList.PRICE_LIST_IMPORT_JOBS_TYPE, 
				couponUsageCollectionModel.getCouponConfig().getGuid());
		wizard.addJobChangeListener(importJobDoneListener);
		
		ImportJobRequest importJobRequest = wizard.getModel();
		
		// Create the wizard dialog
		CouponCodesCsvImportWizardDialog dialog = new CouponCodesCsvImportWizardDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
		PageChangeListener listener = new CouponCodePageChangeListener(wizard, importJobRequest);
		dialog.addPageChangeListener(listener);
		
		// Open the wizard dialog
		dialog.open();
		dialog.removePageChangeListener(listener);
		return dialog.getWindowManager();
	}

	/**
	 * A next page change listener.
	 */
	private class CouponCodePageChangeListener implements PageChangeListener {
		private final Wizard wizard;
		private final ImportJobRequest importJobRequest;
		/**
		 * @param wizard a wizard
		 * @param importJobRequest a model object
		 */
		CouponCodePageChangeListener(final Wizard wizard, final ImportJobRequest importJobRequest) {
			this.wizard = wizard;
			this.importJobRequest = importJobRequest;
		}

		@Override
		public void pageChanged(final IWizardPage page) {
			if (this.wizard.getStartingPage() == page) {
				DirectCsvImportConfigureCouponsImportPage firstPage = (DirectCsvImportConfigureCouponsImportPage) page;				
				firstPage.setNextPage(validateAndSetNextPages());
			}
		}

		private IWizardPage validateAndSetNextPages() {

			this.importJobRequest.setImportSource(this.importJobRequest.getImportJob().getCsvFileName());

			ImportJobDataValidatorProcForPromoCoupons validatorProc = 
				new ImportJobDataValidatorProcForPromoCoupons(
						JobsMessages.get().CouponCodesCsvImportWizard_ProgressValidating,
						JobsMessages.get().CouponCodesCsvImportWizard_ProgressLoading,
						importJobRequest);
			
			ImportJobDataValidator validator = new ImportJobDataValidator(validatorProc); 

			validator.doValidate(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getShell());
			if (!validator.isCsvValidationFault()) {
				return wizard.getPage(SuccessPage.PAGE_NAME);
			}

			AbstractImportJobValidationPage csvPage = 
				(AbstractImportJobValidationPage) wizard.getPage(CsvValidationPage.PAGE_NAME);
			csvPage.setValidationFaults(validator.csvValidationFaults());
			return csvPage;
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
	 * @return the target identifer.
	 */
	@Override
	public String getTargetIdentifier() {
		return "csvImportCouponCodesHandler"; //$NON-NLS-1$
	}

}
