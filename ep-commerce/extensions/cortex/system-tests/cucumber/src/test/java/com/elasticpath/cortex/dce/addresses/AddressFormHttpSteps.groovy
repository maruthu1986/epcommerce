package com.elasticpath.cortex.dce.addresses

import static org.assertj.core.api.Assertions.assertThat

import static com.elasticpath.cortex.dce.ClasspathFluentRelosClientFactory.client
import static com.elasticpath.cortex.dce.addresses.AddressConstants.goToAddressForm

import cucumber.api.DataTable
import cucumber.api.groovy.EN
import cucumber.api.groovy.Hooks

this.metaClass.mixin(Hooks)
this.metaClass.mixin(EN)

When(~'I go to registered shopper profile addresses$') { ->
	client.GET("/")
			.defaultprofile()
			.addresses()
			.element()
	return client.body.links[0].href
}

When(~'I update address via put with country (.*), locality (.*), postal code (.*), region (.*), street address (.*), family name (.*), given name (.*)$') {
	String country, String locality, String postalCode, String region, String streetAddress, String familyName, String givenName ->
		def addressUri = client.body.self.uri

		client.PUT(addressUri, [
				address: ["country-name"  : country,
						  "locality"      : locality,
						  "postal-code"   : postalCode,
						  "region"        : region,
						  "street-address": streetAddress],
				name   : ["family-name": familyName,
						  "given-name" : givenName]
		])
				.stopIfFailure()
}

When(~'I get address form$') { ->
	goToAddressForm()
}

When(~'I update address form via put$') { ->
	goToAddressForm()

	def addressformUri = client.body.self.uri

	client.PUT(addressformUri, [
			"country-name"    : "",
			"extended-address": "",
			"locality"        : "",
			"postal-code"     : "",
			"region"          : "",
			"street-address"  : ""
	])
}

When(~'I post to address form$') { ->
	goToAddressForm()

	def addressformUri = client.body.self.uri

	client.POST(addressformUri, [
			"country-name"    : "",
			"extended-address": "",
			"locality"        : "",
			"postal-code"     : "",
			"region"          : "",
			"street-address"  : ""
	])
}

When(~'I delete address form$') { ->
	goToAddressForm()

	def addressformUri = client.body.self.uri
	client.DELETE(addressformUri)
}

Then(~/^form should have following values$/) { DataTable dataTable ->
	def keyValueList = dataTable.asList(KeyValue)

	for (KeyValue keyValue : keyValueList) {
		def map = keyValue.getFormMap()
		map.each {
			assertThat(client[it.key][it.value])
					.as(it.key + ", " + it.value + " not found")
					.isEqualTo("")
		}
	}
}

public class KeyValue {
	String key
	String value

	Map<String, String> formMap

	def getFormMap() {
		formMap = new HashMap<String, String>()
		formMap.put(key, value)
		return formMap;
	}
}
