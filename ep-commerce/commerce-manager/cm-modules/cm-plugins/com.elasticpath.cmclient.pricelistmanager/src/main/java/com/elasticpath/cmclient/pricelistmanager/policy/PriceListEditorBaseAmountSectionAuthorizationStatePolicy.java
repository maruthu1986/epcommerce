/**
 * Copyright (c) Elastic Path Software Inc., 2009
 */
package com.elasticpath.cmclient.pricelistmanager.policy;

import java.util.HashMap;
import java.util.Map;

import com.elasticpath.cmclient.core.service.AuthorizationService;
import com.elasticpath.cmclient.core.ui.framework.EpControlFactory.EpState;
import com.elasticpath.cmclient.policy.StateDeterminer;
import com.elasticpath.cmclient.policy.common.AbstractDeterminerStatePolicyImpl;
import com.elasticpath.cmclient.policy.common.PolicyActionContainer;
import com.elasticpath.cmclient.pricelistmanager.PriceListManagerPermissions;
import com.elasticpath.cmclient.pricelistmanager.PriceListManagerPlugin;
import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.common.dto.pricing.PriceListDescriptorDTO;

/**
 * Price list editor authorisation policy.
 */
@SuppressWarnings({ "PMD.PrematureDeclaration" })
public class PriceListEditorBaseAmountSectionAuthorizationStatePolicy extends AbstractDeterminerStatePolicyImpl {

	/**
	 * Default state determiner.
	 */
	public class LocalDefaultDeterminer implements StateDeterminer {

		@Override
		public EpState determineState(final PolicyActionContainer targetContainer) {

			final boolean isAuthorizedForPriceLists =
				authorizationService.isAuthorizedWithPermission(PriceListManagerPermissions.PRICE_MANAGEMENT_MANAGE_PRICE_LISTS);
			final boolean isAuthorizedForProductPricing =
				authorizationService.isAuthorizedWithPermission(PriceListManagerPermissions.PRICE_MANAGEMENT_MANAGE_PRODUCT_PRICING);

			if (priceListDto == null || priceListDto.getGuid() == null) {
				return EpState.DISABLED;
			}
			if ((isAuthorizedForPriceLists && authorizationService.isAuthorizedForPriceList(priceListDto) || isAuthorizedForPriceLists && isNewDto())
					&& isAuthorizedForProductPricing) {
				return EpState.EDITABLE;
			}
			return EpState.DISABLED;
		}
	}

	/**
	 * Edit/Delete button state determiner.
	 */
	private class EditDeleteStateDeterminer extends LocalDefaultDeterminer {

		@Override
		public EpState determineState(final PolicyActionContainer targetContainer) {
			if (targetContainer.getPolicyDependent() instanceof BaseAmountDTO) {
				return super.determineState(targetContainer);
			}
			return EpState.DISABLED;
		}

	}

	private PriceListDescriptorDTO priceListDto;

	private AuthorizationService authorizationService;

	private Boolean newDto;


	private boolean isNewDto() {
		if (newDto == null) {
			newDto = priceListDto == null || priceListDto.getName() == null; //name is mandatory so we can perform the
			// check.
		}
		return newDto;
	}

	@Override
	public void init(final Object priceListDto) {
		this.priceListDto = (PriceListDescriptorDTO) priceListDto;
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
		final Map<String, StateDeterminer> determiners = new HashMap<>();
		determiners.put("priceListBaseAmountAddButton", new LocalDefaultDeterminer()); //$NON-NLS-1$
		determiners.put("priceListBaseAmountEditButton", new EditDeleteStateDeterminer()); //$NON-NLS-1$
		determiners.put("priceListBaseAmountRemoveButton", new EditDeleteStateDeterminer()); //$NON-NLS-1$
		return determiners;
	}

}
