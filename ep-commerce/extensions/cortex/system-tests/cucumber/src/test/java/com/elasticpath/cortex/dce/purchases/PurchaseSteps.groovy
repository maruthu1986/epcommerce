package com.elasticpath.cortex.dce.purchases

import static com.elasticpath.cortex.dce.ClasspathFluentRelosClientFactory.client
import static org.assertj.core.api.Assertions.assertThat

import static com.elasticpath.cortex.dce.ClasspathFluentRelosClientFactory.client
import static com.elasticpath.cortex.dce.CommonAssertion.*
import static com.elasticpath.cortex.dce.SharedConstants.*
import static com.elasticpath.rest.ws.assertions.RelosAssert.*

import cucumber.api.DataTable
import cucumber.api.PendingException
import cucumber.api.groovy.EN
import cucumber.api.groovy.Hooks

import com.elasticpath.cortex.dce.CommonMethods

this.metaClass.mixin(Hooks)
this.metaClass.mixin(EN)

def final ADDRESS_FIELD = 'address'

final Map<String, String> ADDRESS = ["country-name": "CA", "locality": "Vancouver", "postal-code": "v7v7v7", "region": "BC", "street-address": "1111 EP Road"]

def final CARRIER_FIELD = 'carrier'

def purchaseUri

def savedPurchaseNumber

def createPurchaseWithFollow = { ->
	CommonMethods.submitPurchase()
	client.follow()
			.stopIfFailure()

	purchaseUri = client.save()
	savedPurchaseNumber = client.body.'purchase-number'
}

When(~'^I (?:can make|make) a purchase$') { ->
	CommonMethods.submitPurchase()
	client.stopIfFailure()
}

When(~'^I create a purchase and view the purchase details$') { ->
	CommonMethods.addEmail()
	CommonMethods.addTokenToOrder()
	CommonMethods.createUniqueAddress()
	createPurchaseWithFollow()
}

Given(~'^I have previously made a purchase with \"(\\d+?)\" (?:physical item|digital item|bundle item|item) \"([^\"]+)\"$') {
	int quantity, String productName ->

		CommonMethods.searchAndOpenItemWithKeyword(productName)
		client.addtocartform()
				.addtodefaultcartaction(quantity: quantity)
		CommonMethods.selectAnyShippingOption()
		createPurchaseWithFollow()
}

Given(~'^I have previously made a purchase with item code (.+)$') {
	def skuCode ->
		CommonMethods.lookupAndAddToCart(skuCode, 1)
		CommonMethods.addEmail()
		CommonMethods.addTokenToOrder()
		CommonMethods.addBillingAddress()
		createPurchaseWithFollow()
}

When(~'^I view the shipment line items') { ->
	client.shipments()
			.element()
			.lineitems()
			.stopIfFailure()
}

Then(~'^I see "(\\d+?)" shipment line items$') { int numLineItems ->
	assertThat(client.body.links.findAll { link -> link.rel == "element" })
			.size()
			.as("Number of shipment line items is not as expected")
			.isEqualTo(numLineItems)
}

And(~'^I see a back link to the shipment$') { ->
	client.shipment()
			.stopIfFailure()
}

When(~'^I view the shipment line item for item "(.+?)"$') { String productName ->
	client.shipments()
			.element()
			.lineitems()
			.findElement { lineitem ->
		lineitem[NAME_FIELD] == productName
	}
	.stopIfFailure()
}

Then(~'^the purchase line item configurable fields for item (.+) are:$') { String itemName, DataTable itemDetailsTable ->
	def Map<String, String> itemDetails = itemDetailsTable.asMap(String, String)

	getLineItemByName(itemName)

	for (Map.Entry<String, String> itemDetail : itemDetails.entrySet()) {
		assertThat(client.body.'configuration'.(itemDetail.key).toString())
				.as(itemDetail.key + " is not as expected")
				.isEqualTo(itemDetail.value)
	}
}

