/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
/**
 * 
 */
package com.elasticpath.cmclient.fulfillment.editors.order.ui;



/**
 * A managed table row. 
 * @param <KEY> a row data key type
 * @param <VALUE> a row data value type
 */
public interface ManagedModel<KEY, VALUE> {

	/**
	 * Get row data key.
	 * @return KEY object 
	 */
	KEY getKey();
	
	/**
	 * Get row data value.
	 * @return the row data value
	 */
	VALUE getValue();

	/**
	 * Get transformed row data value for UI.
	 * @return the row data value
	 */
	VALUE getValueForUI();

	/**
	 * Set value.
	 * @param value a value
	 */
	void setValue(VALUE value);
	
	/**
	 * UI properties for current row.
	 * @return UI properties
	 */
	UiProperty getUiProperty();
}
