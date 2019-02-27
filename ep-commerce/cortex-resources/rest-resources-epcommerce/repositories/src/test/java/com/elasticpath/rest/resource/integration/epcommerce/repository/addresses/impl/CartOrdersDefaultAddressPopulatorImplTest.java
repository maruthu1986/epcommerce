/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.addresses.impl;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;

import io.reactivex.Observable;
import io.reactivex.Single;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;

@RunWith(MockitoJUnitRunner.class)
public class CartOrdersDefaultAddressPopulatorImplTest {
	private static final String NEW_ADDRESS_GUID = "NEW_ADDRESS_GUID";

	private static final String EXISTING_ADDRESS_GUID = "EXISTING_ADDRESS_GUID";

	private static final String CART_ORDER_GUID = "CART_ORDER_GUID";

	private static final String CUSTOMER_GUID = "CUSTOMER_GUID";

	private static final String STORE_CODE = "STORE_CODE";

	private final Collection<String> cartOrderGuids = new ArrayList<>();

	@Mock
	private CartOrderRepository cartOrderRepository;

	@InjectMocks
	private CartOrdersDefaultAddressPopulatorImpl cartOrdersDefaultAddressPopulator;
	
	@Mock
	private CustomerAddress mockAddress;
	
	@Mock
	private Customer mockCustomer;

	@Mock
	private CartOrder mockCartOrder;

	@Before
	public void setUp() {
		when(mockCustomer.getGuid()).thenReturn(CUSTOMER_GUID);
		when(mockAddress.getGuid()).thenReturn(NEW_ADDRESS_GUID);
		when(mockCartOrder.getGuid()).thenReturn(CART_ORDER_GUID);
	}

	@Test
	public void testUpdateBillingAddressOnCartOrdersSuccessfully() {
		setUpSuccessfulCartOrderRetrieval();
		allowingCartOrderBillingAddressToBe(null);

		cartOrdersDefaultAddressPopulator.updateAllCartOrdersAddresses(mockCustomer, mockAddress, STORE_CODE, true, false)
				.test();
		
		verify(mockCartOrder, times(1)).setBillingAddressGuid(NEW_ADDRESS_GUID);
		verify(cartOrderRepository, times(1)).saveCartOrderAsSingle(mockCartOrder);
	}
	
	@Test
	public void testBillingAddressNotSetWhenExisting() {
		setUpSuccessfulCartOrderRetrieval();
		allowingCartOrderBillingAddressToBe(EXISTING_ADDRESS_GUID);

		cartOrdersDefaultAddressPopulator.updateAllCartOrdersAddresses(mockCustomer, mockAddress, STORE_CODE, true, false)
				.test()
				.assertNoErrors();

		verify(mockCartOrder, never()).setBillingAddressGuid(NEW_ADDRESS_GUID);
		verify(cartOrderRepository, never()).saveCartOrder(mockCartOrder);
	}

	@Test
	public void testUpdateShippingAddressOnCartOrdersSuccessfully() {
		setUpSuccessfulCartOrderRetrieval();
		allowingCartOrderShippingAddressToBe(null);

		cartOrdersDefaultAddressPopulator.updateAllCartOrdersAddresses(mockCustomer, mockAddress, STORE_CODE, false, true)
				.test();

		verify(cartOrderRepository, times(1)).updateShippingAddressOnCartOrderAsSingle(NEW_ADDRESS_GUID, CART_ORDER_GUID, STORE_CODE);
	}
	
	@Test
	public void testShippingAddressNotSetWhenExisting() {
		setUpSuccessfulCartOrderRetrieval();
		allowingCartOrderShippingAddressToBe(EXISTING_ADDRESS_GUID);

		cartOrdersDefaultAddressPopulator.updateAllCartOrdersAddresses(mockCustomer, mockAddress, STORE_CODE, false, true)
				.test()
				.assertNoErrors();

		verify(cartOrderRepository, never()).updateShippingAddressOnCartOrderAsSingle(NEW_ADDRESS_GUID, CART_ORDER_GUID, STORE_CODE);
	}
	
