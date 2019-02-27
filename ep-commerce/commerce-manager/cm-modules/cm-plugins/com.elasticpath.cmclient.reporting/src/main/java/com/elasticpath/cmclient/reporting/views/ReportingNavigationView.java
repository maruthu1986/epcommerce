/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.reporting.views;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.reporting.common.SavedReportParameters;
import org.apache.log4j.Logger;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.part.ViewPart;

import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.reporting.IReport;
import com.elasticpath.cmclient.reporting.ReportType;
import com.elasticpath.cmclient.reporting.ReportTypeManager;
import com.elasticpath.cmclient.reporting.ReportingImageRegistry;
import com.elasticpath.cmclient.reporting.ReportingMessages;
import com.elasticpath.cmclient.reporting.service.CmReportService;

/**getReportTypes()
 * The view that allows a user to select a specific report from a list.
 */
public class ReportingNavigationView extends ViewPart implements SelectionListener, IRunnableWithProgress {

	private static final Logger LOG = Logger.getLogger(ReportingNavigationView.class);

	/** View ID. */
	public static final String VIEW_ID = ReportingNavigationView.class.getName();

	private IEpLayoutComposite buttonComposite;

	private IEpLayoutComposite parametersGroup;

	//buttons
	private Button runReportButton;

	private CCombo reportTypes;
	private IEpLayoutComposite mainComposite;

	private Map<IReport, DataBindingContext> bindingContextMap;

	private DataBindingContext reportBindingContext;

	private IReport currentReport;

	private IReport previousReport;

	private URL currentReportLocation;

	private Label separator;

	private CmReportService birt;

	private Map<String, Object> reportParams;

	private String currentReportType;
	private IEpLayoutComposite parentEpComposite;

