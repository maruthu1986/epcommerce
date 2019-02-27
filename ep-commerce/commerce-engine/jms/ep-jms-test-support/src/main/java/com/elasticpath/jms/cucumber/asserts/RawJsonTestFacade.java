/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.jms.cucumber.asserts;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import com.jayway.jsonpath.JsonPath;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

/**
 * Class for testing raw json values.
 */
@SuppressWarnings("PMD.UseUtilityClass")
public final class RawJsonTestFacade {

	private static final Logger LOGGER = Logger.getLogger(RawJsonTestFacade.class);

	/**
	 * Private constructor.
	 */
	private RawJsonTestFacade() {

	}

	/**
	 * Verifies that jsonObjectList contains a jsonObject which contains the given key and value.
	 *
	 * @param jsonObjectList the json object list
	 * @param parent         parent
	 * @param key            key
	 * @param value          value
	 */
	public static void verifyJsonValues(final List<JSONObject> jsonObjectList, final String parent, final String key, final String value) {

		boolean objectExists = false;
		boolean valueExists = false;
		String parentWithKey = "";

		for (JSONObject jsonObject : jsonObjectList) {

			if (parent.length() > 0) {
				parentWithKey = "$." + parent + "." + key;
			} else {
				parentWithKey = "$." + key;
			}

			LOGGER.debug("verifying " + parentWithKey + " for value: " + value);

			if (parent.length() > 0 && jsonObject.containsKey(parent) || jsonObject.containsKey(key)) {
				objectExists = true;
				if (JsonPath.parse(jsonObject.toJSONString()).read(parentWithKey) instanceof String) {
					if (JsonPath.parse(jsonObject.toJSONString()).read(parentWithKey).toString().equals(value)) {
						valueExists = true;
					}
				} else {
					List<String> strList = JsonPath.parse(jsonObject.toJSONString()).read(parentWithKey);
					assertThat(strList)
							.as("JSONObject key '" + parentWithKey + "' doesn't exist")
							.isNotEmpty();

					if (!strList.isEmpty() && strList.get(0).equals(value)) {
						valueExists = true;
					}
				}
				if (valueExists) {
					break;
				}
			}
		}

		assertThat(objectExists)
				.as("JSONObject key '" + parent + "' doesn't exist")
				.isTrue();

		assertThat(valueExists)
				.as("JSONObject value '" + value + "' doesn't exist")
				.isTrue();

	}

}
