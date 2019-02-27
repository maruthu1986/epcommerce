/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.fulfillment.editors.actions;

import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.editors.order.dialog.ViewAllNoteDialog;
import com.elasticpath.domain.order.OrderEvent;

/**
 * Edit order note action.
 */
public class ViewAllNoteAction extends Action {

	private final Set<OrderEvent> orderEvents;

	/**
	 * Constructor.
	 *
	 * @param orderEvents the order events
	 */
	public ViewAllNoteAction(final Set<OrderEvent> orderEvents) {
		super(FulfillmentMessages.get().OrderActionViewAllNotes);
		setToolTipText(FulfillmentMessages.get().OrderActionViewAllNotes);
		this.orderEvents = orderEvents;
	}

	@Override
	public void run() {
		final ViewAllNoteDialog dialog =
			new ViewAllNoteDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
			this.orderEvents);
		dialog.open();

	}

}
