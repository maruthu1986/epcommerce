/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.policy;

import java.util.HashMap;
import java.util.Map;

import com.elasticpath.cmclient.catalog.CatalogPermissions;
import com.elasticpath.cmclient.catalog.CatalogPlugin;
import com.elasticpath.cmclient.catalog.editors.model.CatalogModel;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.StateDeterminer;
import com.elasticpath.cmclient.policy.common.AbstractDeterminerStatePolicyImpl;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.catalog.Catalog;

/**
 * Policy for managing {@link com.elasticpath.domain.catalog.Catalog} {@link com.elasticpath.domain.attribute.Attribute}s.
 */
public class ManageAttributesInCatalogStatePolicy extends AbstractDeterminerStatePolicyImpl {


	private CatalogModel catalogModel;

	private final Map<String, StateDeterminer> determinerMap = new HashMap<>();

	@Override
	public void init(final Object dependentObject) {

		if (dependentObject instanceof CatalogModel) {
			catalogModel = (CatalogModel) dependentObject;
		}

	}

	@Override
	protected String getPluginId() {
		return CatalogPlugin.PLUGIN_ID;
	}
	@Override
	protected StateDeterminer getDefaultDeterminer() {
		return new DefaultManageAttributeAuthorizationDeterminer();
	}

	@Override
	protected Map<String, StateDeterminer> getDeterminerMap() {
		if (determinerMap.isEmpty()) {
			determinerMap.put("epTableSectionAddButton", new AddAttributeAuthorizationDeterminer()); //$NON-NLS-1$
			determinerMap.put("epTableSectionEditButton", new ManageAttributeAuthorizationDeterminer()); //$NON-NLS-1$
			determinerMap.put("epTableSectionControlPane", new ManageAttributeAuthorizationDeterminer()); //$NON-NLS-1$
			determinerMap.put("epTableSectionRemoveButton", new ManageAttributeAuthorizationDeterminer()); //$NON-NLS-1$

		}
		return determinerMap;
	}



	/**
	 * Determines state based on authorisation to manage the dependent object.
	 */
	public class DefaultManageAttributeAuthorizationDeterminer implements StateDeterminer {
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
	public class AddAttributeAuthorizationDeterminer implements StateDeterminer {
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
	public class ManageAttributeAuthorizationDeterminer implements StateDeterminer {
		@Override
		public EpState determineState(final PolicyActionContainer targetContainer) {
			Attribute attribute = null;

			if (targetContainer.getPolicyDependent() instanceof Attribute) {
				attribute = (Attribute) targetContainer.getPolicyDependent();
			}
			if (!hasPermissionToManageCatalog(catalogModel.getCatalog())) {
				return EpState.READ_ONLY;
			}

			if (attributeCannotBeEdited(attribute)) {
				return EpState.READ_ONLY;
			}

			return EpState.EDITABLE;
		}
	}


	private boolean attributeCannotBeEdited(final Attribute attribute) {
		if (attribute == null) {
			return true;
		}

		if (isInRemovalList(attribute)) {
			return true;
		}

		return attribute.isGlobal();
	}

	private boolean isInRemovalList(final Attribute attribute) {
		return catalogModel != null && catalogModel.getAttributeTableItems().getRemovedItems().contains(attribute);
	}

	private boolean hasPermissionToManageCatalog(final Catalog catalog) {
		return catalog != null
				&& AuthorizationService.getInstance().isAuthorizedWithPermission(CatalogPermissions.CATALOG_MANAGE)
				&& AuthorizationService.getInstance().isAuthorizedForCatalog(catalog);
	}

}
