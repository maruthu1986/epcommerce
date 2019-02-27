/**
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.service.order.impl;

import static java.lang.String.format;
import static java.util.Collections.singletonList;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.OptimisticLockException;

import com.google.common.collect.ImmutableMap;

import com.elasticpath.base.common.dto.StructuredErrorMessage;
import com.elasticpath.base.exception.EpServiceException;
import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.commons.handlers.order.OrderShipmentHandler;
import com.elasticpath.commons.handlers.order.OrderShipmentHandlerFactory;
import com.elasticpath.core.messaging.order.OrderEventType;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.cmuser.CmUser;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.event.EventOriginator;
import com.elasticpath.domain.event.EventOriginatorHelper;
import com.elasticpath.domain.event.OrderEventHelper;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderPaymentStatus;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.domain.order.OrderReturnStatus;
import com.elasticpath.domain.order.OrderReturnType;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderShipmentStatus;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.OrderStatus;
import com.elasticpath.domain.shopper.Shopper;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingCartTaxSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.store.Store;
import com.elasticpath.messaging.EventMessage;
import com.elasticpath.messaging.EventMessagePublisher;
import com.elasticpath.messaging.EventType;
import com.elasticpath.messaging.factory.EventMessageFactory;
import com.elasticpath.persistence.api.FetchGroupLoadTuner;
import com.elasticpath.persistence.support.FetchGroupConstants;
import com.elasticpath.persistence.support.OrderCriterion;
import com.elasticpath.persistence.support.OrderCriterion.ResultType;
import com.elasticpath.persistence.support.impl.CriteriaQuery;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.plugin.payment.exceptions.PaymentGatewayException;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.customer.CustomerSessionService;
import com.elasticpath.service.impl.AbstractEpPersistenceServiceImpl;
import com.elasticpath.service.misc.FetchPlanHelper;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.order.IllegalReturnStateException;
import com.elasticpath.service.order.OrderReturnOutOfDateException;
import com.elasticpath.service.order.OrderReturnValidator;
import com.elasticpath.service.order.OrderService;
import com.elasticpath.service.order.ReturnAndExchangeService;
import com.elasticpath.service.order.ReturnExchangeType;
import com.elasticpath.service.payment.PaymentResult;
import com.elasticpath.service.payment.PaymentService;
import com.elasticpath.service.search.query.OrderReturnSearchCriteria;
import com.elasticpath.service.shipping.ShippingOptionResult;
import com.elasticpath.service.shipping.ShippingOptionService;
import com.elasticpath.service.shopper.ShopperService;
import com.elasticpath.service.shoppingcart.CheckoutService;
import com.elasticpath.service.shoppingcart.PricingSnapshotService;
import com.elasticpath.service.shoppingcart.TaxSnapshotService;
import com.elasticpath.service.store.StoreService;
import com.elasticpath.service.tax.ReturnTaxOperationService;
import com.elasticpath.service.tax.TaxDocumentModificationContext;
import com.elasticpath.service.tax.TaxDocumentModificationType;
import com.elasticpath.service.tax.TaxOperationService;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;

/**
 * Provides storage and access to <code>OrderReturn</code> objects.
 */
@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.TooManyMethods", "PMD.ExcessiveImports", "PMD.ExcessiveClassLength", "deprecation",
					"PMD.GodClass" })
public class ReturnAndExchangeServiceImpl extends AbstractEpPersistenceServiceImpl implements ReturnAndExchangeService {

	/** Message for unexpected order return type. */
	protected static final String UNEXPECTED_ORDER_RETURN_TYPE = "Unexpected order return type";

	/**
	 * The default shipping cost amount to be used when no value is provided.
	 */
	static final BigDecimal CALCULATE_SHIPPING_COST = new BigDecimal(Integer.MIN_VALUE);

	/**
	 * The default shipping discount amount to be used when no value is provided.
	 */
	static final BigDecimal CALCULATE_SHIPPING_DISCOUNT = new BigDecimal(Integer.MIN_VALUE);

	private CheckoutService checkoutService;

	private TimeService timeService;

	private ProductSkuLookup productSkuLookup;

	private FetchPlanHelper fetchPlanHelper;

	private OrderService orderService;

	private PaymentService paymentService;

	private ShopperService shopperService;

	private ShippingOptionService shippingOptionService;

	private CustomerSessionService customerSessionService;

	private StoreService storeService;

	private ReturnTaxOperationService returnTaxOperationService;

	private TaxOperationService taxOperationService;

	private EventMessageFactory eventMessageFactory;

	private EventMessagePublisher eventMessagePublisher;

	private OrderShipmentHandlerFactory orderShipmentHandlerFactory;

	private OrderReturnValidator orderReturnValidator;

	private PricingSnapshotService pricingSnapshotService;

	private TaxSnapshotService taxSnapshotService;

