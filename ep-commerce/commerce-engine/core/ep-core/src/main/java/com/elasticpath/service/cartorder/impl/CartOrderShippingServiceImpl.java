/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.service.cartorder.impl;

import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isEmpty;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.cartorder.CartOrderShippingInformationSanitizer;
import com.elasticpath.service.cartorder.CartOrderShippingService;
import com.elasticpath.service.customer.dao.CustomerAddressDao;
import com.elasticpath.service.shipping.ShippingOptionResult;
import com.elasticpath.service.shipping.ShippingOptionService;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;

/**
 * This Class can perform services for CartOrders related to shipping.
 */
public class CartOrderShippingServiceImpl implements CartOrderShippingService {
	private ShippingOptionService shippingOptionService;

	private CartOrderShippingInformationSanitizer cartOrderShippingInformationSanitizer;

	private CustomerAddressDao customerAddressDao;

	@Override
	public Boolean updateCartOrderShippingAddress(final String shippingAddressGuid, final ShoppingCart shoppingCart, final CartOrder cartOrder) {
		if (!isUpdateNeeded(shippingAddressGuid, cartOrder)) {
			return false;
		}

		cartOrder.setShippingAddressGuid(shippingAddressGuid);

		// The address may come directly from the customer profile and so not yet be set on the ShoppingCart
		// so make sure it's set correctly as otherwise the shipping option request below won't calculate the correct shipping options
		final Address shippingAddress = findAddress(shippingAddressGuid);
		shoppingCart.setShippingAddress(shippingAddress);

		final ShippingOptionResult shippingOptionResult = getAvailableShippingOptions(shoppingCart);

		cartOrderShippingInformationSanitizer.sanitize(cartOrder, shippingAddress, () -> shippingOptionResult);

		if (shippingOptionResult.isSuccessful()) {
			updateCartOrderShippingOptionCode(cartOrder, shippingOptionResult.getAvailableShippingOptions());
		}

		return true;
	}

	/**
	 * Returns the available shipping options for the given cart by calling
	 * {@link ShippingOptionService#getShippingOptions(ShoppingCart)}.
	 *
	 * @param shoppingCart the shopping cart to retrieve the options for.
	 * @return the {@link ShippingOptionResult} which may or may not have been successful (see {@link ShippingOptionResult#isSuccessful()}.
	 */
	protected ShippingOptionResult getAvailableShippingOptions(final ShoppingCart shoppingCart) {
		return getShippingOptionService().getShippingOptions(shoppingCart);
	}

	/**
	 * Updates cart order shipping option code using given shipping options.
	 *
	 * @param cartOrder       the cart order
	 * @param shippingOptions the shipping options
	 */
	protected void updateCartOrderShippingOptionCode(final CartOrder cartOrder, final List<ShippingOption> shippingOptions) {
		if (isNotEmpty(shippingOptions)) {
			final String existingShippingOption = cartOrder.getShippingOptionCode();
			final Optional<ShippingOption> matchingShippingOption = getMatchingShippingOption(shippingOptions, existingShippingOption);

			if (!matchingShippingOption.isPresent()) {
				final Optional<ShippingOption> defaultShippingOption = shippingOptionService.getDefaultShippingOption(shippingOptions);

				final String defaultShippingOptionCode = defaultShippingOption.map(ShippingOption::getCode).orElse(null);

				cartOrder.setShippingOptionCode(defaultShippingOptionCode);
			}
		}
	}

	/**
	 * Determine if an address update is necessary. We only need to update if the shipping address guid is different to what is currently stored.
	 *
	 * @param shippingAddressGuid the new shipping address guid
	 * @param cartOrder the cart order
	 * @return {@code true} if an update is needed
	 */
	protected boolean isUpdateNeeded(final String shippingAddressGuid, final CartOrder cartOrder) {
		return !Objects.equals(cartOrder.getShippingAddressGuid(), shippingAddressGuid);
	}

	@Override
	public ShoppingCart populateShoppingCartTransientFields(final ShoppingCart shoppingCart, final CartOrder cartOrder) {

		return populateAddressAndShippingFields(shoppingCart, cartOrder);
	}

	@Override
	public ShoppingCart populateAddressAndShippingFields(final ShoppingCart shoppingCart, final CartOrder cartOrder) {
		final Address billingAddress = findAddress(cartOrder.getBillingAddressGuid());
		shoppingCart.setBillingAddress(billingAddress);

		final Address shippingAddress = findAddress(cartOrder.getShippingAddressGuid());
		shoppingCart.setShippingAddress(shippingAddress);

		populateShippingOptionFields(shoppingCart, cartOrder);

		return shoppingCart;
	}

