/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */

package com.elasticpath.cmclient.catalog.views;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.TableItem;

import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.cmclient.core.helpers.ProductSkuListener;
import com.elasticpath.domain.catalog.ProductSku;

/**
 * The content provider class is responsible for providing objects to the view. It can wrap existing objects in adapters or simply return objects
 * as-is. These objects may be sensitive to the current input of the view, or ignore it and always show the same content.
 */
public class SkuListViewContentProvider implements IStructuredContentProvider, ProductSkuListener {

	private final AbstractSkuListView skuListView;

	/**
	 * Get the product list view.
	 * @return the product list view
	 */
	public AbstractSkuListView getProductSkuListView() {
		return skuListView;
	}

	/**
	 * Constructor.
	 *
	 * @param skuListView the list view that have this content provider
	 */
	public SkuListViewContentProvider(final AbstractSkuListView skuListView) {
		this.skuListView = skuListView;
	}

	@Override
	public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
		// Do nothing
	}

	@Override
	public Object[] getElements(final Object inputElement) {
		if (inputElement instanceof List< ? >) {
			final List<ProductSku> productSkuList = (List<ProductSku>) inputElement;
			return productSkuList.toArray(new ProductSku[productSkuList.size()]);
		} else if (inputElement instanceof Object[]) {
			return (Object[]) inputElement;
		}
		return new Object[0];
	}

	@Override
	public void productSkuSearchResultReturned(final SearchResultEvent<ProductSku> event) {
		//empty
	}

	/**
	 * Show the return message.
	 * @param message the message to be display
	 */
	protected void handleSearchResultMessage(final String message) {
		if (skuListView.getResultsCount() == 0) {
			skuListView.showMessage(message);
		} else {
			skuListView.hideErrorMessage();
		}
	}

	@Override
	public void productSkuChanged(final ItemChangeEvent<ProductSku> event) {
		final ProductSku changedSku = event.getItem();

		for (final TableItem currTableItem : skuListView.getViewer().getTable().getItems()) {
			final ProductSku currSku = (ProductSku) currTableItem.getData();
			if (currSku.getUidPk() == changedSku.getUidPk()) {
				currTableItem.setData(changedSku);
				skuListView.getViewer().refresh();
				break;
			}
		}
	}

	@Override
	public void dispose() {
		//Empty
	}
}
