/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */

package com.elasticpath.cmclient.catalog.policy;

import java.util.HashMap;
import java.util.Map;

import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;
import com.elasticpath.cmclient.policy.StateDeterminer;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;

/**
 * Policy for the Catalog Cart Item Modifier Field Option Add/Edit dialog.
 */
public class AddEditCartItemModifierFieldOptionDialogPolicy extends AbstractCatalogDeterminerStatePolicy {

	private final Map<String, StateDeterminer> determinerMap = new HashMap<>();

	@Override
	public void init(final Object dependentObject) {
		// empty
	}


	@Override
	protected StateDeterminer getDefaultDeterminer() {
		return new DefaultAuthorizationDeterminer();
	}

	@Override
	protected Map<String, StateDeterminer> getDeterminerMap() {
		return determinerMap;
	}

	/**
	 * Determiner for always editable controls.
	 */
	private static class DefaultAuthorizationDeterminer implements StateDeterminer {

		@Override
		public EpControlFactory.EpState determineState(final PolicyActionContainer targetContainer) {
			return EpControlFactory.EpState.EDITABLE;
		}
	}
}
