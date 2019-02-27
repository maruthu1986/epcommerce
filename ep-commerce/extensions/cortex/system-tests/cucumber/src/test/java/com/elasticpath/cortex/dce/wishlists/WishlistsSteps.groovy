/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.cortex.dce.wishlists

import static org.assertj.core.api.Assertions.assertThat

import static com.elasticpath.cortex.dce.ClasspathFluentRelosClientFactory.client
import static com.elasticpath.cortex.dce.CommonAssertion.assertItemConfiguration
import static com.elasticpath.cortex.dce.CommonMethods.*
import static com.elasticpath.cortex.dce.SharedConstants.*
import static com.elasticpath.cortex.dce.wishlists.WishlistsConstants.*
import static com.elasticpath.rest.ws.assertions.RelosAssert.*

import cucumber.api.DataTable
import cucumber.api.groovy.EN
import cucumber.api.groovy.Hooks

import com.elasticpath.cortex.dce.CommonMethods

this.metaClass.mixin(Hooks)
this.metaClass.mixin(EN)

def registeredShopperUsername
def configurationFields

def navigateToWishListLineItems = {
	client.GET("/")
			.defaultwishlist()
			.lineitems()
			.stopIfFailure()
}

Given(~'^I (?:add item with name|have) (.+) (?:in|to) my default favorites$') { String itemDisplayName ->
	CommonMethods.searchAndOpenItemWithKeyword(itemDisplayName)
	client.addtowishlistform()
			.addtodefaultwishlistaction()
			.stopIfFailure()
}

Given(~'^I add item with code (.+) to my default favorites$') { String itemCode ->
	CommonMethods.lookup(itemCode)
	client.addtowishlistform()
			.addtodefaultwishlistaction()
			.follow()
			.stopIfFailure()
}

When(~'^I add selected multisku item to the favorites') { ->
	def itemUri = client.body.self.uri
	client.GET(itemUri)
			.item()
	client.addtowishlistform()
			.addtodefaultwishlistaction()
			.follow()
			.stopIfFailure()
}

Given(~'^I add (.+) to my default cart with quantity (.+)$') { String itemDisplayName, String itemQuantity ->
	CommonMethods.searchAndOpenItemWithKeyword(itemDisplayName)
	client.addtocartform()
			.addtodefaultcartaction(quantity: itemQuantity)
			.follow()
			.stopIfFailure()
}

Given(~'^(.+) is not purchaseable$') { String itemDisplayName ->
	CommonMethods.searchAndOpenItemWithKeyword(itemDisplayName)
	client.addtocartform()
			.stopIfFailure()

	assertLinkDoesNotExist(client, "addtodefaultcartaction")
}

And(~'^item with name (.+) already exists in my cart with quantity (.+)$') { String itemDisplayName, String quantity ->
	CommonMethods.searchAndOpenItemWithKeyword(itemDisplayName)
	client.addtocartform()
			.addtodefaultcartaction(quantity: quantity)
			.stopIfFailure()

	assertThat(client.response.status)
			.as("HTTP response status is not as expected")
			.isEqualTo(201)

	client.follow()
			.stopIfFailure()

	assertThat(client.body.quantity.toString())
			.as("Expected quantity does not match")
			.isEqualTo(quantity)
}

And(~'^item with code (.+) already exists in my cart with quantity (.+)$') { String itemCode, String quantity ->
	CommonMethods.lookup(itemCode)
	client.addtocartform()
			.addtodefaultcartaction(quantity: quantity)
			.stopIfFailure()

	assertThat(client.response.status)
			.as("HTTP response status is not as expected")
			.isEqualTo(201)

	client.follow()
			.stopIfFailure()

	assertThat(client.body.quantity.toString())
			.as("Expected quantity does not match")
			.isEqualTo(quantity)
}

Given(~'^(.+) is in my registered shopper\'s default favorites$') { String itemDisplayName ->
	registeredShopperUsername = UUID.randomUUID().toString() + "@elasticpath.com"
	registerShopper(DEFAULT_SCOPE, FAMILY_NAME, GIVEN_NAME, PASSWORD, registeredShopperUsername)
	client.authRegisteredUserByName(DEFAULT_SCOPE, registeredShopperUsername)

	searchForAndAddToWishlist(itemDisplayName)

	assertThat(client.response.status)
			.as("HTTP response status is not as expected")
			.isEqualTo(201)
}

And(~'^(.+) is in my anonymous shopper\'s default favorites$') { String itemDisplayName ->
	client.authAsAPublicUser(DEFAULT_SCOPE)

	searchForAndAddToWishlist(itemDisplayName)

	assertThat(client.response.status)
			.as("HTTP response status is not as expected")
			.isEqualTo(201)
}

When(~'^I view my default favorites$') { ->
	client.GET("/")
			.defaultprofile()
			.wishlists()
			.element()
			.stopIfFailure()
}

When(~'^I delete item with name (.+) from my default favorites$') { String itemDisplayName ->
	client.GET("/")
			.defaultprofile()
			.wishlists()
			.element()
			.lineitems()
			.stopIfFailure()
	client.DELETE(getLineItemUriForItemName(itemDisplayName))
			.stopIfFailure()
}

