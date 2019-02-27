/**
 * Copyright (c) Elastic Path Software Inc., 2018
 */

package com.elasticpath.test.integration;

import static com.elasticpath.commons.constants.ContextIdNames.ORDER_SEARCH_CRITERIA;
import static com.elasticpath.domain.catalog.AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK;
import static com.elasticpath.domain.catalog.InventoryEventType.STOCK_ADJUSTMENT;
import static com.elasticpath.domain.customer.impl.PaymentTokenImpl.TokenBuilder;
import static com.elasticpath.domain.order.OrderPayment.AUTHORIZATION_TRANSACTION;
import static com.elasticpath.domain.order.OrderPayment.CAPTURE_TRANSACTION;
import static com.elasticpath.domain.order.OrderPayment.REVERSE_AUTHORIZATION;
import static com.elasticpath.domain.order.OrderPaymentStatus.APPROVED;
import static com.elasticpath.domain.order.OrderShipmentStatus.FAILED_ORDER;
import static com.elasticpath.domain.order.OrderShipmentStatus.INVENTORY_ASSIGNED;
import static com.elasticpath.domain.order.OrderShipmentStatus.RELEASED;
import static com.elasticpath.domain.order.OrderStatus.CANCELLED;
import static com.elasticpath.domain.order.OrderStatus.FAILED;
import static com.elasticpath.domain.order.OrderStatus.IN_PROGRESS;
import static com.elasticpath.domain.order.OrderStatus.ONHOLD;
import static com.elasticpath.domain.shipping.ShipmentType.PHYSICAL;
import static com.elasticpath.plugin.payment.PaymentType.PAYMENT_TOKEN;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.collect.Iterables.find;
import static java.lang.System.currentTimeMillis;
import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.persistence.EntityManager;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.EndpointInject;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.spi.DataFormat;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.TransactionSystemException;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.core.messaging.order.OrderEventType;
import com.elasticpath.domain.catalog.AvailabilityCriteria;
import com.elasticpath.domain.catalog.InventoryAudit;
import com.elasticpath.domain.catalog.InventoryEventType;
import com.elasticpath.domain.catalog.Price;
import com.elasticpath.domain.catalog.Product;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.catalog.impl.InventoryAuditImpl;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.customer.PaymentToken;
import com.elasticpath.domain.customer.impl.PaymentTokenImpl;
import com.elasticpath.domain.event.EventOriginatorHelper;
import com.elasticpath.domain.event.impl.EventOriginatorHelperImpl;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderShipmentStatus;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.domain.order.PurchaseHistorySearchCriteria;
import com.elasticpath.domain.order.impl.AbstractOrderShipmentImpl;
import com.elasticpath.domain.order.impl.OrderAddressImpl;
import com.elasticpath.domain.order.impl.OrderEventImpl;
import com.elasticpath.domain.order.impl.OrderImpl;
import com.elasticpath.domain.order.impl.OrderPaymentImpl;
import com.elasticpath.domain.order.impl.OrderSkuImpl;
import com.elasticpath.domain.order.impl.PhysicalOrderShipmentImpl;
import com.elasticpath.domain.order.impl.PurchaseHistorySearchCriteriaImpl;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingCartTaxSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.store.Store;
import com.elasticpath.inventory.InventoryDto;
import com.elasticpath.inventory.InventoryFacade;
import com.elasticpath.messaging.EventMessage;
import com.elasticpath.messaging.camel.test.support.CamelContextMessagePurger;
import com.elasticpath.messaging.factory.EventMessageFactory;
import com.elasticpath.money.Money;
import com.elasticpath.persistence.api.PersistenceEngine;
import com.elasticpath.persistence.api.PersistenceSession;
import com.elasticpath.persistence.api.Transaction;
import com.elasticpath.sellingchannel.director.CartDirector;
import com.elasticpath.service.catalog.ProductInventoryManagementService;
import com.elasticpath.service.catalog.ProductService;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.order.OrderService;
import com.elasticpath.service.payment.PaymentResult;
import com.elasticpath.service.payment.PaymentService;
import com.elasticpath.service.search.query.OrderSearchCriteria;
import com.elasticpath.service.shoppingcart.CheckoutService;
import com.elasticpath.service.shoppingcart.PricingSnapshotService;
import com.elasticpath.service.shoppingcart.TaxSnapshotService;
import com.elasticpath.service.shoppingcart.actions.CheckoutActionContext;
import com.elasticpath.service.shoppingcart.actions.ReversibleCheckoutAction;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.test.integration.checkout.AlwaysHoldCheckoutAction;
import com.elasticpath.test.persister.CatalogTestPersister;
import com.elasticpath.test.persister.TestDataPersisterFactory;
import com.elasticpath.test.persister.testscenarios.SimpleStoreScenario;

/**
 * Integration test for OrderService.
 */
@SuppressWarnings({ "PMD.TooManyStaticImports", "deprecation", "PMD.GodClass", "PMD.CouplingBetweenObjects" })
public class OrderServiceImplTest extends BasicSpringContextTest {

	private static final String INVALID_GUID = "BAD GUID";

	private static final String NON_EXISTENT_CART_ORDER_GUID = "NON_EXISTENT_CART_ORDER_GUID";

	private static final String DIFFERENT_CART_ORDER_GUID = "DIFFERENT_CART_ORDER_GUID";

	private static final String CART_ORDER_GUID = "CART_ORDER_GUID";

	private static final String ORDER_MESSAGING_CAMEL_CONTEXT = "ep-order-messaging";

	@Autowired
	private EventMessageFactory eventMessageFactory;

	@Autowired
	@Qualifier(ORDER_MESSAGING_CAMEL_CONTEXT)
	private CamelContext camelContext;

	@Autowired
	private CheckoutService checkoutService;

	@Autowired
	private PaymentService paymentService;

	private Store store;

	private Product product;

	private Customer customer;

	private CustomerAddress address;

	private CustomerSession customerSession;

	private SimpleStoreScenario scenario;

	private TestDataPersisterFactory persisterFactory;

	@Autowired
	private OrderService orderService;

	private static final int MAX_RESULTS = 10;

	@Autowired
	private CartDirector cartDirector;

	@Autowired
	private InventoryFacade inventoryFacade;

	private Shopper shopper;

	@Autowired
	private PersistenceEngine persistenceEngine;
	@Autowired
	private StoreService storeService;
	@Autowired
	private TimeService timeService;

	@EndpointInject(uri = "mock:orders/events", context = ORDER_MESSAGING_CAMEL_CONTEXT)
	private MockEndpoint mockOrderEventEndpoint;

	@EndpointInject(ref = "epOrderMessagingOrderEventExternalEndpoint")
	private Endpoint orderEventOutgoingEndpoint;

	@Autowired
	private DataFormat eventMessageDataFormat;

	private List<ReversibleCheckoutAction> reversibleCheckoutActions;
	private final ReversibleCheckoutAction failingAction = new ReversibleCheckoutAction() {

		@Override
		public void execute(CheckoutActionContext context) throws EpSystemException {
			throw new EpSystemException("Causing previous ReversibleCheckoutActions to roll back.");

		}

		@Override
		public void rollback(CheckoutActionContext context)
				throws EpSystemException {
			//do nothing
		}
	};

	@Autowired
	private ProductInventoryManagementService productInventoryManagementService;
	@Autowired
	private ProductService productService;
	@Autowired
	private CamelContextMessagePurger camelContextMessagePurger;
	@Autowired
	private CatalogTestPersister catalogTestPersister;
	@Autowired
	private ProductSkuLookup productSkuLookup;
	@Autowired
	private PricingSnapshotService pricingSnapshotService;
	@Autowired
	private TaxSnapshotService taxSnapshotService;
	@Autowired
	private EventOriginatorHelper eventOriginatorHelper;
	@Autowired
	private EntityManager em;

	/**
	 * Get a reference to TestApplicationContext for use within the test. Setup scenarios.
	 */
	@Before
	public void setUp() {
		scenario = getTac().useScenario(SimpleStoreScenario.class);
		persisterFactory = getTac().getPersistersFactory();
		store = scenario.getStore();
		product = catalogTestPersister.createDefaultProductWithSkuAndInventory(
				scenario.getCatalog(), scenario.getCategory(), scenario.getWarehouse());

		customer = persisterFactory.getStoreTestPersister().createDefaultCustomer(store);
		address = persisterFactory.getStoreTestPersister().createCustomerAddress("Bond", "James", "1234 Pine Street", "", "Vancouver", "CA", "BC",
				"V6J5G4", "891312345007");
		customerSession = persisterFactory.getStoreTestPersister().persistCustomerSessionWithAssociatedEntities(customer);
		shopper = customerSession.getShopper();
		reversibleCheckoutActions = getBeanFactory().getBean("reversibleActions");
	}