	@Test
	public void testNoAddressesSetWhenStoreNotValid() {
		allowingCartOrderGuidsByCustomer(Observable.error(ResourceOperationFailure.notFound()));
		cartOrdersDefaultAddressPopulator.updateAllCartOrdersAddresses(mockCustomer, mockAddress, STORE_CODE, true, true)
				.test()
				.assertError(ResourceOperationFailure.notFound());

		verify(cartOrderRepository, never()).updateShippingAddressOnCartOrderAsSingle(NEW_ADDRESS_GUID, CART_ORDER_GUID, STORE_CODE);
		verify(mockCartOrder, never()).setBillingAddressGuid(NEW_ADDRESS_GUID);
		verify(cartOrderRepository, never()).saveCartOrder(mockCartOrder);
	}
	
	@Test
	public void testNoAddressesSetWhenNoCartOrdersFoundForCustomer() {
		allowingCartOrderGuidsByCustomer(Observable.error(ResourceOperationFailure.notFound()));

		cartOrdersDefaultAddressPopulator.updateAllCartOrdersAddresses(mockCustomer, mockAddress, STORE_CODE, true, true)
				.test()
				.assertError(ResourceOperationFailure.notFound());

		verify(cartOrderRepository, never()).updateShippingAddressOnCartOrderAsSingle(NEW_ADDRESS_GUID, CART_ORDER_GUID, STORE_CODE);
		verify(mockCartOrder, never()).setBillingAddressGuid(NEW_ADDRESS_GUID);
		verify(cartOrderRepository, never()).saveCartOrder(mockCartOrder);
	}
	
	@Test
	public void testNoAddressesSetWhenNoCartOrderFoundForGuid() {
		cartOrderGuids.add(CART_ORDER_GUID);
		allowingCartOrderGuidsByCustomer(Observable.fromIterable(cartOrderGuids));
		allowingCartOrderForGuid(Single.error(ResourceOperationFailure.notFound()));

		cartOrdersDefaultAddressPopulator.updateAllCartOrdersAddresses(mockCustomer, mockAddress, STORE_CODE, true, true)
				.test()
				.assertError(ResourceOperationFailure.notFound());

		verify(cartOrderRepository, never()).updateShippingAddressOnCartOrderAsSingle(NEW_ADDRESS_GUID, CART_ORDER_GUID, STORE_CODE);
		verify(mockCartOrder, never()).setBillingAddressGuid(NEW_ADDRESS_GUID);
		verify(cartOrderRepository, never()).saveCartOrder(mockCartOrder);
	}
	
	private void setUpSuccessfulCartOrderRetrieval() {
		cartOrderGuids.add(CART_ORDER_GUID);
		allowingCartOrderGuidsByCustomer(Observable.fromIterable(cartOrderGuids));
		allowingCartOrderForGuid(Single.just(mockCartOrder));
	}

	private void allowingCartOrderShippingAddressToBe(final String existingAddressGuid) {
		when(mockCartOrder.getShippingAddressGuid()).thenReturn(existingAddressGuid);
	}

	private void allowingCartOrderBillingAddressToBe(final String existingAddressGuid) {
		when(mockCartOrder.getBillingAddressGuid()).thenReturn(existingAddressGuid);
	}

	private void allowingCartOrderForGuid(final Single<CartOrder> result) {
		when(cartOrderRepository.findByGuidAsSingle(STORE_CODE, CART_ORDER_GUID)).thenReturn(result);
	}

	private void allowingCartOrderGuidsByCustomer(final Observable<String> result) {
		when(cartOrderRepository.findCartOrderGuidsByCustomerAsObservable(STORE_CODE, CUSTOMER_GUID)).thenReturn(result);
	}


}