When(~'^I delete item with code (.+) from my default favorites$') { String itemCode ->
	navigateToWishListLineItems()
	client.DELETE(getLineItemUriForItemCode(itemCode))
			.stopIfFailure()
}

When(~'^I move item with name (.+) to my cart with quantity (.+)$') { String itemDisplayName, String itemQuantity ->
	navigateToWishListLineItems()

	getLineItemUriForItemName(itemDisplayName)
	client.movetocartform()
			.movetocartaction(quantity: itemQuantity)
			.stopIfFailure()
}

When(~'^I move item with code (.+) to my cart with quantity (.+)$') { String itemCode, String itemQuantity ->
	navigateToWishListLineItems()

	getLineItemUriForItemCode(itemCode)
	client.movetocartform()
			.movetocartaction(quantity: itemQuantity)
			.stopIfFailure()
}

Then(~'I can move configurable itemcode (.+) from favorites to cart with quantity (.+) and preserved data values:$') { String itemCode, String itemQty, DataTable dataTable ->
	navigateToWishListLineItems()
	getLineItemUriForItemCode(itemCode)

	client.movetocartform()
	assertItemConfiguration(dataTable)

	client.movetocartaction(
			["quantity"   : itemQty,
			 configuration: client.body.configuration
			])
			.stopIfFailure()
}

Then(~'I move configurable itemcode (.+) from favorites to my cart with quantity (.+)$') { String itemCode, String itemQty, DataTable dataTable ->
	configurationFields = dataTable.asMap(String, String)
	navigateToWishListLineItems()
	getLineItemUriForItemCode(itemCode)
	client.movetocartform()
			.movetocartaction(
			["quantity"   : itemQty,
			 configuration: configurationFields
			])
			.stopIfFailure()
}

When(~'^I view my profile$') { ->
	client.GET("/")
			.defaultprofile()
			.stopIfFailure()
}

When(~'^I move an item with name (.+) from my cart to my default favorites$') { String itemDisplayName ->
	client.GET("/")
			.defaultcart()
			.lineitems()
			.stopIfFailure()

	getLineItemUriForItemName(itemDisplayName)
	client.movetowishlistform()
			.movetowishlistaction()
			.stopIfFailure()

	assertThat(client.response.status)
			.as("HTTP response status is not as expected")
			.isEqualTo(201)
}

When(~'^I move an item with code (.+) from my cart to my default favorites$') { String itemCode ->
	client.GET("/")
			.defaultcart()
			.lineitems()
			.stopIfFailure()

	getLineItemUriForItemCode(itemCode)
	client.movetowishlistform()
			.movetowishlistaction()
			.stopIfFailure()
}

When(~'^I delete the list of favorites items$') { ->
	navigateToWishListLineItems()

	def lineitemsUri = client.body.self.uri

	client.DELETE(lineitemsUri)

	assertThat(client.response.status)
			.as("HTTP response status is not as expected")
			.isEqualTo(204)
}

When(~'^I retrieve the item details for (.+)$') { String itemDisplayName ->
	CommonMethods.searchAndOpenItemWithKeyword(itemDisplayName)

	// Validate that you found the item
	def itemUri = client.save();
	assertThat(client.definition()["display-name"])
			.as("Expected item display does not match")
			.isEqualTo(itemDisplayName)
	client.resume(itemUri)
}

Then(~'^item with name (.+) is in my default favorites$') { String itemDisplayName ->
	navigateToWishListLineItems()
//	Saving response at lineitems uri
	def response = client.save()

	verifyLineitemsContainElementWithDisplayName(itemDisplayName)

//	Resume response at linteitems uri
	client.resume(response)
}

Then(~'^item with code (.+) is in my default favorites$') { String itemCode ->
	navigateToWishListLineItems()
//	Saving response at lineitems uri
	def response = client.save()

	verifyLineitemsContainElementWithCode(itemCode)

//	Resume response at linteitems uri
	client.resume(response)
}

Then(~'^my default favorites has (.+) (?:lineitems|lineitem)?$') { int numItems ->
	navigateToWishListLineItems()

	verifyNumberOfElements(numItems)
}

Then(~'(.+)\'s favorites membership does not contain the default favorites') { def itemDisplayName ->
	CommonMethods.searchAndOpenItemWithKeyword(itemDisplayName)
	client.wishlistmemberships()
			.stopIfFailure()

	verifyNumberOfElements(0)
}

Then(~'(.+)\'s favorites membership contains the default favorites') { def itemDisplayName ->
	CommonMethods.searchAndOpenItemWithKeyword(itemDisplayName)
	client.wishlistmemberships()
			.stopIfFailure()

	verifyNumberOfElements(1)
}

And(~'^the list of wishlistmemberships has (.+) elements?$') { int numElements ->
	client.wishlistmemberships()
			.stopIfFailure()

	verifyNumberOfElements(numElements)
}

