/**
 * Copyright (c) Elastic Path Software Inc., 2008
 */
package com.elasticpath.cmclient.pricelistmanager.policy.catalog;

import java.util.Collection;

import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.common.AbstractStatePolicyImpl;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.pricelistmanager.PriceListManagerPermissions;
import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;
import com.elasticpath.domain.catalog.Catalog;

/**
 * Policy which determines the authorization for editing prices within the product editor.
 */
public class ProductEditorPricingAuthorizationStatePolicy extends
		AbstractStatePolicyImpl {
	
	private AuthorizationService authorizationService;
	private PriceListDescriptorDTO dto;
	private Collection<Catalog> catalogs;

	@Override
	public EpState determineState(final PolicyActionContainer targetContainer) {
		if (!authorizationService.isAuthorizedWithPermission(PriceListManagerPermissions.PRICE_MANAGEMENT_MANAGE_PRODUCT_PRICING)
				|| !hasAccessToPriceList(dto, catalogs)) {
			return EpState.DISABLED;
		}
		return EpState.EDITABLE;
	}

	@Override
	public void init(final Object dependentObject) {
		authorizationService = AuthorizationService.getInstance();
		if (dependentObject instanceof Object[]) {
			final Object[] object = (Object[]) dependentObject;
			if (object.length == 2 && object[0] instanceof PriceListDescriptorDTO && object[1] instanceof Collection) {
				dto = (PriceListDescriptorDTO) object[0];
				catalogs = (Collection<Catalog>) object[1];
				return;
			}
		}
		throw new IllegalArgumentException("dependentObject MUST be Object[] { PriceListDescriptorDTO, Collection<Catalog> }"); //$NON-NLS-1$
	}
	
	private boolean hasAccessToPriceList(final PriceListDescriptorDTO priceList, 
			final Collection<Catalog> priceListCatalogs) {
		
		if (!authorizationService.isAuthorizedForPriceList(priceList)) {
			return false;
		}
		
		return hasAccessToAtLeastOneCatalog(priceListCatalogs);
	}
	

	private boolean hasAccessToAtLeastOneCatalog(final Collection<Catalog> priceListCatalogs) {
		for (Catalog catalog : priceListCatalogs) {
			
			if (authorizationService.isAuthorizedForCatalog(catalog)) {
				return true;
			}
		}
		return false;
	}

}
