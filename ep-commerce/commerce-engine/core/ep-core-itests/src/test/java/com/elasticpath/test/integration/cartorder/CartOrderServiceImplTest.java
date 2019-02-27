/*
 * Copyright (c) Elastic Path Software Inc., 2011.
 */

package com.elasticpath.test.integration.cartorder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.builder.customer.CustomerBuilder;
import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.cartorder.impl.CartOrderImpl;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.customer.PaymentToken;
import com.elasticpath.domain.customer.impl.AbstractPaymentMethodImpl;
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
import com.elasticpath.plugin.payment.dto.PaymentMethod;
import com.elasticpath.sellingchannel.director.CartDirector;
import com.elasticpath.service.cartorder.CartOrderService;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.service.customer.dao.CustomerAddressDao;
import com.elasticpath.service.shopper.ShopperService;
import com.elasticpath.service.shoppingcart.ShoppingCartService;
import com.elasticpath.test.integration.BasicSpringContextTest;
import com.elasticpath.test.persister.CatalogTestPersister;
import com.elasticpath.test.persister.StoreTestPersister;
import com.elasticpath.test.persister.testscenarios.SimpleStoreScenario;
import com.elasticpath.test.util.Utils;

/**
 * Cart order service integration test.
 */
public class CartOrderServiceImplTest extends BasicSpringContextTest {

	private static final String INVALID_SHIPPING_OPTION_CODE = "INVALID_SHIPPING_OPTION_CODE";

	private static final String INVALID_ADDRESS_GUID = "INVALID_ADDRESS_GUID";

	private static final String INVALID_CARTORDER_GUID = "an invalid guid";

	private static String cartGuid;

	private static String cartOrderGuid;

	private static String billingAddressGuid;

	private static String shippingAddressGuid;

	private static String DEFAULT_COUNTRY_CODE;

	private static String DEFAULT_SUBCOUNTRY_CODE;

	private static String DEFAULT_SHIPPING_OPTION_CODE;


	@Autowired
	private BeanFactory beanFactory;

	@Autowired
	private CustomerAddressDao addressDao;

	@Autowired
	private CartOrderService cartOrderService;

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

	private Customer customer;

	private ProductSku shippableProductSku;
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
		SimpleStoreScenario scenario = getTac().useScenario(SimpleStoreScenario.class);

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
		shippableProductSku = catalogTestPersister.createDefaultProductWithSkuAndInventory(
				scenario.getCatalog(), scenario.getCategory(), scenario.getWarehouse()).getDefaultSku();

