/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;

import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.catalog.CatalogImageRegistry;
import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.editors.product.ProductEditor;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.cmclient.core.EpUiException;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.cmclient.core.helpers.AbstractSearchRequestJob;
import com.elasticpath.cmclient.core.helpers.CategoryListener;
import com.elasticpath.cmclient.core.helpers.ProductSearchRequestJob;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.service.CatalogEventService;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.core.ui.framework.IEpTableColumn;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.core.ui.util.EditorUtil;
import com.elasticpath.cmclient.policy.StatePolicy;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.core.dto.catalog.ProductModel;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductLoadTuner;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.query.CriteriaBuilder;
import com.elasticpath.service.query.QueryResult;
import com.elasticpath.service.query.QueryService;
import com.elasticpath.service.query.ResultType;
import com.elasticpath.service.query.relations.ProductRelation;
import com.elasticpath.service.search.query.ProductSearchCriteria;
import com.elasticpath.service.search.query.SortBy;
import com.elasticpath.service.search.query.SortOrder;
import com.elasticpath.service.search.query.StandardSortBy;

/**
 * This view displays lists of products in a table format. The list of products can come from any source. For example, the results of a product
 * search can be displayed or a the products in a category may be displayed when the category is selected in another view.
 */
public class BrowseProductListView extends AbstractProductListView {

	/**
	 * The part id.
	 */
	public static final String PART_ID = "com.elasticpath.cmclient.catalog.views.BrowseProductListView"; //$NON-NLS-1$

	private static final String BROWSE_PRODUCT_LIST_TABLE = "Browse Product List"; //$NON-NLS-1$

	// NOTE: loadSku !MUST! be !TRUE! since delete action checks whether the orderSkus are in shipment or not.
	// setting loadSku to false results in orderSku = null and omitting the check.
	private final ProductSearchRequestJob browseJob = new ProductSearchRequestJob(true);

	private Object browseTreeObject;

	private IncludeProductAction includeProductAction;

	private ActionContributionItem includeProductActionContributionItem;

	private ExcludeProductAction excludeProductAction;

	private ActionContributionItem excludeProductActionContributionItem;

	private static final int COLUMN_INCLUDED_INDEX = 7;

	private static final int COLUMN_INCLUDED_WIDTH = 80;

	private StatePolicy statePolicy;

	private CategoryChangeListener categoryChangeListener = new CategoryChangeListener();

	/**
	 * Default constructor.
	 */
	public BrowseProductListView() {
		super(BROWSE_PRODUCT_LIST_TABLE);
		CatalogEventService.getInstance().addCategoryListener(
				categoryChangeListener);
	}

	@Override
	public void dispose() {
		CatalogEventService.getInstance().removeCategoryListener(categoryChangeListener);
		categoryChangeListener = null;
	}

	/**
	 * Hides the Include column by setting its width to 0.
	 */
	protected void hideIncludedColumn() {
		this.getViewer().getTable().getColumn(COLUMN_INCLUDED_INDEX).setWidth(0);
	}

	/**
	 * Shows the Include column.
	 */
	protected void showIncludedColumn() {
		this.getViewer().getTable().getColumn(COLUMN_INCLUDED_INDEX).setWidth(COLUMN_INCLUDED_WIDTH);
	}

	/**
	 * Search the products and update the pagination.
	 *
	 * @param category the category in which to search for products
	 */
	public void searchProducts(final Category category) {
		ProductSearchCriteria criteria = createSearchCriteria(category);

		this.getViewer().setData(PRODUCT_SEARCH_CRITERIA, criteria);
		setSearchCriteria(criteria);

		this.browseJob.setSource(this);
		this.browseJob.setSearchCriteria(criteria);
		this.browseJob.executeSearchFromIndex(this.getSite().getShell(), 0);
	}

