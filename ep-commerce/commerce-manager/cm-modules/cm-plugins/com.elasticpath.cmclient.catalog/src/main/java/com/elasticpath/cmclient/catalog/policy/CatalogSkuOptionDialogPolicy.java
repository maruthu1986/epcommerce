/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.policy;

import java.util.HashMap;
import java.util.Map;

import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.StateDeterminer;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.domain.skuconfiguration.SkuOption;

/**
 * Policy for the Catalog Sku option dialog.
 */
public class CatalogSkuOptionDialogPolicy extends AbstractCatalogDeterminerStatePolicy {

	private SkuOption skuOption;

	private final Map<String, StateDeterminer> determinerMap = new HashMap<>();

	@Override
	public void init(final Object dependentObject) {
		skuOption = null;

		if (dependentObject instanceof SkuOption) {
			skuOption = (SkuOption) dependentObject;
		}
	}

	@Override
	protected StateDeterminer getDefaultDeterminer() {
		return new DefaultManageSkuOptionAuthorizationDeterminer();
	}

	@Override
	protected Map<String, StateDeterminer> getDeterminerMap() {
		if (determinerMap.isEmpty()) {
			determinerMap.put("skuCodeEditSkuOptionDialogContainer", new SkuCodeAuthorizationDeterminer()); //$NON-NLS-1$
		}
		return determinerMap;
	}

	/**
	 * Determines state based on persistent state.
	 */
	public class SkuCodeAuthorizationDeterminer implements StateDeterminer {

		@Override
		public EpState determineState(final PolicyActionContainer targetContainer) {
			if (skuOption.isPersisted()) {
				return EpState.READ_ONLY;
			}
			return EpState.EDITABLE;
		}
	}
	
	
	/**
	 * Determiner for always editable controls.
	 */
	public class DefaultManageSkuOptionAuthorizationDeterminer implements StateDeterminer {
		
		@Override
		public EpState determineState(final PolicyActionContainer targetContainer) {
			return EpState.EDITABLE;
		}
	}

}
