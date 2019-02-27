package com.elasticpath.cortex.dce.paymentmethods

import static org.assertj.core.api.Assertions.assertThat

import static com.elasticpath.cortex.dce.ClasspathFluentRelosClientFactory.client
import static com.elasticpath.cortex.dce.SharedConstants.*
import static com.elasticpath.cortex.dce.paymentmethods.PaymentmethodsConstants.*
import static com.elasticpath.rest.ws.assertions.RelosAssert.*

import cucumber.api.groovy.EN
import cucumber.api.groovy.Hooks

import com.elasticpath.cortex.dce.CommonMethods

this.metaClass.mixin(Hooks)
this.metaClass.mixin(EN)

String defaultPaymentMethodDisplayValue

Given(~'^I authenticate as a registered shopper who has a token as their default payment method$') { ->
//   default test user has tokens as payment method by default
	client.authRegisteredUserByName(DEFAULT_SCOPE, DEFAULT_SCOPE_TEST_USER)
}

Given(~'^a registered shopper has payment methods saved to his profile$') { ->
	registerNewShopperAndAuthenticate()

	def paymentTokenForm = client.GET("/")
			.defaultprofile()
			.paymentmethods()
			.paymenttokenform()
			.save()

	client.resume(paymentTokenForm)
			.createpaymenttokenaction(["display-name": TEST_TOKEN_DISPLAY_VALUE_X, "token": TEST_TOKEN_VALUE_X])
			.stopIfFailure()

	client.resume(paymentTokenForm)
			.createpaymenttokenaction(["display-name": TEST_TOKEN_DISPLAY_VALUE_Y, "token": TEST_TOKEN_VALUE_Y])
			.stopIfFailure()
}

Given(~'^I authenticate as a registered shopper with a saved payment method on my profile$') { ->
	registerNewShopperAndAuthenticate()
	CommonMethods.addTokenToProfile(TEST_TOKEN_DISPLAY_VALUE_X, TEST_TOKEN_VALUE_X)
}

Given(~'^a purchase was made with payment token$') { ->
	client.authRegisteredUserByName(DEFAULT_SCOPE, DEFAULT_SCOPE_TEST_USER)

	CommonMethods.searchAndOpenItemWithKeyword(PURCHASEABLE_NON_SHIPPABLE_ITEM)

	client.addtocartform()
			.addtodefaultcartaction(quantity: 1)
			.stopIfFailure()

	saveTokenDetails()

	CommonMethods.submitPurchase()
	client.stopIfFailure()
}

And(~'^my order does not have a payment method applied$') { ->
	client.GET("/")
			.defaultcart()
			.order()
			.paymentmethodinfo()
			.stopIfFailure()

	assertLinkDoesNotExist(client, "paymentmethod")
}

Given(~'^a free product was purchased without payment$') { ->
	client.authRegisteredUserByName(DEFAULT_SCOPE, DEFAULT_SCOPE_TEST_USER)

	CommonMethods.searchAndOpenItemWithKeyword(ZERO_DOLLAR_ITEM)

	client.addtocartform()
			.addtodefaultcartaction(quantity: 1)
			.stopIfFailure()
	CommonMethods.submitPurchase()
	client.stopIfFailure()
}

Given(~'^I authenticate as a shopper with payment tokens X Y and Z and X as the default$') { ->
	client.authRegisteredUserByName(TOKEN_SCOPE, TEST_USER_WITH_TOKENS)

	defaultPaymentMethodDisplayValue = client.GET("/")
			.defaultprofile()
			.paymentmethods()
			.default()
	["display-value"]
}

Given(~'^I authenticate as a shopper with payment method X as the chosen payment method for their order$') { ->
	client.authRegisteredUserByName(TOKEN_SCOPE, TEST_USER_WITH_TOKENS)
}

