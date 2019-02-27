/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
/**
 * 
 */
package com.elasticpath.cmclient.store.policy;

import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.policy.ui.AbstractViewSelectionStatePolicy;
import com.elasticpath.cmclient.store.targetedselling.TargetedSellingPermissions;
import com.elasticpath.cmclient.store.targetedselling.conditionalexpression.views.ConditionalExpressionSearchResultsView;

/**
 * A view selection state policy which states a control would be editable when
 * a selection is available in the target view.
 */
public class ConditionalExpressionViewSelectionStatePolicy extends AbstractViewSelectionStatePolicy {

	@Override
	protected String getViewId() {
		return ConditionalExpressionSearchResultsView.VIEW_ID;
	}

	@Override
	public EpState determineState(final PolicyActionContainer targetContainer) {
		if (AuthorizationService.getInstance().isAuthorizedWithPermission(TargetedSellingPermissions.CONDITIONAL_EXPRESSION_MANAGE)) {
			return super.determineState(targetContainer);
		}
		return EpState.READ_ONLY;
	}

}
