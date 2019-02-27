/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.admin.taxes.actions;

import com.elasticpath.cmclient.admin.taxes.TaxesPlugin;
import com.elasticpath.cmclient.admin.taxes.views.TaxCodeListView;
import com.elasticpath.cmclient.core.EpAuthorizationException;
import com.elasticpath.cmclient.core.actions.AbstractAuthorizedShowViewAction;

/**
 * Shows tax codes list view.
 */
public class ShowTaxCodesViewAction extends AbstractAuthorizedShowViewAction {

	@Override
	protected String getViewId() {
		if (!isAuthorized()) {
			throw new EpAuthorizationException(TaxCodeListView.VIEW_ID);
		}
		return TaxCodeListView.VIEW_ID;
	}

	@Override
	public boolean isAuthorized() {
		return TaxesPlugin.isAuthorized();
	}
}
