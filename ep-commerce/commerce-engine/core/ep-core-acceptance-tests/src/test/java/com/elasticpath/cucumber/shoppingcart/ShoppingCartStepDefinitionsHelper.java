/*
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.cucumber.shoppingcart;

import static java.lang.String.format;
import static java.util.Collections.singletonList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.common.collect.ImmutableMap;

import cucumber.api.DataTable;

import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.cucumber.ScenarioContextValueHolder;
import com.elasticpath.domain.builder.shopper.ShoppingContext;
import com.elasticpath.domain.builder.shopper.ShoppingContextBuilder;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.customer.PaymentToken;
import com.elasticpath.domain.misc.CheckoutResults;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingCartTaxSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.store.Store;
import com.elasticpath.persister.ShoppingContextPersister;
import com.elasticpath.sellingchannel.director.CartDirector;
import com.elasticpath.service.shipping.ShippingOptionResult;
import com.elasticpath.service.shipping.ShippingOptionService;
import com.elasticpath.service.shoppingcart.CheckoutService;
import com.elasticpath.service.shoppingcart.PricingSnapshotService;
import com.elasticpath.service.shoppingcart.TaxSnapshotService;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;
import com.elasticpath.test.persister.OrderTestPersister;
import com.elasticpath.test.persister.StoreTestPersister;
import com.elasticpath.test.persister.TestApplicationContext;
import com.elasticpath.test.persister.TestDataPersisterFactory;

/**
 * Help class for {@link ShoppingCartStepDefinitions}.
 */
public class ShoppingCartStepDefinitionsHelper {

	@Inject
	@Named("storeHolder")
	private ScenarioContextValueHolder<Store> storeHolder;
	
	@Inject
	@Named("shippingOptionHolder")
	private ScenarioContextValueHolder<ShippingOption> shippingOptionHolder;
	
	@Inject
	@Named("customerHolder")
	private ScenarioContextValueHolder<Customer> customerHolder;
	
	@Inject
	@Named("shoppingCartHolder")
	private ScenarioContextValueHolder<ShoppingCart> shoppingCartHolder;
	
	@Inject
	@Named("orderHolder")
	private ScenarioContextValueHolder<Order> orderHolder;
	
	@Autowired
	private TestApplicationContext tac;
	
	@Autowired
	private CartDirector cartDirector;
	
	@Autowired
	private ShippingOptionService shippingOptionService;
	
	@Autowired
	private CheckoutService checkoutService;

	@Autowired
	private ShoppingContextBuilder shoppingContextBuilder;

	@Autowired
	private ShoppingContextPersister shoppingContextPersister;

	@Autowired
	private PricingSnapshotService pricingSnapshotService;

	@Autowired
	private TaxSnapshotService taxSnapshotService;

	/**
	 * Retrieves the shopping cart of the test context.
	 *
	 * @return the shopping cart
	 */
	public ShoppingCart getShoppingCart() {
		if (shoppingCartHolder.get() == null) {
			shoppingCartHolder.set(getEmptyShoppingCart());
		}
		
		return shoppingCartHolder.get();
	}
	
	/**
	 * Retrieves a default shopping cart.
	 *
	 * @return a default shopping cart
	 */
	public ShoppingCart getEmptyShoppingCart() {
		final Customer customer = customerHolder.get();

		final TestDataPersisterFactory persisterFactory = tac.getPersistersFactory();
		final StoreTestPersister storeTestPersister = persisterFactory.getStoreTestPersister();
		final OrderTestPersister orderTestPersister = persisterFactory.getOrderTestPersister();

		final CustomerSession customerSession = storeTestPersister.persistCustomerSessionWithAssociatedEntities(customer);

		return orderTestPersister.persistEmptyShoppingCart(
				customer.getPreferredBillingAddress(), customer.getPreferredShippingAddress(), customerSession,
				shippingOptionHolder.get(),
				storeHolder.get());
	}

	/**
	 * Sets the shipping address of the shopping cart of the current test context.
	 *
	 * @param customerAddress the shipping address, may be null.
	 */
	public void setShippingAddress(final CustomerAddress customerAddress) {
		final ShoppingCart shoppingCart = getShoppingCart();

		shoppingCart.setShippingAddress(customerAddress);

		// If a shipping address has been specified and we don't have a billing address then set the billing address also
		if (customerAddress != null && shoppingCart.getBillingAddress() == null) {
			shoppingCart.setBillingAddress(customerAddress);
		}
	}
	
