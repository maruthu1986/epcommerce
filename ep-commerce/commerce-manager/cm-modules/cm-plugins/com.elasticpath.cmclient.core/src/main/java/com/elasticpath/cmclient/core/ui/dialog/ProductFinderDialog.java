/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.ui.dialog; // NOPMD All imports required

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.AssertionFailedException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.cmclient.core.helpers.BrandComparator;
import com.elasticpath.cmclient.core.helpers.LocalCategoryLookup;
import com.elasticpath.cmclient.core.helpers.ProductListener;
import com.elasticpath.cmclient.core.helpers.ProductSearchRequestJob;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.service.CatalogEventService;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpColumnSorterControl;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.core.ui.framework.IEpTableColumn;
import com.elasticpath.cmclient.core.ui.framework.impl.EpColumnSorterControl;
import com.elasticpath.cmclient.core.validation.EpValidatorFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.service.catalog.BrandService;
import com.elasticpath.service.catalog.CatalogService;
import com.elasticpath.service.catalog.CategoryLookup;
import com.elasticpath.service.search.query.ProductSearchCriteria;
import com.elasticpath.service.search.query.SortOrder;
import com.elasticpath.service.search.query.StandardSortBy;

/**
 * This class provides the presentation of product Finder dialog window.
 */
@SuppressWarnings("PMD.CyclomaticComplexity")
public class ProductFinderDialog extends AbstractEpPriceDialog {

	private static final Logger LOG = Logger.getLogger(ProductFinderDialog.class);

	private static final String PRODUCT_FINDER_TABLE = "Product Finder"; //$NON-NLS-1$

	private static final int LEFT_PANEL_WIDTH = 280;

	private static final int COMBO_HORIZONTAL_SPACING = 30;

	private ProductSearchCriteria searchCriteria;

	private final long categoryUidCriteria;

	private boolean searchActiveOnly;

	private final ProductSearchRequestJob searchJob = new ProductSearchRequestJob(true);

	private final IEpColumnSorterControl columnSorterControl;

	private CategoryLookup categoryLookup;

	private static final long NO_CATEGORY = -1;

	private CCombo brandCombo;

	private Button searchButton;

	private Button clearButton;

	private Text productCodeText;

	private Text productNameText;

	private final List<Brand> brandList;

	private final Locale locale = CorePlugin.getDefault().getDefaultLocale();

	private Text skuCodeText;

	private CCombo catalogCombo;

	private final Catalog catalog;

	private final List<Catalog> catalogList;

	/**
	 * @param parentShell the parent shell of this dialog
	 * @param catalog the catalog to search within. If set, only search within this catalog (i.e. disable the catalog combo-box); set to null to
	 *            allow user to choose catalog from combo-box.
	 * @param showPrices true if price section need in dialog
	 */
	public ProductFinderDialog(final Shell parentShell, final Catalog catalog, final boolean showPrices) {
		this(parentShell, NO_CATEGORY, catalog, false, showPrices);
	}

	/**
	 * @param parentShell the parent shell of this dialog
	 * @param onlyMasterCatalogs set to true if only the master catalogs have to be displayed in the combo box
	 * @param showPrices true if price section need in dialog
	 */
	public ProductFinderDialog(final Shell parentShell, final boolean onlyMasterCatalogs, final boolean showPrices) {
		this(parentShell, NO_CATEGORY, null, onlyMasterCatalogs, showPrices);
	}

	/**
	 * This constructor is the same as the default constructor, but takes in a category uid. <br>
	 * This product finder dialog will only search for products within the corresponding category.
	 *
	 * @param parentShell the parent shell of this dialog
	 * @param categoryUid the category uid
	 * @param catalog the current catalog. If it is set, the catalog combo box will be selected to current catalog and disabled. <br>
	 *            Set it to null if you allow user to choose catalog from the combo box.
	 * @param showPrices true if price section need in dialog
	 */
	public ProductFinderDialog(final Shell parentShell, final long categoryUid, final Catalog catalog, final boolean showPrices) {
		this(parentShell, categoryUid, catalog, false, showPrices);
	}

