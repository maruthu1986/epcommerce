/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */

package com.elasticpath.cmclient.catalog.policy;

import java.util.HashMap;
import java.util.Map;

import com.elasticpath.cmclient.core.ui.framework.EpControlFactory;
import com.elasticpath.cmclient.policy.StateDeterminer;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.domain.cartmodifier.CartItemModifierGroup;

/**
 *  A <code>StatePolicy</code> that will determine UI state for Cart Item Modifier Groups dialog related UI elements.
 */
public class AddEditCartItemModifierGroupsSectionPolicy extends AbstractCatalogDeterminerStatePolicy {

	private CartItemModifierGroup group;

	private final Map<String, StateDeterminer> determinerMap = new HashMap<>();

	@Override
	public void init(final Object dependentObject) {
		if (dependentObject instanceof CartItemModifierGroup) {
			group = (CartItemModifierGroup) dependentObject;
		}
	}

	@Override
	protected StateDeterminer getDefaultDeterminer() {
		return new DefaultManageCartItemModifierGroupAuthorizationDeterminer();
	}

	@Override
	protected Map<String, StateDeterminer> getDeterminerMap() {
		if (determinerMap.isEmpty()) {
			determinerMap.put("cartItemModifierGroupCodeField", new CartItemModifierGroupCodeAuthorizationDeterminer()); //$NON-NLS-1$
		}
		return determinerMap;
	}


	/**
	 * Determines state based on persistent state.
	 */
	public class CartItemModifierGroupCodeAuthorizationDeterminer implements StateDeterminer {

		@Override
		public EpControlFactory.EpState determineState(final PolicyActionContainer targetContainer) {
			if (group.isPersisted()) {
				return EpControlFactory.EpState.READ_ONLY;
			}
			return EpControlFactory.EpState.EDITABLE;
		}
	}

	/**
	 * Determiner for always editable controls.
	 */
	public class DefaultManageCartItemModifierGroupAuthorizationDeterminer implements StateDeterminer {

		@Override
		public EpControlFactory.EpState determineState(final PolicyActionContainer targetContainer) {
			return EpControlFactory.EpState.EDITABLE;
		}
	}

}
