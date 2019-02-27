/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.common;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;

/**
 * The class of label contribution for tool bar.
 */
public class PaginationControlLabelContribution extends ContributionItem {

	private final String labelText;

	private Label label;

	private ToolItem toolItem;

	private Composite parent;

	private final EpControlFactory controlFactory = EpControlFactory.getInstance();

	/**
	 * The constructor.
	 *
	 * @param ident     the id
	 * @param labelText the label text
	 */
	protected PaginationControlLabelContribution(final String ident, final String labelText) {
		super(ident);
		this.labelText = labelText;
	}

	/**
	 * compute the width of the control.
	 *
	 * @param control the control
	 * @return the width
	 */
	protected int computeWidth(final Control control) {
		return control.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x;
	}

	@Override
	public final void fill(final ToolBar parent, final int index) {
		this.parent = parent;
		Control control = createControl(parent);
		toolItem = controlFactory.createToolItem(parent, index, control, computeWidth(control));
	}

	/**
	 * create control.
	 *
	 * @param parent the parent composite
	 * @return the control
	 */
	protected Control createControl(final Composite parent) {
		Composite panel = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		layout.marginBottom = 0;
		layout.marginTop = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		final int horizontalSpacing = 5;
		layout.horizontalSpacing = horizontalSpacing;
		layout.verticalSpacing = 0;
		panel.setLayout(layout);

		GridData pageLabelLayoutData = new GridData(SWT.FILL, SWT.FILL, false, false);
		label = controlFactory.createLabel(panel, labelText, SWT.NONE, pageLabelLayoutData);
		return panel;
	}

	/**
	 * set the text of the label.
	 *
	 * @param pageLabelText the text
	 */
	public void setText(final String pageLabelText) {
		label.setText(pageLabelText + "    "); //$NON-NLS-1$
		toolItem.setWidth(computeWidth(label));
		label.getParent().layout();
		parent.layout();
	}

	/**
	 * Set the text field enabled.
	 *
	 * @param enabled the enable value
	 */
	public void setEnabled(final boolean enabled) {
		label.setEnabled(enabled);
	}
}