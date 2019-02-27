/*
 * Copyright (c) Elastic Path Software Inc., 2007
 */
package com.elasticpath.domain.event.impl;

import static java.lang.String.format;

import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.elasticpath.base.exception.EpSystemException;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.EpDomainException;
import com.elasticpath.domain.event.EventOriginator;
import com.elasticpath.domain.event.EventOriginatorHelper;
import com.elasticpath.domain.event.EventOriginatorType;
import com.elasticpath.domain.event.OrderEventHelper;
import com.elasticpath.domain.event.OrderEventPaymentDetailFormatter;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderEvent;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderPaymentStatus;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.money.Money;
import com.elasticpath.money.MoneyFormatter;
import com.elasticpath.plugin.payment.PaymentType;
import com.elasticpath.service.misc.TimeService;
import com.elasticpath.service.shoppingcart.PricingSnapshotService;

/**
 * The helper on the <code>OrderEvent</code>. Help to generate the event details to track the order changes.
 */
@SuppressWarnings({ "PMD.TooManyMethods", "PMD.GodClass" })
public class OrderEventHelperImpl implements OrderEventHelper {
	private static final String TITLE_ORDER_PLACED = "Order Placed";

	private static final String TITLE_ORDER_CANCELED = "Order Canceled";

	private static final String TITLE_ORDER_SHIPMENT_RELEASED = "Order Shipment Released";

	private static final String TITLE_ORDER_SHIPMENT_CANCELED = "Order Shipment Canceled";

	private static final String TITLE_ORDER_NOTE = "Order Note";

	private static final String TITLE_PAYMENT_CAPTURED = "Payment Captured";

	private static final String TITLE_PAYMENT_REFUND = "Refund";

	private static final String TITLE_SKU_ADDED = "Sku Added";

	private static final String TITLE_SKU_REMOVED = "Sku Removed";

	private static final String TITLE_SKU_MOVED = "Sku Moved";

	private static final String TITLE_SKU_QUANTITY_CHANGED = "Sku Quantity Changed";

	private static final String TITLE_SHIPPING_METHOD_CHANGED = "Shipping Method Changed";

	private static final String TITLE_SHIPPING_ADDRESS_CHANGED = "Shipping Address Changed";

	private static final String TITLE_ORDER_ON_HOLD = "Order on Hold";

	private static final String TITLE_ORDER_HOLD_RELEASED = "Order Hold Released";

	private static final String TITLE_ORDER_RELEASED = "Order Released for Fulfilment";

	private static final String TITLE_RETURN_CREATED = "Return Created";

	private static final String TITLE_ORDER_EXCHANGE_CREATED = "Order Exchange Created";

	private static final String TITLE_RETURN_STOCK_RECEIVED = "Return Stock Received";

	private static final String TITLE_RETURN_CHANGED = "Return Changed";

	private static final String TITLE_RETURN_CANCELED = "Return Canceled";

	private static final String TITLE_RETURN_COMPLETED = "Return Completed";

	private static final String TITLE_EXCHANGE_CREATED = "Exchange Created";

	private static final String TITLE_EXCHANGE_CANCELED = "Exchange Canceled";

	private static final String TITLE_EXCHANGE_COMPLETED = "Exchange Completed";

	private Map<PaymentType, OrderEventPaymentDetailFormatter> formatterMap;

	private TimeService timeService;
	private MoneyFormatter moneyFormatter;
	private BeanFactory beanFactory;
	private PricingSnapshotService pricingSnapshotService;

	/**
	 * Log the event when order placed.
	 *
	 * @param order the new order
	 */
	@Override
	public void logOrderPlaced(final Order order) {

		EventOriginator originator = getEventOriginator(order);

		OrderEvent orderEvent = createOrderEvent(getOrderPlacedDetail(order), TITLE_ORDER_PLACED, originator);

		order.addOrderEvent(orderEvent);
	}

