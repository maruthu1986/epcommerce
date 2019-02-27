/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.views;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ImageHyperlink;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.CmClientResources;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.EpUiException;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.helpers.CatalogListener;
import com.elasticpath.cmclient.core.helpers.SkuSearchRequestJob;
import com.elasticpath.cmclient.core.search.SafeSearchCodes;
import com.elasticpath.cmclient.core.service.CatalogEventService;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTabFolder;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.service.catalog.CatalogService;
import com.elasticpath.service.catalog.SkuOptionService;
import com.elasticpath.service.search.query.SkuSearchCriteria;
import com.elasticpath.service.search.query.SortOrder;
import com.elasticpath.service.search.query.StandardSortBy;

/**
 * The <code>SkuSearchViewTab</code> is used to search for products, either by providing query strings or by selecting filters.
 */
@SuppressWarnings({"PMD.TooManyMethods", "PMD.GodClass"})
public class SkuSearchViewTab extends AbstractCatalogSearchViewTab implements SelectionListener, CatalogListener {
	/**
	 * SearchView ID specified in the plugin.xml file. It is the same as the class name
	 */
	public static final String VIEW_ID = SkuSearchViewTab.class.getName();

	private static final transient Logger LOG = Logger.getLogger(SkuSearchViewTab.class);

	private static final int NORMAL_TEXT_LENGTH = 255;

	private static final String EMPTY_STRING = ""; //$NON-NLS-1$

	private final SkuSearchRequestJob searchJob;

	private SkuSearchCriteria searchCriteria;

	private Text productNameText;

	private Text productCodeText;

	private Text skuCodeText;

	private CCombo brandCombo;

	private CCombo catalogCombo;

	private Button activeOnlyCheckbox;

	private final IEpLayoutComposite tabComposite;

	private final CatalogSearchView parent;

	private List<SkuSearchSkuOptionFilterSection> skuOptionSections;

	private final SkuOptionService skuOptionService;

	private final CatalogService catalogService;

	private final IEpTabFolder tabFolder;

	private IEpLayoutComposite skuOptionFiltersComposite;

