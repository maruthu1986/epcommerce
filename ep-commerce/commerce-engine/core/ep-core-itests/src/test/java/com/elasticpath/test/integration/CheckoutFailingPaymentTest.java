/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.test.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Currency;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.common.dto.sellingchannel.ShoppingItemDtoFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.builder.shopper.ShoppingContext;
import com.elasticpath.domain.builder.shopper.ShoppingContextBuilder;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.customer.impl.CustomerAddressImpl;
import com.elasticpath.domain.event.EventOriginatorHelper;
import com.elasticpath.domain.factory.TestCustomerSessionFactoryForTestApplication;
import com.elasticpath.domain.factory.TestShoppingCartFactoryForTestApplication;
import com.elasticpath.domain.misc.CheckoutResults;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderPaymentStatus;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderShipmentStatus;
import com.elasticpath.domain.order.OrderStatus;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.domain.payment.PaymentGateway;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingCartTaxSnapshot;
import com.elasticpath.persister.Persister;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.sellingchannel.director.CartDirector;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.order.CompleteShipmentFailedException;
import com.elasticpath.service.order.OrderService;
import com.elasticpath.service.payment.gateway.impl.NullPaymentGatewayPluginImpl;
import com.elasticpath.service.shoppingcart.CheckoutService;
import com.elasticpath.service.shoppingcart.PricingSnapshotService;
import com.elasticpath.service.shoppingcart.ShoppingCartService;
import com.elasticpath.service.shoppingcart.TaxSnapshotService;
import com.elasticpath.test.persister.TestDataPersisterFactory;
import com.elasticpath.test.persister.testscenarios.SimpleStoreScenario;
import com.elasticpath.test.util.Utils;

/**
 * Test failing payments on checkout.
 */
public class CheckoutFailingPaymentTest extends BasicSpringContextTest {

	@Autowired
	private CheckoutService checkoutService;

	@Autowired
	private CartDirector cartDirector;
	
	@Autowired
	private ShoppingItemDtoFactory shoppingItemDtoFactory;

	@Autowired
	private ShoppingContextBuilder shoppingContextBuilder;

	/** An anonymous customer, you can use them to make orders. */
	private Customer anonymousCustomer;

	private SimpleStoreScenario scenario;

	private TestDataPersisterFactory persisterFactory;
	
	@Autowired
	private OrderService orderService;

	@Autowired
	private Persister<ShoppingContext> shoppingContextPersister;

	@Autowired
	private PricingSnapshotService pricingSnapshotService;

	@Autowired
	private TaxSnapshotService taxSnapshotService;

	/**
	 * Get a reference to TestApplicationContext for use within the test. Setup scenarios.
	 *
	 * @throws Exception when exception
	 */
	@Before
	public void setUp() throws Exception {
		scenario = getTac().useScenario(SimpleStoreScenario.class);
		persisterFactory = getTac().getPersistersFactory();
		scenario.getStore().setPaymentGateways(setUpPaymentGatewayAndProperties());

		anonymousCustomer = persisterFactory.getStoreTestPersister().createDefaultCustomer(scenario.getStore());

		// Reset the payment gateway for each test.
		NullPaymentGatewayPluginImpl.setFailOnCapture(false);
		NullPaymentGatewayPluginImpl.setFailOnPreAuthorize(false);
		NullPaymentGatewayPluginImpl.setFailOnReversePreAuthorization(false);
		NullPaymentGatewayPluginImpl.setFailOnSale(false);
	}

	@After
	public void tearDown() throws Exception {
		NullPaymentGatewayPluginImpl.setFailOnCapture(false);
		NullPaymentGatewayPluginImpl.setFailOnPreAuthorize(false);
		NullPaymentGatewayPluginImpl.setFailOnReversePreAuthorization(false);
		NullPaymentGatewayPluginImpl.setFailOnSale(false);
	}

	// ============================ TESTS ========================= \\

