/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.shipping.views;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PartInitException;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.EpSortingCompositeControl;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTabFolder;
import com.elasticpath.cmclient.store.shipping.ShippingImageRegistry;
import com.elasticpath.cmclient.store.shipping.ShippingLevelsMessages;
import com.elasticpath.cmclient.store.shipping.helpers.ShippingLevelSearchRequestJob;
import com.elasticpath.cmclient.store.views.IStoreMarketingInnerTab;
import com.elasticpath.cmclient.store.views.SearchView;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.EpShippingContextIdNames;
import com.elasticpath.domain.shipping.ShippingRegion;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.search.query.ShippingServiceLevelSearchCriteria;
import com.elasticpath.service.search.query.StandardSortBy;
import com.elasticpath.service.shipping.ShippingRegionService;
import com.elasticpath.service.store.StoreService;

/**
 * Tab for SearchView. Filters Shipping Service Levels list.
 */
public class ShippingLevelsSearchTab implements SelectionListener, IStoreMarketingInnerTab {
	private static final Logger LOG = Logger.getLogger(ShippingLevelsSearchTab.class);

	private static final int INDEX_ALL = 0;

	private static final int STATE_INDEX_ACTIVE = 0;

	private CCombo shippingRegionCombo;

	private CCombo storeCombo;

	private List<ShippingRegion> shippingRegions;

	private List<Store> stores;

	private CCombo stateCombo;

	private ShippingServiceLevelSearchCriteria searchCriteria;

	private final SearchView searchView;
	
	private EpSortingCompositeControl sortingCompositeControl;
	
	private final ShippingLevelSearchRequestJob searchRequestHelper = new ShippingLevelSearchRequestJob();

	private final int tabIndex;

	/**
	 * The constructor.
	 * 
	 * @param tabFolder parent's searchView tab folder.
	 * @param tabIndex tabIndex of this tab into tabFolder.
	 * @param searchView parent SearchView.
	 */
	public ShippingLevelsSearchTab(final IEpTabFolder tabFolder, final int tabIndex, final SearchView searchView) {

		final Image shippingLevelsImage = ShippingImageRegistry.getImage(ShippingImageRegistry.IMAGE_SHIPPING_LEVEL);
		final IEpLayoutComposite tabComposite = tabFolder.addTabItem(ShippingLevelsMessages.get().ShippingLevelsSearchTabTitle, shippingLevelsImage,
				tabIndex, 1, false);
		this.searchView = searchView;
		this.tabIndex = tabIndex;

		createFilterGroup(tabComposite);
		createSortingGroup(tabComposite);

		populateControls();
		populateSortingControl();
		populateDefaultValues();

		bindControls();
	}

	private void createFilterGroup(final IEpLayoutComposite tabComposite) {
		final IEpLayoutData layoutData = tabComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);
		final IEpLayoutComposite filtersGroup = tabComposite.addGroup(ShippingLevelsMessages.get().ShippingLevelsFiltersGroupTitle,
				1, false, layoutData);

		filtersGroup.addLabelBold(ShippingLevelsMessages.get().ShippingLevelState, null);
		this.stateCombo = filtersGroup.addComboBox(EpState.EDITABLE, layoutData);
		
		// Add the Shipping Region filter item
		filtersGroup.addLabelBold(ShippingLevelsMessages.get().ShippingLevelsShippingRegionLabel, null);
		this.shippingRegionCombo = filtersGroup.addComboBox(EpState.EDITABLE, layoutData);
		this.shippingRegionCombo.setEnabled(true);

