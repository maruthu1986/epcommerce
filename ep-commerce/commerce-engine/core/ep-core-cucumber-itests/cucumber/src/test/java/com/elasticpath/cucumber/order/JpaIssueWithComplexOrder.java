/**
 * Copyright (c) Elastic Path Software Inc., 2016
 */
package com.elasticpath.cucumber.order;

import static org.assertj.core.api.Assertions.assertThat;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.domain.order.Order;
import com.elasticpath.service.order.OrderService;

/**
 * Steps for the jpa-issue-with-complex-order feature.
 */
public class JpaIssueWithComplexOrder {

	@Autowired
	private OrderService orderService;

	private String orderNumber;
	private Order order;

	/**
	 * The method only writes order number to instance variable. The order is expected to already exist in the DB.
	 *
	 * @param orderNumber order number.
	 */
	@Given("^the Order number (.*) contains a complex bundle consisting physical, digital and bundles$")
	public void recordOrderNumber(final String orderNumber) {
		this.orderNumber = orderNumber;
	}

	/**
	 * Try to read the order from the database.
	 */
	@When("^I read this order from the database$")
	public void readOrder() {
		order = orderService.findOrderByOrderNumber(orderNumber);
		assertThat(order).isNotNull();
	}

	/**
	 * Try to get root shopping items.
	 *
	 * @param count expected size of root shopping items collection.
	 */
	@And("^the root shopping items count should be (\\d+)$")
	public void checkCountOfRootShoppingItems(final int count) {
		assertThat(order.getRootShoppingItems()).hasSize(1);
	}
}
