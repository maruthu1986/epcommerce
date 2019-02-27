/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.service.shoppingcart.actions.impl;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.domain.event.EventOriginatorHelper;
import com.elasticpath.service.shoppingcart.actions.CheckoutActionContext;
import com.elasticpath.service.shoppingcart.actions.ReversibleCheckoutAction;

/**
 * Checkout Action to set the transient event originator field on an order.
 */
public class SetOrderEventOriginatorCheckoutAction implements ReversibleCheckoutAction {
	
	private EventOriginatorHelper eventOriginatorHelper;

	@Override
	public void execute(final CheckoutActionContext context) throws EpSystemException {
		context.getOrder().setModifiedBy(getEventOriginatorHelper().getCustomerOriginator(context.getCustomer()));
	}

	@Override
	public void rollback(final CheckoutActionContext context) throws EpSystemException {
		// Do nothing
	}

	public EventOriginatorHelper getEventOriginatorHelper() {
		return eventOriginatorHelper;
	}

	public void setEventOriginatorHelper(final EventOriginatorHelper eventOriginatorHelper) {
		this.eventOriginatorHelper = eventOriginatorHelper;
	}
}
