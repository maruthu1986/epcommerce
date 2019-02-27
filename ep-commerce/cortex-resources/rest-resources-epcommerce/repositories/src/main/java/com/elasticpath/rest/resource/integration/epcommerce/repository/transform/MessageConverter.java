/**
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.rest.resource.integration.epcommerce.repository.transform;

import javax.inject.Named;
import javax.inject.Singleton;

import org.springframework.core.convert.converter.Converter;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.rest.advise.Message;


/**
 * Convert a {@link StructuredErrorMessage} to a {@link Message}.
 */
@Singleton
@Named
public class MessageConverter implements Converter<StructuredErrorMessage, Message> {

	@Override
	public Message convert(final StructuredErrorMessage structuredErrorMessage) {
		return Message.builder()
				.withType(structuredErrorMessage.getType().getName())
				.withId(structuredErrorMessage.getMessageId())
				.withDebugMessage(structuredErrorMessage.getDebugMessage())
				.withData(structuredErrorMessage.getData())
				.build();
	}
}
