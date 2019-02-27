/*
 * Copyright (c) Elastic Path Software Inc., 2016
 */

package com.elasticpath.tools.sync.merge.configuration.impl;

import com.elasticpath.domain.cartmodifier.CartItemModifierGroup;
import com.elasticpath.persistence.api.Persistable;
import com.elasticpath.service.cartitemmodifier.CartItemModifierService;
import com.elasticpath.tools.sync.exception.SyncToolConfigurationException;

/**
 * Locator for {@link CartItemModifierGroup}.
 */
public class CartItemModifierGroupLocatorImpl extends AbstractEntityLocator {

	private CartItemModifierService cartItemModifierService;

	@Override
	public Persistable locatePersistence(final String guid, final Class<?> clazz) throws SyncToolConfigurationException {
		return cartItemModifierService.findCartItemModifierGroupByCode(guid);
	}

	@Override
	public boolean isResponsibleFor(final Class<?> clazz) {
		return CartItemModifierGroup.class.isAssignableFrom(clazz);
	}

	public void setCartItemModifierService(final CartItemModifierService cartItemModifierService) {
		this.cartItemModifierService = cartItemModifierService;
	}
}
