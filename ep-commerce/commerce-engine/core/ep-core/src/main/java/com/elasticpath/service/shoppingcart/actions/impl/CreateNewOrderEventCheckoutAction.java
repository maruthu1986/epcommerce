/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.service.shoppingcart.actions.impl;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.core.messaging.order.OrderEventType;
import com.elasticpath.messaging.EventMessage;
import com.elasticpath.messaging.EventMessagePublisher;
import com.elasticpath.messaging.factory.EventMessageFactory;
import com.elasticpath.service.shoppingcart.actions.CheckoutActionContext;
import com.elasticpath.service.shoppingcart.actions.ReversibleCheckoutAction;

/**
 * Checkout action to create and publish an {@link OrderEventType} {@link EventMessage}.
 */
public class CreateNewOrderEventCheckoutAction implements ReversibleCheckoutAction {

	private EventMessageFactory eventMessageFactory;

	private EventMessagePublisher eventMessagePublisher;

	@Override
	public void execute(final CheckoutActionContext context) throws EpSystemException {
		try {
			final EventMessage orderCreatedEventMessage = getEventMessageFactory().createEventMessage(OrderEventType.ORDER_CREATED,
					context.getOrder().getOrderNumber());

			getEventMessagePublisher().publish(orderCreatedEventMessage);
		} catch (Exception e) {
			throw new EpSystemException("Failed to publish Event Message", e);
		}
	}

	@Override
	public void rollback(final CheckoutActionContext context) throws EpSystemException {
		// NO OP
	}

	public void setEventMessageFactory(final EventMessageFactory eventMessageFactory) {
		this.eventMessageFactory = eventMessageFactory;
	}

	protected EventMessageFactory getEventMessageFactory() {
		return this.eventMessageFactory;
	}

	public void setEventMessagePublisher(final EventMessagePublisher eventMessagePublisher) {
		this.eventMessagePublisher = eventMessagePublisher;
	}

	protected EventMessagePublisher getEventMessagePublisher() {
		return eventMessagePublisher;
	}

}
