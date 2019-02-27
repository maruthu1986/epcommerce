/**
 * Copyright (c) Elastic Path Software Inc., 2013
 */
package com.elasticpath.importexport.common.summary.impl;

import java.util.List;
import java.util.Map;

import com.elasticpath.importexport.common.summary.Summary;
import com.elasticpath.importexport.common.summary.SummaryLayout;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.importexport.common.util.Message;
import com.elasticpath.importexport.common.util.MessageResolver;

/**
 * Simple implementation of SummaryLayout.
 */
public class SimpleSummaryLayout implements SummaryLayout {

	private static final char CRLF = '\n';

	private static final char TAB = '\t';
	
	private MessageResolver messageResolver;

	@Override
	public String format(final Summary summary) {

		final StringBuilder builder = new StringBuilder();

		builder.append("Summary Report").append(CRLF);

		builder.append(CRLF);

		builder.append("Started Time :").append(summary.getStartDate()).append(CRLF);
		builder.append("Elapsed Time :").append(summary.getElapsedTime()).append(CRLF);

		builder.append(CRLF);
		
		final List<Message> failures = summary.getFailures();
		final List<Message> warnings = summary.getWarnings();
		final List<Message> comments = summary.getComments();
		final Map<JobType, Integer> objectCounters = summary.getCounters();

		builder.append("Total Number Of Objects     :").append(summary.sumAllCounters()).append(CRLF);
		builder.append("Total Number Of Failures    :").append(failures.size()).append(CRLF);
		builder.append("Total Number Of Warnings    :").append(warnings.size()).append(CRLF);
		builder.append("Total Number Of Comments    :").append(comments.size()).append(CRLF);
		if (summary.getAddedToChangeSetCount() > 0) {
			builder.append("Objects Added to Change Set :").append(summary.getAddedToChangeSetCount()).append(CRLF);
		}

		if (!objectCounters.isEmpty()) {
			builder.append(CRLF);
			builder.append("Objects :").append(CRLF);
			for (Map.Entry<JobType, Integer> entry : objectCounters.entrySet()) {
				builder.append(TAB).append(entry.getKey().getTagName()).append(" :").append(entry.getValue()).append(CRLF);
			}
		}

		if (!failures.isEmpty()) {
			builder.append(CRLF);
			builder.append("Failures :").append(CRLF);
			for (Message failure : failures) {
				builder.append(TAB).append(convertMessage(failure)).append(CRLF);
			}
		}
		
		if (!warnings.isEmpty()) {
			builder.append(CRLF);
			builder.append("Warnings :").append(CRLF);
			for (Message warning : warnings) {
				builder.append(TAB).append(convertMessage(warning)).append(CRLF);
			}
		}

		if (!comments.isEmpty()) {
			builder.append(CRLF);
			builder.append("Comments :").append(CRLF);
			for (Message comment : comments) {
				builder.append(TAB).append(convertMessage(comment)).append(CRLF);
			}
		}
		return builder.toString();
	}

	/**
	 * Converts the message to string representation.
	 * 
	 * @param message the message 
	 * @return string representation
	 */
	protected String convertMessage(final Message message) {
		if (messageResolver == null) {
			return message.getCode();
		}
		
		return messageResolver.resolve(message);
	}

	/**
	 * Sets message resolver.
	 * 
	 * @param messageResolver the message resolver
	 */
	public void setMessageResolver(final MessageResolver messageResolver) {
		this.messageResolver = messageResolver;
	}

}
