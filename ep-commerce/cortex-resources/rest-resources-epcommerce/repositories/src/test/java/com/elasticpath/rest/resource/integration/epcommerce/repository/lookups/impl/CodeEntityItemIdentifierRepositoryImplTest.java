/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.integration.epcommerce.repository.lookups.impl;

import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Single;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.definition.items.ItemsIdentifier;
import com.elasticpath.rest.definition.lookups.CodeEntity;
import com.elasticpath.rest.id.IdentifierPart;
import com.elasticpath.rest.id.type.CompositeIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.sku.ProductSkuRepository;

/**
 * Test for {@link CodeEntityItemIdentifierRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CodeEntityItemIdentifierRepositoryImplTest {

	private static final String SCOPE = "SCOPE";
	private static final String ITEM_ID = "pillow-id";

	@Mock
	private ItemRepository itemRepository;
	@Mock
	private ProductSkuRepository productSkuRepository;

	@InjectMocks
	private CodeEntityItemIdentifierRepositoryImpl <CodeEntity, ItemIdentifier> repository;

	@Test
	public void findOneProducesCodeEntityWithCorrectCode() {

		repository.findOne(getItemIdentifier())
				.test()
				.assertNoErrors()
				.assertValue(codeEntity -> codeEntity.getCode().equals(ITEM_ID));
	}

	@Test
	public void createProducesItemIdentifierWithCorrectInformation() {
		repository.setItemRepository(itemRepository);
		repository.setProductSkuRepository(productSkuRepository);

		when(productSkuRepository.isDisplayableProductSkuForStore(ITEM_ID, SCOPE)).thenReturn(Single.just(true));

		CompositeIdentifier compositeIdentifier = CompositeIdentifier.of(itemRepository.SKU_CODE_KEY, ITEM_ID);
		when(itemRepository.getItemIdMap(ITEM_ID))
				.thenReturn(compositeIdentifier);

		repository.submit(buildCodeEntity(), StringIdentifier.of(SCOPE))
				.test()
				.assertNoErrors()
				.assertValue(submitResult -> submitResult.getIdentifier().getItemId().equals(compositeIdentifier))
				.assertValue(submitResult -> submitResult.getIdentifier().getItems().getScope().getValue().equals(SCOPE));
	}

	public ItemIdentifier getItemIdentifier() {
		return ItemIdentifier.builder()
				.withItemId(createItemIdentifierMap())
				.withItems(ItemsIdentifier.builder()
						.withScope(StringIdentifier.of(SCOPE))
						.build())
				.build();
	}

	private IdentifierPart<Map<String, String>> createItemIdentifierMap() {
		Map<String, String> identiferMap = new HashMap<>();
		identiferMap.put(ItemRepository.SKU_CODE_KEY, ITEM_ID);
		return CompositeIdentifier.of(identiferMap);
	}

	private CodeEntity buildCodeEntity() {
		return CodeEntity.builder()
				.withCode(ITEM_ID)
				.build();
	}
}