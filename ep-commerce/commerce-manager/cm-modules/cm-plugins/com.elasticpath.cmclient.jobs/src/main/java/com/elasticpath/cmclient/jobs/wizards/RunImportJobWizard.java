/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.jobs.wizards;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.action.Action;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressConstants;

import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.helpers.ChangeSetHelper;
import com.elasticpath.cmclient.core.service.CatalogEventService;
import com.elasticpath.cmclient.core.wizard.AbstractEpWizard;
import com.elasticpath.cmclient.jobs.JobsImageRegistry;
import com.elasticpath.cmclient.jobs.JobsMessages;
import com.elasticpath.cmclient.jobs.JobsPlugin;
import com.elasticpath.cmclient.jobs.dialogs.ImportCompletionDialog;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.changeset.ChangeSet;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.dataimport.ImportBadRow;
import com.elasticpath.domain.dataimport.ImportFault;
import com.elasticpath.domain.dataimport.ImportJob;
import com.elasticpath.domain.dataimport.ImportJobRequest;
import com.elasticpath.domain.dataimport.ImportJobState;
import com.elasticpath.domain.dataimport.ImportJobStatus;
import com.elasticpath.service.dataimport.ImportService;

/**
 * Run import job wizard.
 */
public class RunImportJobWizard extends AbstractEpWizard<ImportJobRequest> {
	private static final Logger LOG = Logger.getLogger(RunImportJobWizard.class);
	private final ChangeSetHelper changeSetHelper = ServiceLocator.getService(ChangeSetHelper.BEAN_ID);

	private final int type;

	private final ImportJobRequest request;
	private Collection<JobChangeAdapter> jobChangeListeners;
	private final Display display;

	/**
	 * Constructor.
	 *
	 * @param importJob job
	 * @param type jobs type
	 */
	public RunImportJobWizard(final ImportJob importJob, final int type) {
		super(JobsMessages.get().RunJobAction, null, null);
		Locale defaultLocale = CorePlugin.getDefault().getDefaultLocale();
		CmUser cmUser = LoginManager.getCmUser();
		request = ServiceLocator.getService(ContextIdNames.IMPORT_JOB_REQUEST);
		request.setImportJob(importJob);
		request.setInitiator(cmUser);
		request.setReportingLocale(defaultLocale);
		ChangeSet activeChangeSet = changeSetHelper.getActiveChangeSet();
		if (activeChangeSet != null) {
			request.setChangeSetGuid(activeChangeSet.getGuid());
		}
		this.type = type;
		this.display = Display.getDefault();
	}

	@Override
	public void addPages() {
		this.addPage(new ImportJobDetailsPage(JobsMessages.get().RunWizard_ImportFilePageTitle,
				JobsMessages.get().RunWizard_ImportFilePageDetails, null, request, type));
		this.addPage(new CsvValidationPage(JobsMessages.get().RunWizard_CsvValidationPageTitle,
				JobsMessages.get().RunWizard_CsvValidationPageDetails, null, request));
		this.addPage(new MappingsValidationPage(JobsMessages.get().RunWizard_MappingsValidationPageTitle,
				JobsMessages.get().RunWizard_MappingsValidationPageDetails, null, request));
		this.addPage(new SuccessPage(JobsMessages.get().RunWizard_MappingsValidationPageTitle,
				JobsMessages.get().RunWizard_MappingsValidationPageDetails, null));
	}

	@Override
	public boolean performFinish() {
		boolean result = super.performFinish();
		if (!result) {
			return false;
		}

		String importName =
			NLS.bind(JobsMessages.get().RunWizard_RunningJob,
			this.request.getImportJob().getName());
		Job job = new CmImportJob(importName);
		job.setUser(true);
		job.schedule();
		job.addJobChangeListener(new JobDoneListener(display));

		if (CollectionUtils.isNotEmpty(jobChangeListeners)) {
			for (JobChangeAdapter jobChangeListener : jobChangeListeners) {
				job.addJobChangeListener(jobChangeListener);
			}
		}
		
		return true;
	}

	/**
	 * Checks job.
	 * 
	 * @param job Job
	 * @return true if model, false otherwise
	 */
	public boolean isModal(final Job job) {
		Boolean isModal = (Boolean) job.getProperty(IProgressConstants.PROPERTY_IN_DIALOG);
		return (isModal != null) && isModal;
	}

	/**
	 * Gets completed action.
	 * 
	 * @param runningJob - running job.
	 * @return action
	 */
	protected Action getJobCompletedAction(final ImportJobStatus runningJob) {
		return new Action() {
			@Override
			public void run() {
				ImportCompletionDialog dialog = new ImportCompletionDialog(getShell(), runningJob);
				dialog.open();
			}
		};
	}

	/**
	 * Shows results.
	 * 
	 * @param eclipseJob eclipse job
	 * @param runningJob running job
	 */
	protected void showResults(final ImportJobStatus runningJob, final Job eclipseJob) {
		eclipseJob.setProperty(IProgressConstants.KEEP_PROPERTY, Boolean.TRUE);
		eclipseJob.setProperty(IProgressConstants.ACTION_PROPERTY, getJobCompletedAction(runningJob));
	}

	@Override
	public boolean canFinish() {
		return getContainer().getCurrentPage().getName().equals(SuccessPage.PAGE_NAME);
	}

	/**
	 * Provides action after job execution.
	 */
	private class JobDoneListener extends JobChangeAdapter {