Then(~'^I open purchase line item (.+)$') { String itemName ->
	getLineItemByName(itemName)
}

Then(~'^I see the quantity "(\\d+?)" and name "(.+?)" fields on the shipment line item$') {
	int quantity, String productName ->

		assertThat(client.body.quantity)
				.as("Quantity is not as expected")
				.isEqualTo(quantity)
		assertThat(client.body.name.toString())
				.as("Name is not as expected")
				.isEqualTo(productName)
}

And(~'^I see a back link to the list of shipment line items$') { ->
	assertThat(client.body.links.findAll { link -> link.rel == "list" })
			.size()
			.as("Link back to the list of shipment line items was not found")
			.isEqualTo(1)
}

Then(~'^I can follow the shipment line item price link$') { ->
	client.price()
			.stopIfFailure()
}

And(~'^the purchase-price has fields amount: (.+?), currency: (.+?) and display: (.+?)$') {
	String expectedAmount, String expectedCurrency, String expectedDisplayName ->
		def costElement = client.body.'purchase-price'
		assertCost(costElement, expectedAmount, expectedCurrency, expectedDisplayName)
}

Then(~'^purchase item monetary total has fields amount: (.+), currency: (.+) and display: (.+)$') {
	def expectedAmount, def expectedCurrency, def expectedDisplayName ->
		def costElement = client.body.'monetary-total'
		assertCost(costElement, expectedAmount, expectedCurrency, expectedDisplayName)
}

And(~/^the purchase item monetary total has currency (.+) and display (.+)$/) { String expectedCurrency, String expectedDisplayName ->
	def costElement = client.body.'monetary-total'
	String costAmount = client.body.'monetary-total'[0].amount
	assertCost(costElement, costAmount, expectedCurrency, expectedDisplayName)
}

Then(~'^purchase item tax total has fields amount: (.+), currency: (.+) and display: (.+)$') {
	def expectedAmount, def expectedCurrency, def expectedDisplayName ->
		def taxElement = client.body.'tax-total'
		assertCost(taxElement, expectedAmount, expectedCurrency, expectedDisplayName)
}

And(~/^the purchase item tax total has currency (.+) and display (.+)$/) { def expectedCurrency, def expectedDisplayName ->
	def taxElement = client.body.'tax-total'
	String expectedAmount = client.body.'tax-total'.amount
	assertCost(taxElement, expectedAmount, expectedCurrency, expectedDisplayName)
}

And(~'^I see the cost field has amount: (.+?), currency: (.+?) and display: (.+?)$') {
	String expectedAmount, String expectedCurrency, String expectedDisplayName ->
		def costElement = client.body.cost
		assertCost(costElement, expectedAmount, expectedCurrency, expectedDisplayName)
}

And(~'^I see the total field has amount: (.+?), currency: (.+?) and display: (.+?)$') {
	String expectedAmount, String expectedCurrency, String expectedDisplayName ->
		def costElement = client.body.total
		assertCost(costElement, expectedAmount, expectedCurrency, expectedDisplayName)
}

And(~'^I see the tax type is (.+) currency is (.+) cost is (.+)$') { taxType, currency, amount ->
	assertThat(client["cost"].find {
		it ->
			it.title == taxType && it.currency == currency
	}.display)
			.as("Display is not as expected")
			.isEqualTo(amount)
}

And(~'^I can follow the back link to the shipment line item$') { ->
	assertThat(client.body.links.findAll { link -> link.rel == "lineitem" })
			.size()
			.as("Link back to the shipment line item was not found")
			.isEqualTo(1)
	client.lineitem()
			.stopIfFailure()
}

