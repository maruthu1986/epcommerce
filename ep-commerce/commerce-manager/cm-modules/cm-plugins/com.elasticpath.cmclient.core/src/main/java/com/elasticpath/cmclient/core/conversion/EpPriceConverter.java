/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
/**
 * 
 */
package com.elasticpath.cmclient.core.conversion;

import java.math.BigDecimal;

import org.eclipse.core.databinding.conversion.Converter;

/**
 * The price converter for binding.
 * From string to BigDecimal.
 */
public class EpPriceConverter extends Converter {

	/** 
	 * A default constructor.
	 */
	public EpPriceConverter() {
		super(String.class, BigDecimal.class);

	}

	@Override
	public Object convert(final Object fromObject) {
		String priceStr = (String) fromObject;
		if (priceStr.length() > 0) {
			BigDecimal newPriceValue = new BigDecimal(priceStr);
			return newPriceValue.setScale(2, BigDecimal.ROUND_HALF_UP);
		}
		return null;

	}
}
