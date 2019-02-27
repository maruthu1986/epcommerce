/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.service.shoppingcart.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerAddress;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.customer.impl.CustomerAddressImpl;
import com.elasticpath.domain.customer.impl.CustomerImpl;
import com.elasticpath.domain.event.EventOriginatorHelper;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.impl.OrderAddressImpl;
import com.elasticpath.domain.order.impl.OrderImpl;
import com.elasticpath.domain.rules.AppliedRule;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.PromotionRecordContainer;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingCartTaxSnapshot;
import com.elasticpath.domain.shoppingcart.impl.ShipmentTypeShoppingCartVisitor;
import com.elasticpath.domain.shoppingcart.impl.ShoppingCartImpl;
import com.elasticpath.domain.shoppingcart.impl.ShoppingCartMementoImpl;
import com.elasticpath.domain.store.Store;
import com.elasticpath.plugin.tax.domain.TaxExemption;
import com.elasticpath.plugin.tax.domain.impl.TaxExemptionImpl;
import com.elasticpath.service.cartorder.CartOrderService;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.order.OrderService;
import com.elasticpath.service.order.impl.ShoppingItemHasRecurringPricePredicate;
import com.elasticpath.service.rules.RuleService;
import com.elasticpath.service.shoppingcart.OrderSkuFactory;
import com.elasticpath.test.BeanFactoryExpectationsFactory;
import com.elasticpath.test.factory.TestCustomerProfileFactory;
import com.elasticpath.test.factory.TestShopperFactory;
import com.elasticpath.test.factory.TestShoppingCartFactory;

/**
 * Test that {@link OrderFactoryImpl} behaves as expected.
 */
public class OrderFactoryImplTest {

	@org.junit.Rule
	public JUnitRuleMockery context = new JUnitRuleMockery();

	private static final String CART_ORDER_GUID = "cart_order_guid";
	private static final Locale LOCALE = Locale.CANADA;
	private static final String RULE_DELETED_MESSAGE = "Referenced rule was deleted";

	private final BeanFactory beanFactory = context.mock(BeanFactory.class);
	private final OrderService orderService = context.mock(OrderService.class);
	private final CartOrderService cartOrderService = context.mock(CartOrderService.class);
	private final TimeService timeService = context.mock(TimeService.class);
	private final CustomerService customerService = context.mock(CustomerService.class);
	private final OrderSkuFactory orderSkuFactory = context.mock(OrderSkuFactory.class);
	private final ProductSkuLookup productSkuLookup = context.mock(ProductSkuLookup.class);
	private final RuleService ruleService = context.mock(RuleService.class);
	private final AppliedRule.Visitor appliedRuleVisitor = appliedRule -> { };

	private final BeanFactoryExpectationsFactory beanExpectations = new BeanFactoryExpectationsFactory(context, beanFactory);

	private OrderFactoryImpl orderFactory;
	private CustomerSession customerSession;
	private Store store;