	/**
	 * Messages will be purged from Camel before and after each individual test is run.
	 *
	 * @throws Exception if the {@link CamelContextMessagePurger} is unable to purge all messages from the Camel context.
	 */
	@After
	@Before
	public void drainCamelEndpoints() throws Exception {
		camelContextMessagePurger.purgeMessages(camelContext);
	}

	// ============================ TESTS ========================= \\

	/**
	 * Make sure that the failing action is removed after each test so that it doesn't break the suite.
	 */
	@After
	public void cleanUpFailingReversibleActionIfExists() {
		cleanUpReversibleCheckoutAction(failingAction);
	}

	/**
	 * Test order inventory.
	 */
	@Test
	public void testOrderInventory() {
		//default product is ALWAYS_AVAILABLE which prevents querying of inventories
		product.setAvailabilityCriteria(AVAILABLE_WHEN_IN_STOCK);

		// construct and save new shopping cart
		final Shopper shopper = customerSession.getShopper();
		ShoppingCart shoppingCart = givenASimpleShoppingCart();

		assertNotNull("Shopping cart should contain items", shoppingCart.getRootShoppingItems());
		assertThat(shoppingCart.getRootShoppingItems()).as("There should be 1 item in the shopping cart").hasSize(1);
		ProductSku productSku = product.getDefaultSku();

		// make new order payment
		OrderPayment templateOrderPayment = persisterFactory.getOrderTestPersister().createOrderPayment(customer, new TokenBuilder()
				.build());

		// checkout
		assertCustomerEmailEqualsShopperCustomerEmail(shopper);

		final ShoppingCartPricingSnapshot pricingSnapshot = pricingSnapshotService.getPricingSnapshotForCart(shoppingCart);
		final ShoppingCartTaxSnapshot taxSnapshot = taxSnapshotService.getTaxSnapshotForCart(shoppingCart, pricingSnapshot);

		checkoutService.checkout(shoppingCart, taxSnapshot, customerSession, templateOrderPayment, true);

		// only one order should have been created by the checkout service
		List<Order> ordersList = orderService.findOrderByCustomerGuid(shopper.getCustomer().getGuid(), true);
		assertThat(ordersList).as("There should be 1 item in the order").hasSize(1);
		Order order = ordersList.iterator().next();

		assertNotNull("We should be able to find the warehouse for this order", order.getStore().getWarehouses().get(0));
		long warehouseUidPk = order.getStore().getWarehouses().get(0).getUidPk();
		assertThat(store.getWarehouses().get(0).getUidPk()).as("Order warehouse should be same as the original product").isEqualTo(warehouseUidPk);
		assertThat(order.getRootShoppingItems()).as("There should be 1 sku in the order").hasSize(1);
		ShoppingItem orderSku = order.getRootShoppingItems().iterator().next();
		assertThat(orderSku.getSkuGuid()).as("The order sku guid should match the original sku guid").isEqualTo(productSku.getGuid());

		InventoryDto orderSkuInventory = productInventoryManagementService.getInventory(
				productSku, scenario.getWarehouse().getUidPk());

		assertNotNull("There should be inventory for the order sku in the store's warehouse", orderSkuInventory);
	}

	/**
	 * Test that if the checkout process fails, checkout actions get reversed correctly and the order is saved with the failed status.
	 */
	@Test(expected = EpSystemException.class)
	public void testFailOrderOnReversingCheckoutProcess() {
		reversibleCheckoutActions.add(failingAction);

		// construct and save new shopping cart
		final Shopper shopper = customerSession.getShopper();
		ShoppingCart shoppingCart = persisterFactory.getOrderTestPersister().persistEmptyShoppingCart(address, address, customerSession,
				scenario.getShippingOption(), scenario.getStore());

		ProductSku productSku = product.getDefaultSku();
		ShoppingItemDto dto = new ShoppingItemDto(productSku.getSkuCode(), 1);
		cartDirector.addItemToCart(shoppingCart, dto);

		assertNotNull("Shopping cart should contain items", shoppingCart.getRootShoppingItems());
		assertThat(shoppingCart.getRootShoppingItems()).as("There should be 1 item in the shopping cart").hasSize(1);

		// make new order payment
		PaymentToken paymentToken = new PaymentTokenImpl.TokenBuilder().build();
		OrderPayment templateOrderPayment = persisterFactory.getOrderTestPersister().createOrderPayment(customer, paymentToken);

		// checkout
		assertCustomerEmailEqualsShopperCustomerEmail(shopper);

		final ShoppingCartPricingSnapshot pricingSnapshot = pricingSnapshotService.getPricingSnapshotForCart(shoppingCart);
		final ShoppingCartTaxSnapshot taxSnapshot = taxSnapshotService.getTaxSnapshotForCart(shoppingCart, pricingSnapshot);

		checkoutService.checkout(shoppingCart, taxSnapshot, customerSession, templateOrderPayment);

		// only one order should have been created by the checkout service
		List<Order> ordersList = orderService.findOrderByCustomerGuid(shopper.getCustomer().getGuid(), true);
		assertThat(ordersList).as("There should be 1 item in the order").hasSize(1);
		Order order = ordersList.iterator().next();

		assertThat(FAILED).as("The order should be in the failed state after checkout reversal").isEqualTo(order.getStatus());

		assertThat(order.getRootShoppingItems()).as("There should be 1 sku in the order").hasSize(1);
		ShoppingItem orderSku = order.getRootShoppingItems().iterator().next();
		assertThat(orderSku.getSkuGuid()).as("The order sku guid should match the original sku guid").isEqualTo(productSku.getGuid());
	}

	/**
	 * Test cancelling an order.
	 */
	@Test
	public void testCancelOrder() {
		// construct and save new shopping cart
		final Shopper shopper = customerSession.getShopper();
		ShoppingCart shoppingCart = givenASimpleShoppingCart();

		assertCustomerEmailEqualsShopperCustomerEmail(shopper);

		// make new order payment
		whenCheckoutIsDone(shoppingCart);

		// only one order should have been created by the checkout service
		List<Order> ordersList = orderService.findOrderByCustomerGuid(shopper.getCustomer().getGuid(), true);
		Order order = ordersList.iterator().next();

		// two shipments should have been created
		List<OrderShipment> shipments = order.getAllShipments();
		assertThat(shipments).hasSize(1);

		// check payments
		Set<OrderPayment> payments = order.getOrderPayments();
		assertThat(payments).hasSize(1);
		OrderPayment authPayment = payments.iterator().next();
		assertThat(authPayment.getAmount().doubleValue()).isEqualTo(order.getTotal().doubleValue());

		assertTrue(order.isCancellable());
		order.setModifiedBy(getEventOriginatorHelper().getSystemOriginator());
		order = orderService.cancelOrder(order);

		assertThat(order.getStatus()).isEqualTo(CANCELLED);

		// 1 auth + 1 reverse auth payments should have been conducted
		assertThat(order.getOrderPayments()).hasSize(2);

		OrderPayment reverseAuth = null;
		for (OrderPayment payment : order.getOrderPayments()) {
			if (payment.getTransactionType().equals(REVERSE_AUTHORIZATION)) {
				assertNull(reverseAuth);
				reverseAuth = payment;
			}
		}
		assertNotNull(reverseAuth);
		assertThat(reverseAuth.getStatus()).isEqualTo(APPROVED);

	}

