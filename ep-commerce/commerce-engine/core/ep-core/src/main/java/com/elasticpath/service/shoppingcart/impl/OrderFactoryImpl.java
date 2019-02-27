/**
 * Copyright (c) Elastic Path Software Inc., 2017
 */
package com.elasticpath.service.shoppingcart.impl;

import static java.lang.String.format;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Sets;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.event.EventOriginatorHelper;
import com.elasticpath.domain.order.ElectronicOrderShipment;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderAddress;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderShipmentStatus;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.domain.order.ServiceOrderShipment;
import com.elasticpath.domain.rules.AppliedRule;
import com.elasticpath.domain.rules.AppliedRule.Visitor;
import com.elasticpath.domain.rules.Rule;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingCartTaxSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.domain.shoppingcart.impl.ShipmentTypeShoppingCartVisitor;
import com.elasticpath.service.cartorder.CartOrderService;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.customer.CustomerService;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.order.OrderService;
import com.elasticpath.service.rules.RuleService;
import com.elasticpath.service.shoppingcart.OrderFactory;
import com.elasticpath.service.shoppingcart.OrderSkuFactory;
import com.elasticpath.service.shoppingcart.ShoppingItemToPricingSnapshotFunction;
import com.elasticpath.service.shoppingcart.actions.impl.MissingShippingOptionException;
import com.elasticpath.service.tax.DiscountApportioningCalculator;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;

/**
 * Creates Orders from ShoppingCarts.
 */
@SuppressWarnings("PMD.GodClass")
public class OrderFactoryImpl implements OrderFactory {

	private static final String RULE_DELETED_MESSAGE = "Referenced rule was deleted";

	private OrderService orderService;
	private OrderSkuFactory orderSkuFactory;
	private BeanFactory beanFactory;
	private RuleService ruleService;
	private CartOrderService cartOrderService;
	private TimeService timeService;

	/** Hook point for alternate logging applied rule strategies. */
	private Visitor appliedRuleVisitor;
	private ProductSkuLookup productSkuLookup;
	private DiscountApportioningCalculator discountCalculator;

	@Override
	public Order createAndPersistNewEmptyOrder(
			final Customer customer, final CustomerSession customerSession, final ShoppingCart shoppingCart,
			final boolean isOrderExchange,
			final boolean awaitExchangeCompletion) {

		Order order = getBean(ContextIdNames.ORDER);
		order.setCreatedDate(getTimeService().getCurrentTime());
		order.setIpAddress(customerSession.getIpAddress());
		order.setCurrency(customerSession.getCurrency());
		order.setLocale(customerSession.getLocale());
		order.setStore(shoppingCart.getStore());
		order.setCustomer(customer);

		if (isOrderExchange) {
			order.setExchangeOrder(Boolean.TRUE);
			if (awaitExchangeCompletion) {
				order = getOrderService().awaitExchnageCompletionForOrder(order);
			}
		}

		setCartOrderGuidOnOrder(order, shoppingCart);

		return getOrderService().add(order);
	}

	@Override
	public Order fillInNewOrderFromShoppingCart(final Order newOrder,
												final Customer customer,
												final CustomerSession customerSession,
												final ShoppingCart shoppingCart,
												final ShoppingCartTaxSnapshot pricingSnapshot) {
		fillInOrderDetails(newOrder, shoppingCart, pricingSnapshot, customerSession, customer, false, false);
		return newOrder;
	}

	@Override
	public Order fillInNewExchangeOrderFromShoppingCart(final Order newOrder,
														final Customer customer,
														final CustomerSession customerSession,
														final ShoppingCart shoppingCart,
														final ShoppingCartTaxSnapshot pricingSnapshot,
														final boolean awaitExchangeCompletion,
														final OrderReturn exchange) {
		newOrder.setExchange(exchange);
		fillInOrderDetails(newOrder, shoppingCart, pricingSnapshot, customerSession, customer, true, awaitExchangeCompletion);
		return newOrder;
	}

	/**
	 * Sets the associated cart order GUID on the given order.
	 *
	 * @param order the order
	 * @param shoppingCart the shopping cart
	 */
	protected void setCartOrderGuidOnOrder(final Order order, final ShoppingCart shoppingCart) {
		final String cartOrderGuid = cartOrderService.getCartOrderGuidByShoppingCartGuid(shoppingCart.getGuid());
		if (cartOrderGuid != null) {
			order.setCartOrderGuid(cartOrderGuid);
		}
	}

