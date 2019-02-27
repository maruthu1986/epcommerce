/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.epcommerce.events;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.FieldOption;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

import com.elasticpath.rest.relos.rs.events.AbstractEventHandler;
import com.elasticpath.rest.relos.rs.events.RoleTransitionEvent;
import com.elasticpath.rest.relos.rs.events.ScopedEventEntityHandler;

/**
 * Handle role transition events by running ScopedEntityEventHandlers in service ranking order.
 */
@Component(
		service = EventHandler.class,
		property = EventConstants.EVENT_TOPIC + "=events/" + RoleTransitionEvent.EVENT_TYPE)
public class MasterRoleTransitionEventHandler extends AbstractEventHandler<RoleTransitionEvent> {


	@Reference (
			cardinality = ReferenceCardinality.MULTIPLE,
			policy = ReferencePolicy.DYNAMIC,
			policyOption = ReferencePolicyOption.GREEDY,
			fieldOption = FieldOption.UPDATE,
			target = "(eventType=roleTransitionEvent)")
	private final Collection<ScopedEventEntityHandler<RoleTransitionEvent>> handlers = new CopyOnWriteArrayList<>();


	/**
	 * Constructor.
	 */
	public MasterRoleTransitionEventHandler() {
		super(RoleTransitionEvent.class);
	}

	@Override
	public void handleEvent(final String scope, final RoleTransitionEvent event) {
		for (ScopedEventEntityHandler<RoleTransitionEvent> handler : handlers) {
			handler.handleEvent(scope, event);
		}
	}
}
