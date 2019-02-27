/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting;

/**
 * Extension interface that enforces additional behaviors not suitable
 * for the IReport interface.  This interface is constructed for the purpose 
 * of enforcing validation behavior for the reports.
 *
 */
public interface IReportExtension extends IReport {

	/**
	 * Validates if the input is valid for report parameters.
	 * 
	 * @return true if input is valid, false otherwise
	 */
	boolean isInputValid();
	
	/**
	 * Refreshes the report parameter section layout.
	 */
	void refreshLayout();
}
