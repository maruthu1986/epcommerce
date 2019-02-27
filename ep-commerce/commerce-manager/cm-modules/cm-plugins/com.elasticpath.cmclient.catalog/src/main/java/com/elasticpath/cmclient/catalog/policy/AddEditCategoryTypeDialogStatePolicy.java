/**
 * Copyright (c) Elastic Path Software Inc., 2011
 */
package com.elasticpath.cmclient.catalog.policy;

import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.StateDeterminer;
import com.elasticpath.cmclient.policy.common.AbstractStatePolicyImpl;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;

/**
 *  A <code>StatePolicy</code> that will determine UI state for category type dialog related UI elements.
 */
public class AddEditCategoryTypeDialogStatePolicy extends AbstractStatePolicyImpl {

	@Override
	public void init(final Object object) {
		// Does nothing
	}

	@Override
	public EpState determineState(final PolicyActionContainer targetContainer) {
		final StateDeterminer determiner = getDeterminer(targetContainer.getName());

		return determiner.determineState(targetContainer);
	}

	/**
	 * Get the state determiner for the given container name.
	 *
	 * @param containerName the name of the container
	 * @return a <code>StateDeterminer</code>
	 */
	protected StateDeterminer getDeterminer(final String containerName) {
		// only one determiner at this time, so no need to check the containerName
		return new DefaultAuthorizationDeterminer();
	}

	/**
	 * Default state determiner.
	 */
	public class DefaultAuthorizationDeterminer implements StateDeterminer {

		/**
		 * Determine the state based on authorization.
		 * @param targetContainer the target container
		 * @return the <code>EpState</code> determined by the authorization.
		 */
		@Override
		public EpState determineState(final PolicyActionContainer targetContainer) {
			// FIXME: Once we have category type permissions, check it here
			return EpState.EDITABLE;
		}
	}
}