When(~'^I have previously made a purchase with a physical item (.+) in a tax free state code is (.+)$') { String productName, String taxFreePostCode ->

	addNewUSAddressForZeroTaxState()
	CommonMethods.searchAndOpenItemWithKeyword(productName)
	client.addtocartform()
			.addtodefaultcartaction(quantity: 1)
	CommonMethods.selectShippingAddressByPostalCode(taxFreePostCode)
	CommonMethods.selectAnyShippingOption()
	createPurchaseWithFollow()
}

When(~'^I view the details of a line item (.+) in a shipment$') { String productName ->
	client.findElement { lineitem ->
		lineitem[NAME_FIELD] == productName
	}
	.stopIfFailure()
}

When(~'^I navigate to shipment$') { ->
	client.shipments()
			.element()
			.stopIfFailure()
}

When(~'^I navigate to the billing address') { ->
	client.billingaddress()
			.stopIfFailure()
}

Then(~'^I can see the shipment status (.+)$') { String shipmentStatus ->
	assertThat(client.body.status.code.toString())
			.as("The shipment status is not as expected")
			.isEqualTo(shipmentStatus)
}

Then(~'^I can follow a link back to the list of shipments') { ->
	client.list()
			.stopIfFailure()
}
Then(~'^I can follow a link back to the purchase') { ->
	client.purchase()
			.stopIfFailure()
}

When(~'^I follow the shipping address link$') { ->
	client.destination()
			.stopIfFailure()
}
When(~'^I follow the shipment tax link$') { ->
	client.tax()
			.stopIfFailure()
}

When(~'^I navigate to the shipment line items$') { ->
	client.shipments()
			.element()
			.lineitems()
			.stopIfFailure()
}

Given(~'^I have purchased physical and digital items: "(.+?)" "(.+?)" "(.+?)"$') {
	String prod1, String prod2, String prod3 ->

		List<String> productList = new ArrayList<String>()
		productList.add(prod1)
		productList.add(prod2)
		productList.add(prod3)

		for (String product : productList) {
			CommonMethods.searchAndOpenItemWithKeyword(product)
			client.addtocartform()
					.addtodefaultcartaction(quantity: 1)
		}

		CommonMethods.selectAnyShippingOption()
		createPurchaseWithFollow()
}

And(~'^I can follow the line item link for product "(.+?)" and back to the list$') { String productName ->
	client.findElement { lineitem ->
		lineitem[NAME_FIELD] == productName
	}
	.list()
			.stopIfFailure()
}

def isProductLinkExists(def prodName) {
	def prodExists = false
	client.body.links.findAll {
		if (it.rel == "element") {
			client.GET(it.href)
			if (client["name"] == prodName) {
				prodExists = true
			}
		}
	}
	return prodExists
}

And(~'I do not see a link to line item "(.+?)"$') { String prodName ->
	assertThat(isProductLinkExists(prodName))
			.as("A link to $prodName should not exist")
			.isFalse()
}

Given(~'^I have previously made a purchase with item "(.+?)" quantity "(.+?)" and item "(.+?)" quantity "(.+?)"$') {
	def prod1, def qty1, def prod2, def qty2 ->

		CommonMethods.searchAndOpenItemWithKeyword(prod1)
		client.addtocartform()
				.addtodefaultcartaction(quantity: qty1)

		CommonMethods.searchAndOpenItemWithKeyword(prod2)
		client.addtocartform()
				.addtodefaultcartaction(quantity: qty2)

		CommonMethods.selectAnyShippingOption()
		createPurchaseWithFollow()
}

And(~'^I go back to the purchase$') { ->
	client.list()
			.shipment()
			.list()
			.purchase()
			.stopIfFailure()
}

Then(~'^I see (.+?) address$') { String name ->
	assertAddress(client[ADDRESS_FIELD], ADDRESS)
}

Then(~'^I can follow a link back to the shipment$') { ->
	client.shipment()
			.stopIfFailure()
}

Then(~'^I follow the shipping option link$') { ->
	client.shippingoption()
			.stopIfFailure()
}

Then(~'^I follow the shipment total link$') { ->
	client.total()
			.stopIfFailure()
}

