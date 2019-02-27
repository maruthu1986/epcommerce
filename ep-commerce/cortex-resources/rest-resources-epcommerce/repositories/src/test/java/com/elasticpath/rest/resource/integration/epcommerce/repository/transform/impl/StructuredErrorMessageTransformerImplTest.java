/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.transform.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.reactivex.Maybe;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.base.common.dto.StructuredErrorMessageType;
import com.elasticpath.base.common.dto.StructuredErrorResolution;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.rest.advise.LinkedMessage;
import com.elasticpath.rest.advise.Message;
import com.elasticpath.rest.definition.items.ItemIdentifier;
import com.elasticpath.rest.definition.items.ItemsIdentifier;
import com.elasticpath.rest.id.type.CompositeIdentifier;
import com.elasticpath.rest.id.type.StringIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.StructuredErrorResolutionStrategy;

/**
 * Tests for {@link StructuredErrorMessageTransformerImpl}.
 */
@RunWith(MockitoJUnitRunner.class)
public class StructuredErrorMessageTransformerImplTest {

	private static final String SKU_GUID = "SKU_GUID";

	private static final String MESSAGE_ID = "message id";

	private static final String DEBUG_MESSAGE = "customerProfile.profileValueMap[CP_FIRST_NAME] cannot be empty.";

	private static final String FIELD_NAME = "field-name";

	private static final String VALUE = "value";

	private static final String KEY = "key";

	private static final String FIELD_VALUE = "customerProfile.profileValueMap[CP_FIRST_NAME]";

	private static final Map<String, String> COMMERCE_ENGINE_FIELD_TO_CORTEX_FIELD_MAP =
			ImmutableMap.of(
					FIELD_VALUE, "given-name"
			);

	private static final String CORTEX_DEBUG_MESSAGE = "given-name cannot be empty.";

	private static final String CORTEX_FIELD_NAME = "given-name";

	private static final String OBJECT_ID = "object id";

	@InjectMocks
	private StructuredErrorMessageTransformerImpl structuredErrorMessageTransformer;

	@Mock
	private StructuredErrorResolutionStrategy resolutionStrategy;

	@Before
	public void initialize() {
		structuredErrorMessageTransformer.setFieldMap(COMMERCE_ENGINE_FIELD_TO_CORTEX_FIELD_MAP);
		structuredErrorMessageTransformer.setResolutionStrategies(ImmutableList.of(resolutionStrategy));
	}

	@Test
	public void shouldTransformAndReplaceCommerceEngineFieldNameByCortexFieldName() {

		// Given
		StructuredErrorMessage structuredErrorMessage = new StructuredErrorMessage(
				MESSAGE_ID,
				DEBUG_MESSAGE,
				ImmutableMap.of(
						FIELD_NAME, FIELD_VALUE,
						KEY, VALUE
				)
		);

		given(resolutionStrategy.getResourceIdentifier(structuredErrorMessage, OBJECT_ID))
				.willReturn(Maybe.empty());

		// When
		List<Message> messages = structuredErrorMessageTransformer.transform(Arrays.asList(structuredErrorMessage), OBJECT_ID);

		// Then
		assertEquals(messages.get(0).getId(), MESSAGE_ID);
		assertEquals(messages.get(0).getDebugMessage(), CORTEX_DEBUG_MESSAGE);
		assertEquals(messages.get(0).getData().get(FIELD_NAME), CORTEX_FIELD_NAME);
		assertEquals(messages.get(0).getData().get(KEY), VALUE);
	}

	@Test
	public void shouldGenerateLinkedMessageWhenThereIsAResolution() {

		// Given
		StructuredErrorResolution resolution = new StructuredErrorResolution(ProductSku.class, SKU_GUID);
		StructuredErrorMessage structuredErrorMessage = new StructuredErrorMessage(
				StructuredErrorMessageType.ERROR,
				MESSAGE_ID,
				DEBUG_MESSAGE,
				ImmutableMap.of(
						FIELD_NAME, FIELD_VALUE,
						KEY, VALUE
				),
				resolution
		);

		ItemIdentifier identifier = ItemIdentifier.builder()
				.withItemId(CompositeIdentifier.of("a", "b"))
				.withItems(ItemsIdentifier.builder()
						.withScope(StringIdentifier.of("SCOPE")).build())
				.build();
		given(resolutionStrategy.getResourceIdentifier(structuredErrorMessage, OBJECT_ID))
				.willReturn(Maybe.just(identifier));

		// When
		List<Message> messages = structuredErrorMessageTransformer.transform(Arrays.asList(structuredErrorMessage), OBJECT_ID);

		// Then

		assertThat(messages.stream().map(LinkedMessage.class::cast))
				.extracting(Message::getId, Message::getDebugMessage, Message::getType, LinkedMessage::getLinkedIdentifier)
				.containsExactly(tuple(
						MESSAGE_ID,
						CORTEX_DEBUG_MESSAGE,
						StructuredErrorMessageType.ERROR.getName(),
						Optional.of(identifier)
				));
	}

}
