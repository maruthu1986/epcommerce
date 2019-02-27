/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.store.targetedselling.conditionalexpression.event;

import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.domain.rules.Rule;

/**
 * This interface must be implemented by part that need to be notified on condition search result events.
 */
public interface ConditionsEventListener {

	/**
	 * The class implementing that interface will receive an event notifying for new results.
	 * 
	 * @param event instance of {@link SearchResultEvent}
	 */
	void searchResultsUpdate(SearchResultEvent<Rule> event);

	/**
	 * Notifies for a changed condition.
	 * 
	 * @param event conditions change event
	 */
	void conditionChanged(ConditionsChangeEvent event);
}
