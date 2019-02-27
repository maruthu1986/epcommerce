/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.fulfillment.editors.actions;

import org.eclipse.jface.action.Action;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.editors.order.AbstractOrderPage;
import com.elasticpath.cmclient.fulfillment.editors.order.OrderEditor;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderLock;
import com.elasticpath.service.order.OrderLockService;

/**
 * This action removes the lock on an Order. 
 */
public class UnlockOrderContributionAction extends Action {

	private final AbstractOrderPage orderPage;
	
	/**
	 * Constructor the new unlock order action.
	 * 
	 * @param orderPage the AbstractOrderPage
	 */
	public UnlockOrderContributionAction(final AbstractOrderPage orderPage) {
		super(FulfillmentMessages.get().OrderActionUnlockOrder, CoreImageRegistry.IMAGE_UNLOCK);
		setToolTipText(FulfillmentMessages.get().OrderActionUnlockOrder);
		this.orderPage = orderPage;
	}

	@Override
	public void run() {
		final OrderLockService orderLockService =
				ServiceLocator.getService(ContextIdNames.ORDER_LOCK_SERVICE);
		final Order order = ((OrderEditor) orderPage.getEditor()).getModel();
		final OrderLock orderLock = orderLockService.getOrderLock(order);
		if (orderLock != null) {
			orderLockService.forceReleaseOrderLock(orderLock);
		}
		((OrderEditor) orderPage.getEditor()).setOrderLock(null);
		((OrderEditor) orderPage.getEditor()).isOrderLocked(false);
		orderPage.setUnlockOrderActionEnabled(false);
		orderPage.setLockedOrderTitle(false);
		orderPage.getEditor().refreshEditorPages();
	}
}