	/**
	 * Integration test to check that a failed auth on a physical shipment causes the order to have
	 * a failed status.
	 */
	@DirtiesDatabase
	@Test
	public void testPreAuthorizeFail() {
		final ShoppingContext shoppingContext = createShoppingContext(anonymousCustomer);
		final ShoppingCart shoppingCart = createAnonymousShoppingCartWithScenarioStore(shoppingContext);
		cartDirector.addItemToCart(shoppingCart, shoppingItemDtoFactory.createDto(createPhysicalProduct(), 1));

		final ShoppingCartPricingSnapshot pricingSnapshot = pricingSnapshotService.getPricingSnapshotForCart(shoppingCart);
		final ShoppingCartTaxSnapshot taxSnapshot = taxSnapshotService.getTaxSnapshotForCart(shoppingCart, pricingSnapshot);

		// make new order payment
		final OrderPayment templateOrderPayment = getOrderPayment();

		NullPaymentGatewayPluginImpl.setFailOnPreAuthorize(true);

		CheckoutResults checkoutResult = checkoutService.checkout(shoppingCart,
				taxSnapshot,
				shoppingContext.getCustomerSession(), templateOrderPayment, false);
		assertTrue("order should fail", checkoutResult.isOrderFailed());

		Order order = checkoutResult.getOrder();
		assertNotNull("There should have been an order on the checkout results", order);
		assertTrue("The order should be persistent", order.isPersisted());
		assertEquals("There should be one payment on the order", 1, order.getOrderPayments().size());

		Collection<OrderPayment> failedAuths = collectByTypeAndStatus(order.getOrderPayments(), OrderPayment.AUTHORIZATION_TRANSACTION,
				OrderPaymentStatus.FAILED);
		assertEquals("There should be 1 failed auth", 1, failedAuths.size());
	}

