/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.cmclient.core.helpers;

import java.util.Currency;

import com.elasticpath.cmclient.core.ServiceLocator;
import com.elasticpath.common.dto.pricing.BaseAmountDTO;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.money.Money;

/**
 * Helper class to deal with various pricing objects. 
 */
public class PriceHelper {
	
	/**
	 * Creates a price object from a base amount dto and a currency.
	 * @param dto base amount dto
	 * @param priceCurrency currency
	 * @return price
	 */
	public Price createPriceFromBaseAmountDto(final BaseAmountDTO dto, final Currency priceCurrency) {
		int quantity = dto.getQuantity().intValue();
		Money listPrice = Money.valueOf(dto.getListValue(), priceCurrency);
		Price price = createPrice();
		price.setListPrice(listPrice, quantity);

		if (dto.getSaleValue() != null) {
			Money salePrice = Money.valueOf(dto.getSaleValue(), priceCurrency);
			price.setSalePrice(salePrice, quantity);
		}

		return price;
	}

	/**
	 * Retrieves a {@link Price} instance using {@link ServiceLocator#getService(String)}.
	 * @return the new {@link Price} instance
	 */
	protected Price createPrice() {
		return ServiceLocator.getService(ContextIdNames.PRICE);
	}

}
