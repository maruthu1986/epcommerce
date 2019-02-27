/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.fulfillment.editors.order;

import java.util.Locale;

import com.elasticpath.domain.order.OrderSku;

/**
 * Renderer interface for converting an Order Sku's sku option's to a localized string.
 */
public interface OrderSkuOptionRenderer {
	/**
	 * Gets the locale dependent sku option representation for an order sku.
	 *
	 * @param orderSku the line item
	 * @param locale the locale
	 * @return comma separated string
	 */
	String getDisplaySkuOptions(OrderSku orderSku, Locale locale);
}