	/**
	 * Populates all the fields of the order. Creates order shipments and sets the corresponding order skus.
	 *
	 * @param order order to populate fields on
	 * @param shoppingCart shopping cart to get fields from
	 * @param pricingSnapshot the shopping cart pricing snapshot
	 * @param customerSession used to populate the order with details from the customer's current shopping session
	 * @param customer customer
	 * @param isExchangeOrder true if order is an exchange. false otherwise
	 * @param awaitExchangeCompletion true if order is awaiting exchange completion. false otherwise
	 */
	protected void fillInOrderDetails(
			final Order order,
			final ShoppingCart shoppingCart,
			final ShoppingCartTaxSnapshot pricingSnapshot,
			final CustomerSession customerSession,
			final Customer customer,
			final boolean isExchangeOrder,
			final boolean awaitExchangeCompletion) {
		order.setCustomer(customer);
		order.setModifiedBy(getEventOriginatorHelper().getCustomerOriginator(customer));

		order.setAppliedRules(getAppliedOrderRules(pricingSnapshot.getShoppingCartPricingSnapshot().getPromotionRecordContainer()
			.getAppliedRules(), customerSession.getLocale()));
		order.setTaxExemption(shoppingCart.getTaxExemption());

		// Set the billing address
		final OrderAddress billingAddress = getBean(ContextIdNames.ORDER_ADDRESS);
		if (shoppingCart.getBillingAddress() != null) {
			billingAddress.init(shoppingCart.getBillingAddress());
			order.setBillingAddress(billingAddress);
		}

		if (customer.isAnonymous()) {
			updateAnonymousCustomer(order, customer);
		}

		// Create & add shipments
		// Allocate inventory to order
		createOrderSkusFromCartItems(shoppingCart, pricingSnapshot, customerSession, order, isExchangeOrder, awaitExchangeCompletion);

		// CM user uid, null if order not placed through CSR
		order.setCmUserUID(shoppingCart.getCmUserUID());

	}

	/**
	 * Set the name and phone number of the anonymous customer from the billing address (only
	 * if they don't have values already set) to enable searching.
	 *
	 * @param order the order
	 * @param customer the anonymous customer
	 */
	protected void updateAnonymousCustomer(final Order order, final Customer customer) {

		final OrderAddress billingAddress = order.getBillingAddress();
		if (billingAddress == null) {
			return;
		}

		final CustomerService customerService = getBean(ContextIdNames.CUSTOMER_SERVICE);
		Customer customerToUpdate = customer;
		if (customer.isPersisted()) {
			customerToUpdate = customerService.findByGuid(customer.getGuid());
		}

		boolean customerUpdated = false;
		// First & last names belong together
		if (customerToUpdate.getFirstName() == null && customer.getLastName() == null) {
			customerToUpdate.setFirstName(billingAddress.getFirstName());
			customerToUpdate.setLastName(billingAddress.getLastName());
			customerUpdated = true;
		}

		if (customerToUpdate.getPhoneNumber() == null) {
			customerToUpdate.setPhoneNumber(billingAddress.getPhoneNumber());
			customerUpdated = true;
		}

		if (customerUpdated) {
			final Customer updatedCustomer = customerService.update(customerToUpdate);
			order.setCustomer(updatedCustomer);
		}
	}

