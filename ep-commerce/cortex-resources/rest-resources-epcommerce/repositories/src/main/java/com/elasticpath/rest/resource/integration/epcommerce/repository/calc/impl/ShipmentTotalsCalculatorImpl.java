/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.calc.impl;

import java.util.Set;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import io.reactivex.Single;

import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.shoppingcart.ShoppingItemTaxSnapshot;
import com.elasticpath.money.Money;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.resource.integration.epcommerce.repository.calc.ShipmentTotalsCalculator;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.PricingSnapshotRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.ShipmentRepository;

/**
 * Performs totals calculations for the Shipment.
 */
@Singleton
@Named("shipmentTotalsCalculator")
public class ShipmentTotalsCalculatorImpl implements ShipmentTotalsCalculator {

	/**
	 * Error message when line item not found.
	 */
	public static final String LINE_ITEM_NOT_FOUND = "Line item not found";

	private final ShipmentRepository shipmentRepository;

	private final PricingSnapshotRepository pricingSnapshotRepository;

	/**
	 * Constructor.
	 *
	 * @param shipmentRepository        the shipmentRepository
	 * @param pricingSnapshotRepository pricingSnapshotRepository
	 */
	@Inject
	ShipmentTotalsCalculatorImpl(
			@Named("shipmentRepository")
			final ShipmentRepository shipmentRepository,
			@Named("pricingSnapshotRepository")
			final PricingSnapshotRepository pricingSnapshotRepository) {

		this.shipmentRepository = shipmentRepository;
		this.pricingSnapshotRepository = pricingSnapshotRepository;
	}

	@Override
	public Single<Money> calculateTotalForShipment(final String orderGuid, final String shipmentGuid) {
		return shipmentRepository.find(orderGuid, shipmentGuid)
				.map(OrderShipment::getTotalMoney);
	}

	@Override
	public Single<Money> calculateTotalForLineItem(final String orderGuid, final String shipmentGuid, final String shipmentItemGuid) {
		return shipmentRepository.find(orderGuid, shipmentGuid)
				.flatMap(orderShipment -> getOrderSkuByGuid(orderShipment.getShipmentOrderSkus(), shipmentItemGuid))
				.flatMap(pricingSnapshotRepository::getTaxSnapshotForOrderSku)
				.map(this::getMoney);
	}

	/**
	 * Get OrderSku by SkuGuid.
	 *
	 * @param orderSkus orderSkus
	 * @param skuGuid   skuGuid
	 * @return the orderSku
	 */
	protected Single<OrderSku> getOrderSkuByGuid(final Set<OrderSku> orderSkus, final String skuGuid) {
		if (skuGuid != null) {
			for (OrderSku orderSku : orderSkus) {
				if (skuGuid.equals(orderSku.getGuid())) {
					return Single.just(orderSku);
				}
			}
		}
		return Single.error(ResourceOperationFailure.notFound(LINE_ITEM_NOT_FOUND));
	}

	/**
	 * Get total from ShoppingItemTaxSnapshot.
	 *
	 * @param taxSnapshot ShoppingItemTaxSnapshot
	 * @return money total
	 */
	protected Money getMoney(final ShoppingItemTaxSnapshot taxSnapshot) {
		return taxSnapshot.getTaxPriceCalculator()
				.withCartDiscounts()
				.getMoney();
	}
}
