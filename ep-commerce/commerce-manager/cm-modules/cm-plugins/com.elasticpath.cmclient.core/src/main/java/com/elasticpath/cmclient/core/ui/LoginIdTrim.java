/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.ui;

import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.menus.AbstractWorkbenchTrimWidget;

import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.LoginManager;

/**
 * Trim for displaying of user login id.
 */
public class LoginIdTrim extends AbstractWorkbenchTrimWidget {

	private Composite composite;

	private Label labelUser;

	@Override
	public void dispose() {

		if (labelUser != null && !labelUser.isDisposed()) {
			labelUser.dispose();
			labelUser = null;
		}

		if (composite != null && !composite.isDisposed()) {
			composite.dispose();
			composite = null;
		}

	}

	@Override
	public void fill(final Composite parent, final int oldSide, final int newSide) {
		composite = new Composite(parent, SWT.NONE);
		FillLayout layout = new FillLayout();
		composite.setLayout(layout);
		layout.marginHeight = 2 + 2;
		layout.marginWidth = 2;
		labelUser = new Label(composite, SWT.BEGINNING);

		String loginId = "MockUpUser"; //$NON-NLS-1$
		if (LoginManager.getInstance() != null) {
			loginId = LoginManager.getCmUserUsername();
		}
		labelUser.setText(
			NLS.bind(CoreMessages.get().LoginIdTrim_Text,
			loginId));
	}
}
