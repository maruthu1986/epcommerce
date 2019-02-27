/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.integration.epcommerce.repository.discounts.impl;

import io.reactivex.Single;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.discounts.DiscountEntity;
import com.elasticpath.rest.definition.discounts.DiscountForPurchaseIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.order.OrderRepository;
import com.elasticpath.rest.resource.integration.epcommerce.transform.MoneyTransformer;

/**
 * Discounts Entity Repository.
 *
 * @param <E> the entity type
 * @param <I> the identifier type
 */
@Component
public class DiscountsEntityForPurchaseRepositoryImpl<E extends DiscountEntity, I extends DiscountForPurchaseIdentifier>
		implements Repository<DiscountEntity, DiscountForPurchaseIdentifier> {

	private OrderRepository orderRepository;
	private MoneyTransformer moneyTransformer;

	@Override
	public Single<DiscountEntity> findOne(final DiscountForPurchaseIdentifier identifier) {

		String purchaseId = identifier.getPurchase().getPurchaseId().getValue();
		String scope = identifier.getPurchase().getPurchases().getScope().getValue();

		return orderRepository.findByGuidAsSingle(scope, purchaseId)
				.map(order -> moneyTransformer.transformToEntity(order.getSubtotalDiscountMoney(), order.getLocale()))
				.map(costEntity -> DiscountEntity.builder()
						.addingDiscount(costEntity)
						.build());
	}

	@Reference
	public void setCartOrderRepository(final OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}

	@Reference
	public void setMoneyTransformer(final MoneyTransformer moneyTransformer) {
		this.moneyTransformer = moneyTransformer;
	}

}