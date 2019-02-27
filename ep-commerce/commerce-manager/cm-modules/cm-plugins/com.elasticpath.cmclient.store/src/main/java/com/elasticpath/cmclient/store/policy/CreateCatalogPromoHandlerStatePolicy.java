/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.store.policy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.StateDeterminer;
import com.elasticpath.cmclient.policy.common.AbstractStatePolicyImpl;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.store.promotions.PromotionsPermissions;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.service.catalog.CatalogService;

/**
 * A <code>StatePolicy</code> that will determine catalog promotion UI elements.
 */

public class CreateCatalogPromoHandlerStatePolicy extends AbstractStatePolicyImpl {

	private final Map<String, StateDeterminer> determinerMap = new HashMap<>();

	/**
	 * Authorization is only checked once to improve performance. The alternative results in
	 * a large number of database hits.
	 */
	private Boolean authorized;
	
	/**
	 * Construct an instance of this policy that ties to the product guid.
	 * 
	 * @param rule the <code>Rule</code> this policy applies to
	 */
	@Override
	public void init(final Object rule) {
		// Does nothing
	}
	
	/**
	 * Determine the UI state of targets based on the following policy.
	 * 
	 * Controls are editable only if the user is authorized to edit products
	 * 
	 * @param targetContainer a set of policy targets
	 * @return the <code>EpState</code> determined by the policy.
	 */
	@Override
	public EpState determineState(final PolicyActionContainer targetContainer) {
		StateDeterminer determiner = getDeterminer(targetContainer.getName());
		return determiner.determineState(targetContainer);
	}
	
	/**
	 * Get the state determiner for the given container name.
	 * 
	 * @param containerName the name of the container
	 * @return a <code>StateDeterminer</code>
	 */
	protected StateDeterminer getDeterminer(final String containerName) {
		StateDeterminer determiner = getDeterminerMap().get(containerName);
		if (determiner == null) {
			determiner = new DefaultDeterminer();
		}
		return determiner;
	}
	
	/**
	 * Map container names to determiners.
	 * 
	 * @return a map of container name to <code>StateDeterminer</code>
	 */
	protected Map<String, StateDeterminer> getDeterminerMap() {
		return determinerMap;
	}

	/**
	 * Checks if a) cm user is authorized and b) if change sets are enabled, an active change is selected.
	 */
	public class DefaultDeterminer implements StateDeterminer {
		@Override
		public EpState determineState(final PolicyActionContainer targetContainer) {
			if (authorized == null) {
				if (AuthorizationService.getInstance().isAuthorizedWithPermission(PromotionsPermissions.PROMOTION_MANAGE)) {
					final CatalogService catalogService =
							ServiceLocator.getService(ContextIdNames.CATALOG_SERVICE);
					final List<Catalog> catalogs = catalogService.findAllCatalogs();
					AuthorizationService.getInstance().filterAuthorizedCatalogs(catalogs);
					authorized = catalogs.isEmpty();
					authorized ^= true; // bitwise inversion faster than 'authorized = !something'
				} else {
					authorized = false;
				}
			}
			
			if (authorized) {
				if (getChangeSetHelper().isChangeSetsEnabled() && !getChangeSetHelper().isActiveChangeSet()) {
					// if change sets are enabled, then an active change set must be selected
					return EpState.READ_ONLY;
				}
				
				return EpState.EDITABLE;
			}
			
			return EpState.READ_ONLY;
		}
	}
	
}