	private void createOrderSkusFromCartItems(final ShoppingCart shoppingCart, final ShoppingCartTaxSnapshot taxSnapshot,
												final CustomerSession customerSession, final Order order,
												final boolean isExchangeOrder, final boolean awaitExchangeCompletion) {

		final Collection<OrderSku> rootItems = getOrderSkuFactory().createOrderSkus(
						shoppingCart.getShoppingItems(shoppingItem -> !shoppingItem.isBundleConstituent()),
						taxSnapshot,
						customerSession.getLocale());

		final Set<OrderSku> physicalSkus = new HashSet<>();
		final Set<OrderSku> electronicSkus = new HashSet<>();
		final Set<OrderSku> serviceShipmentSkus = new HashSet<>();

		splitAccordingToShipmentType(rootItems, physicalSkus, electronicSkus, serviceShipmentSkus);

		final ShoppingCartPricingSnapshot pricingSnapshot = taxSnapshot.getShoppingCartPricingSnapshot();

		//no promotion is applied to service items. So they won't contribute to the hasPhysicalAndElectronicShoppingItems
		final boolean hasPhysicalAndElectronicShoppingItems = !physicalSkus.isEmpty() && !electronicSkus.isEmpty();
		Map<String, BigDecimal> discountByShoppingItemUid = null;
		if (hasPhysicalAndElectronicShoppingItems) {
			//Apportion discount to individual items
			final Collection<OrderSku> nonServiceItems = Sets.union(physicalSkus, electronicSkus);

			final Map<? extends ShoppingItem, ShoppingItemPricingSnapshot> nonServiceItemPricingSnapshotMap = nonServiceItems.stream()
					.collect(toMap(identity(),
								   new ShoppingItemToPricingSnapshotFunction(pricingSnapshot)));

			discountByShoppingItemUid = getDiscountCalculator().apportionDiscountToShoppingItems(pricingSnapshot.getSubtotalDiscountMoney(),
																								nonServiceItemPricingSnapshotMap);
		}
		// Form the order shipments out from the cart item types
		if (!physicalSkus.isEmpty()) {
			//Total up the shipment discount from the skus included
			final BigDecimal discountForShipment = getDiscountForShipment(discountByShoppingItemUid,
					physicalSkus, hasPhysicalAndElectronicShoppingItems, pricingSnapshot.getSubtotalDiscount());
			final OrderShipment physicalShipment = createPhysicalShipment(discountForShipment,
					shoppingCart, pricingSnapshot, physicalSkus, isExchangeOrder, awaitExchangeCompletion);
			physicalShipment.setOrder(order);
			order.addShipment(physicalShipment);
		}
		if (!electronicSkus.isEmpty()) {
			//Total up the shipment discount from the skus included
			//electronic shipment can not be returned.
			final BigDecimal discountForShipment = getDiscountForShipment(discountByShoppingItemUid,
					electronicSkus, hasPhysicalAndElectronicShoppingItems, pricingSnapshot.getSubtotalDiscount());
			final OrderShipment electronicShipment = createElectronicShipment(discountForShipment, pricingSnapshot, electronicSkus);
			electronicShipment.setOrder(order);
			order.addShipment(electronicShipment);
		}
		if (!serviceShipmentSkus.isEmpty()) {
			final OrderShipment serviceOrderShipment = createServiceShipment(serviceShipmentSkus);
			serviceOrderShipment.setOrder(order);
			order.addShipment(serviceOrderShipment);
		}
	}

	private BigDecimal getDiscountForShipment(final Map<String, BigDecimal> discountByShoppingItem, final Set<OrderSku> shoppingItems,
			final boolean splitShipmentMode, final BigDecimal cartSubtotalDiscount) {
		if (!splitShipmentMode) {
			return cartSubtotalDiscount;
		}
		BigDecimal discount = BigDecimal.ZERO;
		for (final OrderSku sku : shoppingItems) {
			if (discountByShoppingItem.containsKey(sku.getGuid())) {
				discount = discount.add(discountByShoppingItem.get(sku.getGuid()));
			}
		}
		return discount;
	}

	private void splitAccordingToShipmentType(final Collection<? extends OrderSku> rootItems,
												final Set<OrderSku> physicalSkus,
												final Set<OrderSku> electronicSkus,
												final Set<OrderSku> serviceSkus) {
		final ShipmentTypeShoppingCartVisitor visitor = getBeanFactory().getBean(ContextIdNames.SHIPMENT_TYPE_SHOPPING_CART_VISITOR);
		for (final OrderSku sku : rootItems) {
			sku.accept(visitor, getProductSkuLookup());
		}
		for (final ShoppingItem item : visitor.getElectronicSkus()) {
			electronicSkus.add((OrderSku) item);
		}
		for (final ShoppingItem item : visitor.getPhysicalSkus()) {
			physicalSkus.add((OrderSku) item);
		}
		for (final ShoppingItem item : visitor.getServiceSkus()) {
			serviceSkus.add((OrderSku) item);
		}
	}

