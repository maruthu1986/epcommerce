/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.store.promotions;

import org.eclipse.swt.widgets.Control;
 
/**
 * A strategy knowing how to how the model before a control.
 */
public interface CouponConfigPageModelUpdateStrategy {
	/**
	 * The control to update.
	 * 
	 * @param control the control.
	 */
	void updateModel(Control control);
}
