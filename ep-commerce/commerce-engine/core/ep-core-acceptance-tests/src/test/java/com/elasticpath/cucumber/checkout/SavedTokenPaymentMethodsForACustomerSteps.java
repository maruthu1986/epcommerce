/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cucumber.checkout;

import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import javax.inject.Inject;
import javax.inject.Named;

import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.cucumber.ScenarioContextValueHolder;
import com.elasticpath.domain.builder.checkout.CheckoutTestCartBuilder;
import com.elasticpath.domain.builder.customer.CustomerBuilder;
import com.elasticpath.domain.builder.payment.gateway.PaymentGatewayBuilder;
import com.elasticpath.domain.builder.shopper.ShoppingContext;
import com.elasticpath.domain.builder.shopper.ShoppingContextBuilder;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerPaymentMethods;
import com.elasticpath.domain.customer.PaymentToken;
import com.elasticpath.domain.customer.impl.PaymentTokenImpl;
import com.elasticpath.domain.factory.OrderPaymentFactory;
import com.elasticpath.domain.misc.CheckoutResults;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.payment.PaymentGateway;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingCartTaxSnapshot;
import com.elasticpath.paymentgateways.cybersource.CyberSourceTestSubscriberFactory;
import com.elasticpath.paymentgateways.cybersource.provider.CybersourceConfigurationProvider;
import com.elasticpath.persister.Persister;
import com.elasticpath.plugin.payment.dto.PaymentMethod;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.service.shoppingcart.CheckoutService;
import com.elasticpath.service.shoppingcart.PricingSnapshotService;
import com.elasticpath.service.shoppingcart.TaxSnapshotService;
import com.elasticpath.test.persister.testscenarios.SimpleStoreScenario;
import com.elasticpath.test.util.Utils;

/**
 * Steps for the savedTokenPaymentMethodsForACustomer feature.
 */
public class SavedTokenPaymentMethodsForACustomerSteps {
	private static final String TOKEN_DISPLAY_VALUE = "**** **** **** 1234";
	private static final String TOKEN_DISPLAY_VALUE2 = "**** **** **** 5678";

	@Autowired
	private OrderPaymentFactory orderPaymentFactory;

	@Autowired
	private CheckoutService checkoutService;

	@Autowired
	private CyberSourceTestSubscriberFactory cyberSourceTestSubscriberFactory;

	@Autowired
	private CustomerBuilder customerBuilder;

	@Autowired
	private CheckoutTestCartBuilder checkoutTestCartBuilder;

	@Autowired
	private PaymentGatewayBuilder paymentGatewayBuilder;

	@Autowired
	private CustomerService customerService;

	@Inject
	@Named("simpleStoreScenarioHolder")
	private ScenarioContextValueHolder<SimpleStoreScenario> simpleStoreScenarioHolder;

	@Autowired
	private ShoppingContextBuilder shoppingContextBuilder;

	@Autowired
	private Persister<ShoppingContext> shoppingContextPersister;

	@Autowired
	private PricingSnapshotService pricingSnapshotService;

	@Autowired
	private TaxSnapshotService taxSnapshotService;

	private SimpleStoreScenario scenario;

	private CheckoutResults checkoutResults;

	private Customer customer;

	private OrderPayment orderPayment;

	private ShoppingCart shoppingCart;

	private ShoppingContext shoppingContext;

	/**
	 * Sets up the checkout test environment for a scenario.
	 */
	@Before("@checkout")
	public void setUpCheckoutEnvironment() {
		
		scenario = simpleStoreScenarioHolder.get();
		checkoutTestCartBuilder.withScenario(scenario);
	}

	/**
	 * Create a customer with a default payment token.
	 */
	@Given("^a customer with payment tokens and a default selected$")
	public void createACustomerWithPaymentTokensAndDefaultSelected() {
		final String[] tokenDisplayValues = new String[]{TOKEN_DISPLAY_VALUE, TOKEN_DISPLAY_VALUE2};
		ArrayList<PaymentToken> paymentTokens = new ArrayList<>(tokenDisplayValues.length);

		PaymentTokenImpl.TokenBuilder tokenBuilder = new PaymentTokenImpl.TokenBuilder();

		for (String tokenDisplayValue : tokenDisplayValues) {
			PaymentToken customerToken = tokenBuilder.withDisplayValue(tokenDisplayValue)
					.withGatewayGuid("abc")
					.withValue(cyberSourceTestSubscriberFactory.createBillableSubscriber())
					.build();

			paymentTokens.add(customerToken);
		}

		customerBuilder.withPaymentMethods(paymentTokens.toArray(new PaymentMethod[paymentTokens.size()]))
				.withDefaultToken(paymentTokens.get(0))
				.withStoreCode(scenario.getStore().getCode())
				.build();
		customer = customerService.add(customerBuilder.build());
		shoppingContext = shoppingContextBuilder.withCustomer(customer)
				.build();
		shoppingContextPersister.persist(shoppingContext);
	}

	/**
	 * Create an order with a default payment token.
	 */
	@Given("^an order is created with the default payment token$")
	public void createAnOrderWithACustomerDefaultPaymentToken() {
		CustomerPaymentMethods customerPaymentTokens = customer.getPaymentMethods();
		PaymentToken orderPaymentToken = (PaymentToken) customerPaymentTokens.getDefault();

		orderPayment = orderPaymentFactory.createTemplateTokenizedOrderPaymentWithToken(orderPaymentToken.getValue());
		shoppingCart = checkoutTestCartBuilder.withGateway(createCyberSourceExternalPaymentGateway())
				.withElectronicProduct()
				.withCustomerSession(shoppingContext.getCustomerSession())
				.build();
	}


	/**
	 * Submit the order created in a previous step.
	 */
	@When("^the order is submitted$")
	public void submitTheOrderCreated() {
		final ShoppingCartPricingSnapshot pricingSnapshot = pricingSnapshotService.getPricingSnapshotForCart(shoppingCart);
		final ShoppingCartTaxSnapshot taxSnapshot = taxSnapshotService.getTaxSnapshotForCart(shoppingCart, pricingSnapshot);
		checkoutResults = checkoutService.checkout(
				shoppingCart, taxSnapshot, shoppingContext.getCustomerSession(), orderPayment, true);
	}

	/**
	 * Create an order with a customer token from previous step.
	 */
	@Given("^an order is created with one of the customer's tokens$")
	public void createAnOrderWithACustomerToken() {
		CustomerPaymentMethods customerPaymentTokens = customer.getPaymentMethods();

		PaymentToken orderPaymentToken = (PaymentToken) customerPaymentTokens.all().iterator().next();

		orderPayment = orderPaymentFactory.createTemplateTokenizedOrderPaymentWithToken(orderPaymentToken.getValue());
		shoppingCart = checkoutTestCartBuilder.withGateway(createCyberSourceExternalPaymentGateway())
				.withElectronicProduct()
				.withCustomerSession(shoppingContext.getCustomerSession())
				.build();
	}

	/**
	 * Ensure the purchase created with the order payment token is successful.
	 */
	@Then("^a purchase should be created from the order with the same payment token$")
	public void ensurePurchaseCreatedWithOrderPaymentTokenWasSuccessful() {
		assertFalse("The purchase should have been created successfully", checkoutResults.isOrderFailed());
	}

	private PaymentGateway createCyberSourceExternalPaymentGateway() {
		return paymentGatewayBuilder.withName(Utils.uniqueCode("CybersourceTokenPaymentGateway"))
				.withType("paymentGatewayCyberSourceToken")
				.withProperties(CybersourceConfigurationProvider.getProvider().getConfigurationProperties())
				.build();
	}
}
