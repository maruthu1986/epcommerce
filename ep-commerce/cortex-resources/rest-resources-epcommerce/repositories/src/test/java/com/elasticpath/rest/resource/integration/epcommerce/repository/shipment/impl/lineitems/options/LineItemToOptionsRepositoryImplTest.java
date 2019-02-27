/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.impl.lineitems.options;

import static org.mockito.Mockito.when;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.ErrorCheckPredicate.createErrorCheckPredicate;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import io.reactivex.Single;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemIdentifier;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemOptionsIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.IdentifierTestFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.ShipmentRepository;

/**
 * Test for {@link LineItemToOptionsRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class LineItemToOptionsRepositoryImplTest {

	private static final String CODE = "code";

	private final Set<String> codes = ImmutableSet.of(CODE);

	private final ShipmentLineItemIdentifier shipmentLineItemIdentifier =
			IdentifierTestFactory.buildShipmentLineItemIdentifier(ResourceTestConstants.SCOPE, ResourceTestConstants.PURCHASE_ID,
					ResourceTestConstants.SHIPMENT_ID, ResourceTestConstants.SHIPMENT_LINE_ITEM_ID);

	@Mock
	private ProductSku productSku;

	@InjectMocks
	private LineItemToOptionsRepositoryImpl<ShipmentLineItemIdentifier, ShipmentLineItemOptionsIdentifier> repository;

	@Mock
	private ShipmentRepository shipmentRepository;

	@Test
	public void verifyGetElementsReturnNotFoundWhenProductSkuNotFound() {
		String errorMsg = "ProductSku not found.";

		when(shipmentRepository.getProductSku(ResourceTestConstants.PURCHASE_ID, ResourceTestConstants.SHIPMENT_ID, ResourceTestConstants
				.SHIPMENT_LINE_ITEM_ID))
				.thenReturn(Single.error(ResourceOperationFailure.notFound(errorMsg)));

		repository.getElements(shipmentLineItemIdentifier)
				.test()
				.assertError(createErrorCheckPredicate(errorMsg, ResourceStatus.NOT_FOUND));
	}

	@Test
	public void verifyGetElementsReturnEmptyWhenNoOptionValueCodesAreFound() {
		when(shipmentRepository.getProductSku(ResourceTestConstants.PURCHASE_ID, ResourceTestConstants.SHIPMENT_ID, ResourceTestConstants
				.SHIPMENT_LINE_ITEM_ID)).thenReturn(Single.just(productSku));
		when(productSku.getOptionValueCodes()).thenReturn(ImmutableSet.of());

		repository.getElements(shipmentLineItemIdentifier)
				.test()
				.assertNoErrors()
				.assertNoValues();
	}

	@Test
	public void verifyGetElementsReturnShipmentLineItemOptionsIdentifier() {
		when(shipmentRepository.getProductSku(ResourceTestConstants.PURCHASE_ID, ResourceTestConstants.SHIPMENT_ID,
				ResourceTestConstants.SHIPMENT_LINE_ITEM_ID)).thenReturn(Single.just(productSku));
		when(productSku.getOptionValueCodes()).thenReturn(codes);

		repository.getElements(shipmentLineItemIdentifier)
				.test()
				.assertNoErrors()
				.assertValue(shipmentLineItemOptionsIdentifier ->
						shipmentLineItemOptionsIdentifier.getShipmentLineItem().getShipmentLineItemId().getValue().equals(ResourceTestConstants
								.SHIPMENT_LINE_ITEM_ID));
	}
}