	/**
	 * Setup required for each test.
	 *
	 * @throws Exception in case of error
	 */
	@Before
	public void setUp() throws Exception {
		orderFactory = new OrderFactoryImpl();
		orderFactory.setBeanFactory(beanFactory);
		orderFactory.setOrderService(orderService);
		orderFactory.setCartOrderService(cartOrderService);
		orderFactory.setTimeService(timeService);
		orderFactory.setOrderSkuFactory(orderSkuFactory);
		orderFactory.setRuleService(ruleService);
		orderFactory.setAppliedRuleVisitor(appliedRuleVisitor);

		customerSession = context.mock(CustomerSession.class);
		store = context.mock(Store.class);

		final EventOriginatorHelper eventOriginatorHelper = context.mock(EventOriginatorHelper.class);
		context.checking(new Expectations() {
			{
				ignoring(eventOriginatorHelper).getCustomerOriginator(with(any(Customer.class)));
				ignoring(cartOrderService).findByShoppingCartGuid(with(any(String.class))); will(returnValue(null));
				ignoring(orderSkuFactory);

				allowing(timeService).getCurrentTime();
				will(returnValue(new Date()));

				allowing(customerSession).getCurrency();
				will(returnValue(Currency.getInstance("CAD")));

				allowing(customerSession).getLocale();
				will(returnValue(LOCALE));

				allowing(store).getCode(); will(returnValue("store"));
			}
		});

		final ShoppingItemHasRecurringPricePredicate recurringPricePredicate = new ShoppingItemHasRecurringPricePredicate();
		final ShipmentTypeShoppingCartVisitor shoppingCartVisitor = new ShipmentTypeShoppingCartVisitor(recurringPricePredicate, productSkuLookup);

		beanExpectations.allowingBeanFactoryGetBean(ContextIdNames.EVENT_ORIGINATOR_HELPER, eventOriginatorHelper);
		beanExpectations.allowingBeanFactoryGetBean(ContextIdNames.CUSTOMER_SERVICE, customerService);
		beanExpectations.allowingBeanFactoryGetBean(ContextIdNames.SHOPPING_CART_MEMENTO, ShoppingCartMementoImpl.class);
		beanExpectations.allowingBeanFactoryGetBean(ContextIdNames.ORDER_ADDRESS, OrderAddressImpl.class);
		beanExpectations.allowingBeanFactoryGetBean(ContextIdNames.SHIPMENT_TYPE_SHOPPING_CART_VISITOR, shoppingCartVisitor);

	}

	@After
	public void tearDown() {
		beanExpectations.close();
	}

	/**
	 * Test that an anonymous customer gets profile information copied from the billing address and tax exemption
	 * information copied from the shopping cart.
	 */
	@Test
	public void testUpdateAnonymousCustomer() {
		final Customer customer = createCustomer();
		customer.setAnonymous(true);

		ignoreCustomerSessionInteractions();

		final ShoppingCart shoppingCart = createShoppingCart();
		final ShoppingCartTaxSnapshot pricingSnapshot = createShoppingCart();

		CustomerAddress billingAddress = addBillingAddressToCart(shoppingCart);
		TaxExemption taxExemption = addTaxExemptionToCart(shoppingCart);

		setupOrder();

		context.checking(new Expectations() {
			{
				allowing(ruleService).findByUids(Collections.emptySet());
				will(returnValue(Collections.emptyList()));

				oneOf(cartOrderService).getCartOrderGuidByShoppingCartGuid(shoppingCart.getGuid());
				will(returnValue(CART_ORDER_GUID));

				oneOf(customerService).update(customer); will(returnValue(customer));
			}
		});

		Order newOrder = orderFactory.createAndPersistNewEmptyOrder(customer, customerSession, shoppingCart, false, false);
		newOrder = orderFactory.fillInNewOrderFromShoppingCart(newOrder, customer, customerSession, shoppingCart, pricingSnapshot);
		assertEquals("The customer first name should have been set", billingAddress.getFirstName(), newOrder.getCustomer().getFirstName());
		assertEquals("The customer last name should have been set", billingAddress.getLastName(), newOrder.getCustomer().getLastName());
		assertEquals("The customer phone number should have been set", billingAddress.getPhoneNumber(), newOrder.getCustomer().getPhoneNumber());
		assertEquals("The tax exemption ID should have been set", taxExemption.getExemptionId(), newOrder.getTaxExemption().getExemptionId());
		assertEquals("The tax exemption data should have been set",
				taxExemption.getData("data1Key"), newOrder.getTaxExemption().getData("data1Key"));
	}

	/**
	 * Test that existing profile information on an anonymous customer doesn't get overwritten.
	 */
	@Test
	public void testUpdateAnonymousCustomerWithExistingProfile() {
		final Customer customer = createCustomer();
		customer.setAnonymous(true);
		customer.setFirstName("Jean-Luc");
		customer.setLastName("Picard");
		customer.setPhoneNumber("555-639-8436");

		ignoreCustomerSessionInteractions();
		final ShoppingCart shoppingCart = createShoppingCart();
		addBillingAddressToCart(shoppingCart);

		setupOrder();

		context.checking(new Expectations() {
			{
				oneOf(cartOrderService).getCartOrderGuidByShoppingCartGuid(shoppingCart.getGuid());
				will(returnValue(CART_ORDER_GUID));

				never(customerService).update(customer);
			}
		});

		Order newOrder = orderFactory.createAndPersistNewEmptyOrder(customer, customerSession, shoppingCart, false, false);
		assertEquals("The customer first name should have been preserved", "Jean-Luc", newOrder.getCustomer().getFirstName());
		assertEquals("The customer last name should have been preserved", "Picard", newOrder.getCustomer().getLastName());
		assertEquals("The customer phone number should have been preserved", "555-639-8436", newOrder.getCustomer().getPhoneNumber());
	}

