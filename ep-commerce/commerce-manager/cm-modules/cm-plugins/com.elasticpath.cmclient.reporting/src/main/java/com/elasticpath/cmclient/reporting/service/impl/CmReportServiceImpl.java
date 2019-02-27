/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.reporting.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.elasticpath.cmclient.core.DownloadServiceHandler;
import com.elasticpath.cmclient.core.util.FileSystemUtil;
import com.elasticpath.cmclient.core.util.ServiceUtil;
import com.elasticpath.cmclient.reporting.ReportingPlugin;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.report.engine.api.EngineConstants;
import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.IDataExtractionTask;
import org.eclipse.birt.report.engine.api.IDataIterator;
import org.eclipse.birt.report.engine.api.IEngineTask;
import org.eclipse.birt.report.engine.api.IExtractionResults;
import org.eclipse.birt.report.engine.api.IRenderOption;
import org.eclipse.birt.report.engine.api.IRenderTask;
import org.eclipse.birt.report.engine.api.IReportDocument;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IResultMetaData;
import org.eclipse.birt.report.engine.api.IResultSetItem;
import org.eclipse.birt.report.engine.api.IRunTask;
import org.eclipse.birt.report.engine.api.impl.RenderTask;
import org.eclipse.birt.report.model.api.ReportDesignHandle;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Display;

import com.elasticpath.cmclient.reporting.ReportingMessages;
import com.elasticpath.cmclient.reporting.service.CmReportService;
import com.lowagie.tools.ConcatPdf;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.UrlLauncher;

/**
 * 
 * Provides services to create reports with the 
 * Eclipse Business Intelligence Reporting Tool (BIRT).
 */
@SuppressWarnings({"PMD.AvoidUsingShortType", "PMD.GodClass"})
public class CmReportServiceImpl implements CmReportService {

	private static final Logger LOG = Logger.getLogger(CmReportServiceImpl.class);
	/**
	 * specifies the temp folder.
	 */
	private static final String TEMPFILENAME = "tempReport"; //$NON-NLS-1$
	
	/**
	 * For POI excel spreadsheet column width, the unit is 1/256 of a character.
	 */
	private static final int NORMALIZE_FACTOR = 256;
	
	private static final double ONEPOINTFIVE = 1.5;

	private static final String BIRT_EXCEPTION_THROWN = "Birt Exception thrown";
	private static final String FILE_NOT_FOUND = "File Not Found";
	private static final String IOEXCEPTION_THROWN = "IOException thrown";

	private IEngineTask task;

	private static final String NAME_TO_BE_FILTERED = "TOTAL_COLUMN"; //$NON-NLS-1$
	
	private IReportEngine engine;

	/**
	 * Initialize the reporting task.  Must be called once for every report.
	 * 
	 * @param isCSVorExcel true if wants to export report in csv or excel format, false otherwise
	 * @param reportLocation the report location in disk
	 * @param classLoader the classloader of each reporting service
	 * @param params parameters of the report
	 */
	public void initializeTask(final boolean isCSVorExcel,
		final URL reportLocation, final ClassLoader classLoader, final Map<String, Object> params) {
		try {
			engine = ReportingPlugin.getInstance().initializeEngine();
		} catch (BirtException e) {
			LOG.error("Report Engine failed to initialize", e); //$NON-NLS-1$
		}
		try {
			task = createTask(isCSVorExcel, reportLocation, classLoader, params);
		} catch (IOException e) {
			LOG.error("Error creating IEngineTask", e);
		}
	}
	
	/**
	 * Closes the engine task when finishes.
	 */
	public void closeTask() {
		IReportEngine engine = task.getEngine();
		task.close();
		engine.destroy();	
	}
	
