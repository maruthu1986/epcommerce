/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.service.shoppingcart.actions.impl;

import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.service.shopper.ShopperService;
import com.elasticpath.service.shoppingcart.actions.CheckoutAction;
import com.elasticpath.service.shoppingcart.actions.CheckoutActionContext;

/**
 * CheckoutService to update the related customer record when an order is processed.
 */
public class UpdateCustomerCheckoutAction implements CheckoutAction {

	private CustomerService customerService;

	private ShopperService shopperService;

	@Override
	public void execute(final CheckoutActionContext context) {
		if (context.isOrderExchange()) {
			return;
		}

		final Shopper shopper = context.getShopper();
		final Customer customer = shopper.getCustomer();

		if (customer.isPersisted()) {
			updateCustomer(shopper, customer);
		} else {
			saveNewCustomer(shopper, customer);
		}
	}

	private void updateCustomer(final Shopper shopper, final Customer customer) {
		customerService.verifyCustomer(customer);
		final Customer updatedCustomer = customerService.update(customer);
		shopper.setCustomer(updatedCustomer);
	}

	private void saveNewCustomer(final Shopper shopper, final Customer customer) {
		customerService.add(customer);
		shopperService.save(shopper);
	}

	protected CustomerService getCustomerService() {
		return customerService;
	}

	public void setCustomerService(final CustomerService customerService) {
		this.customerService = customerService;
	}

	protected ShopperService getShopperService() {
		return shopperService;
	}

	public void setShopperService(final ShopperService shopperService) {
		this.shopperService = shopperService;
	}

}