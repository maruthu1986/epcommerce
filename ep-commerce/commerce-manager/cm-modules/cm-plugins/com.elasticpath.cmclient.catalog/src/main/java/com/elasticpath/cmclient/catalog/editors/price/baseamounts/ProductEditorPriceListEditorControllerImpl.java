/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.catalog.editors.price.baseamounts;

import java.util.Collection;

import com.elasticpath.cmclient.pricelistmanager.controller.impl.PriceListEditorControllerImpl;
import com.elasticpath.common.dto.pricing.BaseAmountDTO;

/**
 *  Extender for {@link PriceListEditorControllerImpl} to used for product editor.
 */
public class ProductEditorPriceListEditorControllerImpl extends PriceListEditorControllerImpl {

	/**
	 * Construct a controller for a certain PriceListDescriptor.
	 * 
	 * @param guid of the PriceListDescriptor
	 */
	public ProductEditorPriceListEditorControllerImpl(final String guid) {
		super(guid);
	}

	@Override
	protected Collection<BaseAmountDTO> getBaseAmounts() {
		return getPriceListService().getBaseAmountsExtWithSkus(getBaseAmountsFilter());
	}

}
