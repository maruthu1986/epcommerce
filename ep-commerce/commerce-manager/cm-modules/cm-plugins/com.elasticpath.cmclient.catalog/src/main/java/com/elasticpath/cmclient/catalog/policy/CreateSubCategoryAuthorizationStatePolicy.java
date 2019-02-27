/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.catalog.policy;

import com.elasticpath.cmclient.catalog.CatalogPermissions;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.StateDeterminer;
import com.elasticpath.cmclient.policy.common.AbstractStatePolicyImpl;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.domain.catalog.Category;

/**
 * Checks whether we have permission to add a linked category to a virtual catalog.
 */
public class CreateSubCategoryAuthorizationStatePolicy extends AbstractStatePolicyImpl {		
		
	private Category category;
	
	/**
	 * Construct an instance of this policy that ties to the product guid.
	 * 
	 * @param object that this policy applies to
	 */
	@Override
	public void init(final Object object) {
		this.category = (Category) object;
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
			if (category == null || category.getCatalog() == null) {
				return EpState.READ_ONLY;
			} else if (!category.isLinked()
						&& AuthorizationService.getInstance().isAuthorizedWithPermission(CatalogPermissions.CATEGORY_MANAGE)
						&& AuthorizationService.getInstance().isAuthorizedForCatalog(category.getCatalog())) {
				return EpState.EDITABLE;
			}
			return EpState.READ_ONLY;
		}
	}	
	
}
