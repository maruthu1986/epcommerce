/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.impl.lineitems;

import static org.mockito.Mockito.when;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.ErrorCheckPredicate.createErrorCheckPredicate;

import io.reactivex.Single;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemEntity;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.IdentifierTestFactory;
import com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.ShipmentRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.impl.ShipmentRepositoryImpl;

/**
 * Test for {@link LineItemForShipmentRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class LineItemForShipmentRepositoryImplTest {

	private static final String ORDER_NAME = "orderName";
	private static final int QUANTITY = 1;

	private final ShipmentLineItemIdentifier shipmentLineItemIdentifier =
			IdentifierTestFactory.buildShipmentLineItemIdentifier(ResourceTestConstants.SCOPE, ResourceTestConstants.PURCHASE_ID,
			ResourceTestConstants.SHIPMENT_ID, ResourceTestConstants.SHIPMENT_LINE_ITEM_ID);

	@Mock
	private OrderSku orderSku;

	@InjectMocks
	private LineItemForShipmentRepositoryImpl<ShipmentLineItemEntity, ShipmentLineItemIdentifier> repository;

	@Mock
	private ShipmentRepository shipmentRepository;

	@Test
	public void verifyFindOneReturnsNotFoundWhenLineItemNotFound() {
		when(shipmentRepository.getOrderSkuWithParentId(ResourceTestConstants.SCOPE, ResourceTestConstants.PURCHASE_ID, ResourceTestConstants
				.SHIPMENT_ID, ResourceTestConstants.SHIPMENT_LINE_ITEM_ID, null))
				.thenReturn(Single.error(ResourceOperationFailure.notFound(ShipmentRepositoryImpl.SHIPMENT_NOT_FOUND)));

		repository.findOne(shipmentLineItemIdentifier)
				.test()
				.assertError(createErrorCheckPredicate(ShipmentRepositoryImpl.SHIPMENT_NOT_FOUND, ResourceStatus.NOT_FOUND));
	}

	@Test
	public void verifyFindOneReturnsShipmentLineItemEntityWhenLineItemIsFound() {
		when(shipmentRepository.getOrderSkuWithParentId(ResourceTestConstants.SCOPE, ResourceTestConstants.PURCHASE_ID, ResourceTestConstants
				.SHIPMENT_ID, ResourceTestConstants.SHIPMENT_LINE_ITEM_ID, null))
				.thenReturn(Single.just(orderSku));
		when(orderSku.getDisplayName()).thenReturn(ORDER_NAME);
		when(orderSku.getQuantity()).thenReturn(QUANTITY);

		repository.findOne(shipmentLineItemIdentifier)
				.test()
				.assertValue(shipmentLineItemEntity -> shipmentLineItemEntity.getLineItemId().equals(ResourceTestConstants.SHIPMENT_LINE_ITEM_ID)
						&& shipmentLineItemEntity.getPurchaseId().equals(ResourceTestConstants.PURCHASE_ID)
						&& shipmentLineItemEntity.getShipmentId().equals(ResourceTestConstants.SHIPMENT_ID));
	}
}
