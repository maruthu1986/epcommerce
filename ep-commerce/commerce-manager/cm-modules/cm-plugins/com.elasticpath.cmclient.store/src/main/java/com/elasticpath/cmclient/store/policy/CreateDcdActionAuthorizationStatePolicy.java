/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.store.policy;

import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.common.AbstractStatePolicyImpl;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;

/**
 * A policy for the create price list action.
 */
public class CreateDcdActionAuthorizationStatePolicy extends AbstractStatePolicyImpl {

	@Override
	public EpState determineState(final PolicyActionContainer targetContainer) {
		return EpState.EDITABLE;
	}

	@Override
	public void init(final Object dependentObject) {
		// not used
	}

}
