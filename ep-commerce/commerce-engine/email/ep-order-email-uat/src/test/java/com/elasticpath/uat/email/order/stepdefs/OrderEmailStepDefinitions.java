/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.uat.email.order.stepdefs;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

import static com.elasticpath.email.test.support.EmailContentAssert.assertEmailContentContainsOrderNumber;
import static com.elasticpath.uat.email.stepdefs.EmailStepDefinitions.getContents;
import static com.elasticpath.uat.email.stepdefs.EmailStepDefinitions.getEmailMessageBySubject;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import javax.mail.Message;

import cucumber.api.java.en.Then;
import org.apache.velocity.tools.generic.DateTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.misc.Geography;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.domain.shipping.ShipmentType;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.uat.ScenarioContextValueHolder;

/**
 * Step Definitions for order email functionality.
 */
public class OrderEmailStepDefinitions {

	@Autowired
	private Geography geography;

	@Autowired
	@Qualifier("orderHolder")
	private ScenarioContextValueHolder<Order> orderHolder;

	@Autowired
	@Qualifier("orderShipmentHolder")
	private ScenarioContextValueHolder<OrderShipment> orderShipmentHolder;

	@Autowired
	@Qualifier("emailMessagesHolder")
	private ScenarioContextValueHolder<Map<String, Message>> receivedMessagesHolder;

	@Autowired
	@Qualifier("productSkuLookup")
	private ProductSkuLookup productSkuLookup;

	@Then("^the(?: \"(.+)\")? email should contain the order number$")
	public void verifyEmailContainsOrderNumber(final String emailSubject) throws Exception {
		final String emailContents = getContents(getEmailMessageBySubject(emailSubject, receivedMessagesHolder.get()));
		assertEmailContentContainsOrderNumber("The order confirmation email contents should include the order number",
											  emailContents, orderHolder.get().getOrderNumber());
	}

	@Then("^the(?: \"(.+)\")? email should contain the order total$")
	public void verifyEmailContainsOrderTotal(final String emailSubject) throws Exception {
		final String emailContents = getContents(getEmailMessageBySubject(emailSubject, receivedMessagesHolder.get()));
		assertThat("The order confirmation email contents should include the order total",
				   emailContents, containsString(orderHolder.get().getTotal().toPlainString()));
	}

	@Then("^the(?: \"(.+)\")? email should contain the order date$")
	public void verifyEmailContainsOrderDate(final String emailSubject) throws Exception {
		final String emailContents = getContents(getEmailMessageBySubject(emailSubject, receivedMessagesHolder.get()));
		assertThat("The order confirmation email contents should include the order date",
				emailContents, containsString(new DateTool().format("long",
						orderHolder.get().getCreatedDate(), orderHolder.get().getLocale())));
	}

	@Then("^the(?: \"(.+)\")? email should contain the order items$")
	public void verifyEmailContainsOrderItems(final String emailSubject) throws Exception {
		final String emailContents = getContents(getEmailMessageBySubject(emailSubject, receivedMessagesHolder.get()));
		final Collection<? extends ShoppingItem> shoppingItems = orderHolder.get().getRootShoppingItems();
		verifyContentsContainsItemDisplayNames(emailContents, shoppingItems, orderHolder.get().getLocale(), productSkuLookup);
	}

	@Then("^the(?: \"(.+)\")? email should contain the order shipping address$")
	public void verifyEmailContainsOrderShippingAddress(final String emailSubject) throws Exception {
		final String emailContents = getContents(getEmailMessageBySubject(emailSubject, receivedMessagesHolder.get()));
		final Address shippingAddress = orderHolder.get().getShippingAddress();
		final String[] addressElements = {
				shippingAddress.getCity(),
				geography.getCountryDisplayName(shippingAddress.getCountry(), orderHolder.get().getLocale()),
				shippingAddress.getFaxNumber(),
				shippingAddress.getFirstName(),
				shippingAddress.getLastName(),
				shippingAddress.getOrganization(),
				shippingAddress.getStreet1(),
				shippingAddress.getStreet2(),
				shippingAddress.getPhoneNumber(),
				shippingAddress.getSubCountry(),
				shippingAddress.getZipOrPostalCode()
		};

		for (final String addressElement : addressElements) {
			if (addressElement != null) {
				assertThat("The order confirmation email contents should include each part of the shipping address",
						   emailContents, containsString(addressElement));
			}
		}
	}

