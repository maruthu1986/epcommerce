/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.service.cartorder.impl;

import static com.elasticpath.test.util.MatcherFactory.isSupplierOf;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.service.cartorder.CartOrderShippingInformationSanitizer;
import com.elasticpath.service.customer.dao.CustomerAddressDao;
import com.elasticpath.service.shipping.ShippingOptionResult;
import com.elasticpath.service.shipping.ShippingOptionService;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;

/**
 * New JUnit4 tests for {@code CartOrderShippingServiceImpl}.
 */
@SuppressWarnings({ "unchecked", "PMD.DontUseElasticPathImplGetInstance" })
@RunWith(MockitoJUnitRunner.class)
public class CartOrderShippingServiceImplTest {

	private static final String CART_ORDER_SHOULD_BE_UPDATED = "Cart Order should be updated.";

	private static final String SHIPPING_ADDRESS_GUID = "shippingAddressGuid";

	private static final String BILLING_ADDRESS_GUID = "billingAddressGuid";

	private static final String EXISTING_SHIPPING_ADDRESS_GUID = "EXISTING_SHIPPING_ADDRESS_GUID";

	private static final String UPDATED_SHIPPING_ADDRESS_GUID = "UPDATED_SHIPPING_ADDRESS_GUID";

	private static final String EXISTING_SHIPPING_OPTION_CODE = "EXISTING_SHIPPING_OPTION_CODE";

	private static final String NEW_SHIPPING_OPTION_CODE = "NEW_SHIPPING_OPTION_CODE";

	public static final String NONEXISTENT_GUID = "nonexistent-guid";

	private CartOrderShippingServiceImpl service;

	@Mock private ShippingOptionService shippingOptionService;

	@Mock private ShippingOptionResult shippingOptionResult;

	@Mock private CartOrderShippingInformationSanitizer cartOrderShippingInformationSanitizer;

	@Mock private CustomerAddressDao customerAddressDao;

	@Mock private CartOrder cartOrder;

	@Mock private Address shippingAddress;

	@Mock private ShippingOption shippingOption;

	@Mock private ShoppingCart shoppingCart;

	@Mock private Address billingAddress;


	/**
	 * Mock required services and objects.
	 */
	@Before
	public void mockRequiredServicesAndObjects() {
		service = new CartOrderShippingServiceImpl();

		service.setCustomerAddressDao(customerAddressDao);
		service.setShippingOptionService(shippingOptionService);
		service.setCartOrderShippingInformationSanitizer(cartOrderShippingInformationSanitizer);

		shippingOption = mockShippingOption(EXISTING_SHIPPING_OPTION_CODE);

		when(customerAddressDao.findByGuid(UPDATED_SHIPPING_ADDRESS_GUID)).thenReturn(shippingAddress);
		when(cartOrder.getBillingAddressGuid()).thenReturn(BILLING_ADDRESS_GUID);
		when(customerAddressDao.findByGuid(BILLING_ADDRESS_GUID)).thenReturn(billingAddress);

	}

	private ShippingOption mockShippingOption(final String code) {
		return mockShippingOption(code, null);
	}

	private ShippingOption mockShippingOption(final String code, final String name) {
		ShippingOption shippingOption = mock(ShippingOption.class, name);
		when(shippingOption.getCode()).thenReturn(code);
		return shippingOption;
	}

	@Test
	public void testCartOrderIsUpdatedWhenCartOrderShippingAddressIsNull() {
		final List<ShippingOption> shippingOptions = singletonList(shippingOption);

		mockShippingOptionServiceToReturnUnpriced(shippingOptions);
		allowingSanitizeWithGivenShippingOptionResult(shippingOptionResult);
		allowingShippingOptionCodeOnCartOrder();

		when(cartOrder.getShippingAddressGuid()).thenReturn(null);
		when(shippingOption.getCode()).thenReturn(EXISTING_SHIPPING_OPTION_CODE);

		boolean result = service.updateCartOrderShippingAddress(UPDATED_SHIPPING_ADDRESS_GUID, shoppingCart, cartOrder);

		assertThat(result).as(CART_ORDER_SHOULD_BE_UPDATED).isTrue();
		verify(shoppingCart).setShippingAddress(shippingAddress);
		verify(cartOrderShippingInformationSanitizer).sanitize(eq(cartOrder), eq(shippingAddress), argThat(isSupplierOf(shippingOptionResult)));

	}

