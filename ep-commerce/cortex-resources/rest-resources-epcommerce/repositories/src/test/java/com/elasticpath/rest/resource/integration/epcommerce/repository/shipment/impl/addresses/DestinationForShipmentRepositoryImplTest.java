/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.impl.addresses;

import static org.mockito.Mockito.when;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.ErrorCheckPredicate.createErrorCheckPredicate;

import io.reactivex.Single;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.order.OrderAddress;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.definition.addresses.AddressEntity;
import com.elasticpath.rest.definition.shipments.ShipmentIdentifier;
import com.elasticpath.rest.resource.integration.commons.addresses.transform.AddressTransformer;
import com.elasticpath.rest.resource.integration.epcommerce.repository.IdentifierTestFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.ShipmentRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.impl.ShipmentRepositoryImpl;

/**
 * Test for {@link DestinationForShipmentRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class DestinationForShipmentRepositoryImplTest {

	private final ShipmentIdentifier shipmentIdentifier = IdentifierTestFactory.buildShipmentIdentifier(ResourceTestConstants.SCOPE,
			ResourceTestConstants.PURCHASE_ID, ResourceTestConstants.SHIPMENT_ID);

	@Mock
	private AddressEntity addressEntity;

	@Mock
	private OrderAddress orderAddress;

	@Mock
	private PhysicalOrderShipment physicalOrderShipment;

	@InjectMocks
	private DestinationForShipmentRepositoryImpl<AddressEntity, ShipmentIdentifier> repository;

	@Mock
	private ShipmentRepository shipmentRepository;

	@Mock
	private AddressTransformer addressTransformer;

	@Test
	public void verifyFindOneReturnsNotFoundWhenOrderShipmentIsNotFound() {
		when(shipmentRepository.find(ResourceTestConstants.PURCHASE_ID, ResourceTestConstants.SHIPMENT_ID))
				.thenReturn(Single.error(ResourceOperationFailure.notFound(ShipmentRepositoryImpl.SHIPMENT_NOT_FOUND)));

		repository.findOne(shipmentIdentifier)
				.test()
				.assertError(createErrorCheckPredicate(ShipmentRepositoryImpl.SHIPMENT_NOT_FOUND, ResourceStatus.NOT_FOUND));
	}

	@Test
	public void verifyFindOneReturnsAddressEntityWhenOrderShipmentIsFound() {
		when(shipmentRepository.find(ResourceTestConstants.PURCHASE_ID, ResourceTestConstants.SHIPMENT_ID))
				.thenReturn(Single.just(physicalOrderShipment));
		when(physicalOrderShipment.getShipmentAddress()).thenReturn(orderAddress);
		when(addressTransformer.transformAddressToEntity(orderAddress)).thenReturn(addressEntity);

		repository.findOne(shipmentIdentifier)
				.test()
				.assertNoErrors()
				.assertValue(addressEntity);
	}
}
