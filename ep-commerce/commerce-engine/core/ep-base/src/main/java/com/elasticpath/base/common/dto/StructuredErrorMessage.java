/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.base.common.dto;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Represents a commerce message data object.
 */
public class StructuredErrorMessage {

	private final StructuredErrorMessageType type;
	private final String messageId;
	private final String debugMessage;
	private final Map<String, String> data;
	private final StructuredErrorResolution resolution;

	/**
	 * Constructor to create a StructuredErrorMessage of type Error.
	 *
	 * @param messageId         messageId
	 * @param debugMessage      debug message
	 * @param data              data
	 */
	public StructuredErrorMessage(
			final String messageId,
			final String debugMessage,
			final Map<String, String> data) {
		this(StructuredErrorMessageType.ERROR, messageId, debugMessage, data);
	}

	/**
	 * Constructor to create a StructuredErrorMessage.
	 * @param type         type
	 * @param messageId         messageId
	 * @param debugMessage      debug message
	 * @param data              data
	 */
	public StructuredErrorMessage(
			final StructuredErrorMessageType type,
			final String messageId,
			final String debugMessage,
			final Map<String, String> data) {
		this(type, messageId, debugMessage, data, null);
	}

	/**
	 * Constructor.
	 *  @param type         type
	 * @param messageId    messageId
	 * @param debugMessage debug message
	 * @param data         data
	 * @param resolution   resolution
	 */
	public StructuredErrorMessage(
			final StructuredErrorMessageType type,
			final String messageId,
			final String debugMessage,
			final Map<String, String> data,
			final StructuredErrorResolution resolution) {

		this.type = type;
		this.messageId = messageId;
		this.debugMessage = debugMessage;
		this.data = data == null
				? null
				: ImmutableMap.copyOf(data);
		this.resolution = resolution;
	}

	public Optional<StructuredErrorResolution> getResolution() {
		return Optional.ofNullable(resolution);
	}

	/**
	 * Get the type of the message.
	 *
	 * @return the type
	 */
	public StructuredErrorMessageType getType() {
		return type;
	}

	/**
	 * Get the id of the message.
	 *
	 * @return  message Id
	 */
	public String getMessageId() {
		return messageId;
	}

	/**
	 * Get the debug message for the structured message.
	 *
	 * @return the debug message
	 */
	public String getDebugMessage() {
		return debugMessage;
	}

	/**
	 * Get the map of additional information related to the message. This includes
	 * values that can be used to replace placeholders in message templates.
	 *
	 * @return the map.
	 */
	public Map<String, String> getData() {
		return data;
	}

	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || getClass() != other.getClass()) {
			return false;
		}
		StructuredErrorMessage that = (StructuredErrorMessage) other;

		return Objects.equals(type, that.type)
				&& Objects.equals(messageId, that.messageId)
			&& Objects.equals(debugMessage, that.debugMessage)
			&& Objects.equals(data, that.data);
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, messageId, debugMessage, data);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE)
				.append(type.getName())
				.append(messageId)
				.append(debugMessage)
				.append(data)
				.build();
	}
}
