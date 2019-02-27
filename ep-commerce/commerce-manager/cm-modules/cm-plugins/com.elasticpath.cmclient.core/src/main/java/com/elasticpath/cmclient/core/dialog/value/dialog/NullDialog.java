/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.dialog.value.dialog;

import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.core.helpers.IValueRetriever;

/**
 * Null Dialog is used to denote a dialog for unsupported type
 * in EditingSupportDialogFactory.
 */
public class NullDialog extends AbstractValueDialog<String> implements IValueRetriever {

	/**
	 * The constructor of the NULL dialog window.
	 * 
	 * @param parentShell the parent shell object of the dialog window
	 */
	public NullDialog(final Shell parentShell) {
		super(parentShell, null, true, false);
	}

	@Override
	protected String getEditTitle() {
		return null;
	}
	
	@Override
	protected String getAddTitle() {
		return null;
	}

	@Override
	protected String getEditWindowTitle() {
		return null;
	}
	
	@Override
	protected String getAddWindowTitle() {
		return null;
	}

	@Override
	protected void populateControls() {
		// do nothing
	}
	
}
