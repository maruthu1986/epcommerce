/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.impl.purchases;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.domain.order.impl.PhysicalOrderShipmentImpl;
import com.elasticpath.rest.definition.purchases.PurchaseIdentifier;
import com.elasticpath.rest.definition.shipments.ShipmentIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.IdentifierTestFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.ShipmentRepository;

/**
 * Test for {@link ShipmentsForPurchaseRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShipmentsForPurchaseRepositoryImplTest {

	private static final int NUM_TEST_DATA = 2;

	private final PurchaseIdentifier purchaseIdentifier = IdentifierTestFactory.buildPurchaseIdentifier(
			ResourceTestConstants.SCOPE, ResourceTestConstants.PURCHASE_ID);

	@InjectMocks
	private ShipmentsForPurchaseRepositoryImpl<PurchaseIdentifier, ShipmentIdentifier> repository;

	@Mock
	private ShipmentRepository shipmentRepository;

	@Test
	public void verifyGetElementsReturnsEmptyWhenPhysicalShipmentsAreEmpty() {
		when(shipmentRepository.findAll(ResourceTestConstants.SCOPE, ResourceTestConstants.PURCHASE_ID)).thenReturn(Observable.empty());

		repository.getElements(purchaseIdentifier)
				.test()
				.assertNoErrors()
				.assertNoValues();
	}

	@Test
	public void verifyGetElementsReturnShipmentIdentifier() {
		List<PhysicalOrderShipment> physicalOrderShipments = new ArrayList<>(NUM_TEST_DATA);
		for (int i = 0; i < NUM_TEST_DATA; i++) {
			physicalOrderShipments.add(createPhysicalOrderShipment(i));
		}

		when(shipmentRepository.findAll(ResourceTestConstants.SCOPE, ResourceTestConstants.PURCHASE_ID))
				.thenReturn(Observable.fromIterable(physicalOrderShipments));

		repository.getElements(purchaseIdentifier)
				.test()
				.assertNoErrors()
				.assertValueCount(NUM_TEST_DATA)
				.assertValueAt(0, shipmentIdentifier -> shipmentIdentifier.getShipmentId().getValue().equals("0"))
				.assertValueAt(1, shipmentIdentifier -> shipmentIdentifier.getShipmentId().getValue().equals("1"));
	}

	private PhysicalOrderShipmentImpl createPhysicalOrderShipment(final int shipmentNumber) {
		PhysicalOrderShipmentImpl physicalOrderShipment = new PhysicalOrderShipmentImpl();
		physicalOrderShipment.setShipmentNumber(String.valueOf(shipmentNumber));
		return physicalOrderShipment;
	}
}