Given(~'^I authenticate as a shopper with saved payment methods X Y and Z and payment method X is chosen on their order$') { ->
	client.authRegisteredUserByName(TOKEN_SCOPE, TEST_USER_WITH_TOKENS)

	client.GET("/")
			.navigations()
			.findElement {
		category ->
			category["name"] == TOKEN_STORE_TEST_CATEGORY
	}
	.items()
	CommonMethods.findItemByDisplayName(TOKEN_STORE_PURCHASEABLE_ITEM)
	client.addtocartform()
			.addtodefaultcartaction(quantity: 1)

	selectorRepresentationResponse = client.GET("/")
			.defaultcart()
			.order()
			.paymentmethodinfo()
			.selector()
			.save()

	client.resume(selectorRepresentationResponse)
			.chosen()
			.description()
	preChosenPaymentMethod = client["display-name"]
}

When(~'^I retrieve the default payment method on my profile$') { ->
	client.GET("/")
			.defaultprofile()
			.paymentmethods()
			.default()
			.stopIfFailure()
}

When(~'^I create a payment method for my profile$') { ->
	CommonMethods.addTokenToProfile()
}

When(~'^a payment method is deleted from the profile$') { ->
	client.GET("/")
			.defaultprofile()
			.paymentmethods()
			.findElement {
		paymenttoken ->
			paymenttoken["display-name"] == TEST_TOKEN_DISPLAY_VALUE_X
	}
	.stopIfFailure()

	def paymenttokenuri = client.body.self.uri

	client.DELETE(paymenttokenuri)
			.stopIfFailure()
}

When(~'^the registered shopper attempts to edit payment method$') { ->
	client.GET("/")
			.defaultprofile()
			.paymentmethods()
			.findElement {
		paymenttoken ->
			paymenttoken["display-name"] == TEST_TOKEN_DISPLAY_NAME
	}
	.stopIfFailure()
	def paymenttokenuri = client.body.self.uri
	client.PUT(paymenttokenuri, [
			"display-name": ""
	])
			.stopIfFailure()
}

When(~'^I view the payment methods available to be selected for my order$') { ->
	paymentMethodSelector = client.GET("/")
			.defaultcart()
			.order()
			.paymentmethodinfo()
			.selector()
			.save()
}

When(~'^I create a payment method for my order$') { ->
	CommonMethods.addTokenToOrder()
}

And(~'^I retrieve my order$') { ->
	client.GET("/")
			.defaultcart()
			.order()
			.stopIfFailure()
}

When(~'^I view the purchase$') { ->
	client.follow()
			.stopIfFailure()
}

When(~'^I get the list of payment methods from my profile$') { ->
	client.GET("/")
			.defaultprofile()
			.paymentmethods()
			.stopIfFailure()
}

When(~'^I get the chosen payment method X$') { ->
	client.GET("/")
			.defaultcart()
			.order()
			.paymentmethodinfo()
			.selector()
			.chosen()
			.stopIfFailure()
}

When(~'^I get the payment method details for the chosen payment method X$') { ->
	client.GET("/")
			.defaultcart()
			.order()
			.paymentmethodinfo()
			.selector()
			.chosen()
			.description()
			.stopIfFailure()
}

When(~'^I get the payment method details for payment method choice Y or Z$') { ->
	client.GET("/")
			.defaultcart()
			.order()
			.paymentmethodinfo()
			.selector()
			.choice()
			.description()
			.stopIfFailure()
}

When(~'^payment method X is now a choice as the payment method on the order$') { ->
	client.GET("/")
			.defaultcart()
			.order()
			.paymentmethodinfo()
			.selector()
			.choice()
			.description()
			.stopIfFailure()

	assertThat(client["display-name"])
			.as("The paymentmethod display-name is not as expected")
			.isEqualTo(DEFAULT_PAYMENT_TOKEN_DISPLAY_VALUE)
}


When(~'^I select payment method Y on the order$') { ->
	client.resume(selectorRepresentationResponse)
			.choice()
			.selectaction()
			.follow()
			.chosen()
			.description()
	newlyChosenPaymentMethod = client["display-name"]
	client.stopIfFailure()
}

When(~'^I complete the purchase for the order$') { ->
	client.GET("/")
			.defaultcart()
			.order()
			.purchaseform()
			.submitorderaction()
			.follow()
			.paymentmeans()
			.element()
			.stopIfFailure()
}