	@Test
	public void testUpdateAnonymousCustomerWithNullBillingAddress() {
		final Customer customer = createCustomer();
		customer.setAnonymous(true);
		ignoreCustomerSessionInteractions();
		final ShoppingCart shoppingCart = createShoppingCart();
		setupOrder();

		context.checking(new Expectations() {
			{
				oneOf(cartOrderService).getCartOrderGuidByShoppingCartGuid(shoppingCart.getGuid());
				will(returnValue(CART_ORDER_GUID));

				never(customerService).update(customer);
			}
		});

		Order newOrder = orderFactory.createAndPersistNewEmptyOrder(customer, customerSession, shoppingCart, false, false);
		assertNull("The customer first name should be null", newOrder.getCustomer().getFirstName());
		assertNull("The customer last name should be null", newOrder.getCustomer().getLastName());
		assertNull("The customer phone number should be null", newOrder.getCustomer().getPhoneNumber());
	}

	@SuppressWarnings("PMD.AvoidUsingHardCodedIP")
	@Test
	public void verifyOrderIsCreatedWithIpAddress() throws Exception {
		context.checking(new Expectations() {
			{
				ignoring(customerSession).getShopper();
				allowing(customerSession).getLocale();
				will(returnValue(Locale.CANADA));
			}
		});
		final ShoppingCart shoppingCart = createShoppingCart();
		final Customer customer = createCustomer();

		final String expectedIpAddress = "1.2.3.4";

		setupOrder();

		context.checking(new Expectations() {
			{
				oneOf(cartOrderService).getCartOrderGuidByShoppingCartGuid(shoppingCart.getGuid());
				will(returnValue(CART_ORDER_GUID));

				oneOf(customerSession).getIpAddress();
				will(returnValue(expectedIpAddress));
			}
		});

		final Order populatedOrder = orderFactory.createAndPersistNewEmptyOrder(
				customer,
				customerSession,
				shoppingCart,
				false,
				false
		);

		assertEquals("IP Address incorrectly set on Order", expectedIpAddress, populatedOrder.getIpAddress());
	}

