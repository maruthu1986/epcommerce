/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.pricelistmanager.policy;

import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.common.AbstractStatePolicyImpl;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.pricelistmanager.PriceListManagerPermissions;

/**
 * A handler state policy to determine the UI state of export to csv.
 */
public class CsvExportPricelistActionStatePolicy extends AbstractStatePolicyImpl {
	
	private AuthorizationService authorizationService;

	@Override
	public EpState determineState(final PolicyActionContainer targetContainer) {
		if (authorizationService.isAuthorizedWithPermission(PriceListManagerPermissions.PRICE_MANAGEMENT_EXPORT_PRICE_LISTS)) {
			return EpState.EDITABLE;		
		}
		return EpState.DISABLED;
	}

	@Override
	public void init(final Object dependentObject) {
		authorizationService = AuthorizationService.getInstance();
		
	}
	

}
