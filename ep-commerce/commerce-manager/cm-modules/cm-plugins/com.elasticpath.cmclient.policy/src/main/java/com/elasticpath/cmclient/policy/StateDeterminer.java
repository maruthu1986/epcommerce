/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.policy;

import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;

/**
 * Something that determines an <code>EpState</code>.
 */
public interface StateDeterminer {

	/**
	 * Determine the state for the given action container.
	 * @param targetContainer the action container whose state we want.
	 * @return an <code>EpState</code>
	 */
	EpState determineState(PolicyActionContainer targetContainer);
}