	/**
	 * Extension point.
	 *
	 * Gets detail by populating the full name of customer in order who placed the order.
	 *
	 * @param order the order
	 * @return the detail wording for order placed event.
	 */
	protected String getOrderPlacedDetail(final Order order) {
		return format("Order is placed by %1$s.", order.getCustomer().getFullName());
	}

	/**
	 * Log the event when order canceled.
	 *
	 * @param order the order been canceled
	 */
	@Override
	public void logOrderCanceled(final Order order) {

		EventOriginator originator = getEventOriginator(order);

		String detail = "Order is canceled.";

		OrderEvent orderEvent = createOrderEvent(detail, TITLE_ORDER_CANCELED, originator);

		order.addOrderEvent(orderEvent);
	}

	/**
	 * Log the event when order shipment released.
	 *
	 * @param order the order
	 * @param shipment the order shipment
	 */
	@Override
	public void logOrderShipmentReleased(final Order order, final OrderShipment shipment) {

		EventOriginator originator = getEventOriginator(order);

		String detail = format("Order shipment #%1$s is released.", shipment.getShipmentNumber());

		OrderEvent orderEvent = createOrderEvent(detail, TITLE_ORDER_SHIPMENT_RELEASED, originator);

		order.addOrderEvent(orderEvent);
	}

	/**
	 * Log the event when order shipment canceled.
	 *
	 * @param order the order
	 * @param shipment the order shipment
	 */
	@Override
	public void logOrderShipmentCanceled(final Order order, final OrderShipment shipment) {

		EventOriginator originator = getEventOriginator(order);

		String detail = format("Order shipment #%1$s is canceled.", shipment.getShipmentNumber());

		OrderEvent orderEvent = createOrderEvent(detail, TITLE_ORDER_SHIPMENT_CANCELED, originator);

		order.addOrderEvent(orderEvent);
	}

	/**
	 * Log the event when notes added.
	 *
	 * @param order the order
	 * @param note the note
	 */
	@Override
	public void logOrderNote(final Order order, final String note) {
		EventOriginator originator = getEventOriginator(order);

		OrderEvent orderEvent = createOrderEvent(note, TITLE_ORDER_NOTE, originator);
		order.addOrderEvent(orderEvent);
	}

	/**
	 * Log the event when payment captured.
	 *
	 * @param order the order
	 * @param orderPayment the order payment
	 */
	@Override
	public void logOrderPaymentCaptured(final Order order, final OrderPayment orderPayment) {

		if (orderPayment == null || !orderPayment.getTransactionType().equals(OrderPayment.CAPTURE_TRANSACTION)
				|| orderPayment.getStatus() != OrderPaymentStatus.APPROVED) {
			return; // Not a valid order payment capture.
		}

		EventOriginator originator = getSystemEventOriginator();

		StringBuilder detail = new StringBuilder();
		detail.append("Payment Captured at ");
		detail.append(orderPayment.getCreatedDate());
		detail.append(" with total ");
		detail.append(getMoneyFormatter().formatCurrency(orderPayment.getAmountMoney(), order.getLocale()));
		detail.append(" on ");
		detail.append(orderPayment.getPaymentMethod());
		detail.append(getPaymentDetails(orderPayment));
		detail.append('.');

		OrderEvent orderEvent = createOrderEvent(detail.toString(), TITLE_PAYMENT_CAPTURED, originator);
		order.addOrderEvent(orderEvent);
	}

