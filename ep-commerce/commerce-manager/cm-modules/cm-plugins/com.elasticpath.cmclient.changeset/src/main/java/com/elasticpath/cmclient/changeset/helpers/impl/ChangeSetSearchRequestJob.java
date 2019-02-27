/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.changeset.helpers.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.elasticpath.cmclient.changeset.event.ChangeSetEventService;
import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.cmclient.core.helpers.AbstractSearchRequestJob;
import com.elasticpath.cmclient.core.search.SearchJob;
import com.elasticpath.domain.changeset.ChangeSet;
import org.eclipse.swt.widgets.Display;

/**
 * This class represents a job responsible for retrieving Change Sets.
 */
public class ChangeSetSearchRequestJob extends AbstractSearchRequestJob<ChangeSet> {

	/**
	 * Default constructor.
	 * 
	 */
	public ChangeSetSearchRequestJob() {
		super();
	}

	/**
	 * .
	 * @param itemList the list
	 * @param startIndex start index
	 * @param totalFound total found
	 */
	public void fireItemsUpdated(final List<ChangeSet> itemList, final int startIndex, final int totalFound) {
		SearchResultEvent<ChangeSet> searchResultEvent = new SearchResultEvent<ChangeSet>(
				this, new ArrayList<ChangeSet>(itemList), startIndex, totalFound, 
					com.elasticpath.cmclient.core.event.EventType.SEARCH);	

		ChangeSetEventService.getInstance().fireChangeSetSearchEvent(searchResultEvent);
	}

	/**
	 * This should not be called.
	 * 
	 * @param uidList the list of uids to ignore.
	 * @return an empty list
	 */
	public List<ChangeSet> getItems(final List<Long> uidList) {
		return Collections.emptyList();
	}
	
	@Override
	protected SearchJob getSearchJob(final Display display) {
		return new ChangeSetSearchJob(this, display);
	}
}
