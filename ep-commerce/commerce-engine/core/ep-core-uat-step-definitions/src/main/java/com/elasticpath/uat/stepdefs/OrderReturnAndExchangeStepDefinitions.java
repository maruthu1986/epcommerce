/**
 * Copyright (c) Elastic Path Software Inc., 2015
 */
package com.elasticpath.uat.stepdefs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.elasticpath.common.dto.ShoppingItemDto;
import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.commons.constants.ContextIdNames;
import com.elasticpath.domain.catalog.ProductSku;
import com.elasticpath.domain.customer.Address;
import com.elasticpath.domain.event.EventOriginator;
import com.elasticpath.domain.event.EventOriginatorType;
import com.elasticpath.domain.impl.AbstractShoppingItemImpl;
import com.elasticpath.domain.order.Order;
import com.elasticpath.domain.order.OrderAddress;
import com.elasticpath.domain.order.OrderPayment;
import com.elasticpath.domain.order.OrderReturn;
import com.elasticpath.domain.order.OrderReturnSku;
import com.elasticpath.domain.order.OrderReturnSkuReason;
import com.elasticpath.domain.order.OrderReturnType;
import com.elasticpath.domain.order.OrderShipment;
import com.elasticpath.domain.order.OrderSku;
import com.elasticpath.domain.order.PhysicalOrderShipment;
import com.elasticpath.domain.shoppingcart.ShoppingCart;
import com.elasticpath.domain.shoppingcart.ShoppingItem;
import com.elasticpath.domain.shoppingcart.ShoppingItemPricingSnapshot;
import com.elasticpath.sellingchannel.director.ShoppingItemAssembler;
import com.elasticpath.service.catalog.ProductSkuLookup;
import com.elasticpath.service.order.ReturnAndExchangeService;
import com.elasticpath.service.order.ReturnExchangeType;
import com.elasticpath.service.shoppingcart.PricingSnapshotService;
import com.elasticpath.shipping.connectivity.dto.ShippingOption;
import com.elasticpath.test.persister.testscenarios.SimpleStoreScenario;
import com.elasticpath.uat.ScenarioContextValueHolder;

/**
 * Step Definitions for order return and exchange functionality.
 */
public class OrderReturnAndExchangeStepDefinitions {

	@Autowired
	private ReturnAndExchangeService returnAndExchangeService;

	@Autowired
	@Qualifier("coreBeanFactory")
	private BeanFactory beanFactory;

	@Autowired
	private ScenarioContextValueHolder<Order> orderHolder;

	@Autowired
	private ScenarioContextValueHolder<OrderReturn> orderReturnHolder;

	@Autowired
	private ScenarioContextValueHolder<SimpleStoreScenario> storeScenarioHolder;

	private boolean physicalReturnRequired;

	@Autowired
	private OrderReturnSkuReason orderReturnSkuReason;

	@Autowired
	private ShoppingItemAssembler shoppingItemAssembler;

	@Autowired
	@Qualifier("productSkuLookup")
	private ProductSkuLookup productSkuLookup;

	@Autowired
	private PricingSnapshotService pricingSnapshotService;

	@Autowired
	private ScenarioContextValueHolder<Runnable> emailSendingCommandHolder;
	
	@Before
	@SuppressWarnings("deprecation")
	public void setUp() {
		// Spring can't create the orderReturnSku bean without this property having been set.
		// In production this Map is set within CM Client only.
		orderReturnSkuReason.setPropertiesMap(new HashMap<>());
	}

	@And("^items (must|need not) be sent back to the manufacturer or retailer for a return to be made$")
	public void setPhysicalReturnRequired(final String physicalReturnRequiredString) throws Throwable {
		physicalReturnRequired = "must".equals(physicalReturnRequiredString);
	}

	@When("^I initiate a return of at least one item in the purchase$")
	public void returnOrder() throws Exception {
		// Defer execution until we are ready to check for the email
		emailSendingCommandHolder.set(() -> {
			final Order order = orderHolder.get();
			final OrderShipment orderShipment = order.getPhysicalShipments().get(0);

			final OrderReturn orderReturn = beanFactory.getBean(ContextIdNames.ORDER_RETURN);
			orderReturn.populateOrderReturn(order, orderShipment, OrderReturnType.RETURN);
			orderReturn.setOrderReturnAddress(((PhysicalOrderShipment) orderShipment).getShipmentAddress());
			orderReturn.recalculateOrderReturn();

			populateOrderReturnSkuQuantityAndReason(orderReturn);

			final ReturnExchangeType returnExchangeType;
			if (physicalReturnRequired) {
				returnExchangeType = ReturnExchangeType.PHYSICAL_RETURN_REQUIRED;
			} else {
				returnExchangeType = ReturnExchangeType.MANUAL_RETURN;
			}

			orderReturnHolder.set(returnAndExchangeService.createShipmentReturn(orderReturn,
																				returnExchangeType,
																				orderReturn.getOrderShipmentForReturn(),
																				getSystemOriginator()));
		});
	}