	/**
	 * This constructor is the same as the default constructor, but takes in a category uid. <br>
	 * This product finder dialog will only search for products within the corresponding category.
	 *
	 * @param catalog the current catalog. If it is set, the catalog combo box will be selected to current catalog and disabled.
	 */
	public ProductFinderDialog(final Catalog catalog) {
		this(null, NO_CATEGORY, catalog, false, false, false);
	}

	/**
	 * This constructor is the same as the default constructor, but takes in a category uid. <br>
	 * This product finder dialog will only search for products within the corresponding category.
	 *
	 * @param parentShell the parent shell of this dialog
	 * @param categoryUid the category uid
	 * @param catalog the current catalog. If it is set, the catalog combo box will be selected to current catalog and disabled.<br>
	 *            Set it to null if you allow user to choose catalog from the combo box.
	 * @param onlyMasterCatalogs set to true if only the master catalogs have to be displayed in the combo box
	 * @param showPrices true if price section need in dialog
	 */
	protected ProductFinderDialog(final Shell parentShell,
								  final long categoryUid,
								  final Catalog catalog,
								  final boolean onlyMasterCatalogs,
								  final boolean showPrices) {
		this(parentShell, categoryUid, catalog, onlyMasterCatalogs, showPrices, true);
	}

	/**
	 * Default constructor.
	 *
	 * @param parentShell the parent shell of this dialog
	 * @param categoryUid the category uid
	 * @param catalog the current catalog. If it is set, the catalog combo box will be selected to current catalog and disabled. <br>
	 *            Set it to null if you allow user to choose catalog from the combo box.
	 * @param onlyMasterCatalogs set to true if only the master catalogs have to be displayed in the combo box
	 * @param showPrices true if price section need in dialog
	 * @param checkAuthorization if true, check if the user has authorization for the provided catalogs
	 */
	protected ProductFinderDialog(final Shell parentShell,
								  final long categoryUid,
								  final Catalog catalog,
								  final boolean onlyMasterCatalogs,
								  final boolean showPrices,
								  final boolean checkAuthorization) {
		super(parentShell, showPrices);

		this.catalog = catalog;
		final BrandService brandService = ServiceLocator.getService(ContextIdNames.BRAND_SERVICE);

		final List<Brand> brands;
		if (catalog == null) {
			catalogList = getCatalogs(onlyMasterCatalogs);
			brands = brandService.list();
		} else {
			// do not need the full list of catalogs if we are searching within a specified catalog
			catalogList = new ArrayList<>();
			catalogList.add(catalog);
			brands = brandService.findAllBrandsFromCatalog(catalog.getUidPk());
		}

		if (checkAuthorization) {
			AuthorizationService.getInstance().filterAuthorizedCatalogs(catalogList);
		}

		if (catalogList.isEmpty()) {
			setNoDataAvailable(true);
		}

		brandList = new ArrayList<>(brands);
		Collections.sort(brandList, new BrandComparator(locale));
		searchActiveOnly = false;
		categoryUidCriteria = categoryUid;

		columnSorterControl = new EpColumnSorterControl(searchJob);
	}

	/**
	 * Gets all the catalogs or only the master catalogs.
	 */
	private List<Catalog> getCatalogs(final boolean onlyMasterCatalogs) {
		if (onlyMasterCatalogs) {
			return ((CatalogService) (ServiceLocator.getService(ContextIdNames.CATALOG_SERVICE))).findMasterCatalogs();
		}
		return ((CatalogService) (ServiceLocator.getService(ContextIdNames.CATALOG_SERVICE))).findAllCatalogs();
	}

	@Override
	protected void createEpDialogContent(final IEpLayoutComposite dialogComposite) {
		final IEpLayoutComposite mainComposite = dialogComposite.addGridLayoutComposite(2, false,
				dialogComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true));

