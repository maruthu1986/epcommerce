/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.store.promotions.views;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PartInitException;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.search.SafeSearchCodes;
import com.elasticpath.cmclient.core.search.impl.SafeSearchCodesImpl;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.EpSortingCompositeControl;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTabFolder;
import com.elasticpath.cmclient.store.promotions.PromotionsImageRegistry;
import com.elasticpath.cmclient.store.promotions.PromotionsMessages;
import com.elasticpath.cmclient.store.promotions.helpers.PromotionsSearchRequestJob;
import com.elasticpath.cmclient.store.views.SearchView;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.rules.RuleScenarios;
import com.elasticpath.domain.rules.RuleSet;
import com.elasticpath.domain.store.Store;
import com.elasticpath.cmclient.store.views.IStoreMarketingInnerTab;
import com.elasticpath.service.catalog.CatalogService;
import com.elasticpath.service.rules.RuleSetService;
import com.elasticpath.service.search.query.PromotionSearchCriteria;
import com.elasticpath.service.search.query.StandardSortBy;
import com.elasticpath.service.store.StoreService;

/**
 * Provides methods for creating the promotions search view.
 */
@SuppressWarnings({ "PMD.TooManyFields", "PMD.CyclomaticComplexity", "PMD.GodClass" })
public class PromotionsSearchTab implements SelectionListener, IStoreMarketingInnerTab {

	/**
	 * PromotionsSearchView ID specified in the plugin.xml file. It is the same as the class name.
	 */
	public static final String ID_PROMOTIONS_SEARCH_VIEW = PromotionsSearchTab.class.getName();

	private static final Logger LOG = Logger.getLogger(PromotionsSearchTab.class);

	// The maximum length of text for codes
	private static final int CODE_TEXT_LENGTH = 100;

	private static final int INDEX_ALL = 0;

	private static final String EMPTY_STRING = ""; //$NON-NLS-1$

	private static final List<String> PROMOTION_TYPES = Arrays.asList(PromotionsMessages.get().SearchView_Filters_AllPromotionTypes,
		PromotionsMessages.get().SearchView_Filters_CatalogPromotionType,
		PromotionsMessages.get().SearchView_Filters_ShoppingCartPromotionType);

	private final int tabIndex;

	private CCombo promotionStateCombo;

	private CCombo promotionTypeCombo;

	private CCombo storeCombo;

	private CCombo catalogCombo;

	private Text promotionNameText;

	private final EpState epState = EpState.EDITABLE;

	private PromotionSearchCriteria searchCriteria;

	private final PromotionsSearchRequestJob searchRequestHelper = new PromotionsSearchRequestJob();

	private final SearchView searchView;

	private static final String DEFAULT_PROMOTION_SEARCH = "default"; //$NON-NLS-1$
	private static final String PROMOTION_CATALOG_SEARCH = "catalog"; //$NON-NLS-1$
	private static final String PROMOTION_SHOPPING_CART_SEARCH = "shoppingCart"; //$NON-NLS-1$

	private String searchMode = DEFAULT_PROMOTION_SEARCH;

	private EpSortingCompositeControl sortingCompositeControl;

	private List<Catalog> catalogs;

	private List<Store> stores;

	/** All promotion states for the promotion states combo filter. */
	private enum PromotionStateFilter {
		// We use the order here to populate the state combo, make sure you add values wisely
		ALL(PromotionsMessages.get().SearchView_Filters_AllPromotions),
		ACTIVE(PromotionsMessages.get().Promotion_State_Active),
		DISABLED(PromotionsMessages.get().Promotion_State_Disabled),
		EXPIRED(PromotionsMessages.get().Promotion_State_Expired);

		private String message;

		PromotionStateFilter(final String message) {
			this.message = message;
		}
	}

	/**
	 * Constructor.
	 *
	 * @param tabFolder parent's searchView tab folder.
	 * @param tabIndex index of this tab into tabFolder.
	 * @param searchView parent SearchView.
	 */
	public PromotionsSearchTab(final IEpTabFolder tabFolder, final int tabIndex, final SearchView searchView) {
		// Create the promotions search tab container
		final Image promotionImage = PromotionsImageRegistry.PROMOTION_CATALOG.createImage();
		final IEpLayoutComposite promotionsTab = tabFolder.addTabItem(PromotionsMessages.get().SearchView_PromotionsTab, promotionImage, tabIndex, 1,
				false);
		this.searchView = searchView;
		this.tabIndex = tabIndex;

		// Create the promotions search tab and select it
		createPromotionsTabItem(promotionsTab);

		bindControls();
	}

