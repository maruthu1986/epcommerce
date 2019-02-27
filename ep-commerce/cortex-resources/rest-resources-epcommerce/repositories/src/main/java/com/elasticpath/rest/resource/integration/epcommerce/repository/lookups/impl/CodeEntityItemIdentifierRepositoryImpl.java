/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.lookups.impl;

import java.util.Map;

import io.reactivex.Single;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.elasticpath.repository.Repository;
import com.elasticpath.rest.ResourceOperationFailure;
import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.definition.items.ItemsIdentifier;
import com.elasticpath.rest.definition.lookups.CodeEntity;
import com.elasticpath.rest.form.SubmitResult;
import com.elasticpath.rest.form.SubmitStatus;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.sku.ProductSkuRepository;

/**
 * Code Entity for Item Identifier Repository.
 *
 * @param <E> the entity type
 * @param <I> the identifier type
 */
@Component
public class CodeEntityItemIdentifierRepositoryImpl<E extends CodeEntity, I extends ItemIdentifier>
		implements Repository<CodeEntity, ItemIdentifier> {

	private static final String MISSING_REQUIRED_REQUEST_BODY = "Code field is missing a value.";
	private static final String ITEM_NOT_FOUND = "Could not find item for item ID.";

	private ItemRepository itemRepository;
	private ProductSkuRepository productSkuRepository;

	@Override
	public Single<CodeEntity> findOne(final ItemIdentifier itemIdentifier) {

		String code = (itemIdentifier.getItemId().getValue()).get(ItemRepository.SKU_CODE_KEY);

		return Single.just(CodeEntity.builder()
				.withCode(code)
				.build());
	}

	@Override
	public Single<SubmitResult<ItemIdentifier>> submit(final CodeEntity entity, final IdentifierPart<String> scope) {

		Single<IdentifierPart<Map<String, String>>> itemIdSingle = validateCodeEntity(entity, scope.getValue())
				.map(codeEntity -> itemRepository.getItemIdMap(codeEntity.getCode()));

		return itemIdSingle.map(itemId -> SubmitResult.<ItemIdentifier>builder()
				.withIdentifier(buildItemIdentifier(scope, itemId))
				.withStatus(SubmitStatus.CREATED)
				.build());
	}

	private ItemIdentifier buildItemIdentifier(final IdentifierPart<String> scope, final IdentifierPart<Map<String, String>> itemId) {
		return ItemIdentifier.builder()
				.withItemId(itemId)
				.withItems(ItemsIdentifier.builder()
						.withScope(scope)
						.build())
				.build();
	}

	/**
	 * Validate code entity.
	 *
	 * @param codeEntity code entity
	 * @return code entity or validation error
	 */
	private Single<CodeEntity> validateCodeEntity(final CodeEntity codeEntity, final String scope) {
		if (codeEntity.getCode().isEmpty()) {
			return Single.error(ResourceOperationFailure.badRequestBody(MISSING_REQUIRED_REQUEST_BODY));
		}
		return productSkuRepository.isDisplayableProductSkuForStore(codeEntity.getCode(), scope)
				.flatMap(isExisting -> isExisting ? Single.just(codeEntity) : Single.error(ResourceOperationFailure.notFound(ITEM_NOT_FOUND)));
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