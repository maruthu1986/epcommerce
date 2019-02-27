/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
/**
 * 
 */
package com.elasticpath.cmclient.jobs.helpers;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.osgi.util.NLS;

import com.elasticpath.cmclient.jobs.JobsMessages;
import com.elasticpath.cmclient.jobs.wizards.RowValidationFault;
import com.elasticpath.domain.dataimport.ImportDataType;
import com.elasticpath.domain.dataimport.ImportJobRequest;

/**
 * A validator process for BaseAmount.
 *
 */
public class ImportJobDataValidatorProcForPromoCoupons extends ImportJobDataValidatorProc {

	private String messageLoading;

	private List<List<String>> previewData;

	/**
	 * A default constructor with message and import job request in parameters.
	 * @param messageValidation a message displayed during validation
	 * @param messageLoading a message displayed during model loading
	 * @param request a request object
	 */
	public ImportJobDataValidatorProcForPromoCoupons(final String messageValidation, final String messageLoading,
			final ImportJobRequest request) {
		super(messageValidation, request);
		this.messageLoading = messageLoading;
	}


	/**
	 * Testing constructor with message and import job request in parameters.
	 * @param message a message
	 * @param request a request object
	 */
	ImportJobDataValidatorProcForPromoCoupons(final String message, final ImportJobRequest request) {
		super(message, request);
	}

	@Override
	public void beforeRunDoneHook(final IProgressMonitor monitor)  throws InvocationTargetException, InterruptedException {
		monitor.beginTask(messageLoading, IProgressMonitor.UNKNOWN);
		if (monitor.isCanceled()) {
			throw new OperationCanceledException();
		}
		monitor.worked(1);
	}

	@Override
	protected void doCsvValidate() throws InterruptedException {
		if (previewData == null) {
			previewData = getImportService().getPreviewData(getRequest().getImportJob(), 1, true);
		}

		if (CollectionUtils.isEmpty(previewData)) {
			addError(JobsMessages.get().CouponCodesCsvImportWizard_Empty, 0);
			return;
		}

		csvValidationFaults().clear();

		// check header is empty or inconsistent
		checkIsHeaderValid();
	}

	private boolean checkIsHeaderValid() {

		ImportDataType importDataType =
			getImportService().findImportDataType(getRequest().getImportJob().getImportDataTypeName());

		String headerStr = importDataType.getImportFields().keySet().toString();

		final List<String> header = previewData.get(0);
		if (header.size() != importDataType.getImportFields().size()) {
			addError(
				NLS.bind(JobsMessages.get().CouponCodesCsvImportWizard_WrongFormat,
				headerStr), 0);
			return false;
		}

		int index = 0;
		for (String fieldName : importDataType.getImportFields().keySet()) {
			if (!fieldName.equals(header.get(index))) {
				addError(
					NLS.bind(JobsMessages.get().CouponCodesCsvImportWizard_WrongFormat,
					headerStr), 0);
				return false;
			}
			index++;
		}
		
		return true;
	}
	
	private void addError(final String errorMessage, final int lineNo) {
		csvValidationFaults().add(new RowValidationFault(lineNo, errorMessage));
	}
	
	@Override
	protected void doMappingsValidate() throws InterruptedException {
		// do nothing
	}

}
