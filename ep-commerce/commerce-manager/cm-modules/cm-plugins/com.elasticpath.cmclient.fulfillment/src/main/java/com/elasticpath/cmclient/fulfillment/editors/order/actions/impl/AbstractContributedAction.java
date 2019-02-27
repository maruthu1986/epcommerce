/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.fulfillment.editors.order.actions.impl;

import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;

import com.elasticpath.cmclient.core.ui.framework.IEpLayoutComposite;
import com.elasticpath.cmclient.core.ui.framework.IEpLayoutData;
import com.elasticpath.cmclient.fulfillment.editors.order.actions.ContributedAction;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderSku;

/**
 * This class contains abstract methods that should be implemented by the concrete contributed action classes to provide necessary behaviour.
 */
public abstract class AbstractContributedAction implements ContributedAction {
	/** Button to be contributed. **/
	private Button button;

	/** button's default SelectionListener. **/
	private SelectionListener defaultListener;

	/** Order instance. **/
	private Order order;

	/** selected OrderSku instance. **/
	private OrderSku orderSku;

	/**
	 * @return true if this contributed action can be applied.
	 */
	protected abstract boolean canContributedActionApplied();

	/**
	 * Creates the button control.
	 * 
	 * @param dialogComposite a composite to create the button
	 * @param buttonData a layout data to create the button
	 */
	protected abstract void buildActionUiElements(IEpLayoutComposite dialogComposite, IEpLayoutData buttonData);

	/**
	 * Builds the default selection listener and attaches it to the button.
	 */
	protected abstract void buildDefaultListener();

	@Override
	public void init(final Order order, final OrderSku orderSku) {
		this.order = order;
		this.orderSku = orderSku;
	}
	
	@Override
	public final Button createActionControl(final IEpLayoutComposite dialogComposite,
											final IEpLayoutData buttonData) {

		if (!canContributedActionApplied()) {
			return null;
		}

		buildActionUiElements(dialogComposite, buttonData);
		buildDefaultListener();

		return button;
	}

	/**
	 * @return the button
	 */
	public Button getButton() {
		return button;
	}

	/**
	 * Sets the button.
	 * 
	 * @param button the button to set
	 */
	public void setButton(final Button button) {
		this.button = button;
	}

	/**
	 * @return the default listener.
	 */
	public SelectionListener getDefaultListener() {
		return defaultListener;
	}

	/**
	 * Sets the default listener.
	 * @param defaultListener selection listener to set 
	 */
	public void setDefaultListener(final SelectionListener defaultListener) {
		this.defaultListener = defaultListener;
	}

	/**
	 * @return the order
	 */
	public Order getOrder() {
		return order;
	}

	/**
	 * @return the order sku
	 */
	public OrderSku getOrderSku() {
		return orderSku;
	}

}
