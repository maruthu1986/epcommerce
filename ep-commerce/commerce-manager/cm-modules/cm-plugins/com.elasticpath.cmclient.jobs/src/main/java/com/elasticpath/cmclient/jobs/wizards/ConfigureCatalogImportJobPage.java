/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.jobs.wizards;

import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;

import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.jobs.JobsMessages;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.dataimport.ImportDataType;
import com.elasticpath.domain.dataimport.ImportJob;

/**
 * The Configure Import Job wizard page.
 */
public class ConfigureCatalogImportJobPage extends AbstractConfigureImportJobPageWithDataTypeAndImportType {

	private CCombo catalogCombo;
	private boolean hasDataTypes;
	
	private List<ImportDataType> dataTypes = Collections.emptyList();

	/**
	 * Constructor.
	 * 
	 * @param importJob the import job to add/edit
	 */
	protected ConfigureCatalogImportJobPage(final ImportJob importJob) {
		super("ConfigureImportJobPage", JobsMessages.get().ImportJobWizard_ConfigureImportJob, //$NON-NLS-1$
				JobsMessages.get().ImportJobWizard_ConfigureImportJobDescription, importJob);
	}

	@Override
	protected void createEpPageContent(final IEpLayoutComposite pageComposite) {
		final IEpLayoutData labelData = pageComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL);
		final IEpLayoutData fieldData = pageComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.FILL, true, false, 3, 1);
		pageComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		pageComposite.addLabelBoldRequired(JobsMessages.get().JobDetailsPage_Catalog, EpState.EDITABLE, labelData);
		catalogCombo = pageComposite.addComboBox(EpState.EDITABLE, fieldData);
		
		catalogCombo.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent event) {
				if (catalogCombo.getSelectionIndex() > 0) {
					updateDataAndImportTypeCombosOnSelect(true);
				}
			}
			
		});
		super.createEpPageContent(pageComposite);
	}

	private void updateDataAndImportTypeCombosOnSelect(final boolean refreshButtons) {
		String item = catalogCombo.getItem(catalogCombo.getSelectionIndex());
		Catalog catalog = (Catalog) catalogCombo.getData(item);
		getImportJob().setCatalog(catalog);
		dataTypes = getImportService().getCatalogImportDataTypes(catalog.getUidPk());
		hasDataTypes = CollectionUtils.isNotEmpty(dataTypes);
		fillDataTypeCombo(dataTypes, refreshButtons);
	}

	@Override
	protected void populateControls() {
		super.populateControls();
		
		if (getImportJob().isPersisted()) {
			catalogCombo.setEnabled(false);
		}

		catalogCombo.add(JobsMessages.get().ImportJobWizard_SelectCatalog, 0);

		Catalog selectedCatalog = getImportJob().getCatalog();		
		
		List<Catalog> masterCatalogs = getImportService().listCatalogs();
		for (Catalog catalog : masterCatalogs) {
			if (AuthorizationService.getInstance().isAuthorizedForCatalog(catalog)) {
				final String catalogName = catalog.getName();
				catalogCombo.setData(catalogName, catalog);
				catalogCombo.add(catalogName);
				if (selectedCatalog != null && catalogName.equals(selectedCatalog.getName())) {
					catalogCombo.select(catalogCombo.getItemCount() - 1);
					hasDataTypes = true;
				}
			}
		}

		if (catalogCombo.getSelectionIndex() == -1) {
			catalogCombo.select(0);
		} else if (getImportJob().isPersisted() && catalogCombo.getSelectionIndex() > 0) {
			updateDataAndImportTypeCombosOnSelect(false);
		}
	}


	@Override
	protected void bindControls() {
		super.bindControls();

		DataBindingContext context = getDataBindingContext();
		EpControlBindingProvider binder = EpControlBindingProvider.getInstance();

		final ObservableUpdateValueStrategy catalogUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				if (catalogCombo.getSelectionIndex() != 0) {
					String item = catalogCombo.getItem(catalogCombo.getSelectionIndex());
					Catalog catalog = (Catalog) catalogCombo.getData(item);
					getImportJob().setCatalog(catalog);
				}
				return Status.OK_STATUS;
			}
		};
		binder.bind(context, catalogCombo, EpValidatorFactory.REQUIRED_COMBO_FIRST_ELEMENT_NOT_VALID, null, catalogUpdateStrategy, true);
	}

	@Override
	protected List<ImportDataType> getImportDataTypes() {
		return dataTypes;
	}

	@Override
	public boolean isPageComplete() {
		return hasDataTypes && super.isPageComplete();
	}
	@Override
	public boolean canFlipToNextPage() {
		return catalogCombo.getSelectionIndex() > 0 && super.canFlipToNextPage();
	}
}
