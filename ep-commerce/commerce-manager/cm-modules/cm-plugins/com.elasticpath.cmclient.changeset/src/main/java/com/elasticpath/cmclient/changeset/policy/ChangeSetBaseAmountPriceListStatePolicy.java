/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.changeset.policy;

import java.util.HashMap;
import java.util.Map;

import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.StateDeterminer;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;

/**
 * A change set aware policy targeting base amounts in a price list editor.
 */
public class ChangeSetBaseAmountPriceListStatePolicy extends AbstractChangeSetDeterminerStatePolicy {

	private PriceListDescriptorDTO priceListDto;

	@Override
	public void init(final Object dependentObject) {
		priceListDto = (PriceListDescriptorDTO) dependentObject;
	}

	@Override
	protected StateDeterminer getDefaultDeterminer() {
		return new DefaultEditableDeterminer();
	}

	@Override
	protected Map<String, StateDeterminer> getDeterminerMap() {
		Map<String, StateDeterminer> determiners = new HashMap<String, StateDeterminer>();
		determiners.put("priceListBaseAmountAddButton", new AddButtonStateDeterminer()); //$NON-NLS-1$
		determiners.put("priceListBaseAmountEditButton", new EditButtonStateDeterminer()); //$NON-NLS-1$
		determiners.put("priceListBaseAmountRemoveButton", new ChangeSetBaseAmountPolicyImpl.BaseAmountRemovingDeterminer()); //$NON-NLS-1$
		return determiners;
	}

	/**
	 * Edit button state determiner.
	 */
	public class EditButtonStateDeterminer implements StateDeterminer {

		@Override
		public EpState determineState(final PolicyActionContainer targetContainer) {
			BaseAmountDTO baseAmount = (BaseAmountDTO) targetContainer.getPolicyDependent();
			if (baseAmount != null && getChangeSetHelper().isMemberOfActiveChangeset(baseAmount)) {
				return EpState.EDITABLE;
			}
			return EpState.READ_ONLY;
		}
	}

	/**
	 * Add button determiner.
	 */
	public class AddButtonStateDeterminer implements StateDeterminer {

		@Override
		public EpState determineState(final PolicyActionContainer targetContainer) {
			if (priceListDto != null && getChangeSetHelper().isMemberOfActiveChangeset(priceListDto)) {
				return EpState.EDITABLE;
			}
			return EpState.READ_ONLY;
		}
	}
}