	/**
	 * Create the OrderShipment. Status of shipment is set according to allocation status of its OrderSkus.
	 *
	 * @param subtotalDiscount the subtotal discount
	 * @param shoppingCart the shopping cart
	 * @param pricingSnapshot the pricing snapshot
	 * @param orderSkuSet the set of order SKUs
	 * @param isExchangeOrder whether or not this is an exchange order
	 * @param awaitExchangeCompletion whether or not to await exchange completion
	 * @return an order shipment
	 */
	private OrderShipment createPhysicalShipment(final BigDecimal subtotalDiscount,
												 final ShoppingCart shoppingCart,
												 final ShoppingCartPricingSnapshot pricingSnapshot,
												 final Set<OrderSku> orderSkuSet,
												 final boolean isExchangeOrder,
												 final boolean awaitExchangeCompletion) {
		final PhysicalOrderShipment orderShipment = getBean(ContextIdNames.PHYSICAL_ORDER_SHIPMENT);
		final OrderAddress shippingAddress = getBean(ContextIdNames.ORDER_ADDRESS);

		shippingAddress.init(shoppingCart.getShippingAddress());
		orderShipment.setShipmentAddress(shippingAddress);
		orderShipment.setCreatedDate(getTimeService().getCurrentTime());


		final Optional<ShippingOption> selectedShippingOptionOptional = shoppingCart.getSelectedShippingOption();
		if (!selectedShippingOptionOptional.isPresent()) {
			final String errorMessage = format(
					"No shipping option is selected on the shopping cart (guid: %s) which is required for a physical shipment.",
					shoppingCart.getGuid());
			throw new MissingShippingOptionException(errorMessage);
		}

		final ShippingOption selectedShippingOption = selectedShippingOptionOptional.get();
		orderShipment.setShippingOptionCode(selectedShippingOption.getCode());
		orderShipment.setShippingOptionName(selectedShippingOption.getDisplayName(shoppingCart.getShopper().getLocale()).orElse(null));
		selectedShippingOption.getCarrierCode().ifPresent(orderShipment::setCarrierCode);
		selectedShippingOption.getCarrierDisplayName().ifPresent(orderShipment::setCarrierName);

		orderShipment.setShippingCost(pricingSnapshot.getShippingCost().getAmount());
		orderShipment.setBeforeTaxShippingCost(pricingSnapshot.getBeforeTaxShippingCost().getAmount());

		addOrderSkusToShipment(orderSkuSet, orderShipment);

		if (isExchangeOrder && awaitExchangeCompletion) {
			orderShipment.setStatus(OrderShipmentStatus.ONHOLD);
		} else {
			orderShipment.setStatus(OrderShipmentStatus.INVENTORY_ASSIGNED);
			for (final OrderSku skus : orderSkuSet) {
				if (!skus.isAllocated()) {
					orderShipment.setStatus(OrderShipmentStatus.AWAITING_INVENTORY);
				}
			}
		}

		// set discount
		orderShipment.setSubtotalDiscount(subtotalDiscount);

		orderShipment.setInclusiveTax(pricingSnapshot.isInclusiveTaxCalculationInUse());

		return orderShipment;
	}

	/**
	 * Adds skus to shipment.
	 */
	private void addOrderSkusToShipment(final Set<OrderSku> orderSkuSet, final OrderShipment orderShipment) {
		for (final OrderSku sku : orderSkuSet) {
			orderShipment.addShipmentOrderSku(sku);
		}
	}

	private OrderShipment createElectronicShipment(final BigDecimal subtotalDiscount,
													final ShoppingCartPricingSnapshot pricingSnapshot,
													final Set<OrderSku> orderSkuSet) {

		final ElectronicOrderShipment orderShipment = getBean(ContextIdNames.ELECTRONIC_ORDER_SHIPMENT);
		orderShipment.setCreatedDate(getTimeService().getCurrentTime());
		orderShipment.setStatus(OrderShipmentStatus.RELEASED);
		// add skus
		addOrderSkusToShipment(orderSkuSet, orderShipment);

		orderShipment.setInclusiveTax(pricingSnapshot.isInclusiveTaxCalculationInUse());

		orderShipment.setSubtotalDiscount(subtotalDiscount);

		return orderShipment;
	}


	private OrderShipment createServiceShipment(final Set<OrderSku> orderSkus) {
		final ServiceOrderShipment orderShipment = getBean(ContextIdNames.SERVICE_ORDER_SHIPMENT);
		addOrderSkusToShipment(orderSkus, orderShipment);
		orderShipment.setCreatedDate(getTimeService().getCurrentTime());
		orderShipment.setSubtotalDiscount(BigDecimal.ZERO.setScale(2));
		orderShipment.setStatus(OrderShipmentStatus.RELEASED);
		return orderShipment;
	}

	private EventOriginatorHelper getEventOriginatorHelper() {
		return getBean(ContextIdNames.EVENT_ORIGINATOR_HELPER);
	}

	/**
	 * Creates <code>AppliedRule</code> objects from the set of applied rule uids and returns them.
	 *
	 * @param appliedRuleIds the applied rule IDs
	 * @param locale         the locale in which to describe the applied rule
	 * @return a <code>Set</code> of applied rules
	 */
	protected Set<AppliedRule> getAppliedOrderRules(final Collection<Long> appliedRuleIds, final Locale locale) {
		final List<Rule> rules = getRuleService().findByUids(appliedRuleIds);

		final Stream<Long> noSuchRuleIdsStream = appliedRuleIds.stream()
				.filter(ruleId -> rules.stream()
						.map(Rule::getUidPk)
						.noneMatch(ruleId::equals));

		return Stream.concat(
				rules.stream()
						.map(rule -> createAppliedRule(rule, locale)),
				noSuchRuleIdsStream
						.map(this::createAppliedRuleForRemovedRule)
		).collect(Collectors.toSet());
	}

