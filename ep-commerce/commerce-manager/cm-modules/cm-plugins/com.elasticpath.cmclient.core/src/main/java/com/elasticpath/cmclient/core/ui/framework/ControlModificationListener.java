/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.ui.framework;

/**
 * This interface is implemented by classes who wish to
 * be notified of modifications to controls.
 */
public interface ControlModificationListener {

	/**
	 * Notifies the implementor that a control
	 * has been modified.
	 */
	void controlModified();
}
