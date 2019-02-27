package com.elasticpath.cortex.dce.zoom

import static com.elasticpath.cortex.dce.ClasspathFluentRelosClientFactory.client
import static org.assertj.core.api.Assertions.assertThat

import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.PathNotFoundException
import cucumber.api.PendingException
import cucumber.api.groovy.EN
import cucumber.api.groovy.Hooks
import org.assertj.core.data.Percentage

import com.elasticpath.cortex.dce.DatabaseAnalyzerClient

this.metaClass.mixin(Hooks)
this.metaClass.mixin(EN)
/**
 * Shared steps.
 */

def ZOOM_ITEM
def SAVED_RESPONSE

When(~'^I start measuring db queries') { ->
	DatabaseAnalyzerClient.instance.start()
}

When(~'^I stop measuring db queries') { ->
	DatabaseAnalyzerClient.instance.stop()
}

Then(~/^total number of DB calls should be close to (\d+) calls with allowed deviation of (\d+.\d+) percent$/) { int dbcalls, double percentage ->
	int totalDbCalls = DatabaseAnalyzerClient.instance.getOverallDBCalls()

	assertThat(totalDbCalls)
			.isCloseTo(dbcalls, Percentage.withPercentage(percentage))
}

When(~'^I zoom to cart with a query (.+)') { def zoom ->
	client.GET("/carts/mobee/default?zoom=$zoom")
}

And(~/^number of DB calls for table (.+) should be close to (\d+) calls with allowed deviation of (\d+.\d+) percent$/) { String table, int dbcalls, double percentage ->
	int totalDbCalls = DatabaseAnalyzerClient.instance.getDBCallsByTableName(table)

	assertThat(totalDbCalls)
			.isCloseTo(dbcalls, Percentage.withPercentage(percentage))
}

Then(~'I inspect the zoom object (.+)') { def jsonpath ->
	try {
		ZOOM_ITEM = JsonPath.read(client.body, "\$." + jsonpath)
	}
	catch (PathNotFoundException e) {
		assertThat(true)
				.as("$jsonpath not found in $client.body")
				.isFalse()
	}
}

Then(~'the response does not contain path (.+)') { def path ->
	try {
		JsonPath.read(client.body, "\$." + path)
		assertThat(true)
				.as("$client.body should not contain path $path")
				.isfalse()
	}
	catch (PathNotFoundException e) {
//        pass
	}
}

Then(~'the response contains path (.+)') { def path ->
	try {
		JsonPath.read(client.body, "\$." + path)
		//        pass
		assertThat(true).isTrue()
	}
	catch (PathNotFoundException e) {
		assertThat(true)
				.as("$path not found in $client.body")
				.isFalse()
	}
}

Then(~'the zoom object does not contain path (.+)') { def path ->
	try {
		JsonPath.read(ZOOM_ITEM, "\$." + path)
		assertThat(true)
				.as("$ZOOM_ITEM should not contain path $path")
				.isFalse()
	}
	catch (PathNotFoundException e) {
//        pass
	}
}

Then(~'the zoom object contains path (.+)') { def path ->
	try {
		JsonPath.read(ZOOM_ITEM, "\$." + path)
		//        pass
		assertThat(true).isTrue()
	}
	catch (PathNotFoundException e) {
		assertThat(true)
				.as("$path not found in $ZOOM_ITEM")
				.isFalse()
	}
}

Then(~'the zoom object does not contain string (.+)') { def regex ->
	assertThat(ZOOM_ITEM.toString())
			.as("Zoom object is not as expected")
			.doesNotContain(regex)
}

Then(~'the zoom object contains string (.+)') { def regex ->
	assertThat(ZOOM_ITEM.toString())
			.as("Zoom object is not as expected")
			.contains(regex)
}

Then(~'save the zoom response') { ->
	SAVED_RESPONSE = client.body
}

Then(~'save the zoomed response') { ->
	SAVED_RESPONSE = ZOOM_ITEM
}

Then(~'In the given format (.*) the response is identical to the saved response') { String format ->

	def body = client.body

	if (format.contains("zoom.noself")) {
		body.remove('self')
		SAVED_RESPONSE.remove('self')
	}

	assertThat(body)
			.as("The response is not as expected")
			.isEqualTo(SAVED_RESPONSE)
}

Then(~'the response is identical to the saved response') { ->

	assertThat(client.body)
			.as("The response is not as expected")
			.isEqualTo(SAVED_RESPONSE)
}

Then(~'the json path (.+) equals (.+)') { def jsonpath, def expected ->
	try {
		assertThat(JsonPath.read(client.body, "\$." + jsonpath))
				.as("The result is not as expected")
				.isEqualTo(expected)
	}
	catch (PathNotFoundException e) {
		assertThat(true)
				.as("$jsonpath not found in $client.body")
				.isFalse()
	}
}

Then(~'the zoomed object contains path (.+) equal to (.+)') { def jsonpath, def expected ->
	try {
		assertThat(JsonPath.read(ZOOM_ITEM, "\$." + jsonpath))
				.as("The zoomed object is not as expected")
				.isEqualTo(expected)
	}
	catch (PathNotFoundException e) {
		assertThat(true)
				.as("$jsonpath not found in $client.body")
				.isFalse()
	}
}

Then(~'I go to the zoom object (.+) with field (.+) and value (.+)') { String path, String field, String value ->
	try {
		def zoomObject = JsonPath.read(client.body, "\$." + path)
		for (def zoomRepresentation : zoomObject) {
			if (zoomObject[field] == value) {
				zoomedResult = zoomRepresentation
			}
		}
		ZOOM_ITEM = zoomedResult
	}
	catch (PathNotFoundException e) {
		assertThat(true)
				.as("$path not found in $client.body")
				.isFalse()
	}
}

Then(~'I zoom the current url (.+)') { def zoom ->
	client.GET(client.body.self.uri + "?zoom=$zoom")
	client.stopIfFailure()
}