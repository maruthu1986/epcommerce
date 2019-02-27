package com.elasticpath.cortex.dce.navigations

import static org.assertj.core.api.Assertions.assertThat

import static com.elasticpath.cortex.dce.ClasspathFluentRelosClientFactory.getClient

import cucumber.api.DataTable
import cucumber.api.groovy.EN
import cucumber.api.groovy.Hooks
import org.json.JSONArray

import com.elasticpath.cortex.dce.CommonMethods

this.metaClass.mixin(Hooks)
this.metaClass.mixin(EN)

def final USER_TRAITS_HEADER = "x-ep-user-traits"

When(~'^I (?:open|follow) the root navigations$') { ->
	client.GET("/")
			.navigations()
			.stopIfFailure()
}

And(~'^the expected navigation list exactly matches the following$') { DataTable navigationTable ->
	def navigationList = navigationTable.asList(String)
	def actualCategoryList = []

//	findAll loops through everything inside the links. "it" is the first item return. Then it looks for "element"
// 	and retrieve the uri and get the "name" value to store in the list.
	client.body.links.findAll {
		if (it.rel == "element") {
			client.GET(it.href)
			actualCategoryList.add(client["name"])
		}
	}

// Verifies if the list is exactly the same and in the same order.
	assertThat(actualCategoryList)
			.containsExactlyElementsOf(navigationList)
}

And(~'the navigation node (.+) should contain exactly the following child nodes$') { String nodeName, DataTable childNodesTable ->
	def childNodeList = childNodesTable.asList(String)
	def actualChildNodeList = []

	findCategory(nodeName)
	client.body.links.findAll {
		if (it.rel == "child") {
			client.GET(it.href)
			actualChildNodeList.add(client["name"])
		}
	}
	assertThat(actualChildNodeList)
			.containsExactlyElementsOf(childNodeList)
}

And(~'the child node (.+) should contain the following sub child nodes$') { String childNode, DataTable subChildNodesTable ->
	def subChildNodeList = subChildNodesTable.asList(String)
	def actualSubChildNodeList = []

//	Navigate back to parent node to retrieve all child nodes since the previous step stops at the last child node.
	client.parent()

	client.body.links.findAll {
		if (it.rel == "child") {
			client.GET(it.href)
			if (client["name"] == childNode) {
				client.body.links.findAll {
					if (it.rel == "child") {
						client.GET(it.href)
						actualSubChildNodeList.add(client["name"])
					}
				}
			}
		}
	}

	assertThat(actualSubChildNodeList)
			.containsExactlyElementsOf(subChildNodeList)
}

Then(~'the items are listed in the follow order') { DataTable dataTable ->
	ArrayList<String> orderedDisplayName = dataTable.asList(String)
	JSONArray jsonArray = new JSONArray()

	int i = 0
	boolean nextExists = true

	while (nextExists) {
		nextExists = false
		client.body.links.find {
			if (it.rel == "element") {
				jsonArray.put(i, it)
				i++
				println("$i: $it")
			}
		}
		client.body.links.find {
			if (it.rel == "next") {
				client.next()
				nextExists = true
			}
		}
	}

	assertThat(orderedDisplayName)
			.size()
			.as("The number of items in list was less than the number of links")
			.isLessThanOrEqualTo(jsonArray.length())

	def listUri = client.body.self.uri
	i = 0

	for (def displayName : orderedDisplayName) {
		client.GET(jsonArray.get(i).getAt("href"))
				.definition()
		assertThat(client["display-name"])
				.as("Element $i was not as expected")
				.isEqualTo(displayName)
		i++
	}
	client.GET(listUri)
}

Then(~'I open the navigation category (.+)') { def value ->
	client.GET("/")
			.navigations()
			.stopIfFailure()
	CommonMethods.openLinkRelWithFieldWithValue("element", "name", value)
}

Then(~'I request the navigation definition of (.+) in language (.+)') { def category, def language ->
	client.headers.put(USER_TRAITS_HEADER, "LOCALE=$language")
	client.GET("/")
			.navigations()
			.stopIfFailure()
	CommonMethods.openLinkRelWithFieldWithValue("element", "name", category)
}

Then(~'I open the navigation subcategory (.+)') { String resources ->
	List<String> path = new ArrayList<String>()
	path.addAll(resources.split("->"))

	client.GET("/")
			.navigations()
			.stopIfFailure()
	CommonMethods.openLinkRelWithFieldWithValue("element", "name", path.get(0).trim())
	path.remove(0)
	for (String name : path) {
		CommonMethods.openLinkRelWithFieldWithValue("child", "name", name.trim())
	}
}


Then(~'the items link contains (.+) elements') { Integer count ->
	client.items()
			.stopIfFailure()

	assertThat(client.body.links.toString().count("rel:element"))
			.as("The number of links is not as expected")
			.isEqualTo(count)
}

Given(~'(?:.+) is missing a value for (?:.+)') { -> }

Given(~'category (?:.+) has a subcategory and no parent category$') { -> }

Given(~'the (?:catalog|category) (?:.+) has (?:.+) (?:categories|subcategories|subcategory)$') { -> }

Given(~'the category (?:.+) is a top level category with no subcategories') { -> }

Given(~'the category (?:.+) contains (?:.+) (?:item|items)') { -> }

Given(~'the item (?:.+) belongs to a subcategory of (?:.+)') { -> }

Given(~'featured items are configured for the category (?:.+)') { -> }

Given(~'that (?:.+) does not belong to the current scope') { -> }


def findCategory(categoryName) {
	client.findElement { category ->
		category["name"] == categoryName
	}
}

When(~/^I look up navigation item with (?:category|sub-category) code (.*)$/) { String categoryCode ->
	CommonMethods.navigationLookupCode(categoryCode)
}

Then(~/^I should see navigation details has display-name (.*)$/) { final String displayName ->
	def navigationDisplayName = client["display-name"]
	assertThat(navigationDisplayName)
			.isEqualTo(displayName)
}

And(~/^I should see navigation details has (.+?) link $/) { final String nodeName ->
	def parentExist = false
	client.body.links.findAll {
		if (it.rel == nodeName) {
			parentExist = true
		}
	}
	Assert.assertTrue(parentExist)
}

And(~/^I should not see navigation details has (.+?) link $/) { final String nodeName ->
	def parentExist = false
	client.body.links.findAll {
		if (it.rel == nodeName) {
			parentExist = true
		}
	}
	Assert.assertFalse(parentExist)
}

def navigationLookupByInvalidCategoryCode(final String categoryCode) {
	client.GET("/")
			.lookups().navigationlookupform()
			.navigationlookupaction([code: categoryCode])
			.stopIfFailure()
}

When(~/^I look up navigation item that (?:is invalid|belongs to another scope) with category code (.*)$/) {String categoryCode ->
	navigationLookupByInvalidCategoryCode(categoryCode)
}