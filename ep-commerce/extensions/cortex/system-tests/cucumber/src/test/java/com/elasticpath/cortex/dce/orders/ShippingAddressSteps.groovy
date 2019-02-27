package com.elasticpath.cortex.dce.orders

import cucumber.api.groovy.EN
import cucumber.api.groovy.Hooks

import static com.elasticpath.cortex.dce.ClasspathFluentRelosClientFactory.client
import static com.elasticpath.cortex.dce.orders.OrderConstants.*
import static com.elasticpath.rest.ws.assertions.RelosAssert.assertLinkDoesNotExist

import static org.assertj.core.api.Assertions.assertThat

import com.elasticpath.cortex.dce.CommonMethods

this.metaClass.mixin(Hooks)
this.metaClass.mixin(EN)

def orderShippingAddressUri;
def defaultShippingAddressUri;


And(~'^Shopper gets the default shipping address$') { ->
	client.GET("/")
			.defaultprofile()
			.addresses()
			.shippingaddresses()
			.default()
			.stopIfFailure()

	defaultShippingAddressUri = client.body.self.uri
}

And(~'^the shoppers order does not have a shipping address applied$') { ->
	CommonMethods.searchAndOpenItemWithKeyword(SHIPPING_PRODUCT)
	client.addtocartform()
			.addtodefaultcartaction(quantity: 1)
			.GET("/")
			.defaultcart()
			.order()
			.deliveries()
			.element()
			.destinationinfo()
			.stopIfFailure()

	assertLinkDoesNotExist(client, DESTINATION_LINK)
}

When(~'^I create a default shipping address on the profile$') { ->
	CommonMethods.createUniqueAddress()

	client.GET("/")
			.defaultprofile()
			.addresses()
			.shippingaddresses()
			.default()
			.stopIfFailure()

	defaultShippingAddressUri = client.body.self.uri
}

When(~'^I retrieve the shoppers shipping address info on the order$') { ->

	CommonMethods.searchAndOpenItemWithKeyword(SHIPPING_PRODUCT)
	client.addtocartform()
			.addtodefaultcartaction(quantity: 1)
			.GET("/")
			.defaultcart()
			.order()
			.deliveries()
			.element()
			.destinationinfo()
			.destination()
			.stopIfFailure()

	orderShippingAddressUri = client.body.self.uri
}

Then(~'^the default shipping address is automatically applied to the order$') { ->
	assertThat(defaultShippingAddressUri)
			.as("Shipping address was not as expected")
			.isEqualTo(orderShippingAddressUri)
}