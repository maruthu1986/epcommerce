/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.catalogview;

import com.elasticpath.domain.catalogview.CatalogViewResultHistory;
import com.elasticpath.domain.catalogview.browsing.BrowsingRequest;
import com.elasticpath.domain.catalogview.browsing.BrowsingResult;
import com.elasticpath.domain.shoppingcart.ShoppingCart;

/**
 * Provide catalog browsing service.
 */
public interface BrowsingService {

	/**
	 * Perform browsing based on the given browsing request and returns the browsing result.
	 * <p>
	 * By giving the previous browsing result history, you may get response quicker. If you don't have it, give a <code>null</code>. It doesn't
	 * affect the result.
	 * <p>
	 * By giving a shopping cart, promotion rules will be applied to the returned products.
	 * <p>
	 *
	 * @param browsingRequest the browsing request
	 * @param previousBrowsingResultHistory the previous browsing results, give <code>null</code> if you don't have it
	 * @param shoppingCart the shopping cart, give <code>null</code> if you don't have it
	 * @param loadProductAssociations true if product associations should be loaded for each product
	 * @param pageNumber the current page number
	 * @return a <code>BrowsingResult</code> instance
	 */
	BrowsingResult browsing(BrowsingRequest browsingRequest, CatalogViewResultHistory previousBrowsingResultHistory, ShoppingCart shoppingCart,
			boolean loadProductAssociations, int pageNumber);

}