	/**
	 * Log the event when payment refund.
	 *
	 * @param order the order
	 * @param orderPayment the order payment
	 */
	@Override
	public void logOrderPaymentRefund(final Order order, final OrderPayment orderPayment) {

		if (orderPayment == null || !orderPayment.getTransactionType().equals(OrderPayment.CREDIT_TRANSACTION)
				|| orderPayment.getStatus() != OrderPaymentStatus.APPROVED) {
			return; // Not a valid order payment refund.
		}

		EventOriginator originator = order.getModifiedBy();
		if (originator == null) {
			originator = getSystemEventOriginator();
		}

		StringBuilder detail = new StringBuilder();
		detail.append("Refund at ");
		detail.append(orderPayment.getCreatedDate());
		detail.append(" with total ");
		detail.append(getMoneyFormatter().formatCurrency(orderPayment.getAmountMoney(), order.getLocale()));
		detail.append(" to ");
		detail.append(orderPayment.getPaymentMethod());
		detail.append(getPaymentDetails(orderPayment));
		detail.append('.');

		OrderEvent orderEvent = createOrderEvent(detail.toString(), TITLE_PAYMENT_REFUND, originator);

		order.addOrderEvent(orderEvent);
	}

	private static String getOrderSkuDisplay(final OrderSku orderSku) {
		return orderSku.getSkuGuid();
	}

	/**
	 * Log the event when new sku added.
	 *
	 * @param shipment the shipment
	 * @param orderSku the sku be ADDED
	 */
	@Override
	public void logOrderSkuAdded(final OrderShipment shipment, final OrderSku orderSku) {

		final Order order = shipment.getOrder();
		EventOriginator originator = getEventOriginator(order);
		String currencyCodeANDMoneyValueAndSymbol = "undefined";
		final ShoppingItemPricingSnapshot pricingSnapshot = getPricingSnapshotService().getPricingSnapshotForOrderSku(orderSku);
		Money listUnitPrice = pricingSnapshot.getListUnitPrice();
		if (listUnitPrice != null) {
			currencyCodeANDMoneyValueAndSymbol = moneyFormatter.formatCurrency(listUnitPrice, order.getLocale());
		}
		String detail = format("New Sku (%1$s @ %2$s of %3$s) is added to the shipment #%5$s, order total changed to %4$s.",
				orderSku.getQuantity(), currencyCodeANDMoneyValueAndSymbol,	getOrderSkuDisplay(orderSku),
				moneyFormatter.formatCurrency(order.getTotalMoney(), order.getLocale()), shipment.getShipmentNumber());

		OrderEvent orderEvent = createOrderEvent(detail, TITLE_SKU_ADDED, originator);
		order.addOrderEvent(orderEvent);
	}

	/**
	 * Log the event when new sku removed.
	 *
	 * @param shipment the shipment
	 * @param orderSku the sku be removed
	 */
	@Override
	public void logOrderSkuRemoved(final OrderShipment shipment, final OrderSku orderSku) {

		final Order order = shipment.getOrder();
		EventOriginator originator = getEventOriginator(order);

		String detail = format("Sku (%1$s of %2$s) is removed from the shipment #%4$s, order total changed to %3$s.", orderSku.getQuantity(),
				getOrderSkuDisplay(orderSku),
				getMoneyFormatter().formatCurrency(order.getTotalMoney(), order.getLocale()),
				shipment.getShipmentNumber());

		OrderEvent orderEvent = createOrderEvent(detail, TITLE_SKU_REMOVED, originator);

		order.addOrderEvent(orderEvent);
	}

	/**
	 * Log the event when sku moved to other shipments.
	 *
	 * @param shipment the shipment
	 * @param orderSku the sku be moved
	 */
	@Override
	public void logOrderSkuMoved(final OrderShipment shipment, final OrderSku orderSku) {

		final Order order = shipment.getOrder();
		EventOriginator originator = getEventOriginator(order);

		String detail = format("Sku (%1$s of %2$s) is moved to shipment #%3$s.", orderSku.getQuantity(), getOrderSkuDisplay(orderSku),
				orderSku.getShipment().getShipmentNumber());

		OrderEvent orderEvent = createOrderEvent(detail, TITLE_SKU_MOVED, originator);

		order.addOrderEvent(orderEvent);
	}

