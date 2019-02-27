/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.impl;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.ErrorCheckPredicate.createErrorCheckPredicate;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.shipmentdetails.ShipmentDetailsUtil.createShipmentDetailsId;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.persistence.PersistenceException;

import io.reactivex.Single;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.cartorder.impl.CartOrderImpl;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.store.Store;
import com.elasticpath.domain.store.impl.StoreImpl;
import com.elasticpath.persistence.api.EpPersistenceException;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.command.ExecutionResult;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.impl.ReactiveAdapterImpl;
import com.elasticpath.rest.test.AssertExecutionResult;
import com.elasticpath.service.cartorder.CartOrderCouponService;
import com.elasticpath.service.cartorder.CartOrderService;
import com.elasticpath.service.cartorder.CartOrderShippingService;
import com.elasticpath.service.rules.CartOrderCouponAutoApplier;

/**
 * Test that {@link CartOrderRepositoryImpl} behaves as expected.
 */
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("PMD.TooManyMethods")
public class CartOrderRepositoryImplTest {

	private static final String STORE_CODE = "store";
	private static final String CART_ORDER_GUID = "cart order guid";
	private static final String INVALID_CART_ORDER_ID = "invalidCartId";
	private static final String CART_GUID = "cart guid";
	private static final String CUSTOMER_GUID = "customer guid";
	private static final String SHIPPING_ADDRESS_GUID = "SHIPPING_ADDRESS_GUID";
	private static final String ORDER_WITH_CART_GUID_NOT_FOUND = "No cart order with cart GUID %s was found.";
	private static final String DELIVERY_ID = "delivery id";

	@Mock
	private CartOrderService cartOrderService;

	@Mock
	private ShoppingCartRepository shoppingCartRepository;

	@Mock
	private CartOrderShippingService cartOrderShippingService;

	@Mock
	private CartOrderCouponService cartOrderCouponService;

	@InjectMocks
	private ReactiveAdapterImpl reactiveAdapter;

	private CartOrderRepositoryImpl repository;

	@Mock
	private CartOrder cartOrder;

	@Mock
	private CartOrderCouponAutoApplier cartOrderCouponAutoApplier;

	@Mock
	private ShoppingCart shoppingCart;

	@Before
	public void setUp() {
		allowingValidCartOrder();

		repository = new CartOrderRepositoryImpl(cartOrderService, shoppingCartRepository, cartOrderShippingService, cartOrderCouponService,
				cartOrderCouponAutoApplier, reactiveAdapter);

		when(shoppingCartRepository.getShoppingCart(CART_GUID)).thenReturn(Single.just(shoppingCart));
	}

	/**
	 * Test the behaviour of find by guid.
	 */
	@Test
	public void testFindByStoreCodeAndGuid() {
		when(cartOrderService.findByStoreCodeAndGuid(STORE_CODE, CART_ORDER_GUID)).thenReturn(cartOrder);

		allowingCartOrderStoreCodeToBe(STORE_CODE);

		ExecutionResult<CartOrder> result = repository.findByGuid(STORE_CODE, CART_ORDER_GUID);

		AssertExecutionResult.assertExecutionResult(result)
				.isSuccessful()
				.data(cartOrder);
	}

	/**
	 * Test the behaviour of find by guid.
	 */
	@Test
	public void testFindByStoreCodeAndGuidAsSingle() {
		allowingCartOrderStoreCodeToBe(STORE_CODE);

		repository.findByGuidAsSingle(STORE_CODE, CART_ORDER_GUID)
				.test()
				.assertNoErrors()
				.assertValue(cartOrder);
	}

	/**
	 * Test the behaviour of find by guid not found.
	 */
	@Test
	public void testFindByStoreCodeAndGuidNotFound() {

		when(cartOrderService.findByStoreCodeAndGuid(STORE_CODE, CART_ORDER_GUID)).thenReturn(null);

		ExecutionResult<CartOrder> result = repository.findByGuid(STORE_CODE, CART_ORDER_GUID);

		AssertExecutionResult.assertExecutionResult(result)
				.isFailure()
				.resourceStatus(ResourceStatus.NOT_FOUND);
	}

