package com.elasticpath.cortex.dce.profile

import static com.elasticpath.cortex.dce.ClasspathFluentRelosClientFactory.currentScope

import cucumber.api.groovy.EN
import cucumber.api.groovy.Hooks

import static com.elasticpath.cortex.dce.ClasspathFluentRelosClientFactory.getClient
import static com.elasticpath.cortex.dce.SharedConstants.DEFAULT_SCOPE

import static org.assertj.core.api.Assertions.assertThat

import com.elasticpath.cortex.dce.CommonMethods

this.metaClass.mixin(Hooks)
this.metaClass.mixin(EN)

def newUserName

When(~'^I create a new shopper profile with family-name (.+), given-name (.+), password (.+), and unique user name$') {
	String familyName, String givenName, String password ->
		newUserName = UUID.randomUUID().toString() + "@elasticpath.com"
		registerShopper(DEFAULT_SCOPE, familyName, givenName, password, newUserName)
				.stopIfFailure()
}

When(~'^I authenticate with newly created shopper$') { ->
	client.authRegisteredUserByName(DEFAULT_SCOPE, newUserName)
			.stopIfFailure()
}

When(~'^I create a new shopper profile with family-name (.*), given-name (.*), password (.*), and user name (.+) in scope (.+)$') {
	String familyName, String givenName, String password, String existingUserName, String scope ->
		registerShopper(scope, familyName, givenName, password, existingUserName)
				.stopIfFailure()
}

Then(~'^I should see my profile name as family-name (.+) and given-name (.+)$') { String familyName, String givenName ->
	client.GET("/")
			.defaultprofile()
			.stopIfFailure()
	assertThat(client["family-name"])
			.as("Family name is not as expected")
			.isEqualTo(familyName)
	assertThat(client["given-name"])
			.as("Given name is not as expected")
			.isEqualTo(givenName)
}

/**
 * The OOTB Cortex behaviours is that in order to update email, user needs to use createemailaction
 * which will replace the old email value.
 */
When(~'^(?:I create my email id|I update my email id with new email) and I can see the new email id in my profile$') {
	->
	def userName = UUID.randomUUID().toString() + "@elasticpath.com"
	CommonMethods.addEmail(userName)
	client.follow()
	assertThat(client["email"])
			.as("The email is not as expected")
			.isEqualTo(userName)
}

When(~'^I create invalid email (.*)$') { String email ->
	CommonMethods.addEmail(email)
}

When(~'^I update my profile family-name (.*) and given-name (.*)$') { String familyName, String firstName ->
	client.GET("/")
			.defaultprofile()
			.stopIfFailure()

	def profileURI = getClient().body.self.uri
	client.PUT(profileURI, [
			"family-name": familyName,
			"given-name" : firstName
	])
}

When(~'^I authenticate as another shopper and attempt to update the other shoppers profile family-name (.+) and given-name (.+)') { String familyName, String firstName ->
	client.GET("/")
			.defaultprofile()
			.stopIfFailure()

	def profileURI = getClient().body.self.uri

	client.authAsRegisteredUser()
	client.PUT(profileURI, [
			"family-name": familyName,
			"given-name" : firstName
	])
}

When(~'^I POST to registration with body (.+)') { String jsonInput ->
	client.GET("/")
			.newaccountform()
			.stopIfFailure()
	client.POST("registrations/$currentScope/newaccount/form", jsonInput)
			.stopIfFailure()
}

When(~'^I PUT to profile with json body (.+)') { String jsonInput ->
	client.GET("/")
			.defaultprofile()
			.stopIfFailure()

	def profileURI = getClient().body.self.uri
	client.PUT(profileURI, jsonInput)
			.stopIfFailure()
}

def registerShopper(registrationScope, familyName, givenName, password, username) {
	client.authAsAPublicUser(registrationScope)

	client.GET("registrations/$registrationScope/newaccount/form")
			.registeraction("family-name": familyName, "given-name": givenName, "password": password, "username": username)
}