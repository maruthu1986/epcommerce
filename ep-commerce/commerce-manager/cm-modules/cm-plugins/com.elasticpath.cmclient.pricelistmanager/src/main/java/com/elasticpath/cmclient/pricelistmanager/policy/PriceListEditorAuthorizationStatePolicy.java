/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.pricelistmanager.policy;

import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.common.AbstractStatePolicyImpl;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.pricelistmanager.PriceListManagerPermissions;
import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;

/**
 * Price list editor authorisation policy.
 */
public class PriceListEditorAuthorizationStatePolicy extends AbstractStatePolicyImpl {
	
	private PriceListDescriptorDTO priceListDto;
	
	private AuthorizationService authorizationService;
	
	private Boolean newDto;

	@Override
	public EpState determineState(final PolicyActionContainer targetContainer) {
		if ((authorizationService.isAuthorizedWithPermission(PriceListManagerPermissions.PRICE_MANAGEMENT_MANAGE_PRICE_LISTS)
				&& authorizationService.isAuthorizedForPriceList(priceListDto))
				||
				(authorizationService.isAuthorizedWithPermission(PriceListManagerPermissions.PRICE_MANAGEMENT_MANAGE_PRICE_LISTS)
				&& isNewDto())
				) {
			return EpState.EDITABLE;			
		}
		return EpState.DISABLED;
		
	}
	
	private boolean isNewDto() {
		if (newDto == null) {
			newDto = priceListDto.getName() == null; //name is mandatory so we can perform the check.
		}		
		return newDto;
	}

	@Override
	public void init(final Object priceListDto) {
		this.priceListDto = (PriceListDescriptorDTO) priceListDto;
		authorizationService = AuthorizationService.getInstance();
	}

}
