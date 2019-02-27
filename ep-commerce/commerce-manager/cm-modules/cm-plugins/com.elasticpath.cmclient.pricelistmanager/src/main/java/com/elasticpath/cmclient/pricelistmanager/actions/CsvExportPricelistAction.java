/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.pricelistmanager.actions;


import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.UrlLauncher;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.core.DownloadServiceHandler;
import com.elasticpath.cmclient.core.csv.PriceListCsvExportNotificationListener;
import com.elasticpath.cmclient.core.util.ServiceUtil;
import com.elasticpath.cmclient.policy.ui.AbstractPolicyAwareAction;
import com.elasticpath.cmclient.pricelistmanager.PriceListManagerMessages;
import com.elasticpath.cmclient.pricelistmanager.views.PriceListSearchResultsView;

/**
 * CSV export action, performs CSV export procedure for selected pricelist.
 */
public class CsvExportPricelistAction extends AbstractPolicyAwareAction {

	private static final Logger LOG = Logger.getLogger(CsvExportPricelistAction.class.getName());

	private final PriceListSearchResultsView view;

	private String priceListGuid;
	private boolean isExportCancelled;
	private boolean isExportFailed;
	private Throwable lastError;
	private final Display display;
	private final String errorMessage;
	private final String failedMessage;
	private final String subTaskNotifyLoadingDataMessage;
	private final String subTaskNotifyTransformingDataMessage;
	private final String subTaskNotifyExportingDataMessage;
	private final String subTaskName;

	/**
	 * The constructor.
	 * @param text the tool tip text.
	 * @param view PriceList list view.
	 * @param display the display.
	 * @param imageDescriptor the image is shown at the title.
	 */	
	public CsvExportPricelistAction(final PriceListSearchResultsView view,
									final String text, final ImageDescriptor imageDescriptor, final Display display) {
		super(text, imageDescriptor);
		this.view = view;
		this.display = display;
		LOG.info("Created " + CsvExportPricelistAction.class.getName());  //$NON-NLS-1$
		errorMessage = PriceListManagerMessages.get().CSV_Export_Cancelled;
		failedMessage = PriceListManagerMessages.get().CSV_Export_Failed;
		subTaskNotifyLoadingDataMessage = PriceListManagerMessages.get().CSV_Retrieving_Data;
		subTaskNotifyTransformingDataMessage = PriceListManagerMessages.get().CSV_Converting_Data;
		subTaskNotifyExportingDataMessage = PriceListManagerMessages.get().CSV_Exporting_File;
		subTaskName = PriceListManagerMessages.get().CSV_Exporting_Prices;
	}

	@Override
	public void run() {
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getDefault().getActiveShell());
		try {
			dialog.run(true, true, monitor -> executeAction(monitor));
		} catch (InvocationTargetException e) {
			LOG.error(String.format("Prices CSV export process failed for price list [%s]", getPriceListGuid()), e); //$NON-NLS-1$
		} catch (InterruptedException e) {
			LOG.error(String.format("Prices CSV export process was interrupted for price list [%s]", getPriceListGuid()), e); //$NON-NLS-1$
		}

		// TODO: This "works" but seems wrong if dialog.run forks to another thread...
		if (isExportCancelled) {

			showErrorDialog(errorMessage);
			isExportCancelled = false;
		}

