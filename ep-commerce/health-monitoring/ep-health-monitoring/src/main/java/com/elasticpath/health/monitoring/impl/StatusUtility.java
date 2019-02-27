/*
 * Copyright (c) Elastic Path Software Inc., 2018
 */
package com.elasticpath.health.monitoring.impl;

import java.util.Map;

import org.json.JSONObject;

import com.elasticpath.health.monitoring.Status;
import com.elasticpath.health.monitoring.StatusType;

/**
 * Utility class for generating the status results.
 */
public class StatusUtility {

	/**
	 * Utility method to determine an aggregated status out of a bunch of results by choosing the maximum severity.
	 *
	 * @param results map of results
	 * @return boolean the aggregated result
	 */
	public StatusType getAggregatedResult(final Map<String, Status> results) {
		StatusType result = StatusType.OK;

		for (Map.Entry<String, Status> entry : results.entrySet()) {
			StatusType status = entry.getValue().getStatus();
			if (status.ordinal() > result.ordinal()) {
				result = status;
			}
		}

		return result;
	}

	/**
	 * Generates a JSON object from the given results.
	 *
	 * @param results the resutls
	 * @return the json object
	 */
	public JSONObject getJSONResults(final Map<String, Status> results) {
		final JSONObject jsonObject = new JSONObject();

		for (Map.Entry<String, Status> entry : results.entrySet()) {
			JSONObject jsonObjectStatus = new JSONObject();
			Status status = entry.getValue();

			jsonObjectStatus.put("status", status.getStatus().toString());
			jsonObjectStatus.put("info", status.getInfo());
			jsonObjectStatus.put("message", status.getMessage());

			jsonObject.put(entry.getKey(), jsonObjectStatus);
		}
		return jsonObject;
	}

}
