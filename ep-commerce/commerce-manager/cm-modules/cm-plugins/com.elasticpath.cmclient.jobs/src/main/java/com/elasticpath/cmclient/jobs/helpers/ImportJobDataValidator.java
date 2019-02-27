/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.jobs.helpers;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.jobs.JobsMessages;
import com.elasticpath.cmclient.jobs.wizards.RowValidationFault;
import com.elasticpath.commons.exception.EpFileManagerException;
import com.elasticpath.domain.dataimport.ImportJobRequest;

/**
 * Import validation progress dialog.
 */
public class ImportJobDataValidator {

	private final ImportJobDataValidatorProc validator;

	private static final Logger LOG = Logger.getLogger(ImportJobDataValidator.class);
	
	/**
	 * constructor.
	 * 
	 * @param message message
	 * @param request import job request
	 */
	public ImportJobDataValidator(final String message, final ImportJobRequest request) {
		validator = new ImportJobDataValidatorProc(message, request);
	}

	/**
	 * Constructor with validator process in parameter.
	 * @param validatorProc validator process object
	 */
	public ImportJobDataValidator(final ImportJobDataValidatorProc validatorProc) {
		this.validator = validatorProc;
	}

	/**
	 * Do all validation work.
	 * 
	 * @param shell shell
	 * @return true if successful
	 */
	public boolean doValidate(final Shell shell) {
		try {
			new ProgressMonitorDialog(shell).run(true, false, validator);
		} catch (final InvocationTargetException e) {
			StringBuffer sBuffer = new StringBuffer(JobsMessages.get().RunWizard_UnexpectedError).
					append("\n").append(e.getTargetException().getMessage()); //$NON-NLS-1$
			MessageDialog.openError(shell, JobsMessages.get().RunWizard_UnexpectedError, sBuffer.toString());
			
			Throwable target = e.getTargetException();
			if (target == null) {
				LOG.error(e.getClass() + " with message " + e.getMessage()); //$NON-NLS-1$
			} else {
				LOG.error(target.getClass() + " with message " + target.getMessage()); //$NON-NLS-1$
			}
			return false;
		} catch (EpFileManagerException fme) {
			MessageDialog.openError(shell, JobsMessages.get().RunWizard_UnexpectedError, fme.getMessage());
			return false;
		} catch (final InterruptedException e) {
			MessageDialog.openInformation(shell, JobsMessages.get().RunWizard_Canceled_Title, e.getCause().getMessage());
			return false;
		} catch (final OperationCanceledException e) {
			MessageDialog.openInformation(shell, JobsMessages.get().RunWizard_Canceled_Title, JobsMessages.get().RunWizard_Canceled_Message);
			return false;
		} catch (final Exception e) {
			MessageDialog.openError(shell, JobsMessages.get().RunWizard_UnexpectedError, e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * checks if CsvValidation is fault.
	 * 
	 * @return true if csv validation is fault
	 */
	public boolean isCsvValidationFault() {
		return !validator.csvValidationFaults().isEmpty();
	}

	/**
	 * checks if Mapping Validation is fault.
	 * 
	 * @return true if mapping validation is fault
	 */
	public boolean isMappingValidationFault() {
		return !validator.mappingValidationFaults().isEmpty();
	}

	/**
	 * Returns a list of faults or empty list.
	 * 
	 * @return a list of faults or empty list
	 */
	public List<RowValidationFault> csvValidationFaults() {
		return validator.csvValidationFaults();
	}

	/**
	 * Returns a list of faults or empty list.
	 * 
	 * @return a list of faults or empty list
	 */
	public List<RowValidationFault> mappingValidationFaults() {
		return validator.mappingValidationFaults();
	}

}