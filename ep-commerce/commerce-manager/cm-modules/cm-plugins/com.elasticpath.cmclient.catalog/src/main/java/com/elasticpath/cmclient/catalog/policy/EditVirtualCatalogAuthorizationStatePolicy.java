/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.catalog.policy;

import java.util.Arrays;
import java.util.Collection;

import com.elasticpath.cmclient.catalog.CatalogPermissions;
import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.StateDeterminer;
import com.elasticpath.cmclient.policy.common.AbstractStatePolicyImpl;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;

/**
 * A <code>StatePolicy</code> that will determine UI state for virtual catalog related UI elements.
 * It will return editable based on the following criteria:
 * <ol>
 *   <li>The user has permission to the virtual catalog</li>
 *   <li>The user has permission to edit virtual catalog</li>
 * </ol>
 */
public class EditVirtualCatalogAuthorizationStatePolicy extends AbstractStatePolicyImpl {

	/**
	 * Construct an instance of this policy that ties to the product guid.
	 * 
	 * @param object that this policy applies to
	 */
	@Override
	public void init(final Object object) {
		// Does nothing
	}

	/**
	 * Determine the UI state of targets based on the following policy.
	 * 
	 * Controls are editable only if the user is authorized to edit products
	 * 
	 * @param targetContainer a set of policy targets
	 * @return the <code>EpState</code> determined by the policy.
	 */
	@Override
	public EpState determineState(final PolicyActionContainer targetContainer) {
		if (getReadOnlyContainerNames().contains(targetContainer.getName())) {
			return EpState.READ_ONLY;
		}

		StateDeterminer determiner = getDeterminer(targetContainer.getName());
		return determiner.determineState(targetContainer);
	}

	/**
	 * Get the collection of names of <code>PolicyTargetContainer</code> objects
	 * that should always have a read only status.
	 * 
	 * @return the collection of names
	 */
	protected Collection<String> getReadOnlyContainerNames() {
		return Arrays.asList("readOnlyVirtualCatalogDialog" //$NON-NLS-1$
				);
	}

	/**
	 * Get the state determiner for the given container name.
	 * 
	 * @param containerName the name of the container
	 * @return a <code>StateDeterminer</code>
	 */
	protected StateDeterminer getDeterminer(final String containerName) {
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
			if (AuthorizationService.getInstance().isAuthorizedWithPermission(CatalogPermissions.MANAGE_VIRTUAL_CATALOG_LINK_CATEGORY)) {
				return EpState.EDITABLE;
			}
			return EpState.READ_ONLY;
		}
	}
}
