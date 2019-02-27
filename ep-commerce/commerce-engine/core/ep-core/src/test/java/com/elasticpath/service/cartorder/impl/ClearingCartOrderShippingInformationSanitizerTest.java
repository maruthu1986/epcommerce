/**
 * Copyright (c) Elastic Path Software Inc., 2012
 */

package com.elasticpath.service.cartorder.impl;

import static java.util.Collections.singletonList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Currency;
import java.util.List;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.cartorder.impl.CartOrderImpl;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.customer.impl.CustomerAddressImpl;
import com.elasticpath.domain.customer.impl.CustomerImpl;
import com.elasticpath.domain.impl.ElasticPathImpl;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.service.customer.CustomerSessionService;
import com.elasticpath.service.customer.dao.CustomerAddressDao;
import com.elasticpath.service.shipping.ShippingOptionResult;
import com.elasticpath.service.shipping.ShippingOptionService;
import com.elasticpath.service.shipping.impl.ShippingOptionResultImpl;
import com.elasticpath.service.shoppingcart.ShoppingCartService;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;
import com.elasticpath.shipping.connectivity.dto.impl.ShippingOptionImpl;

/**
 * Test functionality of the {@link ClearingCartOrderShippingInformationSanitizer}.
 */
@SuppressWarnings({ "unchecked", "PMD.DontUseElasticPathImplGetInstance" })
@RunWith(MockitoJUnitRunner.class)
public class ClearingCartOrderShippingInformationSanitizerTest {

	private static final String SHIPPING_OPTION_CODE_SHOULD_BE_CLEARED = "The cart order shipping option code should be cleared.";

	private static final String ADDRESS_GUID_SHOULD_BE_CLEARED = "The cart order shipping address guid should be cleared.";

	private static final String SHIPPING_OPTION_SHOULD_REMAIN_NULL = "The shipping option should remain null";

	private static final String SHIPPING_OPTION_CODE_SHOULD_BE_UNCHANGED = "The shipping option code should remain unchanged.";

	private static final String ADDRESS_GUID_SHOULD_BE_UNCHANGED = "The shipping address guid should remain unchanged.";

	private static final String CART_ORDER_SHOULD_BE_UNCHANGED = "CartOrder should be unchanged.";

	private static final String CART_ORDER_SHOULD_BE_CHANGED = "CartOrder should be changed.";

	private static final String SHIPPING_ADDRESS_GUID = "SHIPPING_ADDRESS_GUID";

	private static final String SHOPPING_CART_GUID = "SHOPPING_CART_GUID";

	private static final String SHIPPING_OPTION_CODE = "SHIPPING_OPTION_CODE";

	private static final String NON_EXISTENT_SHIPPING_ADDRESS_GUID = "NON_EXISTENT_SHIPPING_ADDRESS_GUID";

	private static final String NON_EXISTENT_SHIPPING_OPTION_CODE = "NON_EXISTENT_SHIPPING_OPTION_CODE";

	private static final String STORE_CODE = "storeCode";

	private static final Currency CURRENCY = Currency.getInstance(Locale.getDefault());
	public static final String USER_ID = "userId";

	@Mock
	private BeanFactory beanFactory;

	@Mock
	private CustomerAddressDao customerAddressDao;

	@Mock
	private ShippingOptionService shippingOptionService;

	@Mock
	private StoreService storeService;

	@Mock
	private ShoppingCartService shoppingCartService;

	@Mock
	private CustomerSessionService customerSessionService;

	@Mock
	private ShoppingCart cart;

	@Mock
	private Shopper shopper;

	@Mock
	private CustomerSession customerSession;

	private List<ShippingOption> shippingOptions;

	private ClearingCartOrderShippingInformationSanitizer clearingCartOrderShippingInformationSanitizer;

