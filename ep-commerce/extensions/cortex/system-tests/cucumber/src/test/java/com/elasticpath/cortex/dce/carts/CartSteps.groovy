package com.elasticpath.cortex.dce.carts

import static org.assertj.core.api.Assertions.assertThat

import static com.elasticpath.cortex.dce.ClasspathFluentRelosClientFactory.client
import static com.elasticpath.cortex.dce.CommonMethods.verifyLineitemsNotContainElementWithDisplayName
import static com.elasticpath.cortex.dce.SharedConstants.*
import static com.elasticpath.rest.ws.assertions.RelosAssert.assertLinkDoesNotExist

import cucumber.api.DataTable
import cucumber.api.groovy.EN
import cucumber.api.groovy.Hooks

import com.elasticpath.cortex.dce.CommonMethods

this.metaClass.mixin(Hooks)
this.metaClass.mixin(EN)


When(~'I add single item (.+) to the cart$') { String productName ->
	CommonMethods.addProductToCart(productName, 1)

	client.follow()
			.stopIfFailure()
}

When(~'I go to add to cart form$') { ->
	client.addtocartform()
			.stopIfFailure()

}

When(~'I add to cart with quantity of (.+)') { String quantity ->
	client.addtodefaultcartaction(
			["quantity": quantity
			])
			.stopIfFailure()
}

Given(~'^item (.+) does not have a price$') { String skuCode ->
	CommonMethods.lookup(skuCode)

	assertLinkDoesNotExist(client, "price")
}

When(~'I (?:add|have) item (.+) (?:to|in) the cart with quantity (.+)$') { String productName, int quantity ->
	CommonMethods.addProductToCart(productName, quantity)
	client.follow()
			.stopIfFailure()
}

When(~'^I update (.+) Details: Message:(.+), RecipientEmail:(.+), RecipientName:(.+), SenderName:(.+) and Quantity:(.+)$') { String itemDisplayName, String msg, String recEmail, String recName, String senderName, String itemQty ->
	def lineitemUri = CommonMethods.findCartLineItemUriByDisplayName(itemDisplayName)
	client.PUT(lineitemUri, [
			quantity     : itemQty,
			configuration: ["giftCertificate.message"       : msg,
							"giftCertificate.recipientEmail": recEmail,
							"giftCertificate.recipientName" : recName,
							"giftCertificate.senderName"    : senderName]
	])

	assertThat(client.response.status)
			.as("HTTP response status is not as expected")
			.isEqualTo(204)
}


When(~'^I update the item (.+) in the cart with quantity (.+) and configurable fields:$') { String skuCode, String quantity, DataTable modifierFieldsTable ->
	def lineitemUri = CommonMethods.findCartLineItemUriBySkuCode(skuCode)
	def Map<String, String> configurationFields = modifierFieldsTable.asMap(String, String)
	client.PUT(lineitemUri, [
			quantity     : quantity,
			configuration: configurationFields
	])

	assertThat(client.response.status)
			.as("HTTP response status is not as expected")
			.isEqualTo(204)
}

When(~'^I change the lineitem quantity for (.+) to (.+)$') { String itemDisplayName, String newQuantity ->
	def lineitemUri = CommonMethods.findCartLineItemUriByDisplayName(itemDisplayName)
	client.PUT(lineitemUri, [
			quantity: newQuantity
	])

	assertThat(client.response.status)
			.as("HTTP response status is not as expected")
			.isEqualTo(204)
}

When(~'^I change the lineitem quantity of item code (.+) to (.+)$') { String itemSkuCode, String newQuantity ->
	def lineitemUri = CommonMethods.findCartLineItemUriBySkuCode(itemSkuCode)
	client.PUT(lineitemUri, [
			quantity: newQuantity
	])

	assertThat(client.response.status)
			.as("HTTP response status is not as expected")
			.isEqualTo(204)
}

When(~'^I delete item (.+) from my cart$') { String itemDisplayName ->
	def lineitemUri = CommonMethods.findCartLineItemUriByDisplayName(itemDisplayName)
	client.DELETE(lineitemUri)

	assertThat(client.response.status)
			.as("HTTP response status is not as expected")
			.isEqualTo(204)
}

When(~'^I delete the lineitem with code (.+) from my cart$') { String itemSkuCode ->
	def lineitemUri = CommonMethods.findCartLineItemUriBySkuCode(itemSkuCode)
	client.DELETE(lineitemUri)

	assertThat(client.response.status)
			.as("HTTP response status is not as expected")
			.isEqualTo(204)
}

When(~'^I view (.+) in the catalog$') { String skuCode ->
	CommonMethods.lookup(skuCode)
}

When(~'^I attempt to add (.+) with invalid quantity (.+)$') { String displayName, String InvalidQuantity ->
	CommonMethods.searchAndOpenItemWithKeyword(displayName)
	client.addtocartform()
			.stopIfFailure()

	def actionLink = client.body.links[0].href

	client.POST(actionLink, [
			quantity: InvalidQuantity
	])
}

When(~'^I attempt to change the lineitem quantity for (.+) to (.+)$') { String itemDisplayName, String newQuantity ->
	def lineitemUri = CommonMethods.findCartLineItemUriByDisplayName(itemDisplayName)
	client.PUT(lineitemUri, [
			quantity: newQuantity
	])
}

Then(~'the items in the cart are ordered as follows$') { DataTable cartItemsTable ->
	def cartItems = cartItemsTable.asList(String)

	client.GET("/")
			.defaultcart()
			.lineitems()
			.stopIfFailure()
	List elementLinks = new ArrayList();
	client.body.links.findAll {
		if (it.rel == "element") {
			elementLinks.add(it.href)
		}
	}

	List<String> items = new ArrayList<>()
	for (String uri : elementLinks) {
		client.GET(uri)
				.item()
				.definition()
				.stopIfFailure()

		items.add((String) client.body[DISPLAY_NAME_FIELD])
	}
	assertThat(cartItems).containsExactlyElementsOf(items)
}

