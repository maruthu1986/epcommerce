/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.addresses.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Single;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.rest.resource.integration.epcommerce.repository.addresses.CartOrdersDefaultAddressPopulator;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;

/**
 * Populate cart order default address fields.
 */
@Singleton
@Named("cartOrdersDefaultAddressPopulator")
public class CartOrdersDefaultAddressPopulatorImpl implements CartOrdersDefaultAddressPopulator {
	
	private final CartOrderRepository cartOrderRepository;

	/**
	 * Constructor.
	 *
	 * @param cartOrderRepository cart order repository.
	 */
	@Inject
	public CartOrdersDefaultAddressPopulatorImpl(
			@Named("cartOrderRepository")
			final CartOrderRepository cartOrderRepository) {
		this.cartOrderRepository = cartOrderRepository;
	}

	@Override
	public Completable updateAllCartOrdersAddresses(final Customer customer, final CustomerAddress address, final String storeCode,
			final boolean updateBillingAddress, final boolean updateShippingAddress) {
		return cartOrderRepository.findCartOrderGuidsByCustomerAsObservable(storeCode, customer.getGuid())
				.flatMapSingle(cartOrderGuid -> cartOrderRepository.findByGuidAsSingle(storeCode, cartOrderGuid))
				.flatMapSingle(cartOrder -> updateBillingAddress(address, updateBillingAddress, cartOrder))
				.flatMapCompletable(cartOrder -> updateShippingAddress(address, updateShippingAddress, storeCode, cartOrder));
	}

	/**
	 * Update the billing address on the cartOrder.
	 *
	 * @param address                        address
	 * @param updatedPreferredBillingAddress updatedPreferredBillingAddress
	 * @param cartOrder                      cartOrder
	 * @return cart order
	 */
	protected Single<CartOrder> updateBillingAddress(final CustomerAddress address, final boolean updatedPreferredBillingAddress,
			final CartOrder cartOrder) {
		if (updatedPreferredBillingAddress && cartOrder.getBillingAddressGuid() == null) {
			cartOrder.setBillingAddressGuid(address.getGuid());
			return cartOrderRepository.saveCartOrderAsSingle(cartOrder);
		}
		return Single.just(cartOrder);
	}

	/**
	 * Update the shipping address on the cart order.
	 *
	 * @param address                         address
	 * @param updatedPreferredShippingAddress updatedPreferredShippingAddress
	 * @param storeCode                       storeCode
	 * @param cartOrder                       cartOrder
	 * @return Completable
	 */
	protected Completable updateShippingAddress(final CustomerAddress address, final boolean updatedPreferredShippingAddress,
			final String storeCode, final CartOrder cartOrder) {
		if (updatedPreferredShippingAddress && cartOrder.getShippingAddressGuid() == null) {
			return cartOrderRepository.updateShippingAddressOnCartOrderAsSingle(address.getGuid(), cartOrder.getGuid(), storeCode).toCompletable();
		}
		return Completable.complete();
	}
}
