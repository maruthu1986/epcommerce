/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.pricelistmanager.policy;

import org.apache.commons.lang.StringUtils;

import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.common.AbstractStatePolicyImpl;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;

/**
 * The policy for creating 'add button' in base amount container. 
 */
public class BaseAmountCreateStatePolicy extends AbstractStatePolicyImpl {

	private PriceListDescriptorDTO priceList;

	@Override
	public EpState determineState(final PolicyActionContainer targetContainer) {
		// if the target container is priceListBaseAmountAddButton and there's no price list in drop-down list.
		if ("priceListBaseAmountAddButton".equals(targetContainer.getName())   //$NON-NLS-1$
				&& StringUtils.isEmpty(priceList.getGuid())) {
			return EpState.READ_ONLY;
		}
		return EpState.EDITABLE;
	}

	@Override
	public void init(final Object dependentObject) {
		priceList = (PriceListDescriptorDTO) dependentObject;
	}

}
