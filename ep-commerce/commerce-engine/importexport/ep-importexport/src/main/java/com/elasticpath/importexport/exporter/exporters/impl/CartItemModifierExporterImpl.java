/*
 * Copyright (c) Elastic Path Software Inc., 2016
 */

package com.elasticpath.importexport.exporter.exporters.impl;

import java.util.List;

import com.elasticpath.domain.cartmodifier.CartItemModifierGroup;
import com.elasticpath.importexport.common.dto.catalogs.CartItemModifierGroupDTO;
import com.elasticpath.importexport.common.dto.catalogs.CatalogDTO;
import com.elasticpath.service.cartitemmodifier.CartItemModifierService;

/**
 * This class is responsible for exporting {@link com.elasticpath.domain.cartmodifier.CartItemModifierGroup}.
 */
public class CartItemModifierExporterImpl extends AbstractDependentExporterImpl<CartItemModifierGroup, CartItemModifierGroupDTO, CatalogDTO> {

	private CartItemModifierService cartItemModifierService;

	@Override
	public List<CartItemModifierGroup> findDependentObjects(final long catalogUid) {
		return cartItemModifierService.findCartItemModifierGroupByCatalogUid(catalogUid);
	}

	@Override
	public void bindWithPrimaryObject(final List<CartItemModifierGroupDTO> list, final CatalogDTO catalogDTO) {
		catalogDTO.setCartItemModifierGroups(list);
	}

	/**
	 * Set the cartItemModifierService.
	 *
	 * @param cartItemModifierService the cartItemModifierService
	 */
	public void setCartItemModifierService(final CartItemModifierService cartItemModifierService) {
		this.cartItemModifierService = cartItemModifierService;
	}

}