	/**
	 * Creates the IEngineTask to run and render reports.
	 * @param isCSV for csv file
	 * @param reportLocation absolute path of the report
	 * @return IEngineTask the task created
	 * @throws IOException thrown when IO exceptions occur.
	 */
	private IEngineTask createTask(final boolean isCSVorExcel,
			final URL reportLocation, final ClassLoader classLoader, final Map<String, Object> params) throws IOException {
		
		File tempFile = null;
		InputStream urlstream = null;
		IRunTask runtask = null;
		
		try {
			urlstream = reportLocation.openStream();
			IReportRunnable design = null;
			try {
				design = engine.openReportDesign(urlstream);
			} catch (final Exception e) {
				LOG.error(new StringBuffer("Engine not initialized"), e); //$NON-NLS-1$
			}

			try {
				URL cssUrl = ReportingPlugin.getInstance().getBundle().getResource("css/report.css");
				ReportDesignHandle designHandle = (ReportDesignHandle) design.getDesignHandle();
				designHandle.addCssByProperties(cssUrl.toString(), null, false);
			} catch (final Exception e) {
				LOG.error(new StringBuffer("Error loading report.css"), e); //$NON-NLS-1$
			}

			runtask = engine.createRunTask(design);
			HashMap<String, ClassLoader> contextMap = new HashMap<String, ClassLoader>();
			contextMap.put(EngineConstants.APPCONTEXT_CLASSLOADER_KEY, new RhinoClassLoader(classLoader)); 
			setTaskParameters(runtask, params);
			engine.getConfig().setAppContext(contextMap);

			try {
				tempFile = File.createTempFile(TEMPFILENAME, ".rptdocument"); //$NON-NLS-1$
			} catch (IOException e1) {
				LOG.error(new StringBuffer("IOException thrown when creating temp rptdocument file"), e1); //$NON-NLS-1$
			}
			
			try {
				runtask.run(tempFile.getAbsolutePath());
			} catch (final Exception e) {
				LOG.error(new StringBuffer("Error running engine task"), e); //$NON-NLS-1$
			}
		} finally {
			if (runtask != null) {
				runtask.close();
			}
			if (urlstream != null) {
				urlstream.close();
			}
		}

		IReportDocument doc = null;
		try {
			doc = engine.openReportDocument(tempFile.getAbsolutePath());
		} catch (final Exception e) {
			LOG.error(new StringBuffer("Error opening report document"), e); //$NON-NLS-1$
		}
		IEngineTask task = engine.createRenderTask(doc);
		if (isCSVorExcel) {
			task = engine.createDataExtractionTask(doc);
		}
		tempFile.deleteOnExit();
		return task;
	}
	
	/**
	 * Set parameter values on report.  Don't set values if null.  Package-level
	 * for testing purposes only.
	 * @param task task to set params.
	 * @param params map of params.
	 */
	void setTaskParameters(final IEngineTask task, final Map<String, Object> params) {
		if (params == null || params.isEmpty()) {
			return;
		}
		for (Iterator<String> i = params.keySet().iterator(); i.hasNext();) {
			String name = i.next();
			task.setParameterValue(name, params.get(name));
		}
	}
	
