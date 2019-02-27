/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.integration.epcommerce.repository.taxes.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.PURCHASE_ID;
import static com.elasticpath.rest.resource.integration.epcommerce.repository.ResourceTestConstants.SHIPMENT_ID;

import java.util.Collections;
import java.util.List;

import io.reactivex.Single;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.domain.order.TaxJournalRecord;
import com.elasticpath.plugin.tax.domain.TaxDocumentId;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.ResourceStatus;
import com.elasticpath.rest.definition.purchases.PurchaseIdentifier;
import com.elasticpath.rest.definition.purchases.PurchasesIdentifier;
import com.elasticpath.rest.definition.shipments.ShipmentIdentifier;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemIdentifier;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemsIdentifier;
import com.elasticpath.rest.definition.shipments.ShipmentsIdentifier;
import com.elasticpath.rest.definition.taxes.ShipmentLineItemTaxIdentifier;
import com.elasticpath.rest.definition.taxes.TaxesEntity;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.impl.ShipmentRepositoryImpl;
import com.elasticpath.rest.resource.integration.epcommerce.repository.taxdocument.TaxDocumentRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.taxes.transform.ShipmentLineItemTaxesEntityTransformer;

@RunWith(MockitoJUnitRunner.class)
public class ShipmentLineItemsTaxEntityRepositoryTest {

	private static final String LINE_ITEM_ID = "Line_Item_Id";
	private static final String LINE_ID_NOT_FOUND = "id that doesn't exist";
	private static final String SKU_CODE = "sku Code";

	@Mock
	private ShipmentRepositoryImpl shipmentRepository;
	@Mock
	private ShipmentLineItemTaxesEntityTransformer transformer;
	@Mock
	private TaxDocumentRepository taxDocumentRepository;

	@InjectMocks
	private ShipmentLineItemsTaxEntityRepositoryImpl<TaxesEntity, ShipmentLineItemTaxIdentifier> repository;

	@Test
	public void findElementInShipmentLineItemsTaxEntityRepository() {

		OrderSku orderSku = mock(OrderSku.class);
		when(orderSku.getGuid()).thenReturn(LINE_ITEM_ID);
		when(orderSku.getSkuCode()).thenReturn(SKU_CODE);

		PhysicalOrderShipment physicalOrderShipment = mock(PhysicalOrderShipment.class);
		when(physicalOrderShipment.getShipmentOrderSkus()).thenReturn(Collections.singleton(orderSku));
		TaxDocumentId taxDocument = mock(TaxDocumentId.class);
		when(physicalOrderShipment.getTaxDocumentId()).thenReturn(taxDocument);

		List<TaxJournalRecord> taxRecords = Collections.singletonList(mock(TaxJournalRecord.class));
		TaxesEntity result = mock(TaxesEntity.class);

		when(shipmentRepository.find(PURCHASE_ID, SHIPMENT_ID)).thenReturn(Single.just(physicalOrderShipment));
		when(taxDocumentRepository.getTaxDocument(taxDocument, SKU_CODE)).thenReturn(Single.just(taxRecords));
		when(transformer.transform(orderSku, taxRecords)).thenReturn(Single.just(result));

		ShipmentLineItemTaxIdentifier lineItemTaxIdentifier = getShipmentLineItemTaxIdentifier(LINE_ITEM_ID);
		repository.findOne(lineItemTaxIdentifier)
				.test()
				.assertNoErrors()
				.assertValue(result);
	}

	@Test
	public void lineItemNotFoundTest() {
		ShipmentLineItemTaxIdentifier lineItemTaxIdentifier = getShipmentLineItemTaxIdentifier(LINE_ID_NOT_FOUND);
		PhysicalOrderShipment physicalOrderShipment = mock(PhysicalOrderShipment.class);

		when(physicalOrderShipment.getShipmentOrderSkus()).thenReturn(Collections.emptySet());
		when(shipmentRepository.find(PURCHASE_ID, SHIPMENT_ID)).thenReturn(Single.just(physicalOrderShipment));

		final ResourceOperationFailure errorNotFound =
				ResourceOperationFailure.notFound(ShipmentLineItemsTaxEntityRepositoryImpl.LINE_ITEM_NOT_FOUND);

		repository.findOne(lineItemTaxIdentifier)
				.test()
				.assertError(throwable -> throwable.equals(errorNotFound));
	}

	@Test
	public void taxesNotFoundWhenOrderIsNotFound() {
		ShipmentLineItemTaxIdentifier lineItemTaxIdentifier = getShipmentLineItemTaxIdentifier(LINE_ITEM_ID);

		when(shipmentRepository.find(PURCHASE_ID, SHIPMENT_ID))
				.thenReturn(Single.error(ResourceOperationFailure.notFound(ShipmentRepositoryImpl.SHIPMENT_NOT_FOUND)));

		repository.findOne(lineItemTaxIdentifier)
				.test()
				.assertError(throwable -> {
					ResourceOperationFailure operationFailure = (ResourceOperationFailure) throwable;
					return operationFailure.getResourceStatus().equals(ResourceStatus.NOT_FOUND);
				});
	}

	private ShipmentLineItemTaxIdentifier getShipmentLineItemTaxIdentifier(final String lineItemId) {
		PurchaseIdentifier purchaseIdentifier = PurchaseIdentifier.builder()
				.withPurchaseId(StringIdentifier.of(PURCHASE_ID))
				.withPurchases(mock(PurchasesIdentifier.class)).build();
		ShipmentsIdentifier shipmentsIdentifier = ShipmentsIdentifier.builder()
				.withPurchase(purchaseIdentifier)
				.build();
		ShipmentIdentifier shipmentIdentifier = ShipmentIdentifier.builder()
				.withShipmentId(StringIdentifier.of(SHIPMENT_ID))
				.withShipments(shipmentsIdentifier)
				.build();
		ShipmentLineItemsIdentifier shipmentLineItemsIdentifier = ShipmentLineItemsIdentifier.builder()
				.withShipment(shipmentIdentifier)
				.build();
		ShipmentLineItemIdentifier shipmentLineItemIdentifier = ShipmentLineItemIdentifier.builder()
				.withShipmentLineItemId(StringIdentifier.of(lineItemId))
				.withShipmentLineItems(shipmentLineItemsIdentifier)
				.build();
		return ShipmentLineItemTaxIdentifier.builder()
				.withShipmentLineItem(shipmentLineItemIdentifier)
				.build();
	}

}
