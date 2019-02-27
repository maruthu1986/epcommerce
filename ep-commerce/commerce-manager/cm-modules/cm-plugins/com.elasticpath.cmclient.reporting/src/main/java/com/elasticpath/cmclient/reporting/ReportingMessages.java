/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.reporting;

import com.elasticpath.cmclient.core.nls.LocalizedMessagePostProcessor;

/**
 * Messages class for the admin plugin.
 */
@SuppressWarnings("PMD.VariableNamingConventions")
public final class ReportingMessages {
	private static final String BUNDLE_NAME = "com.elasticpath.cmclient.reporting.ReportingPluginResources"; //$NON-NLS-1$
	
	private ReportingMessages() { }
	
	public String monitorRunning;
		
	public String reportTypeRequired;
	
	public String reportType;
	
	public String selectReportType;
	
	public String runReport;
	
	public String clear;
	
	public String parameters;
	
	public String emptyString;

	public String HTMLFormat;

	public String ExcelFormat;

	public String PDFFormat;

	public String CSVFormat;

	public String reportFormat;

	public String csv;
	
	public String filterCsv;
	
	public String pdf;
	
	public String filterPdf;
	
	public String xls;
	
	public String filterXls;

	/**
	 * Gets the NLS localize message class.
	 * @return the localized message class.
	 */
	public static ReportingMessages get() {
		return LocalizedMessagePostProcessor.getUTF8Encoded(BUNDLE_NAME, ReportingMessages.class);
	}

}