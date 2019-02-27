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
import com.elasticpath.domain.catalog.ProductType;

/**
 * Policy for the Catalog Product types section.
 */
public class CatalogProductTypeSectionPolicy extends AbstractCatalogDeterminerStatePolicy {

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
		return new DefaultManageProductTypeAuthorizationDeterminer();
	}

	@Override
	protected Map<String, StateDeterminer> getDeterminerMap() {
		if (determinerMap.isEmpty()) {
			determinerMap.put("epTableSectionAddButton", new AddProductTypeAuthorizationDeterminer()); //$NON-NLS-1$
			determinerMap.put("epTableSectionEditButton", new ManageProductTypeAuthorizationDeterminer()); //$NON-NLS-1$
			determinerMap.put("epTableSectionControlPane", new ManageProductTypeAuthorizationDeterminer()); //$NON-NLS-1$
			determinerMap.put("epTableSectionRemoveButton", new ManageProductTypeAuthorizationDeterminer()); //$NON-NLS-1$
		}
		return determinerMap;
	}

	/**
	 * Determines state based on authorisation to manage the dependent object.
	 */
	public class DefaultManageProductTypeAuthorizationDeterminer implements StateDeterminer {
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
	public class AddProductTypeAuthorizationDeterminer implements StateDeterminer {
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
	public class ManageProductTypeAuthorizationDeterminer implements StateDeterminer {
		@Override
		public EpState determineState(final PolicyActionContainer targetContainer) {

			if (!hasPermissionToManageCatalog(catalogModel.getCatalog())) {
				return EpState.READ_ONLY;
			}

			ProductType productType = null;
			if (targetContainer.getPolicyDependent() instanceof ProductType) {
				productType = (ProductType) targetContainer.getPolicyDependent();
			}

			if (productTypeCannotBeEdited(productType)) {
				return EpState.READ_ONLY;
			}

			return EpState.EDITABLE;
		}
	}

	private boolean productTypeCannotBeEdited(final ProductType productType) {
		if (productType == null) {
			return true;
		}
		return isInRemovalList(productType);
	}

	private boolean isInRemovalList(final ProductType productType) {
		return catalogModel != null && catalogModel.getProductTypeTableItems().getRemovedItems().contains(productType);
	}

	private boolean hasPermissionToManageCatalog(final Catalog catalog) {
		return catalog != null
				&& AuthorizationService.getInstance().isAuthorizedWithPermission(CatalogPermissions.CATALOG_MANAGE)
				&& AuthorizationService.getInstance().isAuthorizedForCatalog(catalog);

	}


}