	/**
	 * Creates new search criteria and populates it with category id and default search values.
	 */
	private ProductSearchCriteria createSearchCriteria(final Category category) {
		ProductSearchCriteria criteria = ServiceLocator.getService(ContextIdNames.PRODUCT_SEARCH_CRITERIA);

		if (category.isLinked()) {
			criteria.setOnlySearchMasterCategory(true);
			criteria.setMasterCategoryCode(category.getMasterCategory().getCode());
			criteria.setMasterCategoryCatalogCode(category.getMasterCategory().getCatalog().getCode());
		} else {
			criteria.setOnlySearchMasterCategory(false);
			criteria.setDirectCategoryUid(category.getUidPk());
			criteria.setCatalogCode(category.getCatalog().getCode());
		}
		criteria.setLocale(CorePlugin.getDefault().getDefaultLocale());
		criteria.setSortingType(StandardSortBy.PRODUCT_NAME_NON_LC);
		criteria.setSortingOrder(SortOrder.ASCENDING);
		return criteria;
	}

	@Override
	protected void initializeTable(final IEpTableViewer table) {

		final String[] columnNames =
				new String[] {
						"", //$NON-NLS-1$
						CatalogMessages.get().ProductListView_TableColumnTitle_ProductCode,
						CatalogMessages.get().ProductListView_TableColumnTitle_ProductName,
						CatalogMessages.get().ProductListView_TableColumnTitle_ProductType,
						CatalogMessages.get().ProductListView_TableColumnTitle_Brand,
						CatalogMessages.get().ProductListView_TableColumnTitle_DefaultCategory,
						CatalogMessages.get().ProductListView_TableColumnTitle_Active,
						CatalogMessages.get().ProductListView_TableColumnTitle_Included};  //Don't show this column when initializing
		final int[] columnWidths = new int[] { 28, 150, 250, 150, 100, 150, 70, 0 };
		final SortBy[] sortTypes = new SortBy[] {
				null,
				StandardSortBy.PRODUCT_CODE,
				StandardSortBy.PRODUCT_NAME_NON_LC,
				StandardSortBy.PRODUCT_TYPE_NAME,
				StandardSortBy.BRAND_NAME,
				StandardSortBy.PRODUCT_DEFAULT_CATEGORY_NAME,
				null,
				null,
				null
		};

		for (int i = 0; i < columnNames.length; i++) {
			IEpTableColumn addTableColumn = table.addTableColumn(columnNames[i], columnWidths[i]);
			registerTableColumn(addTableColumn, sortTypes[i]);
		}

		getSite().setSelectionProvider(table.getSwtTableViewer());
	}

	@Override
	public void refreshViewerInput() {
		final ProductSearchCriteria criteria = (ProductSearchCriteria) getViewer().getData(PRODUCT_SEARCH_CRITERIA);
		if (criteria != null) {
			doBrowse(criteria);
		}
	}


	/**
	 * Fire the product browse.
	 *
	 * @param criteria search criteria.
	 */
	protected void doBrowse(final ProductSearchCriteria criteria) {
		browseJob.setSource(this);
		browseJob.setSearchCriteria(criteria);
		browseJob.executeSearchFromIndex(this.getSite().getShell(), getResultsStartIndex());
	}

	@Override
	protected void initializeViewToolbar() {
		super.initializeViewToolbar();
		this.makeExtraActions();
	}

	private void makeExtraActions() {

		includeProductAction = new IncludeProductAction();
		excludeProductAction = new ExcludeProductAction();

		// Actions have to be wrapped in ActionContributionItems so that they can be forced to display both text and image
		includeProductActionContributionItem = new ActionContributionItem(includeProductAction);
		excludeProductActionContributionItem = new ActionContributionItem(excludeProductAction);

		includeProductActionContributionItem.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		excludeProductActionContributionItem.setMode(ActionContributionItem.MODE_FORCE_TEXT);

		final Separator extraActionSeparator = new Separator("extraActionSeparator"); //$NON-NLS-1$
		getToolbarManager().add(extraActionSeparator);
		getToolbarManager().appendToGroup(extraActionSeparator.getGroupName(), includeProductActionContributionItem);
		getToolbarManager().appendToGroup(extraActionSeparator.getGroupName(), excludeProductActionContributionItem);

		includeProductAction.setEnabled(false);
		excludeProductAction.setEnabled(false);

		ListViewerListener listViewerListener = new ListViewerListener(this);
		getViewer().addSelectionChangedListener(listViewerListener);
	}

	/**
	 * Listens for changes to categories and refreshes this view
	 * in case it's an Include or Exclude action.
	 */
	private final class CategoryChangeListener implements CategoryListener {
		@Override
		public void categoryChanged(
				final ItemChangeEvent<Category> event) {
			refreshViewerInput();
			refreshView();
		}

