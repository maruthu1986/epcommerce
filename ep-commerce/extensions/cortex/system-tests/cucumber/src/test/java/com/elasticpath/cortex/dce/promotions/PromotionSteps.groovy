package com.elasticpath.cortex.dce.promotions

import static org.assertj.core.api.Assertions.assertThat

import static com.elasticpath.cortex.dce.ClasspathFluentRelosClientFactory.client
import static com.elasticpath.cortex.dce.SharedConstants.*
import static com.elasticpath.rest.ws.assertions.RelosAssert.assertLinkExists

import cucumber.api.groovy.EN
import cucumber.api.groovy.Hooks

import com.elasticpath.cortex.dce.CommonMethods

this.metaClass.mixin(Hooks)
this.metaClass.mixin(EN)

def final ZOOM_URI = "?zoom="

def final DISPLAY_DESCRIPTION_FIELD = "display-description"

def final APPLIED_PROMOTIONS_LINK = "appliedpromotions"

When(~'^the (.+) promotion link is followed from the coupon$') { String promoName ->
	client.follow()
			.appliedpromotions()
			.findElement { promotion ->
		promotion[NAME_FIELD] == promoName
	}
	.stopIfFailure()
}

Then(~'^the applied promotion shows (.+?)$') { String promoName ->
	client.GET("/")
			.defaultcart()
			.stopIfFailure();

	client.appliedpromotions()
			.stopIfFailure();

	def numberOfPromotions = client.body.links.findAll { link ->
		link.rel == "element"
	}.size()

	assertThat(numberOfPromotions)
			.as("Number of promotions is not as expected")
			.isEqualTo(1)

	client.element()
			.stopIfFailure()

	assertThat(client["display-name"])
			.as("Display name is not as expected")
			.isEqualTo(promoName)
}

Then(~'^the following promotion details are displayed$') { table ->
	Map expectedValues = table.asMap(String.class, String.class)

	assertThat(client[DISPLAY_DESCRIPTION_FIELD])
			.as("Display description is not as expected")
			.isEqualTo(expectedValues.get(DISPLAY_DESCRIPTION_FIELD))
	assertThat(client[DISPLAY_NAME_FIELD])
			.as("Display name is not as expected")
			.isEqualTo(expectedValues.get(DISPLAY_NAME_FIELD))
	assertThat(client[NAME_FIELD])
			.as("Name is not as expected")
			.isEqualTo(expectedValues.get(NAME_FIELD))
}

When(~'I (?:have|add) item (.+) (?:in|to) the (?:default cart|cart)$') { String productName ->
	CommonMethods.searchAndAddProductToCart(productName)

	client.follow()
			.stopIfFailure()
}

When(~'a purchase is created with promotion (.+)$') { String promoItemName ->
	CommonMethods.searchAndAddProductToCart(promoItemName)

	CommonMethods.submitPurchase()
	client.follow()
}

Then(~'^the list of applied promotions contains promotion (.+)$') { String promotionName ->
	verifyPromotionIsPresentInAppliedPromotions(promotionName)
}

When(~'^I go to my default cart$') { ->
	client.GET("/").defaultcart()
}

When(~'^I apply a coupon code (.+) to my order$') { String couponCode ->
	client.GET("/")
			.defaultcart()
			.order()
			.couponinfo()
			.couponform()
			.applycouponaction(["code": couponCode])
			.GET("/")
			.defaultcart()
			.stopIfFailure()
}

Then(~'^the corresponding promotion of the coupon is inactive$') { ->
	//do nothing - the promotion will only active if the trigger product is added to cart.
}

Then(~'^there is a list of applied promotions on the (?:coupon details|purchase|cart|cart line item|shipping option)$') { ->
	assertLinkExists(client, APPLIED_PROMOTIONS_LINK)
}

Then(~'^the applied promotion (.+) matches the coupons$') { String couponPromoName ->
	client.appliedpromotions()
			.stopIfFailure();

	def numberOfPromotions = client.body.links.findAll { link ->
		link.rel == "element"
	}.size()

	assertThat(numberOfPromotions)
			.as("Number of promotions is not as expected")
			.isEqualTo(1)

	client.element()
			.stopIfFailure()

	assertThat(client["name"])
			.as("Promotion name is not as expected")
			.isEqualTo(couponPromoName)
}

Then(~'^the list of applied promotions is empty$') { ->
	client.appliedpromotions()
			.stopIfFailure()

	def promotions = client.body.links.findAll {
		link ->
			link.rel == "element"
	}
	assertThat(promotions)
			.as("List of applied promotions should be empty")
			.isEmpty()
}

When(~'^I view an item (.+?) that (?:has a discount triggered by a |does not have )*promotion$') { String productName ->
	CommonMethods.searchAndOpenItemWithKeyword(productName)
	client.stopIfFailure()
}

