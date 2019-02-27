/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.views;

import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;

import com.elasticpath.cmclient.catalog.CatalogMessages;
import com.elasticpath.cmclient.catalog.CatalogPlugin;
import com.elasticpath.cmclient.catalog.actions.DeleteProductAction;
import com.elasticpath.cmclient.catalog.actions.product.CreateProductAction;
import com.elasticpath.cmclient.catalog.actions.product.CreateProductBundleAction;
import com.elasticpath.cmclient.catalog.editors.product.ProductEditor;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.EpUiException;
import com.elasticpath.cmclient.core.editors.GuidEditorInput;
import com.elasticpath.cmclient.core.event.ChangeSetMemberSelectionProvider;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.cmclient.core.helpers.AbstractSearchRequestJob;
import com.elasticpath.cmclient.core.helpers.ProductListener;
import com.elasticpath.cmclient.core.registry.ObjectRegistry;
import com.elasticpath.cmclient.core.registry.ObjectRegistryListener;
import com.elasticpath.cmclient.core.ui.framework.IEpTableViewer;
import com.elasticpath.cmclient.core.views.AbstractSortListView;
import com.elasticpath.cmclient.policy.PolicyPlugin;
import com.elasticpath.cmclient.policy.StatePolicy;
import com.elasticpath.cmclient.policy.StatePolicyDelegate;
import com.elasticpath.cmclient.policy.StatePolicyTarget;
import com.elasticpath.cmclient.policy.StatePolicyTargetListener;
import com.elasticpath.cmclient.policy.common.DefaultStatePolicyDelegateImpl;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.core.dto.catalog.ProductModel;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.search.query.ProductSearchCriteria;


/**
 * This view displays lists of products in a table format. The list of products can come from any source. For example, the results of a product
 * search can be displayed or a the products in a category may be displayed when the category is selected in another view.
 */
