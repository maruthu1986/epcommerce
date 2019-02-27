/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.price.impl;

import java.util.Locale;

import io.reactivex.Single;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.money.Money;
import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.prices.PriceForShipmentLineItemIdentifier;
import com.elasticpath.rest.definition.prices.ShipmentLineItemPriceEntity;
import com.elasticpath.rest.definition.purchases.PurchaseIdentifier;
import com.elasticpath.rest.definition.shipments.ShipmentIdentifier;
import com.elasticpath.rest.definition.shipments.ShipmentLineItemIdentifier;
import com.elasticpath.rest.identity.util.SubjectUtil;
import com.elasticpath.rest.resource.ResourceOperationContext;
import com.elasticpath.rest.resource.integration.epcommerce.repository.cartorder.PricingSnapshotRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.ShipmentRepository;
import com.elasticpath.rest.resource.integration.epcommerce.transform.MoneyTransformer;

/**
 * Repository that implements retrieving the shipment price of a lineitem.
 *
 * @param <E> extends ShipmentLineItemPriceEntity
 * @param <I> extends PriceForShipmentLineItemIdentifier
 */
@Component
public class PriceForShipmentLineItemEntityRepositoryImpl<E extends ShipmentLineItemPriceEntity, I extends PriceForShipmentLineItemIdentifier>
		implements
		Repository<ShipmentLineItemPriceEntity, PriceForShipmentLineItemIdentifier> {

	private MoneyTransformer moneyTransformer;
	private ShipmentRepository shipmentRepository;
	private ResourceOperationContext resourceOperationContext;
	private PricingSnapshotRepository pricingSnapshotRepository;

	@Override
	public Single<ShipmentLineItemPriceEntity> findOne(final PriceForShipmentLineItemIdentifier priceForShipmentLineItemIdentifier) {
		Locale locale = SubjectUtil.getLocale(resourceOperationContext.getSubject());
		return getPurchasePriceSingle(priceForShipmentLineItemIdentifier)
				.map(purchasePrice -> moneyTransformer.transformToEntity(purchasePrice, locale))
				.map(purchaseCostEntity -> ShipmentLineItemPriceEntity.builder().addingPurchasePrice(purchaseCostEntity).build());
	}

	private Single<Money> getPurchasePriceSingle(final PriceForShipmentLineItemIdentifier priceForShipmentLineItemIdentifier) {
		return getOrderSkuSingle(priceForShipmentLineItemIdentifier)
				.flatMap(pricingSnapshotRepository::getPricingSnapshotForOrderSku)
				.map(shoppingItemPricingSnapshot -> shoppingItemPricingSnapshot.getPriceCalc().forUnitPrice().withCartDiscounts().getMoney());
	}

	private Single<OrderSku> getOrderSkuSingle(final PriceForShipmentLineItemIdentifier priceForShipmentLineItemIdentifier) {
		ShipmentLineItemIdentifier shipmentLineItemIdentifier = priceForShipmentLineItemIdentifier.getShipmentLineItem();
		String lineItemId = shipmentLineItemIdentifier.getShipmentLineItemId().getValue();
		ShipmentIdentifier shipmentIdentifier = shipmentLineItemIdentifier.getShipmentLineItems().getShipment();
		String shipmentId = shipmentIdentifier.getShipmentId().getValue();
		PurchaseIdentifier purchaseIdentifier = shipmentIdentifier.getShipments().getPurchase();
		String purchaseId = purchaseIdentifier.getPurchaseId().getValue();
		String scope = purchaseIdentifier.getPurchases().getScope().getValue();
		return shipmentRepository.getOrderSkuWithParentId(scope, purchaseId, shipmentId, lineItemId, null);
	}

	@Reference
	public void setMoneyTransformer(final MoneyTransformer moneyTransformer) {
		this.moneyTransformer = moneyTransformer;
	}

	@Reference
	public void setShipmentRepository(final ShipmentRepository shipmentRepository) {
		this.shipmentRepository = shipmentRepository;
	}

	@Reference
	public void setResourceOperationContext(final ResourceOperationContext resourceOperationContext) {
		this.resourceOperationContext = resourceOperationContext;
	}

	@Reference
	public void setPricingSnapshotRepository(final PricingSnapshotRepository pricingSnapshotRepository) {
		this.pricingSnapshotRepository = pricingSnapshotRepository;
	}
}
