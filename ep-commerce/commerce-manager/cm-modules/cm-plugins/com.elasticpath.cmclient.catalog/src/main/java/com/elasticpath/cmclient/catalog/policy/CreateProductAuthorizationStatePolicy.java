/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.catalog.policy;

import com.elasticpath.cmclient.catalog.CatalogPermissions;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.common.AbstractStatePolicyImpl;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.domain.catalog.Category;

/**
 * Checks whether we have permission to add a product to a category.
 */
public class CreateProductAuthorizationStatePolicy extends AbstractStatePolicyImpl {		
		
	private Category category;
	
	/**
	 * Construct an instance of this policy that ties to the product guid.
	 * 
	 * @param object that this policy applies to
	 */
	@Override
	public void init(final Object object) {
		if (object instanceof Category) {
			this.category = (Category) object;
		}
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
		if (category == null) {
			return EpState.READ_ONLY;
		}
		
		if (category.isLinked() || category.isVirtual()) {
			return EpState.READ_ONLY;
		}
		
		if (AuthorizationService.getInstance().isAuthorizedForCatalog(category.getCatalog())
				&& AuthorizationService.getInstance().isAuthorizedWithPermission(CatalogPermissions.MANAGE_PRODUCT_SKU)) {
			return EpState.EDITABLE;
		}
		 
		return EpState.READ_ONLY;
	}
	
}
