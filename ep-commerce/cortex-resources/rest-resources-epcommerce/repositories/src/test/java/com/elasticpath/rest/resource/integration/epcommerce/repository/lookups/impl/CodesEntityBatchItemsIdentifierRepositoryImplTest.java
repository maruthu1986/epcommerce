/*
 * Copyright © 2017 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.rest.resource.integration.epcommerce.repository.lookups.impl;

import java.util.Arrays;

import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.rest.definition.lookups.BatchItemsFormIdentifier;
import com.elasticpath.rest.definition.lookups.BatchItemsIdentifier;
import com.elasticpath.rest.definition.lookups.CodesEntity;
import com.elasticpath.rest.definition.lookups.LookupsIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.id.type.StringListIdentifier;

/**
 * Test for {@link CodesEntityBatchItemsIdentifierRepositoryImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class CodesEntityBatchItemsIdentifierRepositoryImplTest {

	private static final String SCOPE = "mobee";
	private static final String ITEM_ID = "pillowid";

	@InjectMocks
	private CodesEntityBatchItemsIdentifierRepositoryImpl <CodesEntity, BatchItemsIdentifier> repository;

	@Test
	public void findOneProducesCodesEntityWithCorreectInformation() {
		repository.findOne(BatchItemsIdentifier.builder()
				.withBatchId(StringListIdentifier.of(ITEM_ID))
				.withBatchItemsForm(getBatchItemsFormIdentifier(SCOPE))
				.build())
				.test()
				.assertNoErrors()
				.assertValue(codesEntity -> codesEntity.getCodes().size() == 1)
				.assertValue(codesEntity -> codesEntity.getCodes().contains(ITEM_ID));
	}

	@Test
	public void createProducesBatchItemsIdentifierWithCorrectInformation() {
		CodesEntity codesEntity = createCodesEntity(ITEM_ID);

		repository.submit(codesEntity, StringIdentifier.of(SCOPE))
				.test()
				.assertNoErrors()
				.assertValue(submitResult -> Lists.newArrayList(submitResult.getIdentifier().getBatchId().getValue()).size() == 1)
				.assertValue(submitResult -> submitResult.getIdentifier().getBatchItemsForm().getLookups()
						.getScope().getValue().equals(SCOPE));
	}

	private CodesEntity createCodesEntity(final String...codes) {
		return CodesEntity.builder()
				.withCodes(Arrays.asList(codes))
				.build();
	}

	private BatchItemsFormIdentifier getBatchItemsFormIdentifier(final String scope) {
		return BatchItemsFormIdentifier.builder()
				.withLookups(LookupsIdentifier.builder()
						.withScope(StringIdentifier.of(scope))
						.build())
				.build();
	}
}