Then(~'^I see shipping (.+) carrier information (.+)') { String shippingDisplayName, String carrierName ->
	assertThat(client[DISPLAY_NAME_FIELD].toString())
			.as("The display name is not as expected")
			.isEqualTo(shippingDisplayName)
	assertThat(client[CARRIER_FIELD].toString())
			.as("The shipping carrier is not as expected")
			.isEqualTo(carrierName)
}

Then(~'^I can follow the link to options for that shipment line item$') { ->
	assertLinkExists(client, "options")
	client.options()
			.stopIfFailure()
}

Then(~'^I view purchase line item option (.+)$') { String optionDisplayName ->
	client.GET("/")
			.defaultprofile()
			.purchases()
			.element()
			.lineitems()
			.element()
			.options()
	client.findElement { option ->
		option[DISPLAY_NAME_FIELD] == optionDisplayName
	}
	.stopIfFailure()
}

And(~'^I should see item option value is (.+)$') { String valueDisplayName ->
	client.value()
			.stopIfFailure()
	assertThat(client[DISPLAY_NAME_FIELD].toString())
			.as("The display name is not as expected")
			.isEqualTo(valueDisplayName)
}

And(~'^I can view the \"([^\"]+)\" option for that shipment line item$') {
	String optionDisplayName ->

		client.findElement { option ->
			option[DISPLAY_NAME_FIELD] == optionDisplayName
		}
		.stopIfFailure()
}

And(~'^I can view the \"([^\"]+)\" value for that option$') {
	String valueDisplayName ->

		client.value()
				.stopIfFailure()
		assertThat(client[DISPLAY_NAME_FIELD].toString())
				.as("The display name is not as expected")
				.isEqualTo(valueDisplayName)
}

And(~'^I can follow back links from a shipment option value all the way to the purchase$') { ->
	client.option()
			.list()
			.lineitem()
			.list()
			.shipment()
			.list()
			.purchase()
			.stopIfFailure()
}

When(~'^I go to the purchases$') { ->
	client.GET("/")
			.defaultprofile()
			.purchases()
			.element()
			.stopIfFailure()
}

Then(~'^the purchase status is (.+)$') { String valueStatus ->
	client.GET("/")
			.defaultprofile()
			.purchases()
			.element()
			.stopIfFailure()
	assertThat(client["status"])
			.as("The purchase status is not as expected")
			.isEqualTo(valueStatus)
}

Then(~'there are no shipments') { ->
	client.shipments()
	assertLinkDoesNotExist(client, "element")
}

Then(~'I do not see a link to options on a single sku item') { ->
	assertLinkDoesNotExist(client, "options")
}

Then(~'^I can follow the total link$') { ->
	client.total()
			.stopIfFailure()
}

And(~'^I can follow line item back links all the way to the purchase$') { ->
	client.lineitem()
			.list()
			.shipment()
			.list()
			.purchase()
			.stopIfFailure()
}

Then(~'^I can follow the price link$') { ->
	client.price()
			.stopIfFailure()
}

And(~'^I can follow the back link to the shipment line item option$') { ->
	client.option()
			.stopIfFailure()
}

And(~'^I can follow the back link to the shipment line item options list$') { ->
	client.list()
			.stopIfFailure()
}

When(~'^Adding an item with item code (.+) and quantity (.*) to the cart$') {
	def itemCode, def quantity ->
		client.GET("/")
				.searches()
				.keywordsearchform()
				.itemkeywordsearchaction(
				[
						'keywords' : itemCode,
						'page-size': 5
				])
				.follow()
				.element()
				.addtocartform()
				.addtodefaultcartaction(quantity: quantity)
				.stopIfFailure()
}

Then(~'the order is submitted') { ->
	createdPurchase = client.GET("/")
			.defaultcart()
			.order()
			.purchaseform()
			.submitorderaction()
			.follow()
			.save()
}

