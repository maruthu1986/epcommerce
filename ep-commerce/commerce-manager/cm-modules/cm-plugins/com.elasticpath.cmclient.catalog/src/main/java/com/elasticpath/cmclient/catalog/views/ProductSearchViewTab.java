/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.views;

import java.util.Locale;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PartInitException;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.EpUiException;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.helpers.CatalogListener;
import com.elasticpath.cmclient.core.helpers.ProductSearchRequestJob;
import com.elasticpath.cmclient.core.search.SafeSearchCodes;
import com.elasticpath.cmclient.core.service.CatalogEventService;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.EpSortingCompositeControl;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.service.search.query.ProductSearchCriteria;
import com.elasticpath.service.search.query.StandardSortBy;

/**
 * The <code>ProductSearchView</code> is used to search for products, either by providing query strings or by selecting filters.
 */
public class ProductSearchViewTab extends AbstractCatalogSearchViewTab implements SelectionListener, CatalogListener {
	
	private static final transient Logger LOG = Logger.getLogger(ProductSearchViewTab.class);

	private static final int NORMAL_TEXT_LENGTH = 255;

	private static final String EMPTY_STRING = ""; //$NON-NLS-1$

	// NOTE: loadSku !MUST! be !TRUE! since delete action checks whether the orderSkus are in shipment or not.
	// setting loadSku to false results in orderSku = null and omitting the check.
	private final ProductSearchRequestJob searchJob = new ProductSearchRequestJob(true);

	private ProductSearchCriteria productSearchCriteria;

	private IEpLayoutComposite searchTermsGroup;

	private Text productNameText;

	private Text productCodeText;

	private Text skuCodeText;

	private CCombo brandCombo;

	private CCombo catalogCombo;

	private Button activeOnlyCheckbox;

	private EpSortingCompositeControl sortingComposite;

	private final IEpLayoutComposite tabComposite;

	private final CatalogSearchView parent;

	/**
	 * The constructor. 
	 * 
	 * @param tabComposite the tab composite
	 * @param productSearchView the product search view which contains this tab
	 */
	public ProductSearchViewTab(final IEpLayoutComposite tabComposite, final CatalogSearchView productSearchView) {
		this.tabComposite = tabComposite;
		this.parent = productSearchView;
		CatalogEventService.getInstance().addCatalogListener(this);
	}
	
	/**
	 * The init method.
	 */
	public void init() {
		this.createViewPartControl();
	}

	private void createViewPartControl() {
		
		this.createTermsGroup(tabComposite);
		this.createFiltersGroup(tabComposite);
		this.createSortingGroup(tabComposite);

		this.populateControls();
		this.bindControls();

		CatalogEventService.getInstance().addCatalogListener(event -> populateCatalogCombo(catalogCombo));

		CatalogEventService.getInstance().addBrandListener(event -> populateBrandCombo(brandCombo));
	}

	/**
	 * Creates the customer search terms group.
	 */
	private void createTermsGroup(final IEpLayoutComposite parentComposite) {
		final IEpLayoutData data = parentComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);
		this.searchTermsGroup = parentComposite.addGroup(CatalogMessages.get().SearchView_SearchTermsGroup, 1, false, data);

		// product name text field
		this.searchTermsGroup.addLabelBold(CatalogMessages.get().SearchView_Search_Label_ProductName, null);
		this.productNameText = this.searchTermsGroup.addTextField(EpState.EDITABLE, data);
		this.productNameText.setTextLimit(NORMAL_TEXT_LENGTH);
		this.productNameText.addSelectionListener(this);

		// product code text field
		this.searchTermsGroup.addLabelBold(CatalogMessages.get().SearchView_Search_Label_ProductCode, null);
		this.productCodeText = this.searchTermsGroup.addTextField(EpState.EDITABLE, data);
		this.productCodeText.setTextLimit(NORMAL_TEXT_LENGTH);
		this.productCodeText.addSelectionListener(this);

