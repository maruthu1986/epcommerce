/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.fulfillment.editors.actions;

import org.eclipse.jface.action.Action;

import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.fulfillment.FulfillmentImageRegistry;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.editors.order.AbstractOrderPage;
import com.elasticpath.cmclient.fulfillment.editors.order.OrderEditor;
import com.elasticpath.cmclient.fulfillment.event.FulfillmentEventService;
import com.elasticpath.cmclient.fulfillment.wizards.RefundWizard;
import com.elasticpath.cmclient.fulfillment.wizards.SubscribingDialog;
import com.elasticpath.domain.order.Order;

/**
 * Create refund action.
 */
public class CreateRefundContributionAction extends Action {

	private final OrderEditor orderEditor;

	/**
	 * Constructor the new create refund action.
	 * 
	 * @param page order page.
	 * @param label string displayed for action text
	 */
	public CreateRefundContributionAction(final AbstractOrderPage page, final String label) {
		super(label, FulfillmentImageRegistry.IMAGE_REFUND_CREATE);
		setToolTipText(label);

		this.orderEditor = (OrderEditor) page.getEditor();
	}

	@Override
	public void run() {
		if (!orderEditor.openDirtyEditorWarning(FulfillmentMessages.get().OrderEditor_CreateRefund_EditorDirtyTitle,
				FulfillmentMessages.get().OrderEditor_CreateRefund_EditorDirtyMessage)) {

			final Order order = orderEditor.getModel();
			RefundWizard refundWizard = new RefundWizard(order);
			SubscribingDialog dialog = new SubscribingDialog(orderEditor.getSite().getShell(), refundWizard);

			if (dialog.open() == 0) {
				FulfillmentEventService.getInstance().fireOrderChangeEvent(new ItemChangeEvent<>(this, order));
				orderEditor.reloadModel();
				orderEditor.refreshEditorPages();
			}
		}
	}
}
