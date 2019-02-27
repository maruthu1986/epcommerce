/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.cortex.dce

import com.elasticpath.rest.ws.client.FluentRelosClient
import com.elasticpath.rest.ws.fixture.alias.AuthenticationFactory
import com.elasticpath.rest.ws.fixture.alias.AuthenticationTypeEnum

import org.apache.commons.lang3.StringUtils

/**
 * Creates fluent relos client using the ep.rest.baseurl system property or falls back on default.
 */
class ClasspathFluentRelosClientFactory {

	public static FluentRelosClient client;

	public static currentScope;

	def static createClient() {
		String baseUrl = StringUtils.defaultString(System.getProperty("ep.rest.baseurl"), "http://localhost:9080/cortex/")
		println "Starting Cucumber tests at: $baseUrl"
		String authType = StringUtils.defaultString(System.getProperty("ep.rest.authtype"),
				AuthenticationTypeEnum.OAUTH2_AUTHENTICATION.getName())
		println "Starting Cucumber tests with authentication type: $authType"

		client = new FluentRelosClient(baseUrl)

		def authentication = AuthenticationFactory.getAuthentication(authType);
		client.alias(authentication)
	}

	static FluentRelosClient getClient() {
		client
	}

}
