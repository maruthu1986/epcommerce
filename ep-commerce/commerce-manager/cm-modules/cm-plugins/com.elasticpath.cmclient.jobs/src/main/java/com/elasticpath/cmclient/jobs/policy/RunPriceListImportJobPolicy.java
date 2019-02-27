/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.jobs.policy;

import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.jobs.JobsPermissions;
import com.elasticpath.cmclient.policy.common.AbstractStatePolicyImpl;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;


/**
 * Policy that checks the user has permissions to run price list import jobs.
 */
public class RunPriceListImportJobPolicy extends AbstractStatePolicyImpl {

	@Override
	public EpState determineState(final PolicyActionContainer targetContainer) {
		if (AuthorizationService.getInstance().isAuthorizedWithPermission(JobsPermissions.PRICE_MANAGEMENT_IMPORT_PRICE_LIST_JOB)) {
			return EpState.EDITABLE;
		}
		return EpState.DISABLED;
	}

	/**
	 * Initialize the policy. There are no dependencies of this policy.
	 *
	 * @param dependentObject a dependent object
	 */
	@Override
	public void init(final Object dependentObject) {
		// No initialization required
	}

}
