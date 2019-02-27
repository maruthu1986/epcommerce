package com.elasticpath.cortex.dce.customerSegments

import static org.assertj.core.api.Assertions.assertThat

import static com.elasticpath.cortex.dce.ClasspathFluentRelosClientFactory.client
import static com.elasticpath.cortex.dce.SharedConstants.*
import static com.elasticpath.cortex.dce.CommonAssertion.assertCost

import cucumber.api.groovy.EN
import cucumber.api.groovy.Hooks

import com.elasticpath.cortex.dce.CommonMethods

this.metaClass.mixin(Hooks)
this.metaClass.mixin(EN)

def final CUSTOMER_SEGMENT_CONTENT_RELATIVE_LOCATION = "images/gift_bag.gif"
def final USER_TRAITS_HEADER = "x-ep-user-traits"

When(~/^I am a member of customer segment (.+?)$/) { String valueUserTraits ->
	client.headers.put(USER_TRAITS_HEADER, "CUSTOMER_SEGMENT=" + valueUserTraits)
}

Then(~'^the customer segment promotion discount is (.+)$') { String expectedDiscountDisplay ->
	client.cart()
			.discount()
			.stopIfFailure()
	def actualCustSegDiscountDisplay = client.body.discount[0].display
	assertThat(actualCustSegDiscountDisplay)
			.as("The customer segment promotion discount is not as expected.")
			.isEqualTo(expectedDiscountDisplay)
}

When(~'^I go to item price') { ->
	client.price()
}

And(~'I view the line item price') { ->
	client.item()
			.price()
			.stopIfFailure()
}

And(~/^the line-item has list amount: (.+?), currency: (.+?) and display: (.+?)$/) {
	String listAmount, String listCurrency, String listDisplay ->
		def listPrice = client.body.'list-price'
		assertCost(listPrice, listAmount, listCurrency, listDisplay)
}

And(~/^the item list price currency is (.+) and the display is (.+)$/) { String listCurrency, String listDisplay ->
	client.item()
			.price()
			.stopIfFailure()
	def listPrice = client.body.'list-price'
	String listAmount = client.body.'list-price'[0].amount
	assertCost(listPrice, listAmount, listCurrency, listDisplay)
}

And(~/^the item purchase price currency is (.+) and the display is (.+)$/) { String purchaseCurrency, String purchaseDisplay ->
	def purchasePrice = client.body.'purchase-price'
	String purchaseAmount = client.body.'purchase-price'[0].amount
	assertCost(purchasePrice, purchaseAmount, purchaseCurrency, purchaseDisplay)
}

And(~/^the line-item has purchase amount: (.+?), currency: (.+?) and display: (.+?)$/) {
	String purchaseAmount, String purchaseCurrency, String purchaseDisplay ->
		def purchasePrice = client.body.'purchase-price'
		assertCost(purchasePrice, purchaseAmount, purchaseCurrency, purchaseDisplay)
}

Then(~'^the item (.+) list price is (.+) and purchase price is (.+)$') { String itemName, String expectedListPrice, String expectedPurchasePrice ->
	client.GET("/")
			.defaultcart()
	client.lineitems()

	CommonMethods.findCartElementByDisplayName(itemName)

	client.price()

	def listPrice = client.body.'list-price'[0].display
	def purchasePrice = client.body.'purchase-price'[0].display
	assertThat(listPrice)
			.as("The list price is not as expected")
			.isEqualTo(expectedListPrice)
	assertThat(purchasePrice)
			.as("The purchase price is not as expected")
			.isEqualTo(expectedPurchasePrice)

}

Given(~'^a customer segment (?:promotion|price list assignment) exists.*') { -> }

Given(~'an item .* with purchase price of .*') { -> }