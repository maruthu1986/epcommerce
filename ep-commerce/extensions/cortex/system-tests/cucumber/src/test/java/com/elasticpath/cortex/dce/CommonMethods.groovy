package com.elasticpath.cortex.dce

import static org.assertj.core.api.Assertions.assertThat
import static org.junit.Assert.assertTrue

import static com.elasticpath.cortex.dce.ClasspathFluentRelosClientFactory.client
import static com.elasticpath.cortex.dce.SharedConstants.*

import cucumber.api.DataTable

/**
 * Shared methods.
 */
class CommonMethods {
	static String getLineItemUriForItemName(String itemDisplayName) {
		def lineitemUri

		client.findElement { element ->
			lineitemUri = client.body.self.uri
			client.item()
					.definition()
			assertThat(client["display-name"])
					.as("Cannot find item for the given item name.")
					.isEqualTo(itemDisplayName)
		}
		.stopIfFailure()
		return lineitemUri
	}

	static String getLineItemUriForItemCode(String itemCode) {
		def lineitemUri

		client.findElement { element ->
			lineitemUri = client.body.self.uri
			client.item()
					.code()
			assertThat(client["code"])
					.as("Cannot find item for the given item code.")
					.isEqualTo(itemCode)
		}
		.stopIfFailure()
		return lineitemUri
	}

	static void verifyLineitemsContainElementWithDisplayName(String itemDisplayName) {
		assertThat(getLineitemNamesList())
				.as("The lineitems do not contain the expected item.")
				.contains(itemDisplayName)
	}

	static void verifyLineitemsContainElementWithCode(String itemCode) {
		assertThat(getLineitemCodesList())
				.as("The lineitems do not contain the expected item.")
				.contains(itemCode)
	}

	static void verifyDependentLineItemsContainAllConstituents(String purchasableItemSkuCode) {
		def itemCodes = getConstituentsCodes(purchasableItemSkuCode).toSorted()
		def dependentLineItemsCodes = getDependentLineItemsCodesList(purchasableItemSkuCode).toSorted()

		def missingCodes = itemCodes - itemCodes.intersect(dependentLineItemsCodes)

		assertThat(missingCodes)
				.as("The Dependent LineItems do not contain the expected items.")
				.isEmpty()
	}

	static getConstituentsCodes(purchasableItemSkuCode) {
		def itemsCodes = []

		lookup(purchasableItemSkuCode)

		client.definition()
				.components().findElement {
			component ->
				component.standaloneitem().code()
				itemsCodes.add(client["code"])
		}

		return itemsCodes
	}

	static void verifyLineitemsNotContainElementWithDisplayName(String itemDisplayName) {
		assertThat(getLineitemNamesList())
				.as("The lineitems contain the unexpected item.")
				.doesNotContain(itemDisplayName)
	}

	static void verifyLineitemsNotContainElementWithCode(String itemCode) {
		assertThat(getLineitemCodesList())
				.as("The lineitems contain the unexpected item.")
				.doesNotContain(itemCode)
	}

	static List getLineitemNamesList() {
		def lineItemNames = []

		client.body.links.findAll {
			if (it.rel == "element") {
				client.GET(it.href)
				client.item()
						.definition()
				lineItemNames.add(client["display-name"])
			}
		}
		return lineItemNames
	}

	static List getLineitemCodesList() {
		def lineItemCodes = []

		client.body.links.findAll {
			if (it.rel == "element") {
				client.GET(it.href)
				client.item()
						.code()
				lineItemCodes.add(client["code"])
			}
		}
		return lineItemCodes
	}

	static List getDependentLineItemsCodesList(String lineItemCode) {
		def lineItemCodes = []

		client.GET(findCartLineItemUriBySkuCode(lineItemCode))
				.dependentlineitems()
				.findElement {
			lineitem ->
				lineitem.item().code()
				lineItemCodes.add(client["code"])
		}

		return lineItemCodes
	}