	@Test
	public void verifyOrderIsCreatedWithAppliedRules() throws Exception {
		final Customer customer = createCustomer();
		customer.setAnonymous(true);

		ignoreCustomerSessionInteractions();

		final ShoppingCart shoppingCart = createShoppingCart();
		final ShoppingCartTaxSnapshot taxSnapshot = context.mock(ShoppingCartTaxSnapshot.class);
		final ShoppingCartPricingSnapshot pricingSnapshot = context.mock(ShoppingCartPricingSnapshot.class);

		final Long ruleId1 = 1L;
		final Long ruleId2 = 2L;
		final Long ruleId3 = 3L; // simulates the scenario where this Rule no longer exists or applies

		final Rule rule1 = createRule(ruleId1);
		final Rule rule2 = createRule(ruleId2);

		final AppliedRule expectedAppliedRule1 = context.mock(AppliedRule.class, "AppliedRule 1");
		final AppliedRule expectedAppliedRule2 = context.mock(AppliedRule.class, "AppliedRule 2");
		final AppliedRule expectedAppliedRule3 = context.mock(AppliedRule.class, "AppliedRule 3");

		setupOrder();

		context.checking(new Expectations() {
			{
				allowing(cartOrderService).getCartOrderGuidByShoppingCartGuid(shoppingCart.getGuid());
				will(returnValue(CART_ORDER_GUID));

				allowing(customerService).update(customer); will(returnValue(customer));

				allowing(taxSnapshot).getShoppingCartPricingSnapshot();
				will(returnValue(pricingSnapshot));

				final PromotionRecordContainer promotionRecordContainer = context.mock(PromotionRecordContainer.class);

				allowing(pricingSnapshot).getPromotionRecordContainer();
				will(returnValue(promotionRecordContainer));

				final Collection<Long> ruleIds = ImmutableSet.of(ruleId1, ruleId2, ruleId3);

				oneOf(promotionRecordContainer).getAppliedRules();
				will(returnValue(ruleIds));

				oneOf(ruleService).findByUids(ruleIds);
				will(returnValue(ImmutableList.of(rule1, rule2)));

				atLeast(1).of(beanFactory).getBean(ContextIdNames.APPLIED_RULE);
				will(onConsecutiveCalls(
						returnValue(expectedAppliedRule1), returnValue(expectedAppliedRule2), returnValue(expectedAppliedRule3)));

				oneOf(expectedAppliedRule1).initialize(rule1, LOCALE);
				oneOf(expectedAppliedRule1).accept(appliedRuleVisitor);

				oneOf(expectedAppliedRule2).initialize(rule2, LOCALE);
				oneOf(expectedAppliedRule2).accept(appliedRuleVisitor);

				oneOf(expectedAppliedRule3).setRuleUid(ruleId3);
				oneOf(expectedAppliedRule3).setRuleCode(RULE_DELETED_MESSAGE);
				oneOf(expectedAppliedRule3).setRuleName(RULE_DELETED_MESSAGE);
			}
		});

		final Order newOrder = orderFactory.createAndPersistNewEmptyOrder(customer, customerSession, shoppingCart, false, false);
		final Order populatedOrder = orderFactory.fillInNewOrderFromShoppingCart(newOrder, customer, customerSession, shoppingCart, taxSnapshot);

		assertThat(populatedOrder.getAppliedRules())
				.containsExactlyInAnyOrder(expectedAppliedRule1, expectedAppliedRule2, expectedAppliedRule3);
	}

	private Rule createRule(final Long ruleId) {
		final Rule rule = context.mock(Rule.class, "Rule " + ruleId);

		context.checking(new Expectations() {
			{
				allowing(rule).getUidPk();
				will(returnValue(ruleId));
			}
		});

		return rule;
	}

	private Customer createCustomer() {
		final CustomerImpl customer = new CustomerImpl();
		customer.setCustomerProfileAttributes(new TestCustomerProfileFactory().getProfile());

		return customer;
	}

	private ShoppingCartImpl createShoppingCart() {
		final Shopper shopper = TestShopperFactory.getInstance().createNewShopperWithMemento();
		final ShoppingCartImpl cart = TestShoppingCartFactory.getInstance().createNewCartWithMemento(shopper, store);
		cart.setCustomerSession(customerSession);
		return cart;
	}

	private CustomerAddress addBillingAddressToCart(final ShoppingCart shoppingCart) {
		final CustomerAddress billingAddress = new CustomerAddressImpl();
		billingAddress.setFirstName("James");
		billingAddress.setLastName("Kirk");
		billingAddress.setPhoneNumber("555-873-5867");
		shoppingCart.setBillingAddress(billingAddress);
		return billingAddress;
	}

	private TaxExemption addTaxExemptionToCart(final ShoppingCart shoppingCart) {
		final TaxExemption taxExemption = new TaxExemptionImpl();
		taxExemption.setExemptionId("EXEMPT123");
		taxExemption.addData("data1Key", "data1Value");
		shoppingCart.setTaxExemption(taxExemption);
		return taxExemption;
	}

	private void setupOrder() {
		final Order order = new OrderImpl();
		context.checking(new Expectations() {
			{
				allowing(beanFactory).getBean(ContextIdNames.ORDER); will(returnValue(order));
				allowing(orderService).add(order); will(returnValue(order));
			}
		});
	}

	private void ignoreCustomerSessionInteractions() {
		context.checking(new Expectations() {
			{
				ignoring(customerSession);
			}
		});
	}

}
