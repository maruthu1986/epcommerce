/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.shipping.impl;

import static org.mockito.Mockito.when;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.ErrorCheckPredicate.createErrorCheckPredicate;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.shipping.impl.ShippingOptionRepositoryImpl
		.SHIPPING_OPTION_SELECTION_OUT_OF_SYNC;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import io.reactivex.Maybe;
import io.reactivex.Single;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.identity.Subject;
import com.elasticpath.rest.identity.TestSubjectFactory;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.CartOrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.impl.CartOrderRepositoryImpl;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipmentdetails.ShipmentDetailsConstants;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipmentdetails.ShipmentDetailsUtil;
import com.elasticpath.service.shipping.ShippingOptionResult;
import com.elasticpath.service.shipping.ShippingOptionService;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;

/**
 * Test that {@link ShippingOptionRepositoryImpl} behaves as expected.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShippingOptionRepositoryImplTest {

	private static final String STORE_CODE = "testStore";
	private static final String ORDER_ID = "testOrderId";
	private static final String DELIVERY_ID = ShipmentDetailsConstants.SHIPMENT_TYPE;
	private static final String SHIPPING_OPTION_CODE = "testShippingOptionCode";
	private static final Locale LOCALE = Locale.CANADA;
	private static final String MISMATCH_SHIPPING_OPTION_CODE = "mismatchShippingOptionCode";
	private static final String OTHER_SHIPPING_OPTION_CODE = "otherShippingOptionCode";
	private static final String USER_ID = "testUserID";


	@Mock
	private CartOrderRepository cartOrderRepository;

	@Mock
	private ShippingOptionService shippingOptionService;

	@Mock
	private ResourceOperationContext resourceOperationContext;

	@Mock
	private ShippingOption shippingOption;

	@Mock
	private Address shippingAddress;

	@Mock
	private ShippingOptionResult shippingOptionResult;

	@Mock
	private CartOrder cartOrder;

	private final Map<String, String> shipmentDetailsId = ShipmentDetailsUtil.createShipmentDetailsId(ORDER_ID, DELIVERY_ID);

	@InjectMocks
	private ShippingOptionRepositoryImpl repository;

	@Before
	public void setUp() {

		final Subject subject = TestSubjectFactory.createWithScopeAndUserIdAndLocale(STORE_CODE, USER_ID, LOCALE);
		when(resourceOperationContext.getSubject()).thenReturn(subject);
		when(shippingOptionService.getShippingOptions(shippingAddress, STORE_CODE, LOCALE)).thenReturn(shippingOptionResult);

		when(shippingOptionResult.getAvailableShippingOptions()).thenReturn(Collections.singletonList(shippingOption));

		when(cartOrderRepository.findByShipmentDetailsId(STORE_CODE, shipmentDetailsId)).thenReturn(Single.just(cartOrder));
		when(cartOrderRepository.getShippingAddress(cartOrder)).thenReturn(Maybe.just(shippingAddress));
		when(shippingOption.getCode()).thenReturn(SHIPPING_OPTION_CODE);

		when(cartOrder.getShippingOptionCode()).thenReturn(SHIPPING_OPTION_CODE);
		when(shippingOption.getCode()).thenReturn(SHIPPING_OPTION_CODE);

	}

	/**
	 * Test the behaviour of find shipping option code for shipment.
	 */
	@Test
	public void testFindShippingOptionCodesForShipment() {

		// when
		repository.findShippingOptionCodesForShipment(STORE_CODE, shipmentDetailsId)
				.test()
				.assertNoErrors()
				.assertValueSet(Collections.singletonList(SHIPPING_OPTION_CODE));
	}

	/**
	 * Test the behavior of find shipping option codes for shipment when cart order not found.
	 */
	@Test
	public void testFindShippingOptionCodesForShipmentWhenCartOrderNotFound() {

		// given
		allowingCartOrderNotFound();

		// when
		String errorMsg = String.format(CartOrderRepositoryImpl.ORDER_WITH_GUID_NOT_FOUND, ORDER_ID, STORE_CODE);
		repository.findShippingOptionCodesForShipment(STORE_CODE, shipmentDetailsId)
				.test()
				.assertError(createErrorCheckPredicate(errorMsg, ResourceStatus.NOT_FOUND));
	}

	/**
	 * Test the behaviour of find by code.
	 */
	@Test
	public void testFindByCode() {

		// when
		repository.findByCode(STORE_CODE, shipmentDetailsId, SHIPPING_OPTION_CODE)
				.test()
				.assertNoErrors()
				.assertValue(shippingOption);
	}

	/**
	 * Test the behaviour of find by guid when not found.
	 */
	@Test
	public void testFindByCodeWhenNotFound() {

		// given
		when(shippingOption.getCode()).thenReturn(OTHER_SHIPPING_OPTION_CODE);

		// when
		repository.findByCode(STORE_CODE, shipmentDetailsId, SHIPPING_OPTION_CODE)
				.test()
				.assertError(createErrorCheckPredicate(ShippingOptionRepositoryImpl.SHIPPING_OPTION_NOT_FOUND, ResourceStatus.NOT_FOUND));
	}

	/**
	 * Test the behaviour of get selected shipping option code for shipment.
	 */
	@Test
	public void testGetSelectedShippingOptionCodeForShipmentDetails() {

		// when
		repository.getSelectedShippingOptionCodeForShipmentDetails(STORE_CODE, shipmentDetailsId)
				.test()
				.assertNoErrors()
				.assertValue(SHIPPING_OPTION_CODE);
	}

	/**
	 * Test the behaviour of get selected shipping option code for shipment when cart order not found.
	 */
	@Test
	public void testGetSelectedShippingOptionCodeForShipmentDetailsWhenCartOrderNotFound() {

		// given
		allowingCartOrderNotFound();

		// when
		String errorMsg = String.format(CartOrderRepositoryImpl.ORDER_WITH_GUID_NOT_FOUND, ORDER_ID, STORE_CODE);
		repository.getSelectedShippingOptionCodeForShipmentDetails(STORE_CODE, shipmentDetailsId)
				.test()
				.assertError(createErrorCheckPredicate(errorMsg, ResourceStatus.NOT_FOUND));
	}

	/**
	 * Test the behaviour of get selected shipping option code for shipment when cart order has no service option code.
	 */
	@Test
	public void testGetSelectedShippingOptionCodeForShipmentDetailsWhenCartOrderHasNoShippingOptionCode() {

		// given
		when(cartOrder.getShippingOptionCode()).thenReturn(null);

		// when
		repository.getSelectedShippingOptionCodeForShipmentDetails(STORE_CODE, shipmentDetailsId)
				.test()
				.assertNoErrors();
	}

	/**
	 * Test the behaviour of get selected shipping option code for shipment when shipping option not found.
	 */
	@Test
	public void testGetSelectedShippingOptionCodeForShipmentDetailsWhenShippingOptionNotFound() {


		// given
		when(shippingOptionResult.getAvailableShippingOptions()).thenReturn(Collections.emptyList());

		// when
		repository.getSelectedShippingOptionCodeForShipmentDetails(STORE_CODE, shipmentDetailsId)
				.test()
				.assertError(createErrorCheckPredicate(SHIPPING_OPTION_SELECTION_OUT_OF_SYNC, ResourceStatus.SERVER_ERROR));
	}

	/**
	 * Test the behaviour of get selected shipping option code for shipment when shipping option not found.
	 */
	@Test
	public void testGetSelectedShippingOptionCodeForShipmentDetailsWhenSelectedShippingOptionAreOutOfSync() {

		// given
		when(shippingOption.getCode()).thenReturn(MISMATCH_SHIPPING_OPTION_CODE);

		// when
		repository.getSelectedShippingOptionCodeForShipmentDetails(STORE_CODE, shipmentDetailsId)
				.test()
				.assertError(createErrorCheckPredicate(SHIPPING_OPTION_SELECTION_OUT_OF_SYNC, ResourceStatus.SERVER_ERROR));
	}


	private void allowingCartOrderNotFound() {
		when(cartOrderRepository.findByShipmentDetailsId(STORE_CODE, shipmentDetailsId)).thenReturn(Single.error(ResourceOperationFailure.notFound(
				String.format(CartOrderRepositoryImpl.ORDER_WITH_GUID_NOT_FOUND, ORDER_ID, STORE_CODE))));

	}

}