	/**
	 * Test the behaviour of find by guid not found.
	 */
	@Test
	public void testFindByStoreCodeAndGuidNotFoundAsSingle() {
		String errorMsg = String.format("No cart order with GUID %s was found in store %s.", CART_ORDER_GUID, STORE_CODE);
		when(cartOrderService.findByStoreCodeAndGuid(STORE_CODE, CART_ORDER_GUID)).thenReturn(null);

		repository.findByGuidAsSingle(STORE_CODE, CART_ORDER_GUID)
				.test()
				.assertError(ResourceOperationFailure.notFound(errorMsg));
	}

	/**
	 * Test the behaviour of find by cart guid.
	 */
	@Test
	public void testFindByCartGuid() {

		when(cartOrderService.findByShoppingCartGuid(CART_GUID)).thenReturn(cartOrder);

		repository.findByCartGuidSingle(CART_GUID)
				.test()
				.assertValue(cartOrder);
	}

	/**
	 * Test the behaviour of find by cart guid when not found.
	 */
	@Test
	public void testFindByCartGuidWhenNotFound() {

		when(cartOrderService.findByShoppingCartGuid(CART_GUID)).thenReturn(null);

		repository.findByCartGuidSingle(CART_GUID)
				.test()
				.assertError(ResourceOperationFailure.notFound(String.format(ORDER_WITH_CART_GUID_NOT_FOUND, CART_GUID)));
	}

	/**
	 * Test the behaviour of find by shipment details id.
	 */
	@Test
	public void testFindByShipmentDetailsId() {

		when(cartOrderService.findByStoreCodeAndGuid(STORE_CODE, CART_ORDER_GUID)).thenReturn(cartOrder);
		allowingCartOrderStoreCodeToBe(STORE_CODE);

		repository.findByShipmentDetailsId(STORE_CODE, createShipmentDetailsId(CART_ORDER_GUID, DELIVERY_ID))
				.test()
				.assertNoErrors()
				.assertValue(cartOrder);
	}

	/**
	 * Test the behaviour of find by shipment details id with invalid id.
	 */
	@Test
	public void testFindByShipmentDetailsIdWithInvalidId() {
		when(cartOrderService.findByStoreCodeAndGuid(STORE_CODE, INVALID_CART_ORDER_ID)).thenReturn(null);

		String errorMsg = String.format(CartOrderRepositoryImpl.ORDER_WITH_GUID_NOT_FOUND, INVALID_CART_ORDER_ID, STORE_CODE);
		repository.findByShipmentDetailsId(STORE_CODE, createShipmentDetailsId(INVALID_CART_ORDER_ID, DELIVERY_ID))
				.test()
				.assertError(createErrorCheckPredicate(errorMsg, ResourceStatus.NOT_FOUND));
	}

	/**
	 * Test the behaviour of find cart order guids by customer.
	 */
	@Test
	public void testFindCartOrderGuidsByCustomer() {
		List<String> listOfCartOrderGuids = Collections.singletonList(CART_ORDER_GUID);
		when(cartOrderService.findCartOrderGuidsByCustomerGuid(STORE_CODE, CUSTOMER_GUID)).thenReturn(listOfCartOrderGuids);

		ExecutionResult<Collection<String>> result = repository.findCartOrderGuidsByCustomer(STORE_CODE, CUSTOMER_GUID);

		AssertExecutionResult.assertExecutionResult(result)
				.isSuccessful()
				.data(listOfCartOrderGuids);
	}

	/**
	 * Test the behaviour of find cart order guids by customer.
	 */
	@Test
	public void testFindCartOrderGuidsByCustomerAsObservable() {
		List<String> listOfCartOrderGuids = Collections.singletonList(CART_ORDER_GUID);
		when(cartOrderService.findCartOrderGuidsByCustomerGuid(STORE_CODE, CUSTOMER_GUID)).thenReturn(listOfCartOrderGuids);

		repository.findCartOrderGuidsByCustomerAsObservable(STORE_CODE, CUSTOMER_GUID)
				.test()
				.assertNoErrors()
				.assertValueSequence(listOfCartOrderGuids);
	}

	/**
	 * Test the behaviour of find cart order guids by customer when none found.
	 */
	@Test
	public void testFindCartOrderGuidsByCustomerWhenNoneFound() {
		when(cartOrderService.findCartOrderGuidsByCustomerGuid(STORE_CODE, CUSTOMER_GUID)).thenReturn(null);

		ExecutionResult<Collection<String>> result = repository.findCartOrderGuidsByCustomer(STORE_CODE, CUSTOMER_GUID);

		AssertExecutionResult.assertExecutionResult(result)
				.isFailure()
				.resourceStatus(ResourceStatus.NOT_FOUND);
	}

