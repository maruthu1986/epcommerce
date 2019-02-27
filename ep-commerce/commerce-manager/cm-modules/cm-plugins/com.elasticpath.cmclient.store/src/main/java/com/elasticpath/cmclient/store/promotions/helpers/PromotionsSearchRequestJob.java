/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.store.promotions.helpers;

import java.util.List;

import org.apache.log4j.Logger;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.event.EventType;
import com.elasticpath.cmclient.core.event.SearchResultEvent;
import com.elasticpath.cmclient.core.helpers.AbstractSearchRequestJob;
import com.elasticpath.cmclient.store.promotions.event.PromotionsEventService;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.service.rules.RuleService;

/**
 * This class represents a job responsible for retrieving promotion rules from the database.
 */
public class PromotionsSearchRequestJob extends AbstractSearchRequestJob<Rule> {
	
	private static final Logger LOG = Logger.getLogger(PromotionsSearchRequestJob.class);
	
	private final RuleService ruleService;

	/**
	 * Default constructor.
	 * 
	 */
	public PromotionsSearchRequestJob() {
		super();
		ruleService = ServiceLocator.getService(ContextIdNames.RULE_SERVICE);
	}

	@Override
	public void fireItemsUpdated(final List<Rule> itemList, final int startIndex, final int totalFound) {
		SearchResultEvent<Rule> event = new SearchResultEvent<>(this, itemList, startIndex, totalFound, EventType.SEARCH);
		PromotionsEventService.getInstance().firePromotionSearchResultEvent(event);
	}

	@Override
	public List<Rule> getItems(final List<Long> uidList) {
		if (LOG.isDebugEnabled()) {
			LOG.debug(String.format("Converting %1$S UID(s) to rules.", uidList.size())); //$NON-NLS-1$
		}
		return ruleService.findByUids(uidList);
	}
}
