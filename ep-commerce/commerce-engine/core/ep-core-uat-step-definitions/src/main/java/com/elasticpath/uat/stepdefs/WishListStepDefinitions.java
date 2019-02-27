/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.uat.stepdefs;

import java.util.Locale;

import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.builder.WishListBuilder;
import com.elasticpath.domain.builder.customer.CustomerBuilder;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.WishList;
import com.elasticpath.domain.shoppingcart.WishListMessage;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.service.shopper.ShopperService;
import com.elasticpath.service.shoppingcart.WishListService;
import com.elasticpath.test.persister.TestDataPersisterFactory;
import com.elasticpath.test.persister.testscenarios.SimpleStoreScenario;
import com.elasticpath.uat.ScenarioContextValueHolder;

/**
 * Step Definitions for Wish List-based functionality.
 */
public class WishListStepDefinitions {

	@Autowired
	private ScenarioContextValueHolder<WishListBuilder> wishListBuilderHolder;

	@Autowired
	private ScenarioContextValueHolder<WishListMessage> wishListMessageHolder;

	@Autowired
	private WishListBuilder wishListBuilder;

	@Autowired
	private WishListService wishListService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private ShopperService shopperService;

	@Autowired
	private ScenarioContextValueHolder<SimpleStoreScenario> storeScenarioHolder;

	@Autowired
	private ScenarioContextValueHolder<CustomerBuilder> customerBuilderHolder;

	@Autowired
	private TestDataPersisterFactory persisterFactory;

	@Autowired
	private BeanFactory beanFactory;

	@Autowired
	private ScenarioContextValueHolder<Runnable> emailSendingCommandHolder;

	@Before
	public void setUp() {
		wishListBuilderHolder.set(wishListBuilder.withScenario(storeScenarioHolder.get()));
	}

	@Given("^(?:I have|the customer has) items in their wish list$")
	public void addItemToWishList() throws Exception {
		wishListBuilderHolder.set(wishListBuilderHolder.get().withPhysicalProduct());
	}

	@When("^the customer sends their wish list to the email address (.+)$")
	public void sendWishList(final String emailRecipient) throws Throwable {
		// Defer execution until we are ready to check for the email
		emailSendingCommandHolder.set(() -> {

			final Shopper shopper = createShopper();
			final Customer customer = shopper.getCustomer();

			wishListBuilderHolder.set(wishListBuilder.withShopper(shopper));

			final WishList wishList = wishListBuilderHolder.get().build();
			wishListService.save(wishList);

			final WishListMessage wishListMessage = beanFactory.getBean(ContextIdNames.WISH_LIST_MESSAGE);
			wishListMessage.setSenderName(customer.getFullName());
			wishListMessage.setMessage("Token Wish List message");
			wishListMessage.setRecipientEmails(emailRecipient);

			final String storeCode = storeScenarioHolder.get().getStore().getCode();
			final Locale locale = customer.getPreferredLocale();

			wishListService.shareWishList(wishListMessage, wishList, storeCode, locale);

			wishListMessageHolder.set(wishListMessage);
		});
	}

	private Shopper createShopper() {
		final Customer customer = customerBuilderHolder.get().build();
		customerService.add(customer);

		final CustomerSession customerSession = persisterFactory.getStoreTestPersister().persistCustomerSessionWithAssociatedEntities(customer);
		return customerSession.getShopper();
	}

}
