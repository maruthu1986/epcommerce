/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */

package com.elasticpath.test.persister;

import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.customer.PaymentToken;
import com.elasticpath.domain.customer.impl.PaymentTokenImpl;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingCartTaxSnapshot;
import com.elasticpath.domain.store.Store;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.service.OrderConfigurationService;
import com.elasticpath.service.shoppingcart.CheckoutService;
import com.elasticpath.service.shoppingcart.PricingSnapshotService;
import com.elasticpath.service.shoppingcart.ShoppingCartService;
import com.elasticpath.service.shoppingcart.TaxSnapshotService;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;

/**
 * Persister allows to create and save into database catalog dependent domain objects.
 */
public class OrderTestPersister {

	private final BeanFactory beanFactory;

	private final ShoppingCartService shoppingCartService;

	private final PricingSnapshotService pricingSnapshotService;

	private final TaxSnapshotService taxSnapshotService;

	private final TestDataPersisterFactory persisterFactory;

	public OrderTestPersister(final BeanFactory beanFactory, final TestDataPersisterFactory persisterFactory) {
		this.beanFactory = beanFactory;
		shoppingCartService = beanFactory.getBean(ContextIdNames.SHOPPING_CART_SERVICE);
		this.persisterFactory = persisterFactory;
		pricingSnapshotService = beanFactory.getBean(ContextIdNames.PRICING_SNAPSHOT_SERVICE);
		taxSnapshotService = beanFactory.getBean(ContextIdNames.TAX_SNAPSHOT_SERVICE);
	}

	/**
	 * Create an empty shopping cart in the DB.
	 *
	 * @param billingAddress  the billing address
	 * @param shippingAddress the shipping address
	 * @param customerSession the customer session the cart belongs to
	 * @param shippingOption  the shipping option
	 * @param store           the store the cart is for
	 * @return the persisted shopping cart
	 */
	public ShoppingCart persistEmptyShoppingCart(final Address billingAddress, final Address shippingAddress,
			final CustomerSession customerSession, final ShippingOption shippingOption, final Store store) {
		customerSession.setCurrency(Currency.getInstance(Locale.US));

		final ShoppingCart shoppingCart = beanFactory.getBean(ContextIdNames.SHOPPING_CART);
		shoppingCart.initialize();
		shoppingCart.setBillingAddress(billingAddress);
		shoppingCart.setShippingAddress(shippingAddress);
		shoppingCart.setCustomerSession(customerSession);
		shoppingCart.setStore(store);
		if (shippingAddress != null) {
			shoppingCart.setSelectedShippingOption(shippingOption);
		}
		return shoppingCartService.saveOrUpdate(shoppingCart);
	}

	/**
	 * Create an order payment object.
	 *
	 * @param customer     the customer whose payment this is
	 * @param creditCard the credit card to use for payment
	 * @return an order payment object
	 */
	public OrderPayment createOrderPayment(final Customer customer, final PaymentToken paymentToken) {
		OrderPayment orderPayment = beanFactory.getBean(ContextIdNames.ORDER_PAYMENT);
		orderPayment.setCreatedDate(new Date());
		orderPayment.setCurrencyCode("USD");
		orderPayment.setEmail(customer.getEmail());
		orderPayment.setDisplayValue(paymentToken.getDisplayValue());
		orderPayment.setPaymentMethod(PaymentType.PAYMENT_TOKEN);
		return orderPayment;
	}

	/**
	 * Create an order in the given store with the given skus.
	 *
	 * @param store the store that the order is for
	 * @param skus  the list of skus to include in the order
	 * @return the order
	 */
	public Order createOrderWithSkus(final Store store, final ProductSku... skus) {
		return createOrderWithSkusQuantity(store, 1, skus);
	}

	/**
	 * Creates an Order containing the given quantity of each of the given Skus.
	 *
	 * @return the created Order
	 */
	public Order createOrderWithSkusQuantity(final Store store, final int quantity, final ProductSku... skus) {
		final Customer customer = persisterFactory.getStoreTestPersister().createDefaultCustomer(store);
		return createOrderForCustomerWithSkusQuantity(customer, store, quantity, skus);
	}

	/**
	 * Creates the Order containing the given quantity of each of the given Skus.
	 *
	 * @param customer the customer
	 * @param store    the store
	 * @param quantity the quantity
	 * @param skus     the skus
	 * @return the order
	 */
	public Order createOrderForCustomerWithSkusQuantity(final Customer customer, final Store store, final int quantity, final ProductSku... skus) {
		final CustomerSession customerSession = persisterFactory.getStoreTestPersister().persistCustomerSessionWithAssociatedEntities(customer);
		final Shopper shopper = customerSession.getShopper();

		final Map<ProductSku, Integer> skuQuantityMap = new HashMap<>();
		for (ProductSku sku : skus) {
			skuQuantityMap.put(sku, quantity);
		}
		ShoppingCart shoppingCart = getOrderConfigurationService().createShoppingCart(store, customer, skuQuantityMap);
		shoppingCart.setShippingAddress(customer.getAddresses().get(0));

		final ShippingOption shippingOption = persisterFactory.getStoreTestPersister().persistDefaultShippingOption(store);

		getOrderConfigurationService().selectCustomerAddressesToShoppingCart(
				shopper, customer.getAddresses().get(0).getStreet1(), customer.getAddresses().get(0).getStreet1());
		shoppingCart = getOrderConfigurationService().selectShippingOption(
				shoppingCart,
				shippingOption.getDisplayName(shopper.getLocale()).orElse(null));
		final ShoppingCartPricingSnapshot pricingSnapshot = pricingSnapshotService.getPricingSnapshotForCart(shoppingCart);
		final ShoppingCartTaxSnapshot taxSnapshot = taxSnapshotService.getTaxSnapshotForCart(shoppingCart, pricingSnapshot);

		final boolean throwExceptions = false;
		getCheckoutService().checkout(shoppingCart, taxSnapshot, customerSession, createOrderPayment(customer, new PaymentTokenImpl.TokenBuilder()
				.build()), throwExceptions);

		// only one order should have been created by the checkout service
		return shoppingCart.getCompletedOrder();
	}

	OrderConfigurationService getOrderConfigurationService() {
		return beanFactory.getBean("orderConfigurationService");
	}

	protected CheckoutService getCheckoutService() {
		return beanFactory.getBean(ContextIdNames.CHECKOUT_SERVICE);
	}
}
