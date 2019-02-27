/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.transform.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.rest.advise.LinkedMessage;
import com.elasticpath.rest.advise.Message;
import com.elasticpath.rest.id.ResourceIdentifier;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.StructuredErrorMessageTransformer;
import com.elasticpath.rest.resource.integration.epcommerce.repository.transform.StructuredErrorResolutionStrategy;

/**
 * Implementation of {@link StructuredErrorMessageTransformer}.
 */
public class StructuredErrorMessageTransformerImpl implements StructuredErrorMessageTransformer {

	private static final String DATA_KEY_FOR_FIELD_NAME = "field-name";

	private Map<String, String> fieldMap;
	private List<StructuredErrorResolutionStrategy> resolutionStrategies;

	@Override
	public List<Message> transform(final Collection<StructuredErrorMessage> structuredErrorMessages, final String cortexResourceID) {
		return structuredErrorMessages.stream().map(structuredErrorMessage ->
				getResourceIdentifier(structuredErrorMessage, cortexResourceID).map(identifier ->
						generateLinkedMessage(structuredErrorMessage, identifier))
						.orElse(generateMessage(structuredErrorMessage))
		).collect(Collectors.toList());
	}

	private Message generateMessage(final StructuredErrorMessage structuredErrorMessage) {
		return Message.builder()
				.withType(structuredErrorMessage.getType().getName())
				.withId(structuredErrorMessage.getMessageId())
				.withDebugMessage(replaceDebugMessageCommerceEngineFieldNameWithCortexFieldName(structuredErrorMessage))
				.withData(replaceCommerceEngineFieldNameValueWithCortexFieldNameValue(structuredErrorMessage.getData()))
				.build();
	}

	private Message generateLinkedMessage(final StructuredErrorMessage structuredErrorMessage, final ResourceIdentifier identifier) {
		return LinkedMessage.builder()
				.withLinkedIdentifier(identifier)
				.withType(structuredErrorMessage.getType().getName())
				.withId(structuredErrorMessage.getMessageId())
				.withDebugMessage(replaceDebugMessageCommerceEngineFieldNameWithCortexFieldName(structuredErrorMessage))
				.withData(replaceCommerceEngineFieldNameValueWithCortexFieldNameValue(structuredErrorMessage.getData()))
				.build();
	}

	/**
	 * Gets resource identifiers using strategies.
	 *
	 * @param message the structured error message.
	 * @param cortexResourceID the id of the resource from cortex.
	 * @return the identifier or empty if no strategy matches.
	 */
	protected Optional<ResourceIdentifier> getResourceIdentifier(final StructuredErrorMessage message, final String cortexResourceID) {
				return resolutionStrategies.stream()
				.map(strategy -> Optional.ofNullable(strategy.getResourceIdentifier(message, cortexResourceID).blockingGet()))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.findFirst();
	}

	/**
	 * Replaces commerce engine field names with cortex ones.
	 *
	 * @param data the data map.
	 * @return a map containing the converted field name.
	 */
	protected Map<String, String> replaceCommerceEngineFieldNameValueWithCortexFieldNameValue(final Map<String, String> data) {
		final String fieldName = data.get(DATA_KEY_FOR_FIELD_NAME);
		String cortexFieldName = fieldMap.getOrDefault(fieldName, fieldName);

		// Replace field name.
		Map<String, String> cortexData = new HashMap<>();
		data.forEach((key, value) -> {
			if (DATA_KEY_FOR_FIELD_NAME.equalsIgnoreCase(key)) {
				value = cortexFieldName;
			}
			cortexData.put(key, value);
		});
		return cortexData;
	}

	/**
	 * Replaces commerce engine debug messages with cortex ones.
	 *
	 * @param structuredErrorMessage the structured error message.
	 * @return a string containing the cortex debug message.
	 */
	protected String replaceDebugMessageCommerceEngineFieldNameWithCortexFieldName(final StructuredErrorMessage structuredErrorMessage) {
		Map<String, String> data = structuredErrorMessage.getData();
		String commerceEngineFieldName = data.get(DATA_KEY_FOR_FIELD_NAME);
		if (commerceEngineFieldName == null) {
			return structuredErrorMessage.getDebugMessage();
		}

		String cortexFieldName = fieldMap.getOrDefault(commerceEngineFieldName, commerceEngineFieldName);
		return structuredErrorMessage.getDebugMessage().replace(commerceEngineFieldName, cortexFieldName);
	}

	protected Map<String, String> getFieldMap() {
		return fieldMap;
	}

	public void setFieldMap(final Map<String, String> fieldMap) {
		this.fieldMap = fieldMap;
	}

	protected List<StructuredErrorResolutionStrategy> getResolutionStrategies() {
		return resolutionStrategies;
	}

	public void setResolutionStrategies(final List<StructuredErrorResolutionStrategy> resolutionStrategies) {
		this.resolutionStrategies = resolutionStrategies;
	}
}
