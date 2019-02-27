/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.calc.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Currency;

import io.reactivex.Single;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.util.collections.Sets;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.domain.shoppingcart.PriceCalculator;
import com.elasticpath.domain.shoppingcart.ShoppingItemTaxSnapshot;
import com.elasticpath.domain.shoppingcart.TaxPriceCalculator;
import com.elasticpath.money.Money;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.PricingSnapshotRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.ShipmentRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.impl.ShipmentRepositoryImpl;

/**
 * Test for {@link ShipmentTotalsCalculatorImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShipmentTotalsCalculatorImplTest {

	private static final String EXISTS_GUID = "exists guid";
	private static final String SHIPMENT_GUID = "shipment guid";
	private static final String LINE_ITEM1_GUID = "line item 1 guid";
	private static final String LINE_ITEM2_GUID = "line item 2 guid";
	private static final Money TEN_CAD = Money.valueOf(BigDecimal.TEN, Currency.getInstance("CAD"));
	private static final String SHIPMENT_NOT_FOUND = "Shipment not found";

	@Mock
	private PhysicalOrderShipment physicalOrderShipment;
	@Mock
	private ShipmentRepository shipmentRepository;
	@Mock
	private PricingSnapshotRepository pricingSnapshotRepository;
	@Mock
	private ShoppingItemTaxSnapshot shoppingItemTaxSnapshot;

	private ShipmentTotalsCalculatorImpl calculator;

	@Before
	public void initialize() {
		calculator = new ShipmentTotalsCalculatorImpl(shipmentRepository, pricingSnapshotRepository);
	}

	@Test
	public void testCalculateTotalForShipmentFound() {
		when(physicalOrderShipment.getTotalMoney()).thenReturn(TEN_CAD);
		when(shipmentRepository.find(EXISTS_GUID, SHIPMENT_GUID)).thenReturn(Single.just(physicalOrderShipment));
		calculator.calculateTotalForShipment(EXISTS_GUID, SHIPMENT_GUID)
				.test()
				.assertNoErrors()
				.assertValue(TEN_CAD);
	}

	@Test
	public void testCalculateTotalForShipmentNotFound() {
		when(shipmentRepository.find(EXISTS_GUID, SHIPMENT_GUID))
				.thenReturn(Single.error(ResourceOperationFailure.notFound(ShipmentRepositoryImpl.SHIPMENT_NOT_FOUND)));
		calculator.calculateTotalForShipment(EXISTS_GUID, SHIPMENT_GUID)
				.test()
				.assertError(ResourceOperationFailure.class)
				.assertErrorMessage(SHIPMENT_NOT_FOUND);
	}

	@Test
	public void testCalculateTotalForShipmentLineItemFound() {
		final OrderSku orderSku1 = mock(OrderSku.class);
		final OrderSku orderSku2 = mock(OrderSku.class);
		final TaxPriceCalculator taxPriceCalculator = mock(TaxPriceCalculator.class);
		final PriceCalculator priceCalculator = mock(PriceCalculator.class);

		when(orderSku1.getGuid()).thenReturn(LINE_ITEM1_GUID);
		when(orderSku2.getGuid()).thenReturn(LINE_ITEM2_GUID);
		when(shipmentRepository.find(EXISTS_GUID, SHIPMENT_GUID)).thenReturn(Single.just(physicalOrderShipment));
		when(physicalOrderShipment.getShipmentOrderSkus()).thenReturn(Sets.newSet(orderSku1, orderSku2));
		when(pricingSnapshotRepository.getTaxSnapshotForOrderSku(orderSku2)).thenReturn(Single.just(shoppingItemTaxSnapshot));
		when(shoppingItemTaxSnapshot.getTaxPriceCalculator()).thenReturn(taxPriceCalculator);
		when(taxPriceCalculator.withCartDiscounts()).thenReturn(priceCalculator);
		when(priceCalculator.getMoney()).thenReturn(TEN_CAD);

		calculator.calculateTotalForLineItem(EXISTS_GUID, SHIPMENT_GUID, LINE_ITEM2_GUID)
				.test()
				.assertNoErrors()
				.assertValue(TEN_CAD);
	}

	@Test
	public void testCalculateTotalForShipmentLineItemNotFound() {
		final OrderSku orderSku1 = mock(OrderSku.class);

		when(orderSku1.getGuid()).thenReturn(LINE_ITEM1_GUID);
		when(shipmentRepository.find(EXISTS_GUID, SHIPMENT_GUID)).thenReturn(Single.just(physicalOrderShipment));
		when(physicalOrderShipment.getShipmentOrderSkus()).thenReturn(Sets.newSet(orderSku1));

		calculator.calculateTotalForLineItem(EXISTS_GUID, SHIPMENT_GUID, LINE_ITEM2_GUID)
				.test()
				.assertError(ResourceOperationFailure.class)
				.assertErrorMessage(ShipmentTotalsCalculatorImpl.LINE_ITEM_NOT_FOUND);
	}
}