	static void verifyNumberOfElements(int numElements) {
		def elements = client.body.links.findAll { link ->
			link.rel == ELEMENT_LINK
		}
		assertThat(elements)
				.as("Expected number of elements not match.")
				.hasSize(numElements)
	}

	static void addProductToCart(String triggerProduct, int quantity) {
		searchAndOpenItemWithKeyword(triggerProduct)

		client.addtocartform()
				.addtodefaultcartaction(quantity: quantity)
				.stopIfFailure()
	}

	static def findCartLineItemUriByDisplayName(String displayName) {
		client.GET("/")
				.defaultcart()
				.lineitems()
				.stopIfFailure()

		findCartElementByDisplayName(displayName)

		return client.body.self.uri
	}

	static def findCartLineItemUriBySkuCode(String skuCode) {
		client.GET("/")
				.defaultcart()
				.lineitems()
				.stopIfFailure()

		findCartElementBySkuCode(skuCode)
		client.stopIfFailure()
		return client.body.self.uri
	}

	static def findCartLineItemUriBySkuCodeAndConfigurableFieldValues(String skuCode, DataTable dataTable) {
		client.GET("/")
				.defaultcart()
				.lineitems()
				.stopIfFailure()

		findCartElementBySkuCodeAndConfigurableFieldValues(skuCode, dataTable)
		client.stopIfFailure()
		return client.body.self.uri
	}

	static def findCartElementByDisplayName(displayName) {
		def itemExists = false
		def elementResponse = null

		client.body.links.find {
			if (it.rel == "element") {
				client.GET(it.href)
				elementResponse = client.save()
				client.item()
						.definition()
				if (client["display-name"] == displayName) {
					itemExists = true
				}
			}
		}
		assertThat(itemExists)
				.as("Item not found for item name - " + displayName,)
				.isEqualTo(true)
		client.resume(elementResponse)
	}

	static def findCartElementBySkuCode(skuCode) {
		def itemExists = false
		def elementResponse = null

		client.body.links.find {
			if (it.rel == "element") {
				client.GET(it.href)
				elementResponse = client.save()
				client.item()
						.code()
				if (client["code"] == skuCode) {
					itemExists = true
				} else {
					itemExists = false
				}
			}
		}
		assertThat(itemExists)
				.as("Item not found for skuCode - " + skuCode)
				.isTrue()
		client.resume(elementResponse)
	}

	static def findCartElementBySkuCodeAndConfigurableFieldValues(skuCode, DataTable dataTable) {
		def itemExists = false
		def elementResponse = null
		def configValueMatch = false
		def configMatch = true
		def failureKey
		def failureValue

		client.body.links.find {
			configMatch = true
			if (it.rel == "element") {
				client.GET(it.href)
				elementResponse = client.save()
				client.item()
				client.code()
				if (client["code"] == skuCode) {
					itemExists = true
				} else {
					itemExists = false
				}
			}

			if (!itemExists)
				return false

			client.resume(elementResponse)
			def mapList = dataTable.asMap(String, String)

			for (def map : mapList) {
				configValueMatch = false
				def key = map.getKey()
				def value = map.getValue()
				if (client.body.'configuration'."$key" == value) {
					configValueMatch = true
				} else {
					configValueMatch = false
					configMatch = false
					failureKey = key
					failureValue = value
				}
			}
			if (configValueMatch && configMatch)
				return true
		}

		assertThat(itemExists)
				.as("Item not found for skuCode - " + skuCode)
				.isTrue()

		assertTrue("unable to find configurable field: $failureKey or/and value: $failureValue", configMatch)
		assertTrue("Configurable field values not match", configValueMatch)
	}

	static def searchAndOpenItemWithKeyword(keyword) {
		search(keyword, "5")
		client.follow()
		findItemByDisplayName(keyword)

	}

	static def search(keyword, pageSize) {
		client.GET("/")
				.searches()
				.keywordsearchform()
				.itemkeywordsearchaction(
				['keywords' : keyword,
				 'page-size': pageSize]
		)
	}

