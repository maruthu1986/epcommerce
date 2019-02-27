/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.cortex.dce

import cucumber.api.groovy.EN
import cucumber.api.groovy.Hooks

import static com.elasticpath.cortex.dce.ClasspathFluentRelosClientFactory.client
import static com.elasticpath.cortex.dce.SharedConstants.DEFAULT_SCOPE

this.metaClass.mixin(Hooks)
this.metaClass.mixin(EN)

def PASSWORD = "password"
def GIVEN_NAME = "testGivenName"
def FAMILY_NAME = "testFamilyName"
def NEW_RANDOM_USERNAME = ""

Given(~'^I (?:login as|am logged in as|transition to|relogin as) (?:a|a new) public shopper$') { ->
	client.authAsAPublicUser(DEFAULT_SCOPE)
			.stopIfFailure()
}

Given(~'^I login as (?:a|another) registered shopper$') { ->
	client.authAsRegisteredUser()

	CommonMethods.clearCart()
}

Given(~'^I transition to registered shopper$') { ->
	client.roleTransitionToRegisteredUser()
			.stopIfFailure()
}

Given(~'^I have authenticated as (?:a newly|another) registered shopper$') { ->
	def userName = UUID.randomUUID().toString() + "@elasticpath.com"
	registerShopper(DEFAULT_SCOPE, FAMILY_NAME, GIVEN_NAME, PASSWORD, userName)

	client.authRegisteredUserByName(DEFAULT_SCOPE, userName)
			.stopIfFailure()
}

Given(~'I transition to the (?:newly|first) registered shopper') { ->
	client.roleTransitionToRegisteredUserByName(DEFAULT_SCOPE, NEW_RANDOM_USERNAME)
			.stopIfFailure()
}

Given(~'^I am logged into scope (.+) as a public shopper$') {
	String scope ->
		client.authAsAPublicUser(scope)
				.stopIfFailure()
}

Given(~'^I have authenticated on scope (.+) as a newly registered shopper$') { def scope ->
	NEW_RANDOM_USERNAME = UUID.randomUUID().toString() + "@elasticpath.com"
	println(NEW_RANDOM_USERNAME)
	registerShopper(scope, FAMILY_NAME, GIVEN_NAME, PASSWORD, NEW_RANDOM_USERNAME)

	client.authRegisteredUserByName(scope, NEW_RANDOM_USERNAME)
			.stopIfFailure()
}

Given(~'^I login as a newly registered shopper$') { ->
	NEW_RANDOM_USERNAME = UUID.randomUUID().toString() + "@elasticpath.com"
	println(NEW_RANDOM_USERNAME)
	registerShopper(DEFAULT_SCOPE, FAMILY_NAME, GIVEN_NAME, PASSWORD, NEW_RANDOM_USERNAME)

	client.authRegisteredUserByName(DEFAULT_SCOPE, NEW_RANDOM_USERNAME)
			.stopIfFailure()
}

Given(~'^I re-authenticate on scope (.+) with the (original|newly) registered shopper$') { def scope, def shopper ->
	client.authRegisteredUserByName(scope, NEW_RANDOM_USERNAME)
			.stopIfFailure()
}

Given(~'^I re-login with the (original|newly) registered shopper$') { def shopper ->
	client.authRegisteredUserByName(DEFAULT_SCOPE, NEW_RANDOM_USERNAME)
			.stopIfFailure()
}

Given(~'^I authenticate as a registered shopper (.+) with the default scope$') {
	String username ->
		client.authRegisteredUserByName(DEFAULT_SCOPE, username)
				.stopIfFailure()
}

Given(~'^I authenticate as a registered shopper (.+) on the default scope with a clear cart$') {
	String username ->
		client.authRegisteredUserByName(DEFAULT_SCOPE, username)
				.stopIfFailure()
		client.GET("/")
				.defaultcart()
				.lineitems()
				.stopIfFailure()
		client.DELETE(client.body.self.uri)
}

Given(~'^I authenticate as a registered shopper (.+) on scope (.+)$') {
	String username, String scope ->
		client.authRegisteredUserByName(scope, username)
				.stopIfFailure()
}

And(~'^I register and transition to a new shopper$') { ->
	def registeredShopperUsername = UUID.randomUUID().toString() + "@elasticpath.com"
	client.GET("/")
			.newaccountform()
			.registeraction("family-name": "fname", "given-name": "gname", "password": PASSWORD, "username": registeredShopperUsername)
	client.roleTransitionToRegisteredUserByName(DEFAULT_SCOPE, registeredShopperUsername)
			.stopIfFailure()
}

Given(~'^I authenticate with (.+) username (.+) and password (.+) and role (.+) in scope (.+)') {
	def scenario, def username, def password, def role, def scope ->
		client.authenticate(username, password, scope, role)
}

Then(~'I invalidate the authentication') { ->
	client.invalidateAuthentication()
}

Then(~'I set (.+) header (.+)') { String header, String value ->
	Map<String, String> headers = new HashMap<String, String>()
	headers.put(header, value)
	client.setHeaders(headers)
}

static def registerShopper(registrationScope, familyName, givenName, password, username) {
	client.authAsAPublicUser(registrationScope)

	client.GET("registrations/$registrationScope/newaccount/form")
			.registeraction("family-name": familyName, "given-name": givenName, "password": password, "username": username)
}