		private final Display display;
		JobDoneListener(final Display display) {
			this.display = display;
		}

		@Override
		public void done(final IJobChangeEvent event) {
			display.syncExec(() -> {

					ImportService importService = ServiceLocator.getService(ContextIdNames.IMPORT_SERVICE);
					if (ContextIdNames.IMPORT_JOB_RUNNER_CATEGORY.equals(
							importService.findImportDataType(
									request.getImportJob().getImportDataTypeName())
							.getImportJobRunnerBeanName())) {
						CatalogEventService.getInstance().notifyCatalogChanged(new ItemChangeEvent<Catalog>("source", null)); //$NON-NLS-1$
					}
				});
		}
	}

	@Override
	public ImportJobRequest getModel() {
		return request;
	}

	/**
	 * Add job change listener. 
	 * 
	 * @param jobChangeAdapter the job change listener
	 */
	public void addJobChangeListener(final JobChangeAdapter jobChangeAdapter) {
		if (jobChangeListeners == null) {
			jobChangeListeners = new ArrayList<>();
		}
		jobChangeListeners.add(jobChangeAdapter);
	}

	/**
	 * Import job.
	 */
	private class CmImportJob extends Job {

		/**
		 * Constructor.
		 *
		 * @param name name of the job
		 */
		CmImportJob(final String name) {
			super(name);
		}

		@Override
		protected IStatus run(final IProgressMonitor monitor) {

			monitor.beginTask("Remote Data Validation", IProgressMonitor.UNKNOWN); //$NON-NLS-1$

			final ImportService importService = ServiceLocator.getService(
				ContextIdNames.IMPORT_SERVICE);

			ImportJobStatus status = importService.scheduleImport(request);

			try {
				monitor.beginTask(getSubTaskTitle(), status.getTotalRows());

				int startRow = 0;
				int currentRow = 0;
				while (!status.isFinished()) {
					// a job is considered cancelled only if it is cancelled by the user and not by the closing workbench
					if (monitor.isCanceled() && !workbenchIsClosing()) {
						importService.cancelImportJob(status.getProcessId(), request.getInitiator());
						return Status.CANCEL_STATUS;
					}
					monitor.worked(currentRow - startRow);
					startRow = currentRow;
					monitor.subTask(getSubTaskCurrentRowName(status, currentRow));
					status = importService.getImportJobStatus(status.getProcessId());
					currentRow = status.getCurrentRow();
				}
				setProperty(IProgressConstants.ICON_PROPERTY, JobsImageRegistry.JOB_IMPORT_DONE);
				showResults(status, this);

				if (ObjectUtils.equals(status.getState(), ImportJobState.VALIDATION_FAILED)) {
					return new Status(IStatus.ERROR, JobsPlugin.PLUGIN_ID, 1, "Remote Validation Failed", null); //$NON-NLS-1$
				} else if (ObjectUtils.equals(status.getState(), ImportJobState.FAILED)) {
					StringBuilder builder = new StringBuilder();
					for (ImportBadRow badRow : status.getBadRows()) {
						for (ImportFault fault : badRow.getImportFaults()) {
							builder
								.append(getMessageForImportFault(fault))
								.append('\n');
						}
					}
					return new Status(IStatus.ERROR, JobsPlugin.PLUGIN_ID, 1, builder.toString(), null);
				}
				return Status.OK_STATUS;
			} catch (Exception exception) {
				LOG.error("Failed to perform import job", exception);
				return Status.CANCEL_STATUS;
			} finally {
				monitor.done();
			}
		}

		private String getSubTaskCurrentRowName(final ImportJobStatus status, final int currentRow) {
			MutableObject<String> message = new MutableObject<>(CoreMessages.EMPTY_STRING);
			display.syncExec(() -> {
				String currentRowName = JobsMessages.get().RunWizard_RunningJobProgress_CurrentRow;
				message.setValue(
					NLS.bind(currentRowName,
					currentRow, status.getTotalRows()));
			});
			return message.getValue();
		}

		private String getSubTaskTitle() {
			MutableObject<String> message = new MutableObject<>(CoreMessages.EMPTY_STRING);
			display.syncExec(() -> {
				String jobTitle = JobsMessages.get().RunWizard_RunningJobProgress_Title;
				message.setValue(
					NLS.bind(jobTitle,
					request.getImportJob().getName()));
			});
			return message.getValue();
		}

		private String getMessageForImportFault(final ImportFault fault) {
			MutableObject<String> message = new MutableObject<>(CoreMessages.EMPTY_STRING);
			display.syncExec(() -> {
				String jobMessage;
				try {
					jobMessage = JobsMessages.get().getMessage(convertFaultCode(fault.getCode()));
				} catch (Exception e) {
					jobMessage = new StringBuilder()
							.append(JobsMessages.get().import_unexpected_error)
							.append(" [")
							.append(e.getMessage())
							.append("]")
							.toString();
				}
				message.setValue(
					NLS.bind(jobMessage,
					fault.getArgs()));
			});
			return message.getValue();
		}

		private String convertFaultCode(final String code) {
			return StringUtils.replaceChars(code, '.', '_');
		}

		private boolean workbenchIsClosing() {
			MutableBoolean workbenchClosed = new MutableBoolean(false);
			display.syncExec(() -> workbenchClosed.setValue(PlatformUI.getWorkbench().isClosing()));
			return workbenchClosed.getValue();
		}
	}
}