	/**
	 * Sets billing address of the shopping cart of the current test context.
	 *
	 * @param customerAddress the billing address, may be null.
	 */
	public void setBillingAddress(final CustomerAddress customerAddress) {
		final ShoppingCart shoppingCart = getShoppingCart();
		shoppingCart.setBillingAddress(customerAddress);
	}

	/**
	 * Sets delivery option of the shopping cart of the current test context. 
	 *
	 * @param deliveryOption the delivery option
	 */
	public void setDeliveryOption(final String deliveryOption) {
		
		final ShoppingCart shoppingCart = getShoppingCart();
		final ShippingOptionResult shippingOptionResult = shippingOptionService.getShippingOptions(shoppingCart);
		final String errorMessage = format("Unable to get available shipping options for the given cart with guid '%s'. "
						+ "So cannot set a selected one.",
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

		final List<ShippingOption> availableOptions = shippingOptionResult.getAvailableShippingOptions();
		if (availableOptions.isEmpty()) {
			return;
		}
		
		availableOptions.stream()
				.filter(option -> option.getCode().equals(deliveryOption))
				.findFirst()
				.ifPresent(shoppingCart::setSelectedShippingOption);
	}

	/**
	 *  Adds items to to the shopping cart of the current test context, and checks out an order from the shopping cart.
	 *
	 * @param itemDtos the shopping item dtos
	 */
	public void purchaseItems(final List<ShoppingItemDto> itemDtos) {		
		addItems(itemDtos);
		submitOrder();		
	}
	
	/**
	 * Adds items to the shopping cart of the current test context.
	 *
	 * @param itemDtos the shopping item dtos
	 */
	public void addItems(final List<ShoppingItemDto> itemDtos) {
		final ShoppingCart shoppingCart = getShoppingCart();
		addShoppingItems(shoppingCart, itemDtos);
	}
	
	/**
	 * Checks out an order from the shopping cart of the current test context.
	 */
	public void submitOrder() {
		final Customer customer = customerHolder.get();
		final ShoppingCart shoppingCart = getShoppingCart();
		
		final OrderPayment orderPayment = tac.getPersistersFactory().getOrderTestPersister().createOrderPayment(
			customer, (PaymentToken) customer.getPaymentMethods().getDefault());

		final ShoppingContext shoppingContext = shoppingContextBuilder.withCustomer(customer)
				.build();
		shoppingContextPersister.persist(shoppingContext);

		final ShoppingCartPricingSnapshot pricingSnapshot = pricingSnapshotService.getPricingSnapshotForCart(shoppingCart);
		final ShoppingCartTaxSnapshot taxSnapshot = taxSnapshotService.getTaxSnapshotForCart(shoppingCart, pricingSnapshot);

		final CheckoutResults checkoutResults = checkoutService.checkout(shoppingCart,
																		taxSnapshot,
																		shoppingContext.getCustomerSession(),
																		orderPayment,
																		true);
		orderHolder.set(checkoutResults.getOrder());
		
		shoppingCart.clearItems();
	}
	
	private List<ShoppingItem> addShoppingItems(final ShoppingCart shoppingCart, final List<ShoppingItemDto> itemDtos) {
		final List<ShoppingItem> lineItems = new ArrayList<>();

		for (ShoppingItemDto dto : itemDtos) {
			lineItems.add(cartDirector.addItemToCart(shoppingCart, dto));
		}

		return lineItems;
	}

	/**
	 * Convert a Gherkin data table into ShoppingItemDtos. We do this instead of taking a List<ShoppingItemDto> because
	 * Cucumber does some weirdness in which the constructor/static initializers are not executed.
	 * @param dataTable a data table representing the Shopping Item Dtos
	 * @return a list of ShoppingItemDto objects
	 */
	public List<ShoppingItemDto> convertDataTableToShoppingItemDtos(final DataTable dataTable) {
		List<ShoppingItemDto> shoppingItemDtoList = new ArrayList<>();
		for (Map<String, String> row : dataTable.asMaps(String.class, String.class)) {
			shoppingItemDtoList.add(new ShoppingItemDto(row.get("skuCode"), Integer.valueOf(row.get("quantity"))));
		}
		return shoppingItemDtoList;
	}
}
