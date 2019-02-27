/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.catalog.policy;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.elasticpath.cmclient.catalog.CatalogPermissions;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.StateDeterminer;
import com.elasticpath.cmclient.policy.common.AbstractStatePolicyImpl;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.domain.catalog.Category;

/**
 * A <code>StatePolicy</code> that will determine UI state for product related UI elements.
 * It will return editable based on the following criteria:
 * <ol>
 *   <li>The user has permission to the product's catalog</li>
 *   <li>The user has permission to edit products</li>
 * </ol>
 */
public class CategoryAuthorizationStatePolicy extends AbstractStatePolicyImpl {

	private Category category;
	
	private final Map<String, StateDeterminer> determinerMap = new HashMap<>();

	/**
	 * Construct an instance of this policy that ties to the product guid.
	 * 
	 * @param category the <code>Category</code> this policy applies to
	 */
	@Override
	public void init(final Object category) {
		this.category = (Category) category;
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
		if (getReadOnlyContainerNames().contains(targetContainer.getName())) {
			return EpState.READ_ONLY;
		}
		
		StateDeterminer determiner = getDeterminer(targetContainer.getName());
		return determiner.determineState(targetContainer);
	}

	/**
	 * Get the collection of names of <code>PolicyTargetContainer</code> objects
	 * that should always have a read only status.
	 * 
	 * @return the collection of names
	 */
	protected Collection<String> getReadOnlyContainerNames() {
		return Arrays.asList(
				"guid", //$NON-NLS-1$
				"categorySummaryReadOnlyControls" //$NON-NLS-1$
				);
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
			determiner = new DefaultAuthorizationDeterminer();
		}
		return determiner;
	}
	
	/**
	 * Map container names to determiners.
	 * 
	 * @return a map of container name to <code>StateDeterminer</code>
	 */
	protected Map<String, StateDeterminer> getDeterminerMap() {
		if (determinerMap.isEmpty()) {
			determinerMap.put("categorySummaryControls", new DefaultAuthorizationDeterminer()); //$NON-NLS-1$
		}
		return determinerMap;
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
			if (category == null) {
				return EpState.READ_ONLY;
			}
			
			if (AuthorizationService.getInstance().isAuthorizedWithPermission(CatalogPermissions.CATEGORY_MANAGE)
					&& AuthorizationService.getInstance().isAuthorizedForCatalog(category.getCatalog())) {
				return EpState.EDITABLE;
			}
			return EpState.READ_ONLY;
		}
	}

}
