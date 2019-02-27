/*
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.test.integration.checkout;

import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.builder.checkout.CheckoutTestCartBuilder;
import com.elasticpath.domain.builder.payment.gateway.PaymentGatewayBuilder;
import com.elasticpath.domain.builder.shopper.ShoppingContext;
import com.elasticpath.domain.builder.shopper.ShoppingContextBuilder;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.event.EventOriginatorHelper;
import com.elasticpath.domain.misc.CheckoutResults;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderStatus;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.domain.payment.PaymentGateway;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingCartTaxSnapshot;
import com.elasticpath.persister.Persister;
import com.elasticpath.plugin.payment.PaymentGatewayType;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.plugin.payment.dto.AddressDto;
import com.elasticpath.plugin.payment.dto.OrderPaymentDto;
import com.elasticpath.plugin.payment.dto.OrderShipmentDto;
import com.elasticpath.plugin.payment.dto.impl.OrderPaymentDtoImpl;
import com.elasticpath.plugin.payment.exceptions.InsufficientFundException;
import com.elasticpath.plugin.payment.transaction.AuthorizationTransactionRequest;
import com.elasticpath.plugin.payment.transaction.AuthorizationTransactionResponse;
import com.elasticpath.plugin.payment.transaction.CaptureTransactionRequest;
import com.elasticpath.plugin.payment.transaction.CaptureTransactionResponse;
import com.elasticpath.plugin.payment.transaction.impl.AuthorizationTransactionResponseImpl;
import com.elasticpath.plugin.payment.transaction.impl.CaptureTransactionResponseImpl;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.service.order.OrderService;
import com.elasticpath.service.shoppingcart.CheckoutService;
import com.elasticpath.service.shoppingcart.ExternalAuthCheckoutService;
import com.elasticpath.service.shoppingcart.PricingSnapshotService;
import com.elasticpath.service.shoppingcart.TaxSnapshotService;
import com.elasticpath.test.integration.BasicSpringContextTest;
import com.elasticpath.test.integration.DirtiesDatabase;
import com.elasticpath.test.persister.testscenarios.SimpleStoreScenario;
import com.elasticpath.test.util.CheckoutHelper;
import com.elasticpath.test.util.mock.MockPaymentGatewayPluginHelper;

public class ExternalAuthCheckoutServiceImplTest extends BasicSpringContextTest {
	@Autowired
	private ExternalAuthCheckoutService externalAuthCheckoutService;

	@Autowired
	private CheckoutService checkoutService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private CheckoutTestCartBuilder checkoutTestCartBuilder;

	@Autowired
	private PaymentGatewayBuilder paymentGatewayBuilder;

	@Autowired
	private EventOriginatorHelper eventOriginatorHelper;

	@Autowired
	private ShoppingContextBuilder shoppingContextBuilder;

	@Autowired
	private Persister<ShoppingContext> shoppingContextPersister;

	@Autowired
	private PricingSnapshotService pricingSnapshotService;

	@Autowired
	private TaxSnapshotService taxSnapshotService;

	@Rule
	public final JUnitRuleMockery context = new JUnitRuleMockery();

	private SimpleStoreScenario scenario;

	private MockPaymentGatewayPluginHelper mockPaymentGatewayPluginHelper;

	/**
	 * Get a reference to TestApplicationContext for use within the test. Setup scenarios.
	 * 
	 * @throws Exception when exception
	 */
	@Before
	public void setUp() throws Exception {
		scenario = getTac().useScenario(SimpleStoreScenario.class);

		mockPaymentGatewayPluginHelper = new MockPaymentGatewayPluginHelper(getBeanFactory(), context, "mock", PaymentGatewayType.CREDITCARD);

		// Create a payment gateway that points to the mock plugin
		final PaymentGateway paymentGateway = paymentGatewayBuilder
				.withType("mock")
				.withName("mock")
				.build();

		checkoutTestCartBuilder
				.withScenario(scenario)
				.clearPaymentGateways()
				.withGateway(paymentGateway);
	}

	private Customer createAnonymousCustomer() {
		Customer anonymousCustomer = getTac().getPersistersFactory().getStoreTestPersister().createDefaultCustomer(scenario.getStore());
		anonymousCustomer.setAnonymous(true);
		return customerService.update(anonymousCustomer);
	}

	@Test
	@DirtiesDatabase
	public void testStandardAuthCheckout() {
		final AuthorizationTransactionResponse authorizationTransactionResponse = new AuthorizationTransactionResponseImpl();
		final CaptureTransactionResponse captureTransactionResponse = new CaptureTransactionResponseImpl();
		context.checking(new Expectations() {
			{
				exactly(2).of(mockPaymentGatewayPluginHelper.getMockPreAuthorizeCapability()).preAuthorize(
						with(any(AuthorizationTransactionRequest.class)), with(any(AddressDto.class)), with(any(OrderShipmentDto.class)));
					will(returnValue(authorizationTransactionResponse));
				oneOf(mockPaymentGatewayPluginHelper.getMockFinalizeShipmentCapability()).finalizeShipment(with(any(OrderShipmentDto.class)));
				exactly(2).of(mockPaymentGatewayPluginHelper.getMockCaptureCapability()).capture(with(any(CaptureTransactionRequest.class)));
					will(returnValue(captureTransactionResponse));
			}
		});

		final ShoppingContext shoppingContext = createPersistedShoppingContext();
		ShoppingCart shoppingCart = createTestShoppingCart(shoppingContext);
		final ShoppingCartPricingSnapshot pricingSnapshot = pricingSnapshotService.getPricingSnapshotForCart(shoppingCart);
		final ShoppingCartTaxSnapshot taxSnapshot = taxSnapshotService.getTaxSnapshotForCart(shoppingCart, pricingSnapshot);

		OrderPayment templateOrderPayment = createTestTemplateOrderPayment(shoppingContext.getCustomerSession(), taxSnapshot);

		CheckoutResults checkoutResults = checkoutService.checkout(shoppingCart,
																   taxSnapshot,
																   shoppingContext.getCustomerSession(), templateOrderPayment, true);
		Order order = checkoutResults.getOrder();
		OrderValidator orderValidator1 = OrderValidator.builder()
				.withStatus(OrderStatus.PARTIALLY_SHIPPED)
				.withPaymentMatchers(
						OrderPaymentMatcherFactory.createSuccessfulTokenAuthorization(),
						OrderPaymentMatcherFactory.createSuccessfulTokenAuthorization(),
						OrderPaymentMatcherFactory.createSuccessfulTokenCapture())
				.build();
		assertThat("The order validation should succeed.", order, orderValidator1);

		// Release and complete the physical shipment
		PhysicalOrderShipment physicalOrderShipment = order.getPhysicalShipments().iterator().next();
		orderService.processReleaseShipment(physicalOrderShipment);
		Order updatedOrder = orderService.completeShipment(physicalOrderShipment.getShipmentNumber(), "trackingCode", true, new Date(),
				false, eventOriginatorHelper.getSystemOriginator());
		OrderValidator orderValidator2 = OrderValidator.builder()
				.withStatus(OrderStatus.COMPLETED)
				.withPaymentMatchers(
						OrderPaymentMatcherFactory.createSuccessfulTokenAuthorization(),
						OrderPaymentMatcherFactory.createSuccessfulTokenAuthorization(),
						OrderPaymentMatcherFactory.createSuccessfulTokenCapture(),
						OrderPaymentMatcherFactory.createSuccessfulTokenCapture())
				.build();
		assertThat("The order validation should succeed.", updatedOrder, orderValidator2);
	}

	@Test
	@DirtiesDatabase
	public void testStandardAuthCheckoutWithAuthFailure() {
		context.checking(new Expectations() {
			{
				exactly(2).of(mockPaymentGatewayPluginHelper.getMockPreAuthorizeCapability()).preAuthorize(
						with(any(AuthorizationTransactionRequest.class)), with(any(AddressDto.class)), with(any(OrderShipmentDto.class)));
					will(throwException(new InsufficientFundException("Card does not have sufficient funds.")));
			}
		});

		final ShoppingContext shoppingContext = createPersistedShoppingContext();
		ShoppingCart shoppingCart = createTestShoppingCart(shoppingContext);

		final ShoppingCartPricingSnapshot pricingSnapshot = pricingSnapshotService.getPricingSnapshotForCart(shoppingCart);
		final ShoppingCartTaxSnapshot taxSnapshot = taxSnapshotService.getTaxSnapshotForCart(shoppingCart, pricingSnapshot);

		OrderPayment templateOrderPayment = createTestTemplateOrderPayment(shoppingContext.getCustomerSession(), taxSnapshot);

		CheckoutResults checkoutResults = checkoutService.checkout(shoppingCart,
																   taxSnapshot,
																   shoppingContext.getCustomerSession(), templateOrderPayment, false);
		Order order = checkoutResults.getOrder();
		OrderValidator orderValidator = OrderValidator.builder()
				.withStatus(OrderStatus.FAILED)
				.withPaymentMatchers(
						OrderPaymentMatcherFactory.createFailedTokenAuthorization(),
						OrderPaymentMatcherFactory.createFailedTokenAuthorization())
				.build();
		assertThat("The order validation should succeed.", order, orderValidator);
	}

	@Test
	@DirtiesDatabase
	public void testDirectPostAuthCheckout() {
		final ShoppingContext shoppingContext = createPersistedShoppingContext();
		final ShoppingCart shoppingCart = createTestShoppingCart(shoppingContext);
		final ShoppingCartPricingSnapshot pricingSnapshot = pricingSnapshotService.getPricingSnapshotForCart(shoppingCart);
		final ShoppingCartTaxSnapshot taxSnapshot = taxSnapshotService.getTaxSnapshotForCart(shoppingCart, pricingSnapshot);

		final OrderPaymentDto testExternalAuthResponse = createTestExternalAuthResponse(shoppingContext.getCustomerSession(),
																						taxSnapshot);
		final CaptureTransactionResponse captureTransactionResponse = new CaptureTransactionResponseImpl();
		final AuthorizationTransactionResponse authorizationTransactionResponse = new AuthorizationTransactionResponseImpl();
		final Map<String, String> responseMap = new HashMap<>();
		context.checking(new Expectations() {
			{
				oneOf(mockPaymentGatewayPluginHelper.getMockExternalAuthCapability()).handleExternalAuthResponse(with(responseMap));
					will(returnValue(testExternalAuthResponse));
					
				oneOf(mockPaymentGatewayPluginHelper.getMockFinalizeShipmentCapability()).finalizeShipment(with(any(OrderShipmentDto.class)));
				
				exactly(2).of(mockPaymentGatewayPluginHelper.getMockCaptureCapability()).capture(with(any(CaptureTransactionRequest.class)));
				will(returnValue(captureTransactionResponse));
				
				atLeast(1).of(mockPaymentGatewayPluginHelper.getMockPreAuthorizeCapability()).preAuthorize(
						with(any(AuthorizationTransactionRequest.class)), with(any(AddressDto.class)), with(any(OrderShipmentDto.class)));
				will(returnValue(authorizationTransactionResponse));
			}
		});

		// Simulate post to billing and review success page
		CheckoutResults checkoutResults = externalAuthCheckoutService.checkoutAfterExternalAuth(shoppingCart,
																								taxSnapshot,
																								shoppingContext.getCustomerSession(),
																								PaymentType.PAYMENT_TOKEN,
																								responseMap);
		Order order = checkoutResults.getOrder();
		OrderValidator orderValidator = OrderValidator.builder()
				.withStatus(OrderStatus.PARTIALLY_SHIPPED)
				.withPaymentMatchers(
						OrderPaymentMatcherFactory.createSuccessfulTokenAuthorization(),
						OrderPaymentMatcherFactory.createSuccessfulTokenAuthorization(),
						OrderPaymentMatcherFactory.createSuccessfulTokenCapture())
				.build();
		assertThat("The order validation should succeed.", order, orderValidator);

		// Release and complete the physical shipment
		PhysicalOrderShipment physicalOrderShipment = order.getPhysicalShipments().iterator().next();
		orderService.processReleaseShipment(physicalOrderShipment);
		Order updatedOrder = orderService.completeShipment(physicalOrderShipment.getShipmentNumber(), "trackingCode", true, new Date(),
				false, eventOriginatorHelper.getSystemOriginator());
		OrderValidator orderValidator2 = OrderValidator.builder()
				.withStatus(OrderStatus.COMPLETED)
				.withPaymentMatchers(
						OrderPaymentMatcherFactory.createSuccessfulTokenAuthorization(),
						OrderPaymentMatcherFactory.createSuccessfulTokenAuthorization(),
						OrderPaymentMatcherFactory.createSuccessfulTokenCapture(),
						OrderPaymentMatcherFactory.createSuccessfulTokenCapture())
				.build();
		assertThat("The order validation should succeed.", updatedOrder, orderValidator2);
	}

	private ShoppingContext createPersistedShoppingContext() {
		final Customer anonymousCustomer = createAnonymousCustomer();
		final ShoppingContext shoppingContext = shoppingContextBuilder
				.withCustomer(anonymousCustomer)
				.withStoreCode(scenario.getStore().getCode())
				.build();

		shoppingContextPersister.persist(shoppingContext);

		return shoppingContext;
	}

	private ShoppingCart createTestShoppingCart(final ShoppingContext shoppingContext) {
		ShoppingCart shoppingCart = checkoutTestCartBuilder
				.withCustomerSession(shoppingContext.getCustomerSession())
				.withElectronicProduct()
				.withPhysicalProduct()
				.build();

		CheckoutHelper checkoutHelper = new CheckoutHelper(getTac());
		checkoutHelper.enrichShoppingCartForCheckout(shoppingCart);
		return shoppingCart;
	}

	private OrderPaymentDto createTestExternalAuthResponse(final CustomerSession customerSession, final	ShoppingCartTaxSnapshot taxSnapshot) {
		final OrderPaymentDto orderPaymentDto = new OrderPaymentDtoImpl();
		orderPaymentDto.setAmount(taxSnapshot.getTotal());
		orderPaymentDto.setCurrencyCode(customerSession.getCurrency().getCurrencyCode());
		return orderPaymentDto;
	}

	private OrderPayment createTestTemplateOrderPayment(final CustomerSession customerSession, final ShoppingCartTaxSnapshot taxSnapshot) {
		OrderPayment templateOrderPayment = getBeanFactory().getBean(ContextIdNames.ORDER_PAYMENT);
		templateOrderPayment.setPaymentMethod(PaymentType.PAYMENT_TOKEN);
		templateOrderPayment.setAmount(taxSnapshot.getTotal());
		templateOrderPayment.setCurrencyCode(customerSession.getCurrency().getCurrencyCode());
		return templateOrderPayment;
	}
}
