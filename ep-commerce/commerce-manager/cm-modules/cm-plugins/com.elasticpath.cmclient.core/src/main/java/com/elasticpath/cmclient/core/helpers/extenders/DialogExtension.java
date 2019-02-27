/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.helpers.extenders;


import com.elasticpath.cmclient.core.ui.dialog.AbstractEpDialog;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;

/**
 * Provides interface for dialogs classes to add custom fields/data to existing {@link AbstractEpDialog} classes..
 */
public interface DialogExtension {
	/**
	 * Binds the extension controls to dialog.
	 * @param dialog the dialog to extend to.
	 */
	void bindControls(AbstractEpDialog dialog);

	/**
	 * Populates the extension controls on the dialog.
	 * @param dialog the dialog to extend.
	 */
	void populateControls(AbstractEpDialog dialog);

	/**
	 * Creates the extension dialog content.
	 * @param dialogComposite the dialog composite.
	 * @param dialog the dialog to extend.
	 */
	void createEpDialogContent(IEpLayoutComposite dialogComposite, AbstractEpDialog dialog);
}
