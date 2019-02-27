/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.impl.lineitems;

import static org.mockito.Mockito.when;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.ErrorCheckPredicate.createErrorCheckPredicate;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.impl.OrderSkuImpl;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.definition.shipments.ShipmentIdentifier;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.IdentifierTestFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.ShipmentRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.impl.ShipmentRepositoryImpl;

/**
 * Test for {@link LineItemsForShipmentRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class LineItemsForShipmentRepositoryImplTest {

	private static final int NUM_TEST_DATA = 2;

	private final ShipmentIdentifier shipmentIdentifier = IdentifierTestFactory.buildShipmentIdentifier(ResourceTestConstants.SCOPE,
			ResourceTestConstants.PURCHASE_ID, ResourceTestConstants.SHIPMENT_ID);

	@InjectMocks
	private LineItemsForShipmentRepositoryImpl<ShipmentIdentifier, ShipmentLineItemIdentifier> repository;

	@Mock
	private ShipmentRepository shipmentRepository;

	@Test
	public void verifyGetElementsReturnShipmentLineItemIdentifier() {
		List<OrderSku> orderSkus = new ArrayList<>(NUM_TEST_DATA);
		for (int i = 0; i < NUM_TEST_DATA; i++) {
			orderSkus.add(getOrderSku(i));
		}

		when(shipmentRepository.getOrderSkusForShipment(ResourceTestConstants.SCOPE, ResourceTestConstants.PURCHASE_ID,
				ResourceTestConstants.SHIPMENT_ID)).thenReturn(Observable.fromIterable(orderSkus));

		repository.getElements(shipmentIdentifier)
				.test()
				.assertNoErrors()
				.assertValueAt(0, shipmentLineItemIdentifier -> shipmentLineItemIdentifier.getShipmentLineItemId().getValue().equals("0"))
				.assertValueAt(1, shipmentLineItemIdentifier -> shipmentLineItemIdentifier.getShipmentLineItemId().getValue().equals("1"));
	}

	private OrderSku getOrderSku(final int guid) {
		OrderSku orderSku = new OrderSkuImpl();
		orderSku.setGuid(String.valueOf(guid));
		return orderSku;
	}

	@Test
	public void verifyGetElementsReturnNotFoundWhenShipmentNotFound() {
		when(shipmentRepository.getOrderSkusForShipment(ResourceTestConstants.SCOPE, ResourceTestConstants.PURCHASE_ID,
				ResourceTestConstants.SHIPMENT_ID))
				.thenReturn(Observable.error(ResourceOperationFailure.notFound(ShipmentRepositoryImpl.SHIPMENT_NOT_FOUND)));

		repository.getElements(shipmentIdentifier)
				.test()
				.assertError(createErrorCheckPredicate(ShipmentRepositoryImpl.SHIPMENT_NOT_FOUND, ResourceStatus.NOT_FOUND));
	}
}