		@Override
		public void categorySearchResultReturned(
				final SearchResultEvent<Category> event) {
			// nothing required
		}
	}

	/**
	 * The viewer listener.
	 */
	protected class ListViewerListener implements ISelectionChangedListener {

		private final BrowseProductListView productListView;

		private final PolicyActionContainer includeExcludeContainer = new PolicyActionContainer("productIncludeExclude");  //$NON-NLS-1$

		/**
		 * Constructor.
		 * @param productListView the list view
		 */
		public ListViewerListener(final BrowseProductListView productListView) {
			this.productListView = productListView;
		}

		@Override
		public void selectionChanged(final SelectionChangedEvent event) {

			final ISelection selection = getViewer().getSelection();
			final Object obj = ((IStructuredSelection) selection).getFirstElement();

			this.productListView.getIncludeProductAction().setEnabled(false);
			this.productListView.getExcludeProductAction().setEnabled(false);

			if (obj instanceof ProductModel &&  this.productListView.getBrowseTreeObject() instanceof Category) {
				Product product = ((ProductModel) obj).getProduct();
				Category category = (Category) this.productListView.getBrowseTreeObject();

				//Only the products in derived virtual category can have enabled include/exclude buttons
				if (category.isLinked()) {
					boolean enabled = (EpState.EDITABLE == statePolicy.determineState(includeExcludeContainer));
					if (product.isBelongToCategory(category.getUidPk())) {
						this.productListView.getExcludeProductAction().setEnabled(enabled);
					} else {
						this.productListView.getIncludeProductAction().setEnabled(enabled);
					}
				}
			}
		}
	}

	/**
	 * @return true if CM User is authorized to work with current catalog.
	 */
	boolean isCatalogAuthorized() {
		Category category = (Category) browseTreeObject;
		return AuthorizationService.getInstance().isAuthorizedForCatalog(category.getCatalog());
	}

	/**
	 * Include the product.
	 */
	protected class IncludeProductAction extends Action {

		/**
		 * Constructor.
		 */
		public IncludeProductAction() {
			super();
			setImageDescriptor(CatalogImageRegistry.PRODUCT_INCLUDE);
			setToolTipText(CatalogMessages.get().IncludeProductAction);
			setText(CatalogMessages.get().IncludeProductAction);
		}

		@Override
		public void run() {
			final ISelection selection = getViewer().getSelection();
			final Object obj = ((IStructuredSelection) selection).getFirstElement();

			if (obj instanceof ProductModel) {
				ProductModel productModel = ((ProductModel) obj);
				Product product = productModel.getProduct();

				boolean hasOpenEditor = checkForDirtyProductEditor(product, CatalogMessages.get().IncludeProductAction,
						CatalogMessages.get().IncludeProduct_SaveDirtyEditor);

				if (!hasOpenEditor) {
					Category category = (Category) browseTreeObject;
					Product productWithAttributes = getProductWithAttributes(productModel.getProduct().getGuid());
					productWithAttributes.addCategory(category);

					final Product updatedProduct = getProductService().saveOrUpdate(productWithAttributes);
					productModel.setProduct(updatedProduct);

					getIncludeProductAction().setEnabled(false);
					getExcludeProductAction().setEnabled(true);
					refreshView();
				}
			}
		}

		private Product getProductWithAttributes(final String productGuid) {
			ProductLoadTuner productLoadTuner = ServiceLocator.getService(ContextIdNames.PRODUCT_LOAD_TUNER);
			productLoadTuner.setLoadingAttributeValue(true);
			productLoadTuner.setLoadingCategories(true);

			QueryService<Product> productQueryService = ServiceLocator.getService(ContextIdNames.PRODUCT_QUERY_SERVICE);

			QueryResult<Product> queryResult = productQueryService.query(CriteriaBuilder.criteriaFor(Product.class)
					.with(ProductRelation.having().codes(productGuid)).usingLoadTuner(productLoadTuner).returning(ResultType.ENTITY));
			return queryResult.getSingleResult();
		}
	}

	/**
	 * Exclude the product.
	 */
	protected class ExcludeProductAction extends Action {

