/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.pricelistmanager.policy;

import com.elasticpath.cmclient.policy.ui.AbstractViewSelectionStatePolicy;
import com.elasticpath.cmclient.pricelistmanager.views.PriceListSearchResultsView;

/**
 * A view selection state policy which states a control would be editable when
 * a selection is available in the target view.
 */
public class PriceListViewSelectionStatePolicy extends AbstractViewSelectionStatePolicy {

	@Override
	protected String getViewId() {
		return PriceListSearchResultsView.VIEW_ID;
	}

}