And(~'^the list of cartmemberships has (.+) elements?$') { int numElements ->
	client.cartmemberships()
			.stopIfFailure()

	verifyNumberOfElements(numElements)
}
Then(~'^item with name (.+) is not (?:found in|in) my default favorites$') { String itemDisplayName ->
	navigateToWishListLineItems()

	verifyLineitemsNotContainElementWithDisplayName(itemDisplayName)
}

Then(~'^item with code (.+) is not (?:found in|in) my default favorites$') { String itemCode ->
	navigateToWishListLineItems()

	verifyLineitemsNotContainElementWithCode(itemCode)
}

And(~'^I cannot move item (.+) to my cart$') { String itemDisplayName ->
	getLineItemUriForItemName(itemDisplayName)
	client.movetocartform()
			.stopIfFailure()

	assertLinkDoesNotExist(client, "movetocartaction")
}

When(~'^I transition to the registered shopper$') { ->
	client.roleTransitionToRegisteredUserByName(DEFAULT_SCOPE, registeredShopperUsername)
			.stopIfFailure()
}

Then(~'^item with name (.+) is in my cart with quantity (.+)$') { String itemDisplayName, String itemQuantity ->
	client.GET("/")
			.defaultcart()
			.lineitems()
			.stopIfFailure()

	getLineItemUriForItemName(itemDisplayName)
	assertThat(client.body.'quantity'.toString())
			.as("The expected quantity does not match")
			.isEqualTo(itemQuantity)
}

Then(~'^item with code (.+) is in my cart with quantity (.+)$') { String itemCode, String itemQuantity ->
	client.GET("/")
			.defaultcart()
			.lineitems()
			.stopIfFailure()

	getLineItemUriForItemCode(itemCode)
	assertThat(client.body.'quantity'.toString())
			.as("The expected quantity does not match")
			.isEqualTo(itemQuantity)
}

Then(~'^there is (?:a|an) (.+) link$') { String relValue ->
	assertLinkExists(client, relValue)
}

And(~'^my list of wishlists contains (.+) favorites') { int numWishlists ->
	client.GET("/")
			.defaultprofile()
			.wishlists()
			.stopIfFailure()

	def elements = client.body.links.findAll { link ->
		link.rel == ELEMENT_LINK
	}
	assertThat(elements)
			.as("Expected wishllist size does not match")
			.hasSize(numWishlists)
}

Then(~'^I cannot delete my default favorites$') { ->
	def wishlistUri = client.body.self.uri

	client.DELETE(wishlistUri)

	assertThat(client.response.status)
			.as("HTTP response status is not as expected")
			.isEqualTo(405)
}

And(~'^item with name (.+) is not found in my cart$') { String itemDisplayName ->
	client.GET("/")
			.defaultcart()
			.lineitems()
			.stopIfFailure()

	verifyLineitemsNotContainElementWithDisplayName(itemDisplayName)
}

And(~'^item with code (.+) is not found in my cart$') { String itemCode ->
	client.GET("/")
			.defaultcart()
			.lineitems()
			.stopIfFailure()

	verifyLineitemsNotContainElementWithCode(itemCode)
}

Then(~/I cannot add the item to my favorites/) { ->
	client.addtowishlistform()
			.stopIfFailure()

	assertLinkDoesNotExist(client, "addtodefaultwishlistaction")
}

Then(~/I cannot move the item to my favorites/) { ->
	client.movetowishlistform()
			.stopIfFailure()

	assertLinkDoesNotExist(client, "movetowishlistaction")
}

When(~'I navigate to root\'s default favorites') { ->
	client.GET("/")
			.defaultwishlist()
			.stopIfFailure()
	CART_URI = client.body.self.uri
}

Then(~'(.+)\'s cart membership contains the default cart') { def itemDisplayName ->
	assertProductCartMembershipCount(itemDisplayName, 1)
}

Then(~'(.+)\'s cart membership does not contain the default cart') { def itemDisplayName ->
	assertProductCartMembershipCount(itemDisplayName, 0)
}

When(~'^I navigate to the movetocartform for favorites item with code (.+)$') { String itemCode ->
	navigateToWishListLineItems()

	getLineItemUriForItemCode(itemCode)
	client.movetocartform()
			.stopIfFailure()
}

private void assertProductCartMembershipCount(def itemDisplayName, def count) {
	CommonMethods.searchAndOpenItemWithKeyword(itemDisplayName)

// Validate that you found the item
	def itemUri = client.save();
	assertThat(client.definition()["display-name"])
			.as("Expected item display does not match")
			.isEqualTo(itemDisplayName)
	client.resume(itemUri)

	client.cartmemberships()
			.stopIfFailure()

	verifyNumberOfElements(count)
}

private void searchForAndAddToWishlist(String itemDisplayName) {
	CommonMethods.searchAndOpenItemWithKeyword(itemDisplayName)
	client.addtowishlistform()
			.addtodefaultwishlistaction()
			.stopIfFailure()
}

def registerShopper(registrationScope, familyName, givenName, password, username) {
	client.authAsAPublicUser(registrationScope)

	client.GET("registrations/$registrationScope/newaccount/form")
			.registeraction("family-name": familyName, "given-name": givenName, "password": password, "username": username)
}