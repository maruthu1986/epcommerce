package com.elasticpath.cortex.dce.addresses

import static com.elasticpath.cortex.dce.ClasspathFluentRelosClientFactory.currentScope

import cucumber.api.groovy.EN
import cucumber.api.groovy.Hooks

import static com.elasticpath.cortex.dce.ClasspathFluentRelosClientFactory.client

this.metaClass.mixin(Hooks)
this.metaClass.mixin(EN)

When(~'^I POST with (.+)') { String jsonInput ->
	client.POST("addresses/$currentScope", jsonInput)
}
