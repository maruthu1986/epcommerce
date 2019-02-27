/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.core.binding;

import org.eclipse.core.runtime.IStatus;

/**
 * Implemented by classes who wish to be notified
 * of update events produced by an <code>UpdateValueStrategy</code>.
 */
public interface UpdateValueStrategyListener {

	/** 
	 * Called when a validation is performed.
	 * @param validationStatus the validation status 
	 */
	void inputValidated(IStatus validationStatus);
	
}
