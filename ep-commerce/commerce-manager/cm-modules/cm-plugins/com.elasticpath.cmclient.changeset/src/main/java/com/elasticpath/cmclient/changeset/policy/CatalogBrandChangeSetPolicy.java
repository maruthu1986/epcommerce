/**
 * Copyright (c) Elastic Path Software Inc., 2011
 */
package com.elasticpath.cmclient.changeset.policy;

import java.util.HashMap;
import java.util.Map;

import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.StateDeterminer;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.domain.catalog.Brand;
import com.elasticpath.domain.changeset.ChangeSetObjectStatus;

/**
 * Changeset policy for catalog brand types.
 */
public class CatalogBrandChangeSetPolicy extends AbstractChangeSetDeterminerStatePolicy {

	private final Map<String, StateDeterminer> determinerMap = new HashMap<String, StateDeterminer>();

	@Override
	protected StateDeterminer getDefaultDeterminer() {
		return new DefaultEditableDeterminer();
	}

	@Override
	protected Map<String, StateDeterminer> getDeterminerMap() {
		if (determinerMap.isEmpty()) {
			determinerMap.put("epTableSectionAddButton", new AddBrandToChangeSetAuthorizationDeterminer()); //$NON-NLS-1$
			determinerMap.put("epTableSectionEditButton", new EditBrandInChangeSetAuthorizationDeterminer()); //$NON-NLS-1$
			determinerMap.put("epTableSectionRemoveButton", new RemoveBrandInChangeSetAuthorizationDeterminer()); //$NON-NLS-1$
		}
		return determinerMap;
	}

	@Override
	protected StateDeterminer getDeterminer(final String containerName) {
		return getDeterminer(containerName, getChangeSetHelper());
	}



	/**
	 * Determines add button state based on authorisation to manage the dependent object in a change set.
	 */
	public class AddBrandToChangeSetAuthorizationDeterminer implements StateDeterminer {
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
	public class EditBrandInChangeSetAuthorizationDeterminer implements StateDeterminer {
		/**
		 * Determine the state.
		 *
		 * @param targetContainer the target container
		 * @return the <code>EpState</code> determined.
		 */
		public EpState determineState(final PolicyActionContainer targetContainer) {

			Brand brand = null;

			if (targetContainer.getPolicyDependent() instanceof Brand) {
				brand = (Brand) targetContainer.getPolicyDependent();
			}

			if (!getChangeSetHelper().isActiveChangeSet()) {
				return EpState.READ_ONLY;
			}

			if (brand == null) {
				return EpState.READ_ONLY;
			}

			if (!getChangeSetHelper().isMemberOfActiveChangeset(brand)) {
				return EpState.READ_ONLY;
			}

			return EpState.EDITABLE;
		}
	}

	/**
	 * Determines remove button state based on authorisation to manage the dependent object in a change set.
	 */
	public class RemoveBrandInChangeSetAuthorizationDeterminer implements StateDeterminer {
		/**
		 * Determine the state.
		 *
		 * @param targetContainer the target container
		 * @return the <code>EpState</code> determined.
		 */
		public EpState determineState(final PolicyActionContainer targetContainer) {

			Brand brand = null;

			if (targetContainer.getPolicyDependent() instanceof Brand) {
				brand = (Brand) targetContainer.getPolicyDependent();
			}

			if (!getChangeSetHelper().isActiveChangeSet()) {
				return EpState.READ_ONLY;
			}

			if (brand == null) {
				return EpState.READ_ONLY;
			}

				// check if brand in change set that is not active
			final ChangeSetObjectStatus status = getChangeSetHelper().getChangeSetObjectStatus(brand);
			if (status.isLocked() && !getChangeSetHelper().isMemberOfActiveChangeset(brand)) {
				return EpState.READ_ONLY;
			}

			return EpState.EDITABLE;
		}
	}
}
