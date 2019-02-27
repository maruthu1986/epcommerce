/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.domain.builder;

import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.domain.builder.checkout.CheckoutTestCartBuilder;
import com.elasticpath.domain.builder.shopper.ShoppingContext;
import com.elasticpath.domain.customer.Customer;
import com.elasticpath.domain.customer.CustomerSession;
import com.elasticpath.domain.event.EventOriginator;
import com.elasticpath.domain.event.impl.EventOriginatorHelperImpl;
import com.elasticpath.domain.factory.OrderPaymentFactory;
import com.elasticpath.domain.misc.CheckoutResults;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.payment.PaymentGateway;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingCartPricingSnapshot;
import com.elasticpath.domain.shoppingcart.ShoppingCartTaxSnapshot;
import com.elasticpath.service.order.OrderService;
import com.elasticpath.service.payment.PaymentResult;
import com.elasticpath.service.payment.PaymentService;
import com.elasticpath.service.shoppingcart.CheckoutService;
import com.elasticpath.service.shoppingcart.OrderFactory;
import com.elasticpath.service.shoppingcart.PricingSnapshotService;
import com.elasticpath.service.shoppingcart.TaxSnapshotService;

public class OrderBuilder implements DomainObjectBuilder<Order> {

	private CheckoutTestCartBuilder checkoutTestCartBuilder;

	@Autowired
	private OrderService orderService;

	@Autowired
	private CheckoutService checkoutService;

	@Autowired
	private OrderPaymentFactory orderPaymentFactory;

	@Autowired
	private OrderFactory orderFactory;

	@Autowired
	private PaymentService paymentService;

	@Autowired
	private PricingSnapshotService pricingSnapshotService;

	@Autowired
	private TaxSnapshotService taxSnapshotService;

	private OrderPayment templateOrderPayment;

	private boolean allShipmentsCompleted;

	private boolean sendShipmentConfirmationEmail = false;

	private ShoppingContext shoppingContext;

	public OrderBuilder withCheckoutTestCartBuilder(final CheckoutTestCartBuilder checkoutTestCartBuilder) {
		this.checkoutTestCartBuilder = checkoutTestCartBuilder;
		return this;
	}

	public OrderBuilder withNonZeroPhysicalShipment() {
		checkoutTestCartBuilder.withPhysicalProduct();
		return this;
	}

	public OrderBuilder withNonZeroElectronicShipment() {
		checkoutTestCartBuilder.withElectronicProduct();
		return this;
	}

	public OrderBuilder withFreeElectronicShipment() {
		checkoutTestCartBuilder.withFreeElectronicProduct();
		return this;
	}

	public OrderBuilder withGiftCertificateProduct() {
		checkoutTestCartBuilder.withGiftCertificateProduct();
		return this;
	}

	public OrderBuilder withTemplateOrderPayment(final OrderPayment templateOrderPayment) {
		this.templateOrderPayment = templateOrderPayment;
		return this;
	}

	public OrderBuilder withTokenizedTemplateOrderPayment() {
		this.templateOrderPayment = orderPaymentFactory.createTemplateTokenizedOrderPayment();
		return this;
	}

	public OrderBuilder withGateway(final PaymentGateway gateway) {
		checkoutTestCartBuilder.withGateway(gateway);
		return this;
	}

	public OrderBuilder withAllShipmentsCompleted() {
		this.allShipmentsCompleted  = true;
		return this;
	}

	public OrderBuilder withShoppingContext(final ShoppingContext shoppingContext) {
		this.shoppingContext = shoppingContext;
		return this;
	}

	public OrderBuilder withShipmentConfirmationEmail() {
		this.sendShipmentConfirmationEmail  = true;
		return this;
	}

	public Order checkout() {
		if (checkoutTestCartBuilder == null) {
			throw new IllegalStateException("Cannot call checkout() prior to calling withCheckoutTestCartBuilder()");
		}

		if (shoppingContext == null) {
			throw new IllegalStateException("Cannot call checkout() prior to calling withShoppingContext()");
		}

		ShoppingCart shoppingCart = checkoutTestCartBuilder.build();
		final ShoppingCartPricingSnapshot pricingSnapshot = pricingSnapshotService.getPricingSnapshotForCart(shoppingCart);
		final ShoppingCartTaxSnapshot taxSnapshot = taxSnapshotService.getTaxSnapshotForCart(shoppingCart, pricingSnapshot);
		CheckoutResults results = checkoutService.checkout(shoppingCart,
														   taxSnapshot,
														   shoppingContext.getCustomerSession(),
														   templateOrderPayment,
														   true);
		Order order = results.getOrder();

		if (allShipmentsCompleted) {
			order = completePhysicalShipmentsForOrder(order, sendShipmentConfirmationEmail);
		}

		return order;
	}

	@Override
	public Order build() {
		if (checkoutTestCartBuilder == null) {
			throw new IllegalStateException("Cannot call build() prior to calling withCheckoutTestCartBuilder()");
		}

		if (shoppingContext == null) {
			throw new IllegalStateException("Cannot call build() prior to calling withShoppingContext()");
		}

		final ShoppingCart shoppingCart = checkoutTestCartBuilder.build();
		final Customer customer = shoppingContext.getShopper().getCustomer();
		final CustomerSession customerSession = shoppingContext.getCustomerSession();
		final ShoppingCartPricingSnapshot pricingSnapshot = pricingSnapshotService.getPricingSnapshotForCart(shoppingCart);
		final ShoppingCartTaxSnapshot taxSnapshot = taxSnapshotService.getTaxSnapshotForCart(shoppingCart, pricingSnapshot);

		Order order = orderFactory.createAndPersistNewEmptyOrder(
				customer,
				customerSession,
				shoppingCart,
				false,
				false);

		order = orderFactory.fillInNewOrderFromShoppingCart(order,
															customer,
															customerSession,
															shoppingCart,
															taxSnapshot);
		order.releaseOrder();

		final PaymentResult paymentResult = paymentService.initializePayments(order, templateOrderPayment, shoppingCart.getAppliedGiftCertificates());

		order.setOrderPayments(new HashSet<>(paymentResult.getProcessedPayments()));

		if (allShipmentsCompleted) {
			order = completePhysicalShipmentsForOrder(order, sendShipmentConfirmationEmail);
		}

		return orderService.update(order);
	}

	private Order completePhysicalShipmentsForOrder(final Order order, final boolean sendShipmentConfirmationEmail) {
		Order completedOrder = order;
		for (OrderShipment orderShipment : order.getPhysicalShipments()) {
			orderShipment = orderService.processReleaseShipment(orderShipment);
			String shipmentNumber = orderShipment.getShipmentNumber();
			EventOriginator systemOriginator = new EventOriginatorHelperImpl().getSystemOriginator();
			completedOrder = orderService.completeShipment(shipmentNumber, "trackingNumber", true, null,
				sendShipmentConfirmationEmail, systemOriginator);
		}

		return completedOrder;
	}

}
