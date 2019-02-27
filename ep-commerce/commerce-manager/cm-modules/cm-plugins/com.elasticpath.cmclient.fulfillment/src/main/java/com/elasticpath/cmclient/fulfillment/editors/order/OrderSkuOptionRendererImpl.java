/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.fulfillment.editors.order;

import java.util.Iterator;
import java.util.Locale;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.skuconfiguration.SkuOptionValue;
import com.elasticpath.service.catalog.ProductSkuLookup;

/**
 * Default implementation of the {@link OrderSkuOptionRenderer} interface.
 */
public class OrderSkuOptionRendererImpl implements OrderSkuOptionRenderer {

	private final ProductSkuLookup skuReader;

	/**
	 * Constructor.
	 *
	 * @param skuReader a product sku reader
	 */
	public OrderSkuOptionRendererImpl(final ProductSkuLookup skuReader) {
		this.skuReader = skuReader;
	}
	
	@Override
	public String getDisplaySkuOptions(final OrderSku orderSku, final Locale locale) {
		final ProductSku sku = getProductSkuLookup().findByGuid(orderSku.getSkuGuid());
		if (sku != null && sku.getOptionValues().size() > 0) {
			final StringBuilder skuOptionValues = new StringBuilder();
			for (final Iterator<SkuOptionValue> optionValueIter = sku.getOptionValues().iterator(); optionValueIter.hasNext();) {
				final SkuOptionValue currOptionValue = optionValueIter.next();
				skuOptionValues.append(currOptionValue.getDisplayName(locale, true));
				if (optionValueIter.hasNext()) {
					skuOptionValues.append(", ");
				}
			}
			return skuOptionValues.toString();
		}
		return orderSku.getDisplaySkuOptions();
	}
	
	protected ProductSkuLookup getProductSkuLookup() {
		return skuReader;
	}
}