	/**
	 * The constructor.
	 *
	 * @param tabComposite      the tab composite
	 * @param catalogSearchView the product search view which contains this tab
	 * @param tabFolder         the tabFolder
	 */
	public SkuSearchViewTab(final IEpLayoutComposite tabComposite, final CatalogSearchView catalogSearchView, final IEpTabFolder tabFolder) {
		this.tabComposite = tabComposite;
		this.parent = catalogSearchView;
		this.tabFolder = tabFolder;


		searchJob = new SkuSearchRequestJob();
		skuOptionService = ServiceLocator.getService(ContextIdNames.SKU_OPTION_SERVICE);
		catalogService = ServiceLocator.getService(ContextIdNames.CATALOG_SERVICE);

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
		this.createSkuOptionFiltersGroup(tabComposite);
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
		IEpLayoutComposite searchTermsGroup = parentComposite.addGroup(CatalogMessages.get().SearchView_SearchTermsGroup, 1, false, data);

		// sku code text field
		searchTermsGroup.addLabelBold(CatalogMessages.get().SearchView_Search_Label_SkuCode, null);
		this.skuCodeText = searchTermsGroup.addTextField(EpState.EDITABLE, data);
		this.skuCodeText.setTextLimit(NORMAL_TEXT_LENGTH);
		this.skuCodeText.addSelectionListener(this);

		// product name text field
		searchTermsGroup.addLabelBold(CatalogMessages.get().SearchView_Search_Label_ProductName, null);
		this.productNameText = searchTermsGroup.addTextField(EpState.EDITABLE, data);
		this.productNameText.setTextLimit(NORMAL_TEXT_LENGTH);
		this.productNameText.addSelectionListener(this);

		// product code text field
		searchTermsGroup.addLabelBold(CatalogMessages.get().SearchView_Search_Label_ProductCode, null);
		this.productCodeText = searchTermsGroup.addTextField(EpState.EDITABLE, data);
		this.productCodeText.setTextLimit(NORMAL_TEXT_LENGTH);
		this.productCodeText.addSelectionListener(this);
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
		this.catalogCombo.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(final SelectionEvent event) {
				// nothing to do
			}

			@Override
			public void widgetSelected(final SelectionEvent event) {
				notifySkuOptionsForCatalogSelectionChange(getMasterCatalogsForSelectedCatalogs());

			}
		});

		// active-only checkbox
		this.activeOnlyCheckbox = groupComposite.addCheckBoxButton(CatalogMessages.get()
				.SearchView_Filter_Label_SKUActiveOnly, EpState.EDITABLE, data);
		this.activeOnlyCheckbox.setEnabled(true);
		this.activeOnlyCheckbox.setSelection(true);

	}

	/**
	 * notify sku options for the changes of catalog selection.
	 *
	 * @param selectedCatalogs the list of selected catalogs
	 */
	protected void notifySkuOptionsForCatalogSelectionChange(final List<Catalog> selectedCatalogs) {
		regenerateSkuOptionFilterSection(getSkuOptionsForCatalogs(selectedCatalogs));
	}

	private void regenerateSkuOptionFilterSection(final List<SkuOption> skuOptions) {
		removeExistSkuOptionSection();

		createAndPopulateOneSkuOptionSection(skuOptionFiltersComposite, skuOptions);
	}

	private void removeExistSkuOptionSection() {
		for (SkuSearchSkuOptionFilterSection skuOptionSection : skuOptionSections) {
			skuOptionSection.dispose();
		}
		skuOptionSections.clear();
	}

	/**
	 * Get the unique list of sku options for the given catalogs.
	 *
	 * @param catalogs the catalogs to look at
	 * @return a list of sku options
	 */
	protected List<SkuOption> getSkuOptionsForCatalogs(final List<Catalog> catalogs) {
		Set<SkuOption> skuOptions = new HashSet<>();
		for (Catalog catalog : catalogs) {
			skuOptions.addAll(skuOptionService.findAllSkuOptionFromCatalog(catalog.getUidPk()));
		}
		return new ArrayList<>(skuOptions);
	}

	/*
	 * Creates the filters group to filter search result by Brand or Catalogue.
	 */
	private void createSkuOptionFiltersGroup(final IEpLayoutComposite parentComposite) {

		skuOptionSections = new ArrayList<>();

		final IEpLayoutComposite groupComposite = parentComposite.addGroup(CatalogMessages.get().SearchView_SkuOptionFiltersGroup, 1, false, null);

		skuOptionFiltersComposite = groupComposite.addGridLayoutComposite(1, false,
				groupComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true));

		addSkuOptionFilter(skuOptionFiltersComposite);

		IEpLayoutData hyperLinkLayoutData = groupComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.BEGINNING);

		IEpLayoutComposite hyperLinkComposite = groupComposite.addGridLayoutComposite(2, false, hyperLinkLayoutData);

		IHyperlinkListener addAnotherFilterListener = new IHyperlinkListener() {
			@Override
			public void linkActivated(final HyperlinkEvent event) {
				createAndPopulateOneSkuOptionSection(skuOptionFiltersComposite, getSkuOptionsForCatalogs(getMasterCatalogsForSelectedCatalogs()));
			}

			@Override
			public void linkEntered(final HyperlinkEvent event) {
				//nothing to do
			}

			@Override
			public void linkExited(final HyperlinkEvent event) {
				//nothing to do
			}
		};

		ImageHyperlink imageHyperLink = hyperLinkComposite.addHyperLinkImage(CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_PLUS),
				EpState.EDITABLE, hyperLinkLayoutData);
		imageHyperLink.addHyperlinkListener(addAnotherFilterListener);


		final Hyperlink addAnotherFilterLink = hyperLinkComposite.addHyperLinkText(CatalogMessages.get().SearchView_Add_Another_SkuOptionFilter_Label,
				EpState.EDITABLE, hyperLinkLayoutData);

		addAnotherFilterLink.setForeground(CmClientResources.getColor(CmClientResources.COLOR_BLUE));
		addAnotherFilterLink.setUnderlined(true);

		addAnotherFilterLink.addHyperlinkListener(addAnotherFilterListener);
	}

	private SkuSearchSkuOptionFilterSection addSkuOptionFilter(final IEpLayoutComposite skuOptionFiltersComposite) {
		SkuSearchSkuOptionFilterSection newSkuOptionFilterSection = new SkuSearchSkuOptionFilterSection(skuOptionFiltersComposite, this);
		skuOptionSections.add(newSkuOptionFilterSection);
		hideOrDisplayRemoveButton();
		return newSkuOptionFilterSection;
	}

	/**
	 * Populate all controls on this view.
	 */
	private void populateControls() {
		populateBrandCombo(brandCombo);
		configureStandardComboWidth(brandCombo);

		populateCatalogCombo(catalogCombo);
		configureStandardComboWidth(catalogCombo);

		populateSkuOptionSection(skuOptionSections, getSkuOptionsForCatalogs(getMasterCatalogsForSelectedCatalogs()));
		for (SkuSearchSkuOptionFilterSection skuOptionSection : skuOptionSections) {
			configureStandardComboWidth(skuOptionSection.getSkuOptionCombo());
		}

		initialSettingsForSearchCriteria();
	}

	private void populateSkuOptionSection(final List<SkuSearchSkuOptionFilterSection> skuOptionSections, final List<SkuOption> skuOptions) {
		for (SkuSearchSkuOptionFilterSection skuOptionSection : skuOptionSections) {
			populateSkuOptionSection(skuOptionSection, skuOptions);
		}
	}

	private void populateSkuOptionSection(final SkuSearchSkuOptionFilterSection skuOptionSection, final List<SkuOption> skuOptions) {
		skuOptionSection.populateControls(skuOptions);
	}

	private void initialSettingsForSearchCriteria() {
		//initial search criteria
		getModel().setCatalogCodes(getCatalogCodesForSearch(getInitializeAuthorizedCatalogsList()));
		getModel().setCatalogSearchableLocales(getCatalogSearchableLocales(getInitializeAuthorizedCatalogsList()));
	}

	/**
	 * The controls of the search tab should be bound here.
	 */
	protected void bindControls() {
		// bind product name
		bind(productNameText, getModel(), "productName", null, null); //$NON-NLS-1$
		// bind product code
		bind(productCodeText, getModel(), "productCode", null, null); //$NON-NLS-1$
		// bind sku code
		bind(skuCodeText, getModel(), "skuCode", null, null); //$NON-NLS-1$

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
	protected SkuSearchCriteria getModel() {
		if (searchCriteria == null) {
			searchCriteria = ServiceLocator.getService(ContextIdNames.SKU_SEARCH_CRITERIA);
			// Set default sorting criteria
			searchCriteria.setSortingOrder(SortOrder.ASCENDING);
			searchCriteria.setSortingType(StandardSortBy.SKU_CODE);
		}
		return searchCriteria;
	}

	/**
	 * Fire the index search.
	 *
	 * @param searchCriteria the product search criteria
	 */
	protected void doSearch(final SkuSearchCriteria searchCriteria) {
		this.searchJob.setSource(this);
		this.searchJob.setSearchCriteria(searchCriteria);
		this.searchJob.executeSearch(null);
	}

	private void collectSkuOptions() {
		searchCriteria.clearSkuOptionAndValues();
		for (SkuSearchSkuOptionFilterSection skuOptionSection : skuOptionSections) {
			SkuOption selectedSkuOption = skuOptionSection.getSelectedSkuOption();
			if (selectedSkuOption != null) {
				Set<String> selectedSkuOptionValues = skuOptionSection.getSelectedSkuOptionValues();
				searchCriteria.addSkuOptionAndValues(selectedSkuOption.getOptionKey(), selectedSkuOptionValues);
			}
		}
	}

	private void checkView() {
		try {
			parent.getSite().getWorkbenchWindow().getActivePage().showView(SearchSkuListView.PART_ID);
		} catch (final PartInitException e) {
			// Log the error and throw an unchecked exception
			LOG.error(e.getStackTrace());
			throw new EpUiException("Fail to reopen product list view.", e); //$NON-NLS-1$
		}
	}

	/**
	 * This method is used for testing.
	 *
	 * @return the skuCodeText
	 */
	protected Text getSkuCodeText() {
		return skuCodeText;
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

	/**
	 * Called when a widget is default selected. For text fields hitting ENTER calls this method.
	 *
	 * @param event the event.
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

	@Override
	public void clear() {
		this.productCodeText.setText(EMPTY_STRING);
		this.skuCodeText.setText(EMPTY_STRING);
		this.productNameText.setText(EMPTY_STRING);
		this.brandCombo.select(getAllFilterIndex());
		this.catalogCombo.select(getAllFilterIndex());
		this.activeOnlyCheckbox.setSelection(false);

		//create new sku option filter section
		this.regenerateSkuOptionFilterSection(getSkuOptionsForCatalogs(getMasterCatalogsForSelectedCatalogs()));

		this.searchCriteria.clear();

		initialSettingsForSearchCriteria();
	}

	@Override
	public void search() {
		checkView();

		this.getDataBindingContext().updateModels();

		searchCriteria.setLocale(CorePlugin.getDefault().getDefaultLocale());

		collectSkuOptions();

		doSearch(this.searchCriteria);
	}

	@Override
	public void catalogChanged(final ItemChangeEvent<Catalog> event) {
		this.populateCatalogCombo(catalogCombo);
	}

	/**
	 * Get the list of master catalogs associated with each of the
	 * given list of catalogs.
	 *
	 * @param catalogs a list of catalogs to find masters of
	 * @return the list of master catalogs
	 */
	protected List<Catalog> getMasterCatalogs(final List<Catalog> catalogs) {
		Set<Catalog> masters = new HashSet<>();
		for (Catalog catalog : catalogs) {
			masters.addAll(getMasterCatalogs(catalog));
		}
		return new ArrayList<>(masters);
	}

	/**
	 * Get the list of master catalogs associated with the given catalog.
	 *
	 * @param catalog the catalog to expand
	 * @return a list of master catalogs
	 */
	protected List<Catalog> getMasterCatalogs(final Catalog catalog) {
		List<Catalog> masters = new ArrayList<>();
		if (catalog.isMaster()) {
			masters.add(catalog);
		} else {
			masters.addAll(catalogService.findMastersUsedByVirtualCatalog(catalog.getCode()));
		}
		return masters;
	}

	/**
	 * Is the sku option filter section removable.
	 *
	 * @return true if more than one sku option filter section exist
	 */
	public boolean isSkuOptionFilterSectionRemovable() {
		return skuOptionSections.size() > 1;
	}


	/**
	 * refresh the tab folder.
	 */
	public void refreshUI() {
		tabFolder.layout();
	}

	/**
	 * remove the sku option filter section from the list.
	 *
	 * @param skuSearchSkuOptionFilterSection the sku option filter section to be removed
	 */
	public void removeSection(final SkuSearchSkuOptionFilterSection skuSearchSkuOptionFilterSection) {
		skuOptionSections.remove(skuSearchSkuOptionFilterSection);
		hideOrDisplayRemoveButton();
	}

	private void createAndPopulateOneSkuOptionSection(final IEpLayoutComposite skuOptionFiltersComposite, final List<SkuOption> skuOptions) {
		SkuSearchSkuOptionFilterSection newSkuOptionFilter = addSkuOptionFilter(skuOptionFiltersComposite);
		populateSkuOptionSection(newSkuOptionFilter, skuOptions);

		hideOrDisplayRemoveButton();

		//Refresh the tabFolder
		refreshUI();
		configureStandardComboWidth(newSkuOptionFilter.getSkuOptionCombo());
		
		//This should happen after the UI is refreshed and its height is recalculated.
		tabFolder.scrollToBottom();
	}

	private void hideOrDisplayRemoveButton() {
		if (skuOptionSections.size() > 1) {
			skuOptionSections.get(0).displayRemoveButton();
		} else {
			skuOptionSections.get(0).hideRemoveButton();
		}
	}


	private List<Catalog> getMasterCatalogsForSelectedCatalogs() {
		final Catalog catalog = (Catalog) catalogCombo.getData(catalogCombo.getText());
		if (catalog == null) {
			return getMasterCatalogs(getInitializeAuthorizedCatalogsList(false));
		}
		return getMasterCatalogs(catalog);
	}

}