/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.core.ui.framework.impl;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * A <code>Label</code> that visually indicates required status.
 */
public class EpRequiredLabel extends Label {

	private static final String REQUIRED_PREFIX = "* "; //$NON-NLS-1$
	
	private String text;
	
	/**
	 * Create a new required label.
	 * 
	 * @param parent the SWT parent
	 * @param style the SWT style
	 */
	public EpRequiredLabel(final Composite parent, final int style) {
		super(parent, style);
	}

	@Override
	public void setText(final String text) {
		if ((super.getStyle() & SWT.READ_ONLY) != 0) {
			return;
		}
		this.text = text;
		setLabelText(getEnabled());
	}

	@Override
	public void setEnabled(final boolean enabled) {
		setLabelText(enabled);
		super.setEnabled(enabled);
	}

	/**
	 * Set the text in the Label control.
	 * 
	 * @param enabled
	 */
	private void setLabelText(final boolean enabled) {
		if (enabled) {
			super.setText(REQUIRED_PREFIX.concat(text));
		} else {
			super.setText(text);
		}
	}

	@Override
	protected void checkSubclass() {
		// Allow subclassing
	}
	

}
