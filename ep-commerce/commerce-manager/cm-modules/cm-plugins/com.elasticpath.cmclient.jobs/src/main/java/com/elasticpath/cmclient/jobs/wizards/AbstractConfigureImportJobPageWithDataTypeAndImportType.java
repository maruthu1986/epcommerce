/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.jobs.wizards;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.jobs.JobsMessages;
import com.elasticpath.domain.dataimport.ImportDataType;
import com.elasticpath.domain.dataimport.ImportJob;
import com.elasticpath.domain.dataimport.ImportType;

/**
 * The Configure Import Job wizard page with datatype and import type controls.
 */
public abstract class AbstractConfigureImportJobPageWithDataTypeAndImportType extends AbstractConfigureImportJobPage {

	private CCombo dataTypeCombo;

	private CCombo importTypeCombo;

	/**
	 * @return list of data types available for this import job.
	 */
	@Override
	protected abstract List<ImportDataType> getImportDataTypes();
	
	/**
	 * Constructor.
	 *  @param pageName the page name
	 * @param title the page title
	 * @param description the page description
	 * @param importJob the import job to add/edit
	 */
	protected AbstractConfigureImportJobPageWithDataTypeAndImportType(
			final String pageName, final String title,
			final String description,
			final ImportJob importJob) {
		super(pageName, title, description, importJob);
	}
		
	/**
	 * @param pageComposite a parent composite
	 */
	@Override
	protected void createTopComponents(final IEpLayoutComposite pageComposite) {
		super.createTopComponents(pageComposite);
		
		final IEpLayoutData labelData = pageComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL);
		final IEpLayoutData fieldData = pageComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false, 3, 1);

		pageComposite.addLabelBoldRequired(JobsMessages.get().JobDetailsPage_DataType, EpState.EDITABLE, labelData);
		dataTypeCombo = pageComposite.addComboBox(EpState.EDITABLE, fieldData);

		pageComposite.addLabelBoldRequired(JobsMessages.get().JobDetailsPage_ImportType, EpState.EDITABLE, labelData);
		importTypeCombo = pageComposite.addComboBox(EpState.EDITABLE, fieldData);

		dataTypeCombo.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(final SelectionEvent event) {
				//nothing to do
				
			}

			public void widgetSelected(final SelectionEvent event) {
				//populate import type combo box
				int index = dataTypeCombo.getSelectionIndex();
				dataTypeComboChanged(index);
			}
			
		});
	}
	
	private void dataTypeComboChanged(final int index) {
		if (index >= 0 && CollectionUtils.isNotEmpty(getImportDataTypes())) {
			ImportDataType importDataType = getImportDataTypes().get(index);
			populateImportTypeCombo(importDataType.getSupportedImportTypes());
		}
	}
	
	
	@Override
	protected void populateControls() {
		super.populateControls();
		if (getImportJob().isPersisted()) {
			dataTypeCombo.setEnabled(false);
			importTypeCombo.setEnabled(false);
		}

		fillDataTypeCombo(getImportDataTypes(), false);
		
		//The import type combo is not populated until the data type is selected.
	}

	private void populateImportTypeCombo(final List<ImportType> importTypes) {
		importTypeCombo.removeAll();
		String selectedImportTypeName = JobsMessages.get().getMessage(getImportJob().getImportType().getNameMessageKey());
		for (ImportType importType : importTypes) {

			String importTypeName = JobsMessages.get().getMessage(importType.getNameMessageKey());
			importTypeCombo.setData(importTypeName, importType);
			importTypeCombo.add(importTypeName);
			if (importTypeName.equals(selectedImportTypeName)) {
				importTypeCombo.select(importTypeCombo.getItemCount() - 1);
				
			}
		}
	}
	
	/**
	 * Should be called from derived pages.
	 * 
	 * @param dataTypes types of data to be imported
	 * @param refreshButtons true if need to refresh buttons.
	 */
	protected void fillDataTypeCombo(final List<ImportDataType> dataTypes, final boolean refreshButtons) {
		String selectedImportDataTypeName = getImportJob().getImportDataTypeName();
		int selectedItem = 0;
		// remove all elements if we have such
		dataTypeCombo.removeAll();
		
		for (ImportDataType importDataType : dataTypes) {
			String importDataTypeName = importDataType.getName();
			dataTypeCombo.add(JobsMessages.get().getMessage(importDataType.getNameMessageKey(), importDataType.getTypeName()));
			if (importDataTypeName.equals(selectedImportDataTypeName)) {
				selectedItem = dataTypeCombo.getItemCount() - 1;
			}
		}
		
		if (getImportJob().isPersisted()) {
			dataTypeCombo.select(selectedItem);
			importTypeCombo.select(0);
			dataTypeComboChanged(0);
			dataTypeCombo.setEnabled(false);
		} else if (dataTypeCombo.getItemCount() == 0) {
			dataTypeCombo.setEnabled(false);
		} else {
			dataTypeCombo.select(selectedItem);
			getImportJob().setImportDataTypeName(dataTypes.get(selectedItem).getName());
			dataTypeComboChanged(selectedItem);
			dataTypeCombo.setData(dataTypes);
			dataTypeCombo.setEnabled(true);
		}
		
		if (refreshButtons) {
			getWizard().getContainer().updateButtons();
		}
	}

	
	@Override
	protected void bindControls() {
		super.bindControls();
		
		DataBindingContext context = getDataBindingContext();
		EpControlBindingProvider binder = EpControlBindingProvider.getInstance();

		final ObservableUpdateValueStrategy dataTypeUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				List<ImportDataType> importDataTypes = (List<ImportDataType>) dataTypeCombo.getData();
				if (importDataTypes != null) {
					ImportDataType importDataType = importDataTypes.get(dataTypeCombo.getSelectionIndex());
					getImportJob().setImportDataTypeName(importDataType.getName());
				}
				return Status.OK_STATUS;
			}
		};
		binder.bind(context, dataTypeCombo, null, null, dataTypeUpdateStrategy, true);

		final ObservableUpdateValueStrategy importTypeUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				int selectionIndex = importTypeCombo.getSelectionIndex();
				if (selectionIndex >= 0) {
					String item = importTypeCombo.getItem(selectionIndex);
					ImportType importType = (ImportType) importTypeCombo.getData(item);
					getImportJob().setImportType(importType);
				}

				return Status.OK_STATUS;
			}
		};
		binder.bind(context, importTypeCombo, null, null, importTypeUpdateStrategy, true);
		
	}

	@Override
	public boolean canFlipToNextPage() {
		return super.canFlipToNextPage()
				&& dataTypeCombo.getSelectionIndex() > -1
				&& importTypeCombo.getSelectionIndex() > -1;
	}
}
