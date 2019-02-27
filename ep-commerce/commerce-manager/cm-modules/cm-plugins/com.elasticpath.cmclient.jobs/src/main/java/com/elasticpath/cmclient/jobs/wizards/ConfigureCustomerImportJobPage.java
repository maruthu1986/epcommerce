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

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.jobs.JobsMessages;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.dataimport.ImportDataType;
import com.elasticpath.domain.dataimport.ImportJob;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.store.StoreService;

/**
 * The Configure Import Job wizard page.
 */
public class ConfigureCustomerImportJobPage extends AbstractConfigureImportJobPageWithDataTypeAndImportType {

	private CCombo storeCombo;

	/**
	 * Constructor.
	 * 
	 * @param importJob the import job to add/edit
	 */
	protected ConfigureCustomerImportJobPage(final ImportJob importJob) {
		super("ConfigureImportJobPage", JobsMessages.get().ImportJobWizard_ConfigureImportJob, //$NON-NLS-1$
				JobsMessages.get().ImportJobWizard_ConfigureImportJobDescription, importJob);
	}

	@Override
	protected void createEpPageContent(final IEpLayoutComposite pageComposite) {
		final IEpLayoutData labelData = pageComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL);
		final IEpLayoutData fieldData = pageComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.FILL, true, false, 3, 1);
		pageComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		pageComposite.addLabelBoldRequired(JobsMessages.get().JobDetailsPage_Store, EpState.EDITABLE, labelData);
		storeCombo = pageComposite.addComboBox(EpState.EDITABLE, fieldData);

		super.createEpPageContent(pageComposite);
	}

	@Override
	protected void populateControls() {
		super.populateControls();

		if (getImportJob().isPersisted()) {
			storeCombo.setEnabled(false);
		}
		StoreService storeService = ServiceLocator.getService(ContextIdNames.STORE_SERVICE);
		List<Store> stores = storeService.findAllStores();
		AuthorizationService.getInstance().filterAuthorizedStores(stores);

		final Store selectedStore = getImportJob().getStore();

		storeCombo.add(JobsMessages.get().ImportJobWizard_SelectStore, 0);

		for (Store store : stores) {
			if (AuthorizationService.getInstance().isAuthorizedForStore(store)) {
				final String storeName = store.getName();
				storeCombo.setData(storeName, store);
				storeCombo.add(storeName);
				if (selectedStore != null && storeName.equals(selectedStore.getName())) {
					storeCombo.select(storeCombo.getItemCount() - 1);
				}
			}
		}

		if (storeCombo.getSelectionIndex() == -1) {
			storeCombo.select(0);
		}
	}

	@Override
	protected void bindControls() {
		super.bindControls();

		DataBindingContext context = getDataBindingContext();
		EpControlBindingProvider binder = EpControlBindingProvider.getInstance();

		final ObservableUpdateValueStrategy storeUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				if (storeCombo.getSelectionIndex() != 0) {
					String item = storeCombo.getItem(storeCombo.getSelectionIndex());
					Store store = (Store) storeCombo.getData(item);
					getImportJob().setStore(store);
				}
				return Status.OK_STATUS;
			}
		};
		binder.bind(context, storeCombo, EpValidatorFactory.REQUIRED_COMBO_FIRST_ELEMENT_NOT_VALID, null, storeUpdateStrategy, true);
	}

	@Override
	protected List<ImportDataType> getImportDataTypes() {
		return getImportService().getCustomerImportDataTypes();
	}

	@Override
	public boolean canFlipToNextPage() {
		return storeCombo.getSelectionIndex() > 0 && super.canFlipToNextPage();
	}
}
