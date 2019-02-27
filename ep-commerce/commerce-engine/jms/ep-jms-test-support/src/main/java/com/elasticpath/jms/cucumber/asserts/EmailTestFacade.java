/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.jms.cucumber.asserts;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Test class for emails.
 */
@SuppressWarnings("PMD.UseUtilityClass")
public final class EmailTestFacade {

	private static final Logger LOGGER = Logger.getLogger(EmailTestFacade.class);

	/**
	 * Private constructor.
	 */
	private EmailTestFacade() {

	}

	/**
	 * Gets the HTML source from the first jsonObject in jsonObjectList that contains htmlBody.
	 *
	 * @param jsonObjectList the json object list
	 * @return the email body as an html string
	 */
	public static String getEmailBody(final List<JSONObject> jsonObjectList) {

		boolean htmlBodyExists = false;
		String htmlStr = null;

		for (JSONObject jsonObject : jsonObjectList) {
			LOGGER.debug(jsonObject);
			htmlStr = (String) jsonObject.get("htmlBody");

			if (0 < htmlStr.length()) {
				LOGGER.debug(htmlStr);
				htmlBodyExists = true;
				break;
			}
		}

		assertThat(htmlBodyExists)
				.as("'htmlBody' doesn't exist")
				.isTrue();

		return htmlStr;
	}

	/**
	 * Verifies the value for the given key in the email matches the expectedValue.
	 *
	 * @param jsonObjectList the json object list
	 * @param key            the key
	 * @param expectedValue  the expected value
	 */
	public static void assertEmailValue(final List<JSONObject> jsonObjectList, final String key, final String expectedValue) {

		boolean keyExists = false;
		String htmlStr = getEmailBody(jsonObjectList);
		assertThat(htmlStr)
				.as("'htmlBody' doesn't exist")
				.isNotNull();

		Document document = Jsoup.parse(htmlStr);
		Element table = document.select("table").get(0);
		Elements rows = table.select("tr");

		for (Element row : rows) {
			Elements columns = row.select("td");
			for (Element column : columns) {
				if (column.text().equals(key)) {
					keyExists = true;
					LOGGER.debug(column.text());
					LOGGER.debug(column.nextElementSibling().text());
					assertThat(column.nextElementSibling().text())
							.as(key + " validation failed")
							.isEqualTo(expectedValue);
					break;
				}
			}
			if (keyExists) {
				break;
			}

		}

		assertThat(keyExists)
				.as("'" + key + "' doesn't exist")
				.isTrue();
	}

}
