package com.elasticpath.cortex.dce.recommendations

import static com.elasticpath.cortex.dce.ClasspathFluentRelosClientFactory.client
import static com.elasticpath.cortex.dce.SharedConstants.*
import static org.assertj.core.api.Assertions.assertThat

import cucumber.api.groovy.EN
import cucumber.api.groovy.Hooks

this.metaClass.mixin(Hooks)
this.metaClass.mixin(EN)


def recommendationsUri;

When(~'^I retrieve the (crosssell|upsell|replacement|warranty|accessory) for this item$') { def recommendationType ->
	client."$recommendationType"()
			.stopIfFailure()
}

When(~'^I go to cross-sell for an item$') { ->
	client.crosssell()
			.stopIfFailure()
}

Then(~'^I get the ([0-9]*) recommended items$') { def numRecommendedItems ->
	def response = client.body.links.findAll {
		link -> link.rel
	}
	assertThat(response.size())
			.as("The number of recommended items is not as expected")
			.isEqualTo(numRecommendedItems.toInteger())
}

And(~'^The ordering is correctly preserved (.+?)$') { String firstAssociationName ->
	client.element()
			.definition()
			.stopIfFailure()
	assertThat(client.body[DISPLAY_NAME_FIELD])
			.as("The first association name is not as expected")
			.isEqualTo(firstAssociationName)
}

When(~'^I zoom (.+?) into the cross-sells for this item$') { String zoomParam ->
	recommendationsUri = client.body.self.uri
	client.GET(recommendationsUri + zoomParam)
}

Then(~'^The zoom ordering is correctly preserved (.+?) and (.+?)$') { String firstAssociationName, String secondAssociationName ->
	assertThat(client.body._crosssell._element._definition[DISPLAY_NAME_FIELD][0][0][0])
			.as("The first association name is not as expected")
			.isEqualTo(firstAssociationName)
	assertThat(client.body._crosssell._element._definition[DISPLAY_NAME_FIELD][0][1][0])
			.as("The second association name is not as expected")
			.isEqualTo(secondAssociationName)
}

Then(~'^cross-sell item is not present') { ->
	def response = client.body.links.findAll {
		link -> link.rel
	}
	assertThat(response.size())
			.as("The cross-sell item should not be present")
			.isEqualTo(0)
}
