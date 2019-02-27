/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
/**
 * 
 */
package com.elasticpath.cmclient.catalog.policy;

import com.elasticpath.cmclient.catalog.CatalogPermissions;
import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.StateDeterminer;
import com.elasticpath.cmclient.policy.common.AbstractStatePolicyImpl;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.Category;
import com.elasticpath.service.catalog.CategoryService;
import com.elasticpath.service.catalog.ProductService;

/**
 * Delete Catalog or Category state policy.
 *
 */
public class EditDeleteCatalogAndCategoryAutorizationStatePolicy extends AbstractStatePolicyImpl {

	private Object dependentObject;
	
	private final StateDeterminer catalogStateDeterminer = new CatalogStateDeterminer();
	private final StateDeterminer categoryStateDeterminer = new CategoryStateDeterminer();

	private final AuthorizationService authorizationService = AuthorizationService.getInstance();

	@Override
	public EpState determineState(final PolicyActionContainer targetContainer) {
		
		StateDeterminer stateDeterminer = this.getStateDeterminer(this.dependentObject);
		if (stateDeterminer == null) {
			return EpState.DISABLED;
		}
		return stateDeterminer.determineState(targetContainer);
	}

	@Override
	public void init(final Object dependentObject) {
		this.dependentObject = dependentObject;
	}

	private StateDeterminer getStateDeterminer(final Object testObject) {
		
		StateDeterminer stateDeterminer = null;
		
		if (testObject instanceof Catalog) {
			stateDeterminer = this.catalogStateDeterminer;
		} else if (testObject instanceof Category) {
			stateDeterminer = this.categoryStateDeterminer;
		}
		return stateDeterminer;
	}
	
	/** Catalog state determiner. */
	private class CatalogStateDeterminer implements StateDeterminer {

		private final CategoryService categoryService = (CategoryService) ServiceLocator.getService(
				ContextIdNames.CATEGORY_SERVICE);

		@Override
		public EpState determineState(final PolicyActionContainer targetContainer) {
			Catalog selectedCatalog = (Catalog) dependentObject;

			EpState stateResult = EpState.DISABLED;

			if (authorizationService.isAuthorizedWithPermission(CatalogPermissions.CATEGORY_MANAGE)
					&& authorizationService.isAuthorizedForCatalog(selectedCatalog)) {

				if (selectedCatalog.isMaster()) {
					stateResult = EpState.EDITABLE;
				} else if (authorizationService.isAuthorizedWithPermission(CatalogPermissions.MANAGE_VIRTUAL_CATALOG_LINK_CATEGORY)) {
					stateResult = EpState.EDITABLE;
				}

				if (stateResult == EpState.EDITABLE && !canDeleteCatalog(selectedCatalog)) {
					stateResult = EpState.DISABLED;
				}
			}
			return stateResult;
		}

		private boolean canDeleteCatalog(final Catalog selectedCatalog) {
			return categoryService.getRootCategoryCount(selectedCatalog.getUidPk()) == 0;
		}
	}

	/** Category state determiner. */
	private class CategoryStateDeterminer implements StateDeterminer {

		private final ProductService productService = (ProductService) ServiceLocator.getService(ContextIdNames.PRODUCT_SERVICE);

		@Override
		public EpState determineState(final PolicyActionContainer targetContainer) {
			Category selectedCategory = (Category) dependentObject;

			if (authorizationService.isAuthorizedWithPermission(CatalogPermissions.CATEGORY_MANAGE)
					&& authorizationService.isAuthorizedForCatalog(selectedCategory.getCatalog())
					&& canDeleteCategory(selectedCategory)) {

				return EpState.EDITABLE;
			}
			return EpState.DISABLED;
		}

		private boolean canDeleteCategory(final Category selectedCategory) {
			return !(selectedCategory.isVirtual() && selectedCategory.isLinked())
			&& hasNoProducts(selectedCategory) 
			&& hasNoChildren(selectedCategory);
		}

		private boolean hasNoChildren(final Category selectedCategory) {
			return !getCategoryService().hasSubCategories(selectedCategory.getUidPk());
		}

		private boolean hasNoProducts(final Category selectedCategory) {
			return !productService.hasProductsInCategory(selectedCategory.getUidPk());
		}
	}

	protected CategoryService getCategoryService() {
		return ServiceLocator.getService(ContextIdNames.CATEGORY_SERVICE);
	}
}
