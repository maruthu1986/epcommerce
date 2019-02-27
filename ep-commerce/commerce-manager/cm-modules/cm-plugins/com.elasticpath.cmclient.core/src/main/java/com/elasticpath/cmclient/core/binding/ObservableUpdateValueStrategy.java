/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.binding;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.runtime.IStatus;


/**
 * Extends <code>UpdateValueStrategy</code> to add the ability for
 * observers to be notified when the input in a control is validated.
 * 
 */
public class ObservableUpdateValueStrategy extends UpdateValueStrategy {

	/** Collection of listeners. */
	private final List<UpdateValueStrategyListener> listeners = new ArrayList<UpdateValueStrategyListener>();
	
	/**
	 * Adds a new listener to be notified of validation events.
	 * 
	 * @param listener the listener.
	 */
	public void addListener(final UpdateValueStrategyListener listener) {
		this.listeners.add(listener);
	}
	
	@Override
	public IStatus validateAfterGet(final Object value) {
		final IStatus validationStatus = super.validateAfterGet(value);
		
		for (UpdateValueStrategyListener currListener : listeners) {
			currListener.inputValidated(validationStatus);
		}

		return validationStatus;
	}
	
}