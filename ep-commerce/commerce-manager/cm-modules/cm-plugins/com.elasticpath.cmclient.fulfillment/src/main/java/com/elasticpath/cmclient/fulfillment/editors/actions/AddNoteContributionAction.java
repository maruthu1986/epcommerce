/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.fulfillment.editors.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;

import com.elasticpath.cmclient.core.CoreImageRegistry;
import com.elasticpath.cmclient.fulfillment.FulfillmentMessages;
import com.elasticpath.cmclient.fulfillment.editors.order.AbstractOrderPage;
import com.elasticpath.cmclient.fulfillment.editors.order.OrderEditor;
import com.elasticpath.cmclient.fulfillment.editors.order.dialog.AddNoteDialog;
import com.elasticpath.domain.order.Order;

/**
 * Add Note Action
 */
/**
 * Begin the process for creating a new role.
 */
public class AddNoteContributionAction extends Action {

	private final AbstractOrderPage orderPage;
	
	/**
	 * Constructor the new add note action.
	 * 
	 * @param orderPage the AbstractOrderPage
	 * 
	 */
	public AddNoteContributionAction(final AbstractOrderPage orderPage) {
		super(FulfillmentMessages.get().OrderActionAddNode, CoreImageRegistry.IMAGE_ADD_NOTE);
		setToolTipText(FulfillmentMessages.get().OrderActionAddNode);
		this.orderPage = orderPage;
	}

	@Override
	public void run() {
		int result = new AddNoteDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(), 
			(Order) this.orderPage.getEditor().getModel()).open();
		if (result == Window.OK) {
			((OrderEditor) this.orderPage.getEditor()).fireAddNoteChanges();
			orderPage.getEditor().controlModified();
			
		}
	}
}