	/**
	 * 1. Add digital product and physical product to the shopping cart 2. Checkout. Check payments and shipments. 3. Fail on release shipment 4.
	 * Check payments Payments should be 2 auths and 1 capture initially when creating the order. After failing to release the shipment there has to
	 * be created one more capture on the previously created auth with FAILED state.
	 */
	@Ignore("FIXME: checkout with electronic shipments will fail intermittently due to @PrePersist and @PostPersist doing too much")
	@DirtiesDatabase
	@Test
	public void testSplitShipmentCaptureFailCausesPartialShippingStatus() {
		final ShoppingContext shoppingContext = createShoppingContext(anonymousCustomer);
		ShoppingCart shoppingCart = createAnonymousShoppingCartWithScenarioStore(shoppingContext);
		cartDirector.addItemToCart(shoppingCart, shoppingItemDtoFactory.createDto(createPhysicalProduct(), 1));
		cartDirector.addItemToCart(shoppingCart, shoppingItemDtoFactory.createDto(createNonShippableProduct(), 2));

		final ShoppingCartPricingSnapshot pricingSnapshot = pricingSnapshotService.getPricingSnapshotForCart(shoppingCart);
		final ShoppingCartTaxSnapshot taxSnapshot = taxSnapshotService.getTaxSnapshotForCart(shoppingCart, pricingSnapshot);

		OrderPayment templateOrderPayment = getOrderPayment();

		// Allow the initial order's payments to succeed
		NullPaymentGatewayPluginImpl.setFailOnCapture(false);
		NullPaymentGatewayPluginImpl.setFailOnPreAuthorize(false);

		checkoutService.checkout(shoppingCart, taxSnapshot, shoppingContext.getCustomerSession(), templateOrderPayment, true);

		List<Order> ordersList = orderService.findOrderByCustomerGuid(shoppingCart.getShopper().getCustomer().getGuid(), true);
		assertEquals("only one order should have been created by the checkout service", 1, ordersList.size());

		Order order = ordersList.iterator().next();
		assertEquals("two shipments should have been created", 2, order.getAllShipments().size());
		assertOneShipmentWithStatus(order.getElectronicShipments(), OrderShipmentStatus.SHIPPED);
		assertOneShipmentWithStatus(order.getPhysicalShipments(), OrderShipmentStatus.INVENTORY_ASSIGNED);

		assertEquals("three payments should exist: 2 auths and 1 capture (for electronic shipment)", 3, order.getOrderPayments().size());
		Collection<OrderPayment> approvedAuthPayments = collectByTypeAndStatus(order.getOrderPayments(), OrderPayment.AUTHORIZATION_TRANSACTION,
				OrderPaymentStatus.APPROVED);
		assertEquals("There should be 2 approved auth payments", 2, approvedAuthPayments.size());

		OrderShipment physicalShipment = getFirstShipment(order.getPhysicalShipments());

		// Allow the order to be released
		physicalShipment.setStatus(OrderShipmentStatus.RELEASED);
		order = orderService.update(order);

		// Cause the release's capture to fail.
		NullPaymentGatewayPluginImpl.setFailOnCapture(true);

		// attempt to release shipment
		physicalShipment = getFirstShipment(order.getPhysicalShipments());
		try {
			orderService.completeShipment(physicalShipment.getShipmentNumber(), Utils.uniqueCode("trackN"), true, new Date(), false,
					getEventOriginatorHelper().getSystemOriginator());
			fail("Shipment should have failed to complete due to payment error.");
		} catch (CompleteShipmentFailedException expected) {
			// Expected
		}

		// Re-read the order because the shipment failed.
		order = orderService.get(order.getUidPk());

		physicalShipment = getFirstShipment(order.getPhysicalShipments());
		assertEquals("Order status shouldn't have changed", OrderStatus.PARTIALLY_SHIPPED, order.getStatus());
		assertEquals("Shipment status shouldn't have changed", OrderShipmentStatus.RELEASED, physicalShipment.getShipmentStatus());

		assertEquals("there should be two payments = 2 auth + 2 capture", 4, order.getOrderPayments().size());

		Collection<OrderPayment> approvedCaptures = collectByTypeAndStatus(order.getOrderPayments(), OrderPayment.CAPTURE_TRANSACTION,
				OrderPaymentStatus.APPROVED);
		assertEquals("There should be 1 approved capture", 1, approvedCaptures.size());

		Collection<OrderPayment> failedCaptures = collectByTypeAndStatus(order.getOrderPayments(), OrderPayment.CAPTURE_TRANSACTION,
				OrderPaymentStatus.FAILED);
		assertEquals("There should be 1 failed capture", 1, failedCaptures.size());
	}

