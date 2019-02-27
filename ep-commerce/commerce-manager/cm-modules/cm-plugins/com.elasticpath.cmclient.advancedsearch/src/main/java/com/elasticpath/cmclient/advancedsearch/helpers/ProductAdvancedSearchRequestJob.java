/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.advancedsearch.helpers;

import java.util.List;

import com.elasticpath.cmclient.advancedsearch.service.AdvancedSearchEventService;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.event.EventType;
import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductLoadTuner;
import com.elasticpath.service.catalog.ProductService;

/**
 * Implementation of <code>AbstractAdvancedSearchRequestJob</code> for product.
 */
public class ProductAdvancedSearchRequestJob extends AbstractAdvancedSearchRequestJob<Product> {
	
	private final ProductService productService;

	/**
	 * Constructs the product advanced search request job.
	 * 
	 * @param pageSize the page size for displaying products
	 */
	public ProductAdvancedSearchRequestJob(final int pageSize) {
		super(pageSize);
		productService = ServiceLocator.getService(ContextIdNames.PRODUCT_SERVICE);
	}

	@Override
	protected void fireItemsUpdated(final List<Product> itemList, final int startIndex, final int totalFound) {
		SearchResultEvent<Product> event = new SearchResultEvent<>(this, itemList, startIndex, totalFound, EventType.SEARCH);
		AdvancedSearchEventService.getInstance().notifyProductSearchResultReturned(getListenerId(), event);
	}

	@Override
	protected List<Product> getItems(final List<Long> uidList) {
		final ProductLoadTuner productLoadTuner = ServiceLocator.getService(ContextIdNames.PRODUCT_LOAD_TUNER);
		productLoadTuner.setLoadingCategories(true);
		return productService.findByUids(uidList, productLoadTuner);
	}

}
