/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.changeset.policy;

import java.util.Arrays;
import java.util.Collection;

import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.common.AbstractStatePolicyImpl;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.service.pricing.PriceListDescriptorService;
import com.elasticpath.cmclient.core.ServiceLocator;

/**
 * Change set policies for price list descriptors. Currently governs <code>PriceListEditor</code> and 
 * components along with <code>PriceListEditorStatePolicy</code> which provides the authorisation policies.
 */
@SuppressWarnings({"PMD.PrematureDeclaration"})
public class ChangeSetPriceListPolicyImpl extends AbstractStatePolicyImpl {

	private PriceListDescriptorService priceListDescriptorService;
	private PriceListDescriptorDTO pldDto;
	
	
	/**
	 * Override the initialisation to hold the price list descriptor we want.
	 * @param dependentObject the dependent object, should be an instance of {@link PriceListDescriptorDTO}
	 */
	@Override
	public void init(final Object dependentObject) {
		pldDto = (PriceListDescriptorDTO) dependentObject;
		priceListDescriptorService = ServiceLocator.getService(ContextIdNames.PRICE_LIST_DESCRIPTOR_SERVICE);
	}

	@Override
	public EpState determineState(final PolicyActionContainer targetContainer) {
		if (getEditableContainerNames().contains(targetContainer.getName()) || pldDto == null) {
			return EpState.EDITABLE;
		}
		
		if (isNewPriceList(pldDto)) {
			return EpState.EDITABLE;
		}
		
		if (getChangeSetHelper().isDisabledOrMemberOfActiveChangeset(pldDto)) {
			return EpState.EDITABLE;
		}
		return EpState.READ_ONLY;
	}

	/**
	 */
	private boolean isNewPriceList(final PriceListDescriptorDTO priceListDescriptor) {
		return priceListDescriptorService.findByGuid(priceListDescriptor.getGuid()) == null;
	}
	
	/**
	 * Get the collection of names of <code>PolicyTargetContainer</code> objects
	 * that should always have editable status.
	 * 
	 * @return the collection of names
	 */
	protected Collection<String> getEditableContainerNames() {
		return Arrays.asList(
				"priceListAlwaysEditableControls", //$NON-NLS-1$
				"editBaseAmountButtonContainer" //$NON-NLS-1$
				); 
	}
	
}
