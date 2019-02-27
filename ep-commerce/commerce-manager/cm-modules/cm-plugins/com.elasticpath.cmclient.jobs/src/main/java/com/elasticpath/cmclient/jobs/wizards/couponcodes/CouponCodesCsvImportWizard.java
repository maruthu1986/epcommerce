/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
/**
 * 
 */
package com.elasticpath.cmclient.jobs.wizards.couponcodes;

import com.elasticpath.cmclient.jobs.JobsMessages;
import com.elasticpath.cmclient.jobs.wizards.CsvValidationPage;
import com.elasticpath.cmclient.jobs.wizards.RunImportJobWizard;
import com.elasticpath.cmclient.jobs.wizards.SuccessPage;
import com.elasticpath.domain.dataimport.ImportJob;
import com.elasticpath.domain.dataimport.ImportJobRequest;

/**
 * A wizard for the CSV Import for BaseAmounts.
 *
 */
public class CouponCodesCsvImportWizard extends RunImportJobWizard {

	private static final String COUPON_CONFIG_GUID = "COUPON_CONFIG_GUID"; //$NON-NLS-1$

	/**
	 * Default constructor for wizard.
	 * @param importJob import job object
	 * @param jobType job type
	 * @param couponConfigGuid the couponConfig guid 
	 */
	public CouponCodesCsvImportWizard(final ImportJob importJob, final int jobType, final String couponConfigGuid) {
		super(importJob, jobType);
		this.setWindowTitle(JobsMessages.get().DirectCsvImportBaseAmountsWizard_Title);
		this.getModel().setParameter(generateParameter(couponConfigGuid));
	}

	private String generateParameter(final String couponConfigGuid) {
		// TODO Auto-generated method stub
		return COUPON_CONFIG_GUID + "=" + couponConfigGuid; //$NON-NLS-1$
	}

	@Override
	public void addPages() {
		
		ImportJobRequest request = this.getModel();
		
		this.addPage(new DirectCsvImportConfigureCouponsImportPage(
				JobsMessages.get().CouponCodesCsvImportWizard_Title,
				JobsMessages.get().CouponCodesCsvImportWizard_Description, request));
		
		this.addPage(new CsvValidationPage(JobsMessages.get().CouponCodesCsvImportWizard_ErrorTitle,
				JobsMessages.get().RunWizard_CsvValidationPageDetails, null, request));
		
		this.addPage(new SuccessPage(JobsMessages.get().RunWizard_MappingsValidationPageTitle,
				JobsMessages.get().RunWizard_MappingsValidationPageDetails, null));
	}

}
