/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.core.helpers;

import java.util.List;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.event.EventType;
import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.cmclient.core.service.CatalogEventService;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.service.catalog.ProductSkuLookup;

/**
 * A job responsible for retrieving skus from the database.
 */
public class SkuSearchRequestJob extends AbstractSearchRequestJob<ProductSku> implements ISearchJobSource {

	private final ProductSkuLookup skuReader;

	private Object source;


	/**
	 * Constructor.
	 */
	public SkuSearchRequestJob() {
		super();
		skuReader = ServiceLocator.getService(ContextIdNames.PRODUCT_SKU_LOOKUP);
	}

	@Override
	public void fireItemsUpdated(final List<ProductSku> itemList, final int startIndex, final int totalFound) {
		Object source;
		if (this.getSource() == null) {
			source = this;
		} else {
			source = this.getSource();
		}
		SearchResultEvent<ProductSku> event = new SearchResultEvent(source, itemList, startIndex, totalFound, EventType.SEARCH);
		CatalogEventService.getInstance().notifyProductSkuSearchResultReturned(event);
	}

	@Override
	public List<ProductSku> getItems(final List<Long> uidList) {
		return skuReader.findByUids(uidList);
	}

	@Override
	public Object getSource() {
		return source;
	}

	@Override
	public void setSource(final Object source) {
		this.source = source;
	}

}