	/**
	 * Creates all sections of the promotions tab.
	 */
	private void createPromotionsTabItem(final IEpLayoutComposite promotionsComposite) {
		// Create all parts of the Promotions tab
		createPromotionsTabFiltersGroup(promotionsComposite);
		createPromotionsTabSearchTermsGroup(promotionsComposite);
		createPromotionsSortingGroup(promotionsComposite);

		// Populate controls with required data
		populateControls();
		populateDefaultValues();
	}

	/**
	 * Creates the filters group.
	 */
	private void createPromotionsTabFiltersGroup(final IEpLayoutComposite parentComposite) {

		// Create the filters container
		final IEpLayoutComposite filtersGroup = parentComposite.addGroup(PromotionsMessages.get().SearchView_FiltersGroup, 1, false, null);
		final IEpLayoutData layoutData = filtersGroup.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);

		// Add the promotion state filter item
		filtersGroup.addLabelBold(PromotionsMessages.get().Promotion_State, null);
		this.promotionStateCombo = filtersGroup.addComboBox(epState, layoutData);
		this.promotionStateCombo.setEnabled(true);

		// Add the promotion type filter item
		filtersGroup.addLabelBold(PromotionsMessages.get().Promotion_Type, null);
		this.promotionTypeCombo = filtersGroup.addComboBox(epState, layoutData);

		// add the promotion type filter item
		filtersGroup.addLabelBold(PromotionsMessages.get().Promotion_Catalog, null);
		catalogCombo = filtersGroup.addComboBox(epState, layoutData);

