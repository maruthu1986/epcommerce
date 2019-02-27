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
 * State Policy which uses catalog permissions to determine whether a user can make product
 * changes in a specific catalog.
 */
public class ManageProductsInCategoryAuthorizationStatePolicy extends AbstractStatePolicyImpl {

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
	 * Determine state using the authorization to check catalog permissions.
	 * 
	 * @param targetContainer not used here
	 * @return EDITABLE only if the user has valid permissions
	 */
	@Override
	public EpState determineState(final PolicyActionContainer targetContainer) {
		if (category == null || category.getCatalog() == null) {
			return EpState.READ_ONLY;
		}
		if (AuthorizationService.getInstance().isAuthorizedForCatalog(category.getCatalog())
			&& AuthorizationService.getInstance().isAuthorizedWithPermission(CatalogPermissions.MANAGE_PRODUCT_SKU)) {
			return EpState.EDITABLE;
		}
		return EpState.READ_ONLY;
	}

}
