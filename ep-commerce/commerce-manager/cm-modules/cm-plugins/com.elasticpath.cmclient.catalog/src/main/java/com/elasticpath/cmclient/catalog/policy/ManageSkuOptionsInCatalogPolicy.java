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
import com.elasticpath.domain.catalog.Catalog;
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;

/**
 * Policy Determiner for Catalog SkuOptionValue Section.
 */
public class ManageSkuOptionsInCatalogPolicy extends AbstractCatalogDeterminerStatePolicy {

	private final Map<String, StateDeterminer> determinerMap = new HashMap<>();

	private CatalogModel catalogModel;


	@Override
	public void init(final Object dependentObject) {


		if (dependentObject instanceof CatalogModel) {
			catalogModel = (CatalogModel) dependentObject;
		}

	}

	@Override
	protected StateDeterminer getDefaultDeterminer() {
		return new DefaultManageSkuOptionDeterminer();
	}

	@Override
	protected Map<String, StateDeterminer> getDeterminerMap() {
		if (determinerMap.isEmpty()) {
			determinerMap.put("addSkuOptionValueButton", new AddOptionValueButtonDeterminer()); //$NON-NLS-1$
			determinerMap.put("moveValueUpButton", new MoveUpDeterminer()); //$NON-NLS-1$
			determinerMap.put("moveValueDownButton", new MoveDownDeterminer()); //$NON-NLS-1$
			determinerMap.put("editSelectionButton", new EditButtonDeterminer()); //$NON-NLS-1$
			determinerMap.put("treeSectionSelectedObjectContainer", new EditButtonDeterminer()); //$NON-NLS-1$
			determinerMap.put("removeSelectionButton", new RemoveButtonDeterminer()); //$NON-NLS-1$
		}
		return determinerMap;
	}

	/**
	 * Determines state based on authorisation to manage the dependent object.
	 */
	public class DefaultManageSkuOptionDeterminer implements StateDeterminer {
		@Override
		public EpState determineState(final PolicyActionContainer targetContainer) {

			if (catalogModel == null) {
				return EpState.READ_ONLY;
			}
			if (hasPermissionToManageCatalog(catalogModel.getCatalog())) {
				return EpState.EDITABLE;
			}

			return EpState.READ_ONLY;
		}
	}

	/**
	 * Determines state based on authorisation to manage the dependent object.
	 */
	public class MoveUpDeterminer implements StateDeterminer {
		@Override
		public EpState determineState(final PolicyActionContainer targetContainer) {

			if (!hasPermissionToManageCatalog(catalogModel.getCatalog())) {
				return EpState.READ_ONLY;
			}

			Object object = targetContainer.getPolicyDependent();
			if (object == null) {
				return EpState.READ_ONLY;
			}

			if (object instanceof SkuOption) {
				return EpState.READ_ONLY;
			}
			if (object instanceof SkuOptionValue) {
				SkuOptionValue optionValue = (SkuOptionValue) object;
				if (isHighestOptionValue(optionValue)) {
					return EpState.READ_ONLY;
				}
			}

			return EpState.EDITABLE;
		}
	}

	/**
	 * Determines state based on authorisation to manage the dependent object.
	 */
	public class MoveDownDeterminer implements StateDeterminer {
		@Override
		public EpState determineState(final PolicyActionContainer targetContainer) {

			if (!hasPermissionToManageCatalog(catalogModel.getCatalog())) {
				return EpState.READ_ONLY;
			}

			Object object = targetContainer.getPolicyDependent();

			if (object == null) {
				return EpState.READ_ONLY;
			}

			if (object instanceof SkuOption) {
				return EpState.READ_ONLY;
			}
			if (object instanceof SkuOptionValue) {
				SkuOptionValue optionValue = (SkuOptionValue) object;
				if (isLowestOptionValue(optionValue)) {
					return EpState.READ_ONLY;
				}
			}

			return EpState.EDITABLE;
		}
	}


	/**
	 * Determines the state of the Edit button.
	 */
	public class EditButtonDeterminer implements StateDeterminer {

		@Override
		public EpState determineState(final PolicyActionContainer targetContainer) {

			SkuOption skuOption = getSkuOptionFromTargetContainer(targetContainer);
			if (skuOptionCannotBeEdited(skuOption)) {
				return EpState.READ_ONLY;
			}
			return EpState.EDITABLE;
		}

	}

	/**
	 * Determines the state of the Add option value button.
	 */
	public class AddOptionValueButtonDeterminer implements StateDeterminer {

		@Override
		public EpState determineState(final PolicyActionContainer targetContainer) {

			if (!(targetContainer.getPolicyDependent() instanceof SkuOption)) {
				return EpState.READ_ONLY;
			}

			SkuOption skuOption = getSkuOptionFromTargetContainer(targetContainer);
			if (skuOptionCannotBeEdited(skuOption)) {
				return EpState.READ_ONLY;
			}
			return EpState.EDITABLE;
		}

	}


	/**
	 * Determines state of the remove button.
	 */
	public class RemoveButtonDeterminer implements StateDeterminer {
		@Override
		public EpState determineState(final PolicyActionContainer targetContainer) {

			if (!hasPermissionToManageCatalog(catalogModel.getCatalog())) {
				return EpState.READ_ONLY;
			}

			SkuOption skuOption = getSkuOptionFromTargetContainer(targetContainer);
			if (skuOptionCannotBeEdited(skuOption)) {
				return EpState.READ_ONLY;
			}

			return EpState.EDITABLE;
		}
	}


	private SkuOption getSkuOptionFromTargetContainer(final PolicyActionContainer targetContainer) {
		SkuOption skuOption = null;
		if (targetContainer.getPolicyDependent() instanceof SkuOptionValue) {
			SkuOptionValue skuOptionValue = (SkuOptionValue) targetContainer.getPolicyDependent();
			skuOption = skuOptionValue.getSkuOption();
		} else if (targetContainer.getPolicyDependent() instanceof SkuOption) {
			skuOption = (SkuOption) targetContainer.getPolicyDependent();
		}
		return skuOption;
	}


	private boolean hasPermissionToManageCatalog(final Catalog catalog) {
		return catalog != null
				&& AuthorizationService.getInstance().isAuthorizedWithPermission(CatalogPermissions.CATALOG_MANAGE)
				&& AuthorizationService.getInstance().isAuthorizedForCatalog(catalog);
	}

	private boolean isLowestOptionValue(final SkuOptionValue optionValue) {
		return !canMove(optionValue, optionValue.getSkuOption().getMaxOrdering());
	}

	private boolean isHighestOptionValue(final SkuOptionValue optionValue) {
		return !canMove(optionValue, optionValue.getSkuOption().getMinOrdering());
	}

	private boolean canMove(final SkuOptionValue optionValue, final int ordering) {
		return ordering != optionValue.getOrdering();
	}


	private boolean skuOptionCannotBeEdited(final SkuOption skuOption) {
		return skuOption == null || isInRemovalList(skuOption);

	}

	private boolean isInRemovalList(final SkuOption skuOption) {
		return catalogModel != null && catalogModel.getSkuOptionTableItems().getRemovedItems().contains(skuOption);
	}


}
