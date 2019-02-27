/*
 * Copyright (c) Elastic Path Software Inc., 2016
 */

package com.elasticpath.importexport.common.adapters.cartmodifier;

import static com.elasticpath.importexport.common.comparators.ExportComparators.DISPLAY_VALUE_COMPARATOR;

import java.util.ArrayList;
import java.util.Collections;

import com.elasticpath.common.dto.DisplayValue;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.cartmodifier.CartItemModifierFieldOption;
import com.elasticpath.domain.cartmodifier.CartItemModifierFieldOptionLdf;
import com.elasticpath.importexport.common.adapters.AbstractDomainAdapterImpl;
import com.elasticpath.importexport.common.dto.catalogs.CartItemModifierFieldOptionDTO;
import com.elasticpath.importexport.common.util.ImportExportUtil;

/**
 * The implementation of <code>DomainAdapter</code> interface.<br>
 * It is responsible for data transformation between <code>CartItemModifierField</code> and
 * <code>CartItemModifierFieldDTO</code> objects.
 */
public class CartItemModifierFieldOptionAdapter extends AbstractDomainAdapterImpl<CartItemModifierFieldOption, CartItemModifierFieldOptionDTO> {

	@Override
	public void populateDomain(final CartItemModifierFieldOptionDTO cartItemModifierFieldOptionDTO,
			final CartItemModifierFieldOption cartItemModifierFieldOption) {
		cartItemModifierFieldOption.setValue(cartItemModifierFieldOptionDTO.getValue());
		cartItemModifierFieldOption.setOrdering(cartItemModifierFieldOptionDTO.getOrdering());

		populateCartItemModifierFieldOptionLdfDomain(cartItemModifierFieldOptionDTO, cartItemModifierFieldOption);

	}

	private void populateCartItemModifierFieldOptionLdfDomain(final CartItemModifierFieldOptionDTO cartItemModifierFieldOptionDTO,
			final CartItemModifierFieldOption cartItemModifierFieldOption) {

		if (cartItemModifierFieldOptionDTO.getValues() == null) {
			return;
		}

		for (DisplayValue displayValue : cartItemModifierFieldOptionDTO.getValues()) {

			CartItemModifierFieldOptionLdf cartItemModifierFieldOptionLdf = cartItemModifierFieldOption.getCartItemModifierFieldOptionsLdfByLocale(
					displayValue.getLanguage());
			if (cartItemModifierFieldOptionLdf == null) {
				cartItemModifierFieldOptionLdf = getBeanFactory().getBean(ContextIdNames.CART_ITEM_MODIFIER_OPTION_LDF);
				ImportExportUtil.getInstance().validateLocale(displayValue.getLanguage());
				cartItemModifierFieldOptionLdf.setLocale(displayValue.getLanguage());
				cartItemModifierFieldOptionLdf.setDisplayName(displayValue.getValue());
				cartItemModifierFieldOption.addCartItemModifierFieldOptionLdf(cartItemModifierFieldOptionLdf);
			} else {
				cartItemModifierFieldOptionLdf.setDisplayName(displayValue.getValue());
			}
		}
	}

	@Override
	public void populateDTO(final CartItemModifierFieldOption cartItemModifierFieldOption, final CartItemModifierFieldOptionDTO dto) {
		dto.setValue(cartItemModifierFieldOption.getValue());
		dto.setOrdering(cartItemModifierFieldOption.getOrdering());
		dto.setValues(new ArrayList<>());

		for (CartItemModifierFieldOptionLdf cartItemModifierFieldOptionLdf : cartItemModifierFieldOption.getCartItemModifierFieldOptionsLdf()) {
			DisplayValue displayValue = new DisplayValue();
			displayValue.setValue(cartItemModifierFieldOptionLdf.getDisplayName());
			displayValue.setLanguage(cartItemModifierFieldOptionLdf.getLocale());
			dto.getValues().add(displayValue);
		}
		Collections.sort(dto.getValues(), DISPLAY_VALUE_COMPARATOR);
	}

}