		// Add the storeCombo filter item
		filtersGroup.addLabelBold(PromotionsMessages.get().Promotion_Store, null);
		storeCombo = filtersGroup.addComboBox(epState, layoutData);
	}

	/*
	 * Crates the sorting group of controls to specify a column for sorting and sort order.
	 */
	private void createPromotionsSortingGroup(final IEpLayoutComposite parentComposite) {
		this.sortingCompositeControl = new EpSortingCompositeControl(parentComposite, getModel());
	}

	/**
	 * Creates the promotions search terms group.
	 */
	private void createPromotionsTabSearchTermsGroup(final IEpLayoutComposite parentComposite) {

		// Create the search terms container
		final IEpLayoutData layoutData = parentComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);
		final IEpLayoutComposite searchTermsGroup = parentComposite.addGroup(PromotionsMessages.get().SearchView_SearchTermsGroup,
				1, false, layoutData);

		// Add the promotion name search term
		searchTermsGroup.addLabelBold(PromotionsMessages.get().Promotion_PromotionName, null);
		this.promotionNameText = searchTermsGroup.addTextField(EpState.EDITABLE, layoutData);
		this.promotionNameText.setTextLimit(CODE_TEXT_LENGTH);
		this.promotionNameText.addSelectionListener(this);
	}

	private <T extends Enum<?>> int findEnumIndex(final T value) {
		Enum<?>[] consts = value.getClass().getEnumConstants();
		for (int index = 0; index < consts.length; ++index) {
			if (value.equals(consts[index])) {
				return index;
			}
		}
		return -1;
	}

	/**
	 * Populates controls with data from the database.
	 */
	private void populateControls() {

		populatePromotionStateCombo();
		populatePromotionTypeCombo();
		populateCatalogCombo();
		populateStoreCombo();
		populateSortingControl();

		promotionTypeCombo.select(INDEX_ALL);
		promotionStateCombo.select(findEnumIndex(PromotionStateFilter.ALL));

		catalogCombo.select(INDEX_ALL);
		catalogCombo.setEnabled(false);

		storeCombo.select(INDEX_ALL);
		storeCombo.setEnabled(false);
	}

	private void populateSortingControl() {
		sortingCompositeControl.addSortTypeItem(PromotionsMessages.get().SearchView_Sort_Promotion_Name,
				StandardSortBy.PROMOTION_NAME, true);
		sortingCompositeControl.addSortTypeItem(PromotionsMessages.get().SearchView_Sort_Promotion_Enabled, StandardSortBy.PROMOTION_STATE);
		sortingCompositeControl.addSortTypeItem(PromotionsMessages.get().SearchView_Sort_Promotion_Type, StandardSortBy.PROMOTION_TYPE);
		sortingCompositeControl.addSortTypeItem(PromotionsMessages.get().SearchView_Sort_Promotion_Start_Date, StandardSortBy.PROMOTION_ENABLE_DATE);
		sortingCompositeControl.addSortTypeItem(PromotionsMessages.get().SearchView_Sort_Promotion_Exp_Date,
				StandardSortBy.PROMOTION_EXPIRATION_DATE);
	}

	/**
	 * Populates controls with data from the database.
	 */
	private void populateDefaultValues() {

		// Reset to default values

		promotionTypeCombo.select(INDEX_ALL);
		promotionStateCombo.select(INDEX_ALL);

		catalogCombo.select(INDEX_ALL);
		catalogCombo.setEnabled(false);

		storeCombo.select(INDEX_ALL);
		storeCombo.setEnabled(false);

		promotionNameText.setText(EMPTY_STRING);

		// Set our search mode to the default
		searchMode = DEFAULT_PROMOTION_SEARCH;

		sortingCompositeControl.clear();
	}

	private void populatePromotionStateCombo() {
		this.promotionStateCombo.removeAll();
		for (PromotionStateFilter filter : PromotionStateFilter.values()) {
			promotionStateCombo.add(filter.message);
		}
	}

	private void populatePromotionTypeCombo() {
		this.promotionTypeCombo.removeAll();
		for (String currString : PROMOTION_TYPES) {
			this.promotionTypeCombo.add(currString);
		}
	}

	private void populateCatalogCombo() {
		final CatalogService catalogService = ServiceLocator.getService(ContextIdNames.CATALOG_SERVICE);

		catalogs = catalogService.findAllCatalogs();
		AuthorizationService.getInstance().filterAuthorizedCatalogs(catalogs);

		Collections.sort(catalogs, Comparator.comparing(Catalog::getName));

		catalogCombo.removeAll();
		catalogCombo.add(PromotionsMessages.get().SearchView_Filters_AllCatalogs, INDEX_ALL);
		for (Catalog catalog : catalogs) {
			catalogCombo.add(catalog.getName());
			catalogCombo.setData(catalog.getName(), catalog);
		}
	}

	private void populateStoreCombo() {
		final StoreService storeService = ServiceLocator.getService(ContextIdNames.STORE_SERVICE);

		stores = storeService.findAllCompleteStores();
		AuthorizationService.getInstance().removeUnathorizedStoresFrom(stores);

		Collections.sort(stores, Comparator.comparing(Store::getName));

		storeCombo.removeAll();
		storeCombo.add(PromotionsMessages.get().SearchView_Filters_AllStores, INDEX_ALL);
		for (Store store : stores) {
			storeCombo.add(store.getName());
			storeCombo.setData(store.getName(), store);
		}
	}

	/**
	 * Reinitialize the store filter.
	 */
	public void reinitStoreFilter() {

		// We could have modified the list of stores so refresh and reselect
		int previousStoreIndex = this.storeCombo.getSelectionIndex();
		populateStoreCombo();
		this.storeCombo.select(previousStoreIndex);

		// We could have modified the list of catalogs so refresh and reselect
		int previousCatalogIndex = this.catalogCombo.getSelectionIndex();
		populateCatalogCombo();
		this.catalogCombo.select(previousCatalogIndex);

		switch (searchMode) {
			case DEFAULT_PROMOTION_SEARCH:
				catalogCombo.setEnabled(false);
				storeCombo.setEnabled(false);
				break;
			case PROMOTION_CATALOG_SEARCH:
				storeCombo.setEnabled(false);
				catalogCombo.setEnabled(true);
				break;
			case PROMOTION_SHOPPING_CART_SEARCH:
				storeCombo.setEnabled(true);
				catalogCombo.setEnabled(false);
				break;
			default:
				//do nothing
				break;
		}
	}

	/**
	 * Must be called after tab creation to bind controls.
	 *
	 */
	private void bindControls() {

		promotionTypeCombo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent event) {

				int promotionTypeIndex = promotionTypeCombo.getSelectionIndex();
				String selectedPromoTypeString = PROMOTION_TYPES.get(promotionTypeIndex);

				// TODO: Potential refactoring relating to adding other combo
				// boxes as listeners on this promotionTypeCombo

				if (selectedPromoTypeString.equalsIgnoreCase(
						PromotionsMessages.get().SearchView_Filters_AllPromotionTypes)) {

					PromotionsSearchTab.this.searchMode = DEFAULT_PROMOTION_SEARCH;
					catalogCombo.setEnabled(false);
					catalogCombo.select(INDEX_ALL);
					storeCombo.setEnabled(false);
					storeCombo.select(INDEX_ALL);

				} else if (selectedPromoTypeString.equalsIgnoreCase(
						PromotionsMessages.get().SearchView_Filters_CatalogPromotionType)) {

					// Enable the catalog dropdown
					PromotionsSearchTab.this.searchMode = PROMOTION_CATALOG_SEARCH;
					storeCombo.setEnabled(false);
					storeCombo.select(INDEX_ALL);
					catalogCombo.setEnabled(true);

				} else if (selectedPromoTypeString.equalsIgnoreCase(
						PromotionsMessages.get().SearchView_Filters_ShoppingCartPromotionType)) {

					// Enable the store dropdown
					PromotionsSearchTab.this.searchMode = PROMOTION_SHOPPING_CART_SEARCH;

					storeCombo.setEnabled(true);
					catalogCombo.setEnabled(false);
					catalogCombo.select(INDEX_ALL);
				}

			}
		});
	}

	/**
	 * Sets the focus to an internal UI control.
	 */
	public void setFocus() {
		if (this.promotionNameText != null) {
			this.promotionNameText.setFocus();
		}
	}

	/**
	 * Gets the model object.
	 *
	 * @return model object
	 */
	public final PromotionSearchCriteria getModel() {
		if (searchCriteria == null) {
			searchCriteria = ServiceLocator.getService(ContextIdNames.PROMOTION_SEARCH_CRITERIA);
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

	/**
	 * Performs a promotions search.
	 */
	private void doSearch() {
		try {
			searchView.getSite().getPage().showView(PromotionsSearchResultsView.ID_PROMOTIONS_SEARCH_RESULTS_VIEW);
		} catch (final PartInitException e1) {
			LOG.error(e1.getMessage(), e1);
		}

		// Empty criteria to start
		searchCriteria.clear();

		// Set search criteria
		setCommonSearchCriteria();

		// Work out what mode we are in then build search criteria accordingly
		if (searchMode.equals(PROMOTION_CATALOG_SEARCH)
				|| searchMode.equals(DEFAULT_PROMOTION_SEARCH)) {
			setCatalogSearchCriteria();
		}
		if (searchMode.equals(PROMOTION_SHOPPING_CART_SEARCH)
				|| searchMode.equals(DEFAULT_PROMOTION_SEARCH)) {
			setShoppingCartSearchCriteria();
		}

		handleEmptySearchCriteria();
		sortingCompositeControl.updateSearchCriteriaValues();
		searchRequestHelper.setSearchCriteria(searchCriteria);
		searchRequestHelper.executeSearch(searchView.getSite().getShell());
	}

	/**
	 * Set the shopping cart search criteria.
	 */
	private void setShoppingCartSearchCriteria() {
		SafeSearchCodes storeCodes = new SafeSearchCodesImpl();
		if (storeCombo.getSelectionIndex() == INDEX_ALL) {
			storeCodes.extractAndAdd(stores, "code");  //$NON-NLS-1$
		} else {
			Store selectedStore = (Store) storeCombo.getData(storeCombo.getText());
			storeCodes.extractAndAdd(selectedStore, "code");  //$NON-NLS-1$
		}
		searchCriteria.setStoreCodes(storeCodes.asSet());

		final RuleSetService ruleSetService = ServiceLocator.getService(
				ContextIdNames.RULE_SET_SERVICE);
		final RuleSet ruleSet = ruleSetService.findByScenarioId(RuleScenarios.CART_SCENARIO);
		searchCriteria.setRuleSetUid(String.valueOf(ruleSet.getUidPk()));
	}

	/**
	 * Set catalog search criteria.
	 */
	private void setCatalogSearchCriteria() {
		if (catalogCombo.getSelectionIndex() == INDEX_ALL) {
			SafeSearchCodes catalogCodes = new SafeSearchCodesImpl();
			catalogCodes.extractAndAdd(catalogs, "code");  //$NON-NLS-1$
			searchCriteria.setCatalogCodes(catalogCodes.asSet());
		} else {
			Catalog selectedCatalog = (Catalog) catalogCombo.getData(catalogCombo.getText());
			searchCriteria.setCatalogCode(selectedCatalog.getCode());
		}

		final RuleSetService ruleSetService = ServiceLocator.getService(
				ContextIdNames.RULE_SET_SERVICE);
		final RuleSet ruleSet = ruleSetService.findByScenarioId(RuleScenarios.CATALOG_BROWSE_SCENARIO);
		searchCriteria.setRuleSetUid(String.valueOf(ruleSet.getUidPk()));
	}

	/**
	 * Set common searcAll Promotions criteria.
	 */
	private void setCommonSearchCriteria() {
		PromotionStateFilter[] filter = PromotionStateFilter.values();
		if (promotionStateCombo.getSelectionIndex() >= 0) {
			switch (filter[promotionStateCombo.getSelectionIndex()]) {
				case ALL:
					// no criteria to set
					break;
				case ACTIVE:
					searchCriteria.setEnabled(true);
					searchCriteria.setActive(true);
					break;
				case DISABLED:
					searchCriteria.setEnabled(false);
					break;
				case EXPIRED:
					searchCriteria.setActive(false);
					break;
				default:
					throw new UnsupportedOperationException("State not implemented"); //$NON-NLS-1$
			}
		}

		if (StringUtils.isNotEmpty(promotionNameText.getText())) {
			searchCriteria.setPromotionName(promotionNameText.getText());
		}
	}

	/**
	 * Handle the empty search criteria scenario.
	 * In this case we set matchAll and return all promotions.
	 */
	private void handleEmptySearchCriteria() {
		if (searchCriteria.isEmpty()) {
			searchCriteria.setMatchAll(true);
		} else {
			searchCriteria.setMatchAll(false);
		}
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
