/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.uat.email.order.stepdefs;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import static com.elasticpath.uat.email.stepdefs.EmailStepDefinitions.getContents;
import static com.elasticpath.uat.email.stepdefs.EmailStepDefinitions.getEmailMessageBySubject;

import java.util.Map;
import javax.mail.Message;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.elasticpath.domain.catalog.GiftCertificate;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.service.catalog.GiftCertificateService;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.shoppingcart.PricingSnapshotService;
import com.elasticpath.uat.ScenarioContextValueHolder;

/**
 * Step Definitions for gift certificate email functionality.
 */
public class GiftCertificateEmailStepDefinitions {

	@Autowired
	private ScenarioContextValueHolder<Map<String, Message>> emailMessagesHolder;

	@Autowired
	private ScenarioContextValueHolder<Order> orderHolder;
	
	@Autowired
	private GiftCertificateService giftCertificateService;

	@Autowired
	@Qualifier("productSkuLookup")
	private ProductSkuLookup productSkuLookup;

	@Autowired
	private PricingSnapshotService pricingSnapshotService;

	@Autowired
	private ScenarioContextValueHolder<Runnable> emailSendingCommandHolder;

	@Then("^the(?: \"(.+)\")? email should contain the gift certificate code$")
	public void verifyEmailContainsGiftCertificateCode(final String emailSubject) throws Exception {
		final OrderSku giftCertificateOrderSku = findGiftCertificateOrderSku();

		assertNthEmailContentsContainsValue(emailSubject, "code", giftCertificateOrderSku.getFieldValue(GiftCertificate.KEY_CODE));
	}

	@Then("^the(?: \"(.+)\")? email should contain the gift certificate amount$")
	public void verifyEmailContainsGiftCertificateAmount(final String emailSubject) throws Exception {
		final OrderSku giftCertificateOrderSku = findGiftCertificateOrderSku();

		final ShoppingItemPricingSnapshot pricingSnapshot = pricingSnapshotService.getPricingSnapshotForOrderSku(giftCertificateOrderSku);
		assertNthEmailContentsContainsValue(emailSubject, "amount", pricingSnapshot.getTotal().getAmount().toPlainString());
	}

	@Then("^the(?: \"(.+)\")? email should contain the gift certificate sender name$")
	public void verifyEmailContainsGiftCertificateSenderName(final String emailSubject) throws Exception {
		final OrderSku giftCertificateOrderSku = findGiftCertificateOrderSku();

		assertNthEmailContentsContainsValue(emailSubject, "sender name", giftCertificateOrderSku.getFieldValue(GiftCertificate.KEY_SENDER_NAME));
	}

	@Then("^the(?: \"(.+)\")? email should contain the gift certificate recipient name$")
	public void verifyEmailContainsGiftCertificateRecipientName(final String emailSubject) throws Exception {
		final OrderSku giftCertificateOrderSku = findGiftCertificateOrderSku();

		assertNthEmailContentsContainsValue(emailSubject, "recipient name", giftCertificateOrderSku.getFieldValue(GiftCertificate.KEY_RECIPIENT_NAME));
	}

	@When("^the gift certificate email is resent to (.+)$")
	public void resendGiftCertificateEmail(final String emailAddress) {
		// For a resend we need to make sure the original command is executed
		emailSendingCommandHolder.get().run();
		// Defer execution until we are ready to check for the email
		emailSendingCommandHolder.set(() -> {
			final OrderSku orderSku = findGiftCertificateOrderSku();
			giftCertificateService.resendGiftCertificate(emailAddress, orderHolder.get().getGuid(), orderSku.getGuid());
		});
	}
	
	private void assertNthEmailContentsContainsValue(final String emailSubject, final String fieldName, final String valueToLocate)
			throws Exception {
		final String emailContents = getContents(getEmailMessageBySubject(emailSubject, emailMessagesHolder.get()));

		assertThat("The gift certificate email contents should include the gift certificate " + fieldName,
				   emailContents, containsString(valueToLocate));
	}

	private OrderSku findGiftCertificateOrderSku() {
		for (final OrderShipment orderShipment : orderHolder.get().getAllShipments()) {
			for (final OrderSku orderSku : orderShipment.getShipmentOrderSkus()) {
				ProductSku productSku = productSkuLookup.findByGuid(orderSku.getSkuGuid());
				if (productSku.getProduct().getProductType().isGiftCertificate()) {
					return orderSku;
				}
			}
		}

		fail("Could not locate a Gift Certificate SKU within order");

		return null; // the compiler doesn't know we'll never get here.
	}

}
