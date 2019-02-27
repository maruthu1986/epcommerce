/*
 * Copyright © 2013 Elastic Path Software Inc. All rights reserved.
 */

package com.elasticpath.test.integration.refund;

import static org.junit.Assert.assertThat;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.domain.builder.OrderBuilder;
import com.elasticpath.domain.builder.checkout.CheckoutTestCartBuilder;
import com.elasticpath.domain.builder.customer.CustomerBuilder;
import com.elasticpath.domain.builder.payment.gateway.PaymentGatewayBuilder;
import com.elasticpath.domain.builder.shopper.ShoppingContext;
import com.elasticpath.domain.builder.shopper.ShoppingContextBuilder;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.event.EventOriginator;
import com.elasticpath.domain.event.EventOriginatorHelper;
import com.elasticpath.domain.factory.OrderPaymentFactory;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderStatus;
import com.elasticpath.domain.payment.PaymentGateway;
import com.elasticpath.paymentgateways.cybersource.CyberSourceTestSubscriberFactory;
import com.elasticpath.paymentgateways.cybersource.provider.CybersourceConfigurationProvider;
import com.elasticpath.persister.Persister;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.service.order.OrderService;
import com.elasticpath.test.integration.BasicSpringContextTest;
import com.elasticpath.test.integration.DirtiesDatabase;
import com.elasticpath.test.integration.checkout.OrderPaymentMatcherFactory;
import com.elasticpath.test.integration.checkout.OrderValidator;
import com.elasticpath.test.persister.testscenarios.SimpleStoreScenario;
import com.elasticpath.test.util.Utils;

/**
 * Tests refunds of orders made with tokenized payments.
 */
