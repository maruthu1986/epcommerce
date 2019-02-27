package com.elasticpath.cortex.dce.addresses

import cucumber.api.groovy.EN
import cucumber.api.groovy.Hooks

import static com.elasticpath.cortex.dce.ClasspathFluentRelosClientFactory.client

this.metaClass.mixin(Hooks)
this.metaClass.mixin(EN)

When(~'^I get the default billing address$') { ->
	String uri = getDefaultBillingAddressUri()
	client.GET(uri)

}

When(~'I put to default billing address$') { ->
	String uri = getDefaultBillingAddressUri()

	client.PUT(uri, '{}')
}

When(~'I post to default billing address$') { ->
	String uri = getDefaultBillingAddressUri()

	client.POST(uri, '{}')
}

When(~'I delete the default billing address$') { ->
	String uri = getDefaultBillingAddressUri()

	client.DELETE(uri)
}

private static String getDefaultBillingAddressUri() {
	client.GET("/")
			.defaultprofile()
			.addresses()
			.billingaddresses()

	return client.body.self.uri + "/default"
}
