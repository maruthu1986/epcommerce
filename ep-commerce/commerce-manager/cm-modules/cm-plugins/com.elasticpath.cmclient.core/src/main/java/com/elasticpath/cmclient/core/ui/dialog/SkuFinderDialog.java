/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.ui.dialog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
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
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.core.CoreMessages;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.binding.EpControlBindingProvider;
import com.elasticpath.cmclient.core.binding.ObservableUpdateValueStrategy;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.cmclient.core.helpers.BrandComparator;
import com.elasticpath.cmclient.core.helpers.ProductSkuListener;
import com.elasticpath.cmclient.core.helpers.SkuSearchRequestJob;
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
import com.elasticpath.domain.catalog.ProductBundle;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.service.catalog.BrandService;
import com.elasticpath.service.catalog.CatalogService;
import com.elasticpath.service.search.query.SkuSearchCriteria;
import com.elasticpath.service.search.query.SortOrder;
import com.elasticpath.service.search.query.StandardSortBy;

/**
 * This class provides the presentation of product Finder dialog window.
 */
@SuppressWarnings({ "PMD.ExcessiveImports", "PMD.GodClass" })
public class SkuFinderDialog extends AbstractEpPriceDialog {

	private static final Logger LOG = Logger.getLogger(SkuFinderDialog.class);

	private static final String SKU_FINDER_TABLE = "Sku Finder"; //$NON-NLS-1$
	private static final int LEFT_PANEL_WIDTH = 280;

	private IEpColumnSorterControl columnSorterControl;

	private static final int OPTIMAL_TABLE_HEIGHT = 100;

	@Override
	public boolean isComplete() {
		if (priceSelectionRequired) {
			return super.isComplete() && isPriceSelected();
		}

		return super.isComplete();
	}

	private boolean isPriceSelected() {
		if (!isShowPriceListSection() || getSelectedItemPriceSummary() != null) {
			return true;
		}
		return false;
	}

