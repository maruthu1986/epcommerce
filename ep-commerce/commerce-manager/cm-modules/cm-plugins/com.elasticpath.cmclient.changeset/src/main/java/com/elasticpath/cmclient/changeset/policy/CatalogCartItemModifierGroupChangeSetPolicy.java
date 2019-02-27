/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.changeset.policy;

import java.util.HashMap;
import java.util.Map;


import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.StateDeterminer;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.domain.changeset.ChangeSetObjectStatus;

import com.elasticpath.domain.cartmodifier.CartItemModifierGroup;

/**
 * Change set policy for catalog cart item modifier groups.
 */
public class CatalogCartItemModifierGroupChangeSetPolicy extends AbstractChangeSetDeterminerStatePolicy {

	private final Map<String, StateDeterminer> determinerMap = new HashMap<>();

	@Override
	protected StateDeterminer getDefaultDeterminer() {
		return new DefaultEditableDeterminer();
	}

	@Override
	protected Map<String, StateDeterminer> getDeterminerMap() {
		if (determinerMap.isEmpty()) {
			determinerMap.put("epTableSectionAddButton", new AddCartItemModifierGroupToChangeSetAuthorizationDeterminer()); //$NON-NLS-1$
			determinerMap.put("epTableSectionEditButton", new EditCartItemModifierGroupInChangeSetAuthorizationDeterminer()); //$NON-NLS-1$
			determinerMap.put("epTableSectionRemoveButton", new RemoveCartItemModifierGroupInChangeSetAuthorizationDeterminer()); //$NON-NLS-1$
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
	public class AddCartItemModifierGroupToChangeSetAuthorizationDeterminer implements StateDeterminer {
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
	public class EditCartItemModifierGroupInChangeSetAuthorizationDeterminer implements StateDeterminer {
		/**
		 * Determine the state.
		 *
		 * @param targetContainer the target container
		 * @return the <code>EpState</code> determined.
		 */
		public EpState determineState(final PolicyActionContainer targetContainer) {

			CartItemModifierGroup cartItemModifierGroup = null;

			if (targetContainer.getPolicyDependent() instanceof CartItemModifierGroup) {
				cartItemModifierGroup = (CartItemModifierGroup) targetContainer.getPolicyDependent();
			}

			if (!getChangeSetHelper().isActiveChangeSet()) {
				return EpState.READ_ONLY;
			}

			if (cartItemModifierGroup == null) {
				return EpState.READ_ONLY;
			}

			if (!getChangeSetHelper().isMemberOfActiveChangeset(cartItemModifierGroup)) {
				return EpState.READ_ONLY;
			}

			return EpState.EDITABLE;
		}
	}

	/**
	 * Determines remove button state based on authorisation to manage the dependent object in a change set.
	 */
	public class RemoveCartItemModifierGroupInChangeSetAuthorizationDeterminer implements StateDeterminer {
		/**
		 * Determine the state.
		 *
		 * @param targetContainer the target container
		 * @return the <code>EpState</code> determined.
		 */
		public EpState determineState(final PolicyActionContainer targetContainer) {

			CartItemModifierGroup cartItemModifierGroup = null;

			if (targetContainer.getPolicyDependent() instanceof CartItemModifierGroup) {
				cartItemModifierGroup = (CartItemModifierGroup) targetContainer.getPolicyDependent();
			}

			if (!getChangeSetHelper().isActiveChangeSet()) {
				return EpState.READ_ONLY;
			}

			if (cartItemModifierGroup == null) {
				return EpState.READ_ONLY;
			}

				// check if category type in changeset that is not active
			final ChangeSetObjectStatus status = getChangeSetHelper().getChangeSetObjectStatus(cartItemModifierGroup);
			 if (status.isLocked() && !getChangeSetHelper().isMemberOfActiveChangeset(cartItemModifierGroup)) {
				 return EpState.READ_ONLY;
			 }

			return EpState.EDITABLE;
		}
	}

}
