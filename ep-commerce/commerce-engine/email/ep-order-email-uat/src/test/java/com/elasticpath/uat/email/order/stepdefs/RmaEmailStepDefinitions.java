/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.uat.email.order.stepdefs;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

import static com.elasticpath.uat.email.order.stepdefs.OrderEmailStepDefinitions.verifyContentsContainsItemDisplayNames;
import static com.elasticpath.uat.email.stepdefs.EmailStepDefinitions.getContents;
import static com.elasticpath.uat.email.stepdefs.EmailStepDefinitions.getEmailMessageBySubject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import javax.mail.Message;

import cucumber.api.java.en.Then;
import org.apache.velocity.tools.generic.DateTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.domain.order.OrderReturnSku;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.uat.ScenarioContextValueHolder;

/**
 * Step Definitions for RMA email functionality.
 */
public class RmaEmailStepDefinitions {

	// This address is defined in the RMA.properties file, not in any domain class that can be accessed programmatically via the OrderReturn.
	private static final String[] MAIL_BACK_ADDRESS_ELEMENTS = {
			"Attn: Online Returns/Exchanges",
			"1045 Howe St., 8th Floor",
			"Vancouver, BC V6Z 2A9",
			"Canada"
	};

	@Autowired
	private ScenarioContextValueHolder<OrderReturn> orderReturnHolder;

	@Autowired
	private ScenarioContextValueHolder<Map<String, Message>> emailMessagesHolder;

	@Autowired
	@Qualifier("productSkuLookup")
	private ProductSkuLookup productSkuLookup;

	@Then("^the(?: \"(.+)\")? email should contain the return date$")
	public void verifyEmailContainsReturnDate(final String emailSubject) throws Exception {
		final String emailContents = getContents(getEmailMessageBySubject(emailSubject, emailMessagesHolder.get()));
		assertThat("The email contents should include the return date",
				   emailContents, containsString(new DateTool().format("E, MMMM d yyyy",
																	   orderReturnHolder.get().getCreatedDate(),
																	   orderReturnHolder.get().getOrder().getLocale())));
	}

	@Then("^the(?: \"(.+)\")? email should contain the RMA number$")
	public void verifyEmailContainsRmaNumber(final String emailSubject) throws Exception {
		final String emailContents = getContents(getEmailMessageBySubject(emailSubject, emailMessagesHolder.get()));
		assertThat("The email contents should include the RMA number",
				   emailContents, containsString(orderReturnHolder.get().getRmaCode()));
	}

	@Then("^the(?: \"(.+)\")? email should contain the name\\(s\\) of the order item\\(s\\) to be returned$")
	public void verifyEmailContainsOrderItemNames(final String emailSubject) throws Exception {
		final String emailContents = getContents(getEmailMessageBySubject(emailSubject, emailMessagesHolder.get()));

		final Set<OrderReturnSku> orderReturnSkus = orderReturnHolder.get().getOrderReturnSkus();
		final Collection<OrderSku> orderSkus = new ArrayList<>(orderReturnSkus.size());
		for (final OrderReturnSku orderReturnSku : orderReturnSkus) {
			orderSkus.add(orderReturnSku.getOrderSku());
		}

		verifyContentsContainsItemDisplayNames(emailContents, orderSkus, orderReturnHolder.get().getOrder().getLocale(), productSkuLookup);
	}

	@Then("^the(?: \"(.+)\")? email should contain the SKU code\\(s\\) of the order item\\(s\\) to be returned$")
	public void verifyEmailContainsOrderItemSkuCodes(final String emailSubject) throws Exception {
		final String emailContents = getContents(getEmailMessageBySubject(emailSubject, emailMessagesHolder.get()));

		final Set<OrderReturnSku> orderReturnSkus = orderReturnHolder.get().getOrderReturnSkus();

		for (final OrderReturnSku orderReturnSku : orderReturnSkus) {
			final String skuCode = orderReturnSku.getOrderSku().getSkuCode();
			assertThat("The email contents should include the SKU code of each item purchased",
					   emailContents, containsString(skuCode));
		}
	}

	@Then("^the(?: \"(.+)\")? email should contain the quantities of each item\\(s\\) to be returned$")
	public void verifyEmailContainsOrderItemQuantities(final String emailSubject) throws Exception {
		final String emailContents = getContents(getEmailMessageBySubject(emailSubject, emailMessagesHolder.get()));

		final Set<OrderReturnSku> orderReturnSkus = orderReturnHolder.get().getOrderReturnSkus();

		for (final OrderReturnSku orderReturnSku : orderReturnSkus) {
			final int quantity = orderReturnSku.getQuantity();
			assertThat("The email contents should include the quantity of each item purchased",
					   emailContents, containsString(String.valueOf(quantity)));
		}
	}

	@Then("^the(?: \"(.+)\")? email should contain the mail-back address$")
	public void verifyEmailContainsMailBackAddress(final String emailSubject) throws Exception {
		final String emailContents = getContents(getEmailMessageBySubject(emailSubject, emailMessagesHolder.get()));

		for (final String addressElement : MAIL_BACK_ADDRESS_ELEMENTS) {
			if (addressElement != null) {
				assertThat("The RMA email contents should include each part of the mail-back address",
						   emailContents, containsString(addressElement));
			}
		}
	}

}