	/**
	 * This label provider returns the text displayed in each column for a given <code>Product</code> object.<br>
	 * This also determines the icon to be displayed for each corresponding product at the first column.
	 */
	@SuppressWarnings({ "PMD.CyclomaticComplexity" })
	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {

		private static final int COLUMN_INDEX_PRODUCT_CODE = 1;

		private static final int COLUMN_INDEX_NAME = 2;

		private static final int COLUMN_INDEX_CONFIG = 3;

		private static final int COLUMN_INDEX_BRAND = 4;

		/**
		 * Get the column image.
		 *
		 * @param element the element object to be displayed.
		 * @param columnIndex the column index.
		 * @return the column image object for the table entry.
		 */
		public Image getColumnImage(final Object element, final int columnIndex) {
			if (columnIndex > 0) {
				return null;
			}

			final ProductSku sku = (ProductSku) element;

			if (sku.getProduct().hasMultipleSkus()) {
				return CoreImageRegistry.getImage(CoreImageRegistry.PRODUCT_SKU_SMALL);
			} else if (sku.getProduct() instanceof ProductBundle) {
				return CoreImageRegistry.getImage(CoreImageRegistry.PRODUCT_BUNDLE_SMALL);
			} else {
				return CoreImageRegistry.getImage(CoreImageRegistry.PRODUCT_SMALL);
			}
		}

		/**
		 * Get column text.
		 *
		 * @param element the product element object to be displayed by the table.
		 * @param columnIndex the column index.
		 * @return the column text content string.
		 */
		@SuppressWarnings({ "PMD.CyclomaticComplexity" })
		public String getColumnText(final Object element, final int columnIndex) {
			final ProductSku sku = (ProductSku) element;
			switch (columnIndex) {
				case 0:
					return ""; //$NON-NLS-1$
				case COLUMN_INDEX_PRODUCT_CODE:
					return sku.getSkuCode();
				case COLUMN_INDEX_NAME:
					return sku.getProduct().getDisplayName(locale);
				case COLUMN_INDEX_CONFIG:
					return sku.getDisplayName(locale);
				case COLUMN_INDEX_BRAND:
					if (sku.getProduct().getBrand() != null) {
						return sku.getProduct().getBrand().getDisplayName(locale, true);
					}
					return CoreMessages.get().NotAvailable;
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
	class ViewContentProvider implements IStructuredContentProvider, ProductSkuListener {
		/**
		 * The default constructor.
		 */
		ViewContentProvider() {
			CatalogEventService.getInstance().addProductSkuListener(this);
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
			CatalogEventService.getInstance().removeProductSkuListener(this);
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
		public void productSkuSearchResultReturned(final SearchResultEvent<ProductSku> event) {

			PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
				public void run() {
					try {
						Table table = getResultTableViewer().getSwtTable();
						if (((GridData) table.getLayoutData()).heightHint == SWT.DEFAULT) {
							((GridData) table.getLayoutData()).heightHint = OPTIMAL_TABLE_HEIGHT;
						}

						getResultTableViewer().setInput(event.getItems().toArray());
						setResultsCount(event.getTotalNumberFound());
						setResultsStartIndex(event.getStartIndex());
						SkuSearchRequestJob skuSearchRequestJob = (SkuSearchRequestJob) event.getSource();
						columnSorterControl.updateColumnOrder(skuSearchRequestJob.getSearchCriteria());
						updateNavigationComponents();
						handleErrorMessage(CoreMessages.get().SkuFinderDialog_NoResultsFound);
						searchButton.setEnabled(true);
					} catch (AssertionFailedException e) {
						LOG.info("Cannot update the view because the user hit ESC or cancel button after the search was trigered"); //$NON-NLS-1$
					}
				}
			});
		}

		/**
		 * Process the action while the table item is changed.
		 *
		 * @param event the product change event object.
		 */
		public void productSkuChanged(final ItemChangeEvent<ProductSku> event) {
			// do nothing
		}

		/**
		 * Return the children object array of the given element.
		 *
		 * @return the children array object.
		 * @param parentElement the parent element object.
		 */
		public Object[] getChildren(final Object parentElement) {
			if (parentElement instanceof Product) {
				final Product product = (Product) parentElement;
				return product.getProductSkus().values().toArray();
			}
			return null;
		}

		/**
		 * Get the element's parent object.
		 *
		 * @return the parent object
		 * @param element the table's element object.
		 */
		public Object getParent(final Object element) {
			// product has no parent
			if (element instanceof ProductSku) {
				final ProductSku sku = (ProductSku) element;
				return sku.getProduct();
			}

			return null;
		}

		/**
		 * Check if this element has children.
		 *
		 * @return true if it has children, or false if it does not have children.
		 * @param element the element to be checked.
		 */
		public boolean hasChildren(final Object element) {
			if (element instanceof Product) {
				return ((Product) element).hasMultipleSkus();
			}
			return false;
		}
	}

	private SkuSearchCriteria searchCriteria;

	private SkuSearchRequestJob searchJob;

	private Locale locale;

	private CCombo brandCombo;

	private Button searchButton;

	private Button clearButton;

	private Text productCodeText;

	private Text productNameText;

	private List<Brand> brandList;

	private final boolean enableNavigation;

	private Text skuCodeText;

	private CCombo catalogCombo;

	private final Catalog currentCatalog;

	private List<Catalog> catalogList;

	private Currency defaultCurrency;

	private boolean priceSelectionRequired = true;

	private static final int FIRST_CATALOG_INDEX = 0;

	private final boolean checkAuthorization;

	/**
	 * Constructor for this class.
	 *
	 * @param parentShell the parent shell of this dialog
	 * @param currentCatalog the current catalog. If it is set, the catalog combo box will be selected to current catalog and disabled. <br>
	 *            Set it to null if you allow user to choose catalog from the combo box.
	 * @param showPrices show or not price section in derived dialog.
	 */
	public SkuFinderDialog(final Shell parentShell, final Catalog currentCatalog, final boolean showPrices) {
		this(parentShell, currentCatalog, null, showPrices, true);
	}

	/**
	 * Constructor for this class.
	 *
	 * @param parentShell the parent shell of this dialog
	 * @param currentCatalog the current catalog. If it is set, the catalog combo box will be selected to current catalog and disabled. <br>
	 *            Set it to null if you allow user to choose catalog from the combo box.
	 * @param showPrices show or not price section in derived dialog.
	 * @param priceSelectionRequired forces price selection if true
	 */
	public SkuFinderDialog(final Shell parentShell, final Catalog currentCatalog, final boolean showPrices, final boolean priceSelectionRequired) {
		this(parentShell, currentCatalog, null, showPrices, true);
		this.priceSelectionRequired = priceSelectionRequired;
	}

	/**
	 * Constructor for this class. Provided in case the currency should not be retrieved from the catalog. <br>
	 * Such case is the order which might have a currency set that is different from the default one in the catalog.
	 *
	 * @param parentShell the parent shell of this dialog
	 * @param currentCatalog the current catalog. If it is set, the catalog combo box will be selected to current catalog and disabled. <br>
	 *            Set it to null if you allow user to choose catalog from the combo box.
	 * @param currency the currency in which the prices should be displayed
	 * @param showPrices show or not price section in derived dialog.
	 * @param priceSelectionRequired forces price selection if true
	 * @param checkAuthorization checks if the user has authorization for the provided catalog
	 */
	public SkuFinderDialog(final Shell parentShell,
						   final Catalog currentCatalog,
						   final Currency currency,
						   final boolean showPrices,
						   final boolean priceSelectionRequired,
						   final boolean checkAuthorization) {
		this(parentShell, currentCatalog, currency, showPrices, checkAuthorization);
		this.priceSelectionRequired = priceSelectionRequired;
	}

	/**
	 * Constructor for this class. Provided in case the currency should not be retrieved from the catalog. <br>
	 * Such case is the order which might have a currency set that is different from the default one in the catalog.
	 *
	 * @param parentShell the parent shell of this dialog
	 * @param currentCatalog the current catalog. If it is set, the catalog combo box will be selected to current catalog and disabled. <br>
	 *            Set it to null if you allow user to choose catalog from the combo box.
	 * @param currency the currency in which the prices should be displayed
	 * @param showPrices show or not price section in derived dialog.
	 */
	public SkuFinderDialog(final Shell parentShell, final Catalog currentCatalog, final Currency currency, final boolean showPrices) {
		this(parentShell, currentCatalog, currency, showPrices, true);
	}

	/**
	 * Constructor for this class. <br>
	 * Provided to make this UI to work with the provided catalog and the defaults.
	 *
	 * @param currentCatalog the current catalog. If it is set, the catalog combo box will be selected to current catalog and disabled. <br>
	 *            Set it to null if you allow user to choose catalog from the combo box.
	 */
	public SkuFinderDialog(final Catalog currentCatalog) {
		this(null, currentCatalog, null, false, false);
	}

	/**
	 * Constructor for this class. <br>
	 * Provided in case the currency should not be retrieved from the catalog. <br>
	 * Such case is the order which might have a currency set that is different from the default one in the catalog.
	 *
	 * @param parentShell the parent shell of this dialog
	 * @param currentCatalog the current catalog. If it is set, the catalog combo box will be selected to current catalog and disabled. <br>
	 *            Set it to null if you allow user to choose catalog from the combo box.
	 * @param currency the currency in which the prices should be displayed
	 * @param showPrices show or not price section in derived dialog.
	 * @param checkAuthorization checks if the user has authorization for the provided catalog
	 */
	public SkuFinderDialog(final Shell parentShell,
						   final Catalog currentCatalog,
						   final Currency currency,
						   final boolean showPrices,
						   final boolean checkAuthorization) {
		super(parentShell, showPrices);
		this.currentCatalog = currentCatalog;
		defaultCurrency = currency;
		this.checkAuthorization = checkAuthorization;
		enableNavigation = true;
	}

	private Currency retrieveCurrency(final Catalog currentCatalog) {
		Currency currency = null;
		if (currentCatalog != null) {
			currency = getPriceListHelperService().getDefaultCurrencyFor(currentCatalog);
		}
		return currency;
	}

	/**
	 * @return the brand service
	 */
	protected BrandService getBrandService() {
		return ServiceLocator.getService(ContextIdNames.BRAND_SERVICE);
	}

	/**
	 * @return the catalog service
	 */
	protected CatalogService getCatalogService() {
		return ServiceLocator.getService(ContextIdNames.CATALOG_SERVICE);
	}

	private void setDefaultCurrency() {
		// check if the default currency is not defined and get the first catalog one
		if (defaultCurrency == null && !catalogList.isEmpty()) {
			defaultCurrency = retrieveCurrency(catalogList.get(FIRST_CATALOG_INDEX));
		}
	}

	private void authorizeByCatalogs() {
		if (checkAuthorization) {
			AuthorizationService.getInstance().filterAuthorizedCatalogs(catalogList);
			if (catalogList.isEmpty()) {
				setNoDataAvailable(true);
			}
		}
	}

	private void populateCatalogsAndBrands(final Catalog currentCatalog) {
		if (currentCatalog == null) {
			catalogList = getCatalogService().findAllCatalogs();
			brandList = getBrandService().list();
		} else {
			catalogList = new ArrayList<>();
			catalogList.add(currentCatalog);
			brandList = getBrandService().findAllBrandsFromCatalog(currentCatalog.getUidPk());
		}
		Collections.sort(brandList, new BrandComparator(locale));
	}

	@Override
	public String getCurrentCurrencyCode() {
		if (defaultCurrency == null) {
			return null;
		}
		return defaultCurrency.getCurrencyCode();
	}

	@Override
	protected void createEpDialogContent(final IEpLayoutComposite dialogComposite) {
		locale = CorePlugin.getDefault().getDefaultLocale();

		populateCatalogsAndBrands(currentCatalog);
		authorizeByCatalogs();
		setDefaultCurrency();
		initColumnSorter();

		final IEpLayoutComposite mainComposite = dialogComposite.addGridLayoutComposite(2, false,
				dialogComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true));

		final IEpLayoutComposite leftPaneComposite = mainComposite.addGridLayoutComposite(1, false,
				mainComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, false, true));

		final IEpLayoutComposite scrolledLeftPaneComposite = leftPaneComposite.addScrolledGridLayoutComposite(1, false, true,
				leftPaneComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, false, true));

