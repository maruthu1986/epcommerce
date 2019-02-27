/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.store.policy;

import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.common.AbstractStatePolicyImpl;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.store.targetedselling.TargetedSellingPermissions;

/**
 * An authorisation state policy for the price list assignment wizard.
 */
public class DynamicContentWizardAuthorisationStatePolicy extends AbstractStatePolicyImpl {

	@Override
	public EpState determineState(final PolicyActionContainer targetContainer) {
		if (AuthorizationService.getInstance().isAuthorizedWithPermission(TargetedSellingPermissions.DYNAMIC_CONTENT_MANAGE)) {
			return EpState.EDITABLE;
		}
		return EpState.READ_ONLY;
	}

	@Override
	public void init(final Object dependentObject) {
		// initialise
	}

}