		// sku code text field
		this.searchTermsGroup.addLabelBold(CatalogMessages.get().SearchView_Search_Label_SkuCode, null);
		this.skuCodeText = this.searchTermsGroup.addTextField(EpState.EDITABLE, data);
		this.skuCodeText.setTextLimit(NORMAL_TEXT_LENGTH);
		this.skuCodeText.addSelectionListener(this);
	}

	/*
	 * Creates the filters group to filter search result by Brand or Catalogue.
	 */
	private void createFiltersGroup(final IEpLayoutComposite parentComposite) {
		final IEpLayoutComposite groupComposite = parentComposite.addGroup(CatalogMessages.get().SearchView_FiltersGroup, 1, false, null);
		final IEpLayoutData data = groupComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);

		// brand combo box
		groupComposite.addLabelBold(CatalogMessages.get().SearchView_Filter_Label_Brand, null);
		this.brandCombo = groupComposite.addComboBox(EpState.EDITABLE, data);
		this.brandCombo.setEnabled(true);

		// catalog combo box
		groupComposite.addLabelBold(CatalogMessages.get().SearchView_Filter_Label_Catalog, null);
		this.catalogCombo = groupComposite.addComboBox(EpState.EDITABLE, data);
		this.catalogCombo.setEnabled(true);

		// active-only checkbox
		this.activeOnlyCheckbox = groupComposite.addCheckBoxButton(CatalogMessages.get()
				.SearchView_Filter_Label_ProductActiveOnly, EpState.EDITABLE, data);
		this.activeOnlyCheckbox.setEnabled(true);
		this.activeOnlyCheckbox.setSelection(true);

	}

	/*
	 * Creates the sorting group controls to sort search resulting set by specified column in specified order.
	 */
	private void createSortingGroup(final IEpLayoutComposite parentComposite) {
		sortingComposite = new EpSortingCompositeControl(parentComposite, getModel());
	}

	/**
	 * Populate all controls on this view.
	 */
	private void populateControls() {
		populateBrandCombo(brandCombo);
		configureStandardComboWidth(brandCombo);

		populateCatalogCombo(catalogCombo);
		configureStandardComboWidth(catalogCombo);

		populateSortingComposite();
		configureStandardComboWidth(sortingComposite.getSortOrderCombo());
		configureStandardComboWidth(sortingComposite.getSortByColumnCombo());

		initialSettingsForSearchCriteria();
	}
	
	private void initialSettingsForSearchCriteria() {
		//initial search criteria
		getModel().setCatalogCodes(getCatalogCodesForSearch(getInitializeAuthorizedCatalogsList()));
		getModel().setCatalogSearchableLocales(getCatalogSearchableLocales(getInitializeAuthorizedCatalogsList()));
	}

	private void populateSortingComposite() {
		sortingComposite.addSortTypeItem(CatalogMessages.get().SearchView_Sort_ProductName, StandardSortBy.PRODUCT_NAME_NON_LC, true);
		sortingComposite.addSortTypeItem(CatalogMessages.get().SearchView_Sort_ProductCode, StandardSortBy.PRODUCT_CODE);
		sortingComposite.addSortTypeItem(CatalogMessages.get().SearchView_Sort_ProductType, StandardSortBy.PRODUCT_TYPE_NAME);
		sortingComposite.addSortTypeItem(CatalogMessages.get().SearchView_Sort_Brand, StandardSortBy.BRAND_NAME);
		sortingComposite.addSortTypeItem(CatalogMessages.get().SearchView_Sort_DefaultCategory, StandardSortBy.PRODUCT_DEFAULT_CATEGORY_NAME);
		sortingComposite.addSortTypeItem(CatalogMessages.get().SearchView_Sort_StartDate, StandardSortBy.PRODUCT_START_DATE);
		sortingComposite.addSortTypeItem(CatalogMessages.get().SearchView_Sort_EndDate, StandardSortBy.PRODUCT_END_DATE);
	}

	@Override
	public void clear() {
		this.productCodeText.setText(EMPTY_STRING);
		this.skuCodeText.setText(EMPTY_STRING);
		this.productNameText.setText(EMPTY_STRING);
		this.brandCombo.select(getAllFilterIndex());
		this.catalogCombo.select(getAllFilterIndex());
		this.activeOnlyCheckbox.setSelection(false);
		this.sortingComposite.clear();
		
		this.productSearchCriteria.clear();
		
		initialSettingsForSearchCriteria();
	}

	/**
	 * Sets the focus to an internal UI control.
	 */
	public void setFocus() {
		if (this.productCodeText != null) {
			this.productCodeText.setFocus();
		}
	}

	/**
	 * The controls of the search tab should be bound here.
	 */
	private void bindControls() {
		// bind product name
		bind(productNameText, getModel(), "productName", null, null); //$NON-NLS-1$
		// bind product code
		bind(productCodeText, getModel(), "productCode", null, null); //$NON-NLS-1$
		// bind sku code
		bind(skuCodeText, getModel(), "productSku", null, null); //$NON-NLS-1$
		
		bindBrandCombo(brandCombo);
		
		bindCatalogCombo(catalogCombo);

		// bind inactive/active
		this.bind(this.activeOnlyCheckbox, this.getModel(), "activeOnly", null, null); //$NON-NLS-1$
	}

	/**
	 * Gets the <code>ProductSearchCriteria</code> model object.
	 * 
	 * @return the <code>ProductSearchCriteria</code> model object
	 */
	protected ProductSearchCriteria getModel() {
		if (this.productSearchCriteria == null) {
			this.productSearchCriteria = ServiceLocator.getService(
					ContextIdNames.PRODUCT_SEARCH_CRITERIA);
		}
		return this.productSearchCriteria;
	}

	/**
	 * Called when a widget is default selected. For text fields hitting ENTER calls this method.
	 * 
	 * @param event selection event
	 */
	@Override
	public void widgetDefaultSelected(final SelectionEvent event) {
		if (event.getSource() instanceof Text) {
			search();
		}
	}

	@Override
	public void widgetSelected(final SelectionEvent event) {
		//nothing to do
	}

	/**
	 * Fire the index search.
	 * 
	 * @param searchCriteria the product search criteria
	 */
	protected void doSearch(final ProductSearchCriteria searchCriteria) {
		this.searchJob.setSource(this);
		this.searchJob.setSearchCriteria(searchCriteria);
		this.searchJob.executeSearch(null);
	}

	/**
	 * Process the search criteria before requesting for index search and call for index search.
	 */
	@Override
	public void search() {
		checkView();
		
		this.getDataBindingContext().updateModels();
			
		sortingComposite.updateSearchCriteriaValues();
		productSearchCriteria.setLocale(CorePlugin.getDefault().getDefaultLocale());
		doSearch(this.productSearchCriteria);
	}

	/**
	 * 
	 */
	private void checkView() {
		try {
			parent.getSite().getWorkbenchWindow().getActivePage().showView(SearchProductListView.PART_ID);
			//workbenchPage.showView(SearchProductListView.PART_ID, null, IWorkbenchPage.VIEW_ACTIVATE);
		} catch (final PartInitException e) {
			// Log the error and throw an unchecked exception
			LOG.error(e.getStackTrace());
			throw new EpUiException("Fail to reopen product list view.", e); //$NON-NLS-1$
		}
	}
	
	/**
	 * This method is used for testing.
	 * 
	 * @return the productCodeText
	 */
	protected Text getProductCodeText() {
		return productCodeText;
	}
	
	@Override
	protected Logger getLog() {
		return LOG;
	}
	
	@Override
	protected void bindBrandCodeToModel(final String brandCode) {
		getModel().setBrandCode(brandCode);
	}
	
	@Override
	protected void bindCatalogSearchableLocalesToModel(final Set<Locale> searchableLocale) {
		getModel().setCatalogSearchableLocales(searchableLocale);
	}
	@Override
	protected void bindCatalogCodesToModel(final SafeSearchCodes catalogCodes) {
		getModel().setCatalogCodes(catalogCodes.asSet());
	}
	
	@Override
	public void catalogChanged(final ItemChangeEvent<Catalog> event) {
		this.populateCatalogCombo(catalogCombo);
	}
	
}