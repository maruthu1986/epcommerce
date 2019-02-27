/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.price.impl;

import io.reactivex.Single;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.prices.PriceForItemdefinitionIdentifier;
import com.elasticpath.rest.definition.prices.PriceRangeEntity;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.price.PriceRepository;
import com.elasticpath.rest.resource.integration.epcommerce.transform.MoneyTransformer;

/**
 * Repository that implements retrieving price for an itemdefinition.
 *
 * @param <E> extends PriceRangeEntity
 * @param <I> extends PriceForItemdefinitionIdentifier
 */
@Component
public class PriceForItemDefinitionEntityRepositoryImpl<E extends PriceRangeEntity, I extends PriceForItemdefinitionIdentifier> implements
		Repository<PriceRangeEntity, PriceForItemdefinitionIdentifier> {

	private PriceRepository priceRepository;
	private MoneyTransformer moneyTransformer;

	@Override
	public Single<PriceRangeEntity> findOne(final PriceForItemdefinitionIdentifier priceForItemdefinitionIdentifier) {
		String skuCode = priceForItemdefinitionIdentifier.getItemDefinition().getItemId().getValue().get(ItemRepository.SKU_CODE_KEY);
		return priceRepository.getLowestPrice(skuCode)
				.map(price -> moneyTransformer.transformToEntity(price.getLowestPrice()))
				.map(costEntity -> PriceRangeEntity.builder()
						.addingFromPrice(costEntity)
						.build());
	}

	@Reference
	public void setPriceRepository(final PriceRepository priceRepository) {
		this.priceRepository = priceRepository;
	}

	@Reference
	public void setMoneyTransformer(final MoneyTransformer moneyTransformer) {
		this.moneyTransformer = moneyTransformer;
	}
}
