/*
 * Copyright (c) Elastic Path Software Inc., 2016
 */

package com.elasticpath.importexport.common.adapters.cartmodifier;

import static com.elasticpath.importexport.common.comparators.ExportComparators.CART_ITEM_MODIFIER_FIELD_OPTION_DTO;
import static com.elasticpath.importexport.common.comparators.ExportComparators.DISPLAY_VALUE_COMPARATOR;

import java.util.ArrayList;
import java.util.Collections;

import com.google.common.base.Preconditions;

import com.elasticpath.common.dto.DisplayValue;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.cartmodifier.CartItemModifierField;
import com.elasticpath.domain.cartmodifier.CartItemModifierFieldLdf;
import com.elasticpath.domain.cartmodifier.CartItemModifierFieldOption;
import com.elasticpath.domain.cartmodifier.CartItemModifierType;
import com.elasticpath.importexport.common.adapters.AbstractDomainAdapterImpl;
import com.elasticpath.importexport.common.dto.catalogs.CartItemModifierFieldDTO;
import com.elasticpath.importexport.common.dto.catalogs.CartItemModifierFieldOptionDTO;
import com.elasticpath.importexport.common.util.ImportExportUtil;

/**
 * The implementation of <code>DomainAdapter</code> interface.<br>
 * It is responsible for data transformation between <code>CartItemModifierField</code> and
 * <code>CartItemModifierFieldDTO</code> objects.
 */
public class CartItemModifierFieldAdapter extends AbstractDomainAdapterImpl<CartItemModifierField, CartItemModifierFieldDTO> {

	private CartItemModifierFieldOptionAdapter cartItemModifierFieldOptionAdapter;

	@Override
	public void populateDomain(final CartItemModifierFieldDTO cartItemModifierFieldDTO, final CartItemModifierField cartItemModifierField) {
		cartItemModifierField.setCode(cartItemModifierFieldDTO.getCode());
		cartItemModifierField.setOrdering(cartItemModifierFieldDTO.getOrdering());
		cartItemModifierField.setFieldType(CartItemModifierType.valueOfCamelCase(cartItemModifierFieldDTO.getType()));
		cartItemModifierField.setRequired(cartItemModifierFieldDTO.isRequired());
		cartItemModifierField.setMaxSize(cartItemModifierFieldDTO.getMaxSize());

		if (cartItemModifierFieldDTO.getMaxSize() != null && cartItemModifierFieldDTO.getMaxSize().intValue() == 0) {
			cartItemModifierField.setMaxSize(null);
		}

		Preconditions.checkNotNull(cartItemModifierField.getFieldType(), "Cannot accept CartItemModifierField with null type");

		validateShortTextRules(cartItemModifierField);

		populateCartItemModifierFieldLdfDomain(cartItemModifierFieldDTO, cartItemModifierField);
		populateCartItemModifierFieldOptionDomain(cartItemModifierFieldDTO, cartItemModifierField);
	}

	private void validateShortTextRules(final CartItemModifierField cartItemModifierField) {
		if (cartItemModifierField.getFieldType().getOrdinal() == CartItemModifierType.SHORT_TEXT_ORDINAL
				&& cartItemModifierField.getMaxSize() == null) {
			throw new IllegalArgumentException("Cannot accept CartItemModifierField with type SHORT_TEXT and null MaxSize");
		}

		if (cartItemModifierField.getFieldType().getOrdinal() != CartItemModifierType.SHORT_TEXT_ORDINAL
				&& cartItemModifierField.getMaxSize() != null) {
			throw new IllegalArgumentException("Cannot accept CartItemModifierField with type different than SHORT_TEXT and with a maxSize defined");
		}
	}

	private void populateCartItemModifierFieldLdfDomain(final CartItemModifierFieldDTO cartItemModifierFieldDTO,
			final CartItemModifierField cartItemModifierField) {
		if (cartItemModifierFieldDTO.getValues() == null) {
			return;
		}

		for (DisplayValue displayValue : cartItemModifierFieldDTO.getValues()) {
			CartItemModifierFieldLdf cartItemModifierFieldLdf = cartItemModifierField
					.findCartItemModifierFieldLdfByLocale(displayValue.getLanguage());

			if (cartItemModifierFieldLdf == null) {
				cartItemModifierFieldLdf = getBeanFactory().getBean(ContextIdNames.CART_ITEM_MODIFIER_FIELD_LDF);
				ImportExportUtil.getInstance().validateLocale(displayValue.getLanguage());
				cartItemModifierFieldLdf.setLocale(displayValue.getLanguage());
				cartItemModifierFieldLdf.setDisplayName(displayValue.getValue());
				cartItemModifierField.addCartItemModifierFieldLdf(cartItemModifierFieldLdf);
			} else {
				cartItemModifierFieldLdf.setDisplayName(displayValue.getValue());
			}
		}
	}