	/**
	 * Test the behaviour of find cart order guids by customer when none found.
	 */
	@Test
	public void testFindCartOrderGuidsByCustomerWhenNoneFoundAsObservable() {
		String errorMsg = String.format("No cart orders for customer with GUID %s were found in store %s.", CUSTOMER_GUID, STORE_CODE);
		when(cartOrderService.findCartOrderGuidsByCustomerGuid(STORE_CODE, CUSTOMER_GUID)).thenReturn(null);

		repository.findCartOrderGuidsByCustomerAsObservable(STORE_CODE, CUSTOMER_GUID)
				.test()
				.assertError(ResourceOperationFailure.notFound(errorMsg));
	}

	/**
	 * Test the behaviour of get billing address.
	 */
	@Test
	public void testGetBillingAddress() {
		final Address address = mock(Address.class);
		when(cartOrderService.getBillingAddress(cartOrder)).thenReturn(address);

		repository.getBillingAddress(cartOrder)
				.test()
				.assertNoErrors()
				.assertValue(address);
	}

	/**
	 * Test the behaviour of get billing address when none found.
	 */
	@Test
	public void testGetBillingAddressWhenNoneFound() {
		when(cartOrderService.getBillingAddress(cartOrder)).thenReturn(null);

		repository.getBillingAddress(cartOrder)
				.test()
				.assertNoErrors()
				.assertNoValues();
	}

	/**
	 * Test the behaviour of save cart order.
	 */
	@Test
	public void testSaveCartOrder() {
		final CartOrder savedCartOrder = mock(CartOrder.class, "saved");
		when(cartOrderService.saveOrUpdate(cartOrder)).thenReturn(savedCartOrder);

		ExecutionResult<CartOrder> result = repository.saveCartOrder(cartOrder);

		AssertExecutionResult.assertExecutionResult(result)
				.isSuccessful()
				.data(savedCartOrder);
	}

	/**
	 * Test the behaviour of save cart order.
	 */
	@Test
	public void testSaveCartOrderAsSingle() {
		final CartOrder savedCartOrder = mock(CartOrder.class, "saved");
		when(cartOrderService.saveOrUpdate(cartOrder)).thenReturn(savedCartOrder);

		repository.saveCartOrderAsSingle(cartOrder)
				.test()
				.assertNoErrors()
				.assertValue(savedCartOrder);
	}

	/**
	 * Test the behaviour of save cart order with exception.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testSaveCartOrderWithException() {
		when(cartOrderService.saveOrUpdate(cartOrder)).thenThrow(PersistenceException.class);

		ExecutionResult<CartOrder> result = repository.saveCartOrder(cartOrder);

		AssertExecutionResult.assertExecutionResult(result)
				.isFailure()
				.resourceStatus(ResourceStatus.SERVER_ERROR);
	}

	/**
	 * Test the behaviour of save cart order with exception.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testSaveCartOrderWithExceptionAsSingle() {
		when(cartOrderService.saveOrUpdate(cartOrder)).thenThrow(PersistenceException.class);

		repository.saveCartOrderAsSingle(cartOrder)
				.test()
				.assertError(PersistenceException.class);
	}

	/**
	 * Test the behaviour of get enriched shopping cart.
	 */
	@Test
	public void testGetEnrichedShoppingCart() {
		final ShoppingCart shoppingCart = mock(ShoppingCart.class);
		final ShoppingCart enrichedShoppingCart = mock(ShoppingCart.class, "enriched");
		when(cartOrderService.findByStoreCodeAndGuid(STORE_CODE, CART_ORDER_GUID)).thenReturn(cartOrder);
		allowingCartOrderStoreCodeToBe(STORE_CODE);
		when(shoppingCartRepository.getShoppingCart(CART_GUID)).thenReturn(Single.just(shoppingCart));
		when(cartOrderCouponService.populateCouponCodesOnShoppingCart(shoppingCart, cartOrder)).thenReturn(enrichedShoppingCart);
		when(cartOrderShippingService.populateAddressAndShippingFields(enrichedShoppingCart, cartOrder)).thenReturn(enrichedShoppingCart);

		ExecutionResult<ShoppingCart> result = repository.getEnrichedShoppingCart(STORE_CODE, CART_ORDER_GUID,
				CartOrderRepository.FindCartOrder.BY_ORDER_GUID);

		AssertExecutionResult.assertExecutionResult(result)
				.isSuccessful()
				.data(enrichedShoppingCart);
	}

