package com.elasticpath.cortex.dce.addresses

import static org.assertj.core.api.Assertions.assertThat

import static com.elasticpath.cortex.dce.ClasspathFluentRelosClientFactory.client
import static com.elasticpath.cortex.dce.addresses.AddressConstants.*

import cucumber.api.groovy.EN
import cucumber.api.groovy.Hooks

this.metaClass.mixin(Hooks)
this.metaClass.mixin(EN)


When(~'^I PUT with (.+)') { String jsonInput ->
	client.PUT(getAddressUri(), jsonInput)
}

When(~'I PUT without input$') { ->
	client.PUT(getAddressUri(), '')
}
And(~'^Error message contains (.+)') { String expectedSnippet ->
	String errorMessage = getClient().getBodyRaw().getText()
	assertThat(errorMessage)
			.as("Error message is not as expected")
			.contains(expectedSnippet)
}

And(~'there is a link to a specific address$') { ->
	goToAddress()
	assertThat(client.body.links[0].type)
			.as("Link type is not as expected")
			.isEqualTo(ADDRESS_LINK_TYPE)
}

private String getAddressUri() {
	goToAddress()
	String uri = client.body.links[0].href
	return uri
}