/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.fulfillment.editors.order.actions;

import java.util.List;

import org.eclipse.swt.widgets.Button;

import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.fulfillment.editors.order.ui.ManagedModel;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderSku;

/**
 * Extension interface for the OrderItemFieldValueDialog extension point.
 */
public interface ContributedAction {
	
	/**
	 * Init method. 
	 * @param order the order
	 * @param orderSku the selected order sku
	 */
	void init(Order order, OrderSku orderSku);
	
	/**
	 * This method should create a Button that will be contributed to the designated area of the OrderItemFieldValueDialog together with
	 * it's link SelectionAdapter, that will perform necessary actions.
	 * 
	 * @param dialogComposite the composite to create a button
	 * @param buttonData the layout data to create a button
	 * @return the created Button instance
	 */
	Button createActionControl(IEpLayoutComposite dialogComposite, IEpLayoutData buttonData);

	/**
	 * Filter display data to mask or remove or add any data.
	 * @param displayData source display list
	 * @return modified display list
	 */
	List<ManagedModel<String, String>> filterDisplayData(List<ManagedModel<String, String>> displayData);
}
