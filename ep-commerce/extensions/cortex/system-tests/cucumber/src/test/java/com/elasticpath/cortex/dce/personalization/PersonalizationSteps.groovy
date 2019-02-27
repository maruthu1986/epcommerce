package com.elasticpath.cortex.dce.personalization

import cucumber.api.groovy.EN
import cucumber.api.groovy.Hooks
import org.apache.commons.lang.StringUtils

import static com.elasticpath.cortex.dce.ClasspathFluentRelosClientFactory.client

import static org.assertj.core.api.Assertions.assertThat

import com.elasticpath.cortex.dce.CommonMethods

this.metaClass.mixin(Hooks)
this.metaClass.mixin(EN)

Given(~'^(.+) has an original purchase price equal to (.+)$') {
	String movieName, String expectedPrice ->
		searchPriceByMovieName(movieName);

		verifyProductPurchasePrice(expectedPrice);
}

When(~'^I append to the overwritten personalization header the key (.+) and value (.+)$') {
	String personalisationKey, String personalisationValue ->
		addPersonalisationHeader(personalisationKey, personalisationValue);
}

And(~'^I request the purchase price for item (.+)$') {
	String movieName ->
		searchPriceByMovieName(movieName);
}

And(~'^I request the purchase price in scope (.+) for item (.+)$') {
	String scope, String movieName ->
		searchPriceByMovieName(movieName);
}

Then(~'^I get the purchase price equal to (.+)$') {
	String price ->
		verifyProductPurchasePrice(price);
}

private void searchPriceByMovieName(final String movieName) {
	CommonMethods.searchAndOpenItemWithKeyword(movieName)

	client.price()
			.stopIfFailure();
}

private void verifyProductPurchasePrice(final String price) {
	assertThat(client.body."purchase-price"[0]["display"])
			.as("The purchase price is not as expected")
			.isEqualTo(price)
}

private void addPersonalisationHeader(final String personalisationKey, final String personalisationValue) {
	String headerKey = "x-ep-user-traits";
	String headerValue = personalisationKey + "=" + personalisationValue;

	String existingHeaderValue = client.headers.getAt(headerKey);
	if (StringUtils.isNotBlank(existingHeaderValue)) {
		headerValue = existingHeaderValue + ", " + headerValue;
	}

	client.headers.putAt(headerKey, headerValue)
}