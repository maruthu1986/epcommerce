/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.jobs.dialogs;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.core.ui.dialog.AbstractEpDialog;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.jobs.JobsMessages;
import com.elasticpath.cmclient.jobs.JobsPlugin;
import com.elasticpath.domain.dataimport.ImportBadRow;
import com.elasticpath.domain.dataimport.ImportFault;
import com.elasticpath.domain.dataimport.ImportJobStatus;

/**
 * Completion dialog.
 */
public class ImportCompletionDialog extends AbstractEpDialog {

	private final ImportJobStatus runningJob;

	/**
	 * Constructs the dialog.
	 * 
	 * @param runningJob running job
	 * @param parentShell the parent Shell A new price tier will be created automatically.
	 */
	public ImportCompletionDialog(final Shell parentShell, final ImportJobStatus runningJob) {
		super(parentShell, 2, false);
		this.runningJob = runningJob;
	}

	@Override
	protected void createEpDialogContent(final IEpLayoutComposite dialogComposite) {

		final IEpLayoutData labelData = dialogComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL);
		final IEpLayoutData fieldData = dialogComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);

		dialogComposite.addLabel(JobsMessages.get().RunWizard_CompletionDialog_StartTime, labelData);
		dialogComposite.addLabel(String.valueOf(runningJob.getStartTime()), fieldData);

		dialogComposite.addLabel(JobsMessages.get().RunWizard_CompletionDialog_ImportJobName, labelData);
		dialogComposite.addLabel(String.valueOf(runningJob.getImportJob().getName()), fieldData);

		dialogComposite.addLabel(JobsMessages.get().RunWizard_CompletionDialog_TotalRows, labelData);
		dialogComposite.addLabel(String.valueOf(runningJob.getTotalRows()), fieldData);

		dialogComposite.addLabel(JobsMessages.get().RunWizard_CompletionDialog_SucceededRows, labelData);
		dialogComposite.addLabel(String.valueOf(runningJob.getSucceededRows()), fieldData);

		dialogComposite.addLabel(JobsMessages.get().RunWizard_CompletionDialog_FailedRows, labelData);
		dialogComposite.addLabel(String.valueOf(runningJob.getFailedRows()), fieldData);

		
		String errorMessage = ""; //$NON-NLS-1$
		if (runningJob.getFailedRows() > 0) {
			errorMessage = JobsMessages.get().RunWizard_UnexpectedErrors;
			
			dialogComposite.addLabel("Errors:", labelData); //$NON-NLS-1$
			List list = dialogComposite.addList(EpState.EDITABLE, 
					dialogComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, false, true));
			
			for (ImportBadRow badRow : runningJob.getBadRows()) {
				for (ImportFault fault : badRow.getImportFaults()) {
					list.add(fault.toString());
				}
			}			
		}
		
		dialogComposite.addLabel(JobsMessages.get().RunWizard_CompletionDialog_ErrorsMessage, labelData);
		dialogComposite.addLabel(errorMessage, fieldData);
		
		dialogComposite.addLabel(JobsMessages.get().RunWizard_CompletionDialog_EndTime, labelData);
		dialogComposite.addLabel(String.valueOf(runningJob.getEndTime()), fieldData);
	}

	@Override
	protected String getPluginId() {
		return JobsPlugin.PLUGIN_ID;
	}

	@Override
	public Object getModel() {
		return runningJob;
	}

	@Override
	protected void populateControls() {
		// Do nothing
	}

	@Override
	protected void bindControls() {
		// Do nothing
	}

	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
		createEpOkButton(parent, ButtonsBarType.OK.getButtonLabel(), ButtonsBarType.OK.getImage());
	}

	@Override
	protected String getTitle() {
		return JobsMessages.get().RunWizard_ImportComplete_Details;
	}

	@Override
	protected String getWindowTitle() {
		return JobsMessages.get().RunWizard_ImportComplete_Title;
	}

	@Override
	protected String getInitialMessage() {
		return JobsMessages.get().RunWizard_ImportComplete_Message;
	}

	@Override
	protected Image getWindowImage() {
		return null;
	}
}