	@Then("^the(?: \"(.+)\")? email should contain the shipment number$")
	public void verifyEmailContainsOrderShipmentNumber(final String emailSubject) throws Exception {
		final String emailContents = getContents(getEmailMessageBySubject(emailSubject, receivedMessagesHolder.get()));
		assertThat("The order shipment confirmation email contents should include the order shipment number",
				   emailContents, containsString(orderShipmentHolder.get().getShipmentNumber()));
	}

	@Then("^the(?: \"(.+)\")? email should contain the date the shipment shipped$")
	public void verifyEmailContainsOrderShipmentShipDate(final String emailSubject) throws Exception {
		final String emailContents = getContents(getEmailMessageBySubject(emailSubject, receivedMessagesHolder.get()));
		assertThat("The order shipment confirmation email contents should include the order shipment date",
				   emailContents, containsString(orderShipmentHolder.get().getShipmentDate().toString()));
	}

	@Then("^the(?: \"(.+)\")? email should contain the shipment method$")
	public void verifyEmailContainsOrderShipmentMethod(final String emailSubject) throws Exception {
		final OrderShipment orderShipment = orderShipmentHolder.get();

		if (orderShipment.getOrderShipmentType().equals(ShipmentType.PHYSICAL)) {
			final String emailContents = getContents(getEmailMessageBySubject(emailSubject, receivedMessagesHolder.get()));

			final PhysicalOrderShipment physicalOrderShipment = (PhysicalOrderShipment) orderShipment;

			assertThat("The physical order shipment confirmation email contents should include the order shipment carier code",
					   emailContents, containsString(physicalOrderShipment.getCarrierCode()));
			assertThat("The physical order shipment confirmation email contents should include the order shipment option name",
					   emailContents, containsString(physicalOrderShipment.getShippingOptionName()));
		}
	}

	@Then("^the(?: \"(.+)\")? email should contain the shipment tracking code \"([^\"]*)\"$")
	public void verifyEmailContainsOrderShipmentTrackingCode(final String emailSubject, final String trackingCode) throws Exception {
		final String emailContents = getContents(getEmailMessageBySubject(emailSubject, receivedMessagesHolder.get()));
		assertThat("The order shipment confirmation email contents should include the order shipment tracking code",
				   emailContents, containsString(trackingCode));
	}

	@Then("^the(?: \"(.+)\")? email should contain the shipment total$")
	public void verifyEmailContainsOrderShipmentTotal(final String emailSubject) throws Exception {
		final String emailContents = getContents(getEmailMessageBySubject(emailSubject, receivedMessagesHolder.get()));
		assertThat("The order shipment confirmation email contents should include the order shipment number",
				   emailContents, containsString(orderShipmentHolder.get().getTotal().toPlainString()));
	}

	/**
	 * Verifies that the given String contents contains the display names of all provided ShoppingItems for the given locale.
	 *
	 * @param emailContents the email contents
	 * @param shoppingItems the items
	 * @param locale the locale for which the display name should be determined
	 * @param productSkuLookup a product sku lookup
	 */
	public static void verifyContentsContainsItemDisplayNames(final String emailContents, final Iterable<? extends ShoppingItem> shoppingItems,
															  final Locale locale, final ProductSkuLookup productSkuLookup) {
		for (final ShoppingItem shoppingItem : shoppingItems) {
			ProductSku productSku = productSkuLookup.findByGuid(shoppingItem.getSkuGuid());
			final String displayName = productSku.getDisplayName(locale);
			assertThat("The email contents should include the name of each item purchased",
					   emailContents, containsString(displayName));
		}
	}

}
