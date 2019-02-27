/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cmclient.core.helpers;

import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.util.Currency;

import org.junit.Before;
import org.junit.Test;

import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.impl.PriceImpl;

/**
 * Tests for {@link PriceHelper}.
 */
public class PriceHelperTest {

	private PriceHelper priceHelper;

	/**
	 * Sets up a stubbed version of the {@link PriceHelper} that can create a price without calling {@link LoginManager}.
	 */
	@Before
	public void setUp() {
		priceHelper = new PriceHelper() {
			@Override
			protected Price createPrice() {
				return new PriceImpl();
			}
		};
	}

	/**
	 * If a {@link BaseAmountDTO} doesn't have a sale price specified, the {@link Price} should still be created successfully, but with a null sale
	 * price.
	 */
	@Test
	public void ensureSalePriceIsNullIfSaleAmountIsNull() {
		Currency currency = Currency.getInstance("USD");
		BaseAmountDTO baseAmountDto = new BaseAmountDTO();
		baseAmountDto.setQuantity(BigDecimal.ONE);
		baseAmountDto.setListValue(BigDecimal.ONE);
		Price price = priceHelper.createPriceFromBaseAmountDto(baseAmountDto, currency);
		assertNull("Sale price should be null because sale amount was null", price.getSalePrice());
	}
}
