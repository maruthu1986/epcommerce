/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.pricelistassignments.views;

import java.util.Arrays;
import java.util.List;

import com.elasticpath.cmclient.core.LoginManager;
import com.elasticpath.cmclient.pricelistmanager.PriceListManagerImageRegistry;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import com.elasticpath.cmclient.core.CmSingletonUtil;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.event.EventType;
import com.elasticpath.cmclient.core.event.UIEvent;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTabFolder;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.cmclient.pricelistassignments.controller.PriceListAssignmentsSearchController;
import com.elasticpath.cmclient.pricelistassignments.model.PriceListAssigmentsSearchTabModel;
import com.elasticpath.cmclient.pricelistmanager.PriceListManagerMessages;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.service.catalog.CatalogService;

/**
 * Composite that displays the Price List Assignment Search GUI.
 */
public class PriceListAssigmentsSearchTab {
	
	private final DataBindingContext dataBindingCtx;
	
	private Text priceListNameText;
	
	private CCombo catalogCombo;

	private Button searchButton;
	
	private Button clearButton;
	
	private final PriceListAssigmentsSearchTabModel model;
	
	private final CatalogService catalogService;
	
	private List<Catalog> allCatalogs;
	
	/**
	 * Constructor.
	 * 
	 * @param tabFolder parent's searchView tab folder.
	 * @param tabIndex index of this tab into tabFolder.
	 */
	public PriceListAssigmentsSearchTab(final IEpTabFolder tabFolder, final int tabIndex) {
		
		dataBindingCtx = new DataBindingContext();
		
		this.model = new PriceListAssigmentsSearchTabModel();
		
		final IEpLayoutComposite tabComposite = tabFolder.addTabItem(
				PriceListManagerMessages.get().PriceListAssignmentSearchTab_Name,
				PriceListManagerImageRegistry.getImage(PriceListManagerImageRegistry.IMAGE_PRICE_LIST_ASSIGN),
				tabIndex,
				1, 
				false);
		
		catalogService = ServiceLocator.getService(ContextIdNames.CATALOG_SERVICE);
		
		createPriceListAssignmentSearchItem(tabComposite);		
		
	}
	
	/**
	 * Creates all sections of the dynamic content tab.
	 * @param tabComposite the Layout Composite 
	 */
	private void createPriceListAssignmentSearchItem(final IEpLayoutComposite tabComposite) {
		
		final IEpLayoutData layoutData = tabComposite.createLayoutData(
				IEpLayoutData.FILL, 
				IEpLayoutData.FILL, 
				true, 
				false);
		
		final IEpLayoutComposite epLayoutComposite =  tabComposite.addGroup(
				PriceListManagerMessages.get().PriceListAssignmentSearchTab_Title,
				1, 
				false, 
				layoutData);
		
		epLayoutComposite.addLabelBold(PriceListManagerMessages.get().PriceListAssignmentSearchTab_PriceListName, null);
		
		priceListNameText = epLayoutComposite.addTextField(EpState.EDITABLE, layoutData);
		
		epLayoutComposite.addLabelBold(PriceListManagerMessages.get().PriceListAssignmentSearchTab_Catalog, null);
		
		catalogCombo = epLayoutComposite.addComboBox(EpState.EDITABLE, layoutData);
		initCombo();
		
		// Create the buttons group container
		final IEpLayoutComposite buttonsGroup = tabComposite.addGridLayoutComposite(1, false, layoutData);

		buttonsGroup.addHorizontalSeparator(buttonsGroup.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false));

		// Create the buttons container
		final IEpLayoutData buttonsCompositeData = tabComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL, false, false);
		final IEpLayoutComposite buttonsComposite = buttonsGroup.addGridLayoutComposite(2, true, buttonsCompositeData);
		
		
		addListeners(buttonsComposite);
		
		//----------------- bind ----------------------
		
		final EpControlBindingProvider bindingProvider = EpControlBindingProvider.getInstance();
		
		final ObservableUpdateValueStrategy storeUpdateStrategy = new ObservableUpdateValueStrategy() {
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				Integer selectedItem = (Integer) newValue;
				if (selectedItem <= 0) {
					model.setCatalogName(null);
				} else {

					model.setCatalogName(catalogCombo.getItem(selectedItem));
				}
				return Status.OK_STATUS;
			}
		};
		
		bindingProvider.bind(dataBindingCtx, catalogCombo, null, null, storeUpdateStrategy, false);
		catalogCombo.select(0);
		
		EpControlBindingProvider.getInstance().bind(
				dataBindingCtx, 
				priceListNameText, 
				model, 
				"priceListName",   //$NON-NLS-1$
				EpValidatorFactory.MAX_LENGTH_255, 
				null, 
				true);
		
		
	}

	private void addListeners(final IEpLayoutComposite buttonsComposite) {
		searchButton =  buttonsComposite.addPushButton(
				PriceListManagerMessages.get().PriceListAssignmentSearchTab_SearchButton,
				CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_SEARCH_ACTIVE),
				EpState.EDITABLE, 
				null);
		
		searchButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent event) {
				UIEvent<PriceListAssigmentsSearchTabModel> searchEvent
						= new UIEvent<>(model, EventType.SEARCH, false);
				CmSingletonUtil.getSessionInstance(PriceListAssignmentsSearchController.class).onEvent(searchEvent);
			}
		});
		
		clearButton =  buttonsComposite.addPushButton(
				PriceListManagerMessages.get().PriceListAssignmentSearchTab_ClearButton,
				null,
				EpState.EDITABLE, 
				null);

		clearButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(final SelectionEvent event) {
				priceListNameText.setText(StringUtils.EMPTY);
				catalogCombo.clearSelection();
				catalogCombo.select(0);
				model.setCatalogName(null);
				model.setPriceListName(StringUtils.EMPTY);
			}
		});		
		
		//[BB-937]reload the catalog combobox data on each selection to keep it's 
		//content up-to-date to all catalog management modifications
		catalogCombo.addListener(SWT.FocusIn, (Listener) event -> populateCombo());
	}

	private void populateCombo() {
		initCombo();
		//retrieving all catalogs
		CmUser currentUser = LoginManager.getCmUser();
		allCatalogs = catalogService.findAllCatalogs();
		if (!currentUser.isAllCatalogsAccess()) {
			allCatalogs.retainAll(currentUser.getCatalogs());
		}
		String[] items = new String[allCatalogs.size()];
		for (int i = 0; i < items.length; i++) {
			items[i] = allCatalogs.get(i).getName();
		}
		//sorting their names alphabetically
		Arrays.sort(items);
		//adding them into combobox
		for (String cName : items) {
			catalogCombo.add(cName);
		}
		int index = 0;
		//setting back the item that was selected before 
		if (model.getCatalogName() != null) {
			//retrieving the item's index by it's name from the model
			int position = Arrays.binarySearch(items, model.getCatalogName());
			if (position > 0) {
				index = position;
			}
		}
		//and if it's in the combobox set it selected
		if (index > 0) {
			catalogCombo.select(index + 1);
			return;
		} 
		//otherwise show the "All" item
		catalogCombo.select(0);
	}

	private void initCombo() {
		catalogCombo.removeAll();
		catalogCombo.add(PriceListManagerMessages.get().PriceListAssignmentSearchTab_AllCatalogs, 0);
	}	
	

	/**
	 * Get the model.
	 * @return model.
	 */
	public PriceListAssigmentsSearchTabModel getModel() {
		return model;
	}
	
	

}
