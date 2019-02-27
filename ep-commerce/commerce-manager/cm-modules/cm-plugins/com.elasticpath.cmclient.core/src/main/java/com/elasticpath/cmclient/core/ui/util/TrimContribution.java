/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.ui.util;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;

import com.elasticpath.cmclient.core.LoginManager;

/**
 * Control that holds the label which displays user name.
 */
public class TrimContribution extends WorkbenchWindowControlContribution {

	private static final int HORIZONTAL_SPACING = 10;

	@Override
	protected Control createControl(final Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);

		GridLayout gridLayout = new GridLayout();
		gridLayout.horizontalSpacing = HORIZONTAL_SPACING;
		composite.setLayout(gridLayout);

		Label labelUser = new Label(composite, SWT.CENTER);
		String loginId = StringUtils.EMPTY;
		if (LoginManager.getInstance() != null) {
			loginId = LoginManager.getCmUserUsername();
		}
		labelUser.setText(loginId);

		return composite;
	}
}

