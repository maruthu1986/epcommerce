package com.elasticpath.cortex.dce.discounts

import cucumber.api.groovy.EN
import cucumber.api.groovy.Hooks

import static com.elasticpath.cortex.dce.ClasspathFluentRelosClientFactory.client
import static com.elasticpath.cortex.dce.CommonAssertion.assertCost
import static org.assertj.core.api.Assertions.assertThat
import com.elasticpath.cortex.dce.CommonMethods

this.metaClass.mixin(Hooks)
this.metaClass.mixin(EN)

def PURCHASE_URI

Then(~'the line item triggering (?:.+) has purchase price of (.+)$') { def value ->
	client.GET("/")
			.defaultcart()
			.total()
	assertThat(client.body.cost[0]["display"])
			.as("Purchase price is not as expected")
			.isEqualTo(value)
}

Then(~'^the cart discount fields has amount: (.+?), currency: (.+?) and display: (.+?)$') {
	String expectedAmount, String expectedCurrency, String expectedDisplayName ->
		client.GET("/")
				.defaultcart()
				.discount()

		def listDiscountElement = client.body.discount
		assertCost(listDiscountElement, expectedAmount, expectedCurrency, expectedDisplayName)

}

And(~/^the cart discount has currency (.+) and display (.+)$/) { def expectedCurrency, def expectedDisplayName ->
	client.GET("/")
			.defaultcart()
			.discount()

	def listDiscountElement = client.body.discount
	String expectedAmount = client.body.discount[0].amount
	assertCost(listDiscountElement, expectedAmount, expectedCurrency, expectedDisplayName)
}

Then(~'^the cart discount amount is (.+?)$') {
	String expectedDisplay ->
		client.GET("/")
				.defaultcart()
				.discount()

		def listDiscountElement = client.body.discount
		assertThat(listDiscountElement["display"][0])
				.as("Display amount is not as expected")
				.isEqualTo(expectedDisplay)

}

Then(~'the cart total is unaffected by the shipping discount and has value (.+)$') { def value ->
	client.GET("/")
			.defaultcart()
			.total()

	assertThat(client.body.cost[0]["display"])
			.as("Cart total is not as expected")
			.isEqualTo(value)
}

Then(~'I view a cart discount$') { ->
	client.GET("/")
			.defaultcart()
			.discount()
			.stopIfFailure()
}

Then(~'I can traverse back to the cart following a link$') { ->
	client.cart()
			.stopIfFailure()
}

Then(~'I retrieve the purchase$') { ->
	CommonMethods.submitPurchase()
	client.follow()
			.stopIfFailure()
	PURCHASE_URI = client.body.self.uri
}

Then(~'the purchase discount (.+) has value (.+)$') { def field, def value ->
	client.GET(PURCHASE_URI)
	client.discount()
			.stopIfFailure()

	assertThat(client.body.discount[0][field])
			.as("Purchase discount value is not as expected")
			.isEqualTo(value)
}

Then(~'the purchase total reflects the discount and has (.+) of (.+)$') { def field, def value ->
	client.GET(PURCHASE_URI)
			.stopIfFailure()

	assertThat(client.body."monetary-total"[0][field])
			.as("Purchase total is not as expected")
			.isEqualTo(value)
}

Then(~'the purchase discount amount is (.+)$') { def expectedDisplay ->
	client.GET(PURCHASE_URI)
	client.discount()
			.stopIfFailure()
	def costElement = client.body.'discount'
	assertThat(costElement["display"][0])
			.as("Display amount is not as expected")
			.isEqualTo(expectedDisplay)
}

Then(~'the purchase total after discount is (.+)') { def expectedDisplay ->

	client.GET(PURCHASE_URI)
			.stopIfFailure()
	def costElement = client.body."monetary-total"

	assertThat(costElement["display"][0])
			.as("Display amount is not as expected")
			.isEqualTo(expectedDisplay)
}

Then(~'the line item (.+?) that had (?:.+) discount has the amount (.+)$') {
	def itemName, def expectedDisplay ->
		client.GET(PURCHASE_URI)
				.lineitems()
				.findElement {
			lineitem -> lineitem["name"] == itemName
		}
				.body
		def costElement = client.body."line-extension-amount"
		assertThat(costElement["display"][0])
				.as("Display amount is not as expected")
				.isEqualTo(expectedDisplay)
}

Given(~'item (?:.+) triggers the (?:.+) promotion (?:.+)') { -> }

Given(~'item (?:.+) has a purchase price of (?:.+)') { -> }