	/**
	 * Set up the test environment.
	 */
	@Before
	public void setUp() {
		when(beanFactory.getBean(ContextIdNames.CUSTOMER_SESSION_SERVICE)).thenReturn(customerSessionService);
		((ElasticPathImpl) ElasticPathImpl.getInstance()).setBeanFactory(beanFactory);
		clearingCartOrderShippingInformationSanitizer = new ClearingCartOrderShippingInformationSanitizer();
		clearingCartOrderShippingInformationSanitizer.setCustomerAddressDao(customerAddressDao);
		clearingCartOrderShippingInformationSanitizer.setShippingOptionService(shippingOptionService);
		clearingCartOrderShippingInformationSanitizer.setShoppingCartService(shoppingCartService);
		clearingCartOrderShippingInformationSanitizer.setStoreService(storeService);
		clearingCartOrderShippingInformationSanitizer.setBeanFactory(beanFactory);

		final ShippingOptionImpl shippingOption = new ShippingOptionImpl();
		shippingOption.setCode(SHIPPING_OPTION_CODE);
		shippingOptions = singletonList(shippingOption);

		doReturn(createShippingOptionResult(shippingOptions)).when(shippingOptionService).getShippingOptions(cart);
	}

	@After
	public void tearDown() {
		((ElasticPathImpl) ElasticPathImpl.getInstance()).setBeanFactory(null);
	}

	/**
	 * Ensure clearing of address and shipping option on cart order with non existent shipping address.
	 */
	@Test
	public void ensureClearingOfAddressAndShippingOptionOnCartOrderWithNonExistentShippingAddress() {
		shouldReturnCustomerAddressFromFindByGuidForGuid(null, NON_EXISTENT_SHIPPING_ADDRESS_GUID);
		shouldReturnShoppingCartByGuid(mockShoppingCart(null));

		CartOrder cartOrder = createCartOrder(NON_EXISTENT_SHIPPING_ADDRESS_GUID, SHIPPING_OPTION_CODE);
		boolean cartOrderWasUpdated = clearingCartOrderShippingInformationSanitizer.sanitize(cartOrder);

		assertThat(cartOrder.getShippingAddressGuid()).as(ADDRESS_GUID_SHOULD_BE_CLEARED).isNull();
		assertThat(cartOrder.getShippingOptionCode()).as(SHIPPING_OPTION_CODE_SHOULD_BE_CLEARED).isNull();

		assertThat(cartOrderWasUpdated).as(CART_ORDER_SHOULD_BE_CHANGED).isTrue();
		verify(customerAddressDao).findByGuid(NON_EXISTENT_SHIPPING_ADDRESS_GUID);
		verify(shoppingCartService).findByGuid(SHOPPING_CART_GUID);

	}

	/**
	 * Ensure clearing of address and shipping option on cart order when a null shipping address is explicitly passed.
	 */
	@Test
	public void ensureClearingOfAddressAndShippingOptionOnCartOrderWithExplicitlyPassedNullShippingAddress() {
		CartOrder cartOrder = createCartOrder(NON_EXISTENT_SHIPPING_ADDRESS_GUID, SHIPPING_OPTION_CODE);
		ShippingOptionResult shippingOptionResult = createShippingOptionResult(shippingOptions);
		boolean cartOrderWasUpdated = clearingCartOrderShippingInformationSanitizer.sanitize(cartOrder, null, () -> shippingOptionResult);

		assertThat(cartOrder.getShippingAddressGuid()).as(ADDRESS_GUID_SHOULD_BE_CLEARED).isNull();
		assertThat(cartOrder.getShippingOptionCode()).as(SHIPPING_OPTION_SHOULD_REMAIN_NULL).isNull();

		assertThat(cartOrderWasUpdated).as(CART_ORDER_SHOULD_BE_CHANGED).isTrue();
	}