	@Override
	public List<OrderReturn> list(final long uidPk) throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("ORDER_RETURN_LIST", uidPk);
	}

	@Override
	public List<OrderReturn> list(final long uidPk, final OrderReturnType returnType) throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("ORDER_RETURN_LIST_BY_RETURN_TYPE", uidPk, returnType);
	}

	@Override
	public OrderReturn get(final long orderReturnUid) throws EpServiceException {
		return get(orderReturnUid, null);
	}

	@Override
	public OrderReturn get(final long orderReturnUid, final FetchGroupLoadTuner loadTuner) throws EpServiceException {
		sanityCheck();
		FetchGroupLoadTuner tuner = loadTuner;
		if (loadTuner == null) {
			tuner = getBean(ContextIdNames.FETCH_GROUP_LOAD_TUNER);
		}
		OrderReturn orderReturn = null;
		if (orderReturnUid <= 0) {
			orderReturn = getBean(ContextIdNames.ORDER_RETURN);
		} else {
			tuner.addFetchGroup(FetchGroupConstants.ORDER_NOTES); //added to ensure that the Order's orderEvents field is loaded
			fetchPlanHelper.configureFetchGroupLoadTuner(tuner);
			orderReturn = getPersistentBeanFinder().get(ContextIdNames.ORDER_RETURN, orderReturnUid);
			fetchPlanHelper.clearFetchPlan();
		}
		return orderReturn;
	}

	@Override
	public Object getObject(final long uid) throws EpServiceException {
		return get(uid);
	}

	@Override
	public List<OrderReturn> list() throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("ORDER_EXCHANGE_AND_RETURN_LIST_BY_ORDER_UID");
	}

	@Override
	public List<Long> findAllUids() throws EpServiceException {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("ORDER_RETURN_UIDS_LIST");
	}

	@Override
	public List<OrderReturn> findByUids(final Collection<Long> orderUids) {
		sanityCheck();

		if (orderUids == null || orderUids.isEmpty()) {
			return new ArrayList<>();
		}

		return getPersistenceEngine().retrieveByNamedQueryWithList("ORDER_RETURNS_BY_UIDS", "list", orderUids);
	}

	/**
	 * Retrieves list of <code>OrderReturn</code> uids where the last modified date is later than the specified date.
	 *
	 * @param date date to compare with the last modified date
	 * @return list of <code>OrderReturn</code> whose last modified date is later than the specified date
	 */
	@Override
	public List<Long> findUidsByModifiedDate(final Date date) {
		sanityCheck();
		return getPersistenceEngine().retrieveByNamedQuery("ORDER_RETURN_UIDS_SELECT_BY_MODIFIED_DATE", date);
	}

	@Override
	public OrderReturn update(final OrderReturn orderReturn) throws EpServiceException {
		sanityCheck();
		OrderReturn updatedOrderReturn = null;
		try {
			orderReturn.setLastModifiedDate(timeService.getCurrentTime());
			updatedOrderReturn = getPersistenceEngine().merge(orderReturn);
		} catch (OptimisticLockException e) {
			throw new OrderReturnOutOfDateException("Return cannot be updated as it has been updated by another user", e);
		}
		return updatedOrderReturn;
	}

	@Override
	public OrderReturn add(final OrderReturn orderReturn) throws EpServiceException {
		sanityCheck();
		orderReturn.setLastModifiedDate(timeService.getCurrentTime());
		if (orderReturn.getReturnPayment() != null && orderReturn.getReturnPayment().isPersisted()) {
			OrderPayment returnPayment = getPersistentBeanFinder().get(ContextIdNames.ORDER_PAYMENT, orderReturn.getReturnPayment().getUidPk());
			orderReturn.setReturnPayment(returnPayment);
		}

		getPersistenceEngine().save(orderReturn);

		return orderReturn;
	}

	@Override
	public OrderReturn getExchange(final long uidPk) {
		sanityCheck();
		List<OrderReturn> exchanges = getPersistenceEngine().retrieveByNamedQuery("EXCHANGE_BY_EXCHANGE_ORDER_UID", uidPk);
		if (!exchanges.isEmpty()) {
			return exchanges.get(0);
		}
		return null;
	}

	/**
	 * Gets the refund total for the order return minus any totals from the
	 * exchange orders.
	 *
	 * @param orderReturn
	 *            The total to retrieve for
	 * @return The total of the refund minus any exchange order totals
	 */
	protected BigDecimal getRefundTotal(final OrderReturn orderReturn) {
		final Order exchangeOrder = orderReturn.getExchangeOrder();
		final BigDecimal refundTotal;
		if (exchangeOrder == null) {
			refundTotal = orderReturn.getReturnTotal();
		} else {
			refundTotal = orderReturn.getReturnTotal().subtract(
					exchangeOrder.getTotal());
		}
		return refundTotal;
	}

	/**
	 * Partially or fully refund the order based on the order return payment and
	 * order shipment. If the shipment is null, an EP service exception is thrown.
	 *
	 * @param orderReturn order return to be refunded.
	 * @param orderShipment the order shipment against which the refund will be applied.
	 * @return shrunk down version of <code>OrderReturn</code>. The return will contain return payment and optionally exchange order.
	 */
	protected OrderReturn refundOrderShipmentReturn(final OrderReturn orderReturn, final OrderShipment orderShipment) {

		if (orderShipment == null) {
			throw new EpServiceException("Can not refund as there is no orderShipment");
		}

		OrderReturn updatedOrderReturn = orderReturn;
		final BigDecimal refundTotal = getRefundTotal(orderReturn);

		if (refundTotal.compareTo(BigDecimal.ZERO) > 0) {
			final Order order = this.orderService.refundOrderPayment(
					orderReturn.getOrder().getUidPk(),
					orderShipment.getShipmentNumber(),
					null, // force the service to find the captured order payments
					refundTotal, orderReturn.getOrder().getModifiedBy());

			updatedOrderReturn = updateReturnPayment(orderReturn, order);
		}

		return updatedOrderReturn;
	}

	/**
	 */
	private OrderReturn findUpdatedOrderReturn(final OrderReturn orderReturn, final Order order) {
		if (order.getReturns().contains(orderReturn)) {
			for (OrderReturn item : order.getReturns()) {
				if (orderReturn.equals(item)) {
					return item;
				}
			}
		}
		return orderReturn;
	}

	/**
	 * Finds the last successful refund in the payments set.
	 *
	 * @param orderPayments the order payments
	 * @return the last refund payment in the set
	 */
	OrderPayment findLastRefundPayment(final Set<OrderPayment> orderPayments) {
		OrderPayment lastRefundPayment = null;
		for (OrderPayment orderPayment : orderPayments) {
			if (OrderPayment.CREDIT_TRANSACTION.equals(orderPayment.getTransactionType())
					&& orderPayment.getStatus() == OrderPaymentStatus.APPROVED) {
				if (lastRefundPayment == null) {
					lastRefundPayment = orderPayment;
				} else if (lastRefundPayment.getCreatedDate()
						.compareTo(orderPayment.getCreatedDate()) < 0) {
					lastRefundPayment = orderPayment;
				}
			}
		}
		return lastRefundPayment;
	}

	/**
	 * Creates new payment in order to keep amount that was refunded manually, e.g. by cash. The payment is required in order to properly calculate
	 * return's refunded money. Exchange order shouldn't and can't know if refund was processed manually or actually.
	 *
	 * @param orderReturn to be manually refunded.
	 * @return manual payment.
	 */
	private OrderReturn manualRefundOrderReturn(final OrderReturn orderReturn) {
		final BigDecimal refundTotal = getRefundTotal(orderReturn);

		if (refundTotal.compareTo(BigDecimal.ZERO) > 0) {
			OrderPayment refundPayment = getBean(ContextIdNames.ORDER_PAYMENT);
			refundPayment.setPaymentMethod(PaymentType.RETURN_AND_EXCHANGE);
			refundPayment.setAmount(refundTotal);
			refundPayment.setTransactionType(OrderPayment.CREDIT_TRANSACTION);
			refundPayment.setStatus(OrderPaymentStatus.APPROVED);
			refundPayment.setCreatedDate(timeService.getCurrentTime());
			refundPayment.setOrder(orderReturn.getOrder());
			orderReturn.setReturnPayment(refundPayment);
		}

		return orderReturn;
	}

	/**
	 * Completes exchange. Places in to ONHOLD state, allocates inventory for shipments. Authorization can be provided for shipments, depending on
	 * exchange order total and exchange total.
	 *
	 * @param exchange exchange to be completed
	 * @param templateOrderPayment template payment to be used for authorization.
	 * @param processMoney whether do refund or auth
	 * @param refundToOriginal whether refund to original
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	private OrderReturn completeExchangeOrderInternal(final OrderReturn exchange, final OrderPayment templateOrderPayment,
			final boolean processMoney, final boolean refundToOriginal) {
		Order exchangeOrder = exchange.getExchangeOrder();

		if (exchangeOrder == null) {
			throw new EpServiceException("Exchange order isn't specified.");
		}

		if (!OrderStatus.AWAITING_EXCHANGE.equals(exchangeOrder.getStatus())) {
			throw new EpServiceException("Cannot complete exchange order. Incorrect order state.");
		}

		exchangeOrder.setModifiedBy(getEventOriginator(exchange.getCreatedByCmUser()));
		exchangeOrder = orderService.releaseHoldOnOrder(exchangeOrder);

		if (processMoney) {
			BigDecimal authTotal = exchangeOrder.getTotal().subtract(exchange.getReturnTotal());

			if (authTotal.compareTo(BigDecimal.ZERO) > 0 && templateOrderPayment != null) {
				PaymentResult paymentResult = paymentService.initializePayments(exchangeOrder, templateOrderPayment, null);
				if (paymentResult.getResultCode() != PaymentResult.CODE_OK) {
					exchangeOrder = orderService.awaitExchnageCompletionForOrder(exchangeOrder);
					orderService.update(exchangeOrder);
					throw paymentResult.getCause();
				}
			}

			BigDecimal refundTotal = exchange.getReturnTotal().subtract(exchangeOrder.getTotal());
			if (refundTotal.compareTo(BigDecimal.ZERO) > 0) {
				Order refundedOrder = null;
				try {
					if (refundToOriginal) {
						refundedOrder = orderService.refundOrderPayment(exchange.getOrder().getUidPk(),
																		exchange.getOrderShipmentForReturn().getShipmentNumber(),
																		null,
																		refundTotal,
																		exchange.getOrder().getModifiedBy());
					} else if (!refundToOriginal && templateOrderPayment != null) {
						refundedOrder = orderService.refundOrderPayment(exchange.getOrder().getUidPk(),
																		templateOrderPayment.getOrderShipment().getShipmentNumber(),
																		templateOrderPayment,
																		refundTotal,
																		exchange.getOrder().getModifiedBy());
					}
				} catch (PaymentGatewayException pge) {
					exchangeOrder = orderService.awaitExchnageCompletionForOrder(exchangeOrder);
					orderService.update(exchangeOrder);
					throw pge;
				}

				if (refundedOrder != null) {
					exchange.setReturnPayment(findLastRefundPayment(refundedOrder.getOrderPayments()));
					// if refund didn't fail we guarantee that refunded order will contain one and only one payment - refunded payment
				}
			}
		}

		for (OrderShipment orderShipment : exchangeOrder.getPhysicalShipments()) {
			orderShipment.setStatus(OrderShipmentStatus.INVENTORY_ASSIGNED);
			for (final OrderSku skus : orderShipment.getShipmentOrderSkus()) {
				if (!skus.isAllocated()) {
					orderShipment.setStatus(OrderShipmentStatus.AWAITING_INVENTORY);
				}
			}
		}

		final Order updatedExchangeOrder = orderService.update(exchangeOrder);
		return findUpdatedOrderReturn(exchange, updatedExchangeOrder);
	}

	/**
	 * @return
	 */
	private EventOriginator getEventOriginator(final CmUser cmUser) {
		EventOriginatorHelper helper = getBean(ContextIdNames.EVENT_ORIGINATOR_HELPER);

		return helper.getCmUserOriginator(cmUser);
	}

	@Override
	public Order createExchangeOrder(final OrderReturn orderExchange, final boolean awaitExchangeCompletion) {
		return createExchangeOrder(orderExchange, getExchangePayment(), awaitExchangeCompletion);
	}

	@Override
	public Order createExchangeOrder(final OrderReturn orderExchange, final OrderPayment templatePayment, final boolean awaitExchangeCompletion) {
		checkoutService.checkoutExchangeOrder(orderExchange, templatePayment, awaitExchangeCompletion);

		final ShoppingCart shoppingCart = orderExchange.getExchangeShoppingCart();
		final Order exchangeOrder = shoppingCart.getCompletedOrder();
		orderExchange.setExchangeOrder(exchangeOrder);

		return exchangeOrder;
	}

	private OrderPayment getExchangePayment() {
		final OrderPayment originalPayment = getBean(ContextIdNames.ORDER_PAYMENT);
		originalPayment.setPaymentMethod(PaymentType.RETURN_AND_EXCHANGE);
		return originalPayment;
	}

	@Override
	public ShoppingCart populateShoppingCart(final OrderReturn exchangeOrder, final Collection<? extends ShoppingItem> itemList,
											 final ShippingOption shippingOption, final Address shippingAddress) {
		return populateShoppingCart(exchangeOrder, itemList, shippingOption, CALCULATE_SHIPPING_COST, CALCULATE_SHIPPING_DISCOUNT,
									shippingAddress);
	}

	@Override
	public ShoppingCart populateShoppingCart(final OrderReturn exchange, final Collection<? extends ShoppingItem> itemList,
											 final ShippingOption shippingOption, final BigDecimal shippingCost, final BigDecimal shippingDiscount,
											 final Address shippingAddress) {

		ShoppingCart shoppingCart = exchange.getExchangeShoppingCart();
		if (shoppingCart == null) {

			final Order order = exchange.getOrder();
			final CustomerSession customerSession = createCustomerSessionForOrder(order);
			shoppingCart = createShoppingCartForOrderAndShopper(order, customerSession);

			shoppingCart.setExchangeOrderShoppingCart(true);
			exchange.setExchangeCustomerSession(customerSession);
		}

		shoppingCart.setShippingAddress(shippingAddress);

		if (shippingOption != null) {
			final ShippingOptionResult shippingOptionResult = shippingOptionService.getShippingOptions(shoppingCart);

			final String errorMessage = format("Unable to get available shipping options for the given cart with guid '%s'. "
							+ "Shipping option cannot be validated.",
					shoppingCart.getGuid());
			shippingOptionResult.throwExceptionIfUnsuccessful(
					errorMessage,
					singletonList(
							new StructuredErrorMessage(
									"shippingoptions.unavailable",
									errorMessage,
									ImmutableMap.of(
											"cart-id", shoppingCart.getGuid(),
											"shipping-option", shippingOption.getCode()))
					));

			final List<ShippingOption> validShippingOptions = shippingOptionResult.getAvailableShippingOptions();

			if (shippingOptionResult.getAvailableShippingOptions().stream()
					.noneMatch(element -> element.getCode().equals(shippingOption.getCode()))) {
				throw new EpDomainException(format("Shipping option '%s' is not a valid available shipping option. Valid shipping options: %s",
												   shippingOption.getCode(), validShippingOptions));
			}

			shoppingCart.setSelectedShippingOption(shippingOption);
		}

		/*
		 * Workaround to prevent shopping cart from removing product sku items. see MSC-5254
		 */
		shoppingCart.clearItems();
		for (final ShoppingItem item : itemList) {
			shoppingCart.addCartItem(item);
		}

		if (CALCULATE_SHIPPING_COST.compareTo(shippingCost) != 0) {
			shoppingCart.setShippingCostOverride(shippingCost);
		}

		if (CALCULATE_SHIPPING_DISCOUNT.compareTo(shippingDiscount) != 0) {
			shoppingCart.setSubtotalDiscountOverride(shippingDiscount);
		}

		ShoppingCartPricingSnapshot pricingSnapshotForCart = pricingSnapshotService.getPricingSnapshotForCart(shoppingCart);
		final ShoppingCartTaxSnapshot taxSnapshotForCart = taxSnapshotService.getTaxSnapshotForCart(shoppingCart, pricingSnapshotForCart);
		exchange.setExchangeShoppingCart(shoppingCart, taxSnapshotForCart);


		return shoppingCart;
	}

	private ShoppingCart createShoppingCartForOrderAndShopper(final Order order, final CustomerSession customerSession) {
		final ShoppingCart shoppingCart = getBean(ContextIdNames.SHOPPING_CART);
		final Store store = getStoreService().findStoreWithCode(order.getStoreCode());

		shoppingCart.setCustomerSession(customerSession);
		customerSession.setShoppingCart(shoppingCart);
		customerSession.setCurrency(order.getCurrency());
		customerSession.setLocale(order.getLocale());

		shoppingCart.setBillingAddress(order.getBillingAddress());
		shoppingCart.setStore(store);

		return shoppingCart;
	}

	private CustomerSession createCustomerSessionForOrder(final Order order) {
		final Store store = getStoreService().findStoreWithCode(order.getStoreCode());
		final Shopper shopper = shopperService.findOrCreateShopper(order.getCustomer(), order.getStoreCode());
		CustomerSession customerSessionWithShopper = customerSessionService.createWithShopper(shopper);
		return customerSessionService.initializeCustomerSessionForPricing(customerSessionWithShopper,
				store.getCode(), order.getCurrency());
	}

	@Override
	public OrderReturn createShipmentReturn(final OrderReturn orderReturn,
											final ReturnExchangeType type,
											final OrderShipment orderShipment,
											final EventOriginator originator) {

		orderReturn.updateOrderReturnableQuantity(orderReturn.getOrder(), getProductSkuLookup());
		getOrderReturnValidator().validate(orderReturn, orderShipment);

		orderReturn.recalculateOrderReturn();
		orderReturn.getOrder().setModifiedBy(originator);

		OrderReturnStatus status = OrderReturnStatus.COMPLETED;
		OrderReturn updatedOrderReturn;
		switch (type) {
		case PHYSICAL_RETURN_REQUIRED:
			status = OrderReturnStatus.AWAITING_STOCK_RETURN;
			updatedOrderReturn = orderReturn;
			updatedOrderReturn.setPhysicalReturn(true);
			break;
		case REFUND_TO_ORIGINAL:
			updatedOrderReturn = refundOrderShipmentReturn(orderReturn, orderShipment);
			break;
		case MANUAL_RETURN:
			updatedOrderReturn = manualRefundOrderReturn(orderReturn);
			break;
		default:
			throw new EpServiceException(UNEXPECTED_ORDER_RETURN_TYPE);
		}

		finalizeReturn(status, updatedOrderReturn);

		OrderReturn result = getReturnAndExchangeService().add(updatedOrderReturn);
		getOrderEventHelper().logOrderReturnCreated(updatedOrderReturn.getOrder(), updatedOrderReturn);

		performPostCreateReturn(result);
		sendReturnExchangeEvent(result.getUidPk(), OrderEventType.RETURN_CREATED, null);
		return result;
	}

	private void finalizeReturn(final OrderReturnStatus status, final OrderReturn updatedOrderReturn) {

		updatedOrderReturn.setReturnStatus(status);
		updatedOrderReturn.normalizeOrderReturn();

		switch (updatedOrderReturn.getReturnType()) {
		case RETURN:
			updatedOrderReturn.getOrder().addReturn(updatedOrderReturn);
			break;
		case EXCHANGE:
			break;
		default:
			throw new EpServiceException(UNEXPECTED_ORDER_RETURN_TYPE);
		}
	}

	@SuppressWarnings("fallthrough")
	@Override
	public OrderReturn createExchange(final OrderReturn orderReturn, final ReturnExchangeType type, final OrderPayment authOrderPayment) {

		OrderReturnStatus status = OrderReturnStatus.COMPLETED;
		OrderReturn updatedOrderReturn = orderReturn;
		final Order exchangeOrder;
		switch (type) {
		case CREATE_WO_PAYMENT:
			exchangeOrder = createExchangeOrder(orderReturn, false);
			orderReturn.setExchangeOrder(exchangeOrder);
			break;
		case PHYSICAL_RETURN_REQUIRED:
			status = OrderReturnStatus.AWAITING_STOCK_RETURN;
			exchangeOrder = createExchangeOrder(orderReturn, true);
			orderReturn.setExchangeOrder(exchangeOrder);
			orderReturn.setPhysicalReturn(true);
			break;
		case REFUND_TO_ORIGINAL:
			exchangeOrder = createExchangeOrder(orderReturn, false);
			orderReturn.setExchangeOrder(exchangeOrder);
			updatedOrderReturn = refundOrderShipmentReturn(orderReturn, orderReturn.getOrderShipmentForReturn());
			break;
		case MANUAL_RETURN:
			exchangeOrder = createExchangeOrder(orderReturn, false);
			orderReturn.setExchangeOrder(exchangeOrder);
			updatedOrderReturn = manualRefundOrderReturn(orderReturn);
			break;
		case ORIGINAL_PAYMENT:

		case NEW_PAYMENT:
			if (authOrderPayment == null) {
				throw new EpServiceException("Auth payment must be specified if additional authorization required.");
			}
			exchangeOrder = createExchangeOrder(updatedOrderReturn, authOrderPayment, false);
			updatedOrderReturn.setExchangeOrder(exchangeOrder);
			break;
		default:
			throw new EpServiceException(UNEXPECTED_ORDER_RETURN_TYPE);
		}

		finalizeReturn(status, updatedOrderReturn);

		final OrderReturn result = getReturnAndExchangeService().add(updatedOrderReturn);
		getOrderEventHelper().logOrderExchangeCreated(updatedOrderReturn.getOrder(), updatedOrderReturn);

		performPostCreateReturn(result);
		sendReturnExchangeEvent(result.getUidPk(), OrderEventType.EXCHANGE_CREATED, null);

		return result;
	}

	@Override
	public OrderReturn editReturn(final OrderReturn orderReturn) {
		if (orderReturn.isInTerminalState()) {
			throw new IllegalReturnStateException("Detected state: " + orderReturn.getReturnStatus());
		}
		orderReturn.normalizeOrderReturn();
		getOrderEventHelper().logOrderReturnChanged(orderReturn.getOrder(), orderReturn);

		OrderReturn result = getReturnAndExchangeService().update(orderReturn);
		performPostEditReturn(result);

		return result;
	}

	@Override
	public OrderReturn completeReturn(final OrderReturn orderReturn, final ReturnExchangeType type) {
		OrderReturn updatedOrderReturn;
		switch (type) {
		case REFUND_TO_ORIGINAL:
			updatedOrderReturn = refundOrderShipmentReturn(orderReturn, orderReturn.getOrderShipmentForReturn());
			break;
		case MANUAL_RETURN:
			updatedOrderReturn = manualRefundOrderReturn(orderReturn);
			break;
		default:
			throw new EpServiceException(UNEXPECTED_ORDER_RETURN_TYPE);
		}

		updatedOrderReturn.setReturnStatus(OrderReturnStatus.COMPLETED);

		// the logging should happen with the original order return as it has the event originator
		getOrderEventHelper().logOrderReturnCompleted(orderReturn.getOrder(), orderReturn);
		return getReturnAndExchangeService().update(updatedOrderReturn);
	}

	@SuppressWarnings("fallthrough")
	@Override
	public OrderReturn completeExchange(final OrderReturn orderReturn, final ReturnExchangeType type, final OrderPayment authOrderPayment) {
		OrderReturn refundedReturn = null;
		switch (type) {
		case CREATE_WO_PAYMENT:
			completeExchangeOrderInternal(orderReturn, null, false, false);
			break;
		case REFUND_TO_ORIGINAL:
			refundedReturn = completeExchangeOrderInternal(orderReturn, null, true, true);
			orderReturn.setReturnPayment(refundedReturn.getReturnPayment());
			break;
		case MANUAL_RETURN:
			completeExchangeOrderInternal(orderReturn, null, false, false);
			manualRefundOrderReturn(orderReturn);
			break;
		case ORIGINAL_PAYMENT:

		case NEW_PAYMENT:
			if (authOrderPayment == null) {
				throw new EpServiceException("Auth payment must be specified if additional authorization required.");
			}
			completeExchangeOrderInternal(orderReturn, authOrderPayment, true, false);
			break;
		default:
			throw new EpServiceException(UNEXPECTED_ORDER_RETURN_TYPE);
		}

		orderReturn.setReturnStatus(OrderReturnStatus.COMPLETED);

		// the logging should happen with the original order return as it has the event originator
		getOrderEventHelper().logOrderExchangeCompleted(orderReturn.getOrder(), orderReturn);
		return getReturnAndExchangeService().update(orderReturn);
	}

	@Override
	public OrderReturn cancelReturnExchange(final OrderReturn orderReturn) {
		if (orderReturn.getReturnStatus() != OrderReturnStatus.AWAITING_STOCK_RETURN) {
			throw new IllegalReturnStateException("Detected state: " + orderReturn.getReturnStatus());
		}
		orderReturn.setReturnStatus(OrderReturnStatus.CANCELLED);

		// Log the return/exchange canceled.
		try {
			if (orderReturn.getReturnType() == OrderReturnType.EXCHANGE) {
				getOrderEventHelper().logOrderExchangeCanceled(orderReturn.getOrder(), orderReturn);
			} else {
				getOrderEventHelper().logOrderReturnCanceled(orderReturn.getOrder(), orderReturn);
			}
			OrderReturn result = getReturnAndExchangeService().update(orderReturn);
			performPostCancelReturnExchange(orderReturn);

			return result;
		} catch (org.springframework.orm.jpa.JpaOptimisticLockingFailureException e) {
			throw new OrderReturnOutOfDateException("Return cannot be Canceled as it has been updated by another user.", e);
		}
	}

	@Override
	public OrderReturn receiveReturn(final OrderReturn orderReturn) {

		getOrderEventHelper().logOrderReturnReceived(orderReturn.getOrder(), orderReturn);
		return getReturnAndExchangeService().update(orderReturn);
	}

	@Override
	public List<OrderReturn> findOrderReturnBySearchCriteria(
			final OrderReturnSearchCriteria orderReturnSearchCriteria, final int start,
			final int maxResults) {
		sanityCheck();
		final OrderCriterion orderCriterion = getBean(ContextIdNames.ORDER_CRITERION);
		CriteriaQuery query = orderCriterion.getOrderReturnSearchCriteria(orderReturnSearchCriteria, ResultType.ENTITY);

		List<OrderReturn> orderReturnList = null;

		FetchGroupLoadTuner loadTuner = getBean(ContextIdNames.FETCH_GROUP_LOAD_TUNER);
		loadTuner.addFetchGroup(FetchGroupConstants.ORDER_RETURN_INDEX);
		fetchPlanHelper.configureFetchGroupLoadTuner(loadTuner);

		if (query.getParameters().isEmpty()) {
			orderReturnList = getPersistenceEngine().retrieve(query.getQuery());
		} else {
			orderReturnList = getPersistenceEngine().retrieve(query.getQuery(), query.getParameters().toArray());
		}

		fetchPlanHelper.clearFetchPlan();

		return orderReturnList;
	}

	@Override
	public long getOrderReturnCountBySearchCriteria(
			final OrderReturnSearchCriteria orderReturnSearchCriteria) {
		sanityCheck();
		final OrderCriterion orderCriterion = getBean(ContextIdNames.ORDER_CRITERION);
		CriteriaQuery query = orderCriterion.getOrderReturnSearchCriteria(orderReturnSearchCriteria, ResultType.COUNT);
		List<Long> orderCount = null;

		FetchGroupLoadTuner loadTuner = getBean(ContextIdNames.FETCH_GROUP_LOAD_TUNER);
		loadTuner.addFetchGroup(FetchGroupConstants.ORDER_RETURN_INDEX);
		fetchPlanHelper.configureFetchGroupLoadTuner(loadTuner);

		if (query.getParameters().isEmpty()) {
			orderCount = getPersistenceEngine().retrieve(query.getQuery());
		} else {
			orderCount = getPersistenceEngine().retrieve(query.getQuery(), query.getParameters().toArray());
		}

		fetchPlanHelper.clearFetchPlan();

		return orderCount.get(0);
	}

	/**
	 * Commits the return's tax documents.
	 */
	private void performPostCreateReturn(final OrderReturn result) {

		getReturnTaxOperationService().commitDocument(result.calculateTaxes().getTaxDocument(), result);

		// Specifically calls update to save the tax document id for the order return
		getReturnAndExchangeService().update(result);

		if (result.getReturnType() != OrderReturnType.EXCHANGE) {
			return;
		}

		Order exchangeOrder = result.getExchangeOrder();

		for (OrderShipment shipment : exchangeOrder.getAllShipments()) {
			getTaxOperationService().commitDocument(shipment.calculateTaxes().getTaxDocument(), shipment);
		}
	}

	/**
	 * Reverse the return's tax document.
	 */
	private void performPostCancelReturnExchange(final OrderReturn result) {

		getReturnTaxOperationService().reverseTaxes(result, result.getOrderReturnAddress());
	}

	/**
	 * Reverse the return's previous tax document, and commits its updated tax document.
	 */
	private void performPostEditReturn(final OrderReturn result) {

		TaxDocumentModificationContext taxDocumentModificationContext = getBean(ContextIdNames.TAX_DOCUMENT_MODIFICATION_CONTEXT);
		taxDocumentModificationContext.add(result, result.getOrderReturnAddress(), TaxDocumentModificationType.UPDATE);

		result.resetTaxDocumentId();
		OrderReturn updatedOrderReturn = getReturnAndExchangeService().update(result);

		getReturnTaxOperationService().updateTaxes(updatedOrderReturn, taxDocumentModificationContext);
	}

	@Override
	public void resendReturnExchangeNotification(final long orderReturnUid, final String emailRecipient) {
		sendReturnExchangeEvent(orderReturnUid, OrderEventType.RESEND_RETURN_EXCHANGE_NOTIFICATION, emailRecipient);
	}

	private void sendReturnExchangeEvent(final long orderReturnUid, final EventType eventType, final String emailRecipient) {
		final Map<String, Object> additionalData = new HashMap<>();
		additionalData.put("UID", orderReturnUid);
		if (emailRecipient != null) {
			additionalData.put("EMAIL", emailRecipient);
		}

		// Send notification via messaging system
		try {
			final EventMessage orderReturnExchangeCreatedEventMessage = getEventMessageFactory().createEventMessage(
																									eventType,
																									null,
																									additionalData);

			getEventMessagePublisher().publish(orderReturnExchangeCreatedEventMessage);

		} catch (final Exception e) {
			throw new EpSystemException("Failed to publish Event Message", e);
		}
	}

	@Override
	public OrderReturn getOrderReturnPrototype(final OrderShipment orderShipment, final OrderReturnType orderReturnType) {
		final OrderReturn orderReturn = getBean(ContextIdNames.ORDER_RETURN);
		orderReturn.setReturnType(orderReturnType);

		final OrderShipmentHandler handler = retrieveOrderShipmentHandler(orderShipment);
		handler.handleOrderReturn(orderReturn, orderShipment);
		return orderReturn;
	}

	/**
	 * Sets the order return's payment to be the last refund payment.
	 * @param orderReturn The order return's field to set
	 * @param order The order to retrieve the payment for
	 * @return The order return with the updated field
	 */
	protected OrderReturn updateReturnPayment(final OrderReturn orderReturn, final Order order) {
		OrderReturn updatedOrderReturn = findUpdatedOrderReturn(orderReturn, order);
		updatedOrderReturn.setReturnPayment(findLastRefundPayment(order.getOrderPayments()));
		return updatedOrderReturn;
	}

	@Override
	public OrderShipmentHandler retrieveOrderShipmentHandler(final OrderShipment orderShipment) {
		return getOrderShipmentHandlerFactory().getOrderShipmentHandler(orderShipment.getOrderShipmentType());
	}

	/**
	 * The OrderShipmentHandlerFactory to set.
	 * @param orderShipmentHandlerFactory the orderShipmentHandlerFactory to set
	 */
	public void setOrderShipmentHandlerFactory(final OrderShipmentHandlerFactory orderShipmentHandlerFactory) {
		this.orderShipmentHandlerFactory = orderShipmentHandlerFactory;
	}

	/**
	 * Returns the OrderShipmentHandlerFactory instance.
	 * @return the orderShipmentHandlerFactory
	 */
	public OrderShipmentHandlerFactory getOrderShipmentHandlerFactory() {
		return orderShipmentHandlerFactory;
	}


	protected ReturnAndExchangeService getReturnAndExchangeService() {
		return getBean(ContextIdNames.ORDER_RETURN_SERVICE);
	}

	protected OrderEventHelper getOrderEventHelper() {
		return getBean(ContextIdNames.ORDER_EVENT_HELPER);
	}

	public void setShopperService(final ShopperService shopperService) {
		this.shopperService = shopperService;
	}

	public ShopperService getShopperService() {
		return shopperService;
	}

	public void setTimeService(final TimeService timeService) {
		this.timeService = timeService;
	}

	public void setPaymentService(final PaymentService paymentService) {
		this.paymentService = paymentService;
	}

	public void setShippingOptionService(final ShippingOptionService shippingOptionService) {
		this.shippingOptionService = shippingOptionService;
	}

	public void setCustomerSessionService(final CustomerSessionService customerSessionService) {
		this.customerSessionService = customerSessionService;
	}

	public void setOrderService(final OrderService orderService) {
		this.orderService = orderService;
	}

	public void setCheckoutService(final CheckoutService checkoutService) {
		this.checkoutService = checkoutService;
	}

	public void setProductSkuLookup(final ProductSkuLookup productSkuLookup) {
		this.productSkuLookup = productSkuLookup;
	}

	protected ProductSkuLookup getProductSkuLookup() {
		return productSkuLookup;
	}

	public void setFetchPlanHelper(final FetchPlanHelper fetchPlanHelper) {
		this.fetchPlanHelper = fetchPlanHelper;
	}

	public void setStoreService(final StoreService storeService) {
		this.storeService = storeService;
	}

	protected StoreService getStoreService() {
		return storeService;
	}

	public ReturnTaxOperationService getReturnTaxOperationService() {
		return returnTaxOperationService;
	}

	public void setReturnTaxOperationService(final ReturnTaxOperationService returnTaxOperationService) {
		this.returnTaxOperationService = returnTaxOperationService;
	}

	public TaxOperationService getTaxOperationService() {
		return taxOperationService;
	}

	public void setTaxOperationService(final TaxOperationService taxOperationService) {
		this.taxOperationService = taxOperationService;
	}

	public void setEventMessageFactory(final EventMessageFactory eventMessageFactory) {
		this.eventMessageFactory = eventMessageFactory;
	}

	protected EventMessageFactory getEventMessageFactory() {
		return this.eventMessageFactory;
	}

	public void setEventMessagePublisher(final EventMessagePublisher eventMessagePublisher) {
		this.eventMessagePublisher = eventMessagePublisher;
	}

	protected EventMessagePublisher getEventMessagePublisher() {
		return this.eventMessagePublisher;
	}

	public OrderReturnValidator getOrderReturnValidator() {
		return orderReturnValidator;
	}

	public void setOrderReturnValidator(final OrderReturnValidator orderReturnValidator) {
		this.orderReturnValidator = orderReturnValidator;
	}

	public PricingSnapshotService getPricingSnapshotService() {
		return pricingSnapshotService;
	}

	public void setPricingSnapshotService(final PricingSnapshotService pricingSnapshotService) {
		this.pricingSnapshotService = pricingSnapshotService;
	}

	public TaxSnapshotService getTaxSnapshotService() {
		return taxSnapshotService;
	}

	public void setTaxSnapshotService(final TaxSnapshotService taxSnapshotService) {
		this.taxSnapshotService = taxSnapshotService;
	}

}
