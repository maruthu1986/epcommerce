/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.pricelistmanager.policy;

import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.StateDeterminer;
import com.elasticpath.cmclient.policy.common.AbstractStatePolicyImpl;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.pricelistmanager.PriceListManagerPermissions;

/**
 * A handler state policy to determine the UI state.
 */
public class CreatePriceListHandlerStatePolicy extends AbstractStatePolicyImpl {

	/**
	 * Determines the state of the controls managed by this state policy.
	 * 
	 * @param targetContainer the target container
	 * @return the state of the controls
	 */
	@Override
	public EpState determineState(final PolicyActionContainer targetContainer) {
		StateDeterminer determiner = getDeterminer(targetContainer.getName());
		return determiner.determineState(targetContainer);
	}

	/**
	 * Gets the actual determiner of the state.
	 * 
	 * @param name the name of the state action container
	 * @return the state determiner instance
	 */
	protected StateDeterminer getDeterminer(final String name) {
		return new DefaultAuthorizationDeterminer();		
	}	

	/**
	 * Default state determiner.
	 */
	public class DefaultAuthorizationDeterminer implements StateDeterminer {
		
		/**
		 * Determine the state based on authorization.
		 * @param targetContainer the target container
		 * @return the <code>EpState</code> determined by the authorization.
		 */
		@Override
		public EpState determineState(final PolicyActionContainer targetContainer) {
			
			if (isAuthorized(PriceListManagerPermissions.PRICE_MANAGEMENT_MANAGE_PRICE_LISTS)
					&& (!getChangeSetHelper().isChangeSetsEnabled() || getChangeSetHelper().isActiveChangeSet())) {
				return EpState.EDITABLE;
			}
			return EpState.READ_ONLY;
		}
	}

	@Override
	public void init(final Object dependentObject) {
		// not used
	}

	private boolean isAuthorized(final String secureId) {
		return AuthorizationService.getInstance().isAuthorizedWithPermission(secureId);
	}
}

