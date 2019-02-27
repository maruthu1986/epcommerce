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
import com.elasticpath.domain.skuconfiguration.SkuOption;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;


/**
 * Policy to manage the sku option section state using ChangeSets.
 * */
@SuppressWarnings({ "PMD.PrematureDeclaration" })
public class ManageSkuOptionInCatalogChangeSetStatePolicy extends AbstractChangeSetDeterminerStatePolicy {


	private final Map<String, StateDeterminer> determinerMap = new HashMap<String, StateDeterminer>();

	@Override
	protected StateDeterminer getDefaultDeterminer() {
		return new DefaultEditableDeterminer();
	}

	@Override
	protected StateDeterminer getDeterminer(final String containerName) {
		return getDeterminer(containerName, getChangeSetHelper());
	}

	@Override
	protected Map<String, StateDeterminer> getDeterminerMap() {
			if (determinerMap.isEmpty()) {
				determinerMap.put("addSkuOptionButton", new AddButtonDeterminer()); //$NON-NLS-1$
				determinerMap.put("addSkuOptionValueButton", new AddOptionValueButtonDeterminer()); //$NON-NLS-1$
				determinerMap.put("editSelectionButton", new EditButtonDeterminer()); //$NON-NLS-1$
				determinerMap.put("moveValueDownButton", new MoveButtonDeterminer()); //$NON-NLS-1$
				determinerMap.put("moveValueUpButton", new MoveButtonDeterminer()); //$NON-NLS-1$
				determinerMap.put("removeSelectionButton", new RemoveButtonDeterminer()); //$NON-NLS-1$

			}
			return determinerMap;
		}

	/**
	 *  Determiner for remove button.
	 */
	public class RemoveButtonDeterminer implements StateDeterminer {

		@Override
		public EpState determineState(final PolicyActionContainer targetContainer) {


			if (!getChangeSetHelper().isActiveChangeSet()) {
				return EpState.READ_ONLY;
			}

			if (targetContainer.getPolicyDependent() == null) {
				return EpState.READ_ONLY;
			}

			Object object = targetContainer.getPolicyDependent();
			if (object instanceof SkuOptionValue) {
				final SkuOptionValue skuOptionValue = (SkuOptionValue) object;
				object = skuOptionValue.getSkuOption();
			}
			// check if category type in changeset that is not active
			final ChangeSetObjectStatus status = getChangeSetHelper().getChangeSetObjectStatus(object);
			if (status.isLocked() && !getChangeSetHelper().isMemberOfActiveChangeset(object)) {
				return EpState.READ_ONLY;
			}
			return EpState.EDITABLE;

		}
	}

	/**
	 *  Determines the state of the Add sku option button.
	 */
	public class AddOptionValueButtonDeterminer implements StateDeterminer {
		@Override
		public EpState determineState(final PolicyActionContainer targetContainer) {

			SkuOption skuOption;

			if (!(targetContainer.getPolicyDependent() instanceof SkuOption)) {
				return EpState.READ_ONLY;
			}

			skuOption = (SkuOption) targetContainer.getPolicyDependent();

			// newly created sku options can have sku option values added to them immediately without being in a changeset.
			if (!skuOption.isPersisted()) {
				return EpState.EDITABLE;
			}

			if (!getChangeSetHelper().isActiveChangeSet()) {
				return EpState.READ_ONLY;
			}

			if (!getChangeSetHelper().isMemberOfActiveChangeset(skuOption)) {
				return EpState.READ_ONLY;
			}

			return EpState.EDITABLE;
		}
	}

	/**
	 *  Determines the state of the Add button.
	 */
	public class AddButtonDeterminer implements StateDeterminer {
		@Override
		public EpState determineState(final PolicyActionContainer targetContainer) {

			if (!getChangeSetHelper().isActiveChangeSet()) {
				return EpState.READ_ONLY;
			}

			return EpState.EDITABLE;
		}
	}

	/**
	 *  Determines the state of the Edit button.
	 */
	public class EditButtonDeterminer implements StateDeterminer {

			@Override
			public EpState determineState(final PolicyActionContainer targetContainer) {

				if (!getChangeSetHelper().isActiveChangeSet()) {
					return EpState.READ_ONLY;
				}

				Object obj = targetContainer.getPolicyDependent();

				if (obj == null) {
					return EpState.READ_ONLY;
				}

				if (obj instanceof SkuOptionValue) {
					// test the parent skuOption for change set inclusion if the object is a skuOptionValue
					obj = ((SkuOptionValue) obj).getSkuOption();
				}

				if (!getChangeSetHelper().isMemberOfActiveChangeset(obj)) {
					return EpState.READ_ONLY;
				}

				return EpState.EDITABLE;
			}

	}

	/**
	 *  Determines the state of the move buttons.
	 */
	public class MoveButtonDeterminer implements StateDeterminer {
		@Override
		public EpState determineState(final PolicyActionContainer targetContainer) {
			if (!getChangeSetHelper().isActiveChangeSet()) {
				return EpState.READ_ONLY;
			}
			if (targetContainer.getPolicyDependent() == null) {
				return EpState.READ_ONLY;
			}
			if (targetContainer.getPolicyDependent() instanceof SkuOption) {
				return EpState.READ_ONLY;
			}

			SkuOptionValue skuOptionValue;

			if (targetContainer.getPolicyDependent() instanceof SkuOptionValue) {
				skuOptionValue = (SkuOptionValue) targetContainer.getPolicyDependent();
				final SkuOption skuOption = skuOptionValue.getSkuOption();

				if (skuOption.isPersisted() && !getChangeSetHelper().isMemberOfActiveChangeset(skuOption)) {
					return EpState.READ_ONLY;
				}

			}

			return EpState.EDITABLE;
		}

	}


}
