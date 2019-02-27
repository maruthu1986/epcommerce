package com.elasticpath.cortex.dce.events

import cucumber.api.groovy.EN
import cucumber.api.groovy.Hooks

import static com.elasticpath.cortex.dce.ClasspathFluentRelosClientFactory.getClient

this.metaClass.mixin(Hooks)
this.metaClass.mixin(EN)

When(~'^I POST an event (.+) to URI (.+)$') { String jsonInput, String eventsResource ->
	String uri = "/" + eventsResource
	client.POST(uri, jsonInput)
			.stopIfFailure()
}
