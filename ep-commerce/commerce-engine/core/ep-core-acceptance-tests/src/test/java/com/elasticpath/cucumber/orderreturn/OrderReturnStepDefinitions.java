/**
 * Copyright (c) Elastic Path Software Inc., 2014
 */
package com.elasticpath.cucumber.orderreturn;

import cucumber.api.DataTable;
import cucumber.api.java.en.And;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import com.elasticpath.cucumber.shoppingcart.ShoppingCartStepDefinitionsHelper;

/**
 * OrderReturn test scenario step definitions class.
 * 
 */
public class OrderReturnStepDefinitions {
		
	@Autowired
	private OrderReturnStepDefinitionsHelper orderReturnStepDefinitionsHelper;

	@Autowired
	private ShoppingCartStepDefinitionsHelper shoppingCartStepDefinitionsHelper;
	
	/**
	 * Creates an order return and save it to the current test context. 
	 *
	 * @param dataTable return items
	 */
	@When("^a return is created with following items$")
	public void createReturn(final DataTable dataTable) {
		orderReturnStepDefinitionsHelper.createShipmentReturn(shoppingCartStepDefinitionsHelper.convertDataTableToShoppingItemDtos(dataTable));
	}
	
	/**
	 * Creates order return with a given collection of items.
	 *
	 * @param dataTable item dtos
	 */
	@When("^an exchange is created with returning following items$")
	public void createExchangeReturning(final DataTable dataTable) {
		orderReturnStepDefinitionsHelper
				.createExchangeReturningItems(shoppingCartStepDefinitionsHelper.convertDataTableToShoppingItemDtos(dataTable));
	}
	
	/**
	 * Creates order return with exchange order. 
	 *
	 * @param dataTable item exchange items
	 */
	@When("^exchanging with following items$")
	public void createExchange(final DataTable dataTable) {
		orderReturnStepDefinitionsHelper.createExchange(shoppingCartStepDefinitionsHelper.convertDataTableToShoppingItemDtos(dataTable));
	}
	
	/**
	 * Cancels the order return of the current test context.
	 */
	@And("^the return is canceled$")
	public void cancelReturn() {
		
		orderReturnStepDefinitionsHelper.cancelReturn();
	}
	
	/**
	 * Modifies order return items.
	 * 
	 * @param dataTable order return items
	 */
	@And("^the return is modified with following items$")
	public void editReturnItems(final DataTable dataTable) {
		orderReturnStepDefinitionsHelper.editReturnItems(shoppingCartStepDefinitionsHelper.convertDataTableToShoppingItemDtos(dataTable));
	}
}
