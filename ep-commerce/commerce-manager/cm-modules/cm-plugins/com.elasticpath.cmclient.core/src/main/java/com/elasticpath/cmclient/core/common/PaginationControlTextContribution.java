/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.common;

import com.elasticpath.cmclient.core.eventlistener.DigitVerifyListener;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;
import org.apache.commons.lang.math.NumberUtils;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * The class of text contribution for tool bar.
 */
public class PaginationControlTextContribution extends ContributionItem {

	private final IPaginationControl paginationControl;
	private Text currentPage;
	private ToolBar parent;
	private ToolItem toolItem;

	private final EpControlFactory controlFactory = EpControlFactory.getInstance();

	/**
	 * The constructor.
	 *
	 * @param ident the ident
	 * @param paginationControl the pagination control
	 */
	protected PaginationControlTextContribution(final String ident, final IPaginationControl paginationControl) {
		super(ident);
		this.paginationControl = paginationControl;
	}

	/**
	 * compute control width.
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
	 * create the control.
	 *
	 * @param parent the parent composite.
	 * @return the control
	 */
	protected Control createControl(final Composite parent) {
		currentPage = new Text(parent, SWT.BORDER);

		currentPage.addVerifyListener(new DigitVerifyListener());

		currentPage.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(final SelectionEvent event) {
				int toPage = paginationControl.getValidPage(NumberUtils.toInt(currentPage.getText().trim(), 0));
				currentPage.setText(String.valueOf(toPage));
				paginationControl.navigateTo(toPage);
				paginationControl.updateNavigationComponents();
			}

			@Override
			public void widgetSelected(final SelectionEvent event) {
				// nothing to do
			}
		});

		currentPage.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(final FocusEvent event) {
				//nothing to do
			}

			@Override
			public void focusLost(final FocusEvent event) {
				currentPage.setText(String.valueOf(paginationControl.getCurrentPage()));
			}
		});

		currentPage.setText("   "); //$NON-NLS-1$
		return currentPage;
	}

	/**
	 * Set the text.
	 *
	 * @param text the text
	 */
	public void setText(final String text) {
		currentPage.setText(text.trim() + "   "); //$NON-NLS-1$
		toolItem.setWidth(computeWidth(currentPage));
		parent.layout();
	}

	/**
	 * set text field visible.
	 *
	 * @param visible the boolean value of visible
	 */
	public void setTextFieldVisible(final boolean visible) {
		currentPage.setVisible(visible);
		parent.layout();
	}

	/**
	 * Set the text field enabled.
	 *
	 * @param enabled the enable value
	 */
	public void setEnabled(final boolean enabled) {
		currentPage.setEnabled(enabled);
	}

}
