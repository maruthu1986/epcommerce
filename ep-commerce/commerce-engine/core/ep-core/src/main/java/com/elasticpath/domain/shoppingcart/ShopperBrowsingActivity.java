/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.domain.shoppingcart;

import com.elasticpath.domain.catalogview.CatalogViewResultHistory;

/**
 * Contains a record of a Shopper's browsing activity.
 */
public interface ShopperBrowsingActivity {

	/**
	 * Returns the browsing result history stored. If none is stored, a new one will be created and returned.
	 *
	 * @return the browsing result history
	 */
	CatalogViewResultHistory getBrowsingResultHistory();

	/**
	 * Returns the catalog view result history stored. A catalog view result history might be a search or a browsing. If none is stored, return
	 * <code>null</code>
	 *
	 * @return the catalog view result history
	 */
	CatalogViewResultHistory getCatalogViewResultHistory();

	/**
	 * Returns the search result history stored. If none is stored, a new one will be created and returned.
	 *
	 * @return the search result history
	 */
	CatalogViewResultHistory getSearchResultHistory();

	/**
	 * Get the View History of the user from the shopping cart.
	 *
	 * @return the ViewHistory
	 */
	ViewHistory getViewHistory();

}
