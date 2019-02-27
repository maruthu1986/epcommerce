/*
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.service.cartorder.impl;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isEmpty;

import java.util.List;
import java.util.function.Supplier;

import org.apache.log4j.Logger;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.store.Store;
import com.elasticpath.service.cartorder.CartOrderShippingInformationSanitizer;
import com.elasticpath.service.customer.CustomerSessionService;
import com.elasticpath.service.customer.dao.CustomerAddressDao;
import com.elasticpath.service.shipping.ShippingOptionResult;
import com.elasticpath.service.shipping.ShippingOptionService;
import com.elasticpath.service.shoppingcart.ShoppingCartService;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;

/**
 * Implementation of {@link CartOrderShippingInformationSanitizer} that sets invalid GUIDs to null.
 */
public class ClearingCartOrderShippingInformationSanitizer implements CartOrderShippingInformationSanitizer {
	private static final Logger LOG = Logger.getLogger(ClearingCartOrderShippingInformationSanitizer.class);

	private ShippingOptionService shippingOptionService;
	private ShoppingCartService shoppingCartService;
	private CustomerSessionService customerSessionService;
	private StoreService storeService;
	private CustomerAddressDao customerAddressDao;

	private BeanFactory beanFactory;

	@Override
	public boolean sanitize(final CartOrder cartOrder) {
		final ShoppingCart shoppingCart = findShoppingCartByGuid(cartOrder);

		final String shippingAddressGuid = cartOrder.getShippingAddressGuid();
		final Address shippingAddress = getShippingAddress(shippingAddressGuid);
		shoppingCart.setShippingAddress(shippingAddress);

		return sanitize(cartOrder, shippingAddress, () -> getShippingOptionService().getShippingOptions(shoppingCart));
	}

	@Override
	public boolean sanitize(final CartOrder cartOrder, final Address shippingAddress, final Supplier<ShippingOptionResult> shippingOptionsSupplier) {
		if (shippingAddress == null) {
			cartOrder.setShippingAddressGuid(null);
			cartOrder.setShippingOptionCode(null);
			return true;
		}

		final String shippingOptionCode = cartOrder.getShippingOptionCode();
		if (shippingOptionCode != null && !isShippingOptionValid(shippingOptionsSupplier, shippingOptionCode)) {
			cartOrder.setShippingOptionCode(null);
			return true;
		}

		return false;
	}

	/**
	 * Finds the {@link ShoppingCart} matching the shopping cart guid stored on the given {@link CartOrder} object.
	 * It also ensures that returned {@link ShoppingCart} has a valid {@link CustomerSession} associated with it
	 * as this is required for retrieving valid shipping options which is requested by {@link #sanitize(CartOrder)}.
	 *
	 * @param cartOrder the {@link CartOrder} to retrieve the matching {@link ShoppingCart} for.
	 * @return the matching {@link ShoppingCart}, populated with a {@link CustomerSession} instance.
	 */
	protected ShoppingCart findShoppingCartByGuid(final CartOrder cartOrder) {
		final String shoppingCartGuid = cartOrder.getShoppingCartGuid();
		final ShoppingCart result = getShoppingCartService().findByGuid(shoppingCartGuid);

		if (result == null) {
			throw new EpSystemException("Unable to find Shopping Cart with guid: " + shoppingCartGuid);
		}

		// The ShoppingCart above does not have a CustomerSession associated with it which causes an error
		// when attempting to get valid shipping options so we need to populate it with a valid CustomerSession
		updateCustomerSessionOnShoppingCart(result);

		return result;
	}

