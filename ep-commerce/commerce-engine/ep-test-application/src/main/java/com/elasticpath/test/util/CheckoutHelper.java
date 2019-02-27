/*
 * Copyright (c) Elastic Path Software Inc., 2015
 */

package com.elasticpath.test.util;

import static com.elasticpath.commons.constants.ContextIdNames.SHIPPING_OPTION_SERVICE;
import static java.lang.String.format;
import static java.util.Collections.singletonList;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.constants.EpShippingContextIdNames;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.misc.CheckoutResults;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.shipping.Region;
import com.elasticpath.domain.shipping.ShippingRegion;
import com.elasticpath.domain.shipping.ShippingServiceLevel;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingCartTaxSnapshot;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.service.shipping.ShippingOptionResult;
import com.elasticpath.service.shipping.ShippingOptionService;
import com.elasticpath.service.shipping.ShippingServiceLevelService;
import com.elasticpath.service.shoppingcart.CheckoutService;
import com.elasticpath.service.shoppingcart.PricingSnapshotService;
import com.elasticpath.service.shoppingcart.TaxSnapshotService;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;
import com.elasticpath.test.persister.StoreTestPersister;
import com.elasticpath.test.persister.TestApplicationContext;

/**
 * A helper to with all util methods for checkout.
 */
public class CheckoutHelper {

	private final CheckoutService checkoutService;

	private final ShippingOptionService shippingOptionService;

	private final PricingSnapshotService pricingSnapshotService;

	private final TaxSnapshotService taxSnapshotService;

	private final StoreTestPersister storeTestPersister;

	private final TestApplicationContext tac;

	private final ShippingServiceLevelService shippingServiceLevelService;

	/**
	 * Constructor.
	 *
	 * @param tac {@link TestApplicationContext}.
	 */
	public CheckoutHelper(final TestApplicationContext tac) {
		this.tac = tac;
		checkoutService = tac.getBeanFactory().getBean(ContextIdNames.CHECKOUT_SERVICE);
		shippingOptionService = tac.getBeanFactory().getBean(SHIPPING_OPTION_SERVICE);
		pricingSnapshotService = tac.getBeanFactory().getBean(ContextIdNames.PRICING_SNAPSHOT_SERVICE);
		taxSnapshotService = tac.getBeanFactory().getBean(ContextIdNames.TAX_SNAPSHOT_SERVICE);
		storeTestPersister = tac.getPersistersFactory().getStoreTestPersister();
		shippingServiceLevelService = tac.getBeanFactory().getBean(EpShippingContextIdNames.SHIPPING_SERVICE_LEVEL_SERVICE);
	}

	/**
	 * Enrich a shopping cart with default payment info and shipping info.
	 *
	 * @param shoppingCart {@link ShoppingCart}.
	 */
	public void enrichShoppingCartForCheckout(final ShoppingCart shoppingCart) {
		final ShippingOption defaultShippingOption = getDefaultShippingOptionFromCart(shoppingCart);
		CustomerAddress address = createCustomerAddressMatchingShippingServiceLevel(defaultShippingOption);
		shoppingCart.setShippingAddress(address);
		shoppingCart.setSelectedShippingOption(defaultShippingOption);
		shoppingCart.setBillingAddress(address);
	}

	/**
	 * Checkout a shopping cart with default billing info, payment info, and shipping info.
	 *
	 * @param shoppingCart    {@link com.elasticpath.domain.shoppingcart.ShoppingCart}.
	 * @param customerSession {@link com.elasticpath.domain.customer.CustomerSession}
	 * @return {@link CheckoutResults}.
	 */
	public CheckoutResults checkoutWithDefaultInfo(final ShoppingCart shoppingCart, final CustomerSession customerSession) {
		enrichShoppingCartForCheckout(shoppingCart);

		final ShoppingCartPricingSnapshot pricingSnapshot = pricingSnapshotService.getPricingSnapshotForCart(shoppingCart);
		final ShoppingCartTaxSnapshot taxSnapshot = taxSnapshotService.getTaxSnapshotForCart(shoppingCart, pricingSnapshot);

		return checkoutService.checkout(shoppingCart, taxSnapshot, customerSession, defaultOrderPayment(), false);
	}

	/**
	 * Checkout a shopping cart with default billing info, payment info, and shipping info.
	 *
	 * @param customerSession {@link CustomerSession}.
	 * @return {@link CheckoutResults}.
	 */
	public CheckoutResults checkoutWithDefaultInfo(final CustomerSession customerSession) {
		return checkoutWithDefaultInfo(customerSession.getShopper().getCurrentShoppingCart(), customerSession);
	}

