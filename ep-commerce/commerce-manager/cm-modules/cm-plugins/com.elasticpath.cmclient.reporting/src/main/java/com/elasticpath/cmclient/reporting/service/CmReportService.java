/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting.service;

import java.net.URL;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.browser.Browser;

/**
 * Provides services to create reports.
 */
public interface CmReportService {

	/**
	 * Initialize the reporting task.  Must be called once for every report.
	 * 
	 * @param isCSVorExcel true if wants to export report in csv or excel format, false otherwise
	 * @param reportLocation the report location
	 * @param classLoader the classloader of each reporting service
	 * @param params parameters of the report
	 */
	void initializeTask(boolean isCSVorExcel,
		URL reportLocation, ClassLoader classLoader, Map<String, Object> params);
	
	/**
	 * Closes the engine task when finishes.
	 */
	void closeTask();
	
	/**
	 * Views the HTML report in a browser.
	 * @param browser the browser for the html to render in
	 */
	void viewHTMLReport(Browser browser);
	
	/**
	 * Saves the report in PDF format.

	 */
	void makePdf();

	/**
	 * Saves the reports in one file of PDF format, used
	 * in packing slip report where user can generate all
	 * the reports.
	 * @param reportLocations list of all report locations
	 * @param params list of all parameters of the reports
	 * @param classLoader the class loader of the report user wishes to view
	 */
	void makePdf(List<URL> reportLocations, List<Map<String, Object>> params, ClassLoader classLoader);

	/**
	 * Saves report in CSV format.
	 */
	void makeCSV();
	
	/**
	 * Saves the report in Excel (XLS) format.
	 */
	void makeExcel();

}