	@Test
	public void testCartOrderIsNotUpdatedWhenUpdatingToSameShippingAddressGuid() {
		allowingShippingOptionCodeOnCartOrder();

		when(cartOrder.getShippingAddressGuid()).thenReturn(EXISTING_SHIPPING_ADDRESS_GUID);

		boolean result = service.updateCartOrderShippingAddress(EXISTING_SHIPPING_ADDRESS_GUID, shoppingCart, cartOrder);

		assertThat(result).as("Cart Order should not be updated.").isFalse();
	}

	@Test
	public void testCartIsSanitizedWhenCartOrderShippingAddressGuidIsUpdated() {
		mockShippingOptionServiceToReturnUnpriced(emptyList());
		allowingSanitizeWithGivenShippingOptionResult(shippingOptionResult);
		allowingShippingOptionCodeOnCartOrder();

		boolean result = service.updateCartOrderShippingAddress(UPDATED_SHIPPING_ADDRESS_GUID, shoppingCart, cartOrder);

		assertThat(result).as(CART_ORDER_SHOULD_BE_UPDATED).isTrue();

		verify(shoppingCart).setShippingAddress(shippingAddress);
		verify(cartOrderShippingInformationSanitizer).sanitize(eq(cartOrder), eq(shippingAddress), argThat(isSupplierOf(shippingOptionResult)));

	}

	@Test
	public void testShippingOptionIsNotSetIfNoneValidForAddress() {
		mockShippingOptionServiceToReturnUnpriced(emptyList());
		allowingSanitizeWithGivenShippingOptionResult(shippingOptionResult);
		allowingShippingOptionCodeOnCartOrder();

		boolean result = service.updateCartOrderShippingAddress(UPDATED_SHIPPING_ADDRESS_GUID, shoppingCart, cartOrder);

		assertThat(result).as(CART_ORDER_SHOULD_BE_UPDATED).isTrue();

		verify(shoppingCart).setShippingAddress(shippingAddress);
		verify(cartOrder).setShippingAddressGuid(anyString());
		verify(cartOrderShippingInformationSanitizer).sanitize(eq(cartOrder), eq(shippingAddress), argThat(isSupplierOf(shippingOptionResult)));

	}

	@Test
	public void testShippingOptionDoesNotSwitchToDefaultIfStillValid() {
		final List<ShippingOption> shippingOptions = singletonList(shippingOption);

		mockShippingOptionServiceToReturnUnpriced(shippingOptions);
		allowingSanitizeWithGivenShippingOptionResult(shippingOptionResult);
		allowingShippingOptionCodeOnCartOrder();

		when(shippingOption.getCode()).thenReturn(EXISTING_SHIPPING_OPTION_CODE);

		boolean result = service.updateCartOrderShippingAddress(UPDATED_SHIPPING_ADDRESS_GUID, shoppingCart, cartOrder);

		assertThat(result).as(CART_ORDER_SHOULD_BE_UPDATED).isTrue();
		verify(shoppingCart).setShippingAddress(shippingAddress);
		verify(cartOrder).setShippingAddressGuid(anyString());
		verify(cartOrderShippingInformationSanitizer).sanitize(eq(cartOrder), eq(shippingAddress), argThat(isSupplierOf(shippingOptionResult)));

	}

