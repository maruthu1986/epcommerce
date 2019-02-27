/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.cortex.dce.coupons

import static org.assertj.core.api.Assertions.assertThat

import static com.elasticpath.cortex.dce.ClasspathFluentRelosClientFactory.client
import static com.elasticpath.cortex.dce.SharedConstants.*
import static com.elasticpath.rest.ws.assertions.RelosAssert.assertLinkDoesNotExist

import cucumber.api.groovy.EN
import cucumber.api.groovy.Hooks

import com.elasticpath.cortex.dce.CommonMethods

this.metaClass.mixin(Hooks)
this.metaClass.mixin(EN)


Given(~'^I have no coupons applied to an order$') { ->
	client.authAsAPublicUser(DEFAULT_SCOPE)
			.stopIfFailure()
}

When(~'^I login to the registered shopper with email ID (.+)$') { String userEmailId ->
	client.authRegisteredUserByName(DEFAULT_SCOPE, userEmailId)
			.stopIfFailure()
}

When(~'^I transition to the registered shopper with email ID (.+)$') { String userEmailId ->
	client.roleTransitionToRegisteredUserByName(DEFAULT_SCOPE, userEmailId)
			.stopIfFailure()
}

And(~'^I create a purchase for item (.+) with saved coupon$') { String productName ->
	CommonMethods.searchAndOpenItemWithKeyword(productName)
	client.addtocartform()
			.addtodefaultcartaction(quantity: 1)
			.follow()
			.stopIfFailure()
	CommonMethods.submitPurchase()
	client.stopIfFailure();
}

//matches:
//Shopper applies a coupon that has not been previously applied to the order
//Shopper applies a coupon to the order
When(~'^I (?:apply|applied) a coupon code (.+?) (?:that has not been previously applied )*to the order$') { String couponCode ->
	applyCoupon(couponCode)

	assertThat(client.response.status)
			.as("HTTP response status is not as expected")
			.isEqualTo(201)
}

When(~'^I re-apply the same coupon code (.+) to the same order$') { String couponCode ->
	applyCoupon(couponCode)

	assertThat(client.response.status)
			.as("HTTP response status is not as expected")
			.isEqualTo(200)
}

When(~'^I apply an invalid coupon (.+) to the order') { String couponCode ->
	applyCoupon(couponCode)

}

Given(~'^a coupon code (.+) exists') { String couponCode ->

}

Given(~'^shopper (.+) has an auto applied coupon (.+)') { String customerId, String couponCode -> }

And(~'^I retrieve the coupon info for the (?:new )*order$') { ->
	getCouponInfo()
}

Then(~'^there (?:is|are) exactly (.+) (?:coupon|coupons) applied to the (?:order|purchase)$') { def expectedNumberOfCoupons ->
	getCouponInfo()

	if (expectedNumberOfCoupons.toInteger() == 0) {
		assertLinkDoesNotExist(client, "coupon")
	} else {
		def newNumberOfCoupons = client.body.links.findAll { link ->
			link.rel == "coupon"
		}.size()
		assertThat(newNumberOfCoupons)
				.as("Number of coupons is not as expected")
				.isEqualTo(expectedNumberOfCoupons.toInteger())
	}
}

Then(~'^the coupon is not accepted') { ->
	assertThat(client.response.status)
			.as("HTTP response status is not as expected")
			.isEqualTo(409)
}
//matches:
//the COUPON is the one that was applied
//the COUPON is the one that was auto applied
And(~'^the coupon (.+) is (?:auto )*applied to the order$') { String couponCode ->
	getCouponInfo()

	def criteria = { coupon ->
		coupon["code"] == couponCode
	}

	client.findLink("coupon", criteria)
			.stopIfFailure()
}

Then(~'^the code of the coupon (.+) is displayed$') { String couponCode ->
	assertThat(client["code"])
			.as("Coupon code is not as expected")
			.isEqualTo(couponCode)
}

When(~'^I remove the coupon (.+) from the order$') { String couponCode ->
	removeCoupon(couponCode)

}


When(~'^I retrieve the coupon (.+) details of my order$') { String couponCode ->
	def criteria = { coupon ->
		coupon["code"] == couponCode
	}

	getCouponInfo()
	client.findLink("coupon", criteria)
			.stopIfFailure()
}

When(~'^Shopper retrieves the coupon info of their purchase$') { ->
	client.follow()
			.coupons()
			.stopIfFailure()
}

When(~'^I retrieve the coupon (.+) details of my purchase$') { String couponCode ->
	client.follow()
			.coupons()
			.findElement { coupon ->
		coupon["code"] == couponCode
	}
	.stopIfFailure()
}

Given(~'^Shopper has coupon (.+) applied to their order$') { String couponCode ->
	client.authAsAPublicUser(DEFAULT_SCOPE)
	applyCoupon(couponCode)
}

Given(~'^I have the product (.+) with coupon (.+) applied to my purchase$') { String productName, String couponCode ->

	client.authAsRegisteredUser()
			.stopIfFailure()

	removeExistingCoupons();

	// for a purchase to have a coupon it must trigger a promotion.
	CommonMethods.searchAndOpenItemWithKeyword(productName)
	client.addtocartform()
			.addtodefaultcartaction(quantity: 1)
			.follow()
			.stopIfFailure()

	applyCoupon(couponCode)


	CommonMethods.submitPurchase()
	client.stopIfFailure()
}

private removeExistingCoupons() {
//clean up if coupons exist on the cart
	getCouponInfo()


	def coupons = client.body.links.findAll {
		link ->
			link.rel == "coupon"
	}

	for (def coupon : coupons) {
		client.DELETE(coupon.href)
	}
}

private applyCoupon(couponCode) {
	client.GET("/")
			.defaultcart()
			.order()
			.couponinfo()
			.couponform()
			.applycouponaction(["code": couponCode])
}

private getCouponInfo() {
	client.GET("/")
			.defaultcart()
			.order()
			.couponinfo()
}

private removeCoupon(couponcode) {
	getCouponInfo()

	def criteria = { coupon ->
		coupon["code"] == couponcode
	}
	client.findLink("coupon", criteria)

	def couponUri = client.body.self.uri

	client.DELETE(couponUri)
}