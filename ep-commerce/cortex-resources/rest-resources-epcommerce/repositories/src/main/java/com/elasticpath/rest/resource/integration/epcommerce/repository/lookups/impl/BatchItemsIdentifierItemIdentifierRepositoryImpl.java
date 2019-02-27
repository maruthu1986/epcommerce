/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.lookups.impl;

import java.util.Map;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.repository.LinksRepository;
import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.definition.items.ItemsIdentifier;
import com.elasticpath.rest.definition.lookups.BatchItemsIdentifier;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.sku.ProductSkuRepository;

/**
 * Item identifier from batch items identifier repository.
 *
 * @param <LI> the linked identifier type
 * @param <I>  the identifier type
 */
@Component
public class BatchItemsIdentifierItemIdentifierRepositoryImpl<LI extends BatchItemsIdentifier, I extends ItemIdentifier>
		implements LinksRepository<BatchItemsIdentifier, ItemIdentifier> {

	private ItemRepository itemRepository;
	private ProductSkuRepository productSkuRepository;

	@Override
	public Observable<ItemIdentifier> getElements(final BatchItemsIdentifier identifier) {

		IdentifierPart<String> scope = identifier.getBatchItemsForm().getLookups().getScope();
		
		return Observable.fromIterable(identifier.getBatchId().getValue())
				.flatMapMaybe(skuCode -> validate(skuCode, scope.getValue()))
				.flatMapMaybe(skuCode -> createItemIdentifier(skuCode, scope))
				.switchIfEmpty(Observable.empty());
	}

	private Maybe<String> validate(final String skuCode, final String scope) {
		return productSkuRepository
				.isDisplayableProductSkuForStore(skuCode, scope)
				.flatMapMaybe(isExisting -> isExisting ? Maybe.just(skuCode) : Maybe.empty());
	}

	private Maybe<ItemIdentifier> createItemIdentifier(final String codeId, final IdentifierPart<String> scope) {

		IdentifierPart<Map<String, String>> itemId = itemRepository.getItemIdMap(codeId);

		if (itemId.getValue().get(ItemRepository.SKU_CODE_KEY).isEmpty()) {
			return Maybe.empty();
		}

		return Maybe.just(ItemIdentifier.builder()
				.withItemId(itemId)
				.withItems(ItemsIdentifier.builder()
						.withScope(scope)
						.build())
				.build());
	}

	@Reference
	public void setItemRepository(final ItemRepository itemRepository) {
		this.itemRepository = itemRepository;
	}

	@Reference
	public void setProductSkuRepository(final ProductSkuRepository productSkuRepository) {
		this.productSkuRepository = productSkuRepository;
	}
}

