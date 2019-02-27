/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.test.integration.cartorder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Currency;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.builder.customer.CustomerBuilder;
import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.cartorder.impl.CartOrderImpl;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.customer.PaymentToken;
import com.elasticpath.domain.customer.impl.CustomerAddressImpl;
import com.elasticpath.domain.customer.impl.PaymentTokenImpl;
import com.elasticpath.domain.factory.TestCustomerSessionFactoryForTestApplication;
import com.elasticpath.domain.factory.TestShopperFactoryForTestApplication;
import com.elasticpath.domain.shipping.Region;
import com.elasticpath.domain.shipping.ShippingRegion;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.impl.ShoppingCartImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.persistence.dao.PaymentTokenDao;
import com.elasticpath.sellingchannel.director.CartDirector;
import com.elasticpath.service.cartorder.CartOrderService;
import com.elasticpath.service.cartorder.CartOrderShippingService;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.service.customer.dao.CustomerAddressDao;
import com.elasticpath.service.shopper.ShopperService;
import com.elasticpath.service.shoppingcart.ShoppingCartService;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;
import com.elasticpath.test.integration.BasicSpringContextTest;
import com.elasticpath.test.persister.CatalogTestPersister;
import com.elasticpath.test.persister.StoreTestPersister;
import com.elasticpath.test.persister.testscenarios.SimpleStoreScenario;
import com.elasticpath.test.util.Utils;

public class CartOrderShippingServiceImplTest  extends BasicSpringContextTest {

	private static String cartGuid;

	private static String cartOrderGuid;

	private static String billingAddressGuid;

	private static String shippingAddressGuid;

	private static String DEFAULT_COUNTRY_CODE;

	private static String DEFAULT_SUBCOUNTRY_CODE;

	private static String DEFAULT_SHIPPING_OPTION_CODE;

	private SimpleStoreScenario scenario;

	private Customer customer;

	@Autowired
	private BeanFactory beanFactory;

	@Autowired
	private CustomerAddressDao addressDao;

	@Autowired
	private CartOrderService cartOrderService;

	@Autowired
	private CartOrderShippingService cartOrderShippingService;

	@Autowired
	private ShoppingCartService shoppingCartService;

	@Autowired
	private CartDirector cartDirector;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private PersistenceEngine persistenceEngine;

	@Autowired
	private CustomerBuilder customerBuilder;

	@Autowired
	private PaymentTokenDao paymentTokenDao;

	@Autowired
	private CatalogTestPersister catalogTestPersister;

	private Store store;

	private ShoppingCart cart;

	private String customerGuid;

	/**
	 * A setup for the integration test.
	 */
	@Before
	public void initialize() {
		cartGuid = Utils.uniqueCode("CART");
		cartOrderGuid = Utils.uniqueCode("CARTORDER");
		billingAddressGuid = Utils.uniqueCode("BAGUID");
		shippingAddressGuid = Utils.uniqueCode("SAGUID");
		customerGuid = Utils.uniqueCode("CUSTOMER");
		scenario = getTac().useScenario(SimpleStoreScenario.class);

		ShippingRegion defaultShippingRegion = scenario.getShippingRegion();
		Map<String, Region> regionMap = defaultShippingRegion.getRegionMap();
		Region region = regionMap.values().iterator().next();
		DEFAULT_COUNTRY_CODE = region.getCountryCode();
		DEFAULT_SUBCOUNTRY_CODE = region.getSubCountryCodeList().get(0);
		DEFAULT_SHIPPING_OPTION_CODE = scenario.getShippingOption().getCode();

		store = scenario.getStore();

		createAndPersistAddressWithGuid(billingAddressGuid);
		createAndPersistAddressWithGuid(shippingAddressGuid);
		cart = configureAndPersistCart();
	}

	/**
	 * Test populating transient fields on shopping cart.
	 */
	@Test
	public void testPopulatingTransientFieldsOnShoppingCart() {
		createAndSaveCartOrder();
		CartOrder cartOrder = cartOrderService.findByStoreCodeAndGuid(store.getCode(), cartOrderGuid);
		ShoppingCart populatedShoppingCart = cartOrderShippingService.populateAddressAndShippingFields(cart, cartOrder);
		ShippingOption shippingOption = scenario.getShippingOption(cart.getShopper().getLocale());
		final Optional<ShippingOption> selectedShippingOption = populatedShoppingCart.getSelectedShippingOption();
		assertTrue(selectedShippingOption.isPresent());
		assertEquals(shippingOption, selectedShippingOption.get());
		assertEquals(cartOrder.getBillingAddressGuid(), populatedShoppingCart.getBillingAddress().getGuid());
	}

