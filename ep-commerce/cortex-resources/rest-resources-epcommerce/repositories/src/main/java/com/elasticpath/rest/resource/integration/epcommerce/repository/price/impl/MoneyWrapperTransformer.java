/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.price.impl;

import java.util.Locale;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.elasticpath.money.Money;
import com.elasticpath.rest.definition.base.CostEntity;
import com.elasticpath.rest.definition.prices.CartLineItemPriceEntity;
import com.elasticpath.rest.resource.integration.epcommerce.transform.MoneyTransformer;
import com.elasticpath.rest.resource.transform.AbstractDomainTransformer;

/**
 * Transforms {@link MoneyWrapper} into a {@link com.elasticpath.rest.definition.prices.CartLineItemPriceEntity}, and vice versa.
 */
@Singleton
@Named("moneyWrapperTransformer")
public class MoneyWrapperTransformer extends AbstractDomainTransformer<MoneyWrapper, CartLineItemPriceEntity> {

	private final MoneyTransformer moneyTransformer;

	/**
	 * Default Constructor.
	 *
	 * @param moneyTransformer the money transformer
	 */
	@Inject
	public MoneyWrapperTransformer(
			@Named("moneyTransformer") final MoneyTransformer moneyTransformer) {

		this.moneyTransformer = moneyTransformer;
	}


	@Override
	public MoneyWrapper transformToDomain(final CartLineItemPriceEntity cartLineitemPriceEntity, final Locale locale) {
		throw new UnsupportedOperationException("This operation is not implemented.");
	}

	@Override
	public CartLineItemPriceEntity transformToEntity(final MoneyWrapper lineItemPrice, final Locale locale) {
		Money purchasePrice = lineItemPrice.getPurchasePrice();
		Money listPrice = lineItemPrice.getListPrice();

		CostEntity purchaseCostEntity = moneyTransformer.transformToEntity(purchasePrice, locale);
		CartLineItemPriceEntity.Builder builder = CartLineItemPriceEntity.builder()
				.addingPurchasePrice(purchaseCostEntity);

		if (listPrice != null) {
			builder.addingListPrice(moneyTransformer.transformToEntity(listPrice, locale));
		}
		return builder.build();
	}
}
