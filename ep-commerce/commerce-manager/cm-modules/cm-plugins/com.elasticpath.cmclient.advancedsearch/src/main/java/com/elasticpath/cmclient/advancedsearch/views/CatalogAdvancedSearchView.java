/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.advancedsearch.views;

import com.elasticpath.domain.advancedsearch.AdvancedQueryType;

/**
 * The <code>AdvancedSearchView</code> is used to build, save and execute EpQL search queries.
 */
public class CatalogAdvancedSearchView extends AbstractAdvancedSearchView {

	/**
	 * AdvancedSearchView ID specified in the plugin.xml file. It is the same as the class name
	 */
	public static final String VIEW_ID = CatalogAdvancedSearchView.class.getName();

	@Override
	public AdvancedQueryType[] getQueryTypes() {
		return new AdvancedQueryType[] { AdvancedQueryType.PRODUCT };
	}

	@Override
	protected String getPartId() {
		return VIEW_ID;
	}
}
