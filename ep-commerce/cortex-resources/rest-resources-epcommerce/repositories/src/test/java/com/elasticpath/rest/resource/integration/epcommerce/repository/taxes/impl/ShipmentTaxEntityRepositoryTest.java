/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.integration.epcommerce.repository.taxes.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.PURCHASE_ID;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.SHIPMENT_ID;

import io.reactivex.Single;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.convert.ConversionService;

import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.domain.order.impl.PhysicalOrderShipmentImpl;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.definition.purchases.PurchaseIdentifier;
import com.elasticpath.rest.definition.purchases.PurchasesIdentifier;
import com.elasticpath.rest.definition.shipments.ShipmentIdentifier;
import com.elasticpath.rest.definition.shipments.ShipmentsIdentifier;
import com.elasticpath.rest.definition.taxes.ShipmentTaxIdentifier;
import com.elasticpath.rest.definition.taxes.TaxesEntity;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.ShipmentRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.impl.ShipmentRepositoryImpl;

/**
 * Test for {@link ShipmentTaxEntityRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShipmentTaxEntityRepositoryTest {

	@Mock
	private ConversionService conversionService;
	@Mock
	private ShipmentRepository shipmentRepository;

	@InjectMocks
	private ShipmentTaxEntityRepositoryImpl<TaxesEntity, ShipmentTaxIdentifier> repository;

	@Test
	public void findElementInShipmentTaxRepository() {

		PhysicalOrderShipment physicalOrderShipment = new PhysicalOrderShipmentImpl();
		when(shipmentRepository.find(PURCHASE_ID, SHIPMENT_ID))
				.thenReturn(Single.just(physicalOrderShipment));

		TaxesEntity result = mock(TaxesEntity.class);
		when(conversionService.convert(any(), any())).thenReturn(result);

		repository.findOne(getShipmentTaxIdentifier())
				.test()
				.assertNoErrors()
				.assertValue(result);
	}

	@Test
	public void elementNotFoundTest() {
		when(shipmentRepository.find(PURCHASE_ID, SHIPMENT_ID))
				.thenReturn(Single.error(ResourceOperationFailure.notFound(ShipmentRepositoryImpl.SHIPMENT_NOT_FOUND)));

		repository.findOne(getShipmentTaxIdentifier())
				.test()
				.assertError(throwable -> ((ResourceOperationFailure) throwable).getResourceStatus().equals(ResourceStatus.NOT_FOUND));
	}

	private ShipmentTaxIdentifier getShipmentTaxIdentifier() {
		return ShipmentTaxIdentifier.builder()
				.withShipment(ShipmentIdentifier.builder()
						.withShipmentId(StringIdentifier.of(SHIPMENT_ID))
						.withShipments(ShipmentsIdentifier.builder()
								.withPurchase(PurchaseIdentifier.builder()
										.withPurchaseId(StringIdentifier.of(PURCHASE_ID))
										.withPurchases(mock(PurchasesIdentifier.class))
										.build())
								.build())
						.build())
				.build();
	}
}