Then(~'the items in the zoomed cart are ordered as follows$') { DataTable cartItemsTable ->
	def cartItems = cartItemsTable.asList(String)

	client.GET(DEFAULT_CART_URL + CartConstants.ZOOM_LINE_ITEM_DEFINITION)
	List bodyElements = client.body._lineitems._element[0]

	List<String> items = new ArrayList<>()
	for (def element : bodyElements) {
		String itemName = element
				._item
				._definition[0]
				."display-name"[0]

		items.add(itemName)
	}
	assertThat(items).containsExactlyElementsOf(cartItems)


}

Then(~'capture the uri of the registered shopper\'s cart$') { ->
	client.GET("/")
			.defaultcart()
			.stopIfFailure()
	CART_URI = client.body.self.uri
}

Then(~'I attempt to view another shopper\'s cart$') { ->
	client.GET(CART_URI)
			.stopIfFailure()
}

Then(~'attempt to add to another shopper\'s cart$') { ->
	CommonMethods.searchAndOpenItemWithKeyword("firstProductAddedToCart")

	def itemURI = client.body.self.uri
	client.POST(CART_URI + "/lineitems" + itemURI, [
			quantity: 2
	])
}

Then(~'I am not able to view the cart$') { ->
	assertThat(client.response.status)
			.as("Access to the specified resource is forbidden.")
			.isEqualTo(403)
	client.follow()
}

Then(~'^the cart total-quantity (?:remains|is) (.+)$') { String cartTotalQty ->
	client.GET("/")
			.defaultcart()
			.stopIfFailure()
	assertThat(client.body.'total-quantity'.toString())
			.as("Cart total quantity is not as expected")
			.isEqualTo(cartTotalQty)
}

And(~'^the cart lineitem quantity for (.+) is (.+)$') { String itemDisplayName, String quantity ->
	client.GET("/")
			.defaultcart()
			.lineitems()
			.stopIfFailure()

	CommonMethods.findCartElementByDisplayName(itemDisplayName)

	assertThat(client.body.'quantity'.toString())
			.as("Cart line item quantity does not match.")
			.isEqualTo(quantity)
}

And(~'^the cart lineitem for item code (.+) has quantity of (.+)$') { String itemCode, String quantity ->
	client.GET("/")
			.defaultcart()
			.lineitems()
			.stopIfFailure()

	CommonMethods.findCartElementBySkuCode(itemCode)

	assertThat(client.body.'quantity'.toString())
			.as("Cart line item quantity does not match.")
			.isEqualTo(quantity)
}

Then(~'^(?:nothing has been added to the cart|the list of cart lineitems is empty)$') { ->
	client.GET("/")
			.defaultcart()
			.lineitems()
			.stopIfFailure()

	assertLinkDoesNotExist(client, ELEMENT_LINK)
}

Then(~'^I am not able to add the item to my cart$') { ->
	def itemUri = client.body.self.uri

	client.GET(itemUri)
			.addtocartform()
			.stopIfFailure()

	// Check that there is no addtodefaultcart link
	assertLinkDoesNotExist(client, "addtodefaultcartaction")
}

Then(~'^I am prevented from adding the item to the cart$') { ->
	def itemUri = client.body.self.uri
	client.GET(itemUri)
			.addtocartform()
			.stopIfFailure()
	assertLinkDoesNotExist(client, "addtodefaultcartaction")
	def lineitemUri = client.body.self.uri
	client.POST(lineitemUri, [
			quantity: 1
	])
			.stopIfFailure()
	assertThat(client.response.status)
			.as("HTTP response status is not as expected")
			.isEqualTo(409)
}

Then(~'^I am allowed to add to cart$') { ->
	def itemUri = client.body.self.uri
	client.GET(itemUri)
	client.addtocartform()
			.stopIfFailure()
	client.addtodefaultcartaction(quantity: 1)
			.follow()
			.stopIfFailure()
	assertThat(client.response.status)
			.as("HTTP response status is not as expected")
			.isEqualTo(200)
}

When(~'^I add selected multisku item to the cart$') { ->
	def itemUri = client.body.self.uri
	client.GET(itemUri)
			.item()
	client.addtocartform()
			.addtodefaultcartaction(quantity: 1)
			.follow()
			.stopIfFailure()
}

When(~'I go to my cart$') { ->
	client.GET("/")
			.defaultcart()
			.stopIfFailure()
}

Then(~'the number of cart lineitems is (.+)') { int numberOfLineitems ->
	client.GET("/")
			.defaultcart()
			.lineitems()
			.stopIfFailure()
	List lineItemElementList = new ArrayList();
	client.body.links.findAll {
		if (it.rel == "element") {
			lineItemElementList.add(it.href)
		}
	}
	assertThat(numberOfLineitems)
			.as("Expected number of cart lineitems do not match.")
			.isEqualTo(lineItemElementList.size())
}

Then(~'^item (.+) is not in any of my carts$') { String itemDisplayName ->
	def response = client.GET("/")
			.defaultcart()
			.lineitems()
			.stopIfFailure()

	verifyLineitemsNotContainElementWithDisplayName(itemDisplayName)
}

Then(~'new cart is created$') { ->
	client.GET("/")
			.defaultcart()
			.stopIfFailure()
	NEW_CART_URI = client.body.self.uri

	assertThat(NEW_CART_URI)
			.as("New cart must be created after checkout")
			.isNotEqualTo(CART_URI)
}

Then(~'that (?:.+) is the url of (?:.+)') { -> }