	/**
	 * Test canceling a shipment.
	 */
	@Test
	public void testCancelShipment() {
		// construct and save new shopping cart
		final Shopper shopper = customerSession.getShopper();
		ShoppingCart shoppingCart = givenASimpleShoppingCart();

		// make new order payment
		OrderPayment templateOrderPayment = persisterFactory.getOrderTestPersister().createOrderPayment(customer, new TokenBuilder()
				.build());

		// checkout
		assertCustomerEmailEqualsShopperCustomerEmail(shopper);

		final ShoppingCartPricingSnapshot pricingSnapshot = pricingSnapshotService.getPricingSnapshotForCart(shoppingCart);
		final ShoppingCartTaxSnapshot taxSnapshot = taxSnapshotService.getTaxSnapshotForCart(shoppingCart, pricingSnapshot);

		checkoutService.checkout(shoppingCart, taxSnapshot, customerSession, templateOrderPayment, true);

		// only one order should have been created by the checkout service
		List<Order> ordersList = orderService.findOrderByCustomerGuid(shopper.getCustomer().getGuid(), true);
		assertThat(ordersList).hasSize(1);
		Order order = ordersList.iterator().next();

		// one shipment should have been created
		List<OrderShipment> shipments = order.getAllShipments();
		assertThat(shipments).hasSize(1);

		// check payments
		Set<OrderPayment> payments = order.getOrderPayments();
		assertThat(payments).hasSize(1);
		OrderPayment authPayment = payments.iterator().next();
		assertThat(authPayment.getAmount().doubleValue()).isEqualTo(order.getTotal().doubleValue());

		PhysicalOrderShipment phShipment = order.getPhysicalShipments().iterator().next();
		assertTrue(phShipment.isCancellable());

		phShipment = orderService.cancelOrderShipment(phShipment);

		assertThat(phShipment.getShipmentStatus()).isEqualTo(OrderShipmentStatus.CANCELLED);
		assertThat(order.getStatus()).isEqualTo(CANCELLED);

		// 1 auth + 1 reverse auth payments should have been conducted
		assertThat(order.getOrderPayments()).hasSize(2);

		OrderPayment reverseAuth = null;
		for (OrderPayment payment : order.getOrderPayments()) {
			if (payment.getTransactionType().equals(REVERSE_AUTHORIZATION)) {
				assertNull(reverseAuth);
				reverseAuth = payment;
			}
		}
		assertNotNull(reverseAuth);
		assertThat(reverseAuth.getStatus()).isEqualTo(APPROVED);
	}

	/**
	 * Test augmenting the shipment total.
	 */
	@Test
	public void testAugmentShipmentTotal() {
		// construct and save new shopping cart
		final Shopper shopper = customerSession.getShopper();
		ShoppingCart shoppingCart = givenASimpleShoppingCart();

		// make new order payment
		OrderPayment templateOrderPayment = persisterFactory.getOrderTestPersister().createOrderPayment(customer, new TokenBuilder()
				.build());

		// checkout
		assertCustomerEmailEqualsShopperCustomerEmail(shopper);

		final ShoppingCartPricingSnapshot pricingSnapshot = pricingSnapshotService.getPricingSnapshotForCart(shoppingCart);
		final ShoppingCartTaxSnapshot taxSnapshot = taxSnapshotService.getTaxSnapshotForCart(shoppingCart, pricingSnapshot);

		checkoutService.checkout(shoppingCart, taxSnapshot, customerSession, templateOrderPayment, true);

		List<Order> ordersList = orderService.findOrderByCustomerGuid(shopper.getCustomer().getGuid(), true);
		assertThat(ordersList).as("only one order should have been created by the checkout service").hasSize(1);
		Order order = ordersList.iterator().next();

		order.setModifiedBy(getEventOriginatorHelper().getSystemOriginator());
		assertNotNull("Order should have a modified by value", order.getModifiedBy());

		List<OrderShipment> shipments = order.getAllShipments();
		assertThat(shipments).as("one shipment should have been created").hasSize(1);

		Set<OrderPayment> payments = order.getOrderPayments();
		assertThat(payments).as("there should be one payment").hasSize(1);
		OrderPayment authPayment = payments.iterator().next();
		assertEquals("payment total should be for full amount of order", order.getTotal().doubleValue(), authPayment.getAmount().doubleValue(), 0);

		PhysicalOrderShipment phShipment = order.getPhysicalShipments().iterator().next();

		OrderSku newProductOrderSku = getNewProductOrderSku();
		BigDecimal previousTotal = phShipment.getTotal();
		phShipment.addShipmentOrderSku(newProductOrderSku);
		assertTrue("the previous total amount should be less than the new one", previousTotal.compareTo(phShipment.getTotal()) < 0);

		PaymentResult paymentResult = paymentService.adjustShipmentPayment(phShipment);

		for (OrderPayment proccessedPayment : paymentResult.getProcessedPayments()) {
			order.addOrderPayment(proccessedPayment);
		}

		order = orderService.update(order);
		order.setModifiedBy(getEventOriginatorHelper().getSystemOriginator());
		phShipment = order.getPhysicalShipments().iterator().next();

		assertThat(phShipment.getShipmentStatus()).as("The shipment status should be inventory assigned")
				.isEqualTo(INVENTORY_ASSIGNED);
		assertThat(phShipment.getOrderShipmentType()).as("The shipment type should be pysical").isEqualTo(PHYSICAL);

		for (OrderSku sku : phShipment.getShipmentOrderSkus()) {
			assertSame("Skus in the shipment should know what shipment they are for", phShipment, sku.getShipment());
		}
		phShipment = (PhysicalOrderShipment) orderService.processReleaseShipment(phShipment);

		final int expectedPayments = 3;
		assertThat(order.getOrderPayments()).as("there should be 3 payments : 1 auth + 1 reverse auth + 1 new auth (new amount)").hasSize
				(expectedPayments);

		OrderPayment reverseAuth = null;
		OrderPayment newAuth = null;
		for (OrderPayment payment : order.getOrderPayments()) {
			if (payment.getTransactionType().equals(REVERSE_AUTHORIZATION)) {
				assertNull("There should only be a single reverse auth", reverseAuth);
				reverseAuth = payment;
			} else if (payment.getTransactionType().equals(AUTHORIZATION_TRANSACTION)) {
				if (newAuth == null) {
					newAuth = payment;
				} else if (payment.getCreatedDate().compareTo(newAuth.getCreatedDate()) > 0) {
					newAuth = payment;
				}
			}
		}
		assertNotNull("There should have been a reverse auth", reverseAuth);
		assertNotNull("There should be a new auth", newAuth);

		assertThat(newAuth.getAmount()).as("Auth should have been for the new shipment total").isEqualTo(phShipment.getTotal());
		assertThat(newAuth.getStatus()).as("New auth status should be approved").isEqualTo(APPROVED);

	}

