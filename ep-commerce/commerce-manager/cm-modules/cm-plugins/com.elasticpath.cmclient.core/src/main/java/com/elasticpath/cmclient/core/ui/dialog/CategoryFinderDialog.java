/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.ui.dialog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
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
import com.elasticpath.cmclient.core.helpers.CategoryListener;
import com.elasticpath.cmclient.core.helpers.CategorySearchRequestJob;
import com.elasticpath.cmclient.core.helpers.LocalCategoryLookup;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.service.CatalogEventService;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.service.catalog.CatalogService;
import com.elasticpath.service.catalog.CategoryLookup;
import com.elasticpath.service.search.query.CategorySearchCriteria;

/**
 * This class provides the presentation of product Finder dialog window.
 */
@SuppressWarnings({ "PMD.GodClass" })
public class CategoryFinderDialog extends AbstractCatalogObjectFinderDialog {

	private static final String CATEGORY_FINDER_TABLE = "Category Finder"; //$NON-NLS-1$

	private static final int TABLE_HEIGHT = 225;

	private CategorySearchCriteria searchCriteria;

	private final CategorySearchRequestJob searchJob = new CategorySearchRequestJob();

	private Boolean searchLinkedCategories;

	private CCombo catalogCombo;

	private final Catalog catalog;

	private final List<Catalog> catalogList;
	private CategoryLookup categoryLookup;

	/**
	 * This label provider returns the text displayed in each column for a given <code>Category</code> object. This also determines the icon to be
	 * displayed for each corresponding product at the first column.
	 */
	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {

		private static final int COLUMN_INDEX_CATEGORY_CODE = 0;

		private static final int COLUMN_INDEX_CATEGORY_NAME = 1;

		private static final int COLUMN_INDEX_PARENT_CATEGORY = 2;

		/**
		 * Over ride method. Get the column image.
		 *
		 * @param element the element object to be displayed.
		 * @param columnIndex the column index.
		 * @return the column image object.
		 */
		public Image getColumnImage(final Object element, final int columnIndex) {
			// no column image is required for the category table.
			return null;
		}

		/**
		 * Get column text.
		 *
		 * @param element the category element object to be displayed by the table.
		 * @param columnIndex the column index.
		 * @return the column text content string.
		 */
		public String getColumnText(final Object element, final int columnIndex) {
			final Category category = (Category) element;
			final Locale defaultLocale = CorePlugin.getDefault().getDefaultLocale();

			switch (columnIndex) {
			case COLUMN_INDEX_CATEGORY_CODE:
				return category.getCode();
			case COLUMN_INDEX_CATEGORY_NAME:
				return category.getDisplayName(defaultLocale);
			case COLUMN_INDEX_PARENT_CATEGORY:
				if (!category.hasParent()) {
					return CoreMessages.get().NotAvailable;
				}
				Category parent = getCategoryLookup().findParent(category);
				return parent.getDisplayName(defaultLocale);
			default:
				return CoreMessages.get().NotAvailable;
			}
		}
	}

	/**
	 * The content provider class is responsible for providing objects to the view. It wraps existing objects in adapters or simply return objects
	 * as-is. These objects may be sensitive to the current input of the view, or ignore it and always show the same content.
	 */
	class ViewContentProvider implements IStructuredContentProvider, CategoryListener {
		/**
		 * The default constructor.
		 */
		ViewContentProvider() {
			CatalogEventService.getInstance().addCategoryListener(this);
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
			CatalogEventService.getInstance().removeCategoryListener(this);
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
		public void categorySearchResultReturned(final SearchResultEvent<Category> event) {
			PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
				public void run() {
					updateNavigationComponents();
					getResultTableViewer().setInput(event.getItems().toArray());
					setResultsCount(event.getTotalNumberFound());
					setResultsStartIndex(event.getStartIndex());
					updateNavigationComponents();
					handleErrorMessage(CoreMessages.get().CategoryFinderDialog_NoResultsFound);
					searchButton.setEnabled(true);
				}
			});
		}