	/**
	 * Log the event when sku quantity changed.
	 *
	 * @param shipment the shipment
	 * @param orderSku the sku be changed
	 * @param quantity quantity changed on the order sku.
	 */
	@Override
	public void logOrderSkuQuantityChanged(final OrderShipment shipment, final OrderSku orderSku, final int quantity) {

		final Order order = shipment.getOrder();
		EventOriginator originator = getEventOriginator(order);

		String detail = format("Sku (%1$s of %2$s) is changed on the shipment #%4$s, order total changed to %3$s.", quantity,
				getOrderSkuDisplay(orderSku),
				getMoneyFormatter().formatCurrency(order.getTotalMoney(), order.getLocale()),
				shipment.getShipmentNumber());

		OrderEvent orderEvent = createOrderEvent(detail, TITLE_SKU_QUANTITY_CHANGED, originator);

		order.addOrderEvent(orderEvent);
	}

	/**
	 * Log the event when shipping method changed.
	 *
	 * @param order the order
	 * @param shipment the shipment.
	 */
	@Override
	public void logOrderShipmentMethodChanged(final Order order, final PhysicalOrderShipment shipment) {

		EventOriginator originator = getEventOriginator(order);

		String carrierCodeString = Optional.ofNullable(shipment.getCarrierCode())
				.map(carrierCode -> format("; Carrier Code: %s", carrierCode))
				.orElse(StringUtils.EMPTY);

		String detail = format("Shipping method on #%1$s is changed to '%2$s' [Shipping Option Code: %3$s%4$s].",
				shipment.getShipmentNumber(), shipment.getShippingOptionName(), shipment.getShippingOptionCode(), carrierCodeString);

		OrderEvent orderEvent = createOrderEvent(detail, TITLE_SHIPPING_METHOD_CHANGED, originator);

		order.addOrderEvent(orderEvent);
	}

	/**
	 * Log the event when shipping address changed.
	 *
	 * @param order the order
	 * @param shipment the shipment.
	 */
	@Override
	public void logOrderShipmentAddressChanged(final Order order, final PhysicalOrderShipment shipment) {

		EventOriginator originator = getEventOriginator(order);

		String detail = format("Shipping address on #%1$s is changed to: %2$s.", shipment.getShipmentNumber(), shipment.getShipmentAddress());

		OrderEvent orderEvent = createOrderEvent(detail, TITLE_SHIPPING_ADDRESS_CHANGED, originator);

		order.addOrderEvent(orderEvent);
	}

	/**
	 * Log the event when order is put on hold.
	 *
	 * @param order the order
	 */
	@Override
	public void logOrderOnHold(final Order order) {

		EventOriginator originator = getEventOriginator(order);

		String detail = format("Order status changed to %1$s.", order.getStatus());

		OrderEvent orderEvent = createOrderEvent(detail, TITLE_ORDER_ON_HOLD, originator);

		order.addOrderEvent(orderEvent);
	}

	/**
	 * Log the event when order hold is released.
	 *
	 * @param order the order
	 */
	@Override
	public void logOrderHoldReleased(final Order order) {

		EventOriginator originator = getEventOriginator(order);

		String detail = format("Order status changed to %1$s.", order.getStatus());

		OrderEvent orderEvent = createOrderEvent(detail, TITLE_ORDER_HOLD_RELEASED, originator);

		order.addOrderEvent(orderEvent);
	}

	@Override
	public void logOrderReleasedForFulfilment(final Order order) {
		final EventOriginator originator = getEventOriginator(order);

		final String detail = "Order is released for fulfilment.";

		final OrderEvent orderEvent = createOrderEvent(detail, TITLE_ORDER_RELEASED, originator);

		order.addOrderEvent(orderEvent);
	}

