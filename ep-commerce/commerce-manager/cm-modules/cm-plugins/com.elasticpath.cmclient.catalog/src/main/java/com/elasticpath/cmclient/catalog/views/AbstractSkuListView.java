/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.catalog.views;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;

import com.elasticpath.cmclient.catalog.CatalogPlugin;
import com.elasticpath.cmclient.catalog.editors.product.ProductEditor;
import com.elasticpath.cmclient.catalog.editors.sku.ProductSkuDetailsPage;
import com.elasticpath.cmclient.catalog.editors.sku.ProductSkuEditor;
import com.elasticpath.cmclient.core.EpUiException;
import com.elasticpath.cmclient.core.editors.GuidEditorInput;
import com.elasticpath.cmclient.core.event.ChangeSetMemberSelectionProvider;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.cmclient.core.helpers.AbstractSearchRequestJob;
import com.elasticpath.cmclient.core.helpers.ProductSkuListener;
import com.elasticpath.cmclient.core.registry.ObjectRegistryListener;
import com.elasticpath.cmclient.core.views.AbstractSortListView;
import com.elasticpath.cmclient.core.dto.catalog.ProductSkuModel;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.search.query.SkuSearchCriteria;


/**
 * This view displays lists of productSku in a table format. The list of products can come from any source. For example, the results of a productSku
 * search can be displayed or a the products in a category may be displayed when the category is selected in another view.
 */
public abstract class AbstractSkuListView extends AbstractSortListView implements ProductSkuListener, 
	ObjectRegistryListener, ChangeSetMemberSelectionProvider {

	private static final Logger LOG = Logger.getLogger(AbstractSkuListView.class);

	private SkuSearchCriteria searchCriteria;

	private static final String ACTIVE_CHANGE_SET = "activeChangeSet"; //$NON-NLS-1$

	/**
	 * Constructor.
	 * @param tableName name of the table
	 */
	public AbstractSkuListView(final String tableName) {
		super(true, tableName);
	}

	/**
	 * The key string to store the product search criteria in the control data.
	 */
	public static final String SKU_SEARCH_CRITERIA = "skuSearchCriteria"; //$NON-NLS-1$

	// Actions
	private Action doubleClickAction;

	private void makeActions() {
		doubleClickAction = new DoubleClickAction();
	}

	@Override
	public void setFocus() {
		getViewer().getControl().setFocus();
	}

	@Override
	protected ITableLabelProvider getViewLabelProvider() {
		return new SkuListViewLabelProvider();
	}

	@Override
	protected String getPluginId() {
		return CatalogPlugin.PLUGIN_ID;
	}

	@Override
	protected void initializeViewToolbar() {
		makeActions();

		this.addDoubleClickAction(doubleClickAction);

		final Separator productActionSeparator = new Separator("productActionSeparator"); //$NON-NLS-1$

		getToolbarManager().add(productActionSeparator);
	}

	/**
	 * Open a product editor for the selected product.
	 */
	protected class DoubleClickAction extends Action {


		@Override
		public void run() {
			final ISelection selection = getViewer().getSelection();
			final Object obj = ((IStructuredSelection) selection).getFirstElement();

			if (obj instanceof ProductSku) {
				ProductSku productSku = (ProductSku) obj;
				Product product = productSku.getProduct();
				
				if (product.hasMultipleSkus()) { //Open SKU editor
					final IEditorInput editorInput = 
						new GuidEditorInput(productSku.getGuid(), determineEditorInputClass(ProductSku.class, productSku));
					try {
						getSite().getPage().openEditor(editorInput, ProductSkuEditor.PART_ID);
					} catch (final PartInitException e) {
						// Log the error and throw an unchecked exception
						LOG.error(e.getStackTrace());
						throw new EpUiException("Could not create Sku Editor", e); //$NON-NLS-1$
					}
				} else {
					final IEditorInput editorInput = new GuidEditorInput(product.getGuid(), Product.class);
					try {
						FormEditor productEditor = (FormEditor) getSite().getPage().openEditor(editorInput, ProductEditor.PART_ID);
						productEditor.setActivePage(ProductSkuDetailsPage.PAGE_ID);
					} catch (final PartInitException e) {
						// Log the error and throw an unchecked exception
						LOG.error(e.getStackTrace());
						throw new EpUiException("Could not create Product Editor", e); //$NON-NLS-1$
					}
				}
			}
		}
	}

	/**
	 * Get the search criteria.
	 *
	 * @return product search criteria
	 */
	public SkuSearchCriteria getSearchCriteria() {
		return searchCriteria;
	}

	/**
	 * Set the search criteria.
	 *
	 * @param searchCriteria the product search criteria
	 */
	public void setSearchCriteria(final SkuSearchCriteria searchCriteria) {
		this.searchCriteria = searchCriteria;
	}

	@Override
	protected Object[] getViewInput() {

		return null;
	}

	@Override
	public void productSkuChanged(final ItemChangeEvent<ProductSku> event) {
		refreshProductSku(event.getItem());
	}

	@Override
	public void productSkuSearchResultReturned(final SearchResultEvent<ProductSku> event) {
		// default is to do nothing
	}

	private void refreshProductSku(final ProductSku productSku) {
		for (TableItem item : getViewer().getTable().getItems()) {
			if (item.getData() instanceof ProductSkuModel 
					&& (((ProductSkuModel) item.getData())).getProductSku().getUidPk() == productSku.getUidPk()) {
				getViewer().refresh(item.getData());
			}
		}
	}

	@Override
	public AbstractSearchRequestJob < ? extends Persistable > getSearchRequestJob() {
		return null;
	}

	/**
	 * Returns product of <code>changeSetObjectSelection</code> which is <code>ProductSkuModel</code>.
	 * @param changeSetObjectSelection <code>ProductSkuModel</code> selection
	 * @return <code>Product</code>
	 */
	@Override
	public Object resolveObjectMember(final Object changeSetObjectSelection) {
		Object resolvedObject = null;
		if (changeSetObjectSelection instanceof ProductSkuModel) {
			ProductSku productSku = ((ProductSkuModel) changeSetObjectSelection).getProductSku();
			Product product = productSku.getProduct();
			if (product.hasMultipleSkus()) {
				resolvedObject = productSku;
			} else {
				resolvedObject = product;
			}
		}
		return resolvedObject;
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


}