		// Add the Store filter item
		filtersGroup.addLabelBold(ShippingLevelsMessages.get().ShippingLevelsStoreLabel, null);
		this.storeCombo = filtersGroup.addComboBox(EpState.EDITABLE, layoutData);
		this.storeCombo.setEnabled(true);
	}

	/*
	 * Crates the sorting group of controls to specify a column for sorting and sort order.
	 */
	private void createSortingGroup(final IEpLayoutComposite parentComposite) {
		this.sortingCompositeControl = new EpSortingCompositeControl(parentComposite, getModel());
	}
	
	private void populateDefaultValues() {
		stateCombo.select(STATE_INDEX_ACTIVE);
		storeCombo.select(INDEX_ALL);
		shippingRegionCombo.setText(shippingRegionCombo.getItem(INDEX_ALL));
		// bindingContext.updateModels();
		sortingCompositeControl.clear();
	}

	private void populateControls() {
		shippingRegionCombo.add(ShippingLevelsMessages.get().ShippingLevelsAllShippingRegionsComboboxItem, INDEX_ALL);
		ShippingRegionService shippingRegionService = ServiceLocator.getService(
				EpShippingContextIdNames.SHIPPING_REGION_SERVICE);
		shippingRegions = shippingRegionService.list();
		for (ShippingRegion shippingRegion : shippingRegions) {
			shippingRegionCombo.setData(shippingRegion.getName(), shippingRegion);
			shippingRegionCombo.add(shippingRegion.getName());
		}

		storeCombo.add(ShippingLevelsMessages.get().ShippingLevelsAllStoresComboboxItem, INDEX_ALL);
		StoreService storeService = ServiceLocator.getService(ContextIdNames.STORE_SERVICE);
		stores = storeService.findAllCompleteStores();
		AuthorizationService.getInstance().removeUnathorizedStoresFrom(stores);
		
		for (Store store : stores) {
			storeCombo.setData(store.getName(), store);
			storeCombo.add(store.getName());
		}
		
		int selectedIndex = stateCombo.getSelectionIndex();
		stateCombo.removeAll();
		stateCombo.setData(ShippingLevelsMessages.get().Active, true);
		stateCombo.add(ShippingLevelsMessages.get().Active);
		stateCombo.setData(ShippingLevelsMessages.get().InActive, false);
		stateCombo.add(ShippingLevelsMessages.get().InActive);
		stateCombo.setData(ShippingLevelsMessages.get().AllShippingServiceLevels, null);
		stateCombo.add(ShippingLevelsMessages.get().AllShippingServiceLevels);
		if (selectedIndex >= 0) {
			stateCombo.select(selectedIndex);
		}
	}
	
	private void populateSortingControl() {
		sortingCompositeControl.addSortTypeItem(ShippingLevelsMessages.get().ShippingLevelStoreColumnLabel, StandardSortBy.STORE_NAME, true);
		sortingCompositeControl.addSortTypeItem(ShippingLevelsMessages.get().ShippingLevelRegionColumnLabel, StandardSortBy.REGION);
		sortingCompositeControl.addSortTypeItem(ShippingLevelsMessages.get().ShippingLevelCarierColumnLabel, StandardSortBy.CARRIER);
		sortingCompositeControl.addSortTypeItem(ShippingLevelsMessages.get().ShippingLevelCode, StandardSortBy.SERVICE_LEVEL_CODE);
		sortingCompositeControl.addSortTypeItem(ShippingLevelsMessages.get().ShippingLevelNameColumnLabel, StandardSortBy.SERVICE_LEVEL_NAME);
		sortingCompositeControl.addSortTypeItem(ShippingLevelsMessages.get().ShippingLevelState, StandardSortBy.ACTIVE);
	}
	
	/**
	 * Reinitialize filter list and try to save filter fields if it's possible,
	 *  if it's not then reset filter fields.
	 */
	public void reinitFilterLists() {
		String shippingText = shippingRegionCombo.getText();
		String storeText = storeCombo.getText();
		shippingRegionCombo.removeAll();
		storeCombo.removeAll();
		populateControls();
		int storeIndex = storeCombo.indexOf(storeText);
		if (storeIndex >= 0) {
			storeCombo.select(storeIndex);
		} else {
			storeCombo.select(INDEX_ALL);
		}
		int shippingIndex = shippingRegionCombo.indexOf(shippingText);
		if (shippingIndex >= 0) {
			shippingRegionCombo.select(shippingIndex);
		} else {
			shippingRegionCombo.select(INDEX_ALL);
		}
	}

	/**
	 * Must be called after tab creation to bind controls.
	 */
	private void bindControls() {
		// nothing to do.
	}

	/**
	 * Performs a promotions search.
	 */
	private void doSearch() {
		try {
			searchView.getSite().getPage().showView(ShippingLevelsSearchResultsView.VIEW_ID);
		} catch (final PartInitException e1) {
			LOG.error(e1.getMessage(), e1);
		}

		// Empty criteria to start
		searchCriteria.clear();
		
		// Set search criteria
		searchCriteria.setActiveFlag((Boolean) stateCombo.getData(stateCombo.getText()));

		if (storeCombo.getSelectionIndex() != INDEX_ALL) {
			searchCriteria.setStoreExact(storeCombo.getText());
		}

		if (shippingRegionCombo.getSelectionIndex() != INDEX_ALL) {
			searchCriteria.setRegionExact(((ShippingRegion) shippingRegionCombo.getData(shippingRegionCombo.getText())).getName());
		}
		
		searchCriteria.setMatchAll(searchCriteria.getActiveFlag() == null 
				&& searchCriteria.getStoreExact() == null && searchCriteria.getRegionExact() == null);
		
		// handleEmptySearchCriteria();
		sortingCompositeControl.updateSearchCriteriaValues();
		searchRequestHelper.setSearchCriteria(searchCriteria);		
		searchRequestHelper.executeSearch(null);
	}
	
	/**
	 * Unregister from ShippingLevelsEventService.
	 */
	public void dispose() {
		// nothing to dispose
	}
	
	/**
	 * Gets the model object.
	 * 
	 * @return model object
	 */
	public final ShippingServiceLevelSearchCriteria getModel() {
		if (searchCriteria == null) {
			searchCriteria = new ShippingServiceLevelSearchCriteria();
		}
		return searchCriteria;
	}

	@Override
	public void widgetDefaultSelected(final SelectionEvent event) {
		if (event.getSource() instanceof Text) {
			doSearch();
		}
	}

	@Override
	public void widgetSelected(final SelectionEvent event) {
		//do nothing
	}

	@Override
	public void search() {
		doSearch();
	}

	@Override
	public void clear() {
		populateDefaultValues();
	}

	@Override
	public boolean isDisplaySearchButton() {
		return true;
	}

	@Override
	public int getTabIndex() {
		return tabIndex;
	}
}

