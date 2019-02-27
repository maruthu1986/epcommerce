/**
 * Copyright (c) Elastic Path Software Inc., 2017
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
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.catalog.Catalog;

/**
 * Policy for the Catalog Brands section.
 */
public class CatalogBrandSectionPolicy extends AbstractCatalogDeterminerStatePolicy {

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
		return new DefaultManageBrandAuthorizationDeterminer();
	}

	@Override
	protected Map<String, StateDeterminer> getDeterminerMap() {
		if (determinerMap.isEmpty()) {
			determinerMap.put("epTableSectionAddButton", new AddBrandAuthorizationDeterminer()); //$NON-NLS-1$
			determinerMap.put("epTableSectionEditButton", new ManageBrandAuthorizationDeterminer()); //$NON-NLS-1$
			determinerMap.put("epTableSectionControlPane", new ManageBrandAuthorizationDeterminer()); //$NON-NLS-1$
			determinerMap.put("epTableSectionRemoveButton", new ManageBrandAuthorizationDeterminer()); //$NON-NLS-1$
		}
		return determinerMap;
	}

	/**
	 * Determines state based on authorisation to manage the dependent object.
	 */
	public class DefaultManageBrandAuthorizationDeterminer implements StateDeterminer {
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
	public class AddBrandAuthorizationDeterminer implements StateDeterminer {
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
	public class ManageBrandAuthorizationDeterminer implements StateDeterminer {
		@Override
		public EpState determineState(final PolicyActionContainer targetContainer) {

			if (!hasPermissionToManageCatalog(catalogModel.getCatalog())) {
				return EpState.READ_ONLY;
			}

			Brand brand = null;
			if (targetContainer.getPolicyDependent() instanceof Brand) {
				brand = (Brand) targetContainer.getPolicyDependent();
			}

			if (brandCannotBeEdited(brand)) {
				return EpState.READ_ONLY;
			}

			return EpState.EDITABLE;
		}
	}

	private boolean brandCannotBeEdited(final Brand brand) {
		if (brand == null) {
			return true;
		}
		return isInRemovalList(brand);
	}

	private boolean isInRemovalList(final Brand brand) {
		return catalogModel != null && catalogModel.getBrandTableItems().getRemovedItems().contains(brand);
	}

	private boolean hasPermissionToManageCatalog(final Catalog catalog) {
		return catalog != null
				&& AuthorizationService.getInstance().isAuthorizedWithPermission(CatalogPermissions.CATALOG_MANAGE)
				&& AuthorizationService.getInstance().isAuthorizedForCatalog(catalog);

	}

}