Then(~'the payment token created is used for the order') { ->
	client.paymentmeans()
			.element()
			.stopIfFailure()

	assertThat(client["display-name"])
			.as("Token display name is not as expected")
			.isEqualTo(TEST_TOKEN_DISPLAY_NAME)
}

And(~'^The store the shopper is in fulfils shipments with no delay$') { ->
	// This can't be controlled for Cortex, but the default store's warehouse has a pick-pack delay of 0
}

Then(~'^the number of purchase lineitems is (\\d+)$') { int numLineItems ->

	client.resume(purchaseUri)
			.lineitems()

	assertThat(client.body.links.findAll { it.rel == "element" })
			.size()
			.as("Number of purchase line items is not as expected")
			.isEqualTo(numLineItems)
}

Then(~'there exists a purchase line item for item (.+) with configurable fields:$') { String itemCode, DataTable modifierFieldsTable ->
	def Map<String, String> configurationFields = modifierFieldsTable.asMap(String, String)
	def actualMap
	client.resume(purchaseUri)
			.lineitems()
	client.body.links.find {
		if (it.rel == "element") {
			client.GET(it.href)
			actualMap = client.body.configuration
			if (configurationFields == actualMap) {
				return true
			}
		}
	}

	assertThat(actualMap)
			.as("Configurable field values are not as expected")
			.isEqualTo(configurationFields)
}

def getLineItemByName(def itemName) {
	client.GET("/")
			.defaultprofile()
			.purchases()
			.element()
			.lineitems()
			.findElement { lineitem ->
		lineitem[NAME_FIELD] == itemName
	}
	.stopIfFailure()
}

private void addNewUSAddressForZeroTaxState() {
	CommonMethods.createAddress("US", "", "Anchorage", "", "", "99501", "AK",
			"123 Main Street", "testFamilyName", "testGivenName")
}

When(~/^I lookup the newly purchased number$/) { ->
	CommonMethods.purchaseNumberLookup(savedPurchaseNumber)
}

Then(~/^the purchase number matches my new purchase$/) { ->
	assertThat(client.body.'purchase-number')
			.as("The purchase number is not as expected")
			.isEqualTo(savedPurchaseNumber)
}
When(~/^I look up an invalid purchase number (.+?)$/) { String purchaseNumber ->
	purchaseLookupByInvalidNumber(purchaseNumber)
}

public static void purchaseLookupByInvalidNumber(final String purchaseNumber) {
	client.GET("/")
			.lookups().purchaselookupform()
			.purchaselookupaction(["purchase-number": purchaseNumber])
			.stopIfFailure()
}

And(~/^I lookup other user's purchase number$/) { ->
	purchaseLookupByInvalidNumber(savedPurchaseNumber)
}

And(~/^I have an order for scope (.+) with following skus?/) { String scope, DataTable dataTable ->
	client.authAsAPublicUser(scope)

	String userName = UUID.randomUUID().toString() + "@elasticpath.com"
	client.GET("registrations/$scope/newaccount/form")
			.registeraction("family-name": FAMILY_NAME, "given-name": GIVEN_NAME, "password": PASSWORD, "username": userName)
	client.authRegisteredUserByName(scope, userName)
			.stopIfFailure()

	Map<String, String> orderDetails = dataTable.asMap(String, String)
	Iterator<Map.Entry<String, String>> it = orderDetails.entrySet().iterator()
	it.next() // skip skuCode -> quantity Pair. cannot remove due to map conversion above returning UnmodifiableMap
	while (it.hasNext()) {
		Map.Entry<String, String> pair = it.next()
		CommonMethods.lookupAndAddToCart(pair.getKey(), pair.getValue())
	}

	CommonMethods.addEmail()
	CommonMethods.addTokenToOrder()
	CommonMethods.addBillingAddress()
	CommonMethods.submitPurchase()
	client.stopIfFailure()
}