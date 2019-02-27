/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
/**
 * 
 */
package com.elasticpath.cmclient.conditionbuilder.editor;

import org.eclipse.core.databinding.DataBindingContext;

/**
 * A listener to get changed DataBindingContext.
 *
 */
public interface DataBindingContextListener {

	/** A status of DataBindingContext.  */
	enum Status { ADD, REMOVE }

    /**
	 * DataBindingContext changed.
	 * @param status a status of changes
	 * @param dataBindingContext a DataBindingContext
	 */
	void changed(Status status, DataBindingContext dataBindingContext);
}