	@When("^I initiate an exchange of at least one item in the purchase$")
	public void exchangeOrder() throws Exception {
		// Defer execution until we are ready to check for the email
		emailSendingCommandHolder.set(() -> {
			final Order order = orderHolder.get();

			final ReturnExchangeType returnExchangeType;
			if (physicalReturnRequired) {
				returnExchangeType = ReturnExchangeType.PHYSICAL_RETURN_REQUIRED;
			} else {
				returnExchangeType = ReturnExchangeType.NEW_PAYMENT;
			}

			OrderReturn orderReturn = createExchangeOrderReturn(order);
			final OrderPayment authOrderPayment = findAuthOrderPayment(order);

			orderReturn = returnAndExchangeService.createExchange(orderReturn, returnExchangeType, authOrderPayment);

			orderReturnHolder.set(orderReturn);
		});
	}

	private OrderPayment findAuthOrderPayment(final Order order) {
		for (final OrderPayment orderPayment : order.getOrderPayments()) {
			if (orderPayment.getTransactionType().equals(OrderPayment.AUTHORIZATION_TRANSACTION)) {
				return orderPayment;
			}
		}

		return null;
	}

	private OrderReturn createExchangeOrderReturn(final Order originalOrder) {
		final OrderShipment orderShipment = originalOrder.getPhysicalShipments().get(0);

		final OrderReturn orderReturn = beanFactory.getBean(ContextIdNames.ORDER_RETURN);
		orderReturn.populateOrderReturn(originalOrder, orderShipment, OrderReturnType.EXCHANGE);

		final OrderAddress address = ((PhysicalOrderShipment) orderShipment).getShipmentAddress();
		orderReturn.setOrderReturnAddress(address);
		orderReturn.recalculateOrderReturn();

		// the exchange cart needs to exist or the call to createExchangeOrderReturn will explode
		createExchangeCartFromOriginalOrder(originalOrder, orderReturn, address);

		populateOrderReturnSkuQuantityAndReason(orderReturn);

		return orderReturn;
	}

	private ShoppingCart createExchangeCartFromOriginalOrder(final Order originalOrder, final OrderReturn exchangeOrderReturn,
															 final Address shippingAddress) {
		final SimpleStoreScenario scenario = storeScenarioHolder.get();

		final Collection<OrderSku> orderSkus = originalOrder.getRootShoppingItems();

		final List<ShoppingItem> exchangeCartItems = new ArrayList<>();

		for (final OrderSku originalItem : orderSkus) {
			final ProductSku productSku = productSkuLookup.findByGuid(originalItem.getSkuGuid());
			final String skuCode = productSku.getSkuCode();

			final ShoppingItemDto shoppingItemDto = new ShoppingItemDto(skuCode, 1);
			final AbstractShoppingItemImpl replacementItem = (AbstractShoppingItemImpl) shoppingItemAssembler.createShoppingItem(shoppingItemDto);

			final ShoppingItemPricingSnapshot originalItemPricingSnapshot = pricingSnapshotService.getPricingSnapshotForOrderSku(originalItem);

			replacementItem.setPrice(1, originalItemPricingSnapshot.getPrice());

			exchangeCartItems.add(replacementItem);
		}

		final ShippingOption shippingOption = scenario.getShippingOption();

		// this method calls exchangeOrderReturn.setExchangeShoppingCart() with the generated shopping cart
		return returnAndExchangeService.populateShoppingCart(exchangeOrderReturn, exchangeCartItems, shippingOption, shippingAddress);
	}

	private void populateOrderReturnSkuQuantityAndReason(final OrderReturn orderReturn) {
		for (OrderReturnSku orderReturnSku : orderReturn.getOrderReturnSkus()) {
			orderReturnSku.setQuantity(1);
			orderReturnSku.setReturnReason("OrderReturnSkuReason_Faulty");
		}
	}
	
	/**
	 * Create the event originator which is the system.
	 * @return the event originator.
	 */
	private EventOriginator getSystemOriginator() {
		EventOriginator originator = beanFactory.getBean(ContextIdNames.EVENT_ORIGINATOR);
		originator.setType(EventOriginatorType.SYSTEM);
		return originator;
	}

}