@SuppressWarnings({ "PMD.GodClass" })
public abstract class AbstractProductListView extends AbstractSortListView implements ProductListener,
	StatePolicyTarget, ObjectRegistryListener, ChangeSetMemberSelectionProvider {

	private static final Logger LOG = Logger.getLogger(AbstractProductListView.class);

	private ProductSearchCriteria searchCriteria;

	private static ListenerList listenerList;

	static {
		listenerList = new ListenerList(ListenerList.IDENTITY);
	}

	/**
	 * Constructor.
	 * @param tableName name of the table
	 */
	public AbstractProductListView(final String tableName) {
		super(true, tableName);
		PolicyPlugin.getDefault().registerStatePolicyTarget(this);
		fireStatePolicyTargetActivated();
	}

	/**
	 * The key string to store the product search criteria in the control data.
	 */
	public static final String PRODUCT_SEARCH_CRITERIA = "productSearchCriteria"; //$NON-NLS-1$

	// Actions
	private Action createProductAction;
	
	private Action createPBundleAction;

	private Action deleteProductAction;

	private Action doubleClickAction;

	private ActionContributionItem createProductActionContributionItem;
	
	private ActionContributionItem createPBundleActionContributionItem;

	private ActionContributionItem deleteProductActionContributionItem;

	private StatePolicyDelegate delegateGovernable;

	private StatePolicy statePolicy;

	private static final String ACTIVE_CHANGE_SET = "activeChangeSet"; //$NON-NLS-1$

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
						CatalogMessages.get().ProductListView_TableColumnTitle_Active };
		final int[] columnWidths = new int[] { 28, 150, 250, 150, 100, 150, 70};

		for (int i = 0; i < columnNames.length; i++) {
			table.addTableColumn(columnNames[i], columnWidths[i]);
		}

		getSite().setSelectionProvider(table.getSwtTableViewer());
	}

	private void makeActions() {

		doubleClickAction = new DoubleClickAction();
		deleteProductAction =  new DeleteProductAction(this);
		createProductAction = new CreateProductAction();
		createPBundleAction = new CreateProductBundleAction();

		// Actions have to be wrapped in ActionContributionItems so that they can be forced to display both text and image
		createProductActionContributionItem = new ActionContributionItem(createProductAction);
		createPBundleActionContributionItem = new ActionContributionItem(createPBundleAction);
		deleteProductActionContributionItem = new ActionContributionItem(deleteProductAction);
	}

	@Override
	public void setFocus() {
		getViewer().getControl().setFocus();
	}



	@Override
	protected ITableLabelProvider getViewLabelProvider() {

		return new ProductListViewLabelProvider(this);
	}

	@Override
	protected String getPluginId() {
		return CatalogPlugin.PLUGIN_ID;
	}

	@Override
	protected void initializeViewToolbar() {

		getViewer().addSelectionChangedListener(new InitPolicyOnProductChangeListener());

		makeActions();

		this.addDoubleClickAction(doubleClickAction);

		final Separator productActionSeparator = new Separator("productActionSeparator"); //$NON-NLS-1$

		getToolbarManager().add(productActionSeparator);

		createProductActionContributionItem.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		createPBundleActionContributionItem.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		deleteProductActionContributionItem.setMode(ActionContributionItem.MODE_FORCE_TEXT);

		getToolbarManager().appendToGroup(productActionSeparator.getGroupName(), createProductActionContributionItem);
		getToolbarManager().appendToGroup(productActionSeparator.getGroupName(), createPBundleActionContributionItem);
		getToolbarManager().appendToGroup(productActionSeparator.getGroupName(), deleteProductActionContributionItem);

		ObjectRegistry.getInstance().addObjectListener(this);
	}

	/**
	 * Open a product editor for the selected product.
	 */
	protected class DoubleClickAction extends Action {


		@Override
		public void run() {
			final ISelection selection = getViewer().getSelection();
			final Object obj = ((IStructuredSelection) selection).getFirstElement();

			if (obj instanceof ProductModel) {
				Product product = ((ProductModel) obj).getProduct();
				final IEditorInput editorInput = new GuidEditorInput(product.getGuid(), determineEditorInputClass(Product.class, product));

				try {
					getSite().getPage().openEditor(editorInput, ProductEditor.PART_ID);
				} catch (final PartInitException e) {
					// Log the error and throw an unchecked exception
					LOG.error(e.getStackTrace());
					throw new EpUiException("Could not create Product Editor", e); //$NON-NLS-1$
				}
			}
		}
	}

	/**
	 * Get the search criteria.
	 *
	 * @return product search criteria
	 */
	public ProductSearchCriteria getSearchCriteria() {
		return searchCriteria;
	}

	/**
	 * Set the search criteria.
	 *
	 * @param searchCriteria the product search criteria
	 */
	public void setSearchCriteria(final ProductSearchCriteria searchCriteria) {
		this.searchCriteria = searchCriteria;
	}

	@Override
	protected Object[] getViewInput() {

		return null;
	}

	/**
	 * Get the product service.
	 * @return product service
	 */
	public ProductService getProductService() {
		return ServiceLocator.getService(ContextIdNames.PRODUCT_SERVICE);
	}


	@Override
	public void productChanged(final ItemChangeEvent<Product> event) {
		refreshProduct(event.getItem());
	}


	@Override
	public void productSearchResultReturned(final SearchResultEvent<Product> event) {
		// TODO Auto-generated method stub

	}

	private void refreshProduct(final Product product) {
		for (TableItem item : getViewer().getTable().getItems()) {
			if (item.getData() instanceof ProductModel && (((ProductModel) item.getData())).getProduct().getUidPk() == product.getUidPk()) {
				getViewer().refresh(item.getData());
			}
		}
	}

	/**
	 * @return the create product action
	 */
	public Action getCreateProductAction() {
		return createProductAction;
	}
	
	/**
	 * @return the create product bundle action
	 */
	public Action getCreateProductBundleAction() {
		return createPBundleAction;
	}

	/**
	 * @return the delete product action
	 */
	public Action getDeleteProductAction() {
		return deleteProductAction;
	}

	/**
	 * Updates the selected category for the registered create product action.
	 * Initially if the user double clicks over the tree without clicking onto it no selection event is
	 * generated which leads to selected category being null.
	 *
	 * @param category the double clicked category
	 */
	public void setSelectedCategory(final Category category) {
		((CreateProductAction) getCreateProductAction()).setCategory(category);
		((CreateProductBundleAction) getCreateProductBundleAction()).setCategory(category);
	}

	@Override
	public AbstractSearchRequestJob < ? extends Persistable > getSearchRequestJob() {
		return null;
	}

	/**
	 * Return an implementation of the policy target that we can delegate to.
	 *
	 * @return a <code>PolicyTarget</code> for delegation.
	 */
	protected StatePolicyDelegate getDelegateGovernable() {
		if (delegateGovernable == null) {
			delegateGovernable = new DefaultStatePolicyDelegateImpl();
		}
		return delegateGovernable;
	}

	@Override
	public PolicyActionContainer addPolicyActionContainer(final String name) {
		return getDelegateGovernable().addPolicyActionContainer(name);
	}

	/**
	 * Apply the given policy, storing it for later use.
	 * Delegates to the delegate target.
	 *
	 * @param policy the <code>StatePolicy</code> to apply.
	 */
	@Override
	public void applyStatePolicy(final StatePolicy policy) {
		this.statePolicy = policy;
		statePolicy.init(getModel());
		getDelegateGovernable().applyStatePolicy(policy);
	}

	/**
	 * Get the state policy that has been applied to this view.
	 *
	 * @return a <code>StatePolicy</code>
	 */
	public StatePolicy getStatePolicy() {
		return this.statePolicy;
	}

	@Override
	public Map<String, PolicyActionContainer> getPolicyActionContainers() {
		return getDelegateGovernable().getPolicyActionContainers();
	}

	@Override
	public String getTargetIdentifier() {
		return "productListView"; //$NON-NLS-1$
	}

	@Override
	public void objectAdded(final String key, final Object object) {
		if (ACTIVE_CHANGE_SET.equals(key)) {
			getViewer().setSelection(getViewer().getSelection());
		}
	}

	@Override
	public void objectRemoved(final String key, final Object object) {
		if (ACTIVE_CHANGE_SET.equals(key)) {
			getViewer().setSelection(getViewer().getSelection());
		}
	}

	@Override
	public void objectUpdated(final String key, final Object oldValue, final Object newValue) {
		if (ACTIVE_CHANGE_SET.equals(key)) {
			getViewer().setSelection(getViewer().getSelection());
		}
	}

	/**
	 * Listener to ensure state policy is initialized with the selected product.
	 */
	protected class InitPolicyOnProductChangeListener implements ISelectionChangedListener {

		@Override
		public void selectionChanged(final SelectionChangedEvent event) {
			final ISelection selection = getViewer().getSelection();
			final Object obj = ((IStructuredSelection) selection).getFirstElement();
			if (obj instanceof ProductModel) {
				getStatePolicy().init(((ProductModel) obj).getProduct());
			}
		}

	}

	@Override
	public void addGovernableListener(final StatePolicyTargetListener governableListener) {
		listenerList.add(governableListener);
	}

	@Override
	public void removeGovernableListener(final StatePolicyTargetListener listener) {
		listenerList.remove(listener);
	}

	/**
	 * Fire the activation event to all listeners.
	 */
	private void fireStatePolicyTargetActivated() {
		for (Object listener : listenerList.getListeners()) {
			((StatePolicyTargetListener) listener).statePolicyTargetActivated(this);
		}
	}
	
	/**
	 * Returns product of <code>changeSetObjectSelection</code> which is <code>ProductModel</code>.
	 * @param changeSetObjectSelection <code>ProductModel</code> selection
	 * @return <code>Product</code>
	 */
	@Override
	public Object resolveObjectMember(final Object changeSetObjectSelection) {
		return ((ProductModel) changeSetObjectSelection).getProduct();
	}

	/**
	 * Determines the class to be used in the editor input.
	 * 
	 * @param <T> the returned class type 
	 * @param clazz the interface that the class should be a subclass of
	 * @param object the object used for the determination
	 * @return the class to use in the editor input
	 */
	protected <T> Class< ? extends T > determineEditorInputClass(final Class<T> clazz, final T object) {
		for (Class< ? > iface : object.getClass().getInterfaces()) {
			if (clazz.isAssignableFrom(iface)) {
				return (Class< ? extends T>) iface;
			}
		}
		return clazz;
	}
}
