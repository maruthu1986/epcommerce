/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.shipmentdetails;

import static org.mockito.Mockito.when;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.shipmentdetails.ShipmentDetailsUtil.createShipmentDetailsId;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.impl.CartOrderRepositoryImpl;

/**
 * Test for {@link ShipmentDetailsIdParameterServiceImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShipmentDetailsIdParameterServiceImplTest {

	private static final String SCOPE = "scope";
	private static final String USER_ID = "userId";
	private static final int NUM_OF_IDS = 2;

	@InjectMocks
	private ShipmentDetailsIdParameterServiceImpl shipmentDetailsIdParameterService;

	@Mock
	private CartOrderRepository cartOrderRepository;

	@Mock
	private ShipmentDetailsService shipmentDetailsService;

	@Test
	public void verifyFindShipmentDetailsIdsReturnsShipmentDetailsIdIdentifierPart() {
		List<String> cartIds = new ArrayList<>(NUM_OF_IDS);
		for (int i = 0; i < NUM_OF_IDS; i++) {
			String cartId = String.valueOf(i);
			cartIds.add(cartId);
			when(shipmentDetailsService.getShipmentDetailsIdForOrder(SCOPE, cartId))
					.thenReturn(Single.just(createShipmentDetailsId(cartId, ShipmentDetailsConstants.SHIPMENT_TYPE)));
		}

		when(cartOrderRepository.findCartOrderGuidsByCustomerAsObservable(SCOPE, USER_ID)).thenReturn(Observable.fromIterable(cartIds));

		shipmentDetailsIdParameterService.findShipmentDetailsIds(SCOPE, USER_ID)
				.test()
				.assertNoErrors()
				.assertValueAt(0, shipmentDetailsId -> shipmentDetailsId.getValue().get(ShipmentDetailsConstants.ORDER_ID).equals("0"))
				.assertValueAt(1, shipmentDetailsId -> shipmentDetailsId.getValue().get(ShipmentDetailsConstants.ORDER_ID).equals("1"));
	}

	@Test
	public void verifyFindShipmentDetailsIdReturnsEmptyWhenCartOrderIdsNotFound() {
		String errorMsg = String.format(CartOrderRepositoryImpl.NO_CART_ORDERS_FOR_CUSTOMER, USER_ID, SCOPE);
		when(cartOrderRepository.findCartOrderGuidsByCustomerAsObservable(SCOPE, USER_ID))
				.thenReturn(Observable.error(ResourceOperationFailure.notFound(errorMsg)));

		shipmentDetailsIdParameterService.findShipmentDetailsIds(SCOPE, USER_ID)
				.test()
				.assertNoErrors()
				.assertNoValues();
	}

	@Test
	public void verifyFindShipmentDetailsIdReturnsEmptyWhenOrderIsNotShippable() {
		List<String> cartIds = new ArrayList<>(NUM_OF_IDS);
		for (int i = 0; i < NUM_OF_IDS; i++) {
			String cartId = String.valueOf(i);
			cartIds.add(cartId);
			when(shipmentDetailsService.getShipmentDetailsIdForOrder(SCOPE, cartId))
					.thenReturn(Single.error(ResourceOperationFailure.notFound(ShipmentDetailsServiceImpl.COULD_NOT_FIND_SHIPMENT)));
		}

		when(cartOrderRepository.findCartOrderGuidsByCustomerAsObservable(SCOPE, USER_ID)).thenReturn(Observable.fromIterable(cartIds));

		shipmentDetailsIdParameterService.findShipmentDetailsIds(SCOPE, USER_ID)
				.test()
				.assertNoErrors()
				.assertNoValues();
	}
}