	@Override
	public void createPartControl(final Composite parent) {
		bindingContextMap = new HashMap<>();

		if (birt != null) {
			birt.closeTask();
		}

		parentEpComposite = CompositeFactory.createGridLayoutComposite(parent, 1, false);
		mainComposite = parentEpComposite.addScrolledGridLayoutComposite(1, true);
		mainComposite.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB, TableWrapData.TOP));

		showReportTypeGroup(mainComposite);
		showButtons(parentEpComposite);
	}

	private void showReportTypeGroup(final IEpLayoutComposite parent) {

		final IEpLayoutComposite reportTypeGroup =
			parent.addGroup(ReportingMessages.get().reportTypeRequired, 1, false,
					parent.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true, false));

		reportTypeGroup.addLabelBoldRequired(ReportingMessages.get().reportType, EpState.EDITABLE,
				reportTypeGroup.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.CENTER));

		final CCombo reportCombo = reportTypeGroup.addComboBox(EpState.EDITABLE,
				reportTypeGroup.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.CENTER, true, true));

		int comboIndex = 0;
		reportCombo.add(ReportingMessages.get().selectReportType, comboIndex);

		// Add all the reports to the combo box
		final List<ReportType> allReportTypes = ReportTypeManager.getInstance().getReportTypes();
		for (ReportType reportType : allReportTypes) {
			if (!reportType.getReport().isAuthorized()) {
				continue;
			}
			comboIndex++;
			reportCombo.add(reportType.getName(), comboIndex);
		}

		reportCombo.setVisibleItemCount(reportCombo.getItemCount());

		reportCombo.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(final SelectionEvent event) {
				widgetSelected(event);
			}

			public void widgetSelected(final SelectionEvent event) {

				disposeNonNullComponents();

				int selectionIndex = reportCombo.getSelectionIndex();
				if (selectionIndex == 0) {
					disableButtons();
				} else {
					previousReport = currentReport;
					String selectedText = reportCombo.getText();
					ReportType selectedReportType = ReportTypeManager.getInstance().findReportTypeByName(selectedText);
					currentReport = selectedReportType.getReport();

					currentReportLocation = selectedReportType.getFileLocation();
					showParameters(parent, currentReport);
					separator = parent.addHorizontalSeparator(
						parent.createLayoutData(
						IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true, false));

					Display.getDefault().syncExec(() -> currentReport.bindControls(
							EpControlBindingProvider.getInstance(), bindingContextMap.get(currentReport))
					);
					parent.getSwtComposite().pack(true);
					parent.getSwtComposite().getParent().layout(true);
				}
			}
		});

		reportCombo.select(0);


	}

	private void disposeNonNullComponents() {
		if (reportBindingContext != null) {
			bindingContextMap.remove(currentReport);
			reportBindingContext.dispose();
			reportBindingContext = null;
		}
		if (parametersGroup != null) {
			parametersGroup.getSwtComposite().dispose();
			parametersGroup = null;
		}
		if (separator != null) {
			separator.dispose();
			separator = null;
		}
	}

	/**
	 * Create and show the Parameters pane for a given report.
	 *
	 * @param report the report for which to show the parameters screen
	 */
	private void showParameters(final IEpLayoutComposite parent, final IReport report) {
		parametersGroup = parent.addGroup(ReportingMessages.get().parameters, 1, true,
					parent.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING, true, false));

		report.createControl(parent.getFormToolkit(), parametersGroup.getSwtComposite(), getSite());
		reportBindingContext = new DataBindingContext();
		bindingContextMap.put(report, reportBindingContext);

	}

	private void showButtons(final IEpLayoutComposite parent) {
		final int numColumns = 2;

		buttonComposite = parent.addGridLayoutComposite(numColumns, false,
				parent.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.CENTER, true, false));
		buttonComposite.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

		IEpLayoutComposite reportFormatGrid = buttonComposite.addGridLayoutComposite(1,
				false, null);

		reportFormatGrid.addLabelBold(ReportingMessages.get().reportFormat,
				reportFormatGrid.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.CENTER, true, false));

		IEpLayoutData reportTypesLayoutData = reportFormatGrid.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.CENTER, true, true);

		reportTypes = reportFormatGrid.addComboBox(EpState.EDITABLE,
				reportTypesLayoutData);

		IEpLayoutComposite reportButtonGrid = buttonComposite.addGridLayoutComposite(1, false,
				buttonComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.END, true, true));

		runReportButton = reportButtonGrid.addPushButton(ReportingMessages.get().runReport,
				ReportingImageRegistry.getImage(ReportingImageRegistry.RUN_REPORT),
				EpState.EDITABLE,
				reportButtonGrid.createLayoutData(IEpLayoutData.END, IEpLayoutData.CENTER, true, true));

		reportTypes.add(ReportingMessages.get().HTMLFormat, 0);
		reportTypes.add(ReportingMessages.get().CSVFormat);
		reportTypes.add(ReportingMessages.get().PDFFormat);
		reportTypes.add(ReportingMessages.get().ExcelFormat);

		reportTypes.select(0);
		addSelectionListeners();
		disableButtons();
	}

	/**
	 * Enables the buttons.
	 *
	 */
	public void enableButtons() {
		runReportButton.setEnabled(true);
	}

	/**
	 * Disables the buttons.
	 */
	public void disableButtons() {
		runReportButton.setEnabled(false);

	}

	private void addSelectionListeners() {
		runReportButton.addSelectionListener(this);

	}

	@Override
	public void setFocus() {
		// nothing

	}

	/**
	 * Not Used.
	 *
	 * @param event the selection event.
	 */
	public void widgetDefaultSelected(final SelectionEvent event) {
		// nothing

	}

	private void startEngineTask(final Map<String, Object> params) {
		if (currentReportType.equals(ReportingMessages.get().HTMLFormat) || currentReportType.equals(ReportingMessages.get().PDFFormat)) {
			if (birt == null) {
				birt = ServiceLocator.getService("cmReportService"); //$NON-NLS-1$
				birt.initializeTask(false, currentReportLocation, currentReport.getClass().getClassLoader(), params);
			} else if (previousReport != null && !previousReport.getReportTitle().equals(currentReport.getReportTitle())) { //NOPMD
				previousReport = currentReport;
				birt.initializeTask(false, currentReportLocation, currentReport.getClass().getClassLoader(), params);
			} else {
				birt.initializeTask(false, currentReportLocation, currentReport.getClass().getClassLoader(), params);
			}
		}
	}

	/**
	 * Invoked when combo box and buttons are selected.
	 *
	 * @param event the selection event
	 */
	public void widgetSelected(final SelectionEvent event) {

		bindingContextMap.get(currentReport).updateModels();
		reportParams = currentReport.getParameters();
		SavedReportParameters.getInstance().saveParameters(reportParams);
		currentReportType = reportTypes.getItem(reportTypes.getSelectionIndex());

		try {
			ProgressMonitorDialog dialog = new ProgressMonitorDialog(getSite().getShell());
			dialog.setCancelable(true);
			dialog.run(true, true, this);
		} catch (InvocationTargetException exception) {
			LOG.error("reporting progress monitor failed", exception); //$NON-NLS-1$
		} catch (InterruptedException exception) {
			LOG.error("Cancelled", exception); //$NON-NLS-1$
		}
	}

	private void cancelMonitor(final IProgressMonitor monitor) {
		if (monitor.isCanceled()) {
			return;
		}
	}

	/**
	 * Runs reporting tasks with progress monitor.
	 * @param monitor the progress monitor which is of type IProgressMonitor.UNKNOWN
	 * @throws InvocationTargetException exception
	 * @throws InterruptedException exception
	 */
	public void run(final IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {
		monitor.beginTask(ReportingMessages.get().monitorRunning, IProgressMonitor.UNKNOWN);
		startEngineTask(reportParams);
		cancelMonitor(monitor);

		if (currentReportType.equals(ReportingMessages.get().HTMLFormat)) {
			//creates a UI thread that handles creating a view, otherwise it throws an NPE because
			//progress monitor runs on a non UI thread, and non UI thread cannot create UI change
			Display.getDefault().syncExec(() -> {
                try {
                    IWorkbenchPage page = getSite().getPage();
                    ReportingView view = (ReportingView) page.showView(ReportingView.REPORTVIEWID, null,
                            IWorkbenchPage.VIEW_ACTIVATE);
                    view.setReportingViewTitle(currentReport.getReportTitle());
                    cancelMonitor(monitor);
                    birt.viewHTMLReport(view.getBrowser());
                    monitor.done();

                } catch (PartInitException exception) {
                    LOG.error("Failed to create reporting view", exception); //$NON-NLS-1$
                }
            });
			return;

		}
		if (currentReportType.equals(ReportingMessages.get().PDFFormat)) {
			cancelMonitor(monitor);
			birt.makePdf();
			monitor.done();
			return;
		}
		if (currentReportType.equals(ReportingMessages.get().ExcelFormat) || currentReportType.equals(ReportingMessages.get().CSVFormat)) {

			CmReportService birtExcelOrCsv = ServiceLocator.getService("cmReportService"); //$NON-NLS-1$
			birtExcelOrCsv.initializeTask(true, currentReportLocation, currentReport.getClass().getClassLoader(), reportParams);
			//if cancel pressed, at least initial the task so it will be faster next time user runs the report
			cancelMonitor(monitor);
			if (currentReportType.equals(ReportingMessages.get().ExcelFormat)) {
				birtExcelOrCsv.makeExcel();

			} else {
				birtExcelOrCsv.makeCSV();

			}
			monitor.done();
			return;
		}

	}



}
