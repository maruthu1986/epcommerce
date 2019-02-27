/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.policy.common;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.helpers.ChangeSetHelper;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.StateChangeTarget;
import com.elasticpath.cmclient.policy.StatePolicy;
import com.elasticpath.cmclient.policy.StatePolicyDelegate;

/**
 * An abstract implementation of <code>StatePolicy</code> which applies the state returned by
 * <code>getState()</code> to the controls in the context. 
 */
public abstract class AbstractStatePolicyImpl implements StatePolicy {

	private final ChangeSetHelper changeSetHelper = ServiceLocator.getService(ChangeSetHelper.BEAN_ID);

	@Override
	public void init(final Object dependentObject) {
		//does nothing by default
	}

	@Override
	public void apply(final PolicyActionContainer targetContainer) {
		for (StatePolicyDelegate governable : targetContainer.getDelegates()) {
			governable.applyStatePolicy(this);
		}
		EpState epState = determineState(targetContainer);
		for (StateChangeTarget target : targetContainer.getTargets()) {
			target.setState(epState);
		}
	}

	public ChangeSetHelper getChangeSetHelper() {
		return changeSetHelper;
	}
}