	/**
	 * Scenario: 1. Create shopping cart (1 digital + 1 physical products) 2. Checkout 3. Cancel order shipment 4. Check payments
	 */
	@Ignore("FIXME: checkout with electronic shipments will fail intermittently due to @PrePersist and @PostPersist doing too much")
	@DirtiesDatabase
	@Test
	public void testSplitShipmentCancelShipment() {
		ShoppingContext shoppingContext = createShoppingContext(anonymousCustomer);
		ShoppingCart shoppingCart = createAnonymousShoppingCartWithScenarioStore(shoppingContext);

		final ShoppingCartPricingSnapshot pricingSnapshot = pricingSnapshotService.getPricingSnapshotForCart(shoppingCart);
		final ShoppingCartTaxSnapshot taxSnapshot = taxSnapshotService.getTaxSnapshotForCart(shoppingCart, pricingSnapshot);

		cartDirector.addItemToCart(shoppingCart, shoppingItemDtoFactory.createDto(createPhysicalProduct(), 1));
		cartDirector.addItemToCart(shoppingCart, shoppingItemDtoFactory.createDto(createNonShippableProduct(), 2));

		OrderPayment templateOrderPayment = getOrderPayment();

		// setup the payment gateway not to fail on preAuth and capture payments
		NullPaymentGatewayPluginImpl.setFailOnCapture(false);
		NullPaymentGatewayPluginImpl.setFailOnPreAuthorize(false);

		checkoutService.checkout(shoppingCart, taxSnapshot, shoppingContext.getCustomerSession(), templateOrderPayment, true);

		List<Order> ordersList = orderService.findOrderByCustomerGuid(shoppingCart.getShopper().getCustomer().getGuid(), true);
		assertEquals("only one order should have been created by the checkout service", 1, ordersList.size());

		Order order = ordersList.iterator().next();
		assertEquals("two shipments should have been created", 2, order.getAllShipments().size());
		assertOneShipmentWithStatus(order.getElectronicShipments(), OrderShipmentStatus.SHIPPED);
		assertOneShipmentWithStatus(order.getPhysicalShipments(), OrderShipmentStatus.INVENTORY_ASSIGNED);

		// three payments should exist: 2 auths and 1 capture (for electronic shipment)
		assertEquals("three payments should exist: 2 auths and 1 capture (for electronic shipment)", 3, order.getOrderPayments().size());
		assertEquals(2,
				collectByTypeAndStatus(order.getOrderPayments(), OrderPayment.AUTHORIZATION_TRANSACTION, OrderPaymentStatus.APPROVED).size());
		OrderShipment physicalShipment = getFirstShipment(order.getPhysicalShipments());

		// Release the shipment.
		order.setModifiedBy(getEventOriginatorHelper().getSystemOriginator());
		physicalShipment = orderService.processReleaseShipment(physicalShipment);

		// set payment gateway to fail on capture
		NullPaymentGatewayPluginImpl.setFailOnCapture(true);

		assertFalse("The order should not be able to be canceled as one shipment is already shipped", order.isCancellable());
		try {
			orderService.cancelOrder(order);
			fail("Exception should be thrown if the order is not in a state to be cancelled");
		} catch (EpServiceException expected) {
			// We expect this
		}

		assertTrue("The physical shipment should be cancellable - it hasn't reached 'Released' yet", physicalShipment.isCancellable());

		physicalShipment = orderService.cancelOrderShipment((PhysicalOrderShipment) physicalShipment);

		order = physicalShipment.getOrder();
		assertEquals("Shipment status should be CANCELLED.", OrderShipmentStatus.CANCELLED, physicalShipment.getShipmentStatus());
		assertEquals("Order should now be completed since one shipment is cancelled and one is shipped.", OrderStatus.COMPLETED, order.getStatus());

		assertEquals("there should be two payments = 2 auth + 2 capture", 4, order.getOrderPayments().size());

		Collection<OrderPayment> approvedCaptures = collectByTypeAndStatus(order.getOrderPayments(), OrderPayment.CAPTURE_TRANSACTION,
				OrderPaymentStatus.APPROVED);
		assertEquals("There should be 1 approved capture", 1, approvedCaptures.size());

		Collection<OrderPayment> failedCaptures = collectByTypeAndStatus(order.getOrderPayments(), OrderPayment.CAPTURE_TRANSACTION,
				OrderPaymentStatus.FAILED);
		assertEquals("There should be no failed capture", 0, failedCaptures.size());

		Collection<OrderPayment> approvedReversals = collectByTypeAndStatus(order.getOrderPayments(), OrderPayment.REVERSE_AUTHORIZATION,
				OrderPaymentStatus.APPROVED);
		assertEquals("There should be 1 approved reversal", 1, approvedReversals.size());

	}

	// =================== UTILITY METHODS ========================= \\

	/**
	 * Gets the order payment.
	 *
	 * @return the order payment
	 */
	private OrderPayment getOrderPayment() {
		final OrderPayment orderPayment = getBeanFactory().getBean(ContextIdNames.ORDER_PAYMENT);
		orderPayment.setCreatedDate(new Date());
		orderPayment.setCurrencyCode("USD");
		orderPayment.setEmail(anonymousCustomer.getEmail());
		orderPayment.setPaymentMethod(PaymentType.PAYMENT_TOKEN);
		return orderPayment;
	}

	private ShoppingContext createShoppingContext(final Customer customer) {
		final ShoppingContext shoppingContext = shoppingContextBuilder
				.withCustomer(customer)
				.build();

		shoppingContextPersister.persist(shoppingContext);

		return shoppingContext;
	}