	/**
	 * Test creating a new shipment.
	 */
	@Test
	public void testCreateNewShipment() {
		// construct new shopping cart
		final Shopper shopper = customerSession.getShopper();
		ShoppingCart shoppingCart = givenASimpleShoppingCart();

		// make new order payment
		OrderPayment templateOrderPayment = persisterFactory.getOrderTestPersister().createOrderPayment(customer, new TokenBuilder()
				.build());

		// checkout
		assertCustomerEmailEqualsShopperCustomerEmail(shopper);

		final ShoppingCartPricingSnapshot pricingSnapshot = pricingSnapshotService.getPricingSnapshotForCart(shoppingCart);
		final ShoppingCartTaxSnapshot taxSnapshot = taxSnapshotService.getTaxSnapshotForCart(shoppingCart, pricingSnapshot);

		checkoutService.checkout(shoppingCart, taxSnapshot, customerSession, templateOrderPayment, true);

		List<Order> ordersList = orderService.findOrderByCustomerGuid(shopper.getCustomer().getGuid(), true);
		assertThat(ordersList).as("Only one order should have been created by the checkout service").hasSize(1);
		Order order = ordersList.iterator().next();

		List<OrderShipment> shipments = order.getAllShipments();
		assertThat(shipments).as("one shipment should have been created").hasSize(1);

		Set<OrderPayment> payments = order.getOrderPayments();
		assertThat(payments).as("there should be one payment").hasSize(1);

		OrderPayment authPayment = payments.iterator().next();
		BigDecimal originalTotal = order.getTotal();
		assertEquals("Payment should be for the full amount of the order", originalTotal.doubleValue(), authPayment.getAmount().doubleValue(), 0);

		PhysicalOrderShipment phShipment = order.getPhysicalShipments().iterator().next();

		PhysicalOrderShipmentImpl newPhysicalShipment = new PhysicalOrderShipmentImpl();
		newPhysicalShipment.setCreatedDate(new Date());
		newPhysicalShipment.setLastModifiedDate(new Date());
		newPhysicalShipment.setOrder(order);
		newPhysicalShipment.setStatus(INVENTORY_ASSIGNED);
		newPhysicalShipment.initialize();

		OrderSku newProductOrderSku = getNewProductOrderSku();
		newPhysicalShipment.addShipmentOrderSku(newProductOrderSku);
		newPhysicalShipment.setShippingOptionCode(scenario.getShippingOption().getCode());
		newPhysicalShipment.setShippingCost(ONE);

		order.addShipment(newPhysicalShipment);
		assertTrue("the new shipment total should be > 0", ZERO.compareTo(newPhysicalShipment.getTotal()) < 0);
		assertThat(order.getPhysicalShipments()).as("the order should now have 2 physical shipments").hasSize(2);

		templateOrderPayment.setAmount(newPhysicalShipment.getTotal());

		OrderPayment lastPayment = paymentService.getAllActiveAuthorizationPayments(phShipment).iterator().next();
		assertNotNull("There should be a payment for the original shipment", lastPayment);
		assertSame("Order shipment for last payment should be the original shipment", phShipment, lastPayment.getOrderShipment());
		assertThat(phShipment.getTotal()).as("Order shipment should have a total of the original order").isEqualTo(originalTotal);

		phShipment.getOrder().setModifiedBy(getEventOriginatorHelper().getSystemOriginator());

		PaymentResult paymentResult = paymentService.initializeNewShipmentPayment(newPhysicalShipment, templateOrderPayment);

		for (OrderPayment proccessedPayment : paymentResult.getProcessedPayments()) {
			order.addOrderPayment(proccessedPayment);
		}

		order = orderService.update(order);
		order.setModifiedBy(getEventOriginatorHelper().getSystemOriginator());
		phShipment = order.getPhysicalShipments().iterator().next();
		orderService.processReleaseShipment(phShipment);

		assertThat(order.getOrderPayments()).as("There should be 2 payments: 1 auth + 1 new auth (new amount)").hasSize(2);

		OrderPayment newAuth = null;
		for (OrderPayment payment : order.getOrderPayments()) {
			if (payment.getTransactionType().equals(REVERSE_AUTHORIZATION)) {
				fail("There should not be any reverse auths");
			} else if (payment.getTransactionType().equals(AUTHORIZATION_TRANSACTION)) {
				if (newAuth == null) {
					newAuth = payment;
				} else if (payment.getCreatedDate().compareTo(newAuth.getCreatedDate()) > 0) {
					newAuth = payment;
				}
			}
		}
		assertNotNull("There should have been a new auth", newAuth);

		assertThat(newAuth.getAmount()).as("new auth total should be for physical shipment").isEqualTo(newPhysicalShipment.getTotal());
		assertThat(newAuth.getStatus()).as("new auth status should be approved").isEqualTo(APPROVED);
	}

	/**
	 * Tests that you can find products codes for skus that have been purchased by a user, given a start and end date.
	 */
	@Test
	public void testFindProductCodesPurchasedByUserAndFromDate() {
		// construct and save new shopping cart
		Order order = createOrder();

		// two shipments should have been created
		List<OrderShipment> shipments = order.getAllShipments();
		assertThat(shipments).hasSize(1);

		// Make a list of productCodes that were in the shopping cart
		List<String> shoppingCartProductCodes = new ArrayList<>();
		for (ShoppingItem orderSku : shipments.get(0).getShipmentOrderSkus()) {
			ProductSku sku = productSkuLookup.findByGuid(orderSku.getSkuGuid());
			shoppingCartProductCodes.add(sku.getProduct().getCode());
		}

		// Make sure we get that list back
		final int fourMinutesInMillis = 240000;
		PurchaseHistorySearchCriteria criteria = createPurchaseHistorySearchCriteria(new Date(currentTimeMillis() - fourMinutesInMillis),
				// from 4 minutes ago
				new Date()); // now

		List<String> purchasedProductCodes = orderService.findProductCodesPurchasedByUser(criteria);
		assertNotNull(purchasedProductCodes);

		for (String code : shoppingCartProductCodes) {
			assertTrue("ProductCode " + code + " was not found in list of purchased codes", purchasedProductCodes.contains(code));
		}
	}

	/**
	 * Tests that while searching for the products bought by a user given a start and end date, failed orders are excluded.
	 */
	@Test
	public void testFindProductCodesPurchasedByUserAndFromDateExcludingFailedOrders() {
		// construct and save new shopping cart
		Order order = createOrder();
		order.failOrder();
		order = orderService.update(order);

		// two shipments should have been created
		List<OrderShipment> shipments = order.getAllShipments();
		assertThat(shipments).hasSize(1);

		// Make sure we get that list back
		final int fourMinutesInMillis = 240000;
		PurchaseHistorySearchCriteria criteria = createPurchaseHistorySearchCriteria(new Date(currentTimeMillis() - fourMinutesInMillis),
				// from 4 minutes ago
				new Date()); // now

		List<String> purchasedProductCodes = orderService.findProductCodesPurchasedByUser(criteria);
		assertNotNull(purchasedProductCodes);
		assertTrue("The product codes for a failed order should not be retrieved.", purchasedProductCodes.isEmpty());
	}

	/**
	 * Tests that you can find products codes for skus that have been purchased by a user.
	 */
	@Test
	public void testFindProductCodesPurchasedByUser() {
		// construct and save new shopping cart
		Order order = createOrder();

		// two shipments should have been created
		List<OrderShipment> shipments = order.getAllShipments();
		assertThat(shipments).hasSize(1);

		// Make a list of productCodes that were in the shopping cart
		List<String> orderProductCodes = new ArrayList<>();
		for (OrderSku orderSku : shipments.get(0).getShipmentOrderSkus()) {
			ProductSku sku = productSkuLookup.findByGuid(orderSku.getSkuGuid());
			orderProductCodes.add(sku.getProduct().getCode());
		}

		// Make sure we get that list back
		PurchaseHistorySearchCriteria criteria = createPurchaseHistorySearchCriteria(null, //no from date
				new Date()); // now

		List<String> purchasedProductCodes = orderService.findProductCodesPurchasedByUser(criteria);
		assertNotNull(purchasedProductCodes);

		for (String code : orderProductCodes) {
			assertTrue("ProductCode " + code + " was not found in list of purchased codes", purchasedProductCodes.contains(code));
		}
	}

	/**
	 * Tests that while searching for the products bought by a user, failed orders are excluded.
	 */
	@Test
	public void testFindProductCodesPurchasedByUserExcludeFailedOrder() {
		// construct and save new shopping cart
		Order order = createOrder();
		order.failOrder();
		orderService.update(order);

		// Make sure we get that list back
		PurchaseHistorySearchCriteria criteria = createPurchaseHistorySearchCriteria(null, //no from date
				new Date()); // now

		List<String> purchasedProductCodes = orderService.findProductCodesPurchasedByUser(criteria);
		assertNotNull(purchasedProductCodes);
		assertTrue("The product codes for a failed order should not be retrieved.", purchasedProductCodes.isEmpty());
	}

	/**
	 * Test that findOrderByCustomerGuid finds regular orders but excludes failed orders.
	 */
	@Test
	public void testFindOrderByCustomerGuid() {
		Order order = createOrder();

		String customerGuid = shopper.getCustomer().getGuid();
		List<Order> orderByCustomerGuid = orderService.findOrderByCustomerGuid(customerGuid, true);
		assertThat(orderByCustomerGuid).as("There should be 1 order found").hasSize(1);
		assertThat(orderByCustomerGuid.get(0)).as("The order should be the one we created").isEqualTo(order);
		List<Order> orderByOtherCustomer = orderService.findOrderByCustomerGuid(INVALID_GUID, true);
		assertTrue("There should be no orders found when customer guid does not match.", orderByOtherCustomer.isEmpty());

		order.failOrder();
		orderService.update(order);

		orderByCustomerGuid = orderService.findOrderByCustomerGuid(customerGuid, true);
		assertTrue("There should be no orders found", orderByCustomerGuid.isEmpty());
	}

	/**
	 * Test that findOrderByCustomerGuidAndStoreCode finds regular orders but excludes failed orders.
	 */
	@Test
	public void testFindOrderByCustomerGuidAndStoreCode() {
		Order order = createOrder();

		String customerGuid = shopper.getCustomer().getGuid();
		List<Order> orderByCustomerGuid = orderService.findOrdersByCustomerGuidAndStoreCode(customerGuid, store.getCode(), true);
		assertThat(orderByCustomerGuid).as("There should be 1 order found").hasSize(1);
		assertThat(orderByCustomerGuid.get(0)).as("The order should be the one we created").isEqualTo(order);
		List<Order> orderByOtherCustomer = orderService.findOrdersByCustomerGuidAndStoreCode(INVALID_GUID, store.getCode(), true);
		assertTrue("There should be no orders found when customer guid does not match.", orderByOtherCustomer.isEmpty());

		order.failOrder();
		orderService.update(order);

		orderByCustomerGuid = orderService.findOrdersByCustomerGuidAndStoreCode(customerGuid, store.getCode(), true);
		assertTrue("There should be no orders found", orderByCustomerGuid.isEmpty());
	}

