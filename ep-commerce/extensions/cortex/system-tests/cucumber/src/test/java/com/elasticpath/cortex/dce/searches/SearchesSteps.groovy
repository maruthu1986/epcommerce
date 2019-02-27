package com.elasticpath.cortex.dce.searches

import cucumber.api.DataTable
import cucumber.api.groovy.EN
import cucumber.api.groovy.Hooks

import static com.elasticpath.cortex.dce.ClasspathFluentRelosClientFactory.getClient
import static com.elasticpath.rest.ws.assertions.RelosAssert.assertLinkExists
import static org.assertj.core.api.Assertions.assertThat

import com.elasticpath.cortex.dce.CommonMethods

this.metaClass.mixin(Hooks)
this.metaClass.mixin(EN)

When(~'^I follow the root searches link$') { ->
	client.GET("/")
			.searches()
			.stopIfFailure()
}

Then(~'^I find the searches resource$') { ->
	assertLinkExists(client, "keywordsearchform")
}

When(~/^I search for item name (.+?)$/) { String searchItemName ->
	CommonMethods.searchAndOpenItemWithKeyword(searchItemName)
}

When(~/^I search for keyword \"(.+?)\"$/) { String keyword ->
	CommonMethods.search(keyword, "5")
	client.follow()
			.stopIfFailure()
}

When(~/^I search for the keyword \"(.+?)\" with page-size (.+)$/) { String keyword, String pageSize ->
	CommonMethods.search(keyword, pageSize)
	client.follow()
			.stopIfFailure()
}

When(~/^I POST to the search form the keyword \"(.+?)\" with page-size (.+)$/) { String keyword, String pageSize ->
	CommonMethods.search(keyword, pageSize)
}

When(~/^I POST to the search form with a (.+) char keyword$/) { int numberOfChars ->
	String keyword = ""
	for (i = 1; i < numberOfChars; i++) {
		keyword = keyword + "a"
	}
	CommonMethods.search(keyword, "5")
}

Then(~'the element list contains items with display-names') { DataTable dataTable ->
	def productNames = dataTable.asList(String)
	def resultsUri = client.body.self.uri
	for (String name : productNames) {
		Boolean found = false
		client.GET(resultsUri)
		client.findElement {
			item ->
				def definition = item.definition()
				if (definition["display-name"] == name)
					found = true
		}
		assertThat(found)
				.as("Item $name was not in element list")
				.isTrue()
	}
}
