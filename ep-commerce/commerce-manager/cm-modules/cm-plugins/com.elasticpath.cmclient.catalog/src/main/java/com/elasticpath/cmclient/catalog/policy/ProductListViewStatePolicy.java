/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.catalog.policy;

import java.util.HashMap;
import java.util.Map;

import com.elasticpath.cmclient.catalog.CatalogPermissions;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.StateDeterminer;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.domain.catalog.Product;

/**
 * State policy for linked categories.
 */
public class ProductListViewStatePolicy extends AbstractCatalogDeterminerStatePolicy {

	private AuthorizationService authorizationService;

	private final Map<String, StateDeterminer> determinerMap = new HashMap<>();

	private Product product;

	/**
	 * Construct an instance of this policy that ties to the product guid.
	 *
	 * @param product the <code>Product</code> this policy applies to
	 */
	@Override
	public void init(final Object product) {
		authorizationService = AuthorizationService.getInstance();
		this.product = (Product) product;
	}


	@Override
	protected Map<String, StateDeterminer> getDeterminerMap() {
		if (determinerMap.isEmpty()) {
			determinerMap.put("productIncludeExclude", new LinkedCategoryIncludeExcludeDeterminer()); //$NON-NLS-1$
			determinerMap.put("deleteProductAction", new DeleteProductDeterminer()); //$NON-NLS-1$
		}
		return determinerMap;
	}

	/**
	 * State determiner for including or excluding products from linked categories.
	 */
	protected class LinkedCategoryIncludeExcludeDeterminer implements StateDeterminer {
		/**
		 * Determine the state based on authorization.
		 *
		 * @param targetContainer the target container
		 * @return the <code>EpState</code> determined by the authorization.
		 */
		@Override
		public EpState determineState(final PolicyActionContainer targetContainer) {
			if (authorizationService.isAuthorizedWithPermission(CatalogPermissions.MANAGE_VIRTUAL_CATALOG_LINK_CATEGORY)
					&& authorizationService.isAuthorizedWithPermission(CatalogPermissions.INCLUDE_EXCLUDE_PRODUCT)) {
				return EpState.EDITABLE;
			}
			return EpState.READ_ONLY;
		}
	}

	/**
	 * State determiner for deleting products.
	 */
	protected class DeleteProductDeterminer implements StateDeterminer {
		/**
		 * Determine the state based on authorization.
		 *
		 * @param targetContainer the target container
		 * @return the <code>EpState</code> determined by the authorization.
		 */
		@Override
		public EpState determineState(final PolicyActionContainer targetContainer) {
			if (authorizationService.isAuthorizedWithPermission(CatalogPermissions.MANAGE_PRODUCT_SKU)
					&& authorizationService.isAuthorizedForProduct(product)) {
				return EpState.EDITABLE;
			}
			return EpState.READ_ONLY;
		}
	}

	@Override
	protected StateDeterminer getDefaultDeterminer() {
		return new DefaultEditableDeterminer();
	}

}