@ContextConfiguration(inheritLocations = true)
public class TokenRefundTest extends BasicSpringContextTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Autowired
	private OrderService orderService;

	@Autowired
	private OrderPaymentFactory orderPaymentFactory;

	@Autowired
	private CyberSourceTestSubscriberFactory cyberSourceTestSubscriberFactory;

	@Autowired
	private CustomerBuilder customerBuilder;

	@Autowired
	private PaymentGatewayBuilder paymentGatewayBuilder;

	@Autowired
	private EventOriginatorHelper eventOriginatorHelper;

	@Autowired
	private OrderBuilder orderBuilder;

	@Autowired
	private CheckoutTestCartBuilder checkoutTestCartBuilder;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private ShoppingContextBuilder shoppingContextBuilder;

	@Autowired
	private Persister<ShoppingContext> shoppingContextPersister;

	/**
	 * Set up {@link CheckoutTestCartBuilder} and {@link OrderBuilder}.
	 */
	@Before
	public void setUp() {
		SimpleStoreScenario scenario = getTac().useScenario(SimpleStoreScenario.class);
		Customer testCustomer = customerBuilder.withStoreCode(scenario.getStore().getCode())
				.build();
		customerService.add(testCustomer);

		final ShoppingContext shoppingContext = shoppingContextBuilder
				.withCustomer(testCustomer)
				.build();

		shoppingContextPersister.persist(shoppingContext);

		final CustomerSession customerSession = shoppingContext.getCustomerSession();

		checkoutTestCartBuilder.withScenario(scenario)
				.withCustomerSession(customerSession);

		final OrderPayment templateOrderPayment = orderPaymentFactory
				.createTemplateTokenizedOrderPaymentWithToken(cyberSourceTestSubscriberFactory.createBillableSubscriber());
		orderBuilder.withCheckoutTestCartBuilder(checkoutTestCartBuilder)
				.withShoppingContext(shoppingContext)
				.withTemplateOrderPayment(templateOrderPayment)
				.withGateway(createCyberSourceExternalPaymentGateway());
	}

	/**
	 * Successful full refund for electronic shipment with token.
	 */
	@DirtiesDatabase
	@Test
	public void successfulFullRefundForElectronicShipmentWithToken() {
		Order order = orderBuilder.withNonZeroElectronicShipment().checkout();
		Order orderWithRefund = orderService.refundOrderPayment(order.getUidPk(), 
																order.getAllShipments().get(0).getShipmentNumber(), 
																null, 
																getAmountPaidForOrder(order), 
																createEventOriginator());

		OrderValidator orderValidator = OrderValidator.builder()
				.withStatus(OrderStatus.COMPLETED)
				.withPaymentMatchers(OrderPaymentMatcherFactory.createSuccessfulTokenAuthorization(),
						OrderPaymentMatcherFactory.createSuccessfulTokenCapture(),
						OrderPaymentMatcherFactory.createSuccessfulTokenCredit())
				.build();
		assertThat("The order validation should succeed.", orderWithRefund, orderValidator);
	}

	/**
	 * Successful full refund for physical shipment with token.
	 */
	@DirtiesDatabase
	@Test
	public void successfulFullRefundForPhysicalShipmentWithToken() {
		Order completedOrder = orderBuilder.withNonZeroPhysicalShipment()
				.withAllShipmentsCompleted()
				.checkout();

		Order orderWithRefund = orderService.refundOrderPayment(completedOrder.getUidPk(), 
																completedOrder.getAllShipments().get(0).getShipmentNumber(), 
																null, 
																getAmountPaidForOrder(completedOrder), 
																createEventOriginator());
		OrderValidator orderValidator = OrderValidator.builder()
				.withStatus(OrderStatus.COMPLETED)
				.withPaymentMatchers(OrderPaymentMatcherFactory.createSuccessfulTokenAuthorization(),
						OrderPaymentMatcherFactory.createSuccessfulTokenCapture(),
						OrderPaymentMatcherFactory.createSuccessfulTokenCredit())
				.build();
		assertThat("The order validation should succeed.", orderWithRefund, orderValidator);
	}

	/**
	 * Refunds on free electronic shipments throw exception.
	 */
	@DirtiesDatabase
	@Test
	public void refundsOnFreeElectronicShipmentsThrowException() {
		Order completedOrder = orderBuilder.withFreeElectronicShipment().checkout();

		exception.expect(EpServiceException.class);
		exception.expectMessage("order does not have a captured payment");
		orderService.refundOrderPayment(completedOrder.getUidPk(), 
										completedOrder.getAllShipments().get(0).getShipmentNumber(), 
										null, 
										getAmountPaidForOrder(completedOrder), 
										createEventOriginator());
	}

	/**
	 * Refund on incomplete physical shipment with token should throw exception.
	 */
	@DirtiesDatabase
	@Test
	public void refundOnIncompletePhysicalShipmentWithTokenShouldThrowException() {
		Order order = orderBuilder.withNonZeroPhysicalShipment().checkout();

		exception.expect(EpServiceException.class);
		exception.expectMessage("Order is not applicable for a refund.");
		orderService.refundOrderPayment(order.getUidPk(), 
										order.getAllShipments().get(0).getShipmentNumber(),
										null, 
										getAmountPaidForOrder(order), 
										createEventOriginator());
	}

	/**
	 * Refund exceeding original token capture amount should throw exception.
	 */
	@DirtiesDatabase
	@Test
	public void refundExceedingOriginalTokenCaptureAmountShouldThrowException() {
		Order order = orderBuilder.withNonZeroElectronicShipment().checkout();

		BigDecimal amountPaidForOrder = getAmountPaidForOrder(order);
		BigDecimal amountExceedingOriginalOrder = amountPaidForOrder.add(BigDecimal.ONE);
		exception.expect(EpServiceException.class);
		exception.expectMessage("exceeds total amount left");
		orderService.refundOrderPayment(order.getUidPk(), 
										order.getAllShipments().get(0).getShipmentNumber(), 
										null, 
										amountExceedingOriginalOrder, 
										createEventOriginator());
	}

	private BigDecimal getAmountPaidForOrder(Order order) {
		return order.getPaidAmount();
	}

	private EventOriginator createEventOriginator() {
		return eventOriginatorHelper.getSystemOriginator();
	}

	private PaymentGateway createCyberSourceExternalPaymentGateway() {
		final PaymentGateway cyberSourceGateway = paymentGatewayBuilder.withName(Utils.uniqueCode("CybersourceTokenPaymentGateway"))
				.withType("paymentGatewayCyberSourceToken")
				.withProperties(CybersourceConfigurationProvider.getProvider().getConfigurationProperties())
				.build();
		return cyberSourceGateway;
	}
}
