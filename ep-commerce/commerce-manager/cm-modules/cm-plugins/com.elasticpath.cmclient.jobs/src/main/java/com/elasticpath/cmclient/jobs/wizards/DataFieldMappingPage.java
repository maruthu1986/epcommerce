/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.jobs.wizards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TableItem;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.ui.framework.CompositeFactory;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.jobs.JobsMessages;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.dataimport.ImportDataType;
import com.elasticpath.domain.dataimport.ImportField;
import com.elasticpath.domain.dataimport.ImportJob;
import com.elasticpath.domain.dataimport.ImportType;
import com.elasticpath.domain.dataimport.impl.AbstractImportTypeImpl;
import com.elasticpath.persistence.CsvFileReader;
import com.elasticpath.service.dataimport.ImportService;

/**
 * The data field mapping wizard page.
 */
@SuppressWarnings({ "PMD.GodClass" })
public class DataFieldMappingPage extends WizardPage {

	private static final int CSV_TABLE_WIDTH = 150;

	private static final int IMAGE_COLUMN_SIZE = 20;

	private static final int DATA_FIELD_COLUMN_SIZE = 160;

	private static final int CSV_COLUMN_COLUMN_SIZE = 160;

	private static final String TABLE = "Table"; //$NON-NLS-1$

	private static final String CSV_TABLE = "CSV"; //$NON-NLS-1$

	private final ImportService importService;

	private final CsvFileReader csvFileReader;

	private final ImportJob importJob;

	private final MappedImportData mappedImportData;

	private IEpTableViewer tableViewer;

	private IEpTableViewer csvColumnsListField;

	private Button mapButton;

	private Button unMapButton;

	private boolean firstViewOfPage;

	/**
	 * Constructor.
	 * 
	 * @param pageName the page name
	 * @param title the page title
	 * @param description the page description
	 * @param titleImage the image to display in the page
	 * @param importJob the import job to add/edit
	 */
	protected DataFieldMappingPage(final String pageName, final String title, final String description, final ImageDescriptor titleImage,
			final ImportJob importJob) {
		super(pageName, title, titleImage);
		this.setDescription(description);
		this.importJob = importJob;
		this.mappedImportData = new MappedImportData();
		this.importService = ServiceLocator.getService(ContextIdNames.IMPORT_SERVICE);
		this.csvFileReader = ServiceLocator.getService(ContextIdNames.CSV_FILE_READER);
		firstViewOfPage = true;
	}

	/**
	 * Create the wizard's page composite.
	 * 
	 * @param parent the page's parent
	 */
	public void createControl(final Composite parent) {
		final IEpLayoutComposite dialogComposite = CompositeFactory.createGridLayoutComposite(parent, 3, false);
		final IEpLayoutData labelData = dialogComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.BEGINNING);
		dialogComposite.addLabelBoldRequired(JobsMessages.get().ImportJobWizard_DataFields, EpState.EDITABLE, labelData);
		dialogComposite.addEmptyComponent(null);
		dialogComposite.addLabelBoldRequired(JobsMessages.get().ImportJobWizard_CsvColumns, EpState.EDITABLE, labelData);

