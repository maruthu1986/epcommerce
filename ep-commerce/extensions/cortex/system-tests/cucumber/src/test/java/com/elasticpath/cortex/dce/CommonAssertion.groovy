package com.elasticpath.cortex.dce

import static org.assertj.core.api.Assertions.assertThat

import static com.elasticpath.cortex.dce.ClasspathFluentRelosClientFactory.client

import cucumber.api.DataTable
import groovy.json.internal.LazyMap
import org.apache.commons.collections.CollectionUtils
import org.apache.commons.collections.MapUtils

import com.elasticpath.rest.ws.client.StopTestException

/**
 * Shared assertions.
 */
class CommonAssertion {

	static assertCost(final LazyMap costElementMap, final String expectedAmount,
					  final String expectedCurrency, final String expectedDisplayName) {
		if (MapUtils.isEmpty(costElementMap)) {
			throw new StopTestException("Cost value map is empty.");
		}
		BigDecimal expAmountBigDouble = new BigDecimal(expectedAmount)
		BigDecimal amount = costElementMap["amount"]
		assertThat(amount)
				.as("Amount is not as expected")
				.isEqualByComparingTo(expAmountBigDouble)
		assertThat(costElementMap["currency"])
				.as("Currency is not as expected")
				.isEqualTo(expectedCurrency)
		assertThat(costElementMap["display"])
				.as("Display is not as expected")
				.isEqualTo(expectedDisplayName)
	}

	static assertCost(final LazyMap costElementMap, final Map<String, String> expectedValuesMap) {
		assertCost(costElementMap, expectedValuesMap["amount"],
				expectedValuesMap["currency"], expectedValuesMap["display"])
	}

	static assertCost(final List<LazyMap> costElementList, final String expectedAmount,
					  final String expectedCurrency, final String expectedDisplayName) {
		if (CollectionUtils.isEmpty(costElementList)) {
			throw new StopTestException("Cost value list is empty.");
		}
		assertCost(costElementList.get(0), expectedAmount,
				expectedCurrency, expectedDisplayName)
	}

	static assertAddress(final LazyMap addressElementMap, final Map<String, String> expectedAddressMap) {
		if (MapUtils.isEmpty(addressElementMap)) {
			throw new StopTestException("Address value map is empty.");
		}
		assertThat(addressElementMap["country-name"])
				.as("Country name is not as expected")
				.isEqualTo(expectedAddressMap["country-name"])
		assertThat(addressElementMap["locality"])
				.as("Locality is not as expected")
				.isEqualTo(expectedAddressMap["locality"])
		assertThat(addressElementMap["postal-code"])
				.as("Postal code is not as expected")
				.isEqualTo(expectedAddressMap["postal-code"])
		assertThat(addressElementMap["region"])
				.as("Region is not as expected")
				.isEqualTo(expectedAddressMap["region"])
		assertThat(addressElementMap["street-address"])
				.as("Street address is not as expected")
				.isEqualTo(expectedAddressMap["street-address"])
	}

	static assertItemConfiguration(DataTable dataTable) {
		def mapList = dataTable.asMap(String, String)
		for (def map : mapList) {
			def key = map.getKey()
			def value = map.getValue()

			assertThat(client.body.'configuration'."$key")
					.as("Expected $key does not match")
					.isEqualTo(value)
		}
	}

	static assertMap(final LazyMap actualMap, final Map<String, String> expectedMap) {
		for (String key : expectedMap.keySet()) {
			String actualValue = actualMap[key]
			String expectedValue = expectedMap[key]
			assertThat(actualValue)
					.as(String.format("Expecting %s to equal %s but was %s", key, expectedValue, actualMap))
					.isEqualTo(expectedValue)
		}
	}
}