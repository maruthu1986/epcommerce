/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.changeset.editors;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 *	 Builder for filters. 
 */
public class ChangeSetObjectFilterFactory {

	
	private final List<ViewerFilter> viewerFilters;
	/**
	 * Default Constructor.
	 */
	public ChangeSetObjectFilterFactory() {
		viewerFilters = new ArrayList<>();
		createFilters();
	}
	
	/**
	 * Creates the viewer filters.
	 */
	private void createFilters() {
		ChangeSetObjectTypeFilter skuOptionValueFilter = new ChangeSetObjectTypeFilter("Sku Option Value2"); //$NON-NLS-1$
		viewerFilters.add(skuOptionValueFilter);
	}
	
	/**
	 *
	 * @return the filters.
	 */
	public ViewerFilter[] getFilters() {
		return viewerFilters.toArray(new ViewerFilter[viewerFilters.size()]);
	}

}
