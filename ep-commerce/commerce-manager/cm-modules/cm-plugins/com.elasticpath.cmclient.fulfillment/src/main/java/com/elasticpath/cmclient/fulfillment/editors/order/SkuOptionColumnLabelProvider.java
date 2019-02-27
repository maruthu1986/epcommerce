/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.fulfillment.editors.order;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.cmclient.core.CorePlugin;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.service.catalog.ProductSkuLookup;

/**
 * Column label provider which renders a line item's sku options.
 */
public class SkuOptionColumnLabelProvider extends ColumnLabelProvider {
	private final OrderSkuOptionRenderer skuOptionRenderer;
	
	/**
	 * SkuOptionColumnLabelProvider constructor.
	 */
	public SkuOptionColumnLabelProvider() {
		ProductSkuLookup skuReader = ServiceLocator.getService(ContextIdNames.PRODUCT_SKU_LOOKUP);
		skuOptionRenderer = new OrderSkuOptionRendererImpl(skuReader);
	}
	
	@Override
	public String getText(final Object element) {
		final OrderSku orderSku = (OrderSku) element;
		return skuOptionRenderer.getDisplaySkuOptions(orderSku, CorePlugin.getDefault().getDefaultLocale());
	}
}