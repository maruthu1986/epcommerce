/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.uat.email.customer.stepdefs;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

import static com.elasticpath.uat.email.stepdefs.EmailStepDefinitions.getContents;
import static com.elasticpath.uat.email.stepdefs.EmailStepDefinitions.getEmailMessageBySubject;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.mail.Message;

import cucumber.api.java.en.And;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.elasticpath.domain.builder.WishListBuilder;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.WishList;
import com.elasticpath.domain.shoppingcart.WishListMessage;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.uat.ScenarioContextValueHolder;

/**
 * Step Definitions for Wish List email-based functionality.
 */
public class WishListEmailStepDefinitions {

	@Autowired
	private ScenarioContextValueHolder<WishListBuilder> wishListBuilderHolder;

	@Autowired
	private ScenarioContextValueHolder<WishListMessage> wishListMessageHolder;

	@Autowired
	@Qualifier("emailMessagesHolder")
	private ScenarioContextValueHolder<Map<String, Message>> receivedMessagesHolder;

	@Autowired
	@Qualifier("productSkuLookup")
	private ProductSkuLookup productSkuLookup;

	@And("^the(?: \"(.+)\")? email should contain the wish list sender name$")
	public void verifyEmailContainsWishListSenderName(final String emailSubject) throws Exception {
		final String emailContents = getContents(getEmailMessageBySubject(emailSubject, receivedMessagesHolder.get()));
		assertThat("The wish list email contents should include the sender name",
				   emailContents, containsString(wishListMessageHolder.get().getSenderName()));
	}

	@And("^the(?: \"(.+)\")? email should contain the wish list items$")
	public void verifyEmailContainsWishListItems(final String emailSubject) throws Exception {
		final String emailContents = getContents(getEmailMessageBySubject(emailSubject, receivedMessagesHolder.get()));
		final WishList wishList = wishListBuilderHolder.get().build();
		final List<ShoppingItem> wishListItems = wishList.getAllItems();
		final Locale locale = wishList.getShopper().getLocale();

		for (final ShoppingItem shoppingItem : wishListItems) {
			ProductSku productSku = productSkuLookup.findByGuid(shoppingItem.getSkuGuid());
			final String displayName = productSku.getDisplayName(locale);
			assertThat("The email contents should include the name of each item purchased",
					   emailContents, containsString(displayName));
		}
	}

}