	/**
	 * Test populating transient fields on shopping cart with null shipping address.
	 */
	@Test
	public void testPopulatingTransientFieldsOnShoppingCartWithNullShippingAddress() {
		createAndSaveCartOrder();
		CartOrder cartOrder = cartOrderService.findByStoreCodeAndGuid(store.getCode(), cartOrderGuid);
		cartOrder.setShippingAddressGuid("non-existent");
		cartOrder = cartOrderService.saveOrUpdate(cartOrder);

		ShoppingCart populatedShoppingCart = cartOrderShippingService.populateAddressAndShippingFields(cart, cartOrder);
		assertEquals("Shopping cart selected shipping option should be empty.", Optional.empty(), populatedShoppingCart.getSelectedShippingOption());
		assertEquals(cartOrder.getBillingAddressGuid(), populatedShoppingCart.getBillingAddress().getGuid());
	}

	/**
	 * Test populating transient fields on shopping cart with non-existent shipping option.
	 */
	@Test
	public void testPopulatingTransientFieldsOnShoppingCartWithNonExistentShippingOption() {
		createAndSaveCartOrder();
		CartOrder cartOrder = cartOrderService.findByStoreCodeAndGuid(store.getCode(), cartOrderGuid);
		cartOrder.setShippingOptionCode("non-existent");
		cartOrder = cartOrderService.saveOrUpdate(cartOrder);

		ShoppingCart populatedShoppingCart = cartOrderShippingService.populateShoppingCartTransientFields(cart, cartOrder);
		assertEquals(cartOrder.getBillingAddressGuid(), populatedShoppingCart.getBillingAddress().getGuid());
	}

	private CartOrder createAndSaveCartOrder() {

		CartOrder cartOrder = new CartOrderImpl();
		cartOrder.setGuid(cartOrderGuid);
		cartOrder.setBillingAddressGuid(billingAddressGuid);
		cartOrder.setShippingAddressGuid(shippingAddressGuid);
		cartOrder.setShippingOptionCode(DEFAULT_SHIPPING_OPTION_CODE);
		cartOrder.setShoppingCartGuid(cartGuid);

		cartOrder.usePaymentMethod(getPersistedToken());

		cartOrder = cartOrderService.saveOrUpdate(cartOrder);

		assertTrue(cartOrder.isPersisted());
		return cartOrder;
	}

	private PaymentToken getPersistedToken() {

		PaymentToken token = new PaymentTokenImpl.TokenBuilder().withDisplayValue("**** **** **** 1234").withGatewayGuid("1234")
				.withValue("token-value").build();

		Customer customer = customerBuilder.withStoreCode(store.getCode()).withGuid(customerGuid).withPaymentMethods(token).build();

		customer = customerService.add(customer);

		token = (PaymentToken) customer.getPaymentMethods().all().iterator().next();
		return token;
}
	private Address createAndPersistAddressWithGuid(final String guid) {
		Address address = new CustomerAddressImpl();
		address.setGuid(guid);
		address.setCountry(DEFAULT_COUNTRY_CODE);
		address.setSubCountry(DEFAULT_SUBCOUNTRY_CODE);
		return addressDao.saveOrUpdate(address);
	}

	private ShoppingCart configureAndPersistCart() {
		ShopperService shopperService = getBean(ContextIdNames.SHOPPER_SERVICE);
		Shopper shopper = TestShopperFactoryForTestApplication.getInstance().createNewShopperWithMemento();
		StoreTestPersister storeTestPersister = getTac().getPersistersFactory().getStoreTestPersister();
		customer = storeTestPersister.createDefaultCustomer(store);
		shopper.setCustomer(customer);
		shopper.setStoreCode(store.getCode());
		shopper = shopperService.save(shopper);

		// TODO: Handrolling customer session is probably not a good idea
		final CustomerSession customerSession = TestCustomerSessionFactoryForTestApplication.getInstance().createNewCustomerSessionWithContext(shopper);
		customerSession.setCurrency(Currency.getInstance("USD"));
		final ShoppingCartImpl shoppingCart = getBean(ContextIdNames.SHOPPING_CART);
		shoppingCart.setShopper(shopper);
		shoppingCart.setStore(store);
		shoppingCart.getShoppingCartMemento().setGuid(cartGuid);
		shoppingCart.setCustomerSession(customerSession);
		shopper.setCurrentShoppingCart(shoppingCart);
		return shoppingCartService.saveOrUpdate(shoppingCart);
	}

	private <T> T getBean(final String name) {
		return getBeanFactory().getBean(name);
	}

}
