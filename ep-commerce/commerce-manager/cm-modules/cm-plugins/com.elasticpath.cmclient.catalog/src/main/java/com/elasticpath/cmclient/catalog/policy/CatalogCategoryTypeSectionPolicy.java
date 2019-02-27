/**
 * Copyright (c) Elastic Path Software Inc., 2011
 */
package com.elasticpath.cmclient.catalog.policy;

import java.util.HashMap;
import java.util.Map;

import com.elasticpath.cmclient.catalog.CatalogPermissions;
import com.elasticpath.cmclient.catalog.editors.model.CatalogModel;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.StateDeterminer;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.catalog.CategoryType;

/**
 * Policy for the Catalog Category types section.
 */
public class CatalogCategoryTypeSectionPolicy extends AbstractCatalogDeterminerStatePolicy {


	private CatalogModel catalogModel;

	private final Map<String, StateDeterminer> determinerMap = new HashMap<>();

	@Override
	public void init(final Object dependentObject) {

		if (dependentObject instanceof CatalogModel) {
			catalogModel = (CatalogModel) dependentObject;
		}
	}

	@Override
	protected StateDeterminer getDefaultDeterminer() {
		return new DefaultManageCategoryTypeAuthorizationDeterminer();
	}

	@Override
	protected Map<String, StateDeterminer> getDeterminerMap() {
		if (determinerMap.isEmpty()) {
			determinerMap.put("epTableSectionAddButton", new AddCategoryTypeAuthorizationDeterminer()); //$NON-NLS-1$
			determinerMap.put("epTableSectionEditButton", new ManageCategoryTypeAuthorizationDeterminer()); //$NON-NLS-1$
			determinerMap.put("epTableSectionRemoveButton", new ManageCategoryTypeAuthorizationDeterminer()); //$NON-NLS-1$
			determinerMap.put("epTableSectionControlPane", new ManageCategoryTypeAuthorizationDeterminer()); //$NON-NLS-1$
		}
		return determinerMap;
	}

	/**
	 * Determines state based on authorisation to manage the dependent object.
	 */
	public class DefaultManageCategoryTypeAuthorizationDeterminer implements StateDeterminer {
		@Override
		public EpState determineState(final PolicyActionContainer targetContainer) {

			if (hasPermissionToManageCatalog(catalogModel.getCatalog())) {
				return EpState.EDITABLE;
			}

			return EpState.READ_ONLY;
		}
	}

	/**
	 * Determines add button state based on authorisation to manage the dependent object.
	 */
	public class AddCategoryTypeAuthorizationDeterminer implements StateDeterminer {
		@Override
		public EpState determineState(final PolicyActionContainer targetContainer) {

			if (!hasPermissionToManageCatalog(catalogModel.getCatalog())) {
				return EpState.READ_ONLY;
			}

			return EpState.EDITABLE;
		}
	}

	/**
	 * Determines edit/remove button state based on authorisation to manage the dependent object.
	 */
	public class ManageCategoryTypeAuthorizationDeterminer implements StateDeterminer {
		@Override
		public EpState determineState(final PolicyActionContainer targetContainer) {

			if (!hasPermissionToManageCatalog(catalogModel.getCatalog())) {
				return EpState.READ_ONLY;
			}

			CategoryType categoryType = null;
			if (targetContainer.getPolicyDependent() instanceof CategoryType) {
				categoryType = (CategoryType) targetContainer.getPolicyDependent();
			}

			if (categoryTypeCannotBeEdited(categoryType)) {
				return EpState.READ_ONLY;
			}

			return EpState.EDITABLE;
		}
	}

	private boolean categoryTypeCannotBeEdited(final CategoryType categoryType) {
		if (categoryType == null) {
			return true;
		}
		return isInRemovalList(categoryType);
	}

	private boolean isInRemovalList(final CategoryType categoryType) {
		return catalogModel != null && catalogModel.getCategoryTypeTableItems().getRemovedItems().contains(categoryType);
	}

	private boolean hasPermissionToManageCatalog(final Catalog catalog) {
		return catalog != null
				&& AuthorizationService.getInstance().isAuthorizedWithPermission(CatalogPermissions.CATALOG_MANAGE)
				&& AuthorizationService.getInstance().isAuthorizedForCatalog(catalog);

	}

}
