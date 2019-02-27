package com.elasticpath.cortex.dce.orders

import static org.assertj.core.api.Assertions.assertThat

import static com.elasticpath.cortex.dce.ClasspathFluentRelosClientFactory.client
import static com.elasticpath.cortex.dce.orders.OrderConstants.BILLING_ADDRESS_LINK
import static com.elasticpath.rest.ws.assertions.RelosAssert.assertLinkDoesNotExist

import cucumber.api.groovy.EN
import cucumber.api.groovy.Hooks

import com.elasticpath.cortex.dce.CommonMethods

this.metaClass.mixin(Hooks)
this.metaClass.mixin(EN)

def orderBillingAddressUri;
def defaultBillingAddressUri;


And(~'^Shopper gets the default billing address$') { ->
	client.GET("/")
			.defaultprofile()
			.addresses()
			.billingaddresses()
			.default()
			.stopIfFailure()

	defaultBillingAddressUri = client.body.self.uri
}

And(~'^the shoppers order does not have a billing address applied$') { ->
	client.GET("/")
			.defaultcart()
			.order()
			.billingaddressinfo()
			.stopIfFailure()

	assertLinkDoesNotExist(client, BILLING_ADDRESS_LINK)
}
When(~'^I create a default billing address on the profile$') { ->
	CommonMethods.createUniqueAddress()
	client.GET("/")
			.defaultprofile()
			.addresses()
			.billingaddresses()
			.default()
			.stopIfFailure()

	defaultBillingAddressUri = client.body.self.uri
}

When(~'^I retrieve the shoppers billing address info on the order$') { ->
	client.GET("/")
			.defaultcart()
			.order()
			.billingaddressinfo()
			.billingaddress()
			.stopIfFailure()

	orderBillingAddressUri = client.body.self.uri
}

Then(~'^the default billing address is automatically applied to the order$') { ->
	assertThat(orderBillingAddressUri)
			.as("Billing address is not as expected")
			.isEqualTo(defaultBillingAddressUri)
}

When(~'^I retrieve the order$') { ->
	client.GET("/")
			.defaultcart()
			.order()
			.stopIfFailure()
}

And(~'^billing address is selected$') { ->
	CommonMethods.createUniqueAddress()
}

Then(~'I use the selectaction') { ->
	client.selectaction()
}

Then(~'I post the selectaction') { ->
	client.selectaction()
			.stopIfFailure()
}

