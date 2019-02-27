package com.elasticpath.cortex.dce.orders

import static org.assertj.core.api.Assertions.assertThat

import static com.elasticpath.cortex.dce.ClasspathFluentRelosClientFactory.client
import static com.elasticpath.cortex.dce.ClasspathFluentRelosClientFactory.currentScope
import static com.elasticpath.cortex.dce.SharedConstants.TEST_EMAIL_VALUE
import static com.elasticpath.cortex.dce.orders.OrderConstants.SEARCHABLE_PRODUCT
import static com.elasticpath.rest.ws.assertions.RelosAssert.assertLinkDoesNotExist
import static com.elasticpath.rest.ws.assertions.RelosAssert.assertLinkExists

import cucumber.api.groovy.EN
import cucumber.api.groovy.Hooks

import com.elasticpath.cortex.dce.CommonMethods

this.metaClass.mixin(Hooks)
this.metaClass.mixin(EN)


When(~'^I submit the order$') { ->
	CommonMethods.submitPurchase()
	client.stopIfFailure()
}

When(~'^I create an email for my order$') { ->
	CommonMethods.addEmail(TEST_EMAIL_VALUE)
}

When(~'^I select only the billing address$') { ->
	configureBillingAddressToBeSelected()
	removeShippingAddressOnOrderIfItExists()
}

When(~'^I also select the shipping address') { ->
	CommonMethods.selectAnyDestination()
	selectShippingServiceLevel()
}

When(~'^I select only the shipping address$') { ->
	configureOnlyOneSelectedShippingAddressForUser()
}

Then(~'^my order fails with status (.+)$') { responseStatus ->
	assertThat(client.response.status.toString())
			.as("The http status is not as expected")
			.isEqualTo(responseStatus)
}

Then(~'^I am not able to submit my order$') { ->
	client.GET("/")
			.defaultcart()
			.order()
			.purchaseform()
			.stopIfFailure()

	assertLinkDoesNotExist(client, "submitorderaction")
}
When(~'^I add an address with country (.+) and region (.+)$') { country, subcountry ->
	addNewAddress(country, subcountry)
}

When(~'^I add a (.+) address$') { country ->
	addNewAddress(country, "")
}

And(~'^I retrieve the order taxes$') { ->
	client.GET("/")
			.defaultcart()
			.order()
	assertLinkExists(client, "tax")

	client.tax()
			.stopIfFailure()
}

Then(~'^my order succeeds$') { ->
	assertThat(client.response.status)
			.as("HTTP response status is not as expected")
			.isEqualTo(201)
}

Then(~'^the email is created and selected for my order$') { ->
	client.GET("/")
			.defaultcart()
			.order()
			.emailinfo()
			.email()
			.stopIfFailure()

	assertThat(client["email"])
			.as("Email on the order is not as expected")
			.isEqualTo(TEST_EMAIL_VALUE)
}

And(~'^the tax total on the order is (.+)$') { taxAmount ->
	assertThat(client["total"]["display"])
			.as("The tax total is not as expected")
			.isEqualTo(taxAmount)
}

Then(~'^the (.+) cost is (.+)$') { taxType, amount ->
	assertThat(client["cost"].find {
		it ->
			it.title == taxType
	}.display)
			.as("The cost is not as expected")
			.isEqualTo(amount)
}

Then(~'resolve the shipping-option-info needinfo') { ->
	client.GET("/")
			.defaultcart()
			.order()
			.deliveries()
			.element()
			.destinationinfo()
			.selector()
			.choice()
			.selectaction()
			.stopIfFailure()
}

Then(~'I select the (new|valid) shipping address') { def ignore ->
	client.GET("/")
			.defaultcart()
			.order()
			.deliveries()
			.element()
			.destinationinfo()
			.selector()
			.choice()
			.selectaction()
			.stopIfFailure()

}

Then(~'post to a created submitorderaction uri') { ->
	client.POST(client.body.self.uri.toString(), [:])
}

Then(~'post to a created addtodefaultcartaction uri') { ->
	def itemID = client.body.self.uri.toString().split("/")[4]
	def postURI = "/carts/items/" + currentScope + "/" + itemID + "/form"
	client.POST(postURI, [quantity: 1])
}

