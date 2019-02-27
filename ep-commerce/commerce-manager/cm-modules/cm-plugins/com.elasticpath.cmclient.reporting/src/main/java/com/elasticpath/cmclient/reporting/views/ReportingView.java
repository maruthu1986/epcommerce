/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/**
 * Represents the UI for displaying the reports.
 */
public class ReportingView extends ViewPart {

	private Browser browser;
	
	/**
	 * ID for ReportView.
	 */
	public static final String REPORTVIEWID = ReportingView.class.getName();


	@Override
	public void createPartControl(final Composite parent) {
		parent.setLayout(new GridLayout());
		GridData grid = new GridData(GridData.FILL_BOTH);
		browser = new Browser(parent, SWT.BEGINNING);
		grid.horizontalSpan = 2;
		browser.setLayoutData(grid);
	}

	@Override
	public void setFocus() {
		// nothing
		
	}
	
	/**
	 * Gets the browser embedded inside the view.
	 * @return org.eclipse.swt.browser.Browser
	 */
	public Browser getBrowser() {
		return browser;
	}

	/**
	 * Sets the title of the reporting view.
	 * 
	 * @param viewTitle the title of the reporting view
	 */
	public void setReportingViewTitle(final String viewTitle) {
		this.setPartName(viewTitle);
	}	
}