	/**
	 * Test find order by customer guid and store code without full info.
	 */
	@Test
	public void testFindOrderByCustomerGuidAndStoreCodeWithoutFullInfo() {
		Order order = createOrder();

		String customerGuid = shopper.getCustomer().getGuid();
		List<Order> retrievedOrders = orderService.findOrdersByCustomerGuidAndStoreCode(customerGuid, store.getCode(), false);
		assertThat(retrievedOrders).as("There should be 1 order found").hasSize(1);

		Order retrievedOrder = retrievedOrders.get(0);
		assertThat(retrievedOrder.getOrderNumber()).as("The order number should be the one we created").isEqualTo(order.getOrderNumber());
		assertThat(retrievedOrder.getStore().getName()).as("The order store name should be the one we created")
				.isEqualTo(order.getStore().getName());
		assertThat(retrievedOrder.getStore().getUrl()).as("The order store url should be the one we created").isEqualTo(order.getStore().getUrl());
		assertThat(retrievedOrder.getTotal()).as("The order total should be the one we created").isEqualTo(order.getTotal());
		assertThat(retrievedOrder.getCreatedDate()).as("The order created date should be the one we created").isEqualTo(order.getCreatedDate());
		assertThat(retrievedOrder.getStatus()).as("The order status should be the one we created").isEqualTo(order.getStatus());

		List<Order> orderByOtherCustomer = orderService.findOrdersByCustomerGuidAndStoreCode(INVALID_GUID, store.getCode(), false);
		assertTrue("There should be no orders found when customer guid does not match.", orderByOtherCustomer.isEmpty());
	}

	private Order createOrder() {
		ShoppingCart shoppingCart = persisterFactory.getOrderTestPersister().persistEmptyShoppingCart(address, address, customerSession,
				scenario.getShippingOption(), scenario.getStore());
		ShoppingItemDto dto = new ShoppingItemDto(product.getDefaultSku().getSkuCode(), 1);
		cartDirector.addItemToCart(shoppingCart, dto);

		// make new order payment
		OrderPayment templateOrderPayment = persisterFactory.getOrderTestPersister().createOrderPayment(customer, new TokenBuilder()
				.build());

		// checkout
		assertCustomerEmailEqualsShopperCustomerEmail(shopper);

		final ShoppingCartPricingSnapshot pricingSnapshot = pricingSnapshotService.getPricingSnapshotForCart(shoppingCart);
		final ShoppingCartTaxSnapshot taxSnapshot = taxSnapshotService.getTaxSnapshotForCart(shoppingCart, pricingSnapshot);

		checkoutService.checkout(shoppingCart, taxSnapshot, customerSession, templateOrderPayment, true);

		// only one order should have been created by the checkout service
		List<Order> ordersList = orderService.findOrderByCustomerGuid(shopper.getCustomer().getGuid(), true);
		assertThat(ordersList).hasSize(1);
		return ordersList.iterator().next();
	}

	/**
	 *
	 */
	@Test
	public void testHoldAndReleaseHoldOnOrder() {
		// construct and save new shopping cart
		final Shopper shopper = customerSession.getShopper();
		ShoppingCart shoppingCart = givenASimpleShoppingCart();

		// make new order payment
		OrderPayment templateOrderPayment = persisterFactory.getOrderTestPersister().createOrderPayment(customer, new TokenBuilder()
				.build());

		// checkout
		assertCustomerEmailEqualsShopperCustomerEmail(shopper);

		final ShoppingCartPricingSnapshot pricingSnapshot = pricingSnapshotService.getPricingSnapshotForCart(shoppingCart);
		final ShoppingCartTaxSnapshot taxSnapshot = taxSnapshotService.getTaxSnapshotForCart(shoppingCart, pricingSnapshot);

		checkoutService.checkout(shoppingCart, taxSnapshot, customerSession, templateOrderPayment, true);

		// only one order should have been created by the checkout service
		List<Order> ordersList = orderService.findOrderByCustomerGuid(shopper.getCustomer().getGuid(), true);
		assertThat(ordersList).hasSize(1);
		Order order = ordersList.iterator().next();

		// two shipments should have been created
		List<OrderShipment> shipments = order.getAllShipments();
		assertThat(shipments).hasSize(1);

		// check payments
		Set<OrderPayment> payments = order.getOrderPayments();
		assertThat(payments).hasSize(1);
		OrderPayment authPayment = payments.iterator().next();
		assertThat(authPayment.getAmount().doubleValue()).isEqualTo(order.getTotal().doubleValue());

		assertThat(order.getStatus()).isEqualTo(IN_PROGRESS);
		order = orderService.get(order.getUidPk());
		assertThat(order.getStatus()).isEqualTo(IN_PROGRESS);
	}

	/**
	 * Tests release shipment with product which is available only when in stock.
	 */
	@Test
	public void testReleaseOrderShipmentProductInStock() {
		releaseOrderShipmentWithProductAvialability(AvailabilityCriteria.AVAILABLE_WHEN_IN_STOCK);
	}

	/**
	 * Tests release of an order shipment. Scenario: 1. Create shopping cart with one product 2. Checkout the shopping cart 3.
	 *
	 * @param availabilityCriteria availability criteria
	 */
	public void releaseOrderShipmentWithProductAvialability(final AvailabilityCriteria availabilityCriteria) {
		product = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(), scenario.getCategory(),
				scenario.getWarehouse());
		product.setAvailabilityCriteria(availabilityCriteria);
		product = productService.saveOrUpdate(product);

		final int qty5 = 5;
		InventoryDto inventoryDto = productInventoryManagementService.getInventory(
				product.getDefaultSku(),
				scenario.getWarehouse().getUidPk());
		productInventoryManagementService.saveOrUpdate(inventoryDto);

		InventoryAudit inventoryAudit = buildInventoryAudit(STOCK_ADJUSTMENT, qty5 - inventoryDto.getQuantityOnHand());
		productInventoryManagementService.processInventoryUpdate(inventoryDto, inventoryAudit);
		inventoryDto = productInventoryManagementService.getInventory(inventoryDto.getSkuCode(), inventoryDto.getWarehouseUid());

		assertNotNull(inventoryDto);
		assertThat(inventoryDto.getQuantityOnHand()).isEqualTo(qty5);
		assertThat(product.getAvailabilityCriteria()).isEqualTo(availabilityCriteria);

		ShoppingCart shoppingCart = persisterFactory.getOrderTestPersister().persistEmptyShoppingCart(address, address, customerSession,
				scenario.getShippingOption(), scenario.getStore());
		ShoppingItemDto dto = new ShoppingItemDto(product.getDefaultSku().getSkuCode(), 2);
		cartDirector.addItemToCart(shoppingCart, dto);

		OrderPayment orderPayment = new OrderPaymentImpl();
		orderPayment.setPaymentMethod(PAYMENT_TOKEN);

		// checkout
		final ShoppingCartPricingSnapshot pricingSnapshot = pricingSnapshotService.getPricingSnapshotForCart(shoppingCart);
		final ShoppingCartTaxSnapshot taxSnapshot = taxSnapshotService.getTaxSnapshotForCart(shoppingCart, pricingSnapshot);

		checkoutService.checkout(shoppingCart, taxSnapshot, customerSession, orderPayment, true);

		inventoryDto = productInventoryManagementService.getInventory(
				inventoryDto.getSkuCode(), inventoryDto.getWarehouseUid());

		// check allocation status
		assertThat(inventoryDto.getAvailableQuantityInStock()).isEqualTo(qty5 - 2);
		assertThat(inventoryDto.getAllocatedQuantity()).isEqualTo(2);

		Order order = shoppingCart.getCompletedOrder();

		List<PhysicalOrderShipment> physicalShipments = order.getPhysicalShipments();
		assertThat(physicalShipments).hasSize(1);
		PhysicalOrderShipment shipment = physicalShipments.iterator().next();

