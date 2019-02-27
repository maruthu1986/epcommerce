/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
/**
 * 
 */
package com.elasticpath.cmclient.pricelistassignments.event;

import com.elasticpath.cmclient.core.CmSingletonUtil;
import com.elasticpath.cmclient.core.event.EventType;
import com.elasticpath.cmclient.core.event.UIEvent;
import com.elasticpath.cmclient.pricelistassignments.controller.PriceListAssignmentsSearchController;
import com.elasticpath.cmclient.pricelistassignments.model.PriceListAssigmentsSearchTabModel;
import com.elasticpath.domain.pricing.PriceListAssignment;

/**
 * A utility class for price list assignment actions.
 */
@SuppressWarnings({ "PMD.UseSingleton", "PMD.UseUtilityClass" })
public class PriceListAssignmentChangeEventUtil {

	/**
	 * fire event for any success operation.
	 * @param eventType event type for fire
	 * @param priceListAssignment object for event
	 */
	public static void fireEvent(final EventType eventType, final PriceListAssignment priceListAssignment) {
		UIEvent<PriceListAssigmentsSearchTabModel> eventForList = new UIEvent<>(
				new PriceListAssigmentsSearchTabModel(),
				eventType,
				false
		);

		UIEvent<PriceListAssignment> event = new UIEvent<>(
				priceListAssignment,
				eventType,
				false
		);

		CmSingletonUtil.getSessionInstance(PriceListAssignmentsSearchController.class).onEvent(eventForList);
		CmSingletonUtil.getSessionInstance(PriceListAssignmentsSearchController.class).onEvent(event);
	}
}
