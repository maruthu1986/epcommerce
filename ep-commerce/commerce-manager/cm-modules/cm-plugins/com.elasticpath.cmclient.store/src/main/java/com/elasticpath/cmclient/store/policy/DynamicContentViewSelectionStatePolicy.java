/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.store.policy;

import com.elasticpath.cmclient.policy.ui.AbstractViewSelectionStatePolicy;
import com.elasticpath.cmclient.store.targetedselling.dynamiccontent.views.DynamicContentSearchResultsView;

/**
 * A view selection state policy which states a control would be editable when
 * a selection is available in the target view.
 */
public class DynamicContentViewSelectionStatePolicy extends AbstractViewSelectionStatePolicy {

	@Override
	protected String getViewId() {
		return DynamicContentSearchResultsView.VIEW_ID;
	}

}
