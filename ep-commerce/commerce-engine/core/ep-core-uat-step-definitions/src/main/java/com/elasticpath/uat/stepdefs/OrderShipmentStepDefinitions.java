/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.uat.stepdefs;

import static org.junit.Assert.fail;

import java.util.Date;
import java.util.List;

import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.elasticpath.domain.event.EventOriginatorHelper;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderShipmentStatus;
import com.elasticpath.service.order.OrderService;
import com.elasticpath.service.order.ReleaseShipmentFailedException;
import com.elasticpath.uat.ScenarioContextValueHolder;

/**
 * Step Definitions for Order Shipment-based functionality.
 */
public class OrderShipmentStepDefinitions {

	@Autowired
	@Qualifier("orderHolder")
	private ScenarioContextValueHolder<Order> orderHolder;

	@Autowired
	@Qualifier("orderShipmentHolder")
	private ScenarioContextValueHolder<OrderShipment> orderShipmentHolder;

	@Autowired
	private OrderService orderService;

	@Autowired
	private EventOriginatorHelper eventOriginatorHelper;

	@Autowired
	private ScenarioContextValueHolder<Runnable> emailSendingCommandHolder;

	@When("^the shipment ships with tracking code \"([^\"]*)\"$")
	public void shipShipment(final String trackingCode) throws Exception {

		// Hold off executing this until we are ready to capture email.
		emailSendingCommandHolder.set(() -> {
			final OrderShipment orderShipment = getOrderShipment();
			orderHolder.set(orderService.completeShipment(
					orderShipment.getShipmentNumber(),
					trackingCode,
					false,
					new Date(),
					false,
					eventOriginatorHelper.getSystemOriginator()));
			orderShipmentHolder.set(findOrderShipmentInOrder());
		});

	}

	@When("^releasing the shipment fails$")
	public void failShipmentRelease() throws Throwable {
		// Defer execution until we are ready to check for the email
		emailSendingCommandHolder.set(() -> {
			final OrderShipment orderShipment = getOrderShipment();

			orderShipment.getOrder().setModifiedBy(eventOriginatorHelper.getSystemOriginator());

			// setting the status to something other than INVENTORY_ASSIGNED will cause the shipment release to fail.
			orderShipment.setStatus(OrderShipmentStatus.AWAITING_INVENTORY);

			try {
				orderService.processReleaseShipment(orderShipment);
				fail("Expected to throw a ReleaseShipmentFailedException when releasing a shipment fails");
			} catch (final ReleaseShipmentFailedException e) {
				// expected
			}
		});
	}

	OrderShipment getOrderShipment() {
		if (orderShipmentHolder.get() == null) {
			orderShipmentHolder.set(findOrderShipmentInOrder());
		}

		return orderShipmentHolder.get();
	}

	OrderShipment findOrderShipmentInOrder() {
		final List<OrderShipment> shipments = orderHolder.get().getAllShipments();

		// Assumes one shipment - please refactor if you require multiple
		return shipments.get(0);
	}

}
