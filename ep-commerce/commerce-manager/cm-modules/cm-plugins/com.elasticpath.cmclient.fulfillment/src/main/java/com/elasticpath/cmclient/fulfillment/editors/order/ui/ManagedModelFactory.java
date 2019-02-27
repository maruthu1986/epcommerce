/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
/**
 * 
 */
package com.elasticpath.cmclient.fulfillment.editors.order.ui;

/**
 * A factory for managed model.
 * @param <KEY> a key type
 * @param <VALUE> a value type 
 */
public interface ManagedModelFactory<KEY, VALUE> {

	/**
	 * Create a ManagedModel object.
	 * @param key a key object
	 * @param value a value object
	 * @param uiProperty a UI property
	 * @return Managed model object
	 */
	ManagedModel<KEY, VALUE> create(KEY key, VALUE value, UiProperty uiProperty);
	
	/**
	 * Create a ManagedModel object from the same model object.
	 * @param managedModel managed model source
	 * @param uiProperty a UI property
	 * @return Managed model object
	 */
	ManagedModel<KEY, VALUE> create(ManagedModel<KEY, VALUE> managedModel, UiProperty uiProperty);

}