		customer.setFirstName("John");
		customer.setLastName("Doe");
	}

	/**
	 * Ensure valid address and shipping option are not sanitized.
	 */
	@Test
	public void ensureValidAddressAndShippingOptionAreNotSanitized() {
		shouldHavePhysicalGoodInCart();
		CartOrder cartOrder = shouldCreateAndPersistCardOrderWithAddressAndShippingOptionCode(shippingAddressGuid, DEFAULT_SHIPPING_OPTION_CODE);

		CartOrder sanitizedCartOrder = cartOrderService.findByStoreCodeAndGuid(store.getCode(), cartOrder.getGuid());

		assertEquals("Address guid is incorrect.", shippingAddressGuid, sanitizedCartOrder.getShippingAddressGuid());
		assertEquals("Shipping option code is incorrect.", DEFAULT_SHIPPING_OPTION_CODE, sanitizedCartOrder.getShippingOptionCode());
	}

	/**
	 * Ensure correct sanitation for null shipping info.
	 */
	@Test
	public void ensureCorrectSanitationForNullShippingInfo() {
		shouldHavePhysicalGoodInCart();
		CartOrder cartOrder = shouldCreateAndPersistCardOrderWithAddressAndShippingOptionCode(null, null);

		CartOrder sanitizedCartOrder = cartOrderService.findByStoreCodeAndGuid(store.getCode(), cartOrder.getGuid());

		assertEquals("Address guid should be null.", null, sanitizedCartOrder.getShippingAddressGuid());
		assertEquals("Shipping option code should be null.", null, sanitizedCartOrder.getShippingOptionCode());
	}

	/**
	 * Ensure correct sanitation for invalid shipping info.
	 */
	@Test
	public void ensureCorrectSanitationForInvalidShippingInfo() {
		shouldHavePhysicalGoodInCart();
		CartOrder cartOrder = shouldCreateAndPersistCardOrderWithAddressAndShippingOptionCode(INVALID_ADDRESS_GUID, INVALID_SHIPPING_OPTION_CODE);

		CartOrder sanitizedCartOrder = cartOrderService.findByStoreCodeAndGuid(store.getCode(), cartOrder.getGuid());

		assertEquals("Address guid should be null.", null, sanitizedCartOrder.getShippingAddressGuid());
		assertEquals("Shipping option code should be null.", null, sanitizedCartOrder.getShippingOptionCode());
	}

	/**
	 * Ensure correct sanitation for invalid address.
	 */
	@Test
	public void ensureCorrectSanitationForInvalidAddress() {
		shouldHavePhysicalGoodInCart();
		CartOrder cartOrder = shouldCreateAndPersistCardOrderWithAddressAndShippingOptionCode(INVALID_ADDRESS_GUID, DEFAULT_SHIPPING_OPTION_CODE);

		CartOrder sanitizedCartOrder = cartOrderService.findByStoreCodeAndGuid(store.getCode(), cartOrder.getGuid());

		assertEquals("Address guid should be null.", null, sanitizedCartOrder.getShippingAddressGuid());
		assertEquals("shipping option code guid should be null.", null, sanitizedCartOrder.getShippingOptionCode());
	}

	/**
	 * Ensure correct sanitation for invalid shipping option.
	 */
	@Test
	public void ensureCorrectSanitationForInvalidShippingOption() {
		shouldHavePhysicalGoodInCart();
		CartOrder cartOrder = shouldCreateAndPersistCardOrderWithAddressAndShippingOptionCode(shippingAddressGuid, INVALID_SHIPPING_OPTION_CODE);

		CartOrder sanitizedCartOrder = cartOrderService.findByStoreCodeAndGuid(store.getCode(), cartOrder.getGuid());

		assertEquals("Address guid is incorrect.", shippingAddressGuid, sanitizedCartOrder.getShippingAddressGuid());
		assertEquals("Shipping option code should be null.", null, sanitizedCartOrder.getShippingOptionCode());
	}

	/**
	 * Tests that all fields of cart order are persisted.
	 */
	@Test
	public void testPersistenceOfCartOrderFields() {
		CartOrder cartOrder = beanFactory.getBean(ContextIdNames.CART_ORDER);
		cartOrder.setBillingAddressGuid(billingAddressGuid);
		cartOrder.setShippingAddressGuid(shippingAddressGuid);
		cartOrder.setShippingOptionCode(DEFAULT_SHIPPING_OPTION_CODE);
		cartOrder.setShoppingCartGuid(cartGuid);

		PaymentToken token = getPersistedToken();
		cartOrder.usePaymentMethod(token);

		cartOrderService.saveOrUpdate(cartOrder);

		CartOrder retrievedCartOrder = cartOrderService.findByStoreCodeAndGuid(store.getCode(), cartOrder.getGuid());

		assertEquals("Customer billing address guid was not persisted", billingAddressGuid, retrievedCartOrder.getBillingAddressGuid());
		assertEquals("Customer shipping address guid was not persisted", shippingAddressGuid, retrievedCartOrder.getShippingAddressGuid());
		assertEquals("Shipping shipping option code was not persisted", DEFAULT_SHIPPING_OPTION_CODE, retrievedCartOrder.getShippingOptionCode());
		assertEquals("Shopping cart guid was not persisted", cartGuid, retrievedCartOrder.getShoppingCartGuid());
		PaymentToken persistedPaymentToken = (PaymentToken) retrievedCartOrder.getPaymentMethod();
		assertEquals("Payment method value was not persisted", token.getValue(), persistedPaymentToken.getValue());
		assertEquals("Payment method display value was not persisted", token.getDisplayValue(), persistedPaymentToken.getDisplayValue());

		assertEquals("The persisted cart order was not identical to expected cart order", cartOrder, retrievedCartOrder);
	}

	/**
	 * Ensure successful persistence of new payment method on new cart order.
	 */
	@Test
	public void ensureSuccessfulPersistenceOfNewPaymentMethodOnNewCartOrder() {
		CartOrder cartOrder = beanFactory.getBean(ContextIdNames.CART_ORDER);
		cartOrder.setShoppingCartGuid(cartGuid);
		cartOrder.setShippingAddressGuid(shippingAddressGuid);
		PaymentToken token = new PaymentTokenImpl.TokenBuilder().withDisplayValue("displayValue").withValue("value").build();
		cartOrder.usePaymentMethod(token);
		cartOrderService.saveOrUpdate(cartOrder);

		CartOrder retrievedCartOrder = cartOrderService.findByStoreCodeAndGuid(store.getCode(), cartOrder.getGuid());
		PaymentToken persistedPaymentToken = (PaymentToken) retrievedCartOrder.getPaymentMethod();
		assertEquals("Payment method value was not persisted", token.getValue(), persistedPaymentToken.getValue());
		assertEquals("Payment method display value was not persisted", token.getDisplayValue(), persistedPaymentToken.getDisplayValue());
	}

	/**
	 * Ensure successful persistence of new payment method on existing cart order.
	 */
	@Test
	public void ensureSuccessfulPersistenceOfNewPaymentMethodOnExistingCartOrder() {
		CartOrder cartOrder = beanFactory.getBean(ContextIdNames.CART_ORDER);
		cartOrder.setShoppingCartGuid(cartGuid);
		cartOrder.setShippingAddressGuid(shippingAddressGuid);
		cartOrderService.saveOrUpdate(cartOrder);

		CartOrder retrievedCartOrder = cartOrderService.findByStoreCodeAndGuid(store.getCode(), cartOrder.getGuid());
		PaymentToken token = new PaymentTokenImpl.TokenBuilder().withDisplayValue("displayValue").withValue("value").build();
		retrievedCartOrder.usePaymentMethod(token);
		cartOrderService.saveOrUpdate(retrievedCartOrder);

		CartOrder updatedCartOrder = cartOrderService.findByStoreCodeAndGuid(store.getCode(), cartOrder.getGuid());
		PaymentToken retrievedPaymentToken = (PaymentToken) updatedCartOrder.getPaymentMethod();
		assertEquals("Retrieved payment token display value does not match", token.getDisplayValue(), retrievedPaymentToken.getDisplayValue());
		assertEquals("Retrieved payment token value does not match", token.getValue(), retrievedPaymentToken.getValue());
	}

	/**
	 * Ensure deletion of cart order does not delete customer payment method.
	 */
	@Test
	public void ensureDeletionOfCartOrderDoesNotDeleteCustomerPaymentMethod() {
		CartOrder cartOrder = beanFactory.getBean(ContextIdNames.CART_ORDER);
		cartOrder.setShoppingCartGuid(cartGuid);
		cartOrder = cartOrderService.saveOrUpdate(cartOrder);

		PaymentMethod paymentMethod = new PaymentTokenImpl.TokenBuilder().withDisplayValue("displayValue").withValue("value").build();
		customer.getPaymentMethods().setDefault(paymentMethod);
		customer = customerService.update(customer);
		PaymentMethod persistedPaymentMethod = customer.getPaymentMethods().getDefault();

		cartOrder.usePaymentMethod(persistedPaymentMethod);
		cartOrder = cartOrderService.saveOrUpdate(cartOrder);

		cartOrderService.remove(cartOrder);
		Customer updatedCustomer = customerService.findByGuid(customer.getGuid());
		assertEquals("Payment method was not retained on Customer after CartOrder deletion.", persistedPaymentMethod,
				updatedCustomer.getPaymentMethods().getDefault());
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void ensureNullingPaymentTokenOnCartOrderRemovesOrphanToken() {
		PaymentToken paymentToken = new PaymentTokenImpl.TokenBuilder().withDisplayValue("displayValue").withValue("value").build();

		CartOrder cartOrder = beanFactory.getBean(ContextIdNames.CART_ORDER);
		cartOrder.setShoppingCartGuid(cartGuid);
		cartOrder.usePaymentMethod(paymentToken);
		cartOrder = cartOrderService.saveOrUpdate(cartOrder);
		AbstractPaymentMethodImpl<?> persistedPaymentMethod = (AbstractPaymentMethodImpl<?>) cartOrder.getPaymentMethod();
		cartOrder.clearPaymentMethod();
		cartOrderService.saveOrUpdate(cartOrder);
		PaymentToken retrievedPaymentMethod = paymentTokenDao.get(persistedPaymentMethod.getUidPk());
		assertNull("Orphaned payment token should be deleted", retrievedPaymentMethod);
	}

	@Test
	public void testRetrieveBillingAddress() {
		CartOrder cartOrder = createAndSaveCartOrder();
		Address retrievedBillingAddress = cartOrderService.getBillingAddress(cartOrder);
		assertNotNull(retrievedBillingAddress);
		assertEquals(billingAddressGuid, retrievedBillingAddress.getGuid());
	}

	@Test
	public void testRetrieveShippingAddress() {
		CartOrder cartOrder = createAndSaveCartOrder();
		Address retrievedShippingAddress = cartOrderService.getShippingAddress(cartOrder);
		assertNotNull(retrievedShippingAddress);
		assertEquals(shippingAddressGuid, retrievedShippingAddress.getGuid());
	}

	/**
	 * Assert that a CartOrder which is updated will also update its ShoppingCart's Last Modified Date.
	 */
	@Test
	public void testUpdateCartAlsoUpdatesShoppingCartLastModifiedDate() {
		createAndSaveCartOrder();
		ShoppingCart cart = loadShoppingCart();
		Date initialDate = cart.getLastModifiedDate();

		CartOrder cartOrder = cartOrderService.findByStoreCodeAndGuid(store.getCode(), cartOrderGuid);
		cartOrder.setBillingAddressGuid("1234"); // This means it will be saved to the DB.
		cartOrderService.saveOrUpdate(cartOrder);

		cart = loadShoppingCart();
		Date updatedDate = cart.getLastModifiedDate();
		assertTrue(initialDate.before(updatedDate));
	}

	/**
	 * Assert that a CartOrder can be removed and its address and shopping cart are not removed.
	 */
	@Test
	public void testRemoveCartOrder() {
		CartOrder cartOrder = createAndSaveCartOrder();

		CartOrder retrievedCartOrder = cartOrderService.findByStoreCodeAndGuid(store.getCode(), cartOrder.getGuid());

		cartOrderService.remove(retrievedCartOrder);
		assertNull(cartOrderService.findByStoreCodeAndGuid(store.getCode(), cartOrderGuid));

		assertNotNull(loadShoppingCart());
		PaymentTokenImpl paymentMethod = (PaymentTokenImpl) cartOrder.getPaymentMethod();
		assertNull("Payment method should have been cascade deleted", persistenceEngine.get(PaymentTokenImpl.class, paymentMethod.getUidPk()));
		assertNotNull("Billing address should not have been deleted", addressDao.findByGuid(billingAddressGuid));
		assertNotNull("Shipping address should not have been deleted", addressDao.findByGuid(shippingAddressGuid));
	}

	/**
	 * Assert that an Address can be removed and the CartOrder and ShoppingCart remain.
	 */
	@Test
	public void testRemoveAddress() {
		createAndSaveCartOrder();
		addressDao.remove(addressDao.findByGuid(billingAddressGuid));
		assertNull(addressDao.findByGuid(billingAddressGuid));
		CartOrder cartOrder = cartOrderService.findByStoreCodeAndGuid(store.getCode(), cartOrderGuid);
		assertNotNull(cartOrder);
		assertNotNull(cartOrder.getShoppingCartGuid());
		assertNull(cartOrderService.getBillingAddress(cartOrder));
	}

	/**
	 * Assert that a CartOrder can have its address removed and updated.
	 */
	@Test
	public void testNullAddress() {
		createAndSaveCartOrder();
		CartOrder cartOrder = cartOrderService.findByStoreCodeAndGuid(store.getCode(), cartOrderGuid);
		cartOrder.setBillingAddressGuid(null);
		cartOrderService.saveOrUpdate(cartOrder);

		cartOrder = cartOrderService.findByStoreCodeAndGuid(store.getCode(), cartOrderGuid);
		assertNotNull(cartOrder);
		assertNull(cartOrder.getBillingAddressGuid());
		assertNotNull(addressDao.findByGuid(billingAddressGuid));
	}

	/**
	 * Assert that a CartOrder will throw an exception if its ShoppingCart GUID is set to null.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testNullShoppingCart() {
		createAndSaveCartOrder();
		CartOrder cartOrder = cartOrderService.findByStoreCodeAndGuid(store.getCode(), cartOrderGuid);
		cartOrder.setShoppingCartGuid(null);
	}

	/**
	 * Asserts that a removed cart order will not exist.
	 */
	@Test
	public void testRemoveCartOrderByShoppingCartGuid() {
		createAndSaveCartOrder();
		ShoppingCart shoppingCart = loadShoppingCart();
		assertNotNull(shoppingCart);
		cartOrderService.removeIfExistsByShoppingCart(shoppingCart);
		CartOrder cartOrder = cartOrderService.findByShoppingCartGuid(cartGuid);
		assertNull(cartOrder);
	}

	/**
	 * Asserts that the {@link CartOrderService#removeIfExistsByShoppingCart(ShoppingCart)} will not fail on a non-existing cart order.
	 */
	@Test
	public void testRemoveNonExistingCartOrderByShoppingCartGuid() {
		CartOrder cartOrder = cartOrderService.findByShoppingCartGuid(cartGuid);
		assertNull(cartOrder);
		cartOrderService.removeIfExistsByShoppingCart(cart);
	}

	/**
	 * Test create if not exists with default billing address.
	 */
	@Test
	public void testCreateIfNotExistsWithDefaultBillingAddress() {
		customer.getPaymentMethods().clear();
		customer = customerService.update(customer);
		cartOrderService.createOrderIfPossible(cart);
		CartOrder cartOrder = cartOrderService.findByShoppingCartGuid(cartGuid);

		assertEquals("Customer preferred billing address guid and cart order billing address guid should be equal.",
				customer.getPreferredBillingAddress().getGuid(), cartOrder.getBillingAddressGuid());
		assertNull("cart order payment method should be null.", cartOrder.getPaymentMethod());
	}

	/**
	 * Assert that the {@link CartOrderService#createOrderIfPossible(com.elasticpath.domain.shoppingcart.ShoppingCart)} will create a CartOrder without payment method GUID and the billing
	 * address GUID because the Customer has no such info.
	 */
	@Test
	public void testCreateIfNotExistsWithNoDefaults() {
		customer.setPreferredBillingAddress(null);
		customer.getPaymentMethods().clear();
		customer = customerService.update(customer);

		cartOrderService.createOrderIfPossible(cart);
		CartOrder cartOrder = cartOrderService.findByShoppingCartGuid(cartGuid);

		assertNull("Cart order billing address guid should be null.", cartOrder.getBillingAddressGuid());
		assertNull("Cart order payment method should be null.", cartOrder.getPaymentMethod());
	}

	/**
	 * Tests {@link CartOrderService#findCartOrderGuidsByCustomerGuid(String, String) for the main flow.
	 */
	@Test
	public void testFindGuidsByCustomerGuid() {
		List<String> guidsBefore = cartOrderService.findCartOrderGuidsByCustomerGuid(store.getCode(), customer.getGuid());
		assertTrue("No cart order GUID should be found, as there are no cart orders yet.", guidsBefore.isEmpty());

		cartOrderService.createOrderIfPossible(cart);
		CartOrder cartOrder = cartOrderService.findByShoppingCartGuid(cartGuid);

		List<String> guids = cartOrderService.findCartOrderGuidsByCustomerGuid(store.getCode(), customer.getGuid());
		assertEquals("Only one cart order should be found.", 1, guids.size());
		assertTrue("The cart order GUID should match that of the newly created cart order.", guids.contains(cartOrder.getGuid()));
	}

	/**
	 * Tests {@link CartOrderService#findCartOrderGuidsByCustomerGuid(String, String) for an invalid customer GUID.
	 */
	@Test
	public void testFindGuidsByCustomerGuidInvalidGuid() {
		cartOrderService.createOrderIfPossible(cart);

		List<String> guids = cartOrderService.findCartOrderGuidsByCustomerGuid(store.getCode(), "INVALID_GUID");
		assertTrue("No cart order guids should be found, as the customer GUID does not match.", guids.isEmpty());
	}

	/**
	 * Tests {@link CartOrderService#findCartOrderGuidsByCustomerGuid(String, String) for an invalid store code.
	 */
	@Test
	public void testFindGuidsByCustomerGuidInvalidStoreCode() {
		cartOrderService.createOrderIfPossible(cart);

		List<String> guids = cartOrderService.findCartOrderGuidsByCustomerGuid("BAD STORE CODE", customer.getGuid());
		assertTrue("No cart order guids should be found, as the store code is invalid.", guids.isEmpty());
	}

	/**
	 * Tests getting the last modified date of a CartOrder and checks it against the one from the shopping cart.
	 */
	@Test
	public void testLastModifiedDate() {
		createAndSaveCartOrder();
		ShoppingCart shoppingCart = loadShoppingCart();
		Date lastModifiedDateFromDB = shoppingCart.getLastModifiedDate();

		Date cartOrderLastModifiedDate = cartOrderService.getCartOrderLastModifiedDate(cartOrderGuid);
		assertEquals("Last modified date of the CartOrder should be equal to the one in the database.",
				lastModifiedDateFromDB,
				cartOrderLastModifiedDate);
	}

	/**
	 * Verifies that getting the last modified date of non-existing CartOrder returns <code>null</code>.
	 */
	@Test
	public void testLastModifiedDateInvalidGuid() {
		Date cartOrderLastModifiedDate = cartOrderService.getCartOrderLastModifiedDate(INVALID_CARTORDER_GUID);
		assertNull("The last modified date should be null, iff the cart order is not found.", cartOrderLastModifiedDate);
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
		final CustomerSession customerSession = TestCustomerSessionFactoryForTestApplication.getInstance()
				.createNewCustomerSessionWithContext(shopper);
		customerSession.setCurrency(Currency.getInstance("USD"));
		final ShoppingCartImpl shoppingCart = getBean(ContextIdNames.SHOPPING_CART);
		shoppingCart.setShopper(shopper);
		shoppingCart.setStore(store);
		shoppingCart.getShoppingCartMemento().setGuid(cartGuid);
		shoppingCart.setCustomerSession(customerSession);
		shopper.setCurrentShoppingCart(shoppingCart);
		return shoppingCartService.saveOrUpdate(shoppingCart);
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

		PaymentToken token = new PaymentTokenImpl.TokenBuilder()
				.withDisplayValue("**** **** **** 1234")
				.withGatewayGuid("1234")
				.withValue("token-value")
				.build();

		Customer customer = customerBuilder.withStoreCode(store.getCode())
				.withGuid(customerGuid)
				.withPaymentMethods(token)
				.build();

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

	private ShoppingCart loadShoppingCart() {
		return shoppingCartService.findByGuid(cartGuid);
	}

	private void shouldHavePhysicalGoodInCart() {
		final ShoppingItemDto dto = new ShoppingItemDto(shippableProductSku.getSkuCode(), 1);
		cartDirector.addItemToCart(cart, dto);
		cart = shoppingCartService.saveOrUpdate(cart);
	}

	private CartOrder shouldCreateAndPersistCardOrderWithAddressAndShippingOptionCode(final String shippingAddressGuid,
			final String shippingOptionCode) {
		cartOrderService.createOrderIfPossible(cart);
		CartOrder cartOrder = cartOrderService.findByShoppingCartGuid(cartGuid);
		cartOrder.setShippingAddressGuid(shippingAddressGuid);
		cartOrder.setShippingOptionCode(shippingOptionCode);

		cartOrderService.saveOrUpdate(cartOrder);
		return cartOrder;
	}

	private <T> T getBean(final String name) {
		return getBeanFactory().getBean(name);
	}

}

