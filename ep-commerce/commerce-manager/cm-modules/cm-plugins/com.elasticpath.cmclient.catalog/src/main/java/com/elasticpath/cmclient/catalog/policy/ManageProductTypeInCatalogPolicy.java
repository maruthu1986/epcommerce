/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.policy;

import java.util.HashMap;
import java.util.Map;

import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.StateDeterminer;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;

/**
 *	 Policy Determiner for Catalog SkuOptionValue Section. 
 */
public class ManageProductTypeInCatalogPolicy  extends AbstractCatalogDeterminerStatePolicy {

	private final Map<String, StateDeterminer> determinerMap = new HashMap<>();
	
	@Override
	public void init(final Object dependentObject) {
		// do nothing
	}

	@Override
	protected StateDeterminer getDefaultDeterminer() {
		return new DefaultManageSkuOptionDeterminer();
		}

	@Override
	protected Map<String, StateDeterminer> getDeterminerMap() {
		if (determinerMap.isEmpty()) {
			determinerMap.put("productTypeWizard", new DefaultManageSkuOptionDeterminer()); //$NON-NLS-1$
			determinerMap.put("productTypeWizardTemplateControl", new SkuOptionTemplateControlDeterminer()); //$NON-NLS-1$
		}
		return determinerMap;
	}
	
	/**
	 * Determines state based on authorization to manage the dependent object.
	 */
	public class SkuOptionTemplateControlDeterminer implements StateDeterminer {
		@Override
		public EpState determineState(final PolicyActionContainer targetContainer) {
			return EpState.READ_ONLY;
		}
	}
	
	/**
	 * Determines state based on authorization to manage the dependent object.
	 */
	public class DefaultManageSkuOptionDeterminer implements StateDeterminer {
		@Override
		public EpState determineState(final PolicyActionContainer targetContainer) {
			return EpState.EDITABLE;
		}
	}

}
