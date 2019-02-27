/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.convertors;

import javax.inject.Named;
import javax.inject.Singleton;

import org.springframework.core.convert.converter.Converter;

import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.rest.definition.carts.CartEntity;

/**
 * ShoppingCart to CartEntity Converter.
 */
@Singleton
@Named
public class ShoppingCartConverter implements Converter<ShoppingCart, CartEntity> {

	@Override
	public CartEntity convert(final ShoppingCart shoppingCart) {
		return CartEntity.builder()
				.withCartId(shoppingCart.getGuid())
				.withTotalQuantity(shoppingCart.getNumItems())
				.build();
	}
}
