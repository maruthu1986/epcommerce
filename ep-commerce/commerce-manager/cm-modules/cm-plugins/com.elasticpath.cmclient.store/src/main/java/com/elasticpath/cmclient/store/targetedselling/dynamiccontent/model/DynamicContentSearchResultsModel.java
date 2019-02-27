/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.store.targetedselling.dynamiccontent.model;

import java.util.Collection;

import com.elasticpath.domain.contentspace.DynamicContent;

/**
 * This model keeps track of search results for Dynamic Contents.
 */
public interface DynamicContentSearchResultsModel {
	/**
	 * Get the dynamic content collection. 
	 * @return Collection of dynamic content
	 */
	Collection<DynamicContent> getDynamicContentSearchResult();
	/**
	 * Set the dynamic content collection.
	 * @param dynamicContentCollection dynamic content collection.
	 */
	void setDynamicContentSearchResult(Collection<DynamicContent> dynamicContentCollection);

}