		((GridData) leftPaneComposite.getSwtComposite().getLayoutData()).widthHint = LEFT_PANEL_WIDTH;
		scrolledLeftPaneComposite.getSwtComposite().setLayoutData(leftPaneComposite.getSwtComposite().getLayoutData());

		// Search group UI presentation including 3 text fields.
		final IEpLayoutComposite searchGroupComposite = scrolledLeftPaneComposite.addGroup(CoreMessages.get().SkuFinderDialog_Search, 1, false,
				scrolledLeftPaneComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true));
		final IEpLayoutData textFieldLayoutData = mainComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true);
		final IEpLayoutData labelLayoutData = mainComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, false, false);

		searchGroupComposite.addLabelBold(CoreMessages.get().SkuFinderDialog_ProductName, labelLayoutData);
		productNameText = searchGroupComposite.addTextField(EpState.EDITABLE, textFieldLayoutData);

		searchGroupComposite.addLabelBold(CoreMessages.get().SkuFinderDialog_ProductCode, labelLayoutData);
		productCodeText = searchGroupComposite.addTextField(EpState.EDITABLE, textFieldLayoutData);

		searchGroupComposite.addLabelBold(CoreMessages.get().SkuFinderDialog_ProductSkuCode, labelLayoutData);
		skuCodeText = searchGroupComposite.addTextField(EpState.EDITABLE, textFieldLayoutData);

		// Brand filter group and buttons.
		final IEpLayoutComposite filtersGroupComposite = scrolledLeftPaneComposite.addGroup(CoreMessages.get().SkuFinderDialog_Filters, 1, false,
				scrolledLeftPaneComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true));
		final IEpLayoutData comboLayoutData = filtersGroupComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, false);

		filtersGroupComposite.addLabelBold(CoreMessages.get().SkuFinderDialog_Brand, labelLayoutData);
		brandCombo = filtersGroupComposite.addComboBox(EpState.EDITABLE, comboLayoutData);

		filtersGroupComposite.addLabelBold(CoreMessages.get().ProductFinderDialog_Catalog, labelLayoutData);
		catalogCombo = filtersGroupComposite.addComboBox(EpState.EDITABLE, comboLayoutData);
		catalogCombo.setEnabled(false);

		//Search and clear buttons
		final IEpLayoutComposite buttonsComposite = leftPaneComposite.addGridLayoutComposite(2, true,
				dialogComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.FILL));

		searchButton = buttonsComposite.addPushButton(CoreMessages.get().SkuFinderDialog_Search,
				CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_SEARCH_ACTIVE),
				EpState.EDITABLE,
				null);
		clearButton = buttonsComposite.addPushButton(CoreMessages.get().SkuFinderDialog_Clear, EpState.EDITABLE, null);

		// Sku Results Composite
		IEpLayoutComposite resultPaneComposite = createResultPaneComposite(mainComposite);
		if (enableNavigation) {
			createPaginationCompositeControl(resultPaneComposite);
			getNavigationService().registerNavigationEventListener(this);
		}
		createErrorMessageControl(resultPaneComposite);

		createTableViewer(resultPaneComposite);
	}

	private void createTableViewer(final IEpLayoutComposite resultPaneComposite) {
		IEpLayoutData tableViewLayoutData = resultPaneComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true, 1, 1);
		createTableViewControl(resultPaneComposite, tableViewLayoutData, SKU_FINDER_TABLE);
		createPriceTableViewer(resultPaneComposite);

		final int columnWidthSkuResultType = 25;
		final int columnWidthProductCode = 120;
		final int columnWidthProductName = 120;
		final int columnWidthSkuConfig = 120;
		final int columnWidthBrand = 80;

		IEpTableColumn column = getResultTableViewer().addTableColumn("", columnWidthSkuResultType); //$NON-NLS-1$
		columnSorterControl.registerTableColumn(column.getSwtTableColumn(), StandardSortBy.SKU_RESULT_TYPE);
		column = getResultTableViewer().addTableColumn(CoreMessages.get().SkuFinderDialog_ProductSkuCode, columnWidthProductCode);
		columnSorterControl.registerTableColumn(column.getSwtTableColumn(), StandardSortBy.SKU_CODE);
		column = getResultTableViewer().addTableColumn(CoreMessages.get().SkuFinderDialog_ProductName, columnWidthProductName);
		columnSorterControl.registerTableColumn(column.getSwtTableColumn(), StandardSortBy.PRODUCT_NAME_NON_LC);
		column = getResultTableViewer().addTableColumn(CoreMessages.get().SkuFinderDialog_SKU_CONFIGURATION, columnWidthSkuConfig);
		columnSorterControl.registerTableColumn(column.getSwtTableColumn(), StandardSortBy.SKU_CONFIG);
		column = getResultTableViewer().addTableColumn(CoreMessages.get().SkuFinderDialog_Brand, columnWidthBrand);
		columnSorterControl.registerTableColumn(column.getSwtTableColumn(), StandardSortBy.BRAND_NAME);
	}

	private void initColumnSorter() {
		searchJob = new SkuSearchRequestJob();
		columnSorterControl = new EpColumnSorterControl(searchJob);
	}

	@Override
	public void populateControls() {

		getResultTableViewer().setContentProvider(new ViewContentProvider());
		getResultTableViewer().setLabelProvider(new ViewLabelProvider());
		getResultTableViewer().getSwtTableViewer().addSelectionChangedListener(this);

		productNameText.setFocus();

		brandCombo.add(CoreMessages.get().SearchView_Filter_Brand_All);
		for (Brand currBrand : brandList) {
			brandCombo.add(String.valueOf(currBrand.getDisplayName(locale, true)));
		}

		brandCombo.setText(CoreMessages.get().SearchView_Filter_Brand_All);

		populateCatalogCombo();

		if (currentCatalog == null) { // No current catalog specified, allow user to choose
			catalogCombo.setEnabled(true);
		} else {
			catalogCombo.setEnabled(false);
		}

		productNameText.addSelectionListener(this);
		productCodeText.addSelectionListener(this);
		skuCodeText.addSelectionListener(this);
		brandCombo.addSelectionListener(this);

		productNameText.addFocusListener(this);
		productCodeText.addFocusListener(this);
		skuCodeText.addFocusListener(this);
		brandCombo.addFocusListener(this);
		catalogCombo.addFocusListener(this);

		searchButton.addSelectionListener(this);
		clearButton.addSelectionListener(this);

		getOkButton().setEnabled(false);
	}

	@Override
	protected String getInitialMessage() {
		return CoreMessages.get().SkuFinderDialog_SelectASku;
	}

	@Override
	protected String getTitle() {
		return CoreMessages.get().SkuFinderDialog_Title;
	}

	@Override
	protected String getWindowTitle() {
		return CoreMessages.get().SkuFinderDialog_WindowTitle;
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

		setSelectedObject(((IStructuredSelection) event.getSelection()).getFirstElement());

		if (getSelectedObject() instanceof ProductSku) {
			setMessage(getInitialMessage());
			setErrorMessage(null);
			getOkButton().setEnabled(isComplete());
			showPriceInfo(getSelectedObject());
			return;
		}

		if (getSelectedObject() instanceof Product) {
			if (((Product) getSelectedObject()).hasMultipleSkus()) {
				setErrorMessage(CoreMessages.get().SkuFinderDialog_ErrorMsg_HasMultiSku);
				getOkButton().setEnabled(false);
			} else {
				setMessage(getInitialMessage());
				setErrorMessage(null);
				getOkButton().setEnabled(isComplete());
				showPriceInfo(getSelectedObject());
			}
			return;
		}
		getOkButton().setEnabled(false);

	}

	@Override
	protected String getBaseAmountObjectType(final Object selectedItem) {
		if (selectedItem instanceof ProductSku) {
			return PRODUCT_SKU_TYPE;
		}
		return PRODUCT_TYPE;

	}

	@Override
	public SkuSearchCriteria getModel() {
		if (searchCriteria == null) {
			searchCriteria = ServiceLocator.getService(ContextIdNames.SKU_SEARCH_CRITERIA);
			// Set default sorting criteria
			searchCriteria.setSortingOrder(SortOrder.ASCENDING);
			searchCriteria.setSortingType(StandardSortBy.SKU_CODE);
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
		getResultTableViewer().setInput(null);
		this.getModel().setBrandCode(null);
		// Set default sorting criteria
		this.getModel().setSortingOrder(SortOrder.ASCENDING);
		this.getModel().setSortingType(StandardSortBy.SKU_CODE);
		clearNavigation();
		columnSorterControl.clear();
	}

	@Override
	protected void bindControls() {

		final DataBindingContext bindingContext = new DataBindingContext();
		// bind product code and adding validator

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
				"skuCode", EpValidatorFactory.SKU_CODE_NOT_REQURED, null, false); //$NON-NLS-1$

		// Bind Brand
		final ObservableUpdateValueStrategy brandUpdateStrategy = new ObservableUpdateValueStrategy() {
			// -- Create a custom update strategy to update the model based on the selected brand
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				if (brandCombo.getSelectionIndex() == 0) {
					SkuFinderDialog.this.getModel().setBrandCode(null);
				} else {
					Brand selectedBrand = brandList.get(brandCombo.getSelectionIndex() - 1);
					SkuFinderDialog.this.getModel().setBrandCode(selectedBrand.getCode());
				}

				return Status.OK_STATUS;
			}
		};
		EpControlBindingProvider.getInstance().bind(bindingContext, brandCombo, null, null, brandUpdateStrategy, false);

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
		searchJob.setSearchCriteria(getModel());
		searchButton.setEnabled(false);
		clearPriceInfo();
		disableButtons();
		searchJob.executeSearchFromIndex(getShell(), getResultsStartIndex());
	}

	@Override
	protected String getMsgForNoResultFound() {
		return CoreMessages.get().SkuFinderDialog_NoResultsFound;
	}

	@Override
	protected void okPressed() {
		/**
		 * To be completed while integrated with other components.
		 */
		final IStructuredSelection selection = (IStructuredSelection) getResultTableViewer().getSwtTableViewer().getSelection();
		if (selection == null || selection.isEmpty()) {
			setErrorMessage(CoreMessages.get().SkuFinderDialog_ErrorMsg_SelectSku);
			return;
		}
		if (selection.getFirstElement() instanceof ProductSku) {
			final ProductSku sku = (ProductSku) selection.getFirstElement();
			setSelectedObject(sku);
		} else if (selection.getFirstElement() instanceof Product) {
			final Product product = (Product) selection.getFirstElement();

			if (product.hasMultipleSkus()) {
				setErrorMessage(CoreMessages.get().SkuFinderDialog_ErrorMsg_HasMultiSku);
				return;
			}
			setSelectedObject(product);
		}
		super.okPressed();
	}

	/**
	 * Process the search criteria before requesting for index search and call for index search.
	 */
	public void processSearch() {
		if (catalogCombo.getSelectionIndex() == 0) {
			// initial the catalog codes for search criteria
			// catalogCombo.select(0) cannot trigger the doSet method
			setCatalogSearchCriteria();
		}

		getModel().setLocale(CorePlugin.getDefault().getDefaultLocale());

		searchJob.setSearchCriteria(getModel());
		setErrorMessage(null);
		setResultsStartIndex(0);
		doSearch();
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
		return currentCatalog;
	}

	@Override
	protected void bindCatalogSearchableLocalesToModel(
			final Set<Locale> searchableLocales) {
		searchCriteria.setCatalogSearchableLocales(searchableLocales);

	}
}
