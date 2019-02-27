/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.helpers;

import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;

/**
 * Abstract class implementing listeners for CatalogEventService and passing the various notifications for
 * handling to two methods. Simplifies interface for classes which need to listen to multiple catalog events.
 * Expected use is to have a private class in the class which needs to listen, which extends this class. 
 *
 */
public abstract class AbstractCatalogEventListener implements ProductListener, ProductSkuListener, CategoryListener, CatalogListener {

	@Override
	public void productChanged(final ItemChangeEvent<Product> event) {
		itemEventOccured(event);
	}

	@Override
	public void productSearchResultReturned(final SearchResultEvent<Product> event) {
		searchEventOccurred(event);
	}

	@Override
	public void productSkuChanged(final ItemChangeEvent<ProductSku> event) {
		itemEventOccured(event);
	}

	@Override
	public void productSkuSearchResultReturned(final SearchResultEvent<ProductSku> event) {
		searchEventOccurred(event);
	}

	@Override
	public void categoryChanged(final ItemChangeEvent<Category> event) {
		itemEventOccured(event);
	}

	@Override
	public void categorySearchResultReturned(final SearchResultEvent<Category> event) {
		searchEventOccurred(event);
	}

	@Override
	public void catalogChanged(final ItemChangeEvent<Catalog> event) {
		itemEventOccured(event);
	}
	
	/**
	 * Method called for any typeChanged events.
	 * @param event the event
	 */
	public abstract void itemEventOccured(final ItemChangeEvent< ? > event);
	
	/**
	 * Method called for any typeSearchResult events.
	 * @param event the event
	 */
	public abstract void searchEventOccurred(final SearchResultEvent< ? > event);
}