	/**
	 * Test the behaviour of get enriched shopping cart when order not found.
	 */
	@Test
	public void testGetEnrichedShoppingCartWhenOrderNotFound() {
		when(cartOrderService.findByStoreCodeAndGuid(STORE_CODE, CART_ORDER_GUID)).thenReturn(null);

		ExecutionResult<ShoppingCart> result = repository.getEnrichedShoppingCart(STORE_CODE, CART_ORDER_GUID,
				CartOrderRepository.FindCartOrder.BY_ORDER_GUID);

		AssertExecutionResult.assertExecutionResult(result)
				.isFailure()
				.resourceStatus(ResourceStatus.NOT_FOUND);
	}

	/**
	 * Test the behaviour of get enriched shopping cart when shopping cart not found.
	 */
	@Test
	public void testGetEnrichedShoppingCartWhenShoppingCartNotFound() {
		when(cartOrderService.findByStoreCodeAndGuid(STORE_CODE, CART_ORDER_GUID)).thenReturn(cartOrder);
		allowingCartOrderStoreCodeToBe(STORE_CODE);
		when(shoppingCartRepository.getShoppingCart(CART_GUID)).thenReturn(Single.error(ResourceOperationFailure.notFound("not found")));
		ExecutionResult<ShoppingCart> result = repository.getEnrichedShoppingCart(STORE_CODE, CART_ORDER_GUID,
					CartOrderRepository.FindCartOrder.BY_ORDER_GUID);

		AssertExecutionResult.assertExecutionResult(result)
				.isFailure()
				.resourceStatus(ResourceStatus.NOT_FOUND);

	}

	@Test
	public void testUpdateShippingAddressReturnsTrueWhenAddressIsUpdated() {

		when(cartOrderService.findByStoreCodeAndGuid(STORE_CODE, CART_ORDER_GUID)).thenReturn(cartOrder);
		allowingCartOrderStoreCodeToBe(STORE_CODE);
		when(cartOrderShippingService.updateCartOrderShippingAddress(SHIPPING_ADDRESS_GUID, shoppingCart, cartOrder)).thenReturn(true);
		when(shoppingCartRepository.getShoppingCart(CART_GUID)).thenReturn(Single.just(shoppingCart));

		ExecutionResult<Boolean> result = repository.updateShippingAddressOnCartOrder(SHIPPING_ADDRESS_GUID, CART_ORDER_GUID, STORE_CODE);

		verify(cartOrderService, times(1)).saveOrUpdate(cartOrder);
		AssertExecutionResult.assertExecutionResult(result)
				.isSuccessful()
				.data(true);
	}

	@Test
	public void testCartIsUpdatedWhenAddressIsUpdatedAsCompletable() {

		when(cartOrderService.findByStoreCodeAndGuid(STORE_CODE, CART_ORDER_GUID)).thenReturn(cartOrder);
		allowingCartOrderStoreCodeToBe(STORE_CODE);
		when(cartOrderShippingService.updateCartOrderShippingAddress(SHIPPING_ADDRESS_GUID, shoppingCart, cartOrder)).thenReturn(true);

		repository.updateShippingAddressOnCartOrderAsSingle(SHIPPING_ADDRESS_GUID, CART_ORDER_GUID, STORE_CODE).toCompletable()
				.test();

		verify(cartOrderService, times(1)).saveOrUpdate(cartOrder);
	}

