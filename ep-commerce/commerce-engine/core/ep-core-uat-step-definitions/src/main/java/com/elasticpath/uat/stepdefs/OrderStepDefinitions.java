/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.uat.stepdefs;

import java.util.List;
import java.util.concurrent.TimeUnit;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import org.apache.camel.builder.NotifyBuilder;
import org.jvnet.mock_javamail.Mailbox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.elasticpath.domain.builder.OrderBuilder;
import com.elasticpath.domain.builder.checkout.CheckoutTestCartBuilder;
import com.elasticpath.domain.builder.customer.CustomerBuilder;
import com.elasticpath.domain.builder.shopper.ShoppingContext;
import com.elasticpath.domain.builder.shopper.ShoppingContextBuilder;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.event.EventOriginatorHelper;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderShipmentStatus;
import com.elasticpath.domain.shipping.ShipmentType;
import com.elasticpath.email.test.support.EmailSendingMockInterceptor;
import com.elasticpath.persister.ShoppingContextPersister;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.service.order.OrderService;
import com.elasticpath.test.persister.testscenarios.SimpleStoreScenario;
import com.elasticpath.uat.ScenarioContextValueHolder;

/**
 * Step Definitions for Order-based functionality.
 */
public class OrderStepDefinitions {

	private static final long MAX_SECONDS_TO_WAIT_FOR_EMAIL = 20;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private OrderBuilder orderBuilder;

	@Autowired
	private OrderService orderService;

	@Autowired
	@Qualifier("customerBuilderHolder")
	private ScenarioContextValueHolder<CustomerBuilder> customerBuilderHolder;

	@Autowired
	@Qualifier("storeScenarioHolder")
	private ScenarioContextValueHolder<SimpleStoreScenario> scenarioHolder;

	@Autowired
	@Qualifier("orderHolder")
	private ScenarioContextValueHolder<Order> orderHolder;

	@Autowired
	@Qualifier("checkoutTestCartBuilderHolder")
	private ScenarioContextValueHolder<CheckoutTestCartBuilder> checkoutTestCartBuilderHolder;

	@Autowired
	private EventOriginatorHelper eventOriginatorHelper;

	@Autowired
	private EmailSendingMockInterceptor emailSendingMockInterceptor;

	@Autowired
	private ScenarioContextValueHolder<Runnable> emailSendingCommandHolder;

	@Autowired
	private ShoppingContextBuilder shoppingContextBuilder;

	@Autowired
	private ShoppingContextPersister shoppingContextPersister;

	@When("^I successfully purchase my shopping cart contents$")
	public void createOrder() throws Exception {
		// Defer execution until we are ready to check for the email
		emailSendingCommandHolder.set(() -> {
			final Customer customer = customerBuilderHolder.get().build();
			customerService.add(customer);

		final ShoppingContext shoppingContext = shoppingContextBuilder.withCustomer(customer)
				.build();
		shoppingContextPersister.persist(shoppingContext);

		final CheckoutTestCartBuilder checkoutTestCartBuilder = checkoutTestCartBuilderHolder.get()
				.withScenario(scenarioHolder.get())
				.withCustomerSession(shoppingContext.getCustomerSession())
				.withTestDoubleGateway();

			orderHolder.set(orderBuilder.withCheckoutTestCartBuilder(checkoutTestCartBuilder)
									.withTokenizedTemplateOrderPayment()
									.withShoppingContext(shoppingContext)
									.checkout());
		});
	}

	@When("^the CSR resends the order confirmation email$")
	public void resendOrderConfirmationEmail() {
		// We need to ensure the previous command is executed for a resend
		emailSendingCommandHolder.get().run();
		// Defer execution until we are ready to check for the email
		emailSendingCommandHolder.set(() -> {
			final String orderNumber = orderHolder.get().getOrderNumber();
			orderService.resendOrderConfirmationEvent(orderNumber);
		});
	}
	
	@Given("^(?:I have|a customer has) previously made a purchase$")
	public void createInProgressOrder() {
		final Customer customer = customerBuilderHolder.get().build();
		customerService.add(customer);

		final ShoppingContext shoppingContext = shoppingContextBuilder.withCustomer(customer)
				.build();
		shoppingContextPersister.persist(shoppingContext);

		final CheckoutTestCartBuilder checkoutTestCartBuilder = checkoutTestCartBuilderHolder.get()
				.withScenario(scenarioHolder.get())
				.withCustomerSession(shoppingContext.getCustomerSession())
				.withTestDoubleGateway();

		final Order order = orderBuilder.withCheckoutTestCartBuilder(checkoutTestCartBuilder)
				.withTokenizedTemplateOrderPayment()
				.withNonZeroPhysicalShipment()
				.withShoppingContext(shoppingContext)
				.build();

		orderHolder.set(orderService.update(order));
	}

	@And("^the purchase has been completed and delivered$")
	public void completeAllOrderShipments() throws Throwable {
		Order order = orderHolder.get();

		final int numberOfPhysicalShipments = getPhysicalShipmentCount(order);
		final NotifyBuilder notifyBuilder = emailSendingMockInterceptor.createNotifyBuilderForEmailSendingMockInterceptor()
				.whenDone(numberOfPhysicalShipments)
				.create();

		final List<OrderShipment> shipments = order.getAllShipments();
		for (final OrderShipment shipment : shipments) {
			if (!shipment.getShipmentStatus().equals(OrderShipmentStatus.SHIPPED)) {
				order = orderService.completeShipment(shipment.getShipmentNumber(),
													  null,
													  false,
													  null,
													  false,
													  eventOriginatorHelper.getSystemOriginator());
			}
		}

		orderHolder.set(order);

		// wait for the order shipment confirmation email(s) to be sent, then clear them
		notifyBuilder.matches(MAX_SECONDS_TO_WAIT_FOR_EMAIL, TimeUnit.SECONDS);

		// Clear the shipment confirmation emails
		Mailbox.clearAll();
	}

	private int getPhysicalShipmentCount(final Order order) {
		int count = 0;

		for (final OrderShipment shipment : order.getAllShipments()) {
			if (shipment.getOrderShipmentType().equals(ShipmentType.PHYSICAL)) {
				count++;
			}
		}

		return count;
	}

}
