/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.pricelistmanager.policy;

import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.common.AbstractStatePolicyImpl;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;

/**
 * An authorisation state policy for the price list assignment wizard.
 */
public class PriceListAssignmentWizardAuthorisationStatePolicy extends AbstractStatePolicyImpl {

	@Override
	public EpState determineState(final PolicyActionContainer targetContainer) {
		return EpState.EDITABLE;
	}

	@Override
	public void init(final Object dependentObject) {
		// initialise
	}

}
