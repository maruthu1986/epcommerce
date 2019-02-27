/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shoppingcart.validation.impl;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.shoppingcart.validation.PurchaseCartValidationService;
import com.elasticpath.service.shoppingcart.validation.ShoppingCartValidationContext;

/**
 * Implements {@link PurchaseCartValidationService}.
 */
public class PurchaseCartValidationServiceImpl
		extends AbstractAggregateValidator<ShoppingCartValidationContext, ShoppingCartValidationContext>
		implements PurchaseCartValidationService {

	private BeanFactory beanFactory;

	@Override
	public ShoppingCartValidationContext buildContext(final ShoppingCart shoppingCart, final CartOrder cartOrder) {
		ShoppingCartValidationContext validationContext = beanFactory.getBean(ContextIdNames.SHOPPING_CART_VALIDATION_CONTEXT);
		validationContext.setShoppingCart(shoppingCart);
		validationContext.setCartOrder(cartOrder);
		return validationContext;
	}

	@Override
	public ShoppingCartValidationContext buildContext(final ShoppingCart shoppingCart) {
		return buildContext(shoppingCart, null);
	}

	protected BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
}