	/**
	 * Views the HTML report in a browser.
	 * @param browser the browser object where HTML report can be viewed
	 */
	public void viewHTMLReport(final Browser browser) {

		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			final IRenderOption options = new HTMLRenderOption();
			options.setOutputFormat(IRenderOption.OUTPUT_FORMAT_HTML);
			options.setOutputStream(bos);

			IRenderTask renderTask = (IRenderTask) task;
			renderTask.setRenderOption(options);
			renderTask.render();

			browser.setText(bos.toString("UTF-8"));
		} catch (final BirtException birtException) {
			LOG.error(new StringBuffer("Engine Exception thrown")); //$NON-NLS-1$
			LOG.error(new StringBuffer(BIRT_EXCEPTION_THROWN)); //$NON-NLS-1$
		} catch (UnsupportedEncodingException e) {
			LOG.error("Encoding is not supported", e); //$NON-NLS-1$
		}

	}
	
	/**
	 * Downloads the report in Excel (XLS) format.
	 */
	public void makeExcel() {
		String fileName = getDefaultFileName(ReportingMessages.get().xls);
		File xlsFile = new File(fileName);

		try (final FileOutputStream file = new FileOutputStream(xlsFile.getAbsolutePath())) {
			final IDataExtractionTask dataExtractionTask = (IDataExtractionTask) task;
			final List<?> resultSetList = dataExtractionTask.getResultSetList();
			final IResultSetItem resultItem = (IResultSetItem) resultSetList.get(0);
			final String dispName = resultItem.getResultSetName();
			dataExtractionTask.selectResultSet(dispName);
			final IExtractionResults iExtractResults = dataExtractionTask.extract();

			HSSFWorkbook spreadSheetWorkBook = new HSSFWorkbook();
			HSSFSheet spreadSheet = spreadSheetWorkBook.createSheet("page 1"); //$NON-NLS-1$
			
			if (iExtractResults != null) {
				writeToXLS(iExtractResults, spreadSheet);
			}
			
			spreadSheetWorkBook.write(file);
			downloadReport(xlsFile.getName());
			iExtractResults.close();

		} catch (final FileNotFoundException exception) {
			LOG.error(new StringBuffer(FILE_NOT_FOUND)); //$NON-NLS-1$
		} catch (final IOException exception) {
			LOG.error(new StringBuffer(IOEXCEPTION_THROWN)); //$NON-NLS-1$
		} catch (final BirtException exception) {
			LOG.error(new StringBuffer(BIRT_EXCEPTION_THROWN)); //$NON-NLS-1$
		}
	
	}

	/**
	 * Writes the report output to xls report spreadsheet.
	 * @param iExtractResults the result output from report
	 * @param spreadSheet the spreadsheet to write to
	 * @throws BirtException exception thrown
	 */
	private void writeToXLS(final IExtractionResults iExtractResults, final HSSFSheet spreadSheet) throws BirtException {
		final IDataIterator iData = iExtractResults.nextResultIterator();
		if (iData != null) {
			HSSFRow columnRow = spreadSheet.createRow((short) 0);
			
			//Get metadata on retrieved results
			final IResultMetaData irmd = iData.getResultMetaData();
			int avgColumnLength = 0;
			List<Integer> filteredIndices = new ArrayList<Integer>();
			for (int i = 0; i < irmd.getColumnCount(); i++) {
				//*** NOTICE: Workaround for MSC-6327.
				String columnName = irmd.getColumnName(i);
				if (isRightColumn(columnName)) {
					continue;
				}
				filteredIndices.add(i);
				//************************************ 
				HSSFCell cell = columnRow.createCell((short) filteredIndices.indexOf(i));
				cell.setCellValue(new HSSFRichTextString(columnName));
				avgColumnLength += irmd.getColumnLabel(i).length();
			}
			avgColumnLength = avgColumnLength / filteredIndices.size();
			int dataRowCount = 1;
			while (iData.next()) {
				HSSFRow dataRow = spreadSheet.createRow((short) dataRowCount);
				for (int index : filteredIndices) {
					Object temp = iData.getValue(index);
					int columnNumber = filteredIndices.indexOf(index);
					int dataLength = 0;
					if (temp != null) { //NOPMD
						dataLength = temp.toString().length();
					} else {
						dataLength = avgColumnLength;
					}
					if (iData.getValue(index) == null) {
						HSSFCell dataCell = dataRow.createCell((short) columnNumber);
						dataCell.setCellValue(new HSSFRichTextString("")); //$NON-NLS-1$
						spreadSheet.setColumnWidth((short) columnNumber, 
							(short) (avgColumnLength * NORMALIZE_FACTOR * ONEPOINTFIVE));
					} else {
						HSSFCell dataCell = dataRow.createCell((short) columnNumber);
						setCellValue(iData.getValue(index), dataCell);
						if (dataLength > avgColumnLength) {
							spreadSheet.setColumnWidth((short) columnNumber, 
								(short) (dataLength * NORMALIZE_FACTOR * ONEPOINTFIVE));
						} else {
							spreadSheet.setColumnWidth((short) columnNumber, 
								(short) (avgColumnLength * NORMALIZE_FACTOR * ONEPOINTFIVE));
						}
					}
				}
				dataRowCount++;
			}
			iData.close();
		}
	}

	/**
	 * Sets the data cell value of the excel spreadsheet. Default case is to turn the data into a
	 * HSSFRichTextString format.
	 *
	 * @param value the value to input into the cell
	 * @param dataCell the cell in the spreadsheet
	 * @throws BirtException throws a BirtException
	 */
	private void setCellValue(final Object value, final HSSFCell dataCell) throws BirtException {
		if (value instanceof Integer) {
			dataCell.setCellValue(Double.parseDouble(value.toString()));
		} else if (value instanceof Double) {
			dataCell.setCellValue((Double) value);
		} else if (value instanceof Boolean) {
			dataCell.setCellValue((Boolean) value);
		} else {
			dataCell.setCellValue(new HSSFRichTextString(value.toString()));
		}
	}

	/**
	 * Downloads the report in PDF format.
	 */
	public void makePdf() {

		String fileName = getDefaultFileName(ReportingMessages.get().pdf);
		File pdfFile = new File(fileName);
		try (final FileOutputStream file = new FileOutputStream(pdfFile.getAbsolutePath())) {
			final IRenderTask renderTask = (IRenderTask) task;
			final ByteArrayOutputStream bos = new ByteArrayOutputStream();

			final IRenderOption options = new HTMLRenderOption();
			options.setOutputFormat(IRenderOption.OUTPUT_FORMAT_PDF);
			options.setOutputStream(bos);

			renderTask.setRenderOption(options);
			renderTask.render();
			
			bos.writeTo(file);
			bos.close();

			downloadReport(pdfFile.getName());

		} catch (final FileNotFoundException exception) {
			LOG.error(new StringBuffer(FILE_NOT_FOUND)); //$NON-NLS-1$
		} catch (final IOException exception) {
			LOG.error(new StringBuffer(IOEXCEPTION_THROWN)); //$NON-NLS-1$
		} catch (final Exception exception) {
			LOG.error(new StringBuffer(BIRT_EXCEPTION_THROWN)); //$NON-NLS-1$
		}
	}

	/**
	 * Download a file.
	 * @param filename the filename.
	 */
	protected void downloadReport(final String filename) {
		UrlLauncher urlLauncher = ServiceUtil.getUrlLauncherService();
		try {
			if (urlLauncher != null) {
				Display.getDefault().asyncExec(() -> {
					String fileURL = createDownloadUrl(filename);
					urlLauncher.openURL(fileURL);
				});
			}
		} catch (final Exception e) {
			LOG.error(e.getMessage());
		}
	}

	/**
	 * Generate a file and return the filename.
	 * @param extension the extension of the file being generated.
	 * @return the filename.
	 */
	protected String getDefaultFileName(final String extension) {
		StringBuilder stringBuilder = new StringBuilder(FileSystemUtil.getTempDirectory()).append(File.separator);
		stringBuilder.append(UUID.randomUUID()).append(extension);
		return stringBuilder.toString();
	}

	/**
	 * Create a download url.
	 * @param filename the filename.
	 * @return the download url.
	 */
	protected String createDownloadUrl(final String filename) {
		StringBuilder url = new StringBuilder();

		url.append(RWT.getServiceManager().getServiceHandlerUrl(DownloadServiceHandler.SERVICE_NAME));
		url.append("&filename=").append(filename);

		return url.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	public void makePdf(final List<URL> reportLocations, 
		final List<Map<String, Object>> params, final ClassLoader classLoader) {
		if (reportLocations.size() != params.size()) {
			LOG.error(new StringBuffer("Wrong parameters")); //$NON-NLS-1$
			return;
		}

		try {
			List<String> files = new ArrayList<String>();
			for (int i = 0; i < reportLocations.size(); ++i) {

				initializeTask(false, reportLocations.get(i), classLoader, params.get(i));
				
				final IRenderTask renderTask = (RenderTask) task;
					
				final ByteArrayOutputStream bos = new ByteArrayOutputStream();

				final IRenderOption options = new HTMLRenderOption();
				options.setOutputFormat(IRenderOption.OUTPUT_FORMAT_PDF);
				options.setOutputStream(bos);

				renderTask.setRenderOption(options);
				renderTask.render();

				String fileName = getDefaultFileName(ReportingMessages.get().pdf);
				File pdfFile = new File(fileName);

				final FileOutputStream file = new FileOutputStream(pdfFile.getAbsolutePath());
				bos.writeTo(file);

				files.add(pdfFile.getName());

				bos.close();
				file.close();
				task.close();
			}
			
			String fileName = getDefaultFileName(ReportingMessages.get().pdf);
			File resFile = new File(fileName);

			files.add(resFile.getName());

			ConcatPdf.main(files.toArray(new String[files.size()]));

			downloadReport(resFile.getName());

		} catch (final FileNotFoundException exception) {
			LOG.error(new StringBuffer(FILE_NOT_FOUND)); //$NON-NLS-1$
		} catch (final IOException exception) {
			LOG.error(new StringBuffer(IOEXCEPTION_THROWN)); //$NON-NLS-1$
		} catch (final Exception exception) {
			LOG.error(new StringBuffer(BIRT_EXCEPTION_THROWN)); //$NON-NLS-1$
		}
	}
	
	
	/**
	 * Downloads report in CSV format.
	 */
	public void makeCSV() {
		try {
			final IDataExtractionTask dataExtractionTask = (IDataExtractionTask) task;
			final List<?> resultSetList = dataExtractionTask.getResultSetList();
			final IResultSetItem resultItem = (IResultSetItem) resultSetList.get(0);
			final String dispName = resultItem.getResultSetName();
			dataExtractionTask.selectResultSet(dispName);
			final IExtractionResults iExtractResults = dataExtractionTask.extract();

			String fileName = getDefaultFileName(ReportingMessages.get().csv);
			File csvFile = new File(fileName);

			final FileWriter fileToSave = new FileWriter(csvFile.getAbsolutePath());
			if (iExtractResults != null) {
				printCSV(iExtractResults, fileToSave);
			}
			fileToSave.close();
			iExtractResults.close();

			downloadReport(csvFile.getName());

		} catch (final FileNotFoundException exception) {
			LOG.error(new StringBuffer(FILE_NOT_FOUND)); //$NON-NLS-1$
		} catch (final IOException exception) {
			LOG.error(new StringBuffer(IOEXCEPTION_THROWN)); //$NON-NLS-1$
		} catch (final Exception exception) {
			LOG.error(new StringBuffer(BIRT_EXCEPTION_THROWN)); //$NON-NLS-1$
		
		}
	}

	private void printCSV(final IExtractionResults iExtractResults, final FileWriter file) {
		try {
			final IDataIterator iData = iExtractResults.nextResultIterator();
			if (iData != null) {
				PrintWriter writer = new PrintWriter(file);
				//Get metadata on retrieved results
				final IResultMetaData irmd = iData.getResultMetaData();
				List<Integer> filteredIndices = new ArrayList<Integer>();
				for (int i = 0; i < irmd.getColumnCount(); i++) {
					String columnName = irmd.getColumnLabel(i);
					if (isRightColumn(columnName)) {
						continue;
					}
					filteredIndices.add(i);
					writer.print("\"" + columnName.replaceAll("\"", "\"\"") + "\","); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$ //$NON-NLS-4$
				}
				writer.println();
				while (iData.next()) {
					printRow(iData, filteredIndices, writer);
				}
				writer.close();
				iData.close();
			}
		} catch (final Exception exception) {
			LOG.error(new StringBuffer(BIRT_EXCEPTION_THROWN)); //$NON-NLS-1$
		} 
	}
	
	private void printRow(final IDataIterator dataitr, final List<Integer> filteredIndices, final PrintWriter writer) {
		try {
			for (int index : filteredIndices) {
				if (dataitr.getValue(index) == null) {
					writer.print(","); //$NON-NLS-1$
				} else {
					writer
							.print("\"" + dataitr.getValue(index).toString().replaceAll(//$NON-NLS-1$
													"\"", "\"\"") + "\","); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
				}
			}
		} catch (final Exception exception) {
			LOG.error(new StringBuffer("Failed to get column value.")); //$NON-NLS-1$
		}
		writer.println();
	}

	/*
	 * This methods should return if column defined by columnName must be displayed on report. 
	 */
	private boolean isRightColumn(final String columnName) {
		return columnName.contains(NAME_TO_BE_FILTERED.subSequence(0, NAME_TO_BE_FILTERED.length()));
	}

	
	
	/**
	 * Forces Birt to work with Rhino from the OSGi class-realm, rather than finding the
	 * Rhino classes from cmclientlibs (currently 1.6R2 as part of spring modules.  The 
	 * upgrade to RCP 3.6 requried us to do this as there is a class check in Kit.class
	 * (testIfCanLoadRhinoClasses) which would fail to recognize two copies of 
	 * ContextFactory as the same class. 
	 */
	private static class RhinoClassLoader extends ClassLoader {

		private final ClassLoader reportPluginClassLoader;
		private final ClassLoader classLoaderForRhino;

		/**
		 * Create an instance to fool Rhino into working.
		 * @param applicationClassLoader the report-specific classloader.
		 */
		RhinoClassLoader(final ClassLoader applicationClassLoader) {
			this.reportPluginClassLoader = applicationClassLoader;
			this.classLoaderForRhino = org.eclipse.birt.report.engine.api.DataID.class.getClassLoader();
		}

		@Override
		protected Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
			// Force loading of this class through the Rhino classloader to trick Birt to work.
			if ("org.mozilla.javascript.ContextFactory".equals(name)) { //$NON-NLS-1$
				return classLoaderForRhino.loadClass(name);
			}			
			return reportPluginClassLoader.loadClass(name);
		}
	}	
	
}