	/**
	 * Populates the selected shipping option field on the Shopping Cart if there is one set on the {@link CartOrder} and it is still valid.
	 *
	 * @param shoppingCart the shopping cart to update.
	 * @param cartOrder the cart order to read from.
	 */
	protected void populateShippingOptionFields(final ShoppingCart shoppingCart, final CartOrder cartOrder) {
		final String shippingOptionCodeFromCartOrder = cartOrder.getShippingOptionCode();

		if (shippingOptionCodeFromCartOrder != null) {
			final ShippingOptionResult shippingOptionResult = getAvailableShippingOptions(shoppingCart);

			if (shippingOptionResult.isSuccessful()) {
				getMatchingShippingOption(shippingOptionResult.getAvailableShippingOptions(), shippingOptionCodeFromCartOrder)
						.ifPresent(shoppingCart::setSelectedShippingOption);
			} else {
				handleNoShippingOptionsAvailableForShoppingCartPopulation(shippingOptionResult, shoppingCart);
			}
		}
	}

	/**
	 * Handles the scenario when no shipping options are available from {@link ShippingOptionService} when called by
	 * {@link #populateShippingOptionFields(ShoppingCart, CartOrder)}.
	 * <p>
	 * By default this method will throw an exception since we cannot populate the {@link ShoppingCart} without retrieving the available
	 * shipping options.
	 *
	 * @param shippingOptionResult the unsuccessful {@link ShippingOptionResult} returned from {@link ShippingOptionService}.
	 * @param shoppingCart         shopping cart
	 */
	protected void handleNoShippingOptionsAvailableForShoppingCartPopulation(final ShippingOptionResult shippingOptionResult,
																			 final ShoppingCart shoppingCart) {
		final String shoppingCartGuid = shoppingCart.getGuid();
		final ShippingOption shippingOptionSelected = shoppingCart.getSelectedShippingOption().orElse(null);
		requireNonNull(shippingOptionSelected, "Shipping option should already been selected.");

		final String errorMessage = format("Unable to get available shipping options for the given cart with guid '%s'. "
						+ "And there is a shipping option selected so we cannot continue.",
				shoppingCartGuid);
		shippingOptionResult.throwException(
				errorMessage,
				singletonList(
						new StructuredErrorMessage(
								"shippingoptions.unavailable",
								errorMessage,
								ImmutableMap.of(
										"cart-id", shoppingCartGuid,
										"shipping-option", shippingOptionSelected.getCode()))));
	}

	/**
	 * Returns matching shipping option by code from the list of shipping options.
	 *
	 * @param shippingOptions    list shipping options
	 * @param shippingOptionCode shipping option code
	 * @return matching shipping option, or {@link Optional#empty()} if none matched.
	 */
	protected Optional<ShippingOption> getMatchingShippingOption(final List<ShippingOption> shippingOptions, final String shippingOptionCode) {
		if (isEmpty(shippingOptionCode) || isEmpty(shippingOptions)) {
			return Optional.empty();
		}

		return shippingOptions.stream()
				.filter(shippingOption -> shippingOption.getCode().equals(shippingOptionCode))
				.findFirst();
	}

	/**
	 * Returns the address specified by the given GUID.
	 *
	 * @param addressGuid the GUID to look up.
	 * @return the matching {@link Address} object for the given address GUID.
	 */
	protected Address findAddress(final String addressGuid) {
		if (isEmpty(addressGuid)) {
			return null;
		}
		return getCustomerAddressDao().findByGuid(addressGuid);
	}

	protected ShippingOptionService getShippingOptionService() {
		return this.shippingOptionService;
	}

	public void setShippingOptionService(final ShippingOptionService shippingOptionService) {
		this.shippingOptionService = shippingOptionService;
	}

	protected CartOrderShippingInformationSanitizer getCartOrderShippingInformationSanitizer() {
		return cartOrderShippingInformationSanitizer;
	}

	public void setCartOrderShippingInformationSanitizer(final CartOrderShippingInformationSanitizer cartOrderShippingInformationSanitizer) {
		this.cartOrderShippingInformationSanitizer = cartOrderShippingInformationSanitizer;
	}

	protected CustomerAddressDao getCustomerAddressDao() {
		return customerAddressDao;
	}

	public void setCustomerAddressDao(final CustomerAddressDao customerAddressDao) {
		this.customerAddressDao = customerAddressDao;
	}

}
