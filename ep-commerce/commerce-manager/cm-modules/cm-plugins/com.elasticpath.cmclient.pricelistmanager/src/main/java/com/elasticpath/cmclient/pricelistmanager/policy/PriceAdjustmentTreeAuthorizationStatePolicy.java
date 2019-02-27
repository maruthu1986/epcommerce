/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.pricelistmanager.policy;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.StateDeterminer;
import com.elasticpath.cmclient.policy.common.AbstractDeterminerStatePolicyImpl;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.pricelistmanager.PriceListManagerPermissions;
import com.elasticpath.cmclient.pricelistmanager.PriceListManagerPlugin;
import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;

/**
 * Price adjustment authorization policy.
 */
public class PriceAdjustmentTreeAuthorizationStatePolicy extends AbstractDeterminerStatePolicyImpl {

	/**
	 * Default state determiner. 
	 */
	public class LocalDefaultDeterminer implements StateDeterminer {

		@Override
		public EpState determineState(final PolicyActionContainer targetContainer) {
			
			boolean isAuthorizedForPriceLists = authorizationService.isAuthorizedWithPermission(
					PriceListManagerPermissions.PRICE_MANAGEMENT_MANAGE_PRICE_LISTS);
			boolean isAuthorizedForProductPricing = 
				authorizationService.isAuthorizedWithPermission(PriceListManagerPermissions.PRICE_MANAGEMENT_MANAGE_PRODUCT_PRICING);

			if (((isAuthorizedForPriceLists && authorizationService.isAuthorizedForPriceList(priceListDto))
					|| (isAuthorizedForPriceLists && isNewDto()))
					&& isAuthorizedForProductPricing) {
				return EpState.EDITABLE;
			}
			return EpState.DISABLED;
		}
	}

	private PriceListDescriptorDTO priceListDto;
	
	private AuthorizationService authorizationService;
	
	private Boolean newDto;


	private boolean isNewDto() {
		if (newDto == null) {
			newDto = priceListDto == null || priceListDto.getName() == null; //name is mandatory so we can perform the check.
		}		
		return newDto;
	}

	/**
	 * Expects the dependent object to be a collection which contains the priceListDto.
	 * 
	 * @param dependentObject a collection which contains the price list descriptor
	 */
	@Override
	public void init(final Object dependentObject) {
		if (dependentObject instanceof Collection< ? >) {
			for (Object dependent : (Collection< ? >) dependentObject) {
				if (dependent instanceof PriceListDescriptorDTO) {
					this.priceListDto = (PriceListDescriptorDTO) dependent;
					break;
				}
			}
		}
		
		authorizationService = AuthorizationService.getInstance();
	}

	@Override
	protected String getPluginId() {
		return PriceListManagerPlugin.PLUGIN_ID;
	}

	@Override
	protected StateDeterminer getDefaultDeterminer() {
		return new LocalDefaultDeterminer();
	}

	@Override
	protected Map<String, StateDeterminer> getDeterminerMap() {
		Map<String, StateDeterminer> determiners = new HashMap<>();
		determiners.put("priceAdjustmentTreeContainer", new LocalDefaultDeterminer()); //$NON-NLS-1$
		return determiners;
	}

}