	/**
	 * Factory method for creating {@link AppliedRule} instances from a given {@link Rule} and {@link Locale}.
	 *
	 * @param rule   the rule from which to create the AppliedRule
	 * @param locale the locale in which to describe the applied rule
	 * @return a new {@link AppliedRule} instance
	 */
	protected AppliedRule createAppliedRule(final Rule rule, final Locale locale) {
		final AppliedRule appliedRule = getBean(ContextIdNames.APPLIED_RULE);

		appliedRule.initialize(rule, locale);
		appliedRule.accept(appliedRuleVisitor);

		return appliedRule;
	}

	/**
	 * Factory method for creating {@link AppliedRule} instances for a Rule ID that no longer corresponds to an actual, existing {@link Rule} record.
	 *
	 * @param uid the UIDPK of the rule that no longer exists
	 * @return a new {@link AppliedRule} instance
	 */
	protected AppliedRule createAppliedRuleForRemovedRule(final Long uid) {
		final AppliedRule appliedRule = getBean(ContextIdNames.APPLIED_RULE);

		appliedRule.setRuleUid(uid);
		appliedRule.setRuleName(RULE_DELETED_MESSAGE);
		appliedRule.setRuleCode(RULE_DELETED_MESSAGE);

		return appliedRule;
	}

	/**
	 * Convenience method for getting a bean instance from elastic path.
	 * @param <T> the type of bean to return
	 * @param beanName the name of the bean to get an instance of.
	 * @return an instance of the requested bean.
	 */
	protected <T> T getBean(final String beanName) {
		return getBeanFactory().<T>getBean(beanName);
	}

	/**
	 * @return the orderService
	 */
	OrderService getOrderService() {
		return orderService;
	}

	/**
	 * @param orderService the orderService to set
	 */
	public void setOrderService(final OrderService orderService) {
		this.orderService = orderService;
	}

	/**
	 * @return the orderSkuFactory
	 */
	OrderSkuFactory getOrderSkuFactory() {
		return orderSkuFactory;
	}

	/**
	 * @param appliedRuleVisitor the appliedRuleLoggingVisitor to set
	 */
	public void setAppliedRuleVisitor(final Visitor appliedRuleVisitor) {
		this.appliedRuleVisitor = appliedRuleVisitor;
	}
	/**
	 * @return the appliedRuleLoggingVisitor
	 */
	protected Visitor getAppliedRuleVisitor() {
		return appliedRuleVisitor;
	}

	/**
	 * @param orderSkuFactory the orderSkuFactory to set
	 */
	public void setOrderSkuFactory(final OrderSkuFactory orderSkuFactory) {
		this.orderSkuFactory = orderSkuFactory;
	}
	/**
	 * @return the beanFactory
	 */
	BeanFactory getBeanFactory() {
		return beanFactory;
	}

	/**
	 * @param beanFactory the beanFactory to set
	 */
	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * @return the ruleService
	 */
	RuleService getRuleService() {
		return ruleService;
	}

	/**
	 * @param ruleService the ruleService to set
	 */
	public void setRuleService(final RuleService ruleService) {
		this.ruleService = ruleService;
	}

	/**
	 * Gets the cart order service.
	 *
	 * @return the cartOrderService
	 */
	protected CartOrderService getCartOrderService() {
		return cartOrderService;
	}

	/**
	 * Sets the cart order service.
	 *
	 * @param cartOrderService the cartOrderService to set
	 */
	public void setCartOrderService(final CartOrderService cartOrderService) {
		this.cartOrderService = cartOrderService;
	}

	protected ProductSkuLookup getProductSkuLookup() {
		return productSkuLookup;
	}

	public void setProductSkuLookup(final ProductSkuLookup productSkuLookup) {
		this.productSkuLookup = productSkuLookup;
	}

	protected DiscountApportioningCalculator getDiscountCalculator() {
		return discountCalculator;
	}

	public void setDiscountCalculator(final DiscountApportioningCalculator discountCalculator) {
		this.discountCalculator = discountCalculator;
	}

	/**
	 * Set the time service.
	 *
	 * @param timeService the <code>TimeService</code> instance.
	 */
	public void setTimeService(final TimeService timeService) {
		this.timeService = timeService;
	}

	protected TimeService getTimeService() {
		return timeService;
	}
}