		if (isExportFailed) {

			showErrorDialog(failedMessage, lastError);
			isExportFailed = false;
			lastError = null;
		}
	}

	private void showErrorDialog(final String message) {
		showErrorDialog(message, null);
	}

	private void showErrorDialog(final String message, final Throwable throwable) {
		final String detailMessage;
		if (throwable == null) {
			detailMessage = message;
		} else {
			detailMessage = throwable.getMessage();
		}

		showErrorDialog(message, detailMessage, throwable);
	}

	/**
	 * @param message The message
	 * @param detailMessage The detail message.
	 * @param throwable The error.
	 */
	protected void showErrorDialog(final String message, final String detailMessage, final Throwable throwable) {
		Shell parent = this.view.getSite().getShell();
		String title = PriceListManagerMessages.get().PriceListEditorError_Title;
		Status status = new Status(IStatus.ERROR, title, null, throwable); // null message here enables details button.
		int allDisplayMask = IStatus.OK | IStatus.INFO | IStatus.WARNING | IStatus.ERROR;
		ErrorDialog errorDialog = new ErrorDialog(parent, title, message, status, allDisplayMask);
		errorDialog.open();
	}

	/**
	 * @param monitor The swt monitor/listener callback.
	 */
	protected void executeAction(final IProgressMonitor monitor) {
		String exportedFileName = exportDataToCsvFile(monitor);
		if (!isExportCancelled) {
			LOG.info(String.format("Exported price list to [%s]", exportedFileName)); //$NON-NLS-1$
		}
		openExportedFile(monitor, exportedFileName);
	}

	/**
	 * Exports data to csv.
	 * @param monitor Progress monitor.
	 * @return The file name.
	 */
	protected String exportDataToCsvFile(final IProgressMonitor monitor) {
		// TODO: Spring bean?
		PriceListExportService priceListExportService = new CsvChunkedPriceListExportService();
		priceListExportService.addListenter(createProgressMonitorListener(monitor));
		priceListExportService.exportPrices(getPriceListGuid());
		return priceListExportService.getFileName();
	}

	/**
	 * Opens the exported csv file in an external editor.
	 * @param monitor Progress monitor.
	 * @param exportedFileName The file name.
	 */
	protected void openExportedFile(final IProgressMonitor monitor, final String exportedFileName) {
		UrlLauncher urlLauncher = ServiceUtil.getUrlLauncherService();
		try {
			if (urlLauncher != null) {
				String filename = new File(exportedFileName).getName();
				display.asyncExec(() -> {
					String fileURL = createDownloadUrl(filename);
					urlLauncher.openURL(fileURL);
				});
			}
		} catch (final Exception e) {
			LOG.error(e.getMessage());
		}

		monitor.subTask(PriceListManagerMessages.get().CSV_Opening_File);
	}

	private String createDownloadUrl(final String filename) {
		StringBuilder url = new StringBuilder();

		url.append(RWT.getServiceManager().getServiceHandlerUrl(DownloadServiceHandler.SERVICE_NAME));
		url.append("&filename=").append(filename);

		return url.toString();
	}

	/**
	 * Creates an adaptor for price list export notifications to the progress monitor impl.
	 * @param monitor The progress monitor.
	 * @return An export even listener.
	 */
	protected PriceListCsvExportNotificationListener createProgressMonitorListener(
			final IProgressMonitor monitor) {
		return new PriceListCsvExportNotificationListener() {
			@Override
			public void notifyStart() {
				monitor.beginTask(subTaskName, IProgressMonitor.UNKNOWN);
			}

			@Override
			public void notifyLoadingData() {
				monitor.subTask(subTaskNotifyLoadingDataMessage);
			}

			@Override
			public void notifyTransformingData() {
				monitor.subTask(subTaskNotifyTransformingDataMessage);
			}

			@Override
			public void notifyExportingData() {
				monitor.subTask(subTaskNotifyExportingDataMessage);
			}

			@Override
			public void notifyError(final Exception cause) {
				isExportFailed = true;
				lastError = cause;
				LOG.error("Prices CSV export failed", cause); //$NON-NLS-1$
			}

			@Override
			public void notifyDone() {
				isExportCancelled = isCancelled();
				monitor.done();
			}

			@Override
			public boolean isCancelled() {
				return monitor.isCanceled();
			}
		};
	}

	/**
	 * Gets the price list guid.
	 * @return String - The guid.
	 */
	protected String getPriceListGuid() {
		Display.getDefault().syncExec(() -> priceListGuid = view.getSelectedItem().getGuid());
		return priceListGuid;
	}

	@Override
	public String getTargetIdentifier() {
		return "csvExportPricelistAction"; //$NON-NLS-1$
	}
}
