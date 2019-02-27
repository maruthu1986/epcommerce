/*
 * Copyright © 2016 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.cortex.dce.availabilities

import static org.assertj.core.api.Assertions.assertThat

import static com.elasticpath.cortex.dce.ClasspathFluentRelosClientFactory.client

import cucumber.api.groovy.EN
import cucumber.api.groovy.Hooks

this.metaClass.mixin(Hooks)
this.metaClass.mixin(EN)

When(~/^I view the item availability$/) { ->
	client.availability()
}

Then(~/^The availability should be (.+?)$/) { def expectedAvailability ->
	assertThat(client.body.state)
			.as("Availablility is not as expected")
			.isEqualTo(expectedAvailability)
}

Then(~'^the field release-date contains a valid date') { ->
	assertThat(client["release-date"]."display-value".toString())
			.as("Release date is not as expected")
			.matches("(January|February|March|April|May|June|July|August|September|October|November|December) [1-3]?[0-9], \\d{4} \\d{1,2}:\\d{2}:\\d{2} (AM|PM)")
}

private findByCode(String code) {
	client.GET("/")
			.lookups()
			.itemlookupform()
			.itemlookupaction(["code": code])
			.follow()
			.stopIfFailure()
}
