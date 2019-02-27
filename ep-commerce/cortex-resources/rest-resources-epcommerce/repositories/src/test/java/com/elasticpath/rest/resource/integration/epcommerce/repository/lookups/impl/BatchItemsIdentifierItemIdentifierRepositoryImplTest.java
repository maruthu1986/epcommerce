/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.integration.epcommerce.repository.lookups.impl;

import static org.mockito.Mockito.when;

import io.reactivex.Single;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.ProductSkuImpl;
import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.definition.lookups.BatchItemsFormIdentifier;
import com.elasticpath.rest.definition.lookups.BatchItemsIdentifier;
import com.elasticpath.rest.definition.lookups.LookupsIdentifier;
import com.elasticpath.rest.id.type.CompositeIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.id.type.StringListIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.item.ItemRepository;
import com.elasticpath.rest.resource.integration.epcommerce.repository.sku.ProductSkuRepository;

/**
 * Test for {@link BatchItemsIdentifierItemIdentifierRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class BatchItemsIdentifierItemIdentifierRepositoryImplTest {

	private static final String SCOPE = "mobee";
	private static final String ITEM_ID = "pillow_sku";

	@Mock
	private ItemRepository itemRepository;
	@Mock
	private ProductSkuRepository productSkuRepository;

	@InjectMocks
	private BatchItemsIdentifierItemIdentifierRepositoryImpl <BatchItemsIdentifier, ItemIdentifier> repository;

	@Before
	public void setUp() {
		repository.setItemRepository(itemRepository);
		repository.setProductSkuRepository(productSkuRepository);
	}

	@Test
	public void getElementsProducesItemIdentifier() {

		ProductSku productSku = new ProductSkuImpl();
		productSku.setSkuCode(ITEM_ID);

		when(itemRepository.getItemIdMap(ITEM_ID))
				.thenReturn(CompositeIdentifier.of(itemRepository.SKU_CODE_KEY, ITEM_ID));

		when(productSkuRepository.isDisplayableProductSkuForStore(ITEM_ID, SCOPE)).thenReturn(Single.just(true));

		repository.getElements(BatchItemsIdentifier.builder()
				.withBatchId(StringListIdentifier.of(ITEM_ID))
				.withBatchItemsForm(getBatchItemsFormIdentifier(SCOPE))
				.build())
				.test()
				.assertNoErrors()
				.assertValue(itemIdentifier -> itemIdentifier.getItemId().equals(CompositeIdentifier.of(itemRepository.SKU_CODE_KEY, ITEM_ID)))
				.assertValue(itemIdentifier -> itemIdentifier.getItems().getScope().getValue().equals(SCOPE));
	}

	@Test
	public void getElementsProducesEmptyObservableWhenNoSkusFound() {
		when(productSkuRepository.isDisplayableProductSkuForStore(ITEM_ID, SCOPE)).thenReturn(Single.just(false));

		repository.getElements(BatchItemsIdentifier.builder()
				.withBatchId(StringListIdentifier.of(ITEM_ID))
				.withBatchItemsForm(getBatchItemsFormIdentifier(SCOPE))
				.build())
				.test()
				.assertNoErrors()
				.assertNoValues();

	}

	private BatchItemsFormIdentifier getBatchItemsFormIdentifier(final String scope) {
		return BatchItemsFormIdentifier.builder()
				.withLookups(LookupsIdentifier.builder()
						.withScope(StringIdentifier.of(scope))
						.build())
				.build();
	}
}