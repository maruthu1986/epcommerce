/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.domain.shoppingcart.impl;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalogview.CatalogViewResultHistory;
import com.elasticpath.domain.shoppingcart.ShopperBrowsingActivity;
import com.elasticpath.domain.shoppingcart.ViewHistory;

/**
 * Default implementation of {@link ShopperBrowsingActivity}.
 */
public class ShopperBrowsingActivityImpl implements ShopperBrowsingActivity {

	private CatalogViewResultHistory searchResultHistory;
	private CatalogViewResultHistory browsingResultHistory;
	private ViewHistory viewHistory;
	private BeanFactory beanFactory;

	@Override
	public CatalogViewResultHistory getSearchResultHistory() {
		if (searchResultHistory == null) {
			searchResultHistory = getBeanFactory().getBean(ContextIdNames.CATALOG_VIEW_RESULT_HISTORY);
		}

		// To help figuring out how a customer reaches to a product, we only maintain one kind of catalog view result history,
		// either search or browsing. This policy also happens to release memory earlier.
		browsingResultHistory = null;
		return searchResultHistory;
	}

	@Override
	public CatalogViewResultHistory getBrowsingResultHistory() {
		if (browsingResultHistory == null) {
			browsingResultHistory = getBeanFactory().getBean(ContextIdNames.CATALOG_VIEW_RESULT_HISTORY);
		}

		// To help figuring out how a customer reaches to a product, we only maintain one kind of catalog view result history,
		// either search or browsing. This policy also happens to release memory earlier.
		searchResultHistory = null;
		return browsingResultHistory;
	}

	@Override
	public CatalogViewResultHistory getCatalogViewResultHistory() {
		if (browsingResultHistory != null) {
			return browsingResultHistory;
		}

		if (searchResultHistory != null) {
			return searchResultHistory;
		}

		return null;
	}

	@Override
	public ViewHistory getViewHistory() {
		if (viewHistory == null) {
			viewHistory = getBeanFactory().getBean("viewHistory");
		}
		return viewHistory;
	}

	protected BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

}
