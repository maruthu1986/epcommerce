/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.totals.impl;

import io.reactivex.Single;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.springframework.core.convert.ConversionService;

import com.elasticpath.money.Money;
import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.carts.CartIdentifier;
import com.elasticpath.rest.definition.totals.CartTotalIdentifier;
import com.elasticpath.rest.definition.totals.TotalEntity;
import com.elasticpath.rest.resource.integration.epcommerce.repository.calc.TotalsCalculator;

/**
 * Cart Total Entity Repository.
 *
 * @param <E> the entity type
 * @param <I> the identifier type
 */
@Component
public class CartTotalEntityRepositoryImpl<E extends TotalEntity, I extends CartTotalIdentifier>
		implements Repository<TotalEntity, CartTotalIdentifier> {

	private TotalsCalculator totalsCalculator;
	private ConversionService conversionService;

	@Override
	public Single<TotalEntity> findOne(final CartTotalIdentifier cartTotalIdentifier) {
		final CartIdentifier cartIdentifier = cartTotalIdentifier.getCart();

		String scope = cartIdentifier.getScope().getValue();
		String cartID = cartIdentifier.getCartId().getValue();
		return totalsCalculator.calculateTotalForShoppingCart(scope, cartID)
				.map(this::convertMoneyToTotalEntity);
	}

	/**
	 * Converts given Money to TotalEntity.
	 *
	 * @param money money to convert
	 * @return the converted total entity
	 */
	protected TotalEntity convertMoneyToTotalEntity(final Money money) {
		return conversionService.convert(money, TotalEntity.class);
	}

	@Reference
	public void setTotalsCalculator(final TotalsCalculator totalsCalculator) {
		this.totalsCalculator = totalsCalculator;
	}

	@Reference
	public void setConversionService(final ConversionService conversionService) {
		this.conversionService = conversionService;
	}
}
