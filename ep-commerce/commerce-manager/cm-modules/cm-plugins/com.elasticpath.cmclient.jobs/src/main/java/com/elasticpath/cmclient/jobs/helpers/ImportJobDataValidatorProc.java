/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.jobs.helpers;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.operation.IRunnableWithProgress;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.jobs.JobsMessages;
import com.elasticpath.cmclient.jobs.wizards.RowValidationFault;
import com.elasticpath.domain.dataimport.ImportBadRow;
import com.elasticpath.domain.dataimport.ImportFault;
import com.elasticpath.domain.dataimport.ImportJobRequest;
import com.elasticpath.service.dataimport.ImportService;

/**
 * Import Validation Running Process.
 */
public class ImportJobDataValidatorProc implements IRunnableWithProgress {

	private final List<RowValidationFault> csvValidationResult = new ArrayList<RowValidationFault>();

	private final List<RowValidationFault> mappingValidationResult = new ArrayList<RowValidationFault>();

	private final String message;

	private final ImportJobRequest request;

	/**
	 * A default constructor with message and import job request in parameters.
	 * @param message message text
	 * @param request request object
	 */
	public ImportJobDataValidatorProc(final String message, final ImportJobRequest request) {
		this.message = message;
		this.request = request;
	}

	/**
	 * Runs the process.
	 * @param monitor The progress monitor.
	 * @throws InvocationTargetException is this necessary?
	 * @throws InterruptedException  Thrown when job is interrupted.
	 */
	public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		
		try {
			monitor.beginTask(message, IProgressMonitor.UNKNOWN);

			if (monitor.isCanceled()) {
				throw new OperationCanceledException();
			}
			doCsvValidate();
			monitor.worked(1);

			if (csvValidationResult.isEmpty()) {
				if (monitor.isCanceled()) {
					throw new OperationCanceledException();
				}
				doMappingsValidate();
				monitor.worked(1);
				beforeRunDoneHook(monitor);
			}
			
			
		} finally {
			monitor.done();
		}
	}
	
	/**
	 * Run method hook to perform last minute changed before end.
	 * @param monitor progress monitor
	 * @throws InvocationTargetException same as {@link #run(IProgressMonitor)}
	 * @throws InterruptedException same as {@link #run(IProgressMonitor)}
	 */
	public void beforeRunDoneHook(final IProgressMonitor monitor)  throws InvocationTargetException, InterruptedException {
		// do nothing in concrete implementation.
	}

	/**
	 * Returns a list of faults or empty list.
	 * 
	 * @return a list of faults or empty list
	 */
	public List<RowValidationFault> csvValidationFaults() {
		return csvValidationResult;
	}

	/**
	 * Returns a list of faults or empty list.
	 * 
	 * @return a list of faults or empty list
	 */
	public List<RowValidationFault> mappingValidationFaults() {
		return mappingValidationResult;
	}

	/**
	 * @return import service
	 */
	protected ImportService getImportService() {
		return ServiceLocator.getService("importService"); //$NON-NLS-1$
	}

	/** 
	 * Try SCV format validation. 
	 * @exception InterruptedException a fault
	 */
	protected void doCsvValidate() throws InterruptedException {
		List<ImportBadRow> failedRows = getImportService().validateCsvFormat(request);
		csvValidationResult.clear();
		if (!failedRows.isEmpty()) {
			for (ImportBadRow badRow : failedRows) {
				List<ImportFault> faults = badRow.getImportFaults();
				for (ImportFault fault : faults) {
					RowValidationFault rowFault = new RowValidationFault(badRow.getRowNumber(), fault);
					csvValidationResult.add(rowFault);
				}
			}
		}
		final Map<String, Integer> map = request.getImportJob().getMappings();
		List<String> titleLine = getImportService().getTitleLine(request);
		for (Map.Entry<String, Integer> mapping : map.entrySet()) {
			if (titleLine.size() < mapping.getValue()) {
				RowValidationFault badRow = new RowValidationFault(0, JobsMessages.get().RunWizard_WrongColumnNumber);
				csvValidationResult.add(badRow);
				break;
			}
		}
	}

	/** 
	 * Try Mapping validation. 
	 * @exception InterruptedException a fault
	 */
	protected void doMappingsValidate() throws InterruptedException {
		List<ImportBadRow> failedMappingRows = getImportService().validateMappings(request);
		mappingValidationResult.clear();
		for (ImportBadRow badRow : failedMappingRows) {
			List<ImportFault> faults = badRow.getImportFaults();
			for (ImportFault fault : faults) {
				RowValidationFault rowFault = new RowValidationFault(badRow.getRowNumber(), fault);
				mappingValidationResult.add(rowFault);
			}
		}
	}
	
	/**
	 * @return the message
	 */
	protected String getMessage() {
		return message;
	}

	/**
	 * @return the request
	 */
	protected ImportJobRequest getRequest() {
		return request;
	}


}