		/**
		 * Process the action while the table item is changed.
		 *
		 * @param event the category change event object.
		 */
		public void categoryChanged(final ItemChangeEvent<Category> event) {
			final Category changedCategory = event.getItem();

			for (final TableItem currTableItem : getResultTableViewer().getSwtTableViewer().getTable().getItems()) {
				final Product currProduct = (Product) currTableItem.getData();
				if (currProduct.getUidPk() == changedCategory.getUidPk()) {
					currTableItem.setData(changedCategory);
					getResultTableViewer().getSwtTableViewer().refresh();
					break;
				}
			}
		}
	}

	private Button searchButton;

	private Text categoryName;

	private final boolean enableNavigation;

	private Text categoryCode;

	private static final int TABLE_VERTICAL_SPAN = 2;

	/**
	 * @param parentShell the parent shell of this dialog
	 * @param onlyMasterCatalogs set to true if only the master catalogs have to be displayed in the combo box
	 */
	public CategoryFinderDialog(final Shell parentShell, final boolean onlyMasterCatalogs) {
		this(parentShell, null, onlyMasterCatalogs);
	}

	/**
	 * @param parentShell the parent shell of this dialog
	 * @param catalog the catalog to search within. If set, only search within this catalog (i.e. disable the catalog combo-box); <br>
	 *            Set it to null if you allow user to choose catalog from the combo box.
	 */
	public CategoryFinderDialog(final Shell parentShell, final Catalog catalog) {
		this(parentShell, catalog, false);
	}

	/**
	 * @param parentShell the parent shell of this dialog
	 * @param catalog the catalog to search within. If set, only search within this catalog (i.e. disable the catalog combo-box);<br>
	 *            Set it to null if you allow user to choose catalog from the combo box.
	 * @param onlyMasterCatalogs set to true if only the master catalogs have to be displayed in the combo box
	 */
	protected CategoryFinderDialog(final Shell parentShell, final Catalog catalog, final boolean onlyMasterCatalogs) {
		super(parentShell);

		this.catalog = catalog;
		if (catalog == null) {
			catalogList = getCatalogs(onlyMasterCatalogs);
		} else {
			// do not need the full list of catalogs if we are searching within a specified catalog
			catalogList = new ArrayList<Catalog>();
			catalogList.add(catalog);
		}

		AuthorizationService.getInstance().filterAuthorizedCatalogs(catalogList);
		if (catalogList.isEmpty()) {
			setNoDataAvailable(true);
		}

		enableNavigation = true;
		// By default, search all categories (i.e. both linked and non-linked)
		searchLinkedCategories = null;
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

		final IEpLayoutComposite scrolledLeftPaneComposite = leftPaneComposite.addScrolledGridLayoutComposite(1, false, false,
				leftPaneComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true));

		final IEpLayoutData groupLayoutData = mainComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true);

		final IEpLayoutComposite searchGroupComposite = scrolledLeftPaneComposite.addGroup(CoreMessages.get().CategoryFinderDialog_Search, 1, false,
				groupLayoutData);

		final IEpLayoutData fieldLayoutData = scrolledLeftPaneComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true);
		final IEpLayoutData labelLayoutData = scrolledLeftPaneComposite.createLayoutData(IEpLayoutData.BEGINNING, IEpLayoutData.FILL, false, false);

		searchGroupComposite.addLabelBold(CoreMessages.get().CategoryFinderDialog_CategoryName, labelLayoutData);
		categoryName = searchGroupComposite.addTextField(EpState.EDITABLE, fieldLayoutData);

		searchGroupComposite.addLabelBold(CoreMessages.get().CategoryFinderDialog_CategoryCode, labelLayoutData);
		categoryCode = searchGroupComposite.addTextField(EpState.EDITABLE, fieldLayoutData);

		final IEpLayoutComposite filtersGroupComposite = scrolledLeftPaneComposite.addGroup(CoreMessages.get().ProductFinderDialog_Filters, 1, false,
				groupLayoutData);

		filtersGroupComposite.addLabelBold(CoreMessages.get().CategoryFinderDialog_Catalog, labelLayoutData);
		catalogCombo = filtersGroupComposite.addComboBox(EpState.EDITABLE, fieldLayoutData);
		catalogCombo.setEnabled(false);

		searchButton = leftPaneComposite.addPushButton(CoreMessages.get().CategoryFinderDialog_Search,
				CoreImageRegistry.getImage(CoreImageRegistry.IMAGE_SEARCH_ACTIVE),
				EpState.EDITABLE,
				leftPaneComposite.createLayoutData(IEpLayoutData.END, IEpLayoutData.BEGINNING));


		IEpLayoutComposite resultPaneComposite = createResultPaneComposite(mainComposite);

		if (enableNavigation) {
			getNavigationService().registerNavigationEventListener(this);
			createPaginationCompositeControl(resultPaneComposite);
		}

		createErrorMessageControl(resultPaneComposite);
		createTableViewControl(resultPaneComposite,
				mainComposite.createLayoutData(IEpLayoutData.FILL, IEpLayoutData.FILL, true, true, 1, TABLE_VERTICAL_SPAN), CATEGORY_FINDER_TABLE);
		addColumnsToTableViewer();
	}

	private void addColumnsToTableViewer() {
		final int categoryCodeColumn = 100;
		final int categoryNameColumn = 130;
		final int parentCategoryColumn = 130;
		final int tableHeight = 150;

		getResultTableViewer().addTableColumn(CoreMessages.get().CategoryFinderDialog_CategoryCode, categoryCodeColumn);
		getResultTableViewer().addTableColumn(CoreMessages.get().CategoryFinderDialog_CategoryName, categoryNameColumn);
		getResultTableViewer().addTableColumn(CoreMessages.get().CategoryFinderDialog_ParentCategory, parentCategoryColumn);

		((GridData) getResultTableViewer().getSwtTable().getLayoutData()).heightHint = tableHeight;
	}

	@Override
	public void populateControls() {
		searchButton.addSelectionListener(this);
		getResultTableViewer().getSwtTableViewer().addSelectionChangedListener(this);
		getResultTableViewer().setContentProvider(new ViewContentProvider());
		getResultTableViewer().setLabelProvider(new ViewLabelProvider());

		populateCatalogCombo();

		categoryName.addFocusListener(this);
		categoryName.addSelectionListener(this);

		categoryCode.addFocusListener(this);
		categoryCode.addSelectionListener(this);

		catalogCombo.addFocusListener(this);
		catalogCombo.addSelectionListener(this);

		categoryName.setFocus();
	}

	@Override
	protected String getInitialMessage() {
		return CoreMessages.get().CategoryFinderDialog_FindACategory;
	}

	@Override
	protected String getTitle() {
		return CoreMessages.get().CategoryFinderDialog_Title;
	}

	@Override
	protected String getWindowTitle() {
		return CoreMessages.get().CategoryFinderDialog_WindowTitle;
	}

	/**
	 * Called when a widget is default selected. For text fields hitting ENTER calls this method.
	 *
	 * @param event selection event
	 */
	public void widgetDefaultSelected(final SelectionEvent event) {
		if (event.getSource() instanceof Text) {
			processSearch();
		}
	}

	/**
	 * @param event the SelectionEvent object
	 */
	public void widgetSelected(final SelectionEvent event) {
		if (event.getSource() == searchButton) {
			processSearch();
		} else {
			this.clearFields();
		}
	}

	@Override
	public void selectionChanged(final SelectionChangedEvent event) {
		final Object selectedItem = ((IStructuredSelection) event.getSelection()).getFirstElement();
		if (selectedItem instanceof Category) {
			setMessage(getInitialMessage());
			setErrorMessage(null);
			setSelectedObject(selectedItem);
			getOkButton().setEnabled(true);
		}
	}

	@Override
	protected Image getWindowImage() {
		return null;
	}

	@Override
	public CategorySearchCriteria getModel() {
		if (searchCriteria == null) {
			searchCriteria = ServiceLocator.getService(ContextIdNames.CATEGORY_SEARCH_CRITERIA);
		}
		return searchCriteria;
	}

	@Override
	protected void bindControls() {
		final DataBindingContext bindingContext = new DataBindingContext();
		// Bind category code or name input to model.
		EpControlBindingProvider.getInstance().bind(bindingContext, categoryCode, getModel(), "categoryCode", null, null, false); //$NON-NLS-1$
		EpControlBindingProvider.getInstance().bind(bindingContext, categoryName, getModel(), "categoryName", null, null, false); //$NON-NLS-1$

		// Bind Catalog
		final ObservableUpdateValueStrategy catalogUpdateStrategy = new ObservableUpdateValueStrategy() {
			// If selected catalog is the first item, it is ALL catalogs. In this case when OK is pressed it assumes all catalogs is selected.
			// If its anything else, then we get the exact catalog selected, save it, and when OK is pressed we submit the search with this catalog.
			@Override
			protected IStatus doSet(final IObservableValue observableValue, final Object newValue) {
				setCatalogSearchCriteria();
				return Status.OK_STATUS;
			}
		};

		EpControlBindingProvider.getInstance().bind(bindingContext, catalogCombo, null, null, catalogUpdateStrategy, false);
	}

	@Override
	protected void doSearch() {
		disableButtons();
		searchButton.setEnabled(false);
		searchJob.executeSearchFromIndex(getShell(), getResultsStartIndex());
	}

	@Override
	protected String getMsgForNoResultFound() {
		return CoreMessages.get().CategoryFinderDialog_NoResultsFound;
	}

	@Override
	protected void clearFields() {
		categoryName.setText(CoreMessages.EMPTY_STRING);
		categoryCode.setText(CoreMessages.EMPTY_STRING);
	}

	@Override
	@SuppressWarnings({ "PMD.UselessOverridingMethod" })
	protected void okPressed() {
		/**
		 * To be completed while integrated with other components.
		 */
		final IStructuredSelection selection = (IStructuredSelection) getResultTableViewer().getSwtTableViewer().getSelection();
		if (selection == null || selection.isEmpty()) {
			setErrorMessage(CoreMessages.get().CategoryFinderDialog_ErrorMsg_SelectCategory);
			return;
		}
		if (selection.getFirstElement() instanceof Category) {
			final Category category = (Category) selection.getFirstElement();
			setSelectedObject(category);
		}
		super.okPressed();
	}

	/**
	 * Process the search criteria before requesting for index search and call for index search.
	 */
	public void processSearch() {
		if (CoreMessages.get().SearchView_Filter_Brand_All.equals(getCatalogCombo().getText())) {
			// If searching for ALL catalogs then only include non-linked categories - avoid duplicate categories
			getModel().setLinked(false);
		} else {
			// Check if searching only for linked categories, only for non-linked categories, or for all categories
			getModel().setLinked(searchLinkedCategories);
		}

		if (catalogCombo.getSelectionIndex() == 0) {
			setCatalogSearchCriteria();
		}

		if (getModel().getLocale() == null) {
			getModel().setLocale(CorePlugin.getDefault().getDefaultLocale());
		}
		
		setErrorMessage(null);
		setResultsStartIndex(0);
		searchJob.setSearchCriteria(getModel());
		doSearch();
	}
	
	/**
	 * Set to <code>true</code> if this <code>CategoryFinderDialog</code> should only search for categories that are linked; <br>
	 * set to <code>false</code> if this <code>CategoryFinderDialog</code> should only search for categories that are not linked; <br>
	 * set to <code>null</code> if this <code>CategoryFinderDialog</code> should search for all categories.
	 *
	 * @param isLinked <code>true</code> if this <code>CategoryFinderDialog</code> should only search for categories that are linked; <br>
	 *            set to <code>false</code> if this <code>CategoryFinderDialog</code> should only search for categories that are not linked; <br>
	 *            set to <code>null</code> if this <code>CategoryFinderDialog</code> should search for all categories.
	 */
	public void setSearchedLinkedCategories(final Boolean isLinked) {
		searchLinkedCategories = isLinked;
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

	@Override
	protected int getResultTableHeight() {
		return TABLE_HEIGHT;
	}
}