		Set<OrderSku> shipmentOrderSkus = shipment.getShipmentOrderSkus();
		assertThat(shipmentOrderSkus).as("One product was checked out only").hasSize(1);
		OrderSku orderSku = shipmentOrderSkus.iterator().next();
		assertThat(orderSku.getAllocatedQuantity()).as("Quantity of two products was checked out").isEqualTo(2);
		inventoryDto = productInventoryManagementService.getInventory(
				orderSku.getSkuCode(),
				scenario.getWarehouse().getUidPk());

		// check allocation status
		assertThat(inventoryDto.getAllocatedQuantity()).as("Two allocated quantities expected").isEqualTo(2);

		order = orderService.get(order.getUidPk());

		assertThat(order.getAllShipments().iterator().next().getShipmentStatus()).isEqualTo(INVENTORY_ASSIGNED);

		order.setModifiedBy(getEventOriginatorHelper().getSystemOriginator());
		shipment = (PhysicalOrderShipment) order.getAllShipments().iterator().next();

		shipment = (PhysicalOrderShipment) orderService.processReleaseShipment(shipment);

		assertThat(shipment.getShipmentStatus()).isEqualTo(RELEASED);

		orderSku = shipment.getShipmentOrderSkus().iterator().next();
		InventoryDto inventory2 = productInventoryManagementService.getInventory(
				orderSku.getSkuCode(),
				scenario.getWarehouse().getUidPk());
		inventory2 = productInventoryManagementService.getInventory(
				inventory2.getSkuCode(), inventory2.getWarehouseUid());

		assertThat(orderSku.getAllocatedQuantity()).isEqualTo(2);
		assertThat(inventory2.getAllocatedQuantity()).isEqualTo(2);

		inventory2 = productInventoryManagementService.getInventory(
				inventory2.getSkuCode(), inventory2.getWarehouseUid());

