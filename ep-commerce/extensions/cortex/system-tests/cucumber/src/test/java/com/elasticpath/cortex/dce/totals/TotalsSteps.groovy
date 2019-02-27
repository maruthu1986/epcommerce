package com.elasticpath.cortex.dce.totals

import cucumber.api.groovy.EN
import cucumber.api.groovy.Hooks

import static com.elasticpath.cortex.dce.CommonAssertion.assertCost
import static com.elasticpath.cortex.dce.ClasspathFluentRelosClientFactory.client

this.metaClass.mixin(Hooks)
this.metaClass.mixin(EN)


Then(~'^I follow the total link from the cart lineitem for (.+)$') { def itemName ->
	client.GET("/")
			.defaultcart()
			.lineitems()
			.findElement {
		lineitem ->
			def definition = lineitem.item().definition()
			definition["display-name"] == itemName
	}
	.total()
			.stopIfFailure()
}

And(~'^the line item purchase price fields has amount: (.+), currency: (.+) and display: (.+)$') {
	String expectedAmount, String expectedCurrency, String expectedDisplay ->
		def purchasePriceElement = client.price().body."purchase-price"[0]

		assertCost(purchasePriceElement, expectedAmount, expectedCurrency, expectedDisplay)
}

Then(~'^I retrieve the cart total$') { ->
	client.GET("/")
			.defaultcart()
			.total()
			.stopIfFailure()
}

Then(~'^the cart total does not include discounts$') { ->
//do nothing step added for business clarity
}

Then(~'^the cost is the sum of each lineitem$') { ->
//do nothing step added for business clarity
}

Then(~'there is no lineitem in cart$') { ->
//do nothing step added for business clarity
}