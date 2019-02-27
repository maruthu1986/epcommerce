/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.price.impl;

import io.reactivex.Single;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.domain.catalog.Price;
import com.elasticpath.repository.Repository;
import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.definition.prices.ItemPriceEntity;
import com.elasticpath.rest.definition.prices.PriceForItemIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.price.PriceRepository;
import com.elasticpath.rest.resource.integration.epcommerce.transform.MoneyTransformer;

/**
 * Repository that implements retrieving price for an item.
 *
 * @param <E> extends ItemPriceEntity
 * @param <I> extends PriceForItemIdentifier
 */
@Component
public class ItemPriceEntityRepositoryImpl<E extends ItemPriceEntity, I extends PriceForItemIdentifier>
		implements Repository<ItemPriceEntity, PriceForItemIdentifier> {

	private MoneyTransformer moneyTransformer;
	private PriceRepository priceRepository;

	@Override
	public Single<ItemPriceEntity> findOne(final PriceForItemIdentifier priceForItemIdentifier) {
		ItemIdentifier itemIdentifier = priceForItemIdentifier.getItem();
		String skuCode = itemIdentifier.getItemId().getValue().get(ItemRepository.SKU_CODE_KEY);
		String scope = itemIdentifier.getItems().getScope().getValue();
		return priceRepository.getPrice(scope, skuCode)
				.map(this::getItemPriceEntity);
	}

	private ItemPriceEntity getItemPriceEntity(final Price price) {
		return ItemPriceEntity.builder()
				.addingListPrice(moneyTransformer.transformToEntity(price.getListPrice()))
				.addingPurchasePrice(moneyTransformer.transformToEntity(price.getLowestPrice()))
				.build();
	}

	@Reference
	public void setMoneyTransformer(final MoneyTransformer moneyTransformer) {
		this.moneyTransformer = moneyTransformer;
	}

	@Reference
	public void setPriceRepository(final PriceRepository priceRepository) {
		this.priceRepository = priceRepository;
	}
}
