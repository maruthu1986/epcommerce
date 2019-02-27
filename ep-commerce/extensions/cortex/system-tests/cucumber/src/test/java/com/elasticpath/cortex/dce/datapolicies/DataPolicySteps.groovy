/*
 * Copyright © 2018 Elastic Path Software Inc. All rights reserved.
 */
package com.elasticpath.cortex.dce.datapolicies

import static org.assertj.core.api.Assertions.assertThat

import static com.elasticpath.cortex.dce.ClasspathFluentRelosClientFactory.client

import cucumber.api.DataTable
import cucumber.api.groovy.EN
import cucumber.api.groovy.Hooks

import com.elasticpath.cortex.dce.SharedConstants

this.metaClass.mixin(Hooks)
this.metaClass.mixin(EN)

/**
 * Retrieve list of data policies.
 * @return list
 */
def retrieveDataPolicyList() {
	def actualDataPolicyList = []
	client.body.links.findAll {
		if (it.rel == "element") {
			client.GET(it.uri)
			actualDataPolicyList.add(client.body.'policy-name')
		}
	}
	return actualDataPolicyList
}

Given(~'^the following data policies are assigned to data policy segment (.+)$') { String value, DataTable dataPolicyListTable ->
	//	Non implementation Given statement.
}

Given(~'the following data policies with segment (.+) are in (?:Disabled|Draft) state$') { String segment, DataTable dataPolicyListTable ->
	//	Non implementation Given statement.
}

When(~'^I access the data policies resource from root') { ->
	client.GET("/")
			.'data-policies'()
			.stopIfFailure()
}

When(~'^I access the data policies resource from a scope with no data polices enabled') { ->
	def scope = SharedConstants.TOKEN_SCOPE
	client.GET("datapolicies/$scope")
}

Then(~'^I can follow a link to data policies') { ->
	client.'data-policies'()
			.stopIfFailure()
}

Then(~'the following expected data policies are visible in my profile') { DataTable dataPolicyListTable ->
	def dataPolicyList = dataPolicyListTable.asList(String)
	assertThat(retrieveDataPolicyList())
			.containsExactlyElementsOf(dataPolicyList)

}

Then(~'the following list of data policies are not visible') { DataTable dataPolicyListTable ->
	def dataPolicyList = dataPolicyListTable.asList(String)
	assertThat(retrieveDataPolicyList())
			.doesNotContainAnyElementsOf(dataPolicyList)
}

Then(~'I should not see any data policies$') { ->
	boolean containsDataPolicies = false
	client.body.'links'.each { link ->
		if (link.rel == "element") {
			containsDataPolicies = true
		}
	}
	assertThat(client.body.'links'.size())
			.as("Number of links is not as expected")
			.isEqualTo(1)

	assertThat(containsDataPolicies)
			.as("Resource should not contain data policies")
			.isFalse()
}

Then(~'^I can (?:access|see) (?:a|the) (?:data policy|data policies) with the following (?:field|fields):') { DataTable dataPolicyTable ->
	def policyFlag = "data-policy-consent"
	def policyKey = "policy-reference-key"
	def policyName = "policy-name"

	def dataPoliciesMap = dataPolicyTable.asMap(String, String)
	def dataPolicy = client.body

	assertThat(dataPolicy[policyFlag])
			.as("Policy flag is not as expected.")
			.isEqualTo(dataPoliciesMap.get(policyFlag))

	assertThat(dataPolicy[policyKey].toString())
			.as("Policy key is not as expected.")
			.isEqualTo(dataPoliciesMap.get(policyKey))

	assertThat(dataPolicy[policyName].toString())
			.as("Policy name is not as expected.")
			.isEqualTo(dataPoliciesMap.get(policyName))
}

When(~/^I attempt to access the data policy after removing the (.*) header/) { String headerName ->
	Map<String, String> headers = (HashMap) client.getHeaders()
	headers.remove(headerName)
	client.GET(client.body.self.href)
}

When(~/^I remove the (.*) header$/) { String headerName ->
	Map<String, String> headers = (HashMap) client.getHeaders()
	headers.remove(headerName)
}

When(~/^I access the data policy form$/) { ->
	client.datapolicyconsentform()
			.stopIfFailure()
}

When(~/^I post the following fields to the data policy form:$/) { DataTable dataPolicyTable ->
	def dataPoliciesMap = dataPolicyTable.asMap(String, String)
	client.POST(client.body.self.href, dataPoliciesMap)
			.stopIfFailure()
}

When(~/^I select the data policy (.*)$/) { String policyName ->
	def policyExist = false

	client.body.links.find {
		if (it.rel == 'element') {
			client.GET(it.href)
					.stopIfFailure()
			if (client.body.'policy-name' == policyName) {
				policyExist = true
			}
		}
	}

	assertThat(policyExist)
			.as("Unable to find the given data policy name - " + policyName)
			.isTrue()
}