package com.elasticpath.cortex.dce.recommendations

import cucumber.api.groovy.EN
import cucumber.api.groovy.Hooks

import static com.elasticpath.cortex.dce.ClasspathFluentRelosClientFactory.client
import static com.elasticpath.rest.ws.assertions.RelosAssert.assertLinkExists
import static org.assertj.core.api.Assertions.assertThat

this.metaClass.mixin(Hooks)
this.metaClass.mixin(EN)

When(~'^I go to recommendations for an item$') { ->
	client.recommendations()
			.stopIfFailure()
}
Then(~'^I get all the recommendation groups that exist$') { ->
	// mapped from CE to Crtex - currently there are 6 (cross sells, upsells, warranties, topsellers, accessories, replacements)

	def response = client.body.links.findAll {
		link -> link.rel
	}
	assertThat(response.size())
			.as("Number of links not as expected")
			.isEqualTo(6)
	assertThat(assertLinkExists(client, "crosssell"))
			.as("Cross Sell link does not exist")
			.isTrue()
	assertThat(assertLinkExists(client, "upsell"))
			.as("Upsell link does not exist")
			.isTrue()
	assertThat(assertLinkExists(client, "replacement"))
			.as("Replacement link does not exist")
			.isTrue()
	assertThat(assertLinkExists(client, "accessory"))
			.as("Accessory link does not exist")
			.isTrue()
	assertThat(assertLinkExists(client, "warranty"))
			.as("Warrenty link does not exist")
			.isTrue()
	assertThat(assertLinkExists(client, "recommendation"))
			.as("Recommendation link does not exist")
			.isTrue()
}