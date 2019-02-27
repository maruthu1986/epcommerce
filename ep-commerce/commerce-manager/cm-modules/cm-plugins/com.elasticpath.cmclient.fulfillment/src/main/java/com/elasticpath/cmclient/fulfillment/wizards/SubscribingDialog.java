/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.fulfillment.wizards;

import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.core.wizard.AbstractEpWizard;
import com.elasticpath.cmclient.core.wizard.EpWizardDialog;

/**
 * Provide automatic subscription of wizard to page changing event.
 */
public class SubscribingDialog extends EpWizardDialog {

	/**
	 * The Constructor.
	 * 
	 * @param parentShell the parent shell
	 * @param newWizard the wizard to be inserted into this Dialog
	 */

	public SubscribingDialog(final Shell parentShell, final AbstractEpWizard<?> newWizard) {
		super(parentShell, newWizard);
		addPageChangingListener(newWizard);
	}

}
