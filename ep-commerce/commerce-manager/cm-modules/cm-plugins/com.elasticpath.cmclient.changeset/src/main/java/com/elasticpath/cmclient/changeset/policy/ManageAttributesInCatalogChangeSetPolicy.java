/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.changeset.policy;

import java.util.HashMap;
import java.util.Map;

import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.StateDeterminer;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.domain.attribute.Attribute;
import com.elasticpath.domain.changeset.ChangeSetObjectStatus;

/**
 * Policy for managing Catalog attributes in a ChangeSet.
 */
public class ManageAttributesInCatalogChangeSetPolicy extends AbstractChangeSetDeterminerStatePolicy {

	private final Map<String, StateDeterminer> determinerMap = new HashMap<String, StateDeterminer>();

	@Override
	protected StateDeterminer getDefaultDeterminer() {
		return new DefaultEditableDeterminer();
	}

	@Override
	protected Map<String, StateDeterminer> getDeterminerMap() {
		if (determinerMap.isEmpty()) {
			determinerMap.put("epTableSectionAddButton", new AddAttributeToChangeSetAuthorizationDeterminer()); //$NON-NLS-1$
			determinerMap.put("epTableSectionEditButton", new EditAttributeInChangeSetAuthorizationDeterminer()); //$NON-NLS-1$
			determinerMap.put("epTableSectionRemoveButton", new RemoveAttributeInChangeSetAuthorizationDeterminer()); //$NON-NLS-1$
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
	public class AddAttributeToChangeSetAuthorizationDeterminer implements StateDeterminer {
		/**
		 * Determine the state.
		 * @param targetContainer the target container
		 * @return the <code>EpState</code> determined.
		 */
		public EpState determineState(final PolicyActionContainer targetContainer) {

			if (!getChangeSetHelper()
				.isActiveChangeSet()) {
				return EpState.READ_ONLY;
			}

			return EpState.EDITABLE;
		}
	}


	/**
	 * Determines edit button state based on authorisation to manage the dependent object in a change set.
	 */
	public class EditAttributeInChangeSetAuthorizationDeterminer implements StateDeterminer {
		/**
		 * Determine the state.
		 * @param targetContainer the target container
		 * @return the <code>EpState</code> determined.
		 */
		public EpState determineState(final PolicyActionContainer targetContainer) {

			Attribute attribute = null;

			if (targetContainer.getPolicyDependent() instanceof Attribute) {
				attribute = (Attribute) targetContainer.getPolicyDependent();
			}

			
			if (!getChangeSetHelper().isActiveChangeSet()) {
				return EpState.READ_ONLY;
			}
			if (attribute == null) {
				return EpState.READ_ONLY;
			}
			if (!getChangeSetHelper().isMemberOfActiveChangeset(attribute)) {
				return EpState.READ_ONLY;
			}
			return EpState.EDITABLE;
		}
	}


	/**
	 * Determines remove button state based on authorisation to manage the dependent object in a change set.
	 */
	public class RemoveAttributeInChangeSetAuthorizationDeterminer implements StateDeterminer {
		/**
		 * Determine the state.
		 * @param targetContainer the target container
		 * @return the <code>EpState</code> determined.
		 */
		public EpState determineState(final PolicyActionContainer targetContainer) {

			Attribute attribute = null;

			if (targetContainer.getPolicyDependent() instanceof Attribute) {
				attribute = (Attribute) targetContainer.getPolicyDependent();
			}

			
			if (!getChangeSetHelper().isActiveChangeSet()) {
				return EpState.READ_ONLY;
			}

			if (attribute == null) {
				return EpState.READ_ONLY;
			}

			// check if category type in changeset that is not active
			ChangeSetObjectStatus status = getChangeSetHelper().getChangeSetObjectStatus(attribute);
			if (status.isLocked() && !getChangeSetHelper().isMemberOfActiveChangeset(attribute)) {
				return EpState.READ_ONLY;
			}
			return EpState.EDITABLE;
		}
	}

}
