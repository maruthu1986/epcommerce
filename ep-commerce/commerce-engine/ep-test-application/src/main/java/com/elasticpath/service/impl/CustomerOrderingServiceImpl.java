/*
 * Copyright (c) Elastic Path Software Inc., 2006
 */
package com.elasticpath.service.impl;

import static java.lang.String.format;
import static java.util.Collections.singletonList;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.collections.CollectionUtils;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.CustomerOrderingService;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.service.shipping.ShippingOptionResult;
import com.elasticpath.service.shipping.ShippingOptionService;
import com.elasticpath.service.shoppingcart.CheckoutService;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;

/**
 * Provides a high-level service for customer ordering controllers (used only in FIT tests).
 */
public class CustomerOrderingServiceImpl implements CustomerOrderingService {

	private CheckoutService checkoutService;

	private CustomerService customerService;

	private ShippingOptionService shippingOptionService;

	@Override
	public void selectShippingAddress(final Shopper shopper, final CustomerAddress shippingAddress) {

		final ShoppingCart shoppingCart = shopper.getCurrentShoppingCart();

		// Update the shopping cart with shipping address
		shoppingCart.setShippingAddress(shippingAddress);

		// Throw the exception if no shipping service is provided for the selected address
		checkoutService.retrieveShippingOption(shoppingCart);

		final ShippingOptionResult shippingOptionResult = shippingOptionService.getShippingOptions(shoppingCart);
		final String errorMessage = format("Unable to get available shipping options for the given cart with guid '%s'. "
						+ "So cannot validate shipping address.",
				shoppingCart.getGuid());
		shippingOptionResult.throwExceptionIfUnsuccessful(
				errorMessage,
				singletonList(
						new StructuredErrorMessage(
								"shippingoptions.unavailable",
								errorMessage,
								ImmutableMap.of(
										"cart-id", shoppingCart.getGuid())
						)
				));
		if (CollectionUtils.isEmpty(shippingOptionResult.getAvailableShippingOptions())) {
			throw new EpServiceException("No shipping option could be found for the shipping address " + shippingAddress);
		}

		final Customer customer = shopper.getCustomer();
		// Address will actually be added just if it's not currently contained in the customer's addresses list. Safe operation.
		customer.addAddress(shippingAddress);

		// Set the billing address to the shipping address by default
		// if no billing address is available
		if (shoppingCart.getBillingAddress() == null) {
			if (customer.getPreferredBillingAddress() == null) {
				shoppingCart.setBillingAddress(shippingAddress);
				customer.setPreferredBillingAddress(shippingAddress);
			} else {
				shoppingCart.setBillingAddress(customer.getPreferredBillingAddress());
			}
		}

		// Update the customer
		customer.setPreferredShippingAddress(shippingAddress);
		if (customer.isPersisted()) {
			customerService.verifyCustomer(customer);
			Customer updatedCustomer = customerService.update(customer);
			shopper.setCustomer(updatedCustomer);
		}
	}

	@Override
	public void selectBillingAddress(final Shopper shopper, final CustomerAddress billingAddress) {
		Customer customer = shopper.getCustomer();

		// Address will actually be added just if it's not currently contained in the customer's addresses list. Safe operation.
		customer.addAddress(billingAddress);
		customer.setPreferredBillingAddress(billingAddress);

		if (customer.isPersisted()) {
			customerService.verifyCustomer(customer);
		}

		// Update the shopping cart
		shopper.getCurrentShoppingCart().setBillingAddress(billingAddress);
	}

	@Override
	public ShoppingCart selectShippingOption(final ShoppingCart shoppingCart, final ShippingOption shippingOption) {
		shoppingCart.setSelectedShippingOption(shippingOption);
		return shoppingCart;
	}

	@Override
	public void setCheckoutService(final CheckoutService checkoutService) {
		this.checkoutService = checkoutService;
	}

	@Override
	public void setCustomerService(final CustomerService customerService) {
		this.customerService = customerService;
	}

	public void setShippingOptionService(final ShippingOptionService shippingOptionService) {
		this.shippingOptionService = shippingOptionService;
	}

}
