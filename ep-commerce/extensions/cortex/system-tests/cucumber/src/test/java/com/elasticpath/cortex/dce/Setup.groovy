/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.cortex.dce

import static com.elasticpath.cortex.dce.ClasspathFluentRelosClientFactory.getClient

import cucumber.api.groovy.Hooks

import static com.elasticpath.cortex.dce.ClasspathFluentRelosClientFactory.createClient

this.metaClass.mixin(Hooks)

Before() {
	createClient()
}

Before("@HAL") {
	client.getHeaders().Accept = "application/hal+json"
	def restClient = client.getRestClient()
	restClient.parser.'application/hal+json' = restClient.parser.'application/json'
}