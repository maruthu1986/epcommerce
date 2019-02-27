/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.policy;

import java.util.HashMap;
import java.util.Map;

import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.StateDeterminer;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;

/**
 * Policy for the Catalog Sku option value dialog.
 */
public class CatalogSkuOptionValueDialogPolicy extends AbstractCatalogDeterminerStatePolicy {

	private SkuOptionValue skuOptionValue;
	
	private final Map<String, StateDeterminer> determinerMap = new HashMap<>();

	@Override
	public void init(final Object dependentObject) {
		skuOptionValue = null;

		if (dependentObject instanceof SkuOptionValue) {
			skuOptionValue = (SkuOptionValue) dependentObject;
		}
	}

	@Override
	protected StateDeterminer getDefaultDeterminer() {
		return new DefaultManageSkuOptionValueAuthorizationDeterminer();
	}

	@Override
	protected Map<String, StateDeterminer> getDeterminerMap() {
		if (determinerMap.isEmpty()) {
			determinerMap.put("catalogSkuOptionValueCodeField", new SkuOptionValueCodeAuthorizationDeterminer()); //$NON-NLS-1$
		}
		return determinerMap;
	}


	/**
	 * Determines state based on persistent state.
	 */
	public class SkuOptionValueCodeAuthorizationDeterminer implements StateDeterminer {

		@Override
		public EpState determineState(final PolicyActionContainer targetContainer) {
			if (skuOptionValue.isPersisted()) {
				return EpState.READ_ONLY;
			}
			return EpState.EDITABLE;
		}
	}
	
	/**
	 * Determiner for always editable controls.
	 */
	public class DefaultManageSkuOptionValueAuthorizationDeterminer implements StateDeterminer {
		
		@Override
		public EpState determineState(final PolicyActionContainer targetContainer) {
			return EpState.EDITABLE;
		}
	}
	
}