	/**
	 * Checkout a shopping cart with default payment info but override selected shipping option
	 *
	 * @param customerSession    {@link CustomerSession}.
	 * @param shippingOptionName service option name (not code) specified
	 * @return {@link CheckoutResults}.
	 */
	public CheckoutResults checkoutWithDefaultInfoOverrideShipping(final CustomerSession customerSession, final String shippingOptionName) {
		final ShoppingCart shoppingCart = customerSession.getShopper().getCurrentShoppingCart();
		List<ShippingOption> defaultShippingOptions = getShippingOptionsFromCart(shoppingCart);
		boolean found = false;
		for (ShippingOption defaultShippingOption : defaultShippingOptions) {
			if (StringUtils.equals(shippingOptionName, defaultShippingOption.getDisplayName(shoppingCart.getShopper().getLocale()).orElse(null))) {
				CustomerAddress address = createCustomerAddressMatchingShippingServiceLevel(defaultShippingOption);
				shoppingCart.setBillingAddress(address);
				shoppingCart.setShippingAddress(address);
				shoppingCart.setSelectedShippingOption(defaultShippingOption);
				found = true;
				break;
			}
		}
		if (!found) {
			throw new EpSystemException("Chosen ShippingOption not available in for store.");
		}

		final ShoppingCartPricingSnapshot pricingSnapshot = pricingSnapshotService.getPricingSnapshotForCart(shoppingCart);
		final ShoppingCartTaxSnapshot taxSnapshot = taxSnapshotService.getTaxSnapshotForCart(shoppingCart, pricingSnapshot);

		return checkoutService.checkout(shoppingCart, taxSnapshot, customerSession, defaultOrderPayment(), false);
	}

	private OrderPayment defaultOrderPayment() {
		OrderPayment defaultOrderPayment = tac.getBeanFactory().getBean(ContextIdNames.ORDER_PAYMENT);
		defaultOrderPayment.setPaymentMethod(PaymentType.PAYMENT_TOKEN);
		return defaultOrderPayment;
	}

	private List<ShippingOption> getShippingOptionsFromCart(final ShoppingCart storeShoppingCart) {
		final ShippingOptionResult result = shippingOptionService.getAllShippingOptions(
				storeShoppingCart.getStore().getCode(),
				storeShoppingCart.getShopper().getLocale());

		final String errorMessage = format("Unable to get available shipping options for the given cart with guid '%s'.",
				storeShoppingCart.getGuid());

		result.throwExceptionIfUnsuccessful(
				errorMessage,
				singletonList(
						new StructuredErrorMessage(
								"shippingoptions.unavailable",
								errorMessage,
								ImmutableMap.of(
										"cart-id", storeShoppingCart.getGuid())
						)
				));

		return result.getAvailableShippingOptions();
	}

	private ShippingOption getDefaultShippingOptionFromCart(final ShoppingCart storeShoppingCart) {
		return getShippingOptionsFromCart(storeShoppingCart).iterator().next();
	}

	private CustomerAddress defaultCustomerAddress() {
		return storeTestPersister.createCustomerAddress("Ou", "Owen", "street1", "street2", "Vancouver", "CA", "BC", "zip", "phone");
	}

	private CustomerAddress createCustomerAddressMatchingShippingServiceLevel(final ShippingOption shippingOption) {
		final List<ShippingServiceLevel> shippingServiceLevels = shippingServiceLevelService.findAll();

		final ShippingServiceLevel matchedShippingServiceLevel = shippingServiceLevels.stream().filter(shippingServiceLevel ->
				ObjectUtils.equals(shippingServiceLevel.getCode(), shippingOption.getCode()))
				.findFirst().orElse(null);
		return createCustomerAddressMatchingShippingServiceLevel(matchedShippingServiceLevel);
	}

	private CustomerAddress createCustomerAddressMatchingShippingServiceLevel(final ShippingServiceLevel shippingServiceLevel) {
		final ShippingRegion shippingRegion = shippingServiceLevel.getShippingRegion();
		final Map<String, Region> regionMap = shippingRegion.getRegionMap();
		final Collection<Region> regions = regionMap.values();
		final Region firstRegion = regions.iterator().next();
		final String countryCodeFromFirstRegion = firstRegion.getCountryCode();
		final String firstSubCountryFromFirstRegion = firstRegion.getSubCountryCodeList().get(0);
		final CustomerAddress customerAddressMatchingShippingServiceLevel = defaultCustomerAddress();
		customerAddressMatchingShippingServiceLevel.setCountry(countryCodeFromFirstRegion);
		customerAddressMatchingShippingServiceLevel.setSubCountry(firstSubCountryFromFirstRegion);
		return customerAddressMatchingShippingServiceLevel;
	}
}
