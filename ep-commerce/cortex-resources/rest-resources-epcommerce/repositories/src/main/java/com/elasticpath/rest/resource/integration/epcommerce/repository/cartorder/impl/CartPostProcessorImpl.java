/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.impl;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;

import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartPostProcessor;
import com.elasticpath.service.cartorder.CartOrderService;
import com.elasticpath.service.shoppingcart.ShoppingCartRefresher;

/**
 * Performs post processing on carts. Implements {@link CartPostProcessor}.
 */
@Named("cartPostProcessor")
public class CartPostProcessorImpl implements CartPostProcessor {

	private static final Logger LOG = LoggerFactory.getLogger(CartPostProcessorImpl.class);

	private final CartOrderService cartOrderService;
	private final ShoppingCartRefresher shoppingCartRefresher;

	/**
	 * Constructor.
	 *
	 * @param cartOrderService      the cartOrderService
	 * @param shoppingCartRefresher the shopping cart refresher.
	 */
	@Inject
	public CartPostProcessorImpl(
			@Named("cartOrderService")
			final CartOrderService cartOrderService,
			@Named("shoppingCartRefresher")
			final ShoppingCartRefresher shoppingCartRefresher) {

		this.cartOrderService = cartOrderService;

		this.shoppingCartRefresher = shoppingCartRefresher;
	}

	@Override
	public void postProcessCart(final ShoppingCart cart,
			final Shopper shopper,
			final CustomerSession customerSession) {

		if (cart != null) {
			//Need to setup session and cart for refresher to work properly.
			shopper.setCurrentShoppingCart(cart);
			shopper.updateTransientDataWith(customerSession);

			shoppingCartRefresher.refresh(cart);

			//Required by cortex
			try {
				cartOrderService.createOrderIfPossible(cart);
			} catch (DataIntegrityViolationException dive) {
				LOG.warn("Cart order already created by another thread for the same shopping cart with id: {} and shopper: {}",
					cart.getGuid(), shopper.getGuid());
			}
		}
	}

}