	/**
	 * Ensure unchanged cart order when shipping option not set.
	 */
	@Test
	public void ensureUnchangedCartOrderWhenShippingOptionNotSet() {
		CustomerAddress address = createCustomerAddress();
		shouldReturnCustomerAddressFromFindByGuidForGuid(address, SHIPPING_ADDRESS_GUID);
		shouldReturnShoppingCartByGuid(mockShoppingCart(address));

		CartOrder cartOrder = createCartOrder(SHIPPING_ADDRESS_GUID, null);
		boolean cartOrderWasUpdated = clearingCartOrderShippingInformationSanitizer.sanitize(cartOrder);

		assertThat(SHIPPING_ADDRESS_GUID).as(ADDRESS_GUID_SHOULD_BE_UNCHANGED).isEqualTo(cartOrder.getShippingAddressGuid());
		assertThat(cartOrder.getShippingOptionCode()).as(SHIPPING_OPTION_SHOULD_REMAIN_NULL).isNull();
		assertThat(cartOrderWasUpdated).as(CART_ORDER_SHOULD_BE_UNCHANGED).isFalse();
		verify(customerAddressDao).findByGuid(SHIPPING_ADDRESS_GUID);
		verify(shoppingCartService).findByGuid(SHOPPING_CART_GUID);

	}

	/**
	 * Ensure unchanged cart order when shipping option not set when calling
	 * {@link ClearingCartOrderShippingInformationSanitizer#sanitize(CartOrder, Address, java.util.function.Supplier)}.
	 */
	@Test
	public void ensureUnchangedCartOrderWhenShippingOptionNotSetWithExplicitlyPassedArguments() {
		CartOrder cartOrder = createCartOrder(SHIPPING_ADDRESS_GUID, null);

		CustomerAddress address = createCustomerAddress();
		ShippingOptionResult shippingOptionResult = createShippingOptionResult(shippingOptions);
		boolean cartOrderWasUpdated = clearingCartOrderShippingInformationSanitizer.sanitize(cartOrder, address, () -> shippingOptionResult);

		assertThat(SHIPPING_ADDRESS_GUID).as(ADDRESS_GUID_SHOULD_BE_UNCHANGED).isEqualTo(cartOrder.getShippingAddressGuid());
		assertThat(cartOrder.getShippingOptionCode()).as(SHIPPING_OPTION_SHOULD_REMAIN_NULL).isNull();
		assertThat(cartOrderWasUpdated).as(CART_ORDER_SHOULD_BE_UNCHANGED).isFalse();
	}

	/**
	 * Ensure shipping option cleared on cart order when shipping option does not exist.
	 */
	@Test
	public void ensureShippingOptionClearedOnCartOrderWhenShippingOptionDoesNotExist() {
		CustomerAddress address = createCustomerAddress();
		shouldReturnCustomerAddressFromFindByGuidForGuid(address, SHIPPING_ADDRESS_GUID);
		shouldReturnShoppingCartByGuid(mockShoppingCart(address));

		CartOrder cartOrder = createCartOrder(SHIPPING_ADDRESS_GUID, NON_EXISTENT_SHIPPING_OPTION_CODE);
		boolean cartOrderWasUpdated = clearingCartOrderShippingInformationSanitizer.sanitize(cartOrder);

		assertThat(SHIPPING_ADDRESS_GUID).as(ADDRESS_GUID_SHOULD_BE_UNCHANGED).isEqualTo(cartOrder.getShippingAddressGuid());
		assertThat(cartOrder.getShippingOptionCode()).as(SHIPPING_OPTION_CODE_SHOULD_BE_CLEARED).isNull();
		assertThat(cartOrderWasUpdated).as(CART_ORDER_SHOULD_BE_CHANGED).isTrue();
		verify(customerAddressDao).findByGuid(SHIPPING_ADDRESS_GUID);
		verify(shoppingCartService).findByGuid(SHOPPING_CART_GUID);

	}