Then(~'^I get the default token (.+)$') { tokenValue ->
	assertThat(client["display-name"])
			.as("Default token display-name is not as expected")
			.isEqualTo(tokenValue)
}

Then(~'^the payment method is available from their profile$') { ->
	client.GET("/")
			.defaultprofile()
			.paymentmethods()
			.element()
			.stopIfFailure()
	assertPaymentTokenCreation()
}

Then(~'^the payment method has been added to their profile$') { ->
	assertPaymentTokenCreation()
}

Then(~'^it no longer shows up in his list of saved payment methods on his profile$') { ->
	client.GET("/")
			.defaultprofile()
			.paymentmethods()
			.stopIfFailure()

	boolean deletedTokenFound = client.body.links.findAll {
		link -> link.rel == "element"
	}.any {
		link -> client.GET(link.href)["display-name"] == TEST_TOKEN_DISPLAY_VALUE_X
	}

	assertThat(deletedTokenFound)
			.as("The deleted payment method should have not been found")
			.isFalse()
}

Then(~'^I see only the payment method available from my profile$') { ->
	def displayName = client.chosen().description()["display-name"]

	assertThat(displayName)
			.as("The chosen payment method on the order is not as expected")
			.isEqualTo(TEST_TOKEN_DISPLAY_VALUE_X)

	client.stopIfFailure()

	client.GET("/")
			.defaultcart()
			.order()
			.paymentmethodinfo()
			.selector()
			.stopIfFailure()

	assertLinkDoesNotExist(client, "choice")
}

Then(~'^the new payment method will be set for their order') { ->
	client.GET("/")
			.defaultcart()
			.order()
			.paymentmethodinfo()
			.paymentmethod()
			.stopIfFailure()

	assertThat(client["display-name"])
			.as("The chosen payment method on the order is not as expected")
			.isEqualTo(TEST_TOKEN_DISPLAY_NAME)

}
And(~'^the new payment method is not available from their profile') { ->
	client.GET("/")
			.defaultprofile()
			.paymentmethods()
			.stopIfFailure()

	assertLinkDoesNotExist(client, "element")
}

Then(~'^the default payment method is automatically applied to the order$') { ->
	client.paymentmethodinfo()
			.selector()
			.stopIfFailure()

	assertLinkExists(client, "chosen")

	def displayName = client.chosen().description()["display-name"]

	assertThat(displayName)
			.as("The chosen payment method on the order is not as expected")
			.isEqualTo(TEST_TOKEN_DISPLAY_NAME)

	client.stopIfFailure()

	client.GET("/")
			.defaultcart()
			.order()
			.paymentmethodinfo()
			.selector()
			.stopIfFailure()

	assertLinkDoesNotExist(client, "choice")

}

Then(~'^the paymentmeans is a paymenttoken type$') { ->
	client.paymentmeans()
			.element()
			.stopIfFailure()
	assertThat(client.body.self.type)
			.as("The paymentmeans is not as expected")
			.isEqualTo("purchases.purchase-paymentmean")
}

And(~'^the token display-name matches the token used to create the purchase$') { ->
	assertThat(client["display-name"])
			.as("The token display-name is not as expected")
			.isEqualTo(token)
}

Then(~'^the paymentmeans is empty$') { ->
	client.paymentmeans()
			.stopIfFailure()
	assertLinkDoesNotExist(client, "element")
}

Then(~'^the paymentmeans is a credit card type$') { ->
	client.paymentmeans()
			.element()
			.stopIfFailure()
	assertThat(client.body.self.type)
			.as("The paymentmeans is not as expected")
			.isEqualTo("purchases.purchase-paymentmean")
}

And(~'^the billing address matches the billing address used to create the purchase$') { ->
	assertThat(client["billing-address"]["address"])
			.as("the billing-address address is not as expected")
			.isEqualTo(address)
	assertThat(client["billing-address"]["name"])
			.as("the billing-address name is not as expected")
			.isEqualTo(name)
}

