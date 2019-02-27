/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.service.shipping;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;

/**
 * Shipping option service interface.
 */
public interface ShippingOptionService {

	/**
	 * Returns a list of unpriced shipping options that are applicable to the given {@link ShoppingCart}.
	 *
	 * @param shoppingCart shippable item container.
	 * @return {@link ShippingOptionResult}, never {@code null}.
	 */
	ShippingOptionResult getShippingOptions(ShoppingCart shoppingCart);

	/**
	 * Returns a list of unpriced shipping options that are applicable to the given arguments.
	 *
	 * @param destination the destination address
	 * @param storeCode   the store code
	 * @param locale      the locale
	 * @return {@link ShippingOptionResult}, never {@code null}.
	 */
	ShippingOptionResult getShippingOptions(Address destination,
											String storeCode,
											Locale locale);

	/**
	 * Returns the default shipping option from list of available shipping options if there is one configured.
	 *
	 * @param availableShippingOptions available shipping options
	 * @return returns an {@link Optional} containing the default shipping option if one has been set, or {@link Optional#empty()} if not.
	 */
	Optional<ShippingOption> getDefaultShippingOption(List<ShippingOption> availableShippingOptions);

	/**
	 * Returns all the default shipping option list with locale.
	 *
	 * @param storeCode the store code
	 * @param locale    the locale
	 * @return {@link ShippingOptionResult}, never {@code null}.
	 */
	ShippingOptionResult getAllShippingOptions(String storeCode, Locale locale);
}
