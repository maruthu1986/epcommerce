package com.elasticpath.cortex.dce.geographies

import static com.elasticpath.cortex.dce.ClasspathFluentRelosClientFactory.client
import static com.elasticpath.cortex.dce.ClasspathFluentRelosClientFactory.currentScope
import static com.elasticpath.cortex.dce.SharedConstants.*
import static org.assertj.core.api.Assertions.assertThat

import static com.elasticpath.rest.id.util.Base32Util.encode

import cucumber.api.groovy.EN
import cucumber.api.groovy.Hooks

import static com.elasticpath.rest.ws.assertions.RelosAssert.assertLinkDoesNotExist

import com.elasticpath.cortex.dce.CommonMethods

this.metaClass.mixin(Hooks)
this.metaClass.mixin(EN)

Given(~'^there is a list of supported (?:countries|countries and regions) for scope (.+)$') { def storeScope ->
	// Lists of countries and regions is configured in properties files in CE.
	client.authAsAPublicUser(storeScope)
			.stopIfFailure()
}

Given(~'^there is a list of (?:.+) supported regions for (?:.+) in scope (.+)$') { def storeScope ->
	// Lists of countries and regions is configured in properties files in CE.
	client.authAsAPublicUser(storeScope)
			.stopIfFailure()
}

Given(~'^there is a list of (?:.+) supported countries for scope (.+)$') { def storeScope ->
	// Lists of countries and regions is configured in properties files in CE.
	client.authAsAPublicUser(storeScope)
			.stopIfFailure()
}

Given(~'^scope (.+) supports (.+) language$') { String storeScope, String language ->
	client.authAsAPublicUser(storeScope)
			.stopIfFailure()
}

And(~'^one of the supported countries is (.+)$') { def supportedCountry ->
	// Countries has been defined in the backend system (CE)
}

Given(~'^one of the supported regions for (.+) is (.+)$') { def country, def region ->
	// List of regions has been defined in the backend system (CE)
}

Given(~'^there are no supported regions for (.+)$') { def countryName ->
	// List of regions has been defined in the backend system (CE)
	client.authAsAPublicUser(DEFAULT_SCOPE)
			.stopIfFailure()
}

When(~'^the country Canada and the region British Columbia is selected$') { ->
	client.GET("/geographies/$currentScope/countries")
			.findElement {
		country ->
			country["display-name"] == "Canada"
	}
	countryCode = client["name"]
	client.regions()
			.findElement {
		region ->
			region["display-name"] == "British Columbia"
	}
	.stopIfFailure()
	regionCode = client["name"]
}

When(~'^I request the list of countries$') { ->
	client.GET("/geographies/$currentScope/countries")
			.stopIfFailure()
}

When(~'^I request region (.+)') { def regionId ->
	def CanadaCountryCode = "CA";
	client.GET("/geographies/$currentScope/countries/${encode(CanadaCountryCode)}/regions/${encode(regionId)}")
			.stopIfFailure()
}
When(~'^I request a region with an undecodable id') { ->
	def CanadaCountryCode = "CA";
	def undecodableId = "2=";
	client.GET("/geographies/$currentScope/countries/${encode(CanadaCountryCode)}/regions/${undecodableId}")
			.stopIfFailure()
}

When(~'^I request the list of countries in language (.+) in scope (.+)') { def locale, def storeScope ->
	client.headers.put("x-ep-user-traits", "LOCALE=" + locale)
	client.GET("/geographies/${storeScope}/countries")
			.stopIfFailure()
}

When(~'^I request the list of sub-countries in language (.+) in scope (.+)') { def locale, def storeScope ->
	client.headers.put("x-ep-user-traits", "LOCALE=" + locale)

	client.GET("/geographies/${storeScope}/countries")
			.findElement {
		country ->
			country["name"] == "CA"
	}
	canadaUri = client.body.self.uri
	client.regions()
			.stopIfFailure()

}

When(~'^I request the list of countries in scope (.+)') { def storeScope ->
	client.GET("/geographies/${storeScope}/countries")
			.stopIfFailure()
}

When(~'^I request the list of regions for Canada in scope (.+)$') { def storeScope ->
	client.GET("/geographies/${storeScope}/countries")
			.findElement {
		country ->
			country["name"] == "CA"
	}
	canadaUri = client.body.self.uri
	client.regions()
			.stopIfFailure()
}

When(~'^I request the list of regions for Japan$') { ->
	client.GET("/geographies/$currentScope/countries")
			.findElement {
		country ->
			country["name"] == "JP"
	}
	.regions()
			.stopIfFailure()
}

Then(~'^I can obtain the country and region code to create an address$') { ->

	CommonMethods.createAddress(countryCode, "", "Vancouver", "", "", "V7V7V7", regionCode,
			"1234 Hello World", "itest created", "address")

	assertThat(client.response.status)
			.as("HTTP status is not as expected")
			.isEqualTo(201)

	client.follow()
			.stopIfFailure()

	assertThat(client.body.address."country-name")
			.as("Country is not as expected")
			.isEqualTo(countryCode)
	assertThat(client.body.address."region")
			.as("Region is not as expected")
			.isEqualTo(regionCode)
}

Then(~'^I get back all ([0-9]*) supported countries$') { Integer numSupportedCountries ->
	def countries = client.body.links.findAll {
		link ->
			link.rel == "element"
	}
	assertThat(countries)
			.size()
			.as("Number of supported countries is not as expected")
			.isEqualTo(numSupportedCountries)
}

Then(~'^one of the countries is (.+)$') { def countryName ->
	client.findElement {
		country ->
			country["display-name"] == countryName
	}
	.stopIfFailure()
}

Then(~'^I get back all ([0-9]*) supported regions for Canada$') { Integer numSupportedRegions ->
	def regions = client.body.links.findAll {
		link ->
			link.rel == "element"
	}
	client.stopIfFailure()
	assertThat(regions)
			.size()
			.as("Number of regions is not as expected")
			.isEqualTo(numSupportedRegions)
}

Then(~'^I get back an empty list$') { ->
	assertLinkDoesNotExist(client, "element")
}

Then(~'^one of the regions is (.+)$') { def regionName ->
	client.findElement {
		region ->
			region["display-name"] == regionName
	}
	.stopIfFailure()
}