	static def navigate(categoryName, itemName) {
		client.GET("/")
				.navigations()
				.findElement {
			category ->
				category["name"] == categoryName
		}
		.items()
		findItemByDisplayName(itemName)
	}

	static def findItemByDisplayName(displayName) {
		client.findElement {
			item ->
				def definition = item.definition()
				definition["display-name"] == displayName
		}
		.stopIfFailure()
	}

	static def submitPurchase() {
		client.GET("/")
				.defaultcart()
				.order()
				.purchaseform()
				.submitorderaction()
	}

	static def selectAnyShippingOption() {
		client.GET("/")
				.defaultcart()
				.order()
				.deliveries()
				.element()
				.shippingoptioninfo()
				.selector()

		def choiceExists = false
		client.body.links.find {
			if (it.rel == "choice") {
				choiceExists = true
			}
		}
		if (choiceExists) {
			client.choice().selectaction()
		}
	}

	static def createUniqueAddress() {
		def randomAddress = UUID.randomUUID().toString() + "random street"

		createAddress("CA", "", "Vancouver", "", "", "V7V7V7", "BC",
				randomAddress, "itest", "generated")
	}

	static def selectAnyDestination() {
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

	static def selectAnyBillingInfo() {
		client.GET("/")
				.defaultcart()
				.order()
				.billingaddressinfo()
				.selector()
				.choice()
				.selectaction()
				.stopIfFailure()
	}

	static def selectShippingAddressByPostalCode(postalCode) {
		client.GET("/")
				.defaultcart()
				.order()
				.deliveries()
				.findElement {
			element ->
				def destinationinfo = element.destinationinfo()
				followSelectedAddress(destinationinfo, postalCode)
		}

		client.GET("/").defaultcart()
				.stopIfFailure()
	}

	static def followSelectedAddress(destinationinfo, postalCode) {
		destinationinfo.selector()
				.findChoice {
			addressOption ->
				def description = addressOption.description()
				description["address"]["postal-code"] == postalCode
		}
		.selectaction()
				.follow()
				.stopIfFailure()
	}

	static def selectShippingOptionByName(shippingServiceName) {
		client.GET("/")
				.defaultcart()
				.order()
				.deliveries()
				.element()
				.shippingoptioninfo()
				.selector()
				.findChoice {
			shippingService ->
				def description = shippingService.description()
				description["display-name"] == shippingServiceName
		}
		.selectaction()
	}

	static def lookup(def skuCode) {
		client.GET("/")
				.lookups()
				.itemlookupform()
				.itemlookupaction([code: skuCode])
				.follow()
				.stopIfFailure()
	}

	static def lookupAndAddToCart(def itemCode, def qty) {
		lookup(itemCode)
		client.addtocartform()
				.addtodefaultcartaction(quantity: qty)
				.follow()
				.stopIfFailure()
	}

	static def lookupAndAddToCartNoFollow(def itemCode, def qty) {
		lookup(itemCode)
		client.addtocartform()
				.addtodefaultcartaction(quantity: qty)
				.stopIfFailure()
	}

	static def searchAndAddProductToCart(triggerProduct) {
		searchAndOpenItemWithKeyword(triggerProduct)
		client.addtocartform()
				.addtodefaultcartaction(quantity: 1)
				.stopIfFailure()
	}

	static void addPersonalisationHeader(String personalisationKey, String personalisationValue) {
		String headerKey = "x-ep-user-traits"
		String headerValue = personalisationKey + "=" + personalisationValue
		client.headers.putAt(headerKey, headerValue)
	}

	static void selectShippingOption(String shippingOptionName) {
		findShippingOptionChoiceOrChosen(shippingOptionName)
		selectIfNotAlreadySelected()
	}

	static void createAddress(String countryCode, String extendedAddress, String locale, String organization, String phoneNumber, String postalCode, String regionCode,
							  String streetAddress, String familyName, String givenName) {
		client.GET("/")
				.defaultprofile()
				.addresses()
				.addressform()
				.createaddressaction(
				[
						address: ["country-name"    : countryCode,
								  "extended-address": extendedAddress,
								  "locality"        : locale,
								  "organization"    : organization,
								  "phone-number"    : phoneNumber,
								  "postal-code"     : postalCode,
								  "region"          : regionCode,
								  "street-address"  : streetAddress],
						name   : ["family-name": familyName,
								  "given-name" : givenName]
				]
		)
				.stopIfFailure()
	}

	static void findShippingOptionChoiceOrChosen(String shippingOptionName) {
		client.GET("/")
				.defaultcart()
				.order()
				.deliveries()
				.element()
				.shippingoptioninfo()
				.selector()
				.findChoiceOrChosen {
			shippingService ->
				def description = shippingService.description()
				description["display-name"] == shippingOptionName
		}
	}

	static void selectIfNotAlreadySelected() {
		def selectAction = client.body.links.findAll {
			link ->
				link.rel == "selectaction"
		}

		if (selectAction.toList().size() > 0) {
			client.selectaction()
					.follow() // back to selector
		} else {
			client.selector()
		}
	}

	static void addEmail() {
		addEmail(TEST_EMAIL_VALUE)
	}

	static void addEmail(String userName) {
		client.GET("/")
				.defaultprofile()
				.emails()
				.emailform()
				.createemailaction("email": userName)
				.stopIfFailure()
	}

	static void addTokenToOrder() {
		addTokenToOrder(TEST_TOKEN_DISPLAY_NAME, TEST_TOKEN)
	}

	static void addTokenToOrder(String displayName, String token) {
		client.GET("/")
				.defaultcart()
				.order()
				.paymentmethodinfo()
				.paymenttokenform()
				.createpaymenttokenfororderaction(
				['display-name': displayName,
				 'token'       : token]
		)
				.follow()
				.stopIfFailure()
	}

	static void addTokenToProfile() {
		addTokenToProfile(TEST_TOKEN_DISPLAY_NAME, TEST_TOKEN)
	}

	static void addTokenToProfile(String displayName, String token) {
		client.GET("/")
				.defaultprofile()
				.paymentmethods()
				.paymenttokenform()
				.createpaymenttokenaction(
				['display-name': displayName,
				 'token'       : token]
		)
				.follow()
				.stopIfFailure()
	}

	static void clearCart() {
		client.GET("/")
				.defaultcart()
				.lineitems()
				.stopIfFailure()
		client.DELETE(client.body.self.uri)
	}


	static void openLinkRelWithFieldWithValue(def linkrel, def field, def value) {
		def elementExists = false
		def elementResponse
		client.body.links.find {
			if (it.rel == linkrel) {
				client.GET(it.href)
				if (client[field] == value) {
					elementResponse = client.save()
					elementExists = true
				}
			}
		}

		assertThat(elementExists)
				.as("$linkrel with $field = $value not found")
				.isTrue()

		client.resume(elementResponse)
			.stopIfFailure()
	}

	static void addBillingAddress() {
		createAddress("CA", "", "Vancouver", "", "", "555555", "BC",
				"123 Somestreet", "testFamilyName", "testGivenName")
		assert client.response.status == 201
	}

	static def purchaseNumberLookup(def purchaseNumber) {
		client.GET("/")
				.lookups()
				.purchaselookupform()
				.purchaselookupaction(["purchase-number": purchaseNumber])
		        .follow()
				.stopIfFailure()
	}


	static def navigationLookupCode(def categoryCode) {
		client.GET("/")
				.lookups()
				.navigationlookupform()
				.navigationlookupaction([code: categoryCode])
				.follow()
				.stopIfFailure()
	}

	static def verifyNavigationLinkDoesNotExist(def expectedLink){
		def expectedLinks = client.body.links.findAll {
			link ->
				link.rel == expectedLink
		}
		assertThat(expectedLinks)
				.size()
				.as("$expectedLink links found but should not exist: $client.body.links")
				.isEqualTo(0)
	}

}