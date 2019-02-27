/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.impl;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.annotations.VisibleForTesting;
import io.reactivex.Observable;
import io.reactivex.Single;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.domain.shipping.ShipmentType;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.cache.CacheResult;
import com.elasticpath.rest.resource.integration.epcommerce.repository.order.OrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipment.ShipmentRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.sku.ProductSkuRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.ReactiveAdapter;
import com.elasticpath.service.order.OrderService;

/**
 * The facade for {@link OrderShipment} related operations.
 */
@Singleton
@Named("shipmentRepository")
public class ShipmentRepositoryImpl implements ShipmentRepository {

	/**
	 * Error message when shipment not found.
	 */
	@VisibleForTesting
	public static final String SHIPMENT_NOT_FOUND = "Shipment not found";

	/**
	 * Error message when line item not found.
	 */
	static final String LINE_ITEM_NOT_FOUND = "Line item not found";

	private static final String VALUE_NOT_FOUND = "Sku option value not found.";

	private final OrderService orderService;

	private final OrderRepository orderRepository;

	private final ReactiveAdapter reactiveAdapter;

	private final ProductSkuRepository productSkuRepository;

	/**
	 * Initialize.
	 *
	 * @param orderService the order service
	 * @param orderRepository the order repository
	 * @param reactiveAdapter the reactive adapter
	 * @param productSkuRepository the product sku repository
	 */
	@Inject
	public ShipmentRepositoryImpl(
			@Named("orderService")
			final OrderService orderService,
			@Named("orderRepository")
			final OrderRepository orderRepository,
			@Named("reactiveAdapter")
			final ReactiveAdapter reactiveAdapter,
			@Named("productSkuRepository")
			final ProductSkuRepository productSkuRepository) {

		this.orderService = orderService;
		this.orderRepository = orderRepository;
		this.reactiveAdapter = reactiveAdapter;
		this.productSkuRepository = productSkuRepository;
	}

	@Override
	public Single<PhysicalOrderShipment> find(final String orderId, final String shipmentId) {
		return getOrderShipment(shipmentId)
				.flatMap(orderShipment -> orderId.equals(orderShipment.getOrder().getGuid())
						? Single.just((PhysicalOrderShipment) orderShipment) : Single.error(ResourceOperationFailure.notFound(SHIPMENT_NOT_FOUND)));
	}

	@CacheResult
	private Single<OrderShipment> getOrderShipment(final String shipmentId) {
		return reactiveAdapter.fromServiceAsSingle(() -> orderService.findOrderShipment(shipmentId, ShipmentType.PHYSICAL), SHIPMENT_NOT_FOUND);
	}

	@Override
	public Observable<PhysicalOrderShipment> findAll(final String storeCode, final String orderGuid) {
		return orderRepository.findByGuidAsSingle(storeCode, orderGuid)
				.flatMapObservable(order -> order.getPhysicalShipments() == null
						? Observable.empty() : Observable.fromIterable(order.getPhysicalShipments()));
	}

	@Override
	public Observable<OrderSku> getOrderSkusForShipment(final String scope, final String purchaseId, final String shipmentId) {
		return find(purchaseId, shipmentId)
				.flatMapObservable(shipment -> Observable.fromIterable(shipment.getShipmentOrderSkus()));
	}

	@Override
	public Single<OrderSku> getOrderSkuWithParentId(final String scope,
													final String purchaseId,
													final String shipmentId,
													final String lineItemId,
													final String parentOrderSkuGuid) {
		return getOrderSku(purchaseId, shipmentId, lineItemId)
				.flatMap(orderSku -> validateOrderSkuAndParentId(parentOrderSkuGuid, orderSku));
	}

	private Single<OrderSku> validateOrderSkuAndParentId(final String parentOrderSkuGuid, final OrderSku orderSku) {
		return isOrderSkuInvalid(parentOrderSkuGuid, orderSku)
				? Single.error(ResourceOperationFailure.notFound(LINE_ITEM_NOT_FOUND)) : Single.just(orderSku);
	}

	private boolean isOrderSkuInvalid(final String parentOrderSkuGuid, final OrderSku orderSku) {
		return (!isTopLevel(parentOrderSkuGuid, orderSku) && !isBundleComponent(orderSku))
				|| (parentOrderSkuGuid != null && !parentOrderSkuGuid.equals(orderSku.getParent().getGuid()));
	}

	@Override
	public Single<OrderSku> getOrderSku(final String purchaseId, final String shipmentId, final String lineItemId) {
		return find(purchaseId, shipmentId)
				.flatMapObservable(shipment -> Observable.fromIterable(shipment.getShipmentOrderSkus()))
				.filter(orderSku -> orderSku.getGuid().equals(lineItemId))
				.firstOrError()
				.onErrorResumeNext(Single.error(ResourceOperationFailure.notFound(LINE_ITEM_NOT_FOUND)));
	}

	@Override
	public Single<ProductSku> getProductSku(final String purchaseId, final String shipmentId, final String lineItemId) {
		return getOrderSku(purchaseId, shipmentId, lineItemId)
				.flatMap(orderSku -> productSkuRepository.getProductSkuWithAttributesByGuidAsSingle(orderSku.getSkuGuid()));
	}

	@Override
	public Single<SkuOptionValue> getSkuOptionValue(final String purchaseId, final String shipmentId, final String lineItemId,
													final String optionId) {
		return getProductSku(purchaseId, shipmentId, lineItemId)
				.flatMap(productSku -> reactiveAdapter.fromNullableAsSingle(() -> productSku.getOptionValueMap().get(optionId), VALUE_NOT_FOUND));
	}

	private boolean isTopLevel(final String parentOrderSkuGuid, final OrderSku orderSku) {
		return parentOrderSkuGuid == null && orderSku.getParent() == null;
	}

	private boolean isBundleComponent(final OrderSku orderSku) {
		return orderSku.getParent() != null;
	}
}