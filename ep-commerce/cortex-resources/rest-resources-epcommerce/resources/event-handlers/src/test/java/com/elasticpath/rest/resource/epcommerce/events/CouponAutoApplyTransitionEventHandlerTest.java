/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.epcommerce.events;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.reactivex.Single;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.store.Store;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.command.ExecutionResultFactory;
import com.elasticpath.rest.relos.rs.events.RoleTransitionEvent;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.ShoppingCartRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.store.StoreRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.ExceptionTransformer;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.impl.ReactiveAdapterImpl;

@RunWith(MockitoJUnitRunner.class)
public class CouponAutoApplyTransitionEventHandlerTest {
	private static final String EMAIL = "EMAIL";

	private static final String SHOPPING_CART_GUID = "SHOPPING_CART_GUID";

	private static final String NEW_USER_GUID = "NEW_USER_GUID";

	private static final String STORE_CODE = "STORE_CODE";

	@Mock
	private CartOrderRepository cartOrderRepository;

	@Mock
	private ShoppingCartRepository shoppingCartRepository;

	@Mock
	private StoreRepository storeRepository;

	@Mock
	private ExceptionTransformer exceptionTransformer;

	@Spy
	private final ReactiveAdapterImpl reactiveAdapter = new ReactiveAdapterImpl(exceptionTransformer);

	@InjectMocks
	private CouponAutoApplyTransitionEventHandler couponAutoApplyTransitionEventHandler;

	@Mock
	private RoleTransitionEvent mockEvent;

	@Mock
	private CartOrder mockCartOrder;

	@Mock
	private Store mockStore;

	@Before
	public void setUp() {
		when(mockEvent.getNewUserGuid()).thenReturn(NEW_USER_GUID);

		Customer mockCustomer = mock(Customer.class);
		when(mockCustomer.getEmail()).thenReturn(EMAIL);

		ShoppingCart mockShoppingCart = mock(ShoppingCart.class, Answers.RETURNS_DEEP_STUBS.get());
		when(shoppingCartRepository.getShoppingCartForCustomer(NEW_USER_GUID))
				.thenReturn(Single.just(mockShoppingCart));
		when(mockShoppingCart.getShopper().getCustomer()).thenReturn(mockCustomer);
		when(mockShoppingCart.getGuid()).thenReturn(SHOPPING_CART_GUID);

		when(storeRepository.findStoreAsSingle(STORE_CODE)).thenReturn(Single.just(mockStore));

		when(cartOrderRepository.saveCartOrderAsSingle(mockCartOrder)).thenReturn(Single.just(mockCartOrder));
	}

	@Test
	public void testCartOrderIsSavedWhenCouponAutoApplierUpdatesCartOrder() throws Exception {
		allowingRepositoryToFindCartOrder(true);
		when(cartOrderRepository.filterAndAutoApplyCoupons(mockCartOrder, mockStore, EMAIL)).thenReturn(ExecutionResultFactory.createReadOK(true));

		couponAutoApplyTransitionEventHandler.handleEvent(STORE_CODE, mockEvent);

		verifyMocks();
		verify(reactiveAdapter, atLeastOnce()).fromRepositoryAsSingle(any());
		verify(storeRepository).findStoreAsSingle(STORE_CODE);
		verify(cartOrderRepository).filterAndAutoApplyCoupons(any(CartOrder.class), any(Store.class), eq(EMAIL));
		verify(cartOrderRepository).saveCartOrderAsSingle(mockCartOrder);
	}


	@Test
	public void testCartOrderIsNotSavedWhenNoCouponsAreAutoAppliedToCartOrder() throws Exception {
		allowingRepositoryToFindCartOrder(true);
		when(cartOrderRepository.filterAndAutoApplyCoupons(mockCartOrder, mockStore, EMAIL)).thenReturn(ExecutionResultFactory.createReadOK(false));

		couponAutoApplyTransitionEventHandler.handleEvent(STORE_CODE, mockEvent);

		verifyMocks();
		verify(reactiveAdapter, atLeastOnce()).fromRepositoryAsSingle(any());
		verify(storeRepository).findStoreAsSingle(STORE_CODE);
		verify(cartOrderRepository).filterAndAutoApplyCoupons(any(CartOrder.class), any(Store.class), eq(EMAIL));
		verify(cartOrderRepository, never()).saveCartOrderAsSingle(mockCartOrder);
	}

	@Test
	public void testCartOrderNotSavedWhenCartOrderNotFound() throws Exception {
		allowingRepositoryToFindCartOrder(false);

		couponAutoApplyTransitionEventHandler.handleEvent(STORE_CODE, mockEvent);

		verifyMocks();
		verify(reactiveAdapter, never()).fromRepositoryAsSingle(any());
		verify(storeRepository, never()).findStoreAsSingle(STORE_CODE);
		verify(cartOrderRepository, never()).filterAndAutoApplyCoupons(any(CartOrder.class), any(Store.class), eq(EMAIL));
		verify(cartOrderRepository, never()).saveCartOrderAsSingle(mockCartOrder);
	}

	private void allowingRepositoryToFindCartOrder(final boolean findCartOrder) {
		if (findCartOrder) {
			when(cartOrderRepository.findByCartGuidSingle(SHOPPING_CART_GUID)).thenReturn(Single.just(mockCartOrder));
		} else {
			when(cartOrderRepository.findByCartGuidSingle(SHOPPING_CART_GUID)).thenReturn(Single.error(ResourceOperationFailure.notFound()));
		}
	}

	private void verifyMocks() {
		verify(shoppingCartRepository).getShoppingCartForCustomer(NEW_USER_GUID);
		verify(cartOrderRepository).findByCartGuidSingle(SHOPPING_CART_GUID);
	}

}