		/**
		 * Constructor.
		 */
		public ExcludeProductAction() {
			super();
			setImageDescriptor(CatalogImageRegistry.PRODUCT_EXCLUDE);
			setToolTipText(CatalogMessages.get().ExcludeProductAction);
			setText(CatalogMessages.get().ExcludeProductAction);
		}

		@Override
		public void run() {
			final ISelection selection = getViewer().getSelection();
			final Object obj = ((IStructuredSelection) selection).getFirstElement();

			if (obj instanceof ProductModel) {
				ProductModel productModel = ((ProductModel) obj);
				Product product = productModel.getProduct();

				boolean hasOpenEditor = checkForDirtyProductEditor(product, CatalogMessages.get().ExcludeProductAction,
						CatalogMessages.get().ExcludeProduct_SaveDirtyEditor);

				if (!hasOpenEditor) {
					Category category = (Category) browseTreeObject;
					Product productWithAttributes = getProductWithAttributes(productModel.getProduct().getGuid());
					productWithAttributes.removeCategory(category);

					final Product updatedProduct = getProductService().saveOrUpdate(productWithAttributes);
					productModel.setProduct(updatedProduct);

					getIncludeProductAction().setEnabled(true);
					getExcludeProductAction().setEnabled(false);
					refreshView();
				}
			}
		}

		private Product getProductWithAttributes(final String productGuid) {
			ProductLoadTuner productLoadTuner = ServiceLocator.getService(ContextIdNames.PRODUCT_LOAD_TUNER);
			productLoadTuner.setLoadingAttributeValue(true);
			productLoadTuner.setLoadingCategories(true);

			QueryService<Product> productQueryService = ServiceLocator.getService(ContextIdNames.PRODUCT_QUERY_SERVICE);

			QueryResult<Product> queryResult = productQueryService.query(CriteriaBuilder.criteriaFor(Product.class)
					.with(ProductRelation.having().codes(productGuid)).usingLoadTuner(productLoadTuner).returning(ResultType.ENTITY));
			return queryResult.getSingleResult();
		}
	}

	private void refreshView() {
			getViewer().refresh();
	}

	@Override
	protected IStructuredContentProvider getViewContentProvider() {
		return new BrowseProductViewContentProvider(this);
	}

	/**
	 * Get the selected object in the browse tree.
	 * @return the selected object
	 */
	public Object getBrowseTreeObject() {
		return browseTreeObject;
	}

	/**
	 * Set the selected object in the browse tree.
	 * @param browseTreeObject the object being selected in the browse tree
	 */
	public void setBrowseTreeObject(final Object browseTreeObject) {
		this.browseTreeObject = browseTreeObject;
	}

	/**
	 * Get the include action.
	 * @return include action
	 */
	public IncludeProductAction getIncludeProductAction() {
		return includeProductAction;
	}

	/**
	 * Get the exclude action.
	 * @return exclude action
	 */
	public ExcludeProductAction getExcludeProductAction() {
		return excludeProductAction;
	}

	@Override
	public AbstractSearchRequestJob < ? extends Persistable > getSearchRequestJob() {
		return browseJob;
	}

	@Override
	public void applyStatePolicy(final StatePolicy policy) {
		this.statePolicy = policy;
		super.applyStatePolicy(policy);
	}

	/**
	 * Check for open product editor.
	 *
	 * @param product the product
	 * @param heading the heading to display
	 * @param warning the warning to display
	 * @return true if the editor is dirty
	 */
	private boolean checkForDirtyProductEditor(final Product product, final String heading, final String warning) {
		final IWorkbenchPage workbenchPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

		for (IEditorReference editorRef : workbenchPage.getEditorReferences()) {

			try {
				if (EditorUtil.isSameEditor(editorRef, ProductEditor.PART_ID) && EditorUtil.isSameEntity(product, editorRef)
						&& editorRef.isDirty()) {
					MessageDialog.openWarning(null,
							heading,

							NLS.bind(warning,
							new Object[]{product.getCode(), product.getDisplayName(CorePlugin.getDefault().getDefaultLocale())}));
							return true;
					}

			} catch (PartInitException e) {
				throw new EpUiException("Could not get product editor input", e); //$NON-NLS-1$
			}
		}
		return false;
	}

	@Override
	protected String getPartId() {
		return PART_ID;
	}
}