/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.shipmentdetails.destinationinfo;

import static org.mockito.Mockito.when;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.shipmentdetails.ShipmentDetailsUtil.createShipmentDetailsId;

import io.reactivex.Maybe;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.definition.addresses.AddressIdentifier;
import com.elasticpath.rest.definition.shipmentdetails.DestinationInfoIdentifier;
import com.elasticpath.rest.id.type.CompositeIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipmentdetails.ShipmentDetailsConstants;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipmentdetails.ShipmentDetailsServiceImpl;

/**
 * Test for {@link AddressForDestinationInfoRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class AddressForDestinationInfoRepositoryImplTest {

	private static final String ORDER_ID = "orderId";
	private static final String SCOPE = "scope";
	private static final String SELECTED_ID = "addressId";

	private final DestinationInfoIdentifier destinationInfoIdentifier = DestinationInfoIdentifier.builder()
			.withScope(StringIdentifier.of(SCOPE))
			.withShipmentDetailsId(CompositeIdentifier.of(createShipmentDetailsId(ORDER_ID, ShipmentDetailsConstants.SHIPMENT_TYPE)))
			.build();

	@InjectMocks
	private AddressForDestinationInfoRepositoryImpl<DestinationInfoIdentifier, AddressIdentifier> repository;

	@Mock
	private DestinationInfoService destinationInfoService;

	@Test
	public void verifyGetElementsReturnsAddressIDentifierWhenSelectedAddressExists() {
		when(destinationInfoService.getSelectedAddressGuidIfShippable(SCOPE, ORDER_ID)).thenReturn(Maybe.just(SELECTED_ID));

		repository.getElements(destinationInfoIdentifier)
				.test()
				.assertNoErrors()
				.assertValue(addressIdentifier -> addressIdentifier.getAddressId().getValue().equals(SELECTED_ID));
	}

	@Test
	public void verifyGetElementsReturnsEmptyWhenThereIsNoSelectedAddress() {
		when(destinationInfoService.getSelectedAddressGuidIfShippable(SCOPE, ORDER_ID)).thenReturn(Maybe.empty());

		repository.getElements(destinationInfoIdentifier)
				.test()
				.assertNoValues();
	}

	@Test
	public void verifyGetElementsReturnEmptyWhenOrderIsNotShippable() {
		when(destinationInfoService.getSelectedAddressGuidIfShippable(SCOPE, ORDER_ID))
				.thenReturn(Maybe.error(ResourceOperationFailure.notFound(ShipmentDetailsServiceImpl.COULD_NOT_FIND_SHIPMENT)));

		repository.getElements(destinationInfoIdentifier)
				.test()
				.assertNoValues();
	}
}
