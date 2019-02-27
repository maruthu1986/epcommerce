/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.id.type.CompositeIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipmentdetails.ShipmentDetailsConstants;
import com.elasticpath.rest.resource.integration.epcommerce.repository.shipmentdetails.ShipmentDetailsUtil;

/**
 * Constants for resource tests.
 */
public final class ResourceTestConstants {
	/**
	 * Test data for scope.
	 */
	public static final String SCOPE = "scope";
	/**
	 * Test data for purchase id.
	 */
	public static final String PURCHASE_ID = "purchaseId";
	/**
	 * Test data for shipment id.
	 */
	public static final String SHIPMENT_ID = "shipmentId";
	/**
	 * Test data for shipment line item id.
	 */
	public static final String SHIPMENT_LINE_ITEM_ID = "shipmentLineItemId";
	/**
	 * Test data for option id.
	 */
	public static final String OPTION_ID = "optionId";
	/**
	 * Test data for sku code.
	 */
	public static final String SKU_CODE = "skuCode";
	/**
	 * Test data for line item id.
	 */
	public static final String LINE_ITEM_ID = "lineItemId";
	/**
	 * Test data for cart id.
	 */
	public static final String CART_ID = "cartId";
	/**
	 * Test data for cart order guid.
	 */
	public static final String CART_ORDER_GUID = "cartOrderGuid";
	/**
	 * Test not found message.
	 */
	public static final String NOT_FOUND = "notFound";
	/**
	 * Test server error message.
	 */
	public static final String SERVER_ERROR = "serverError";
	/**
	 * Test data for coupon id.
	 */
	public static final String COUPON_CODE = "couponCode";
	/**
	 * Test data for order id.
	 */
	public static final String ORDER_ID = "orderId";
	/**
	 * Test data for shipping option id.
	 */
	public static final String SHIPPING_OPTION_ID = "shippingOptionId";
	/**
	 * Test data for user id.
	 */
	public static final String USER_ID = "userId";
	/**
	 * Test data for promotion id.
	 */
	public static final String PROMOTION_ID = "promotionId";
	/**
	 * Test data for favorites id.
	 */
	public static final String WISHLIST_ID = "wishlistId";
	/**
	 * Test data for email.
	 */
	public static final String EMAIL = "harry.potter@elasticpath.com";
	/**
	 * Test data for shipment details id.
	 */
	public static final Map<String, String> SHIPMENT_DETAILS_ID = ShipmentDetailsUtil.createShipmentDetailsId(ORDER_ID,
			ShipmentDetailsConstants.SHIPMENT_TYPE);
	/**
	 * Test identifier part for scope.
	 */
	public static final IdentifierPart<String> SCOPE_IDENTIFIER_PART = StringIdentifier.of(SCOPE);
	/**
	 * Test identifier part for promotion id.
	 */
	public static final IdentifierPart<String> PROMOTION_IDENTIFIER_PART = StringIdentifier.of(PROMOTION_ID);
	/**
	 * Item id map, indexed with the established sku code key.
	 */
	public static final Map<String, String> ITEM_ID_MAP = ImmutableMap.of(ItemRepository.SKU_CODE_KEY, SKU_CODE);
	/**
	 * Test identifier part for item id.
	 */
	public static final IdentifierPart<Map<String, String>> ITEM_IDENTIFIER_PART = CompositeIdentifier.of(ITEM_ID_MAP);
	/**
	 * Test Cart guid.
	 */
	public static final String CART_GUID = "cart_guid";


	private ResourceTestConstants() {
	}
}