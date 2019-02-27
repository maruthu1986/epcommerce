/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.impl.lineitems.options;

import static org.mockito.Mockito.when;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.ErrorCheckPredicate.createErrorCheckPredicate;

import io.reactivex.Single;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemOptionIdentifier;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemOptionValueIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.IdentifierTestFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.ShipmentRepository;

/**
 * Test for {@link ShipmentLineItemOptionValueForOptionRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShipmentLineItemOptionValueForOptionRepositoryImplTest {

	private static final String VALUE = "value";

	private final ShipmentLineItemOptionIdentifier shipmentLineItemOptionIdentifier =
			IdentifierTestFactory.buildShipmentLineItemOptionIdentifier(ResourceTestConstants.SCOPE, ResourceTestConstants.PURCHASE_ID,
					ResourceTestConstants.SHIPMENT_ID, ResourceTestConstants.SHIPMENT_LINE_ITEM_ID, ResourceTestConstants.OPTION_ID);

	@Mock
	private SkuOptionValue skuOptionValue;

	@InjectMocks
	private ShipmentLineItemOptionValueForOptionRepositoryImpl<ShipmentLineItemOptionIdentifier, ShipmentLineItemOptionValueIdentifier> repository;

	@Mock
	private ShipmentRepository shipmentRepository;

	@Test
	public void verifyGetElementsReturnNotFoundWhenProductSkuNotFound() {
		String errorMsg = "Product sku not found";
		when(shipmentRepository.getSkuOptionValue(ResourceTestConstants.PURCHASE_ID, ResourceTestConstants.SHIPMENT_ID, ResourceTestConstants
				.SHIPMENT_LINE_ITEM_ID, ResourceTestConstants.OPTION_ID))
				.thenReturn(Single.error(ResourceOperationFailure.notFound(errorMsg)));

		repository.getElements(shipmentLineItemOptionIdentifier)
				.test()
				.assertError(createErrorCheckPredicate(errorMsg, ResourceStatus.NOT_FOUND));
	}

	@Test
	public void verifyGetElementReturnsShipmentLineItemOptionValueIdentifier() {
		when(shipmentRepository.getSkuOptionValue(ResourceTestConstants.PURCHASE_ID, ResourceTestConstants.SHIPMENT_ID, ResourceTestConstants
				.SHIPMENT_LINE_ITEM_ID, ResourceTestConstants.OPTION_ID))
				.thenReturn(Single.just(skuOptionValue));
		when(skuOptionValue.getGuid()).thenReturn(VALUE);

		repository.getElements(shipmentLineItemOptionIdentifier)
				.test()
				.assertNoErrors()
				.assertValue(shipmentLineItemOptionValueIdentifier ->
						shipmentLineItemOptionValueIdentifier.getShipmentLineItemOptionValueId().getValue().equals(VALUE));
	}
}
