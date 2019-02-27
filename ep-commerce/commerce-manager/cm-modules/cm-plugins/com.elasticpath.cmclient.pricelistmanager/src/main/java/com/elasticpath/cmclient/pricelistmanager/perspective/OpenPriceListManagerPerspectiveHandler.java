/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.pricelistmanager.perspective;

import com.elasticpath.cmclient.core.helpers.AbstractOpenPerspectiveHandler;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.pricelistmanager.PriceListManagerPermissions;

/**
 * Opens perspective on user action.
 */
public class OpenPriceListManagerPerspectiveHandler extends AbstractOpenPerspectiveHandler {

	@Override
	protected String getPerspectiveId() {
		return PriceListManagerPerspectiveFactory.PERSPECTIVE_ID;
	}

	@Override
	public boolean isEnabled() {
		return AuthorizationService.getInstance().isAuthorizedWithPermission(
				PriceListManagerPermissions.PRICE_MANAGEMENT_MANAGE_PRICE_LISTS) && super.isEnabled();
	}
}
