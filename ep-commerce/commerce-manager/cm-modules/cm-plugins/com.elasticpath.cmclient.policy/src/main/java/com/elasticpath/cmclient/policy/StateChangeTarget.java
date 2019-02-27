/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.policy;

import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;

/**
 * An object that can be the target of a state policy.
 */
public interface StateChangeTarget {

	/**
	 * Sets the UI state of this target to the given state.
	 * 
	 * @param state the <code>EpState</code> to set
	 */
	void setState(EpState state);
	
}
