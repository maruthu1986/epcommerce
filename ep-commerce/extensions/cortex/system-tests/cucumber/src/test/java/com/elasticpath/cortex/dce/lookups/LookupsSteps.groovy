/*
 * Copyright © 2014 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.cortex.dce.lookups

import static org.assertj.core.api.Assertions.assertThat

import static com.elasticpath.cortex.dce.ClasspathFluentRelosClientFactory.client
import static com.elasticpath.cortex.dce.SharedConstants.*

import cucumber.api.DataTable
import cucumber.api.groovy.EN
import cucumber.api.groovy.Hooks

import com.elasticpath.cortex.dce.CommonMethods

this.metaClass.mixin(Hooks)
this.metaClass.mixin(EN)

When(~'^I follow a link back to the item') { ->
	client.item()
			.stopIfFailure()
}

When(~/^I look up an item with code (.+?)$/) { String skuCode ->
	CommonMethods.lookup(skuCode)
}

When(~/^I look up an (?:invalid|out of scope) item (.+?)$/) { String skuCode ->
	itemLookupByInvalidCode(skuCode)
}

When(~/^I (?:add item|have item) with code (.+?) (?:to my|in my) cart$/) { String itemCode ->
	CommonMethods.lookupAndAddToCart(itemCode, 1)
}

When(~/^I (?:add item|have item) with code (.+?) (?:to my|in my) cart without the required configurable fields$/) { String itemCode ->
	CommonMethods.lookup(itemCode)
	client.addtocartform()
			.addtodefaultcartaction(quantity: 1)
			.stopIfFailure()
}

When(~/^I (?:add|have) item with code (.+?) (?:to my|in my) cart with quantity (\d+)$/) { String itemCode, int qty ->
	CommonMethods.lookupAndAddToCart(itemCode, qty)
}

When(~'I add following items to the cart$') { DataTable skuList ->
	for (def sku : skuList.asList(String)) {
		CommonMethods.lookupAndAddToCart(sku, 1)
	}
}

When(~/^I add item with code (.+?) to my cart with quantity (\d+) and do not follow location$/) { String itemCode, int qty ->
	CommonMethods.lookupAndAddToCartNoFollow(itemCode, qty)
}

Then(~'I (?:have a|add an item with code|have an item with code) (.+) (?:in|to) the cart with quantity (.+) and configurable fields:$') { String itemCode, String itemQty, DataTable modifierFieldsTable ->
	Map<String, String> configurationFields = modifierFieldsTable.asMap(String, String)
	addConfigurableItemToCart(itemCode, itemQty, configurationFields)
}

Given(~'a registered shopper (.+) with the following configured item in their cart') { String email, DataTable modifierFieldsTable ->
	Map<String, String> configurationFields = new HashMap<>(modifierFieldsTable.asMap(String, String))
	def itemcode = configurationFields.get("itemcode")
	configurationFields.remove("itemcode")
	def itemqty = configurationFields.get("itemqty")
	configurationFields.remove("itemqty")

	client.authRegisteredUserByName(DEFAULT_SCOPE, email)
			.stopIfFailure()
	client.GET("/")
			.defaultcart()
			.lineitems()
			.stopIfFailure()
	client.DELETE(client.body.self.uri)
	addConfigurableItemToCart(itemcode, itemqty, configurationFields)
}

When(~'^I change the multi sku selection by (.+) and select choice (.+)$') { String itemOption, String itemChoice ->
	client.definition()
			.options()
	client.findElement { option ->
		option[DISPLAY_NAME_FIELD] == itemOption
	}
	.selector()
			.findChoice { itemoption ->
		def description = itemoption.description()
		description[DISPLAY_NAME_FIELD] == itemChoice
	}
	.selectaction()
			.follow()
			.stopIfFailure()
}

Then(~'^the item code is (.+)$') { String itemSkuCode ->
	client.code()
	assertThat(client["code"])
			.as("Item code is not as expected")
			.isEqualTo(itemSkuCode)
}

Then(~'^I should see item name is (.+)$') { String itemName ->
	client.definition()
			.stopIfFailure()
	assertThat(client[DISPLAY_NAME_FIELD])
			.as("Item name is not as expected")
			.isEqualTo(itemName)
}

Then(~'^I should see item details shows: display name is (.+) and display value is (.+)$') { String itemDisplayName, String itemDisplayValue ->
	assertThat(client.body.details.'display-name')
			.as("Display name is not as expected")
			.isEqualTo([itemDisplayName])
	assertThat(client.body.details.'display-value')
			.as("Display value is not as expected")
			.isEqualTo([itemDisplayValue])
}

Given(~'^I retrieve the batch items lookup form$') { ->
	client.GET("/")
			.lookups()
			.batchitemslookupform()
			.stopIfFailure()
}

When(~'^I submit a batch of sku codes (.*)$') { final String skuCodes ->
	client.GET("/")
			.lookups()
			.batchitemslookupform()
			.batchitemslookupaction([codes: skuCodes])
			.follow()
			.stopIfFailure()
}

Then(~'^a batch of (.*) items is returned$') { final int numberOfItems ->
	def items = client.body.links.findAll {
		link ->
			link.rel == "element"
	}
	assertThat(items).size()
			.as("Number of elements is not as expected")
			.isEqualTo(numberOfItems)
}

Then(~'^the batch lookup returns the correct (.+)$') { String skuCodes ->
	List<String> sku_codes = Eval.me(skuCodes)
	def elementResponse = client.save()

	assertThat(client.body.links.size())
			.as("Number of elements is not as expected")
			.isEqualTo(sku_codes.size())
	
	for (String skucode : sku_codes) {
		boolean itemExists = false
		client.resume(elementResponse)

		client.body.links.find {
			if (it.rel == "element") {
				client.GET(it.href)
				client.code()
				if (client["code"] == skucode) {
					itemExists = true
				}
			}
		}
		assertThat(itemExists)
				.as("Item not found for sku code: " + skucode)
				.isTrue()
	}
}

When(~'I submit the invalid item uri (.+)$') { String uri ->
	client.GET(uri)
			.stopIfFailure()
}


When(~/^I cannot add to cart line item with code (.+?) with quantity (\d+)$/) { String itemCode, int qty ->
	CommonMethods.lookup(itemCode)
	client.addtocartform()
			.addtodefaultcartaction(quantity: qty)
			.stopIfFailure()
	assertThat(client.response.status)
			.as("The response status is not as expected")
			.isEqualTo(400)

}


public static void itemLookupByInvalidCode(final String itemCode) {
	client.GET("/")
			.lookups().itemlookupform()
			.itemlookupaction([code: itemCode])
			.stopIfFailure()
}

public static addConfigurableItemToCart(String itemCode, String itemQty, Map<String, String> configurationFields) {
	CommonMethods.lookup(itemCode)
	client.addtocartform()
	client.addtodefaultcartaction(
			["quantity"   : itemQty,
			 configuration: configurationFields
			])
			.stopIfFailure()
}