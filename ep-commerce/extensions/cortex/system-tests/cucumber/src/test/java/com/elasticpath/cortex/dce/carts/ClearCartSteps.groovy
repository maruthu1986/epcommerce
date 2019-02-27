package com.elasticpath.cortex.dce.carts

import static org.assertj.core.api.Assertions.assertThat

import cucumber.api.groovy.EN
import cucumber.api.groovy.Hooks

import static com.elasticpath.cortex.dce.ClasspathFluentRelosClientFactory.client
import static com.elasticpath.cortex.dce.SharedConstants.DEFAULT_SCOPE
import static com.elasticpath.cortex.dce.CommonAssertion.assertCost

import com.elasticpath.cortex.dce.CommonMethods

this.metaClass.mixin(Hooks)
this.metaClass.mixin(EN)


Then(~'the total quantity in the cart is (.+)$') { String quantity ->
	client.GET("/")
			.defaultcart()
			.stopIfFailure()
	assertThat(client.body.'total-quantity')
			.as("The cart total quantity is not as expected")
			.isEqualTo(quantity.toInteger())
}

Then(~'I clear the cart$') { ->
	CommonMethods.clearCart()
	assertThat(client.response.status)
			.as("HTTP response status is not as expected")
			.isEqualTo(204)
}

Then(~'there are no lineitems in the cart$') { ->
	client.GET("/")
			.defaultcart()
			.lineitems()
			.stopIfFailure()

	assertThat(client.body.links[0].rel)
			.as("Cart lineitem links are not as expected")
			.isEqualTo("cart")
	assertThat(client.body.links[1])
			.as("Cart lineitem links are not as expected")
			.isEqualTo(null)
	client.GET("/").defaultcart()
}


Then(~'I save the cart URI and login in as another shopper$') { ->
	client.GET("/").defaultcart()
	CART_URI = client.body.self.uri

	client.authRegisteredUserByName(DEFAULT_SCOPE, "harry.potter@elasticpath.com")
}

Then(~"I attempt to clear the first shopper's cart") { ->
	client.DELETE(CART_URI + "/lineitems")
}

Then(~'I am forbidden from (?:.+)$') { ->
	assertThat(client.response.status)
			.as("HTTP response status is not as expected")
			.isEqualTo(403)

}

Then(~"the first shopper's cart is not cleared") { ->
	client.GET("/")
			.defaultcart() //clear the 403 status that causes test to exit
			.authAsRegisteredUser()
			.GET("/")
			.defaultcart()
			.stopIfFailure()
	assertThat(client.body.'total-quantity')
			.as("The cart total quantity is not as expected")
			.isEqualTo(1)
}

Then(~'the cart total has amount: (.+), currency: (.+) and display: (.+)$') {
	String amount, String currency, String display ->
		client.GET("/")
				.defaultcart()
				.total()
				.stopIfFailure()
		assertCost(client.body.cost[0], amount, currency, display)
}

Then(~'the order total has amount: (.+), currency: (.+) and display: (.+)$') {
	String amount, String currency, String display ->
		client.GET("/")
				.defaultcart()
				.order()
				.total()
				.stopIfFailure()
		assertCost(client.body.cost[0], amount, currency, display)
}