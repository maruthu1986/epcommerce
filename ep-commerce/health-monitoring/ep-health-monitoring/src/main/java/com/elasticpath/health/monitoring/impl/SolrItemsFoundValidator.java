/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.health.monitoring.impl;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import com.elasticpath.health.monitoring.ResponseValidator;
import com.elasticpath.health.monitoring.Status;
import com.elasticpath.health.monitoring.StatusType;

/**
 * A {@link ResponseValidator} instance which validates the number of items found by Solr.
 */
public class SolrItemsFoundValidator implements ResponseValidator<String> {
	private int minimumNumberOfItemsRequired = 1;

	@Override
	public Status validate(final String response) {
		if (StringUtils.isBlank(response)) {
			return createStatus(StatusType.UNKNOWN, "No response body returned", null);
		}

		final JSONObject jsonResponse = new JSONObject(response);
		final int responseStatus = jsonResponse.getJSONObject("responseHeader").getInt("status");

		if (responseStatus != 0) {
			return createStatus(StatusType.UNKNOWN, "Solr response status non-zero", response);
		}

		final int numberRequired = getMinimumNumberOfItemsRequired();
		final int numberFound = jsonResponse.getJSONObject("response").getInt("numFound");
		if (numberFound < numberRequired) {
			return createStatus(StatusType.UNKNOWN, numberFound + " number of items returned from Solr is less than required ("
				+ numberRequired + ")", response);
		}

		return createStatus(StatusType.OK, "Number of items returned: " + numberFound + "; minimum number required: " + numberRequired, null);
	}

	/**
	 * Instantiates a Status object and sets the values on it.
	 *
	 * @param info the info
	 * @param type the type
	 * @param message the message
	 * @return the status
	 */
	protected Status createStatus(final StatusType type, final String message, final String info) {
		Status status = new StatusImpl();
		status.setStatus(type);
		status.setMessage(message);
		status.setInfo(info);
		return status;
	}

	protected int getMinimumNumberOfItemsRequired() {
		return this.minimumNumberOfItemsRequired;
	}

	public void setMinimumNumberOfItemsRequired(final int minimumNumberOfItemsRequired) {
		this.minimumNumberOfItemsRequired = minimumNumberOfItemsRequired;
	}
}