	@Test
	public void ensureThatCustomerSessionIsUpdated() {

		CustomerAddress address = createCustomerAddress();
		shouldReturnCustomerAddressFromFindByGuidForGuid(address, SHIPPING_ADDRESS_GUID);
		shouldReturnShoppingCartByGuid(mockShoppingCart(address));

		CartOrder cartOrder = createCartOrder(SHIPPING_ADDRESS_GUID, NON_EXISTENT_SHIPPING_OPTION_CODE);

		givenNoCustomerSessionExists();

		boolean cartOrderWasUpdated = clearingCartOrderShippingInformationSanitizer.sanitize(cartOrder);

		assertThat(SHIPPING_ADDRESS_GUID).as(ADDRESS_GUID_SHOULD_BE_UNCHANGED).isEqualTo(cartOrder.getShippingAddressGuid());
		assertThat(cartOrder.getShippingOptionCode()).as(SHIPPING_OPTION_CODE_SHOULD_BE_CLEARED).isNull();
		assertThat(cartOrderWasUpdated).as(CART_ORDER_SHOULD_BE_CHANGED).isTrue();

		verify(customerSessionService).findByCustomerIdAndStoreCode(USER_ID, STORE_CODE);
		verify(cart).getCustomerSession();
		verify(shopper).getStoreCode();
		verify(shopper).getCustomer();
		verify(customerSessionService).createWithShopper(shopper);
		verify(customerSession).setLocale(Locale.getDefault());
		verify(customerAddressDao).findByGuid(SHIPPING_ADDRESS_GUID);
		verify(shoppingCartService).findByGuid(SHOPPING_CART_GUID);
		verify(customerSessionService).initializeCustomerSessionForPricing(customerSession, STORE_CODE, CURRENCY);

	}

	private void givenNoCustomerSessionExists() {
		Customer customer = new CustomerImpl();
		customer.setUserId(USER_ID);

		Store store = new StoreImpl();
		store.setDefaultLocale(Locale.getDefault());
		store.setDefaultCurrency(CURRENCY);

		when(shopper.getStoreCode()).thenReturn(STORE_CODE);
		when(shopper.getCustomer()).thenReturn(customer);
		when(cart.getCustomerSession()).thenReturn(null);
		when(customerSessionService.findByCustomerIdAndStoreCode(USER_ID, STORE_CODE)).thenReturn(null);
		when(customerSessionService.createWithShopper(shopper)).thenReturn(customerSession);
		when(storeService.findStoreWithCode(STORE_CODE)).thenReturn(store);
	}

	/**
	 * Ensure shipping option cleared on cart order when shipping option does not exist in the explicit list passed in.
	 */
	@Test
	public void ensureShippingOptionClearedOnCartOrderWhenShippingOptionDoesNotExistInExplicitList() {
		CustomerAddress address = createCustomerAddress();

		CartOrder cartOrder = createCartOrder(SHIPPING_ADDRESS_GUID, NON_EXISTENT_SHIPPING_OPTION_CODE);
		ShippingOptionResult shippingOptionResult = createShippingOptionResult(shippingOptions);
		boolean cartOrderWasUpdated = clearingCartOrderShippingInformationSanitizer.sanitize(cartOrder, address, () -> shippingOptionResult);

		assertThat(SHIPPING_ADDRESS_GUID).as(ADDRESS_GUID_SHOULD_BE_UNCHANGED).isEqualTo(cartOrder.getShippingAddressGuid());
		assertThat(cartOrder.getShippingOptionCode()).as(SHIPPING_OPTION_CODE_SHOULD_BE_CLEARED).isNull();
		assertThat(cartOrderWasUpdated).as(CART_ORDER_SHOULD_BE_CHANGED).isTrue();
	}

