/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.jobs.wizards;

import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.layout.GridData;

import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.jobs.JobsMessages;
import com.elasticpath.domain.dataimport.ImportDataType;
import com.elasticpath.domain.dataimport.ImportJob;
import com.elasticpath.domain.store.Warehouse;

/**
 * The Configure Warehouse Import Job wizard page.
 */
public class ConfigureWarehouseImportJobPage extends AbstractConfigureImportJobPageWithDataTypeAndImportType {

	private CCombo warehouseCombo;
	
	/**
	 * The constructor.
	 * 
	 * @param importJob import job to configure.
	 */
	protected ConfigureWarehouseImportJobPage(final ImportJob importJob) {
		super("ConfigureImportJobPage", JobsMessages.get().ImportJobWizard_ConfigureImportJob, //$NON-NLS-1$
				JobsMessages.get().ImportJobWizard_ConfigureImportJobDescription, importJob);
	}

	@Override
	protected List<ImportDataType> getImportDataTypes() {
		return getImportService().getWarehouseImportDataTypes();
	}
	
	@Override
	protected void createEpPageContent(final IEpLayoutComposite pageComposite) {
		final IEpLayoutData labelData = pageComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL);
		final IEpLayoutData fieldData = pageComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.FILL, true, false, 3, 1);
		pageComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		pageComposite.addLabelBoldRequired(JobsMessages.get().JobDetailsPage_Warehouse, EpState.EDITABLE, labelData);
		warehouseCombo = pageComposite.addComboBox(EpState.EDITABLE, fieldData);

		super.createEpPageContent(pageComposite);
	}

	@Override
    protected void populateControls() {
		if (getImportJob().isPersisted()) {
			warehouseCombo.setEnabled(false);
		}

		warehouseCombo.add(JobsMessages.get().ImportJobWizard_SelectWarehouse, 0);

		final Warehouse selectedWarehouse = getImportJob().getWarehouse();
		
		List<Warehouse> warehouses = getImportService().listWarehouses();
		for (Warehouse warehouse : warehouses) {
			if (AuthorizationService.getInstance().isAuthorizedForWarehouse(warehouse)) {
				final String warehouseName = warehouse.getName();
				warehouseCombo.add(warehouseName);
				warehouseCombo.setData(warehouseName, warehouse);
				if (selectedWarehouse != null && warehouseName.equals(selectedWarehouse.getName())) {
					warehouseCombo.select(warehouseCombo.getItemCount() - 1);
				}
			}
		}

		if (warehouseCombo.getSelectionIndex() == -1) {
			warehouseCombo.select(0);
		}
		
		super.populateControls();
	}

	@Override
    protected void bindControls() {
		DataBindingContext context = getDataBindingContext();
		EpControlBindingProvider binder = EpControlBindingProvider.getInstance();

		final ObservableUpdateValueStrategy warehouseUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				if (warehouseCombo.getSelectionIndex() != 0) {
					String item = warehouseCombo.getItem(warehouseCombo.getSelectionIndex());				
					Warehouse warehouse = (Warehouse) warehouseCombo.getData(item);
					getImportJob().setWarehouse(warehouse);
				}
				return Status.OK_STATUS;
			}
		};
		binder.bind(context, warehouseCombo, EpValidatorFactory.REQUIRED_COMBO_FIRST_ELEMENT_NOT_VALID, null, warehouseUpdateStrategy, true);

		super.bindControls();
	}

	@Override
	public boolean canFlipToNextPage() {
		return warehouseCombo.getSelectionIndex() > 0 && super.canFlipToNextPage();
	}

}
