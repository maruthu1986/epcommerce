/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.cmclient.admin.taxes.actions;

import com.elasticpath.cmclient.admin.taxes.TaxesPlugin;
import com.elasticpath.cmclient.admin.taxes.views.TaxJurisdictionsListView;
import com.elasticpath.cmclient.core.EpAuthorizationException;
import com.elasticpath.cmclient.core.actions.AbstractAuthorizedShowViewAction;

/**
 * Shows tax jurisdictions list view.
 */
public class ShowTaxJurisdictionsViewAction extends AbstractAuthorizedShowViewAction {

	@Override
	protected String getViewId() {
		if (!isAuthorized()) {
			throw new EpAuthorizationException(TaxJurisdictionsListView.VIEW_ID);
		}
		return TaxJurisdictionsListView.VIEW_ID;
	}

	@Override
	public boolean isAuthorized() {
		return TaxesPlugin.isAuthorized();
	}

}