	/**
	 * Log the event when order return is created.
	 *
	 * @param order the order
	 * @param orderReturn the order return
	 */
	@Override
	public void logOrderReturnCreated(final Order order, final OrderReturn orderReturn) {

		EventOriginator originator = getEventOriginator(order);

		String detail = String.format("Order return with RMA code #%1$s is created. Return note: %2$s", orderReturn.getRmaCode(),
			StringUtils.defaultIfBlank(orderReturn.getReturnComment(), ""));

		OrderEvent orderEvent = createOrderEvent(detail, TITLE_RETURN_CREATED, originator);

		order.addOrderEvent(orderEvent);
	}

	/**
	 * Log the event when order exchange is created.
	 *
	 * @param order the order
	 * @param orderExchange the order return
	 */
	@Override
	public void logOrderExchangeCreated(final Order order, final OrderReturn orderExchange) {

		EventOriginator originator = getEventOriginator(order);

		String detail = String.format("Order exchange with RMA code #%1$s is created. Return note: %2$s", orderExchange.getRmaCode(),
			StringUtils.defaultIfBlank(orderExchange.getReturnComment(), ""));

		OrderEvent orderEvent = createOrderEvent(detail, TITLE_ORDER_EXCHANGE_CREATED, originator);

		order.addOrderEvent(orderEvent);
	}

	/**
	 * Log the event when receive the return item.
	 *
	 * @param order the order
	 * @param orderReturn the order return
	 */
	@Override
	public void logOrderReturnReceived(final Order order, final OrderReturn orderReturn) {
		if (order.getOrderEvents() == null) {
			throw new EpDomainException(
					"Error: The order's OrderEvents must not be null; the field was not populated by the persistence layer.");
		}

		EventOriginator originator = getEventOriginator(order);

		String detail = format("Stock received on order return with RMA code #%1$s.", orderReturn.getRmaCode());

		OrderEvent orderEvent = createOrderEvent(detail, TITLE_RETURN_STOCK_RECEIVED, originator);

		order.addOrderEvent(orderEvent);
	}

	/**
	 * Log the event when the order return is changed.
	 *
	 * @param order the order
	 * @param orderReturn the order return
	 */
	@Override
	public void logOrderReturnChanged(final Order order, final OrderReturn orderReturn) {

		EventOriginator originator = getEventOriginator(order);

		String detail = format("Order return with RMA code #%1$s is changed.", orderReturn.getRmaCode());

		OrderEvent orderEvent = createOrderEvent(detail, TITLE_RETURN_CHANGED, originator);

		order.addOrderEvent(orderEvent);
	}

	/**
	 * Log the event when the order return is canceled.
	 *
	 * @param order the order
	 * @param orderReturn the order return
	 */
	@Override
	public void logOrderReturnCanceled(final Order order, final OrderReturn orderReturn) {

		EventOriginator originator = getEventOriginator(order);

		String detail = format("Order return with RMA code #%1$s is canceled.", orderReturn.getRmaCode());

		OrderEvent orderEvent = createOrderEvent(detail, TITLE_RETURN_CANCELED, originator);

		order.addOrderEvent(orderEvent);
	}

	/**
	 * Log the event when the order return is completed.
	 *
	 * @param order the order
	 * @param orderReturn the order return
	 */
	@Override
	public void logOrderReturnCompleted(final Order order, final OrderReturn orderReturn) {

		EventOriginator originator = getEventOriginator(order);

		String detail = format("Order return with RMA code #%1$s is completed.", orderReturn.getRmaCode());

		OrderEvent orderEvent = createOrderEvent(detail, TITLE_RETURN_COMPLETED, originator);

		order.addOrderEvent(orderEvent);
	}

	/**
	 * Log the event when exchange order is created.
	 *
	 * @param order the exchange order
	 */
	@Override
	public void logOrderExchangeCreated(final Order order) {

		EventOriginator originator = getEventOriginator(order);

		OrderEvent orderEvent = createOrderEvent("Order exchange is created.", TITLE_EXCHANGE_CREATED, originator);

		order.addOrderEvent(orderEvent);
	}