When(~'^I retrieve the purchase form$') { ->
	client.GET("/")
			.defaultcart()
			.order()
			.purchaseform()
			.stopIfFailure()
}

Then(~'^there is a needinfo link to (.+)$') { def resourceName ->
	assertThat(needInfoExistsWithName(resourceName))
			.as("Needinfo link for $resourceName not found")
			.isTrue()
}

Then(~'^there is no needinfo link to (.+)$') { resourceName ->
	assertThat(needInfoExistsWithName(resourceName))
			.as("Needinfo link for $resourceName was found")
			.isFalse()
}

Then(~'^there is an advisor linked to (.+)$') { def resourceName ->
	assertThat(advisorExistsWithLinkedToName(resourceName))
			.as("Advisor with link for $resourceName not found")
			.isTrue()
}

Then(~'^there is no advisor linked to (.+)$') { resourceName ->
	assertThat(advisorExistsWithLinkedToName(resourceName))
			.as("Needinfo link for $resourceName was found")
			.isFalse()
}

And(~'^I delete the chosen billing address$') { ->
	deleteChosenBillingAddress()
}

And(~'^there is no (.+) link found$') { def linkName ->
	assertLinkDoesNotExist(client, linkName)
}

private void configureOnlyOneSelectedShippingAddressForUser() {
	CommonMethods.createUniqueAddress()
	CommonMethods.createUniqueAddress()
	CommonMethods.selectAnyDestination()
	selectShippingServiceLevel()

	// Delete the billing address on order since it was automatically set
	// when an address was created above.
	deleteChosenBillingAddress()
}

private void selectShippingServiceLevel() {
	client.GET("/")
			.defaultcart()
			.order()
			.deliveries()
			.element()
			.shippingoptioninfo()
			.selector()
			.findChoice { shippingoption ->
		def description = shippingoption.description()
		description["name"] == "CanadaPostExpress"
	}
	.selectaction()
			.stopIfFailure()
}

private void deleteChosenBillingAddress() {
	client.GET("/")
			.defaultcart()
			.order()
			.billingaddressinfo()
			.billingaddress()
			.stopIfFailure()

	def billingAddressUri = client.body.self.uri
	client.DELETE(billingAddressUri)

	assertThat(client.response.status)
			.as("HTTP response status is not as expected")
			.isEqualTo(204)
}

private void configureBillingAddressToBeSelected() {
	CommonMethods.createUniqueAddress()
	CommonMethods.createUniqueAddress()

	CommonMethods.selectAnyBillingInfo()
}

private void removeShippingAddressOnOrderIfItExists() {
	client.GET("/")
			.defaultcart()
			.order()
			.deliveries()
			.stopIfFailure()
	def link = client.body.links.find { link ->
		link.rel == "element"
	}
	if (link != null) {
		client.element()
				.destinationinfo()
				.destination()
				.stopIfFailure()

		def destinationUri = client.body.self.uri
		client.DELETE(destinationUri)
	}
}

def needInfoExistsWithName(def name) {
	def found = false
	def startingPointUri = client.body.self.uri
	def listoflinks = client.body.links.findAll {
		link ->
			link.rel == "needinfo"
	}
	listoflinks.findResult {
		link ->
			client.GET(link.href).response

			//checks if current representation contains the info name being queried for
			if (client["name"] == name) {
				client.GET(startingPointUri)
				found = true
			}
	}
	client.GET(startingPointUri)
	return found
}

def advisorExistsWithLinkedToName(def name) {
	def found = false
	def startingPointUri = client.body.self.uri
	def listOfLinkedMessages = client.body.messages.findAll { message ->
		message.containsKey('linked-to')
	}
	listOfLinkedMessages.findResult { message ->
		client.GET(message.'linked-to'.href).response

		//checks if current representation contains the info name being queried for
		if (client["name"] == name) {
			client.GET(startingPointUri)
			found = true
		}
	}
	client.GET(startingPointUri)
	return found
}

private void addNewAddress(country, subcountry) {
	CommonMethods.createAddress(country, "", "Vancouver", "", "", "555555", subcountry,
			"123 Somestreet", "testFamilyName", "testGivenName")
	assert client.response.status == 201
}