	/**
	 * Sets the {@link ShoppingCart}'s {@link CustomerSession} reference if it currently doesn't have any {@link CustomerSession} reference.
	 *
	 * @param shoppingCart the shoppingCart to update if no {@link CustomerSession} is present
	 */
	protected void updateCustomerSessionOnShoppingCart(final ShoppingCart shoppingCart) {
		final Shopper shopper = shoppingCart.getShopper();

		// Ensure the Shopper's shopping cart reference is set as if not it causes NPEs
		if (shopper.getCurrentShoppingCart() == null) {
			shopper.setCurrentShoppingCart(shoppingCart);
		}

		if (shoppingCart.getCustomerSession() == null) {

			final String storeCode = shopper.getStoreCode();
			CustomerSession result = getCustomerSessionService().findByCustomerIdAndStoreCode(shopper.getCustomer().getUserId(), storeCode);

			if (result == null) {
				result = getCustomerSessionService().createWithShopper(shopper);

				final Store store = getStoreService().findStoreWithCode(storeCode);
				result.setLocale(store.getDefaultLocale());

				getCustomerSessionService().initializeCustomerSessionForPricing(result, storeCode, store.getDefaultCurrency());
			} else {
				shopper.updateTransientDataWith(result);
			}
		}
	}

	/**
	 * Returns whether the shipping option code given matches the code of one of the {@link ShippingOption} objects returned from the
	 * {@link ShippingOptionResult} supplied.
	 *
	 * @param shippingOptionsSupplier a {@link Supplier} of a {@link ShippingOptionResult} to validate against if successful.
	 * @param shippingOptionCode the code to look for.
	 * @return {@code true} if a match was found or the {@link ShippingOptionResult} wasn't successful; {@code false} otherwise.
	 */
	protected boolean isShippingOptionValid(final Supplier<ShippingOptionResult> shippingOptionsSupplier, final String shippingOptionCode) {
		if (isEmpty(shippingOptionCode)) {
			return false;
		}

		final ShippingOptionResult shippingOptionResult = shippingOptionsSupplier.get();

		// If we cannot get the shipping options then don't invalidate unnecessarily
		if (!shippingOptionResult.isSuccessful()) {
			shippingOptionResult.logError(LOG, "Unable to retrieve available shipping options to validate the currently selected shipping option, "
					+ "so temporarily skipping validation of it.");

			return true;
		}

		// Otherwise validate the selected shipping option against the list returned
		final List<ShippingOption> validShippingOptions = shippingOptionResult.getAvailableShippingOptions();

		return isNotEmpty(validShippingOptions)
				&& validShippingOptions.stream().anyMatch(shippingOption -> shippingOption.getCode().equals(shippingOptionCode));
	}

	/**
	 * Returns the address specified by the given GUID.
	 *
	 * @param shippingAddressGuid the GUID to look up.
	 * @return the matching {@link Address} object for the given address GUID.
	 */
	protected Address getShippingAddress(final String shippingAddressGuid) {
		if (isEmpty(shippingAddressGuid)) {
			return null;
		}
		return getCustomerAddressDao().findByGuid(shippingAddressGuid);
	}

	protected ShippingOptionService getShippingOptionService() {
		return this.shippingOptionService;
	}

	public void setShippingOptionService(final ShippingOptionService shippingOptionService) {
		this.shippingOptionService = shippingOptionService;
	}

	protected ShoppingCartService getShoppingCartService() {
		return this.shoppingCartService;
	}

	public void setShoppingCartService(final ShoppingCartService shoppingCartService) {
		this.shoppingCartService = shoppingCartService;
	}

	/**
	 * On demand getter for CustomerSessionService.
	 * To avoid a circular dependency which breaks Spring (This bean is included by CartOrderService,
	 * but CustomerSessionService depends (indirectly on CartOrderService)), we load this service on demand from the bean factory
	 *
	 * @return CustomerSessionService
	 */
	protected CustomerSessionService getCustomerSessionService() {
		if (this.customerSessionService == null) {
			this.customerSessionService = getBeanFactory().getBean(ContextIdNames.CUSTOMER_SESSION_SERVICE);
		}
		return this.customerSessionService;
	}

	protected StoreService getStoreService() {
		return this.storeService;
	}

	public void setStoreService(final StoreService storeService) {
		this.storeService = storeService;
	}

	protected CustomerAddressDao getCustomerAddressDao() {
		return customerAddressDao;
	}

	public void setCustomerAddressDao(final CustomerAddressDao customerAddressDao) {
		this.customerAddressDao = customerAddressDao;
	}

	protected BeanFactory getBeanFactory() {
		return this.beanFactory;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

}