		final IEpLayoutData dataFieldData = dialogComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true);
		tableViewer = dialogComposite.addTableViewer(false, EpState.EDITABLE, dataFieldData, TABLE);

		// add Property table columns
		tableViewer.addTableColumn("", IMAGE_COLUMN_SIZE); //$NON-NLS-1$
		tableViewer.addTableColumn(JobsMessages.get().ImportJobWizard_DataFieldsColumnDataField, DATA_FIELD_COLUMN_SIZE);
		tableViewer.addTableColumn(JobsMessages.get().ImportJobWizard_DataFieldsColumnCsvColumn, CSV_COLUMN_COLUMN_SIZE);

		tableViewer.setContentProvider(new DataFieldMappingContentProvider());
		tableViewer.setLabelProvider(new DataFieldMappingLabelProvider());

		final IEpLayoutData buttonMappingLayoutData = dialogComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.CENTER);
		IEpLayoutComposite mappingButtonsComposite = dialogComposite.addGridLayoutComposite(1, true, buttonMappingLayoutData);

		csvColumnsListField = dialogComposite.addTableViewer(false, EpState.EDITABLE, dataFieldData, CSV_TABLE);
		csvColumnsListField.addTableColumn("", CSV_TABLE_WIDTH); //$NON-NLS-1$
		csvColumnsListField.setContentProvider(new ArrayContentProvider());
		csvColumnsListField.setLabelProvider(new CsvColumnsLabelProvider());
		csvColumnsListField.getSwtTableViewer().addSelectionChangedListener(new CsvColumnsListFieldSelectionListener());

		tableViewer.getSwtTable().addSelectionListener(new DataFieldTableSelectionListener());

		final IEpLayoutData buttonLayoutData = dialogComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.BEGINNING);

		mapButton = mappingButtonsComposite.addPushButton(JobsMessages.get().ImportJobWizard_MapButton, EpState.EDITABLE, buttonLayoutData);
		mapButton.setToolTipText(JobsMessages.get().ImportJobWizard_MapButtonHelp);
		mapButton.setEnabled(false);
		mapButton.addSelectionListener(new MapButtonSelectionListener());

		unMapButton = mappingButtonsComposite.addPushButton(JobsMessages.get().ImportJobWizard_UnmapButton, EpState.EDITABLE, buttonLayoutData);
		unMapButton.setToolTipText(JobsMessages.get().ImportJobWizard_MapButtonHelp);
		unMapButton.setEnabled(false);
		unMapButton.addSelectionListener(new UnmapButtonSelectionListener());

		Button clearMapButton =
				mappingButtonsComposite.addPushButton(JobsMessages.get().ImportJobWizard_ClearButton, EpState.EDITABLE, buttonLayoutData);
		clearMapButton.setToolTipText(JobsMessages.get().ImportJobWizard_ClearButtonHelp);
		clearMapButton.addSelectionListener(new ClearButtonSelectionListener());

		this.setControl(dialogComposite.getSwtComposite());
	}

	@Override
	public void setVisible(final boolean visible) {
		if (visible && mappedImportData.isDirty()) {
			updateDataFieldsTable();
			updateCsvColumnsListViewer();
			Control wizardPageAreaControl = null;
			Control wizardPageWithProgressAreaControl = null;
			Control wizardControl = null;
			wizardPageAreaControl = getControl().getParent();
			if (wizardPageAreaControl != null) {
				wizardPageWithProgressAreaControl = wizardPageAreaControl.getParent();
			}
			if (wizardPageWithProgressAreaControl != null) {
				wizardControl = wizardPageWithProgressAreaControl.getParent();
			}
			if (wizardControl != null) {
				wizardControl.pack();
			}

			if (firstViewOfPage) {
				initDataFieldsTable();
				firstViewOfPage = false;
			} else {
				importJob.getMappings().clear();
			}

		}
		mappedImportData.update();
		super.setVisible(visible);
	}

	private void initDataFieldsTable() {
		if (importJob.isPersisted()) {
			TableItem[] tableItems = tableViewer.getSwtTable().getItems();
			Map<String, TableItem> tableItemsMap = new HashMap<String, TableItem>();
			for (TableItem tableItem : tableItems) {
				tableItemsMap.put(tableItem.getText(1), tableItem);
			}
			for (Map.Entry<String, Integer> mapping : importJob.getMappings().entrySet()) {
				TableItem tableItem = tableItemsMap.get(mapping.getKey());
				if (tableItem != null) {
					int columnIndex = mapping.getValue() - 1;
					if (csvColumnsListField.getSwtTable().getItemCount() > columnIndex) {
						tableItem.setText(2, csvColumnsListField.getSwtTable().getItem(columnIndex).getText());
					}
				}
			}
		}
	}

	private void updateDataFieldsTable() {
		ImportDataType importDataType = importService.findImportDataType(importJob.getImportDataTypeName());
		importDataType = importService.initImportDataTypeLocalesAndCurrencies(importDataType, importJob);
		java.util.List<ImportField> importFields = new ArrayList<ImportField>();
		if (importJob.getImportType().equals(AbstractImportTypeImpl.DELETE_TYPE)) {
			importFields = importDataType.getRequiredImportFields();
		} else {
			importFields = importDataType.getRequiredImportFields();
			importFields.addAll(importDataType.getOptionalImportFields());
		}
		tableViewer.setInput(importFields);
	}

	private void updateCsvColumnsListViewer() {
		csvFileReader.open(importJob.getCsvFileName(), importJob.getCsvFileColDelimeter(), importJob.getCsvFileTextQualifier());
		String[] titleLine = csvFileReader.readNext();
		csvColumnsListField.setInput(Arrays.asList(titleLine).subList(1, titleLine.length));
		csvFileReader.close();
	}

	/**
	 * CSV columns list field selection listener enables the map button when an item is selected.
	 */
	private class CsvColumnsListFieldSelectionListener implements ISelectionChangedListener {
		public void selectionChanged(final SelectionChangedEvent event) {
			mapButton.setEnabled((csvColumnsListField.getSwtTable().getSelection().length > 0)
					&& (tableViewer.getSwtTable().getSelection().length > 0));
		}
	}

	/**
	 * Data field table selection listener enables the map button when an item is selected, enables the unmap button when an item is selected and has
	 * an association, and disables the unmap button when item selected with no association.
	 */
	private class DataFieldTableSelectionListener extends SelectionAdapter {
		@Override
		public void widgetSelected(final SelectionEvent event) {
			TableItem[] selectedDataFieldItems = tableViewer.getSwtTable().getSelection();
			mapButton.setEnabled((csvColumnsListField.getSwtTable().getSelection().length > 0) && (selectedDataFieldItems.length > 0));
			if (selectedDataFieldItems[0].getText(2).length() > 0) {
				unMapButton.setEnabled(true);
			} else {
				unMapButton.setEnabled(false);
			}
		}
	}

	/**
	 * Map button selection listener associates the selected CSV column to the selected data field and enables the unmap button.
	 */
	private class MapButtonSelectionListener extends SelectionAdapter {
		@Override
		public void widgetSelected(final SelectionEvent event) {
			String csvColumn = csvColumnsListField.getSwtTable().getSelection()[0].getText();
			TableItem selectedDataFieldItem = tableViewer.getSwtTable().getSelection()[0];
			selectedDataFieldItem.setText(2, csvColumn);
			String dataFieldName = selectedDataFieldItem.getText(1);
			int columnNumber = csvColumnsListField.getSwtTable().getSelectionIndex();
			importJob.getMappings().put(dataFieldName, Integer.valueOf(columnNumber + 1));
			unMapButton.setEnabled(true);
			getWizard().getContainer().updateButtons();
		}
	}

	/**
	 * Unmap button selection listener removes the CSV column association from the selected data field and disables the unmap button.
	 */
	private class UnmapButtonSelectionListener extends SelectionAdapter {
		@Override
		public void widgetSelected(final SelectionEvent event) {
			TableItem selectedDataFieldItem = tableViewer.getSwtTable().getSelection()[0];
			selectedDataFieldItem.setText(2, ""); //$NON-NLS-1$
			String dataFieldName = selectedDataFieldItem.getText(1);
			importJob.getMappings().remove(dataFieldName);
			unMapButton.setEnabled(false);
			getWizard().getContainer().updateButtons();
		}
	}

	/**
	 * Clear button selection listener removes CSV column associations from all data fields.
	 */
	private class ClearButtonSelectionListener extends SelectionAdapter {
		@Override
		public void widgetSelected(final SelectionEvent event) {
			TableItem[] tableItems = tableViewer.getSwtTable().getItems();
			for (int i = 0; i < tableItems.length; i++) {
				tableItems[i].setText(2, ""); //$NON-NLS-1$
			}
			importJob.getMappings().clear();
			getWizard().getContainer().updateButtons();
		}
	}

	/**
	 * Data field mapping content provider.
	 */
	protected static final class DataFieldMappingContentProvider implements IStructuredContentProvider {

		@Override
		public Object[] getElements(final Object inputElement) {
			return ((Collection< ? >) inputElement).toArray();
		}

		@Override
		public void dispose() {
			// do nothing
		}

		@Override
		public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
			// do nothing
		}
	}

	/**
	 * Data field mapping label provider.
	 */
	protected class DataFieldMappingLabelProvider extends LabelProvider implements ITableLabelProvider {
		@Override
		public Image getColumnImage(final Object element, final int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(final Object element, final int columnIndex) {
			final ImportField importField = (ImportField) element;
			switch (columnIndex) {
			case 0:
				if (importJob.getImportType().equals(AbstractImportTypeImpl.DELETE_TYPE)
						|| importJob.getImportType().equals(AbstractImportTypeImpl.UPDATE_TYPE)) {
					if (importField.isRequiredPrimaryField()) {
						return "*"; //$NON-NLS-1$;
					}
					return ""; //$NON-NLS-1$;
				}
				if (importField.isRequired()) {
					return "*"; //$NON-NLS-1$;
				}
				return ""; //$NON-NLS-1$;
			case 1:
				return importField.getName();
			default:
				return ""; //$NON-NLS-1$
			}
		}
	}


	/**
	 * Label provider for table with only column.
	 */
	private class CsvColumnsLabelProvider extends LabelProvider implements ITableLabelProvider {

		@Override
		public Image getColumnImage(final Object element, final int columnIndex) {
			return null;
		}
		@Override
		public String getColumnText(final Object element, final int columnIndex) {
			return (String) element;
		}

	}

	@Override
	public void setErrorMessage(final String newMessage) {
		// Do nothing
	}

	@Override
	public boolean isPageComplete() {
		boolean pageComplete = true;
		TableItem[] tableItems = tableViewer.getSwtTable().getItems();
		if (tableItems.length == 0) {
			pageComplete = false;
		} else {
			for (TableItem tableItem : tableItems) {
				String required = tableItem.getText(0);
				String csvColumn = tableItem.getText(2);
				if ((required.length() > 0) && (csvColumn.length() == 0)) {
					pageComplete = false;
					break;
				}
			}
		}
		return pageComplete;
	}

	/**
	 * Holds the import data that was used for the current data field mapping. Used to determine if the data field mapping should be updated when
	 * page is current page displayed.
	 */
	class MappedImportData {
		private String importDataTypeName;

		private ImportType importType;

		private String csvFileName;

		private char csvFileColDelimiter;

		private char gsvFileTextQualifier;

		/**
		 * Constructor used initialize with default values.
		 */
		MappedImportData() {
			this.importDataTypeName = ""; //$NON-NLS-1$
			this.importType = AbstractImportTypeImpl.INSERT_UPDATE_TYPE;
			this.csvFileName = ""; //$NON-NLS-1$
			this.csvFileColDelimiter = Character.MIN_VALUE;
			this.gsvFileTextQualifier = Character.MIN_VALUE;
		}

		/**
		 * Update internal data with current importJob data.
		 */
		void update() {
			this.importDataTypeName = importJob.getImportDataTypeName();
			this.importType = importJob.getImportType();
			this.csvFileName = importJob.getCsvFileName();
			this.csvFileColDelimiter = importJob.getCsvFileColDelimeter();
			this.gsvFileTextQualifier = importJob.getCsvFileTextQualifier();
		}

		/**
		 * Returns <code>true</code> if mapped import data is different than current importJob data, otherwise returns <code>false</code>.
		 * 
		 * @return <code>true</code> if mapped import data is different than current importJob data, otherwise returns <code>false</code>.
		 */
		boolean isDirty() {
			boolean mappingChanged = false;
			if (!importDataTypeName.equals(importJob.getImportDataTypeName()) || !importType.equals(importJob.getImportType())
					|| !csvFileName.equals(importJob.getCsvFileName()) || (csvFileColDelimiter != importJob.getCsvFileColDelimeter())
					|| (gsvFileTextQualifier != importJob.getCsvFileTextQualifier())) {
				mappingChanged = true;
			}
			return mappingChanged;
		}
	}

}