	/**
	 * Ensure unchanged cart order when all shipping info is valid.
	 */
	@Test
	public void ensureUnchangedCartOrderWhenAllShippingInfoIsValid() {
		CustomerAddress address = createCustomerAddress();
		shouldReturnCustomerAddressFromFindByGuidForGuid(address, SHIPPING_ADDRESS_GUID);
		shouldReturnShoppingCartByGuid(mockShoppingCart(address));

		CartOrder cartOrder = createCartOrder(SHIPPING_ADDRESS_GUID, SHIPPING_OPTION_CODE);
		boolean cartOrderWasUpdated = clearingCartOrderShippingInformationSanitizer.sanitize(cartOrder);

		assertThat(cartOrderWasUpdated).as(CART_ORDER_SHOULD_BE_UNCHANGED).isFalse();
		assertThat(SHIPPING_ADDRESS_GUID).as(ADDRESS_GUID_SHOULD_BE_UNCHANGED).isEqualTo(cartOrder.getShippingAddressGuid());
		assertThat(SHIPPING_OPTION_CODE).as(SHIPPING_OPTION_CODE_SHOULD_BE_UNCHANGED).isEqualTo(cartOrder.getShippingOptionCode());
		verify(customerAddressDao).findByGuid(SHIPPING_ADDRESS_GUID);
		verify(shoppingCartService).findByGuid(SHOPPING_CART_GUID);

	}

	/**
	 * Ensure unchanged cart order when all shipping info is valid.
	 */
	@Test
	public void ensureUnchangedCartOrderWhenAllShippingInfoIsValidAndExplicitlyProvided() {
		CustomerAddress address = createCustomerAddress();

		CartOrder cartOrder = createCartOrder(SHIPPING_ADDRESS_GUID, SHIPPING_OPTION_CODE);
		ShippingOptionResult shippingOptionResult = createShippingOptionResult(shippingOptions);
		boolean cartOrderWasUpdated = clearingCartOrderShippingInformationSanitizer.sanitize(cartOrder, address, () -> shippingOptionResult);

		assertThat(cartOrderWasUpdated).as(CART_ORDER_SHOULD_BE_UNCHANGED).isFalse();
		assertThat(SHIPPING_ADDRESS_GUID).as(ADDRESS_GUID_SHOULD_BE_UNCHANGED).isEqualTo(cartOrder.getShippingAddressGuid());
		assertThat(SHIPPING_OPTION_CODE).as(SHIPPING_OPTION_CODE_SHOULD_BE_UNCHANGED).isEqualTo(cartOrder.getShippingOptionCode());
	}

	private void shouldReturnCustomerAddressFromFindByGuidForGuid(final CustomerAddress customerAddress, final String customerAddressGuid) {
		when(customerAddressDao.findByGuid(customerAddressGuid)).thenReturn(customerAddress);
	}

	private void shouldReturnShoppingCartByGuid(final ShoppingCart shoppingCart) {
		when(shoppingCartService.findByGuid(ClearingCartOrderShippingInformationSanitizerTest.SHOPPING_CART_GUID)).thenReturn(shoppingCart);
	}

	private CartOrder createCartOrder(final String shippingAddressGuid, final String shippingOptionCode) {
		CartOrder initialCartOrder = new CartOrderImpl();
		initialCartOrder.setShippingAddressGuid(shippingAddressGuid);
		initialCartOrder.setShippingOptionCode(shippingOptionCode);
		initialCartOrder.setShoppingCartGuid(ClearingCartOrderShippingInformationSanitizerTest.SHOPPING_CART_GUID);
		return initialCartOrder;
	}

	private ShoppingCart mockShoppingCart(final Address address) {
		when(cart.getShopper()).thenReturn(shopper);
		when(cart.getCustomerSession()).thenReturn(mock(CustomerSession.class));
		when(shopper.getCurrentShoppingCart()).thenReturn(cart);
		doNothing().when(cart).setShippingAddress(address);
		return cart;
	}

	private CustomerAddress createCustomerAddress() {
		CustomerAddress customerAddress = new CustomerAddressImpl();
		customerAddress.setGuid(ClearingCartOrderShippingInformationSanitizerTest.SHIPPING_ADDRESS_GUID);
		return customerAddress;
	}

	private ShippingOptionResult createShippingOptionResult(final List<ShippingOption> shippingOptions) {
		final ShippingOptionResultImpl result = new ShippingOptionResultImpl();
		result.setAvailableShippingOptions(shippingOptions);
		return result;
	}
}
