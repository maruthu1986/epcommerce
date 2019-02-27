/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */

package com.elasticpath.cmclient.catalog.policy;

import java.util.HashMap;
import java.util.Map;

import com.elasticpath.cmclient.catalog.CatalogPermissions;
import com.elasticpath.cmclient.catalog.editors.model.CatalogModel;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;
import com.elasticpath.cmclient.policy.StateDeterminer;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.domain.cartmodifier.CartItemModifierGroup;
import com.elasticpath.domain.catalog.Catalog;

/**
 * Policy for the Catalog CatalogCartItemModifierGroups section.
 */
public class CatalogCartItemModifierGroupsSectionPolicy extends AbstractCatalogDeterminerStatePolicy {

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
		return new DefaultManageGroupAuthorizationDeterminer();
	}

	@Override
	protected Map<String, StateDeterminer> getDeterminerMap() {
		if (determinerMap.isEmpty()) {
			determinerMap.put("epTableSectionAddButton", new AddGroupAuthorizationDeterminer()); //$NON-NLS-1$
			determinerMap.put("epTableSectionEditButton", new ManageGroupAuthorizationDeterminer()); //$NON-NLS-1$
			determinerMap.put("epTableSectionControlPane", new ManageGroupAuthorizationDeterminer()); //$NON-NLS-1$
			determinerMap.put("epTableSectionRemoveButton", new ManageGroupAuthorizationDeterminer()); //$NON-NLS-1$
		}
		return determinerMap;
	}

	/**
	 * Determines state based on authorisation to manage the dependent object.
	 */
	public class DefaultManageGroupAuthorizationDeterminer implements StateDeterminer {
		@Override
		public EpControlFactory.EpState determineState(final PolicyActionContainer targetContainer) {

			if (hasPermissionToManageCatalog(catalogModel.getCatalog())) {
				return EpControlFactory.EpState.EDITABLE;
			}

			return EpControlFactory.EpState.READ_ONLY;
		}
	}

	/**
	 * Determines add button state based on authorisation to manage the dependent object.
	 */
	public class AddGroupAuthorizationDeterminer implements StateDeterminer {
		@Override
		public EpControlFactory.EpState determineState(final PolicyActionContainer targetContainer) {

			if (!hasPermissionToManageCatalog(catalogModel.getCatalog())) {
				return EpControlFactory.EpState.READ_ONLY;
			}

			return EpControlFactory.EpState.EDITABLE;
		}
	}

	/**
	 * Determines edit/remove button state based on authorisation to manage the dependent object.
	 */
	public class ManageGroupAuthorizationDeterminer implements StateDeterminer {
		@Override
		public EpControlFactory.EpState determineState(final PolicyActionContainer targetContainer) {

			if (!hasPermissionToManageCatalog(catalogModel.getCatalog())) {
				return EpControlFactory.EpState.READ_ONLY;
			}

			CartItemModifierGroup group = null;
			if (targetContainer.getPolicyDependent() instanceof CartItemModifierGroup) {
				group = (CartItemModifierGroup) targetContainer.getPolicyDependent();
			}

			if (groupCannotBeEdited(group)) {
				return EpControlFactory.EpState.READ_ONLY;
			}

			return EpControlFactory.EpState.EDITABLE;
		}
	}

	private boolean groupCannotBeEdited(final CartItemModifierGroup group) {
		if (group == null) {
			return true;
		}
		return isInRemovalList(group);
	}

	private boolean isInRemovalList(final CartItemModifierGroup group) {
		return catalogModel != null && catalogModel.getBrandTableItems().getRemovedItems().contains(group);
	}

	private boolean hasPermissionToManageCatalog(final Catalog catalog) {
		return catalog != null
				&& AuthorizationService.getInstance().isAuthorizedWithPermission(CatalogPermissions.CATALOG_MANAGE)
				&& AuthorizationService.getInstance().isAuthorizedForCatalog(catalog);

	}
}
