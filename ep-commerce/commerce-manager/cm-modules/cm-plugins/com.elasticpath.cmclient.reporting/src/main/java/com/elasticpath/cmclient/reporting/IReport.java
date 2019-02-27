/**
 * Copyright (c) Elastic Path Software Inc., 2007
 *
 */
package com.elasticpath.cmclient.reporting;

import java.util.Map;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;

/**
 * Defines the interface that must be implemented by Report plugins so that
 * they can provide a pane that allows for entry of report parameters.
 */
public interface IReport {

	/**
	 * Creates the parameter controls specified by the Report.
	 * 
	 * @param toolkit the top level toolkit which contains the Report configuration pane
	 * @param parent the parent composite which is the container for this specific Report Parameters section
	 * @param site the Workbench site, so that the composite can get a reference to Views that it should open.
	 */
	void createControl(FormToolkit toolkit, Composite parent, IWorkbenchPartSite site);

	/**
	 * Returns whether the user is authorized to view the Report.
	 *
	 * @return <code>true</code> if the user authorized to view the Report, <code>false</code> otherwise
	 */
	boolean isAuthorized();
	
	/**
	 * Binds inputs to controls.
	 * 
	 * @param bindingProvider the binding provider
	 * @param context the data binding context
	 */
	void bindControls(EpControlBindingProvider bindingProvider, DataBindingContext context);
	
	/**
	 * Gets the report's parameters and stores them in a map.
	 * @return map that stores parameter keys and values
	 */
	Map<String, Object> getParameters();
	
	/**
	 * Gets the title of the report.
	 * @return String the title of the report
	 */
	String getReportTitle();
	
}