		final IEpLayoutComposite leftPaneComposite = mainComposite.addGridLayoutComposite(1, false,
				mainComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true));

		final IEpLayoutComposite scrolledLeftPaneComposite = leftPaneComposite.addScrolledGridLayoutComposite(1, true, true,
				leftPaneComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true));

		((GridData) leftPaneComposite.getSwtComposite().getLayoutData()).widthHint = LEFT_PANEL_WIDTH;
		scrolledLeftPaneComposite.getSwtComposite().setLayoutData(leftPaneComposite.getSwtComposite().getLayoutData());

		final IEpLayoutComposite searchGroupComposite = scrolledLeftPaneComposite.addGroup(CoreMessages.get().ProductFinderDialog_Search, 1, false,
				scrolledLeftPaneComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false));
		final IEpLayoutData textFieldLayoutData = searchGroupComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true);

		searchGroupComposite.addLabelBold(CoreMessages.get().ProductFinderDialog_ProductName, null);
		productNameText = searchGroupComposite.addTextField(EpState.EDITABLE, textFieldLayoutData);

		searchGroupComposite.addLabelBold(CoreMessages.get().ProductFinderDialog_ProductCode, null);
		productCodeText = searchGroupComposite.addTextField(EpState.EDITABLE, textFieldLayoutData);

		searchGroupComposite.addLabelBold(CoreMessages.get().ProductFinderDialog_ProductSKU, null);
		skuCodeText = searchGroupComposite.addTextField(EpState.EDITABLE, textFieldLayoutData);

		// Brand and Catalog filter group
		final IEpLayoutComposite filtersGroupComposite = scrolledLeftPaneComposite.addGroup(CoreMessages.get().ProductFinderDialog_Filters, 1, false,
				scrolledLeftPaneComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false));
		final IEpLayoutData labelLayoutData = filtersGroupComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.FILL, false, false);

		GridLayout comboLayout = new GridLayout();
		comboLayout.horizontalSpacing = COMBO_HORIZONTAL_SPACING;

		filtersGroupComposite.addLabelBold(CoreMessages.get().ProductFinderDialog_Brand, labelLayoutData);
		brandCombo = filtersGroupComposite.addComboBox(EpState.EDITABLE,
				filtersGroupComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false));
		brandCombo.setLayout(comboLayout);

		filtersGroupComposite.addLabelBold(CoreMessages.get().ProductFinderDialog_Catalog, labelLayoutData);
		catalogCombo = filtersGroupComposite.addComboBox(EpState.EDITABLE,
				filtersGroupComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false));
		catalogCombo.setEnabled(false);
		catalogCombo.setLayout(comboLayout);

		//Search and Clear buttons
		final IEpLayoutComposite buttonsComposite = leftPaneComposite.addGridLayoutComposite(2, true,
				searchGroupComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL));

		searchButton = buttonsComposite.addPushButton(CoreMessages.get().ProductFinderDialog_Search,
				CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_SEARCH_ACTIVE),
				EpState.EDITABLE,
				null);
		clearButton = buttonsComposite.addPushButton(CoreMessages.get().ProductFinderDialog_Clear, EpState.EDITABLE, null);

		// Product result table tab
		IEpLayoutComposite resultPaneComposite = createResultPaneComposite(mainComposite);
		createPaginationCompositeControl(resultPaneComposite);
		createErrorMessageControl(resultPaneComposite);
		createTableViewControl(resultPaneComposite,
				resultPaneComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false), PRODUCT_FINDER_TABLE);
		createPriceTableViewer(resultPaneComposite);

		// the first column is for images

		final int columnWidthEmpty = 28;
		final int columnWidthProductCode = 118;
		final int columnWidthProductName = 227;
		final int columnWidthBrand = 125;

		getResultTableViewer().addTableColumn(CoreMessages.EMPTY_STRING, columnWidthEmpty);
		IEpTableColumn tableColumn = getResultTableViewer()
				.addTableColumn(CoreMessages.get().ProductFinderDialog_ProductCode, columnWidthProductCode);
		columnSorterControl.registerTableColumn(tableColumn.getSwtTableColumn(), StandardSortBy.PRODUCT_CODE);
		tableColumn = getResultTableViewer().addTableColumn(CoreMessages.get().ProductFinderDialog_ProductName, columnWidthProductName);
		columnSorterControl.registerTableColumn(tableColumn.getSwtTableColumn(), StandardSortBy.PRODUCT_NAME_NON_LC);
		tableColumn = getResultTableViewer().addTableColumn(CoreMessages.get().ProductFinderDialog_Brand, columnWidthBrand);
		columnSorterControl.registerTableColumn(tableColumn.getSwtTableColumn(), StandardSortBy.BRAND_NAME);

		getNavigationService().registerNavigationEventListener(this);
	}

	@Override
	public void populateControls() {
		searchButton.addSelectionListener(this);
		clearButton.addSelectionListener(this);

		productNameText.addFocusListener(this);
		productCodeText.addFocusListener(this);
		brandCombo.addFocusListener(this);
		skuCodeText.addFocusListener(this);

		productNameText.addSelectionListener(this);
		productCodeText.addSelectionListener(this);
		brandCombo.addSelectionListener(this);
		skuCodeText.addSelectionListener(this);

		getResultTableViewer().setContentProvider(new ViewContentProvider());
		getResultTableViewer().setLabelProvider(new ViewLabelProvider());
		getResultTableViewer().getSwtTableViewer().addSelectionChangedListener(this);

		productNameText.setFocus();

		brandCombo.add(CoreMessages.get().SearchView_Filter_Brand_All);
		for (Brand currBrand : brandList) {
			brandCombo.add(String.valueOf(currBrand.getDisplayName(CorePlugin.getDefault().getDefaultLocale(), true)));
		}
		brandCombo.setText(CoreMessages.get().SearchView_Filter_Brand_All);

		populateCatalogCombo();
	}

	@Override
	protected String getInitialMessage() {
		if (categoryUidCriteria == NO_CATEGORY) {
			return CoreMessages.get().ProductFinderDialog_FindAProduct;
		}

		// if searching in a specific category, show a different initial message
		return
			NLS.bind(CoreMessages.get().ProductFinderDialog_ByCategory_FindAProduct,
			new Object[]{getCategoryLookup().findByUid(categoryUidCriteria)
				.getDisplayName(CorePlugin.getDefault().getDefaultLocale()) });
	}

	@Override
	protected String getTitle() {
		if (categoryUidCriteria == NO_CATEGORY) {
			return CoreMessages.get().ProductFinderDialog_Title;
		}

		// if searching in a specific category, show a different title
		return
			NLS.bind(CoreMessages.get().ProductFinderDialog_ByCategory_Title,
			new Object[]{getCategoryLookup().findByUid(categoryUidCriteria).getDisplayName(CorePlugin.getDefault().getDefaultLocale())});
	}

	@Override
	protected String getWindowTitle() {
		if (categoryUidCriteria == NO_CATEGORY) {
			return CoreMessages.get().ProductFinderDialog_WindowTitle;
		}

		// if searching in a specific category, show a different window title
		return
			NLS.bind(CoreMessages.get().ProductFinderDialog_ByCategory_WindowTitle,
			new Object[]{getCategoryLookup().findByUid(categoryUidCriteria)
				.getDisplayName(CorePlugin.getDefault().getDefaultLocale()) });
	}

	/**
	 * Called when a widget is default selected. For text fields hitting ENTER calls this method.
	 *
	 * @param event selection event
	 */
	public void widgetDefaultSelected(final SelectionEvent event) {
		if (event.getSource() instanceof Text || event.getSource() instanceof CCombo) {
			processSearch();
		}
	}

	/**
	 * @param event the SelectionEvent object
	 */
	public void widgetSelected(final SelectionEvent event) {
		if (event.getSource() == searchButton) {
			processSearch();
		} else if (event.getSource() == clearButton) {
			clearPriceInfo();
			this.clearFields();
		}
	}

	@Override
	public void selectionChanged(final SelectionChangedEvent event) {
		final Object selectedItem = ((IStructuredSelection) event.getSelection()).getFirstElement();
		if (selectedItem instanceof Product) {
			setMessage(getInitialMessage());
			setErrorMessage(null);
			setSelectedObject(selectedItem);
			showPriceInfo(selectedItem);
			getOkButton().setEnabled(true);
		}
	}

	@Override
	protected String getBaseAmountObjectType(final Object selectedItem) {
		return PRODUCT_TYPE;
	}

	@Override
	public ProductSearchCriteria getModel() {
		if (searchCriteria == null) {
			searchCriteria = (ProductSearchCriteria) ServiceLocator.getService(ContextIdNames.PRODUCT_SEARCH_CRITERIA);
		}
		return searchCriteria;
	}

	@Override
	protected void clearFields() {
		productCodeText.setText(CoreMessages.EMPTY_STRING);
		skuCodeText.setText(CoreMessages.EMPTY_STRING);
		productNameText.setText(CoreMessages.EMPTY_STRING);
		brandCombo.select(0);
		catalogCombo.select(0);
		getModel().setBrandCode(null);
		getResultTableViewer().getSwtTableViewer().setInput(null);
		// Set default sorting criteria
		getModel().setSortingOrder(SortOrder.ASCENDING);
		getModel().setSortingType(StandardSortBy.PRODUCT_CODE);
		clearNavigation();
		columnSorterControl.clear();
	}

	@Override
	protected void bindControls() {

		final DataBindingContext bindingContext = new DataBindingContext();
		// bind product code
		EpControlBindingProvider.getInstance().bind(bindingContext, productNameText, this.getModel(), "productName", null, null, false); //$NON-NLS-1$

		// bind product name
		EpControlBindingProvider.getInstance().bind(bindingContext,
				productCodeText,
				this.getModel(),
				"productCode", EpValidatorFactory.PRODUCT_CODE_NOT_REQUIRED, null, false); //$NON-NLS-1$

		// bind sku code
		EpControlBindingProvider.getInstance().bind(bindingContext,
				skuCodeText,
				this.getModel(),
				"productSku", EpValidatorFactory.SKU_CODE_NOT_REQURED, null, false); //$NON-NLS-1$

		// Bind Brand
		final ObservableUpdateValueStrategy brandUpdateStrategy = new ObservableUpdateValueStrategy() {
			// -- Create a custom update strategy to update the model based on the selected brand
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				if (brandCombo.getSelectionIndex() == 0) {
					ProductFinderDialog.this.getModel().setBrandCode(null);
				} else {
					Brand selectedBrand = brandList.get(brandCombo.getSelectionIndex() - 1);
					ProductFinderDialog.this.getModel().setBrandCode(selectedBrand.getCode());
				}
				return Status.OK_STATUS;
			}
		};
		EpControlBindingProvider.getInstance().bind(bindingContext, brandCombo, null, null, brandUpdateStrategy, false);
		createBinding(bindingContext, searchButton);

		// Bind Catalog
		final ObservableUpdateValueStrategy catalogUpdateStrategy = new ObservableUpdateValueStrategy() {
			// -- Create a custom update strategy to update the model based on the selected brand
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				setCatalogSearchCriteria();
				return Status.OK_STATUS;
			}
		};
		EpControlBindingProvider.getInstance().bind(bindingContext, catalogCombo, null, null, catalogUpdateStrategy, false);
		createBinding(bindingContext, searchButton);
	}

	@Override
	protected void doSearch() {
		disableButtons();
		searchButton.setEnabled(false);
		clearPriceInfo();
		searchJob.executeSearchFromIndex(getShell(), getResultsStartIndex());
	}

	@Override
	protected String getMsgForNoResultFound() {
		return CoreMessages.get().ProductFinderDialog_NoResultsFound;
	}

	@Override
	@SuppressWarnings({ "PMD.UselessOverridingMethod" })
	protected void okPressed() {
		/**
		 * To be completed while integrated with other components.
		 */
		final IStructuredSelection selection = (IStructuredSelection) getResultTableViewer().getSwtTableViewer().getSelection();
		if (selection == null || selection.isEmpty()) {
			setErrorMessage(CoreMessages.get().ProductFinderDialog_ErrorMsg_SelectPro);
			return;
		}
		if (selection.getFirstElement() instanceof Product) {
			final Product product = (Product) selection.getFirstElement();
			setSelectedObject(product);
		}
		super.okPressed();
	}

	/**
	 * Process the search criteria before requesting for index search and call for index search.
	 */
	public void processSearch() {
		// check whether or not to search within a category
		if (categoryUidCriteria != NO_CATEGORY) {
			// add category uid to the search criteria
			final Set<Long> categoryUids = new HashSet<Long>();
			categoryUids.add(new Long(categoryUidCriteria));
			this.getModel().setAncestorCategoryUids(categoryUids);
			this.getModel().setDirectCategoryUid(categoryUidCriteria);
			this.getModel().setOnlyWithinDirectCategory(false);
		}

		// check whether or not to search only for active products
		this.getModel().setActiveOnly(searchActiveOnly);

		if (catalogCombo.getSelectionIndex() == 0) {
			setCatalogSearchCriteria();
		}

		searchCriteria.setLocale(CorePlugin.getDefault().getDefaultLocale());
		searchJob.setSearchCriteria(this.getModel());

		// Set default sorting criteria
		getModel().setSortingOrder(SortOrder.ASCENDING);
		getModel().setSortingType(StandardSortBy.PRODUCT_CODE);

		setErrorMessage(null);
		setResultsStartIndex(0);

		doSearch();
	}

	/**
	 * Set the activeOnly flag to <code>true</code> if this <code>ProductFinderDialog</code> box should only search for active products (i.e.
	 * Products whose enable date is before the current date and whose disable date, if set, is after the current date). <br>
	 * Set it to <code>false</code> if this <code>ProductFinderDialog</code> box should search for both active and inactive products.
	 *
	 * @param activeOnly set to <code>true</code> to search for active products only
	 */
	public void setSearchActiveProductsOnly(final boolean activeOnly) {
		searchActiveOnly = activeOnly;
	}

	@Override
	protected CCombo getCatalogCombo() {
		return catalogCombo;
	}

	@Override
	protected Collection<Catalog> getCatalogs() {
		return catalogList;
	}

	@Override
	protected Catalog getCatalog() {
		return catalog;
	}

	@Override
	protected void bindCatalogSearchableLocalesToModel(
			final Set<Locale> searchableLocales) {
		searchCriteria.setCatalogSearchableLocales(searchableLocales);

	}

	/**
	 * Lazy loads a category lookup.
	 * @return a category lookup
	 */
	protected CategoryLookup getCategoryLookup() {
		if (categoryLookup == null) {
			categoryLookup = new LocalCategoryLookup();
		}
		return categoryLookup;
	}

	/**
	 * This label provider returns the text displayed in each column for a given <code>Product</code> object. <br>
	 * This also determines the icon to be displayed for each corresponding product at the first column.
	 */
	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		private static final int COLUMN_INDEX_FIRST = 0;

		private static final int COLUMN_INDEX_PRODUCT_CODE = 1;

		private static final int COLUMN_INDEX_NAME = 2;

		private static final int COLUMN_INDEX_BRAND = 3;

		/**
		 * Get the column image.
		 *
		 * @param element the element object to be displayed.
		 * @param columnIndex the column index.
		 * @return the column image object.
		 */
		public Image getColumnImage(final Object element, final int columnIndex) {
			if (columnIndex == 0) {
				final Product product = (Product) element;
				return CoreImageRegistry.getSmallImageForProduct(product);
			}

			return null;
		}

		/**
		 * Get column text.
		 *
		 * @param element the product element object to be displayed by the table.
		 * @param columnIndex the column index.
		 * @return the column text content string.
		 */
		public String getColumnText(final Object element, final int columnIndex) {
			final Product product = (Product) element;

			switch (columnIndex) {
				case COLUMN_INDEX_FIRST:
					if (product.hasMultipleSkus()) {
						return CoreMessages.get().ProductFinderDialog_MultiSku;
					}
					return CoreMessages.get().ProductFinderDialog_SingleSku;
				case COLUMN_INDEX_PRODUCT_CODE:
					return product.getCode();
				case COLUMN_INDEX_NAME:
					return product.getDisplayName(locale);
				case COLUMN_INDEX_BRAND:
					if (product.getBrand() == null) {
						return CoreMessages.get().NotAvailable;
					}
					return product.getBrand().getDisplayName(locale, true);
				default:
					return CoreMessages.get().NotAvailable;
			}
		}
	}

	/**
	 * The content provider class is responsible for providing objects to the view. <br>
	 * It wraps existing objects in adapters or simply return objects as-is. <br>
	 * These objects may be sensitive to the current input of the view, or ignore it and always show the same content.
	 */
	class ViewContentProvider implements IStructuredContentProvider, ProductListener {
		/**
		 * The default constructor.
		 */
		ViewContentProvider() {
			CatalogEventService.getInstance().addProductListener(this);
		}

		/**
		 * Input changed action.
		 *
		 * @param viewer the table viewer
		 * @param oldInput the old input of the table.
		 * @param newInput the new input of the table
		 */
		public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
			// do nothing
		}

		/**
		 * Dispose action.
		 */
		public void dispose() {
			CatalogEventService.getInstance().removeProductListener(this);
		}

		/**
		 * @param inputElement the input element of the table.
		 * @return the product element array to be displayed in the table.
		 */
		public Object[] getElements(final Object inputElement) {
			if (inputElement instanceof Object[]) {
				LOG.debug("TableViewer input set to array of Objects"); //$NON-NLS-1$
				return (Object[]) inputElement;
			}
			return new Object[0];
		}

		/**
		 * Process the action while the search results are returned.
		 *
		 * @param event ProductSearchResultEvent object.
		 */
		public void productSearchResultReturned(final SearchResultEvent<Product> event) {
			PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
				public void run() {
					try {
						getResultTableViewer().setInput(event.getItems().toArray());
						setResultsCount(event.getTotalNumberFound());
						setResultsStartIndex(event.getStartIndex());
						ProductSearchRequestJob productSearchRequestJob = (ProductSearchRequestJob) event.getSource();
						columnSorterControl.updateColumnOrder(productSearchRequestJob.getSearchCriteria());
						handleErrorMessage(CoreMessages.get().ProductFinderDialog_NoResultsFound);
						searchButton.setEnabled(true);
						updateNavigationComponents();
					} catch (AssertionFailedException e) {
						LOG.info("Cannot update the view because the user hit ESC or cancel button after the search was complete"); //$NON-NLS-1$
					}
				}
			});
		}

		/**
		 * Process the action while the table item is changed.
		 *
		 * @param event the product change event object.
		 */
		public void productChanged(final ItemChangeEvent<Product> event) {
			final Product changedProduct = event.getItem();

			for (final TableItem currTableItem : getResultTableViewer().getSwtTableViewer().getTable().getItems()) {
				final Product currProduct = (Product) currTableItem.getData();
				if (currProduct.getUidPk() == changedProduct.getUidPk()) {
					currTableItem.setData(changedProduct);
					getResultTableViewer().getSwtTableViewer().refresh();
					break;
				}
			}
		}
	}


}