/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.pricelistmanager.dialogs;

import org.eclipse.jface.dialogs.IDialogLabelKeys;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.pricelistmanager.PriceListManagerMessages;
import com.elasticpath.common.dto.pricing.BaseAmountDTO;

/**
 * Dialog shown when removing base amounts.
 * 
 */
public class BaseAmountRemoveDialog extends MessageDialog {
	
	private static final String[] BUTTONS = { 
			JFaceResources.getString(IDialogLabelKeys.OK_LABEL_KEY),
			JFaceResources.getString(IDialogLabelKeys.CANCEL_LABEL_KEY) };

	private final BaseAmountDTO baDto;

	/**
	 * Dialog for confirmation to remove a BaseAmount entry.
	 *
	 * @param parentShell the parent shell
	 * @param badto the BaseAmountDTO
	 */
	public BaseAmountRemoveDialog(final Shell parentShell, final BaseAmountDTO badto) {
		super(parentShell, PriceListManagerMessages.get().BaseAmount_Delete_Title, null,
				PriceListManagerMessages.get().BaseAmount_Delete_Message, QUESTION, BUTTONS, 0);
		this.baDto = badto;
	}

	/**
	 * Dialog for confirmation to remove a BaseAmount entry.
	 *
	 * @param parentShell the parent shell
	 * @param badto the BaseAmountDTO
	 * @param windowTitle the windowTitle
	 * @param confirmationMessage the confirmationMessage
	 */
	public BaseAmountRemoveDialog(final Shell parentShell, final BaseAmountDTO badto, final String windowTitle, final String confirmationMessage) {
		super(parentShell, windowTitle, null, 
				confirmationMessage, QUESTION, BUTTONS, 0);
		this.baDto = badto;
	}
	
	@Override
	protected void configureShell(final Shell newShell) {
		super.configureShell(newShell);
	}

	@Override
	public int open() {
		message = message + "\n\n" //$NON-NLS-1$
				+ baDto.getObjectType()
				+ "\t" //$NON-NLS-1$
				+ baDto.getObjectGuid();
		return super.open();
	}
	
	

}
