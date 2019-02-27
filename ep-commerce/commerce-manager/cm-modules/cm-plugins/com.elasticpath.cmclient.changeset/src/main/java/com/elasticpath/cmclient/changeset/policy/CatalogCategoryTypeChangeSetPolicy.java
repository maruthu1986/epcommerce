/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.changeset.policy;

import java.util.HashMap;
import java.util.Map;

import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.StateDeterminer;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.domain.catalog.CategoryType;
import com.elasticpath.domain.changeset.ChangeSetObjectStatus;

/**
 * Changeset policy for catalog category types.
 */
public class CatalogCategoryTypeChangeSetPolicy extends AbstractChangeSetDeterminerStatePolicy {

	private final Map<String, StateDeterminer> determinerMap = new HashMap<String, StateDeterminer>();

	@Override
	protected StateDeterminer getDefaultDeterminer() {
		return new DefaultEditableDeterminer();
	}

	@Override
	protected Map<String, StateDeterminer> getDeterminerMap() {
		if (determinerMap.isEmpty()) {
			determinerMap.put("epTableSectionAddButton", new AddCategoryTypeToChangeSetAuthorizationDeterminer()); //$NON-NLS-1$
			determinerMap.put("epTableSectionEditButton", new EditCategoryTypeInChangeSetAuthorizationDeterminer()); //$NON-NLS-1$
			determinerMap.put("epTableSectionRemoveButton", new RemoveCategoryTypeInChangeSetAuthorizationDeterminer()); //$NON-NLS-1$
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
	public class AddCategoryTypeToChangeSetAuthorizationDeterminer implements StateDeterminer {
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
	public class EditCategoryTypeInChangeSetAuthorizationDeterminer implements StateDeterminer {
		/**
		 * Determine the state.
		 *
		 * @param targetContainer the target container
		 * @return the <code>EpState</code> determined.
		 */
		public EpState determineState(final PolicyActionContainer targetContainer) {

			CategoryType categoryType = null;

			if (targetContainer.getPolicyDependent() instanceof CategoryType) {
				categoryType = (CategoryType) targetContainer.getPolicyDependent();
			}
			
			if (!getChangeSetHelper().isActiveChangeSet()) {
				return EpState.READ_ONLY;
			}

			if (categoryType == null) {
				return EpState.READ_ONLY;
			}

			if (!getChangeSetHelper().isMemberOfActiveChangeset(categoryType)) {
				return EpState.READ_ONLY;
			}

			return EpState.EDITABLE;
		}
	}

	/**
	 * Determines remove button state based on authorisation to manage the dependent object in a change set.
	 */
	public class RemoveCategoryTypeInChangeSetAuthorizationDeterminer implements StateDeterminer {
		/**
		 * Determine the state.
		 *
		 * @param targetContainer the target container
		 * @return the <code>EpState</code> determined.
		 */
		public EpState determineState(final PolicyActionContainer targetContainer) {

			CategoryType categoryType = null;

			if (targetContainer.getPolicyDependent() instanceof CategoryType) {
				categoryType = (CategoryType) targetContainer.getPolicyDependent();
			}
			
			if (!getChangeSetHelper().isActiveChangeSet()) {
				return EpState.READ_ONLY;
			}

			if (categoryType == null) {
				return EpState.READ_ONLY;
			}

				// check if category type in changeset that is not active
			ChangeSetObjectStatus status = getChangeSetHelper().getChangeSetObjectStatus(categoryType);
			if (status.isLocked() && !getChangeSetHelper().isMemberOfActiveChangeset(categoryType)) {
				return EpState.READ_ONLY;
			}
			
			return EpState.EDITABLE;
		}
	}
}
