/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.inventory.log.impl;

/**
 * Inventory log formatter. Formats information to be logged according to the specific log format.
 */
public class InventoryLogFormatter {

	/**
	 * Formats the log message according to specified format.
	 *
	 * @param message log message
	 * @param logContext log context
	 * @return a formatted string
	 */
	public String format(final String message, final InventoryLogContext logContext) {
		LogEntryFormatBuilder entryBuilder = LogEntryFormatBuilder.getInstance();
		entryBuilder.addLogMessage(message);
		if (logContext != null) {
			entryBuilder.addInventoryKey(logContext.getInventoryKey())
				.addOrderNumber(logContext.getAttribute(InventoryLogContext.ORDER_NUMBER))
				.addOriginator(logContext.getAttribute(InventoryLogContext.EVENT_ORIGINATOR))
				.addComment(logContext.getAttribute(InventoryLogContext.COMMENT))
				.addCommandInfo(logContext.getCommandName(), logContext.getQuantity())
				.addReason(logContext.getAttribute(InventoryLogContext.REASON))
				.addContextAttributes(logContext.getContextAttributes());
		}
		return entryBuilder.buildLogString();
	}

	/**
	 * Formats the log message according to specified format.
	 *
	 * @param message log message
	 * @return a formatted string
	 */
	public String format(final String message) {
		return format(message, null);
	}

}