		// The allocated quantity and quantity on hand should not be changed in orderService.processReleaseShipment,
		// we only change them in orderService.completeShipment.
		assertThat(inventory2.getAllocatedQuantity()).isEqualTo(2);
		assertThat(inventory2.getQuantityOnHand()).isEqualTo(inventoryDto.getQuantityOnHand());
	}

	private InventoryAudit buildInventoryAudit(final InventoryEventType inventoryEventType, final int quantity) {
		InventoryAudit inventoryAudit = new InventoryAuditImpl();
		inventoryAudit.setEventType(inventoryEventType);
		inventoryAudit.setQuantity(quantity);
		return inventoryAudit;
	}

	// =================== UTILITY METHODS ========================= \\

	private PurchaseHistorySearchCriteria createPurchaseHistorySearchCriteria(final Date fromDate, final Date toDate) {
		PurchaseHistorySearchCriteria criteria = new PurchaseHistorySearchCriteriaImpl();
		criteria.setUserId(shopper.getCustomer().getUserId());
		criteria.setStoreCode(store.getCode());
		criteria.setFromDate(fromDate); // from 4 minutes ago
		criteria.setToDate(toDate);
		return criteria;
	}

	private OrderSku getNewProductOrderSku() {
		Product newProduct = persisterFactory.getCatalogTestPersister().createDefaultProductWithSkuAndInventory(scenario.getCatalog(),
				scenario.getCategory(), scenario.getWarehouse());

		final OrderSku orderSku = getBeanFactory().getBean(ContextIdNames.ORDER_SKU);

		final ProductSku productSku = newProduct.getDefaultSku();

		final Price price = getBeanFactory().getBean(ContextIdNames.PRICE);
		final Money amount = Money.valueOf(BigDecimal.ONE, Currency.getInstance("USD"));
		price.setListPrice(amount);
		orderSku.setPrice(1, price);
		orderSku.setUnitPrice(BigDecimal.ONE);
		final Date now = new Date();
		orderSku.setCreatedDate(now);
		final int qty3 = 3;
		orderSku.setQuantity(qty3);
		orderSku.setSkuCode(productSku.getSkuCode());
		orderSku.setSkuGuid(productSku.getGuid());
		orderSku.setDigitalAsset(productSku.getDigitalAsset());
		orderSku.setTaxCode(newProduct.getTaxCodeOverride().getCode());
		orderSku.setTaxAmount(BigDecimal.ONE);
		orderSku.setAllocatedQuantity(qty3);

		if (productSku.getImage() != null) {
			orderSku.setImage(productSku.getImage());
		}

		orderSku.setDisplayName("product_name2");
		return orderSku;
	}

	private void assertCustomerEmailEqualsShopperCustomerEmail(final Shopper shopper) {
		assertThat(shopper.getCustomer().getEmail()).isEqualTo(customer.getEmail());
	}

	/**
	 * getEventOriginatorHelper.
	 * @return EventOriginatorHelper
	 */
	public EventOriginatorHelper getEventOriginatorHelper() {
		return getBeanFactory().getBean(ContextIdNames.EVENT_ORIGINATOR_HELPER);
	}

	/**
	 * Test that finding by criteria with a store code works as expected.
	 */
	@Test
	public void testFindBySearchCriteriaStoreCode() {
		Order order = persistOrder(product);
		OrderSearchCriteria criteria = getBeanFactory().getBean(ORDER_SEARCH_CRITERIA);
		Set<String> storeCodes = new HashSet<>();
		storeCodes.add(scenario.getStore().getCode());
		criteria.setStoreCodes(storeCodes);

		List<Order> results = orderService.findOrdersBySearchCriteria(criteria, 0, MAX_RESULTS);
		assertThat(results).as("One order should have been found").hasSize(1);
		assertThat(results.get(0)).as("The search result should be the expected order").isEqualTo(order);
	}

	/**
	 * Test that finding by criteria with a store code works as expected.
	 */
	@Test
	public void testFindBySearchCriteriaStoreCodeWithExcludedOrderStatusSetToFailedOrder() {
		createAndPersistFailedOrder(product);

		OrderSearchCriteria criteria = getBeanFactory().getBean(ORDER_SEARCH_CRITERIA);
		criteria.setExcludedOrderStatus(FAILED);
		Set<String> storeCodes = new HashSet<>();
		storeCodes.add(scenario.getStore().getCode());
		criteria.setStoreCodes(storeCodes);

		List<Order> results = orderService.findOrdersBySearchCriteria(criteria, 0, MAX_RESULTS);
		assertThat(results).as("No order should have been found").hasSize(0);
	}

	/**
	 * Test that finding by criteria with a store code works as expected.
	 */
	@Test
	public void testFindBySearchCriteriaStoreCodeWithoutExcludedOrderStatusSetToFailedOrder() {
		Order order = createAndPersistFailedOrder(product);

		OrderSearchCriteria criteria = getBeanFactory().getBean(ORDER_SEARCH_CRITERIA);
		Set<String> storeCodes = new HashSet<>();
		storeCodes.add(scenario.getStore().getCode());
		criteria.setStoreCodes(storeCodes);

		List<Order> results = orderService.findOrdersBySearchCriteria(criteria, 0, MAX_RESULTS);
		assertThat(results).as("One order should have been found").hasSize(1);
		assertThat(results.get(0)).as("The search result should be the expected order").isEqualTo(order);
	}

	private Order createAndPersistFailedOrder(final Product product) {
		Order order = persistOrder(product);
		order.failOrder();
		return orderService.update(order);
	}

	/**
	 * Test that getting the count by criteria with a store code works as expected.
	 */
	@Test
	public void testGetCountBySearchCriteriaStoreCode() {
		persistOrder(product);
		OrderSearchCriteria criteria = getBeanFactory().getBean(ORDER_SEARCH_CRITERIA);
		Set<String> storeCodes = new HashSet<>();
		storeCodes.add(scenario.getStore().getCode());
		criteria.setStoreCodes(storeCodes);

		long count = orderService.getOrderCountBySearchCriteria(criteria);
		assertThat(count).as("One order should have been found").isEqualTo(1);
	}

	/**
	 * Test persistence of the Applied Rule with the connected coupon.
	 */
	@Test
	public void testFindOrderByState() {
		// construct and save new shopping cart
		final Shopper shopper = customerSession.getShopper();
		ShoppingCart shoppingCart = givenASimpleShoppingCart();

		// make new order payment
		OrderPayment templateOrderPayment = persisterFactory.getOrderTestPersister().createOrderPayment(customer, new TokenBuilder()
				.build());

		// checkout
		assertCustomerEmailEqualsShopperCustomerEmail(shopper);

		final ShoppingCartPricingSnapshot pricingSnapshot = pricingSnapshotService.getPricingSnapshotForCart(shoppingCart);
		final ShoppingCartTaxSnapshot taxSnapshot = taxSnapshotService.getTaxSnapshotForCart(shoppingCart, pricingSnapshot);

		checkoutService.checkout(shoppingCart, taxSnapshot, customerSession, templateOrderPayment, true);

		// only one order should have been created by the checkout service
		List<Order> ordersList = orderService.findOrderByCustomerGuid(shopper.getCustomer().getGuid(), true);
		assertThat(ordersList).hasSize(1);
		final Order order = ordersList.get(0);

		try {
			List<Order> returnValues = orderService.findOrderByStatus(order.getStatus(), APPROVED,
					INVENTORY_ASSIGNED);
			assertThat(returnValues).contains(order);
		} catch (Exception e) {
			fail("Error should not occur " + e.getMessage());
		}

	}

	/**
	 * Test that an order can have failed status and be found by such.
	 */
	@Test
	public void testFindFailedOrder() {
		Order order = persistOrder(product);
		List<Order> failedOrders = orderService.findOrderByStatus(FAILED, null, null);
		assertThat(failedOrders).as("the order created should not be failed").doesNotContain(order);

		order.failOrder();
		Order updatedOrder = orderService.update(order);

		failedOrders = orderService.findOrderByStatus(FAILED, null, null);
		assertThat(failedOrders).as("the order should now be failed").contains(order);

		Order failedOrder = find(failedOrders, equalTo(updatedOrder));
		assertThat(failedOrder).as("The failed order should be the we updated").isEqualTo(updatedOrder);
		assertThat(failedOrder.getStatus()).as("Order status should be failed").isEqualTo(FAILED);

		assertTrue("The failed order needs to have shipments.", failedOrder.getAllShipments().size() > 0);
		for (OrderShipment failedOrderShipment : failedOrder.getAllShipments()) {
			assertThat(failedOrderShipment.getShipmentStatus()).as("OrderShipment status should be failed").isEqualTo(FAILED_ORDER);
		}
	}



	/**
	 * Test that an order can have failed status and be found by such.
	 */
	@Test
	public void testFindFailedOrderUids() {
		Order order = persistOrder(product);

		final long ninetyDaysInMillis = 90L * 24 * 60 * 60 * 1000;
		Date now = timeService.getCurrentTime();
		Date past = new Date(now.getTime() - ninetyDaysInMillis);

		final int maxResults = 10;
		List<Long> failedOrders = orderService.getFailedOrderUids(past, maxResults);
		assertTrue("There should be no failed orders", failedOrders.isEmpty());

		failedOrders = orderService.getFailedOrderUids(now, maxResults);
		assertTrue("There should be no failed orders", failedOrders.isEmpty());

		order.failOrder();
		order = orderService.update(order);

		failedOrders = orderService.getFailedOrderUids(past, maxResults);
		assertTrue("There should be no failed orders", failedOrders.isEmpty());

		failedOrders = orderService.getFailedOrderUids(now, maxResults);
		assertThat(failedOrders).as("There should be one order returned").hasSize(1);

		Long failedOrderUid = failedOrders.get(0);
		assertThat(failedOrderUid.longValue()).as("The failed order should be the we updated").isEqualTo(order.getUidPk());
	}

	private Order persistOrder(final Product product) {
		Order order = persisterFactory.getOrderTestPersister().createOrderWithSkus(scenario.getStore(), product.getDefaultSku());
		assertThat(order.getStatus()).as("Order status should be in progress").isEqualTo(IN_PROGRESS);
		return order;
	}

	/**
	 * Tests {@link OrderService#getFailedOrderUids(Date, int)} to ensure it does not return more results than it is supposed to be returning.
	 */
	@Test
	public void testFindFailedOrderUidsMaxResults() {
		final int maxResults = 5;
		final int numOrders = 10;
		for (int i = 0; i < numOrders; ++i) {
			createAndPersistFailedOrder(product);
		}
		Date now = timeService.getCurrentTime();
		List<Long> failedOrders = orderService.getFailedOrderUids(now, maxResults);
		assertThat(failedOrders).as("The method should return exactly maxResults results").hasSize(maxResults);
	}

	/**
	 * Tests {@link OrderService#deleteOrders(List)} to ensure it cleans all the associations as well.
	 */
	@Test
	public void testDeleteOrder() {
		Order order = persistOrder(product);
		assertExpectedPersistedInstances(1, OrderImpl.class);
		assertExpectedPersistedInstances(1, OrderSkuImpl.class);
		assertExpectedPersistedInstances(1, OrderPaymentImpl.class);
		assertExpectedPersistedInstances(1, AbstractOrderShipmentImpl.class);
		// 2 Order events are sent: create and release
		assertExpectedPersistedInstances(2, OrderEventImpl.class);
		assertExpectedPersistedInstances(2, OrderAddressImpl.class);

		orderService.deleteOrders(Arrays.asList(order.getUidPk()));

		assertExpectedPersistedInstances(0, OrderImpl.class);
		assertExpectedPersistedInstances(0, OrderSkuImpl.class);
		assertExpectedPersistedInstances(0, OrderPaymentImpl.class);
		assertExpectedPersistedInstances(0, AbstractOrderShipmentImpl.class);
		assertExpectedPersistedInstances(0, OrderEventImpl.class);
		assertExpectedPersistedInstances(0, OrderAddressImpl.class);
	}

	private long getCount(final Class<?> entityClass) {
		PersistenceEngine persistenceEngine = getBeanFactory().getBean(ContextIdNames.PERSISTENCE_ENGINE);
		String entityName = entityClass.getSimpleName();
		String query = "SELECT COUNT(o.uidPk) FROM " + entityName + " o";
		List<Long> retrieve = persistenceEngine.retrieve(query);
		return retrieve.get(0);
	}

	private void assertExpectedPersistedInstances(final long count, final Class<?> entityClass) {
		assertThat(getCount(entityClass)).as("The number of persisted instances of " + entityClass + " should be as expected").isEqualTo(count);
	}

	/**
	 * Test finding the latest order GUID by cart order GUID when no cart orders exist.
	 */
	@Test
	public void testFindLatestOrderGuidByCartOrderGuidWhenNoCartOrdersExist() {
		// create an order with no cart order GUID assigned
		persistOrder(product);
		String result = orderService.findLatestOrderGuidByCartOrderGuid(NON_EXISTENT_CART_ORDER_GUID);
		assertNull("There should be no order guid returned.", result);
	}

	/**
	 * Test finding latest order GUID by cart order GUID when many orders exist for a cart order.
	 */
	@Test
	public void testFindLatestOrderGuidByCartOrderGuidWhenManyOrdersExistForACartOrder() {
		Order firstOrder = createOrderWithCartOrderGuid(product, CART_ORDER_GUID);
		Order secondOrder = createOrderWithCartOrderGuid(product, CART_ORDER_GUID);
		Order thirdOrder = createOrderWithCartOrderGuid(product, CART_ORDER_GUID);

		String result = orderService.findLatestOrderGuidByCartOrderGuid(CART_ORDER_GUID);
		assertFalse("The returned order guid should not equal the first order created.", firstOrder.getGuid().equals(result));
		assertFalse("The returned order guid should not equal the second order created.", secondOrder.getGuid().equals(result));
		assertThat(result).as("The order guid should be the same as the last order created.").isEqualTo(thirdOrder.getGuid());
	}

	/**
	 * Test finding an order by cart order GUID will not return an order that does not match the cart order GUID.
	 */
	@Test
	public void testFindLatestOrderGuidByCartOrderGuidWhenManyOrdersExistForACartOrderButDoesNotMatchCartOrderGuidArgument() {
		createOrderWithCartOrderGuid(product, CART_ORDER_GUID);
		String result = orderService.findLatestOrderGuidByCartOrderGuid(DIFFERENT_CART_ORDER_GUID);
		assertNull("There should be no order guid returned.", result);
	}

	/**
	 * Tests {@link OrderService#findOrderNumbersByCustomerGuid(String, String)} for the main flow.
	 */
	@Test
	public void testFindOrderNumbersByCustomerGuid() {
		String customerGuid = customer.getGuid();
		String storeCode = store.getCode();

		List<String> orderNumbersBefore = orderService.findOrderNumbersByCustomerGuid(storeCode, customerGuid);
		assertTrue("No orders should be found as none is created yet.", orderNumbersBefore.isEmpty());

		Order order = createOrder();

		List<String> orderNumbers = orderService.findOrderNumbersByCustomerGuid(storeCode, customerGuid);
		assertThat(orderNumbers).as("There should be exactly one order found.").hasSize(1);
		assertTrue("The order number should match the one we just created.", orderNumbers.contains(order.getOrderNumber()));
	}

	/**
	 * Tests {@link OrderService#findOrderNumbersByCustomerGuid(String, String)} for the main flow.
	 */
	@Test
	public void testFindOrderNumbersByCustomerGuidInvalidCustomerGuid() {
		String storeCode = store.getCode();
		createOrder();

		List<String> orderNumbers = orderService.findOrderNumbersByCustomerGuid(storeCode, "INVALID CUSTOMER GUID");
		assertTrue("No order should be found, since the customer guid does not match the GUID of the order's owner",
				orderNumbers.isEmpty());
	}

	/**
	 * Tests {@link OrderService#findOrderNumbersByCustomerGuid(String, String)} for the main flow.
	 */
	@Test
	public void testFindOrderNumbersByCustomerGuidInvalidStoreCode() {
		String customerGuid = customer.getGuid();
		createOrder();

		List<String> orderNumbers = orderService.findOrderNumbersByCustomerGuid("INVALID_STORE_CODE", customerGuid);
		assertTrue("No order should be found, since the store code does not match that of the order.",
				orderNumbers.isEmpty());
	}



	/**
	 * This test currently fails because the isSanitized variable is @Transient and its state is lost between the Merge operation
	 * and when OpenJPA flushes the tx.  As a result, there's no way to ensure that updates cannot indirectly modify an OrderPayment
	 * (including change the credit card number) and that encrypted credit card number possibly being persisted if the user
	 * insists on bypassing the OrderService.
	 * <p>
	 * On the other hand, a) users should not be bypassing the OrderService.update() method and b) I can't think of any valid reasons
	 * why anyone should be modifying OrderPayments anyways.  Deleting, maybe.  But Modifying?
	 */
	@DirtiesDatabase
	@Ignore
	@Test(expected = TransactionSystemException.class)
	public void ensureThatDirectlyUpdatingPaymentsWithoutGoingThroughTheOrderServiceIsStrictlyForbidden() {
		Order order = createOrder();
		order.getOrderPayment();

		//  Simulate an update through something other than the OrderService
		PersistenceSession session = persistenceEngine.getPersistenceSession();
		try {
			Transaction tx = session.beginTransaction();
			try {
				persistenceEngine.update(order);
			} finally {
				if (!tx.isRollbackOnly()) {
					tx.commit();
				} else {
					tx.rollback();
				}
			}
		} finally {
			session.close();
		}
	}

	@DirtiesDatabase
	@Test
	public void verifyProcessOrderShipmentPaymentHappyPath() {
		// Given
		Order order = createOrder();
		OrderShipment orderShipment = order.getAllShipments().get(0);

		order.setModifiedBy(getEventOriginatorHelper().getSystemOriginator());
		orderService.processReleaseShipment(orderShipment);

		// When
		PaymentResult result = orderService.processOrderShipmentPayment(orderShipment.getShipmentNumber());

		// Then
		OrderPayment origAuth = order.getOrderPayment();
		OrderPayment capture = result.getProcessedPayments().iterator().next();

		assertThat(result.getProcessedPayments()).as("One payments should have been processed").hasSize(1);
		assertThat(capture.getReferenceId()).as("Original auth should now be captured").isEqualTo(origAuth.getReferenceId());
		assertThat(capture.getTransactionType()).as("Process payment should have captured").isEqualTo(CAPTURE_TRANSACTION);
	}

	@DirtiesContext
	@Test(expected = EpSystemException.class)
	public void verifyExceptionThrownWhenCamelUnavailableOnOrderCreation() throws Exception {
		camelContext.stop();
		createOrder();
	}

	@Test
	public void messageDeliveredToJmsQueueOnCheckout() throws Exception {
		camelContext.addRoutes(new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				from(orderEventOutgoingEndpoint)
						.unmarshal(eventMessageDataFormat)
						.to("mock:orders/events");
			}
		});

		final NotifyBuilder notifyBuilder = new NotifyBuilder(camelContext)
				.whenDone(1)
				.wereSentTo("mock:orders/events")
				.create();

		final Order order = createOrder();

		assertTrue(notifyBuilder.matches(1, TimeUnit.SECONDS));

		mockOrderEventEndpoint.message(0)
				.body(EventMessage.class)
				.isEqualTo(eventMessageFactory.createEventMessage(OrderEventType.ORDER_CREATED, order.getGuid()));
		mockOrderEventEndpoint.assertIsSatisfied();
	}

	@Test
	@DirtiesDatabase
	public void heldOrderCreatedAndReleasedInDetachedState() {
		// Given
		ReversibleCheckoutAction orderHolderAction = givenACheckoutActionThatHoldsTheOrder();

		final Shopper shopper = customerSession.getShopper();
		ShoppingCart shoppingCart = givenASimpleShoppingCart();

		// When
		whenCheckoutIsDone(shoppingCart);

		// Then
		// only one order should have been created by the checkout
		List<Order> ordersList = orderService.findOrderByCustomerGuid(shopper.getCustomer().getGuid(), true);
		assertThat(ordersList).hasSize(1);
		Order order = ordersList.iterator().next();

		// one shipment should have been created
		List<OrderShipment> shipments = order.getAllShipments();
		assertThat(shipments).hasSize(1);

		// Order is on hold
		assertThat(order.getStatus()).isEqualTo(ONHOLD);

		// Detach and Release order
		em.detach(order);

		eventOriginatorHelper = new EventOriginatorHelperImpl();
		order.setModifiedBy(eventOriginatorHelper.getSystemOriginator());
		orderService.releaseOrder(order);

		// Order is in progress
		order = orderService.get(order.getUidPk());
		assertThat(order.getStatus()).isEqualTo(IN_PROGRESS);

		// Clean up
		assertThat(reversibleCheckoutActions.contains(orderHolderAction));
		cleanUpReversibleCheckoutAction(orderHolderAction);
	}

	private void cleanUpReversibleCheckoutAction(final ReversibleCheckoutAction actionToRemove) {
		reversibleCheckoutActions.remove(actionToRemove);
	}

	private void whenCheckoutIsDone(final ShoppingCart shoppingCart) {
		OrderPayment templateOrderPayment = persisterFactory.getOrderTestPersister().createOrderPayment(customer, new PaymentTokenImpl.TokenBuilder()
				.build());

		// checkout
		final ShoppingCartPricingSnapshot pricingSnapshot = pricingSnapshotService.getPricingSnapshotForCart(shoppingCart);
		final ShoppingCartTaxSnapshot taxSnapshot = taxSnapshotService.getTaxSnapshotForCart(shoppingCart, pricingSnapshot);

		checkoutService.checkout(shoppingCart, taxSnapshot, customerSession, templateOrderPayment, true);
	}

	private ShoppingCart givenASimpleShoppingCart() {
		ShoppingCart shoppingCart = persisterFactory.getOrderTestPersister().persistEmptyShoppingCart(address, address, customerSession,
				scenario.getShippingOption(), scenario.getStore());
		ShoppingItemDto shoppingItemDto = new ShoppingItemDto(product.getDefaultSku().getSkuCode(), 1);
		cartDirector.addItemToCart(shoppingCart, shoppingItemDto);
		return shoppingCart;
	}

	private ReversibleCheckoutAction givenACheckoutActionThatHoldsTheOrder() {
		AlwaysHoldCheckoutAction orderHolderAction = new AlwaysHoldCheckoutAction();
		orderHolderAction.setOrderService(orderService);

		ReversibleCheckoutAction commitOrderTaxCheckoutAction = getBeanFactory().getBean("commitOrderTaxCheckoutAction");
		int firstIndex = reversibleCheckoutActions.indexOf(commitOrderTaxCheckoutAction);
		ReversibleCheckoutAction buffer = reversibleCheckoutActions.set(firstIndex, orderHolderAction);

		for (int i = firstIndex + 1; i < reversibleCheckoutActions.size(); i++) {
			buffer = reversibleCheckoutActions.set(i, buffer);
		}

		return orderHolderAction;
	}

	private Order createOrderWithCartOrderGuid(final Product product, final String cartOrderGuid) {
		Order order = persistOrder(product);
		order.setCartOrderGuid(cartOrderGuid);
		return orderService.update(order);
	}

}