Then(~'^the field (.+) has value (.+)$') { def field, def value ->
	assertThat(client[field])
			.as("the field " + field + " is not as expected")
			.isEqualTo(value)
}

Then(~'^the list contains payment tokens X Y and Z and X is displayed as the default$') { ->
	FindPaymentToken(DEFAULT_PAYMENT_TOKEN_DISPLAY_VALUE)
	FindPaymentToken(SECOND_PAYMENT_TOKEN_DISPLAY_VALUE)
	FindPaymentToken(THIRD_PAYMENT_TOKEN_DISPLAY_VALUE)
	client.default()
	assertThat(client["display-name"])
			.as("The default display name is not as expected")
			.isEqualTo(DEFAULT_PAYMENT_TOKEN_DISPLAY_VALUE)
}

Then(~'^there is no way to select the payment method for my order$') { ->
	def selectActionLink = client.body.links.find {
		link ->
			link.rel == "selectaction"
	}
	assertThat(selectActionLink == null)
			.as("The chosen payment method should not have a select action link")
			.isTrue()
}

Then(~'^the payment method details display the correct values$') { ->
	assertThat(client["display-name"])
			.as("The chosen payment method was not the default payment method")
			.isEqualTo(DEFAULT_PAYMENT_TOKEN_DISPLAY_VALUE)
}

Then(~'^the payment method details display the correct values for that choice$') { ->
	def paymentMethodDisplayValue = client["display-name"]
	assertThat(paymentMethodDisplayValue == SECOND_PAYMENT_TOKEN_DISPLAY_VALUE || paymentMethodDisplayValue == THIRD_PAYMENT_TOKEN_DISPLAY_VALUE)
			.as("The chosen payment method was not the default payment method")
			.isTrue()
}

Then(~'^payment method Y is now chosen as the payment method on the order$') { ->
	//Clean up test by completing the purchase
	completePurchase()

	assertThat(newlyChosenPaymentMethod != preChosenPaymentMethod)
			.as("The newly selected payment method should now be chosen for the order")
			.isTrue()
}

Then(~'^payment method X is chosen as the payment method for my purchase$') { ->
	String test = client.resume(paymentMethodSelector)
			.chosen()
			.description()
	["display-value"]

	assertThat(test)
			.as("The payment method selected was not the default payment method")
			.isEqualTo(defaultPaymentMethodDisplayValue)
}

Then(~'^the chosen payment method is displayed correctly as the payment mean for the purchase$') { ->
	assertThat(client["display-name"])
			.as("The payment method used for purchase should be the same as the chosen payment method")
			.isEqualTo(preChosenPaymentMethod)
}

private registerNewShopperAndAuthenticate() {
	def USERNAME = UUID.randomUUID().toString() + "@elasticpath.com"

	client.authAsAPublicUser(DEFAULT_SCOPE)
			.stopIfFailure()

	registerShopper(TOKEN_SCOPE, FAMILY_NAME, GIVEN_NAME, PASSWORD, USERNAME)

	client.authRegisteredUserByName(TOKEN_SCOPE, USERNAME)
}

private assertPaymentTokenCreation() {
	assertThat(client["display-name"])
			.as("Display name is not as expected")
			.isEqualTo(TEST_TOKEN_DISPLAY_NAME)
}

private saveTokenDetails() {
	client.GET("/")
			.defaultcart()
			.order()
			.paymentmethodinfo()
			.paymentmethod()
			.stopIfFailure()

	token = client["display-name"]
}


private FindPaymentToken(def tokenDisplayName) {
	client.findElement {
		paymentmethod ->
			paymentmethod["display-name"] == tokenDisplayName
	}.list()
}

private completePurchase() {
	client.GET("/")
			.defaultcart()
			.order()
			.purchaseform()
			.submitorderaction()
}

def registerShopper(registrationScope, familyName, givenName, password, username) {
	client.authAsAPublicUser(registrationScope)

	client.GET("registrations/$registrationScope/newaccount/form")
			.registeraction("family-name": familyName, "given-name": givenName, "password": password, "username": username)
}