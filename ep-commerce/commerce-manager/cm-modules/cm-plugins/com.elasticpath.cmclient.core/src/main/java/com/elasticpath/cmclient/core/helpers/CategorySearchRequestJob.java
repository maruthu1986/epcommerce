/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.helpers;

import java.util.List;

import org.apache.log4j.Logger;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.event.EventType;
import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.cmclient.core.service.CatalogEventService;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.service.catalog.CategoryLookup;

/**
 * This class represents a job responsible for retrieving categories from the database.
 */
public class CategorySearchRequestJob extends AbstractSearchRequestJob<Category> {

	private static final Logger LOG = Logger.getLogger(CategorySearchRequestJob.class);

	private final CategoryLookup categoryLookup;

	/**
	 * Default constructor.
	 * 
	 */
	public CategorySearchRequestJob() {
		super();
		categoryLookup = ServiceLocator.getService(ContextIdNames.CATEGORY_LOOKUP);
	}

	@Override
	public void fireItemsUpdated(final List<Category> itemList, final int startIndex, final int totalFound) {
		SearchResultEvent<Category> event = new SearchResultEvent<Category>(this, itemList, startIndex, totalFound, EventType.SEARCH);
		CatalogEventService.getInstance().notifyCategorySearchResultReturned(event);
	}

	/**
	 * Gets a list of {@link Category} with the given <code>uidList</code>.
	 * 
	 * @param uidList a list of {@link Category} UIDs
	 * @return a list of {@link Category}s
	 */
	public List<Category> getItems(final List<Long> uidList) {
		if (LOG.isDebugEnabled()) {
			LOG.debug(String.format("Converting %1$S UID(s) to categories.", uidList.size())); //$NON-NLS-1$
		}
		return categoryLookup.findByUids(uidList);
	}

}