	/**
	 * Creates the anonymous shopping cart with scenario store.
	 *
	 * @return the shopping cart
	 */
	private ShoppingCart createAnonymousShoppingCartWithScenarioStore(final ShoppingContext shoppingContext) {
		final ShoppingCart shoppingCart = TestShoppingCartFactoryForTestApplication.getInstance().createNewCartWithMemento(
				shoppingContext.getShopper(), scenario.getStore());

		final CustomerSession customerSession = shoppingContext.getCustomerSession();
		customerSession.setCurrency(Currency.getInstance(Locale.US));

		// FIXME: Remove once shoppingCart does not delegate back to CustomerSession.
		shoppingCart.setCustomerSession(customerSession);

		shoppingCart.initialize();
		shoppingCart.setBillingAddress(getBillingAddress());
		shoppingCart.setShippingAddress(getBillingAddress());
		shoppingCart.setSelectedShippingOption(scenario.getShippingOption());

		final ShoppingCartService shoppingCartService = getBeanFactory().getBean(ContextIdNames.SHOPPING_CART_SERVICE);
		shoppingCartService.saveOrUpdate(shoppingCart);

		return shoppingCart;
	}

	/**
	 * Initializes a mock billing address.
	 *
	 * @return the Address
	 */
	private Address getBillingAddress() {
		final Address billingAddress = new CustomerAddressImpl();
		billingAddress.setFirstName("Billy");
		billingAddress.setLastName("Bob");
		billingAddress.setCountry("CA");
		billingAddress.setStreet1("1295 Charleston Road");
		billingAddress.setCity("Vancouver");
		billingAddress.setSubCountry("BC");
		billingAddress.setZipOrPostalCode("V5N1T8");
		billingAddress.setGuid(Utils.uniqueCode("address"));
		return billingAddress;
	}

	private Set<PaymentGateway> setUpPaymentGatewayAndProperties() {
		final Set<PaymentGateway> gateways = new HashSet<>();
		gateways.add(persisterFactory.getStoreTestPersister().persistDefaultPaymentGateway());
		return gateways;
	}

	private Collection<OrderPayment> collectByTypeAndStatus(final Set<OrderPayment> orderPayments, final String transactionType,
			final OrderPaymentStatus expectedStatus) {
		List<OrderPayment> payments = new ArrayList<>();
		for (OrderPayment payment : orderPayments) {
			if (transactionType.equals(payment.getTransactionType()) && expectedStatus.equals(payment.getStatus())) {
				payments.add(payment);
			}
		}
		return payments;
	}

	private OrderShipment getFirstShipment(final Collection<? extends OrderShipment> shipments) {
		return shipments.iterator().next();
	}

	private void assertOneShipmentWithStatus(final Collection<? extends OrderShipment> shipments, final OrderShipmentStatus expectedStatus) {
		assertEquals("Unexpected number of shipments", 1, shipments.size());
		OrderShipment shipment = shipments.iterator().next();
		assertEquals(expectedStatus, shipment.getShipmentStatus());
	}

	private Product createPhysicalProduct() {
		return persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());
	}

	private Product createNonShippableProduct() {
		Product digitalProduct = createPhysicalProduct();
		digitalProduct.getDefaultSku().setShippable(false);

		ProductService prodService = getBeanFactory().getBean(ContextIdNames.PRODUCT_SERVICE);
		digitalProduct = prodService.saveOrUpdate(digitalProduct);
		return digitalProduct;
	}

	public EventOriginatorHelper getEventOriginatorHelper() {
		return getBeanFactory().getBean(ContextIdNames.EVENT_ORIGINATOR_HELPER);
	}

	public void setScenario(final SimpleStoreScenario scenario) {
		this.scenario = scenario;
	}

	public void setPersisterFactory(final TestDataPersisterFactory persisterFactory) {
		this.persisterFactory = persisterFactory;
	}
}
