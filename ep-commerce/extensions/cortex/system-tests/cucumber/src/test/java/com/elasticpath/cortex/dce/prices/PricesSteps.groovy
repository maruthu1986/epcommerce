/*
 * Copyright © 2015 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.cortex.dce.prices

import cucumber.api.groovy.EN
import cucumber.api.groovy.Hooks

import static com.elasticpath.cortex.dce.ClasspathFluentRelosClientFactory.client
import static com.elasticpath.cortex.dce.CommonAssertion.assertCost
import static com.elasticpath.rest.ws.assertions.RelosAssert.assertLinkDoesNotExist

import com.elasticpath.cortex.dce.CommonMethods

this.metaClass.mixin(Hooks)
this.metaClass.mixin(EN)

Given(~'^an item (.+?) exists in my catalog$') { def productName ->
	CommonMethods.searchAndOpenItemWithKeyword(productName)
}

When(~'^I view the (?:item|lineitem) price$') { ->
	client.price()
			.stopIfFailure()
}

When(~'^I follow the fromprice link on the item definition$') { ->
	client.definition()
			.fromprice()
			.stopIfFailure()
}

When(~'^I hack a URI to attempt to GET the price for (.+?)$') { def itemDisplayName ->
	CommonMethods.searchAndOpenItemWithKeyword(itemDisplayName)

	def itemUri = client.body.self.uri
	def hackedUri = "/prices" + itemUri

	client.GET(hackedUri)
			.stopIfFailure()
}

When(~'^I view the pricing for the (.+?) configuration$') { def configurationOptionName ->
	client.item()
			.definition()
			.options()
			.element()
			.selector()
			.findChoice() { configurationOption ->
		def description = configurationOption.description()
		description["name"] == configurationOptionName
	}
	.selectaction()
			.follow()
			.price()
			.stopIfFailure()
}

Then(~'^the list-price has fields amount: (.+?), currency: (.+?) and display: (.+?)$') {
	String expectedAmount, String expectedCurrency, String expectedDisplayName ->

		def listPriceElement = client.body.'list-price'
		assertCost(listPriceElement, expectedAmount, expectedCurrency, expectedDisplayName)
		client.stopIfFailure()
}

Then(~'^the from-price has fields amount: (.+?), currency: (.+?) and display: (.+?)$') {
	String expectedAmount, String expectedCurrency, String expectedDisplayName ->

		def fromPriceElement = client.body.'from-price'
		assertCost(fromPriceElement, expectedAmount, expectedCurrency, expectedDisplayName)
		client.stopIfFailure()
}

Then(~'^the lineitem price has list-price fields amount: (.+?), currency: (.+?) and display: (.+?)$') {
	String expectedAmount, String expectedCurrency, String expectedDisplayName ->

		client.price()
		def listPriceElement = client.body.'list-price'
		assertCost(listPriceElement, expectedAmount, expectedCurrency, expectedDisplayName)
		client.stopIfFailure()
}

And(~'^the lineitem price has purchase-price fields amount: (.+?), currency: (.+?) and display: (.+?)$') {
	String expectedAmount, String expectedCurrency, String expectedDisplayName ->

		def costElement = client.body.'purchase-price'
		assertCost(costElement, expectedAmount, expectedCurrency, expectedDisplayName)
		client.stopIfFailure()
}

Then(~'^the item does not have a (.+?) link$') { def link ->
	assertLinkDoesNotExist(client, link)
}

Then(~'^the cost fields has amount: (.+?), currency: (.+?) and display: (.+?)$') {
	String expectedAmount, String expectedCurrency, String expectedDisplayName ->

		def listPriceElement = client.body.cost
		assertCost(listPriceElement, expectedAmount, expectedCurrency, expectedDisplayName)
		client.stopIfFailure()

}