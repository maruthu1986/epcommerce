/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */

package com.elasticpath.cmclient.catalog.views;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.TableItem;

import com.elasticpath.cmclient.catalog.editors.model.ProductModelController;
import com.elasticpath.cmclient.core.event.BrowseResultEvent;
import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.cmclient.core.helpers.ProductBrowseListener;
import com.elasticpath.cmclient.core.helpers.ProductListener;
import com.elasticpath.cmclient.core.dto.catalog.ProductModel;
import com.elasticpath.domain.catalog.Product;

/**
 * The content provider class is responsible for providing objects to the view. It can wrap existing objects in adapters or simply return objects
 * as-is. These objects may be sensitive to the current input of the view, or ignore it and always show the same content.
 */
public class ProductListViewContentProvider implements IStructuredContentProvider, ProductListener, ProductBrowseListener {

	private final AbstractProductListView productListView;

	private final ProductModelController productModelController = new ProductModelController();

	/**
	 * Get the product list view.
	 * @return the product list view
	 */
	public AbstractProductListView getProductListView() {
		return productListView;
	}

	/**
	 * Constructor.
	 *
	 * @param productListView the list view that have this content provider
	 */
	public ProductListViewContentProvider(final AbstractProductListView productListView) {

		this.productListView = productListView;


	}

	@Override
	public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
		// Do nothing
	}


	@Override
	public Object[] getElements(final Object inputElement) {

		if (inputElement instanceof List) {
			final List<Product> productList = (List<Product>) inputElement;
			return productList.toArray(new Product[productList.size()]);
		} else if (inputElement instanceof Object[]) {
			return (Object[]) inputElement;
		}
		return new Object[0];
	}

	@Override
	public void productSearchResultReturned(final SearchResultEvent<Product> event) {
		//Empty
	}

	@Override
	public void productBrowseResultReturned(final BrowseResultEvent<Product> event) {
		//Empty
	}


	/**
	 * Show the return message.
	 * @param message the message to be display
	 */
	protected void handleSearchResultMessage(final String message) {
		if (productListView.getResultsCount() == 0) {
			productListView.showMessage(message);
		} else {
			productListView.hideErrorMessage();
		}
	}

	@Override
	public void productChanged(final ItemChangeEvent<Product> event) {
		final Product changedProduct = event.getItem();

		for (final TableItem currTableItem : productListView.getViewer().getTable().getItems()) {
			final Product currProduct = ((ProductModel) currTableItem.getData()).getProduct();
			if (currProduct.getUidPk() == changedProduct.getUidPk()) {
				currTableItem.setData(changedProduct);
				productListView.getViewer().refresh();
				break;
			}
		}
	}

	/**
	 * Wraps <code>Product</code> objects into <code>ProductModel</code> to be displayed on UI.
	 *
	 * @param products the list of {@link Product} objects to wrap
	 * @return array of models encapsulating wrapped products
	 */
	protected ProductModel[] wrapProducts(final List<Product> products) {
		return productModelController.buildLiteProductModels(products);
	}

	
	@Override
	public void dispose() {
		//Empty

	}

}
