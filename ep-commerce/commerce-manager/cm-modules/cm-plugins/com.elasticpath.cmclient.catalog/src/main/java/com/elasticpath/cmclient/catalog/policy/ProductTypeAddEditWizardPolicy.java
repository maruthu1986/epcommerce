/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.policy;

import java.util.HashMap;
import java.util.Map;

import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.StateDeterminer;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.domain.catalog.ProductType;

/**
 * Policy for the Catalog Product Type Wizard.
 */
public class ProductTypeAddEditWizardPolicy extends AbstractCatalogDeterminerStatePolicy {
	
	private ProductType productType;

	private final Map<String, StateDeterminer> determinerMap = new HashMap<>();

	@Override
	public void init(final Object dependentObject) {
		productType = null;

		if (dependentObject instanceof ProductType) {
			productType = (ProductType) dependentObject;
		}
	}
	
	@Override
	public EpState determineState(final PolicyActionContainer targetContainer) {
		final StateDeterminer determiner = getDeterminer(targetContainer.getName());
		return determiner.determineState(targetContainer);
	}

	@Override
	protected StateDeterminer getDefaultDeterminer() {
		return new DefaultEditableDeterminer();
	}

	@Override
	protected Map<String, StateDeterminer> getDeterminerMap() {
		if (determinerMap.isEmpty()) {
			determinerMap.put("productTypeWizardPageOneMultiSku", new ProductTypeMultipleSkuAuthorizationDeterminer()); //$NON-NLS-1$
		}
		return determinerMap;
	}

	/**
	 * Determines state based on if the product type multiple sku is multi-sku enabled.
	 */
	public class ProductTypeMultipleSkuAuthorizationDeterminer implements StateDeterminer {

		@Override
		public EpState determineState(final PolicyActionContainer targetContainer) {
			if (productType == null) {
				return EpState.EDITABLE;
			}
			
			if (productType.isMultiSku()) {
				return EpState.READ_ONLY;
			}
			return EpState.EDITABLE;
		}
	}
	
}
