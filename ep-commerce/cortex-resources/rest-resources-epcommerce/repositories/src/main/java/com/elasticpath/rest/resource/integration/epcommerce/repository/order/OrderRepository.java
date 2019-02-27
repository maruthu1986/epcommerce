/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.order;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

import com.elasticpath.domain.cartorder.CartOrder;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.rest.command.ExecutionResult;

/**
 * The facade for operations with orders.
 */
public interface OrderRepository {

	/**
	 * Find by order GUID.
	 *
	 * @param storeCode the store code
	 * @param orderGuid the order GUID
	 * @return ExecutionResult with the order
	 * @deprecated use findByGuidAsSingle
	 */
	@Deprecated
	ExecutionResult<Order> findByGuid(String storeCode, String orderGuid);

	/**
	 * Find by order GUID.
	 *
	 * @param storeCode the store code
	 * @param orderGuid the order GUID
	 * @return Single with the order
	 */
	Single<Order> findByGuidAsSingle(String storeCode, String orderGuid);

	/**
	 * Find all order Ids for Customer GUID.
	 *
	 * @param storeCode the store code
	 * @param customerGuid the customer GUID
	 * @return ExecutionResult with the order Ids
	 */
	Observable<String> findOrderIdsByCustomerGuid(String storeCode, String customerGuid);

	/**
	 * Find by order and Customer GUID.
	 *
	 * @param storeCode the store code
	 * @param orderGuid the order GUID
	 * @param customerGuid the customer GUID
	 * @return Maybe with the order id
	 */
	Maybe<String> findByGuidAndCustomerGuid(String storeCode, String orderGuid, String customerGuid);

	/**
	 * Get cart order given order id.
	 *
	 * @param scope store code
	 * @param orderId order if
	 * @return cart order
	 */
	Single<CartOrder> getOrderByOrderId(String scope, String orderId);

	/**
	 * Find the order sku.
	 *
	 * @param scope                the scope
	 * @param orderId              the orderId
	 * @param guidPathFromRootItem a shopping item can be a root item or a component of a bundle item. This is a list of all guids
	 *                             from the root item to the component item.
	 * @return the order sku
	 */
	Single<OrderSku> findOrderSku(String scope, String orderId, List<String> guidPathFromRootItem);

	/**
	 * Find the product sku of the order sku.
	 *
	 * @param scope                the scope
	 * @param orderId              the orderId
	 * @param guidPathFromRootItem a shopping item can be a root item or a component of a bundle item. This is a list of all guids
	 *                             from the root item to the component item.
	 * @return ProductSku of OrderSku
	 */
	Single<ProductSku> findProductSku(String scope, String orderId, List<String> guidPathFromRootItem);

}
