package com.elasticpath.cortex.dce.carts

import static org.assertj.core.api.Assertions.assertThat

import static com.elasticpath.cortex.dce.ClasspathFluentRelosClientFactory.client
import static com.elasticpath.cortex.dce.CommonAssertion.assertItemConfiguration

import cucumber.api.DataTable
import cucumber.api.groovy.EN
import cucumber.api.groovy.Hooks

import com.elasticpath.cortex.dce.CommonMethods

this.metaClass.mixin(Hooks)
this.metaClass.mixin(EN)


Then(~'I (?:have|add) the item (.+) (?:in|to) the cart with quantity (.+) and configurable fields:$') { String itemCode, String itemQty, DataTable modifierFieldsTable ->
	def Map<String, String> configurationFields = modifierFieldsTable.asMap(String, String)

	CommonMethods.lookup(itemCode)

	client.addtocartform()
			.addtodefaultcartaction(
			["quantity"   : itemQty,
			 configuration: configurationFields
			])
			.stopIfFailure()
}

Then(~'I add the item to the cart with quantity (.+) and configurable fields:$') { String itemQty, DataTable modifierFieldsTable ->
	def Map<String, String> configurationFields = modifierFieldsTable.asMap(String, String)

	client.addtodefaultcartaction(
			["quantity"   : itemQty,
			 configuration: configurationFields
			])
			.stopIfFailure()
}

Then(~'I successfully add the item to the cart with quantity (.+) and configurable fields:$') { String itemQty, DataTable modifierFieldsTable ->
	def Map<String, String> configurationFields = modifierFieldsTable.asMap(String, String)
	client.addtodefaultcartaction(
			["quantity"   : itemQty,
			 configuration: configurationFields
			])
			.follow()
			.stopIfFailure()
}

Then(~'^the cart lineitem with itemcode (.+) has quantity (.+) and configurable fields as:$') { String itemSkuCode, String qty, DataTable itemDetailsTable ->
	client.GET("/")
			.defaultcart()
			.lineitems()
			.stopIfFailure()

	CommonMethods.findCartElementBySkuCodeAndConfigurableFieldValues(itemSkuCode, itemDetailsTable)

	assertThat(client.body.'quantity'.toString())
			.as("Line item quantity does not match for itemcode - " + itemSkuCode)
			.isEqualTo(qty)
}

When(~'^I change the lineitem quantity of configurable item code (.+) with given configuration to (.+)$') { String itemSkuCode, String newQuantity, DataTable itemDetailsTable ->
	def lineitemUri = CommonMethods.findCartLineItemUriBySkuCodeAndConfigurableFieldValues(itemSkuCode, itemDetailsTable)
	client.PUT(lineitemUri, [
			quantity: newQuantity
	])

	assertThat(client.response.status)
			.as("HTTP response status is not as expected")
			.isEqualTo(204)
}

When(~'^I delete the configurable lineitem with code (.+) and with given configuration from my cart$') { String itemSkuCode, DataTable itemDetailsTable ->
	def lineitemUri = CommonMethods.findCartLineItemUriBySkuCodeAndConfigurableFieldValues(itemSkuCode, itemDetailsTable)
	client.DELETE(lineitemUri)

	assertThat(client.response.status)
			.as("HTTP response status is not as expected")
			.isEqualTo(204)
}

Then(~'^I should see favorites line item configurable fields for itemcode (.+) as:$') { String itemSkuCode, DataTable itemDetailsTable ->
	client.GET("/")
			.defaultwishlist()
			.lineitems()
			.stopIfFailure()

	CommonMethods.findCartElementBySkuCode(itemSkuCode)

	assertItemConfiguration(itemDetailsTable)
}

Then(~'I should see favorites line item (.+) with configurable field values as:$') { String itemSkuCode, DataTable itemDetailsTable ->
	client.GET("/")
			.defaultwishlist()
			.lineitems()
			.stopIfFailure()
	CommonMethods.findCartElementBySkuCodeAndConfigurableFieldValues(itemSkuCode, itemDetailsTable)
}

Then(~'^I should see in the response the line item just added with configurable fields as:$') { DataTable itemDetailsTable ->
	assertItemConfiguration(itemDetailsTable)
}

