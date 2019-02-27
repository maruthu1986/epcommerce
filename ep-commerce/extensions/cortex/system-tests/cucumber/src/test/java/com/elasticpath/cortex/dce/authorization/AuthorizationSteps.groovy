package com.elasticpath.cortex.dce.authorization

import static com.elasticpath.cortex.dce.ClasspathFluentRelosClientFactory.client
import static org.assertj.core.api.Assertions.assertThat

import cucumber.api.groovy.EN
import cucumber.api.groovy.Hooks

this.metaClass.mixin(Hooks)
this.metaClass.mixin(EN)


Then(~'capture the uri of the registered shopper\'s cart line item price$') { ->
	client.GET("/")
			.defaultcart()
			.lineitems()
			.element()
			.price()
			.stopIfFailure()
	CART_LINE_ITEM_PRICE_URI = client.body.self.uri
}

Then(~'I attempt to view another shopper\'s cart line item price$') { ->
	client.GET(CART_LINE_ITEM_PRICE_URI)
			.stopIfFailure()
}

Then(~'I am not able to view another shopper\'s cart line item price$') { ->
	assertThat(client.response.status)
			.as("HTTP response status is not as expected")
			.isEqualTo(403)
	client.follow()
}