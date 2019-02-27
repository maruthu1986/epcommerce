/**
 * Copyright (c) Elastic Path Software Inc., 2011
 */
package com.elasticpath.cmclient.catalog.policy;

import java.util.HashMap;
import java.util.Map;

import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.StateDeterminer;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.domain.catalog.Brand;

/**
 *  A <code>StatePolicy</code> that will determine UI state for brand dialog related UI elements.
 */
public class AddEditBrandDialogStatePolicy extends AbstractCatalogDeterminerStatePolicy {

	private Brand brand;

	private final Map<String, StateDeterminer> determinerMap = new HashMap<>();

	@Override
	public void init(final Object dependentObject) {
		brand = null;

		if (dependentObject instanceof Brand) {
			brand = (Brand) dependentObject;
		}
	}



	@Override
	protected StateDeterminer getDefaultDeterminer() {
		return new DefaultManageBrandAuthorizationDeterminer();
	}

	@Override
	protected Map<String, StateDeterminer> getDeterminerMap() {
		if (determinerMap.isEmpty()) {
			determinerMap.put("catalogBrandCodeField", new BrandCodeAuthorizationDeterminer()); //$NON-NLS-1$
		}
		return determinerMap;
	}


	/**
	 * Determines state based on persistent state.
	 */
	public class BrandCodeAuthorizationDeterminer implements StateDeterminer {

		@Override
		public EpState determineState(final PolicyActionContainer targetContainer) {
			if (brand.isPersisted()) {
				return EpState.READ_ONLY;
			}
			return EpState.EDITABLE;
		}
	}

	/**
	 * Determiner for always editable controls.
	 */
	public class DefaultManageBrandAuthorizationDeterminer implements StateDeterminer {

		@Override
		public EpState determineState(final PolicyActionContainer targetContainer) {
			return EpState.EDITABLE;
		}
	}

}
