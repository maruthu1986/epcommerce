/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.changeset.policy;

import java.util.HashMap;
import java.util.Map;

import com.elasticpath.cmclient.changeset.ChangeSetPlugin;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.StateDeterminer;
import com.elasticpath.cmclient.policy.common.AbstractDeterminerStatePolicyImpl;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.domain.catalog.ProductType;
import com.elasticpath.domain.changeset.ChangeSetObjectStatus;

/**
 * Change set policy for catalog productTypes.
 */
public class CatalogProductTypeChangeSetPolicy extends AbstractDeterminerStatePolicyImpl {

	private final Map<String, StateDeterminer> determinerMap = new HashMap<String, StateDeterminer>();

	@Override
	protected StateDeterminer getDefaultDeterminer() {
		return new DefaultEditableDeterminer();
	}

	@Override
	protected Map<String, StateDeterminer> getDeterminerMap() {
		if (determinerMap.isEmpty()) {
			determinerMap.put("epTableSectionAddButton", new AddProductTypeToChangeSetAuthorizationDeterminer()); //$NON-NLS-1$
			determinerMap.put("epTableSectionEditButton", new EditProductTypeInChangeSetAuthorizationDeterminer()); //$NON-NLS-1$
			determinerMap.put("epTableSectionRemoveButton", new RemoveProductTypeInChangeSetAuthorizationDeterminer()); //$NON-NLS-1$
		}
		return determinerMap;
	}

	@Override
	protected StateDeterminer getDeterminer(final String containerName) {
		return getDeterminer(containerName, getChangeSetHelper());
	}

	@Override
	protected String getPluginId() {
		return ChangeSetPlugin.PLUGIN_ID;
	}

	/**
	 * Determines add button state based on authorisation to manage the dependent object in a change set.
	 */
	public class AddProductTypeToChangeSetAuthorizationDeterminer implements StateDeterminer {
		/**
		 * Determine the state.
		 *
		 * @param targetContainer the target container
		 * @return the <code>EpState</code> determined.
		 */
		public EpState determineState(final PolicyActionContainer targetContainer) {

			if (!getChangeSetHelper().isActiveChangeSet()) {
				return EpState.READ_ONLY;
			}

			return EpState.EDITABLE;
		}
	}

	/**
	 * Determines edit button state based on authorisation to manage the dependent object in a change set.
	 */
	public class EditProductTypeInChangeSetAuthorizationDeterminer implements StateDeterminer {
		/**
		 * Determine the state.
		 *
		 * @param targetContainer the target container
		 * @return the <code>EpState</code> determined.
		 */
		public EpState determineState(final PolicyActionContainer targetContainer) {

			ProductType productType = null;

			if (targetContainer.getPolicyDependent() instanceof ProductType) {
				productType = (ProductType) targetContainer.getPolicyDependent();
			}

			if (!getChangeSetHelper().isActiveChangeSet()) {
				return EpState.READ_ONLY;
			}

			if (productType == null) {
				return EpState.READ_ONLY;
			}

			if (!getChangeSetHelper().isMemberOfActiveChangeset(productType)) {
				return EpState.READ_ONLY;
			}

			return EpState.EDITABLE;
		}
	}

	/**
	 * Determines remove button state based on authorisation to manage the dependent object in a change set.
	 */
	public class RemoveProductTypeInChangeSetAuthorizationDeterminer implements StateDeterminer {
		/**
		 * Determine the state.
		 *
		 * @param targetContainer the target container
		 * @return the <code>EpState</code> determined.
		 */
		public EpState determineState(final PolicyActionContainer targetContainer) {

			ProductType productType = null;

			if (targetContainer.getPolicyDependent() instanceof ProductType) {
				productType = (ProductType) targetContainer.getPolicyDependent();
			}

			if (!getChangeSetHelper().isActiveChangeSet()) {
				return EpState.READ_ONLY;
			}

			if (productType == null) {
				return EpState.READ_ONLY;
			}

				// check if category type in changeset that is not active
			final ChangeSetObjectStatus status = getChangeSetHelper().getChangeSetObjectStatus(productType);
			if (status.isLocked() && !getChangeSetHelper().isMemberOfActiveChangeset(productType)) {
				return EpState.READ_ONLY;
			}

			return EpState.EDITABLE;
		}
	}

}
