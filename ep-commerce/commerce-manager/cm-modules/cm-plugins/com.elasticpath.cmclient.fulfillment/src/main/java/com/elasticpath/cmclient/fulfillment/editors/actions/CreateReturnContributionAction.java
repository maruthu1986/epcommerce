/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.fulfillment.editors.actions;

import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.fulfillment.FulfillmentImageRegistry;
import com.elasticpath.cmclient.fulfillment.editors.order.OrderEditor;
import com.elasticpath.cmclient.fulfillment.event.FulfillmentEventService;
import com.elasticpath.cmclient.fulfillment.wizards.ReturnWizard;
import com.elasticpath.cmclient.fulfillment.wizards.SubscribingDialog;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderShipment;

/**
 * Create Return Action.
 */
public class CreateReturnContributionAction extends org.eclipse.jface.action.Action {
	private final Shell parentShell;

	private final Order order;

	private final OrderEditor editor;

	private final OrderShipment orderShipment;

	/**
	 * Constructor the create return action.
	 * 
	 * @param label string displayed for action text
	 * @param orderEditor editor that had launched the action
	 * @param orderShipment The order shipment
	 */
	public CreateReturnContributionAction(final String label, final OrderEditor orderEditor, final OrderShipment orderShipment) {
		super(label, FulfillmentImageRegistry.IMAGE_RETURN_CREATE);
		setToolTipText(label);

		this.parentShell = orderEditor.getSite().getShell();
		this.order = orderEditor.getModel();
		this.orderShipment = orderShipment;		
		this.editor = orderEditor;
	}

	@Override
	public void run() {
		ReturnWizard wizard = ReturnWizard.createReturnWizard(order, orderShipment);
		// Create the wizard dialog
		SubscribingDialog dialog = new SubscribingDialog(parentShell, wizard);
		// Open the wizard dialog
		if (dialog.open() == 0) {			
			FulfillmentEventService.getInstance().fireOrderChangeEvent(new ItemChangeEvent<>(this, order));
			editor.reloadModel();
			editor.refreshEditorPages();
		}

	}
}
