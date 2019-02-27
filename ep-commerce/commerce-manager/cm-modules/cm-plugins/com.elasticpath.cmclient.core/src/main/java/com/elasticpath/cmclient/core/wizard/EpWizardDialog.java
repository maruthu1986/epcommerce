/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.wizard;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.core.CmClientResources;
import com.elasticpath.cmclient.core.helpers.EPTestUtilFactory;
import com.elasticpath.cmclient.core.wizard.page.IBeforeFinishNotifier;

/**
 * The EpWizardDialog class extends the WizardDialog to add EP-specific functionality (particularly colors).
 */
public class EpWizardDialog extends WizardDialog {

	/**
	 * Constructor. Creates and EpWizardDialog.
	 *
	 * @param parentShell the parent shell
	 * @param newWizard   the wizard to be inserted into this Dialog
	 */
	public EpWizardDialog(final Shell parentShell, final IWizard newWizard) {
		super(parentShell, newWizard);

		if (newWizard instanceof AbstractEpWizard) {
			// set reference to dialog for wizard
			((AbstractEpWizard<?>) getWizard()).setWizardDialog(this);
		}

	}

	@Override
	protected void configureShell(final Shell newShell) {
		super.configureShell(newShell);
		EPTestUtilFactory.getInstance().getTestIdUtil().setId(newShell, this.getWizard().getWindowTitle());
	}

	@Override
	protected Control createDialogArea(final Composite parent) {
		final Composite epDialogArea = (Composite) super.createDialogArea(parent);
		// Ensure that the wizard pages are WHITE
		epDialogArea.setBackground(CmClientResources.getBackgroundColor());
		epDialogArea.setBackgroundMode(SWT.INHERIT_FORCE);
		return epDialogArea;
	}

	@Override
	public void updateButtons() {
		super.updateButtons();

		if (getWizard() instanceof AbstractEpWizard) {
			((AbstractEpWizard<?>) getWizard()).onUpdateButtons(this);
		}
	}

	@Override
	public void showPage(final IWizardPage page) {
		super.showPage(page);
		if (getWizard().getStartingPage() == page) {
			page.setPreviousPage(null);
			updateButtons();
		} else if (page instanceof IBeforeFinishNotifier) {
			// force redraw if we are blocking/ unblocking buttons
			updateButtons();
		}
	}

	/**
	 * This method just increases visibility of getButton() method to allow descendants to disable wizard buttons.
	 *
	 * @param buttonId button identifier.
	 * @return button reference.
	 */
	public Button getWizardButton(final int buttonId) {
		return getButton(buttonId);
	}

	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
		super.createButtonsForButtonBar(parent);
		int[] buttonIds = {
			IDialogConstants.FINISH_ID,
			IDialogConstants.BACK_ID,
			IDialogConstants.CANCEL_ID,
			IDialogConstants.OK_ID,
			IDialogConstants.NEXT_ID
		};

		for (int buttonId : buttonIds) {
			Button button = getButton(buttonId);

			if (button != null) {
				EPTestUtilFactory.getInstance().getTestIdUtil().setId(button, button.getText());
			}
		}
	}
}