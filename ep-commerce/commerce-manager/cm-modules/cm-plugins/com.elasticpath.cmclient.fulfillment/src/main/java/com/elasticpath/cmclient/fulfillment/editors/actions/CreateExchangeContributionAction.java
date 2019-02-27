/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.fulfillment.editors.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Shell;

import com.elasticpath.cmclient.core.event.ItemChangeEvent;
import com.elasticpath.cmclient.core.helpers.LocalProductSkuLookup;
import com.elasticpath.cmclient.fulfillment.FulfillmentImageRegistry;
import com.elasticpath.cmclient.fulfillment.editors.order.OrderEditor;
import com.elasticpath.cmclient.fulfillment.event.FulfillmentEventService;
import com.elasticpath.cmclient.fulfillment.wizards.ExchangeWizard;
import com.elasticpath.cmclient.fulfillment.wizards.SubscribingDialog;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.service.catalog.ProductSkuLookup;

/**
 * Create Exchange Action.
 */
public class CreateExchangeContributionAction extends Action {

	private final Shell parentShell;
	
	private final Order order;
		
	private final OrderShipment orderShipment;
	
	private final OrderEditor editor;
	private ProductSkuLookup productSkuLookup;
	
	/**
	 * Constructor the create exchange action.
	 * 
	 * @param label string displayed for action text
	 * @param orderEditor editor that had launched the action
	 * @param orderShipment The physical order shipment
	 */
	public CreateExchangeContributionAction(final String label, final OrderEditor orderEditor, final OrderShipment orderShipment) {
		super(label, FulfillmentImageRegistry.IMAGE_EXCHANGE_CREATE);
		setToolTipText(label);
		
		this.parentShell = orderEditor.getSite().getShell();
		this.order = orderEditor.getModel();
		this.orderShipment = orderShipment;	
		this.editor = orderEditor;		
	}
	@Override
	public void run() {
		// Create the wizard
		ExchangeWizard wizard = ExchangeWizard.createExchangeWizard(orderShipment);
		// Create the wizard dialog
		SubscribingDialog dialog = new SubscribingDialog(parentShell, wizard);
		// Open the wizard dialog
		if (dialog.open() == 0) {			
			FulfillmentEventService.getInstance().fireOrderChangeEvent(new ItemChangeEvent<>(this, order));
			editor.reloadModel();
			editor.refreshEditorPages();
		}
	}
	
	/**
	 * Lazy loads a ProductSkuLookup.
	 *
	 * @return a product sku reader.
	 */
	protected ProductSkuLookup getProductSkuLookup() {
		if (productSkuLookup == null) {
			productSkuLookup = new LocalProductSkuLookup();
		}
		
		return productSkuLookup;
	}
}