	@Test
	public void testShippingOptionSwitchesToDefaultIfExistingShippingOptionNotValid() {
		ShippingOption newOption = mockShippingOption(NEW_SHIPPING_OPTION_CODE, "newOption");
		final List<ShippingOption> shippingOptions = singletonList(newOption);

		mockShippingOptionServiceToReturnUnpriced(shippingOptions);
		allowingSanitizeWithGivenShippingOptionResult(shippingOptionResult);
		allowingShippingOptionCodeOnCartOrder();

		when(shippingOptionService.getDefaultShippingOption(shippingOptions)).thenReturn(Optional.of(shippingOption));

		boolean result = service.updateCartOrderShippingAddress(UPDATED_SHIPPING_ADDRESS_GUID, shoppingCart, cartOrder);

		assertThat(result).as(CART_ORDER_SHOULD_BE_UPDATED).isTrue();
		verify(shoppingCart).setShippingAddress(shippingAddress);
		verify(shippingOptionService).getDefaultShippingOption(shippingOptions);
		verify(cartOrder).setShippingOptionCode(EXISTING_SHIPPING_OPTION_CODE);
		verify(cartOrderShippingInformationSanitizer).sanitize(eq(cartOrder), eq(shippingAddress), argThat(isSupplierOf(shippingOptionResult)));
	}

	/**
	 * Test population of shopping cart with valid address and shipping transient fields.
	 */
	@Test
	public void testPopulateAddressAndShippingFields() {
		final List<ShippingOption> shippingOptions = singletonList(shippingOption);
		mockShippingOptionServiceToReturnUnpriced(shippingOptions);
		allowingShippingOptionCodeOnCartOrder();

		when(cartOrder.getShippingAddressGuid()).thenReturn(SHIPPING_ADDRESS_GUID);
		when(customerAddressDao.findByGuid(SHIPPING_ADDRESS_GUID)).thenReturn(shippingAddress);
		when(shippingOption.getCode()).thenReturn(EXISTING_SHIPPING_OPTION_CODE);

		service.populateAddressAndShippingFields(shoppingCart, cartOrder);

		verify(shoppingCart).setBillingAddress(billingAddress);
		verify(shoppingCart).setShippingAddress(shippingAddress);
		verify(shoppingCart).setSelectedShippingOption(shippingOption);
	}

	/**
	 * Test population of shopping cart with null shipping address.
	 */
	@Test
	public void testPopulationOfShoppingCartWithNullShippingAddress() {
		mockShippingOptionServiceToReturnUnpriced(emptyList());
		allowingShippingOptionCodeOnCartOrder();

		when(cartOrder.getShippingAddressGuid()).thenReturn(NONEXISTENT_GUID);
		when(customerAddressDao.findByGuid(NONEXISTENT_GUID)).thenReturn(null);

		service.populateAddressAndShippingFields(shoppingCart, cartOrder);

		verify(cartOrder).getShippingAddressGuid();
		verify(customerAddressDao).findByGuid(NONEXISTENT_GUID);

		verify(shoppingCart).setBillingAddress(billingAddress);
		verify(shoppingCart).setShippingAddress(null);
		verify(shoppingCart, never()).setSelectedShippingOption(any(ShippingOption.class));

	}

	private void allowingSanitizeWithGivenShippingOptionResult(final ShippingOptionResult shippingOptionResult) {
		when(cartOrder.getShippingAddressGuid()).thenReturn(EXISTING_SHIPPING_ADDRESS_GUID);
		when(cartOrderShippingInformationSanitizer.sanitize(eq(cartOrder), eq(shippingAddress), argThat(isSupplierOf(shippingOptionResult))))
				.thenReturn(true);
	}

	private void allowingShippingOptionCodeOnCartOrder() {

		when(cartOrder.getShippingOptionCode())
				.thenReturn(
						EXISTING_SHIPPING_OPTION_CODE,
						EXISTING_SHIPPING_OPTION_CODE,
						NEW_SHIPPING_OPTION_CODE);

	}

	private void mockShippingOptionServiceToReturnUnpriced(final List<ShippingOption> shippingOptions) {
		mockShippingOptionResultToReturn(shippingOptions);
		when(shippingOptionService.getShippingOptions(shoppingCart)).thenReturn(shippingOptionResult);
	}

	private void mockShippingOptionResultToReturn(final List<ShippingOption> shippingOptions) {
		when(shippingOptionResult.isSuccessful()).thenReturn(true);
		when(shippingOptionResult.getAvailableShippingOptions()).thenReturn(shippingOptions);
	}
}