	@Test
	public void testUpdateShippingAddressReturnsFalseWhenAddressIsNotUpdated() {

		when(cartOrderService.findByStoreCodeAndGuid(STORE_CODE, CART_ORDER_GUID)).thenReturn(cartOrder);
		allowingCartOrderStoreCodeToBe(STORE_CODE);
		when(cartOrderShippingService.updateCartOrderShippingAddress(SHIPPING_ADDRESS_GUID, shoppingCart, cartOrder)).thenReturn(false);
		when(shoppingCartRepository.getShoppingCart(CART_GUID)).thenReturn(Single.just(shoppingCart));

		ExecutionResult<Boolean> result = repository.updateShippingAddressOnCartOrder(SHIPPING_ADDRESS_GUID, CART_ORDER_GUID, STORE_CODE);

		verify(cartOrderService, times(0)).saveOrUpdate(any(CartOrder.class));
		AssertExecutionResult.assertExecutionResult(result)
				.isSuccessful()
				.data(false);
	}

	@Test
	public void testCartIsNotUpdatedWhenAddressIsNotUpdatedAsCompletable() {

		when(cartOrderService.findByStoreCodeAndGuid(STORE_CODE, CART_ORDER_GUID)).thenReturn(cartOrder);
		allowingCartOrderStoreCodeToBe(STORE_CODE);
		when(cartOrderShippingService.updateCartOrderShippingAddress(SHIPPING_ADDRESS_GUID, shoppingCart, cartOrder)).thenReturn(false);

		repository.updateShippingAddressOnCartOrderAsSingle(SHIPPING_ADDRESS_GUID, CART_ORDER_GUID, STORE_CODE).toCompletable()
				.test();

		verify(cartOrderService, times(0)).saveOrUpdate(any(CartOrder.class));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testUpdateShippingAddressReturnsServerErrorWhenFailsToSave() {

		when(cartOrderService.findByStoreCodeAndGuid(STORE_CODE, CART_ORDER_GUID)).thenReturn(cartOrder);
		allowingCartOrderStoreCodeToBe(STORE_CODE);
		when(cartOrderShippingService.updateCartOrderShippingAddress(SHIPPING_ADDRESS_GUID, shoppingCart, cartOrder)).thenReturn(true);
		when(cartOrderService.saveOrUpdate(cartOrder)).thenThrow(EpPersistenceException.class);
		when(shoppingCartRepository.getShoppingCart(CART_GUID)).thenReturn(Single.just(shoppingCart));

		ExecutionResult<Boolean> result = repository.updateShippingAddressOnCartOrder(SHIPPING_ADDRESS_GUID, CART_ORDER_GUID, STORE_CODE);

		AssertExecutionResult.assertExecutionResult(result)
				.isFailure()
				.resourceStatus(ResourceStatus.SERVER_ERROR);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testUpdateShippingAddressReturnsServerErrorWhenFailsToSaveAsCompletable() {

		when(cartOrderService.findByStoreCodeAndGuid(STORE_CODE, CART_ORDER_GUID)).thenReturn(cartOrder);
		allowingCartOrderStoreCodeToBe(STORE_CODE);

		when(cartOrderShippingService.updateCartOrderShippingAddress(SHIPPING_ADDRESS_GUID, shoppingCart, cartOrder)).thenReturn(true);
		when(cartOrderService.saveOrUpdate(cartOrder)).thenThrow(EpPersistenceException.class);

		repository.updateShippingAddressOnCartOrderAsSingle(SHIPPING_ADDRESS_GUID, CART_ORDER_GUID, STORE_CODE).toCompletable()
				.test()
				.assertError(EpPersistenceException.class);
	}

	@Test
	public void testFilterAndAutoApplyCoupons() {
		CartOrder cartOrder = new CartOrderImpl();
		Store store = new StoreImpl();
		String customerEmail = "customerEmail";

		when(cartOrderCouponAutoApplier.filterAndAutoApplyCoupons(any(CartOrder.class), any(Store.class), anyString())).thenReturn(true);

		ExecutionResult<Boolean> booleanExecutionResult = repository.filterAndAutoApplyCoupons(cartOrder, store, customerEmail);

		verify(cartOrderCouponAutoApplier).filterAndAutoApplyCoupons(any(CartOrder.class), any(Store.class), anyString());
		assertTrue(booleanExecutionResult.getData());
	}

	private void allowingValidCartOrder() {
		when(cartOrder.getShoppingCartGuid()).thenReturn(CART_GUID);
	}

	private void allowingCartOrderStoreCodeToBe(final String storeCode) {
		when(cartOrderService.findByStoreCodeAndGuid(storeCode, CART_ORDER_GUID)).thenReturn(cartOrder);
	}

}
