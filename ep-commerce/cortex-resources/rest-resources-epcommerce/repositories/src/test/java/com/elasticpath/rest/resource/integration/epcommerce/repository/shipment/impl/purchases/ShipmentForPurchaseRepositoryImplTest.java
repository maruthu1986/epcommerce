/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.impl.purchases;

import static org.mockito.Mockito.when;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.ErrorCheckPredicate.createErrorCheckPredicate;

import io.reactivex.Single;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.order.OrderShipmentStatus;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.definition.shipments.ShipmentEntity;
import com.elasticpath.rest.definition.shipments.ShipmentIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.IdentifierTestFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.ShipmentRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.impl.ShipmentRepositoryImpl;

/**
 * Test for {@link ShipmentForPurchaseRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShipmentForPurchaseRepositoryImplTest {

	private static final String NAME = "name";

	private final ShipmentIdentifier shipmentIdentifier = IdentifierTestFactory.buildShipmentIdentifier(ResourceTestConstants.SCOPE,
			ResourceTestConstants.PURCHASE_ID, ResourceTestConstants.SHIPMENT_ID);

	@Mock
	private PhysicalOrderShipment physicalOrderShipment;

	@Mock
	private OrderShipmentStatus orderShipmentStatus;

	@InjectMocks
	private ShipmentForPurchaseRepositoryImpl<ShipmentEntity, ShipmentIdentifier> repository;

	@Mock
	private ShipmentRepository shipmentRepository;

	@Test
	public void verifyFindOneReturnsNotFoundWhenShipmentNotFound() {
		when(shipmentRepository.find(ResourceTestConstants.PURCHASE_ID, ResourceTestConstants.SHIPMENT_ID))
				.thenReturn(Single.error(ResourceOperationFailure.notFound(ShipmentRepositoryImpl.SHIPMENT_NOT_FOUND)));

		repository.findOne(shipmentIdentifier)
				.test()
				.assertError(createErrorCheckPredicate(ShipmentRepositoryImpl.SHIPMENT_NOT_FOUND, ResourceStatus.NOT_FOUND));
	}

	@Test
	public void verifyFindOneReturnsShipmentEntity() {
		when(shipmentRepository.find(ResourceTestConstants.PURCHASE_ID, ResourceTestConstants.SHIPMENT_ID))
				.thenReturn(Single.just(physicalOrderShipment));
		when(physicalOrderShipment.getShipmentStatus()).thenReturn(orderShipmentStatus);
		when(orderShipmentStatus.getName()).thenReturn(NAME);

		repository.findOne(shipmentIdentifier)
				.test()
				.assertNoErrors()
				.assertValue(shipmentEntity -> shipmentEntity.getStatus().getCode().equals(NAME));
	}
}