Given(~'^I fill in address for Canadian Shipping$') { ->
	createDefaultAddressForCanadianShipping()
	// NOTE: Destination is already selected because this was a new user and
	// the first address gets selected as default.
}

Given(~'^I select shipping option (.+)$') { String shippingOption ->
	// NOTE: Destination is already selected because this was a new user and
	// the first address gets selected as default.
	CommonMethods.selectShippingOption(shippingOption) //"Fixed Price With 100% Off Promotion Shipping Option")
}

When(~'^I view a selected shipping option with promotions$') { ->
	// Assume that we start on the shipping option selector.
	client.chosen()
			.description()
			.stopIfFailure()
}

When(~'^I view an unselected shipping option (.+)$') { String shippingOptionWithWithOutPromo ->
	// Assume that we start on the shipping option selector.
	client.findChoice {
		shippingoption ->
			def description = shippingoption.description()
			description["name"] == shippingOptionWithWithOutPromo
	}
	.description()
			.stopIfFailure()
}

And(~'^a personalisation header triggers a cart promotion$') { ->
	// This is set up in the test data.  Product triggerprodforpersonalisedcartdiscountpromo in cart will
	// trigger promo PersonalisedCartDiscountPromo (15% cart sub-total discount) for shoppers with age > 45
	CommonMethods.addPersonalisationHeader("CUSTOMER_AGE_YEARS", "50")
}

When(~'^I view the unselected shipping option (.+) with personalised shipping promotions$') { String shippingOption ->
	CommonMethods.addPersonalisationHeader("REFERRING_URL", "personalisedshippingpromos.elasticpath.net")
	// NOTE: Destination is already selected because this was a new user and
	// the first address gets selected as default.
	CommonMethods.findShippingOptionChoiceOrChosen(shippingOption)
	client.description()
			.stopIfFailure()
}

And(~'I zoom (.+) to the line item (.+) that contains the promotion (.+)') { String zoomParamValue, String expectedProductName, String expectedPromotionName ->
	client.GET(DEFAULT_CART_URL + ZOOM_URI + zoomParamValue)
	def bodyElements = client.body._lineitems._element[0]
	def found = false
	for (def element : bodyElements) {
		if (hasProductInLineItemElement(element, expectedProductName)
				&& hasPromotionInLineItemElement(element, expectedPromotionName)) {
			found = true
			break
		}
	}
	assertThat(found)
			.as("Product not found")
			.isTrue()
}

And(~'I zoom (.+) into the cart lineitem price and total$') { String zoomParamValue ->
	client.GET(DEFAULT_CART_URL + ZOOM_URI + zoomParamValue)
}

Then(~'I see the lineitiem list-price display is (.+)$') { String expectedListPriceDisplay ->
	def actualListPriceDisplay = client.body._lineitems._element[0]._price[LIST_PRICE_FIELD][0][0][0].display
	assertThat(actualListPriceDisplay)
			.as("The lineitem list-price display is not as expected")
			.isEqualTo(expectedListPriceDisplay)
}

And(~'I see the lineitem purchase-price display is (.+)$') { String expectedPurchasePriceDisplay ->
	def actualPurchasePriceDisplay = client.body._lineitems._element[0]._price[PURCHASE_PRICE_FIELD][0][0][0].display
	assertThat(actualPurchasePriceDisplay)
			.as("The lineitem purchase-price display is not as expected")
			.isEqualTo(expectedPurchasePriceDisplay)
}

And(~'I see the lineitem total cost display is (.+)$') { String expectedTotalCostDisplay ->
	def actualTotalCostDisplay = client.body._lineitems._element[0]._total[COST_FIELD][0][0][0].display
	assertThat(actualTotalCostDisplay)
			.as("The lineitem total cost display is not as expected")
			.isEqualTo(expectedTotalCostDisplay)
}

private boolean hasProductInLineItemElement(def element, String expectedProductName) {
	def productName = element._item._definition[DISPLAY_NAME_FIELD][0][0]
	return expectedProductName.equals(productName);
}

private boolean hasPromotionInLineItemElement(def element, String expectedPromotionName) {
	def promotionName = element._appliedpromotions._element[NAME_FIELD][0][0]
	return expectedPromotionName.equals(promotionName);
}

private void createDefaultAddressForCanadianShipping() {
	def randomAddress = UUID.randomUUID().toString() + "random street"
	CommonMethods.createAddress("CA", "", "Vancouver", "", "", "V7V7V7", "BC",
			randomAddress, "itest", "generated")
}

private verifyPromotionIsPresentInAppliedPromotions(String promotionName) {
	client.appliedpromotions()
			.findElement { promotion ->
		promotion[NAME_FIELD] == promotionName
	}
	.stopIfFailure()
}

