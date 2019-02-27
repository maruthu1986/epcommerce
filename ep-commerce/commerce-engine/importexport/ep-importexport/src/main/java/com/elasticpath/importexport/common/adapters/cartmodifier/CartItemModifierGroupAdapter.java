/*
 * Copyright (c) Elastic Path Software Inc., 2016
 */

package com.elasticpath.importexport.common.adapters.cartmodifier;

import static com.elasticpath.importexport.common.comparators.ExportComparators.CART_ITEM_MODIFIER_FIELD_DTO;
import static com.elasticpath.importexport.common.comparators.ExportComparators.DISPLAY_VALUE_COMPARATOR;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.log4j.Logger;

import com.elasticpath.common.dto.DisplayValue;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.cartmodifier.CartItemModifierField;
import com.elasticpath.domain.cartmodifier.CartItemModifierGroup;
import com.elasticpath.domain.cartmodifier.CartItemModifierGroupLdf;
import com.elasticpath.importexport.common.adapters.AbstractDomainAdapterImpl;
import com.elasticpath.importexport.common.dto.catalogs.CartItemModifierFieldDTO;
import com.elasticpath.importexport.common.dto.catalogs.CartItemModifierGroupDTO;
import com.elasticpath.importexport.common.util.ImportExportUtil;
import com.elasticpath.service.cartitemmodifier.CartItemModifierService;

/**
 * The implementation of <code>DomainAdapter</code> interface.<br>
 * It is responsible for data transformation between <code>CartItemModifierGroup</code> and
 * <code>CartItemModifierGroupDTO</code> objects.
 */
public class CartItemModifierGroupAdapter extends AbstractDomainAdapterImpl<CartItemModifierGroup, CartItemModifierGroupDTO> {

	private static final Logger LOG = Logger.getLogger(CartItemModifierGroupAdapter.class);

	private CartItemModifierFieldAdapter cartItemModifierFieldAdapter;

	private CartItemModifierService cartItemModifierService;

	@Override
	public void populateDomain(final CartItemModifierGroupDTO dto, final CartItemModifierGroup cartItemModifierGroup) {
		cartItemModifierGroup.setCode(dto.getCode());

		populateCartItemModifierGroupLdfDomain(dto, cartItemModifierGroup);

		populateCartItemModifierFieldDomain(dto, cartItemModifierGroup);
	}

	private void populateCartItemModifierGroupLdfDomain(final CartItemModifierGroupDTO cartItemModifierGroupDTO, final CartItemModifierGroup
			cartItemModifierGroup) {
		if (cartItemModifierGroupDTO.getValues() == null) {
			return;
		}

		for (DisplayValue displayValue : cartItemModifierGroupDTO.getValues()) {
			CartItemModifierGroupLdf cartItemModifierGroupLdf = cartItemModifierGroup
					.getCartItemModifierGroupLdfByLocale(displayValue.getLanguage());

			if (cartItemModifierGroupLdf == null) {
				cartItemModifierGroupLdf = getBeanFactory().getBean(ContextIdNames.CART_ITEM_MODIFIER_GROUP_LDF);
				ImportExportUtil.getInstance().validateLocale(displayValue.getLanguage());
				cartItemModifierGroupLdf.setLocale(displayValue.getLanguage());
				cartItemModifierGroupLdf.setDisplayName(displayValue.getValue());
				cartItemModifierGroup.addCartItemModifierGroupLdf(cartItemModifierGroupLdf);
			} else {
				cartItemModifierGroupLdf.setDisplayName(displayValue.getValue());
			}

			cartItemModifierGroupLdf.setDisplayName(displayValue.getValue());
		}
	}

	private void populateCartItemModifierFieldDomain(final CartItemModifierGroupDTO dto, final CartItemModifierGroup cartItemModifierGroup) {
		for (CartItemModifierFieldDTO cartItemModifierFieldDTO : dto.getCartItemModifierFields()) {

			LOG.info("Processing cartItemModifierFieldDTO with code: " + cartItemModifierFieldDTO.getCode());

			CartItemModifierField cartItemModifierField = cartItemModifierGroup.getCartItemModifierFieldByCode(cartItemModifierFieldDTO.getCode());

			if (cartItemModifierField == null) {
				verifyCartItemModifierFieldDoesNotExist(dto.getCode(), cartItemModifierFieldDTO.getCode());
				LOG.info("Creating new cartItemModifierFieldDTO with code: " + cartItemModifierFieldDTO.getCode());
				cartItemModifierField = getBeanFactory().getBean(ContextIdNames.CART_ITEM_MODIFIER_FIELD);
				cartItemModifierField.setCode(cartItemModifierFieldDTO.getCode());
			}

			cartItemModifierFieldAdapter.populateDomain(cartItemModifierFieldDTO, cartItemModifierField);
			cartItemModifierGroup.addCartItemModifierField(cartItemModifierField);
		}
	}

	private void verifyCartItemModifierFieldDoesNotExist(final String groupCode, final String cartItemModifierFieldCode) {
		CartItemModifierField cartItemModifierFieldByCode = cartItemModifierService.findCartItemModifierFieldByCode(cartItemModifierFieldCode);
		if (cartItemModifierFieldByCode != null) {
			throw new IllegalArgumentException("A CartItemModifierField cannot belong to more than one CartItemModifierGroup ("
					+ groupCode + ", " + cartItemModifierFieldCode + ")");
		}
	}

	@Override
	public void populateDTO(final CartItemModifierGroup cartItemModifierGroup, final CartItemModifierGroupDTO dto) {
		dto.setCode(cartItemModifierGroup.getCode());
		dto.setValues(new ArrayList<>());
		dto.setCartItemModifierFields(new ArrayList<>());

		for (CartItemModifierGroupLdf cartItemModifierGroupLdf : cartItemModifierGroup.getCartItemModifierGroupLdf()) {
			DisplayValue displayValue = new DisplayValue();
			displayValue.setValue(cartItemModifierGroupLdf.getDisplayName());
			displayValue.setLanguage(cartItemModifierGroupLdf.getLocale());
			dto.getValues().add(displayValue);
		}
		Collections.sort(dto.getValues(), DISPLAY_VALUE_COMPARATOR);

		for (CartItemModifierField cartItemModifierField : cartItemModifierGroup.getCartItemModifierFields()) {
			CartItemModifierFieldDTO cartItemModifierFieldDTO = new CartItemModifierFieldDTO();
			dto.getCartItemModifierFields().add(cartItemModifierFieldDTO);
			cartItemModifierFieldAdapter.populateDTO(cartItemModifierField, cartItemModifierFieldDTO);
		}
		Collections.sort(dto.getCartItemModifierFields(), CART_ITEM_MODIFIER_FIELD_DTO);
	}

	@Override
	public CartItemModifierGroup createDomainObject() {
		return getBeanFactory().getBean(ContextIdNames.CART_ITEM_MODIFIER_GROUP);
	}

	@Override
	public CartItemModifierGroupDTO createDtoObject() {
		return new CartItemModifierGroupDTO();
	}

	public void setCartItemModifierFieldAdapter(final CartItemModifierFieldAdapter cartItemModifierFieldAdapter) {
		this.cartItemModifierFieldAdapter = cartItemModifierFieldAdapter;
	}

	public void setCartItemModifierService(final CartItemModifierService cartItemModifierService) {
		this.cartItemModifierService = cartItemModifierService;
	}
}