	/**
	 * Log the event when the exchange order is canceled.
	 *
	 * @param order the exchange order
	 * @param orderExchange the orderExchange
	 */
	@Override
	public void logOrderExchangeCanceled(final Order order, final OrderReturn orderExchange) {

		EventOriginator originator = getEventOriginator(order);

		String detail = format("Order exchange #%1$s is canceled.", orderExchange.getRmaCode());

		OrderEvent orderEvent = createOrderEvent(detail, TITLE_EXCHANGE_CANCELED, originator);

		order.addOrderEvent(orderEvent);
	}

	/**
	 * Log the event when the exchange order is completed.
	 *
	 * @param order the exchange order
	 * @param orderExchange the orderExchange
	 */
	@Override
	public void logOrderExchangeCompleted(final Order order, final OrderReturn orderExchange) {

		EventOriginator originator = getEventOriginator(order);

		String detail = format("Order exchange #%1$s is completed.", orderExchange.getRmaCode());

		OrderEvent orderEvent = createOrderEvent(detail, TITLE_EXCHANGE_COMPLETED, originator);

		order.addOrderEvent(orderEvent);
	}

	private EventOriginator getEventOriginator(final Order order) {
		EventOriginator originator = order.getModifiedBy();
		if (originator == null) {
			throw new EpSystemException("No event originator found in the order, " + "please set this value so we can track who changes the order.");
		}

		if (originator.getCmUser() == null
				&& (originator.getType() == EventOriginatorType.CMUSER || originator.getType() == EventOriginatorType.WSUSER)) {
			throw new EpSystemException("Changes should be initialized by a cmUser, but no cmUser response for this.");
		}
		return originator;
	}

	private EventOriginator getSystemEventOriginator() {
		EventOriginatorHelper helper = beanFactory.getBean(ContextIdNames.EVENT_ORIGINATOR_HELPER);
		return helper.getSystemOriginator();
	}

	/**
	 * Creates an {@link OrderEvent}.
	 * 
	 * @param detail detail message
	 * @param title the title
	 * @param originator the originator
	 * @return instance of {@link OrderEvent}
	 */
	protected OrderEvent createOrderEvent(final String detail, final String title, final EventOriginator originator) {
		OrderEvent orderEvent = beanFactory.getBean(ContextIdNames.ORDER_EVENT);
		orderEvent.setOriginatorType(originator.getType());
		orderEvent.setCreatedBy(originator.getCmUser());
		orderEvent.setCreatedDate(this.timeService.getCurrentTime());
		orderEvent.setTitle(title);

		orderEvent.setNote(detail);
		return orderEvent;
	}

	private String getPaymentDetails(final OrderPayment orderPayment) {
		return getPaymentDetailsFormatter(orderPayment).formatPaymentDetails(orderPayment);
	}

	private OrderEventPaymentDetailFormatter getPaymentDetailsFormatter(final OrderPayment orderPayment) {
		OrderEventPaymentDetailFormatter formatter = formatterMap.get(orderPayment.getPaymentMethod());

		if (formatter == null) {
			throw new IllegalArgumentException("No formatter found for " + orderPayment.getPaymentMethod());
		} else {
			return formatter;
		}
	}

	public void setFormatterMap(final Map<PaymentType, OrderEventPaymentDetailFormatter> formatterMap) {
		this.formatterMap = formatterMap;
	}

	public void setTimeService(final TimeService timeService) {
		this.timeService = timeService;
	}

	public void setMoneyFormatter(final MoneyFormatter formatter) {
		this.moneyFormatter = formatter;
	}

	protected MoneyFormatter getMoneyFormatter() {
		return moneyFormatter;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	protected PricingSnapshotService getPricingSnapshotService() {
		return pricingSnapshotService;
	}

	public void setPricingSnapshotService(final PricingSnapshotService pricingSnapshotService) {
		this.pricingSnapshotService = pricingSnapshotService;
	}

}