	private void populateCartItemModifierFieldOptionDomain(final CartItemModifierFieldDTO cartItemModifierFieldDTO, final CartItemModifierField
			cartItemModifierField) {

		if (cartItemModifierField.getFieldType().isPickType() && !doesCartItemModifierFieldDTOHasOptions(cartItemModifierFieldDTO)) {
			throw new IllegalArgumentException("When cartItemModifierField.type is either PICK_SINGLE_OPTION or PICK_MULTI_OPTION then you need to"
					+ " specify options");
		}

		if (!cartItemModifierField.getFieldType().isPickType() && doesCartItemModifierFieldDTOHasOptions(cartItemModifierFieldDTO)) {
			throw new IllegalArgumentException("When cartItemModifierField.type is neither PICK_SINGLE_OPTION or PICK_MULTI_OPTION then you cannot"
					+ " specify options");
		}

		if (!doesCartItemModifierFieldDTOHasOptions(cartItemModifierFieldDTO)) {
			return;
		}

		for (CartItemModifierFieldOptionDTO cartItemModifierFieldOptionDTO : cartItemModifierFieldDTO.getCartItemModifierFieldOptions()) {

			CartItemModifierFieldOption cartItemModifierFieldOption = cartItemModifierField.findCartItemModifierFieldOptionByValue(
					cartItemModifierFieldOptionDTO.getValue());

			if (cartItemModifierFieldOption == null) {
				cartItemModifierFieldOption = getBeanFactory().getBean(ContextIdNames.CART_ITEM_MODIFIER_FIELD_OPTION);
				cartItemModifierFieldOptionAdapter.populateDomain(cartItemModifierFieldOptionDTO, cartItemModifierFieldOption);
				cartItemModifierField.addCartItemModifierFieldOption(cartItemModifierFieldOption);
			} else {
				cartItemModifierFieldOptionAdapter.populateDomain(cartItemModifierFieldOptionDTO, cartItemModifierFieldOption);
			}
		}
	}

	private boolean doesCartItemModifierFieldDTOHasOptions(final CartItemModifierFieldDTO cartItemModifierFieldDTO) {
		return !(cartItemModifierFieldDTO.getCartItemModifierFieldOptions() == null
			|| cartItemModifierFieldDTO.getCartItemModifierFieldOptions().isEmpty());
	}

	@Override
	public void populateDTO(final CartItemModifierField cartItemModifierField, final CartItemModifierFieldDTO dto) {
		dto.setCode(cartItemModifierField.getCode());
		dto.setMaxSize(cartItemModifierField.getMaxSize());
		dto.setOrdering(cartItemModifierField.getOrdering());
		dto.setRequired(cartItemModifierField.isRequired());
		dto.setType(cartItemModifierField.getFieldType().getCamelName());
		dto.setValues(new ArrayList<>());
		dto.setCartItemModifierFieldOptions(new ArrayList<>());

		for (CartItemModifierFieldLdf cartItemModifierFieldLdf : cartItemModifierField.getCartItemModifierFieldsLdf()) {
			DisplayValue displayValue = new DisplayValue();
			displayValue.setValue(cartItemModifierFieldLdf.getDisplayName());
			displayValue.setLanguage(cartItemModifierFieldLdf.getLocale());
			dto.getValues().add(displayValue);
		}
		Collections.sort(dto.getValues(), DISPLAY_VALUE_COMPARATOR);

		for (CartItemModifierFieldOption cartItemModifierFieldOption : cartItemModifierField.getCartItemModifierFieldOptions()) {
			CartItemModifierFieldOptionDTO cartItemModifierFieldOptionDTO = new CartItemModifierFieldOptionDTO();
			dto.getCartItemModifierFieldOptions().add(cartItemModifierFieldOptionDTO);
			cartItemModifierFieldOptionAdapter.populateDTO(cartItemModifierFieldOption, cartItemModifierFieldOptionDTO);
		}
		Collections.sort(dto.getCartItemModifierFieldOptions(), CART_ITEM_MODIFIER_FIELD_OPTION_DTO);
	}

	public void setCartItemModifierFieldOptionAdapter(final CartItemModifierFieldOptionAdapter cartItemModifierFieldOptionAdapter) {
		this.cartItemModifierFieldOptionAdapter = cartItemModifierFieldOptionAdapter;
	}
}
